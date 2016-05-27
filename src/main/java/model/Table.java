package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import model.condition.Condition;
import model.condition.Value;

public class Table implements Serializable {
	private String name;
	private Type type;
	private List<Row> rows;
	private List<String> fieldNames;
	private Condition joinCondition;
	private List<Table> subTables;

	public static enum Type {
		MAIN, LEFT_JOIN, RIGHT_JOIN, INNER_JOIN
	}

	public Table() {
		// TODO Auto-generated constructor stub
		this.rows = new ArrayList<Row>();
		this.fieldNames = new ArrayList<String>();
		this.subTables = new ArrayList<Table>();
	}

	public Table(Type type) {
		this();
		this.type = type;
	}

	public Condition getJoinCondition() {
		return joinCondition;
	}

	public void setJoinCondition(Condition joinCondition) {
		this.joinCondition = joinCondition;
	}

	public int getFieldCount() {
		return fieldNames.size();
	}

	public List<String> getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(List<String> fieldNames) {
		this.fieldNames = fieldNames;
	}

	public Table(String name, Type type) {
		this();
		this.name = name;
		this.type = type;
	}

	public Table(String name, Type type, Condition joinCondition) {
		this();
		this.name = name;
		this.type = type;
		this.joinCondition = joinCondition;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public List<Row> getRows() {
		return rows;
	}

	public void setRows(List<Row> rows) {
		this.rows = rows;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return name.equals(((Table) obj).name);
	}

	public List<Table> getSubTables() {
		return subTables;
	}

	public void setSubTables(List<Table> subTables) {
		this.subTables = subTables;
	}

	public int getIndexOfField(String tableName, Condition condition) {
		if (this.getRows().size() <= 0) {
			return -1;
		}
		Row row = this.getRows().get(0);
		Value leftValue = condition.getLeftValue();
		Value rightValue = condition.getRightValue();
		List<Value> values = new ArrayList<Value>();
		values.add(leftValue);
		values.add(rightValue);
		int i = 0;
		for (Column col : row.getColumns()) {
			for (Value val : values) {
				if (tableName.equals(val.getTableName()) && col.getBelongTable().getName().equals(val.getTableName())
						&& col.getField().equals(val.getFieldName())) {
					return i;
				}
			}
			i++;
		}
		return -1;
	}
}
