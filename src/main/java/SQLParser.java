import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Column;
import model.Row;
import model.Table;

public class SQLParser {
	private Table mainTable;
	private List<Table> joinTables;

	public void init() {
		mainTable = new Table(Table.Type.MAIN);
		joinTables = new ArrayList<Table>();
	}

	public ResultSet executeQuery(String sql) throws IOException {
		init();
		sql = sql.trim();
		sql = sql.toLowerCase();
		ResultSet rs = new ResultSet();
		rs.setResultSet(new ArrayList<Row>());
		List<Row> resultSet = rs.getResultSet();
		Pattern mainTablePattern = Pattern.compile("from (\\w+)");
		Pattern joinTablePattern = Pattern.compile("(\\w+) join (\\w+)");
		Pattern onPattern = Pattern.compile("on (\\w+)\\.(\\w+)\\s*=\\s*(\\w+)\\.(\\w+)");
		Matcher mainTableMatcher = mainTablePattern.matcher(sql);
		Matcher joinTableMatcher = joinTablePattern.matcher(sql);
		if (mainTableMatcher.find()) {
			String mainTableName = mainTableMatcher.group(1);
			mainTable.setName(mainTableName);
		}
		while (joinTableMatcher.find()) {
			String operator = joinTableMatcher.group(1);
			String tableName = joinTableMatcher.group(2);
			switch (operator) {
			case "left":
				joinTables.add(new Table(tableName, Table.Type.LEFT_JOIN));
				break;
			case "inner":
			default:
				joinTables.add(new Table(tableName, Table.Type.INNER_JOIN));
				break;
			}
		}
		// read table data
		new DBReader(mainTable.getName()).readTable(mainTable);
		for (Table joinTable : joinTables) {
			new DBReader(joinTable.getName()).readTable(joinTable);
		}
		// join operation
		Table joinedTable = mainTable;
		for (Table joinTable : joinTables) {
			if (joinTable.getType().equals(Table.Type.LEFT_JOIN)) {
				joinedTable = leftJoin(mainTable, joinTable);
			} else if (joinTable.getType().equals(Table.Type.INNER_JOIN)) {
				joinedTable = innerJoin(mainTable, joinTable);
			}
		}
		resultSet.addAll(joinedTable.getRows());
		return rs;
	}

	private Table leftJoin(Table mainTable, Table joinTable) {
		Table table = new Table(Table.Type.LEFT_JOIN);
		List<Row> rows = table.getRows();
		table.getFieldNames().addAll(mainTable.getFieldNames());
		table.getFieldNames().addAll(joinTable.getFieldNames());

		for (Row outterRecord : mainTable.getRows()) {
			boolean find = false;
			for (Row innerRecord : joinTable.getRows()) {
				if (outterRecord.getColumns().get(2).equals(innerRecord.getColumns().get(0))) {
					outterRecord.addAll(innerRecord.getColumns());
					find = true;
					break;
				}
			}
			if (!find) {
				List<Column> nullList = new ArrayList<Column>();
				for (int i = 0; i < joinTable.getFieldCount(); i++) {
					nullList.add(null);
				}
				outterRecord.addAll(nullList);
			}
		}

		rows.addAll(mainTable.getRows());
		return table;
	}

	private Table innerJoin(Table mainTable, Table joinTable) {
		Table table = new Table(Table.Type.INNER_JOIN);
		List<Row> rows = table.getRows();
		table.getFieldNames().addAll(mainTable.getFieldNames());
		table.getFieldNames().addAll(joinTable.getFieldNames());

		for (Row outterRecord : mainTable.getRows()) {
			for (Row innerRecord : joinTable.getRows()) {
				if (outterRecord.getColumns().get(2).equals(innerRecord.getColumns().get(0))) {
					outterRecord.addAll(innerRecord.getColumns());
					rows.add(outterRecord);
					break;
				}
			}
		}
		return table;
	}
}
