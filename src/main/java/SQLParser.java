import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Column;
import model.Row;
import model.Table;
import model.condition.Condition;
import model.condition.Operator;
import model.condition.Value;

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
		Pattern onPattern = Pattern.compile("on (\\w+)\\.(\\w+)\\s*([=])\\s*(\\w+)\\.(\\w+)");
		Matcher mainTableMatcher = mainTablePattern.matcher(sql);
		Matcher joinTableMatcher = joinTablePattern.matcher(sql);
		Matcher onMatcher = onPattern.matcher(sql);
		if (mainTableMatcher.find()) {
			String mainTableName = mainTableMatcher.group(1);
			mainTable.setName(mainTableName);
		}
		while (joinTableMatcher.find()) {
			String joinMethod = joinTableMatcher.group(1);
			String tableName = joinTableMatcher.group(2);
			Condition condition = null;
			if (onMatcher.find()) {
				Value leftValue = new Value(onMatcher.group(1), onMatcher.group(2));
				Value rightValue = new Value(onMatcher.group(4), onMatcher.group(5));
				Operator operator = new Operator(onMatcher.group(3));
				condition = new Condition(leftValue, rightValue, operator);
			}
			switch (joinMethod) {
			case "left":
				joinTables.add(new Table(tableName, Table.Type.LEFT_JOIN, condition));
				break;
			case "right":
				joinTables.add(new Table(tableName, Table.Type.RIGHT_JOIN, condition));
				break;
			case "inner":
			default:
				joinTables.add(new Table(tableName, Table.Type.INNER_JOIN, condition));
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
			} else if (joinTable.getType().equals(Table.Type.RIGHT_JOIN)) {
				joinedTable = rightJoin(mainTable, joinTable);
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

		int mainTableColumnIndex = -1;
		int joinTableColumnIndex = -1;
		Condition joinCondition = joinTable.getJoinCondition();
		Value leftValue = joinCondition.getLeftValue();
		Value rightValue = joinCondition.getRightValue();
		List<Value> values = new ArrayList<>();
		values.add(leftValue);
		values.add(rightValue);
		for (Value value : values) {
			if (mainTable.getName().equals(value.getTableName())) {
				mainTableColumnIndex = mainTable.getFieldNames().indexOf(value.getFieldName());
			}
			if (joinTable.getName().equals(value.getTableName())) {
				joinTableColumnIndex = joinTable.getFieldNames().indexOf(value.getFieldName());
			}
		}

		for (Row outterRecord : mainTable.getRows()) {
			boolean find = false;
			for (Row innerRecord : joinTable.getRows()) {
				if (outterRecord.getColumns().get(mainTableColumnIndex)
						.equals(innerRecord.getColumns().get(joinTableColumnIndex))) {
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

	private Table rightJoin(Table mainTable, Table joinTable) {
		Table table = new Table(Table.Type.RIGHT_JOIN);
		List<Row> rows = table.getRows();
		table.getFieldNames().addAll(mainTable.getFieldNames());
		table.getFieldNames().addAll(joinTable.getFieldNames());

		int mainTableColumnIndex = -1;
		int joinTableColumnIndex = -1;
		Condition joinCondition = joinTable.getJoinCondition();
		Value leftValue = joinCondition.getLeftValue();
		Value rightValue = joinCondition.getRightValue();
		List<Value> values = new ArrayList<>();
		values.add(leftValue);
		values.add(rightValue);
		for (Value value : values) {
			if (mainTable.getName().equals(value.getTableName())) {
				mainTableColumnIndex = mainTable.getFieldNames().indexOf(value.getFieldName());
			}
			if (joinTable.getName().equals(value.getTableName())) {
				joinTableColumnIndex = joinTable.getFieldNames().indexOf(value.getFieldName());
			}
		}

		for (Row outterRecord : joinTable.getRows()) {
			boolean find = false;
			Row row = new Row();
			for (Row innerRecord : mainTable.getRows()) {
				if (outterRecord.getColumns().get(mainTableColumnIndex)
						.equals(innerRecord.getColumns().get(joinTableColumnIndex))) {
					row.addAll(innerRecord.getColumns());
					row.addAll(outterRecord.getColumns());
					find = true;
					break;
				}
			}
			if (!find) {
				List<Column> nullList = new ArrayList<Column>();
				for (int i = 0; i < joinTable.getFieldCount(); i++) {
					nullList.add(null);
				}
				row.addAll(nullList);
				row.addAll(outterRecord.getColumns());
			}
			rows.add(row);
		}

		return table;
	}

	private Table innerJoin(Table mainTable, Table joinTable) {
		Table table = new Table(Table.Type.INNER_JOIN);
		List<Row> rows = table.getRows();
		table.getFieldNames().addAll(mainTable.getFieldNames());
		table.getFieldNames().addAll(joinTable.getFieldNames());

		int mainTableColumnIndex = -1;
		int joinTableColumnIndex = -1;
		Condition joinCondition = joinTable.getJoinCondition();
		Value leftValue = joinCondition.getLeftValue();
		Value rightValue = joinCondition.getRightValue();
		List<Value> values = new ArrayList<>();
		values.add(leftValue);
		values.add(rightValue);
		for (Value value : values) {
			if (mainTable.getName().equals(value.getTableName())) {
				mainTableColumnIndex = mainTable.getFieldNames().indexOf(value.getFieldName());
			}
			if (joinTable.getName().equals(value.getTableName())) {
				joinTableColumnIndex = joinTable.getFieldNames().indexOf(value.getFieldName());
			}
		}

		for (Row outterRecord : mainTable.getRows()) {
			for (Row innerRecord : joinTable.getRows()) {
				if (outterRecord.getColumns().get(mainTableColumnIndex)
						.equals(innerRecord.getColumns().get(joinTableColumnIndex))) {
					outterRecord.addAll(innerRecord.getColumns());
					rows.add(outterRecord);
					break;
				}
			}
		}
		return table;
	}
}
