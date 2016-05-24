import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLParser {
	private String mainTable;
	private List<String> leftJoinTables;
	private List<String> innerJoinTables;
	
	public void init() {
		mainTable = null;
		leftJoinTables = new ArrayList<String>();
		innerJoinTables = new ArrayList<String>();
	}

	public ResultSet executeQuery(String sql) throws IOException {
		init();
		sql = sql.trim();
		sql = sql.toLowerCase();
		ResultSet rs = new ResultSet();
		rs.setResultSet(new ArrayList<List<Object>>());
		List<List<Object>> resultSet = rs.getResultSet();
		Pattern mainTablePattern = Pattern.compile("from (\\w+)");
		Pattern joinTablePattern = Pattern.compile("(\\w+) join (\\w+)");
		Pattern onPattern = Pattern.compile("on (\\w+)\\.(\\w+)\\s*=\\s*(\\w+)\\.(\\w+)");
		Matcher mainTableMatcher = mainTablePattern.matcher(sql);
		Matcher joinTableMatcher = joinTablePattern.matcher(sql);
		if (mainTableMatcher.find()) {
			mainTable = mainTableMatcher.group(1);
		}
		while (joinTableMatcher.find()) {
			String operator = joinTableMatcher.group(1);
			String tableName = joinTableMatcher.group(2);
			switch (operator) {
			case "left":
				leftJoinTables.add(tableName);
				break;
			case "inner":
			default:
				innerJoinTables.add(tableName);
				break;
			}
		}

		DBReader mainTableReader = new DBReader(mainTable);
		List<DBReader> leftJoinTableReaders = new ArrayList<DBReader>();
		List<DBReader> innerJoinTableReaders = new ArrayList<DBReader>();

		for (String joinTable : leftJoinTables) {
			leftJoinTableReaders.add(new DBReader(joinTable));
		}
		for (String joinTable : innerJoinTables) {
			innerJoinTableReaders.add(new DBReader(joinTable));
		}

		List<List<Object>> mainTableResultSet = mainTableReader.getRecords();
		List<List<List<Object>>> leftJoinTablesResultSets = new ArrayList<List<List<Object>>>();
		List<List<List<Object>>> innerJoinTablesResultSets = new ArrayList<List<List<Object>>>();

		for (DBReader joinTableReader : leftJoinTableReaders) {
			List<List<Object>> records = joinTableReader.getRecords();
			leftJoinTablesResultSets.add(records);
		}

		for (DBReader joinTableReader : innerJoinTableReaders) {
			List<List<Object>> records = joinTableReader.getRecords();
			innerJoinTablesResultSets.add(records);
		}
		//join operation
		List<List<Object>> leftJoinTableResultSet = null;
		List<List<Object>> innerJoinTablesResultSet = null;
		if (leftJoinTablesResultSets.size() > 0) {
			leftJoinTableResultSet = leftJoinTablesResultSets.get(0);
		}
		if (innerJoinTablesResultSets.size() > 0) {
			innerJoinTablesResultSet = innerJoinTablesResultSets.get(0);
		}
		boolean noJoin = true;
		if (leftJoinTableResultSet != null) {
			List<List<Object>> leftJoinResultSet = leftJoin(mainTableResultSet, leftJoinTableResultSet);
			resultSet.addAll(leftJoinResultSet);
			noJoin = false;
		}
		if (innerJoinTablesResultSet != null) {
			List<List<Object>> innerJoinResultSet = innerJoin(mainTableResultSet, innerJoinTablesResultSet);
			resultSet.addAll(innerJoinResultSet);
			noJoin = false;
		}
		if(noJoin){
			resultSet.addAll(mainTableResultSet);
		}
		// close
		mainTableReader.close();
		for (DBReader joinTableReader : leftJoinTableReaders) {
			joinTableReader.close();
		}
		for (DBReader joinTableReader : innerJoinTableReaders) {
			joinTableReader.close();
		}
		return rs;
	}

	private List<List<Object>> leftJoin(List<List<Object>> mainTableResultSet, List<List<Object>> joinTableResultSet) {
		for (List<Object> outterRecord : mainTableResultSet) {
			boolean find = false;
			for (List<Object> innerRecord : joinTableResultSet) {
				if (outterRecord.get(2).equals(innerRecord.get(0))) {
					outterRecord.addAll(innerRecord);
					find = true;
					break;
				}
			}
			if (!find) {
				List<Object> nullList = new ArrayList<Object>();
				for (int i = 0; i < joinTableResultSet.get(0).size(); i++) {
					nullList.add(null);
				}
				outterRecord.addAll(nullList);
			}
		}
		return mainTableResultSet;
	}
	
	private List<List<Object>> innerJoin(List<List<Object>> mainTableResultSet, List<List<Object>> joinTableResultSet) {
		List<List<Object>> table = new ArrayList<List<Object>>();
		for (List<Object> outterRecord : mainTableResultSet) {
			for (List<Object> innerRecord : joinTableResultSet) {
				if (outterRecord.get(2).equals(innerRecord.get(0))) {
					outterRecord.addAll(innerRecord);
					table.add(outterRecord);
					break;
				}
			}
		}
		return table;
	}
}
