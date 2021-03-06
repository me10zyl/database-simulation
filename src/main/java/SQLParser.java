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
	private Condition whereCondition;

	public void init() {
		mainTable = new Table(Table.Type.MAIN);
		joinTables = new ArrayList<Table>();
		whereCondition = null;
	}

	public ResultSet executeQuery(String sql) throws IOException {
		init();
		sql = sql.trim();
		sql = sql.toLowerCase();
		ResultSet rs = new ResultSet();
		rs.setResultSet(new ArrayList<Row>());
		List<Row> resultSet = rs.getResultSet();
		Pattern mainTablePattern = Pattern.compile("from (\\w+)");
		Pattern joinTablePattern = Pattern.compile("(\\w*) join (\\w+)");
		Pattern onPattern = Pattern.compile("on (\\w+)\\.(\\w+)\\s*([=])\\s*(\\w+)\\.(\\w+)");
		Pattern wherePattern = Pattern.compile("where (\\w+)\\.(\\w+)\\s*([=])\\s*(\\w+)");
		Matcher mainTableMatcher = mainTablePattern.matcher(sql);
		Matcher joinTableMatcher = joinTablePattern.matcher(sql);
		Matcher onMatcher = onPattern.matcher(sql);
		Matcher whereMatcher = wherePattern.matcher(sql);
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
			if ("left".equals(joinMethod)) {
				joinTables.add(new Table(tableName, Table.Type.LEFT_JOIN, condition));
			} else if ("right".equals(joinMethod)) {
				joinTables.add(new Table(tableName, Table.Type.RIGHT_JOIN, condition));
			} else {
				joinTables.add(new Table(tableName, Table.Type.INNER_JOIN, condition));
			}
		}
		if (whereMatcher.find()) {
			Value leftValue = new Value(whereMatcher.group(1), whereMatcher.group(2));
			Value rightValue = new Value();
			rightValue.setLiteral(whereMatcher.group(4));
			rightValue.setType(Value.Type.LITERAL);
			Operator operator = new Operator(whereMatcher.group(3));
			whereCondition = new Condition(leftValue, rightValue, operator);
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
				joinedTable = leftJoin(joinedTable, joinTable);
			} else if (joinTable.getType().equals(Table.Type.INNER_JOIN)) {
				joinedTable = innerJoin(joinedTable, joinTable);
			} else if (joinTable.getType().equals(Table.Type.RIGHT_JOIN)) {
				joinedTable = rightJoin(joinedTable, joinTable);
			}
		}
		// where opeartion
		where(joinedTable);

		resultSet.addAll(joinedTable.getRows());
		return rs;
	}

	private Table crossJoin(Table mainTable, Table joinTable) {
		Table table = new Table();
		List<Row> rows = table.getRows();
		table.getFieldNames().addAll(mainTable.getFieldNames());
		table.getFieldNames().addAll(joinTable.getFieldNames());

		for (Row outterRecord : mainTable.getRows()) {
			for (Row innerRecord : joinTable.getRows()) {
				Row cloneRow = outterRecord.clone();
				cloneRow.getColumns().addAll(innerRecord.getColumns());
				rows.add(cloneRow);
			}
		}
		
		table.getSubTables().add(mainTable);
		table.getSubTables().add(joinTable);
		return table;
	}

	private Table leftJoin(Table mainTable, Table joinTable) {
		Table table = crossJoin(mainTable, joinTable);
		table.setType(Table.Type.LEFT_JOIN);
		List<Row> rows = table.getRows();
		int joinTableColumnIndex = table.getJoinTableIndexOfFiled(table, joinTable);
		int mainTableColumnIndex = table.getMainTableIndexOfField(table, mainTable, joinTable);

		List<Row> deletingRows = new ArrayList<Row>();
		for (Row row : rows) {
			List<Column> columns = row.getColumns();
			if (!columns.get(mainTableColumnIndex).equals(columns.get(joinTableColumnIndex))) {
				deletingRows.add(row);
			}
		}
		for (Row row : deletingRows) {
			rows.remove(row);
		}

		List<Row> addingRows = new ArrayList<Row>();
		for (Row outRow : mainTable.getRows()) {
			boolean add = true;
			for (Row inRow : rows) {
				Row inRowClone = inRow.clone();
				inRowClone.subColumns(0, mainTable.getFieldCount());
				if (outRow.equals(inRowClone)) {
					add = false;
					break;
				}
			}
			if (add) {
				List<Column> nullList = new ArrayList<Column>();
				for (int i = 0; i < table.getFieldCount() - mainTable.getFieldCount(); i++) {
					nullList.add(null);
				}
				Row newRow = outRow.clone();
				newRow.addAll(nullList);
				addingRows.add(newRow);
			}
		}
		rows.addAll(addingRows);
		return table;
	}

	private Table rightJoin(Table mainTable, Table joinTable) {
		Table table = crossJoin(mainTable, joinTable);
		table.setType(Table.Type.RIGHT_JOIN);
		List<Row> rows = table.getRows();
		int joinTableColumnIndex = table.getJoinTableIndexOfFiled(table, joinTable);
		int mainTableColumnIndex = table.getMainTableIndexOfField(table, mainTable, joinTable);
		
		List<Row> deletingRows = new ArrayList<Row>();
		for (Row row : rows) {
			List<Column> columns = row.getColumns();
			if (!columns.get(mainTableColumnIndex).equals(columns.get(joinTableColumnIndex))) {
				deletingRows.add(row);
			}
		}
		for (Row row : deletingRows) {
			rows.remove(row);
		}

		List<Row> addingRows = new ArrayList<Row>();
		for (Row outRow : joinTable.getRows()) {
			boolean add = true;
			for (Row inRow : rows) {
				Row inRowClone = inRow.clone();
				inRowClone.subColumns(inRow.getColumns().size() - joinTable.getFieldCount(), inRow.getColumns().size());
				if (outRow.equals(inRowClone)) {
					add = false;
					break;
				}
			}
			if (add) {
				List<Column> nullList = new ArrayList<Column>();
				for (int i = 0; i < table.getFieldCount() - joinTable.getFieldCount(); i++) {
					nullList.add(null);
				}
				Row newRow = outRow.clone();
				nullList.addAll(newRow.getColumns());
				newRow.setColumns(nullList);
				addingRows.add(newRow);
			}
		}
		rows.addAll(addingRows);
		return table;
	}

	private Table innerJoin(Table mainTable, Table joinTable) {
		Table table = crossJoin(mainTable, joinTable);
		table.setType(Table.Type.INNER_JOIN);
		if (joinTable.getJoinCondition() != null) {
			List<Row> rows = table.getRows();
			int joinTableColumnIndex = table.getJoinTableIndexOfFiled(table, joinTable);
			int mainTableColumnIndex = table.getMainTableIndexOfField(table, mainTable, joinTable);

			List<Row> deletingRows = new ArrayList<Row>();
			for (Row row : rows) {
				List<Column> columns = row.getColumns();
				if (!columns.get(mainTableColumnIndex).equals(columns.get(joinTableColumnIndex))) {
					deletingRows.add(row);
				}
			}
			for (Row row : deletingRows) {
				rows.remove(row);
			}
		}
		return table;
	}

	private Table where(Table table) {
		if (whereCondition != null) {
			Value rightValue = whereCondition.getRightValue();
			int leftValueIndex = table.getIndexOfField(whereCondition.getLeftValue().getTableName(), whereCondition);
			List<Row> deletingRows = new ArrayList<Row>();
			for (Row row : table.getRows()) {
				Column column = row.getColumns().get(leftValueIndex);
				if (column == null || !rightValue.getLiteral().equals(column.getValue())) {
					deletingRows.add(row);
				}
			}
			for (Row row : deletingRows) {
				table.getRows().remove(row);
			}
		}
		return table;
	}
}
