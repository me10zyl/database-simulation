package model;

import java.util.ArrayList;
import java.util.List;

import model.condition.Condition;

public class Table {
	private String name;
	private Type type;
	private List<Row> rows;
	private List<String> fieldNames;
	private Condition joinCondition;

	public static enum Type {
		MAIN, LEFT_JOIN, RIGHT_JOIN, INNER_JOIN
	}

	public Table() {
		// TODO Auto-generated constructor stub
		this.rows = new ArrayList<Row>();
		this.fieldNames = new ArrayList<String>();
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
}
