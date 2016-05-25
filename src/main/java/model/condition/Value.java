package model.condition;

public class Value {
	private String tableName;
	private String fieldName;
	private String literal;
	private Type type;

	public enum Type {
		FIELD_VALUE, LITERAL
	}

	public Value() {
		this.type = Type.FIELD_VALUE;
		// TODO Auto-generated constructor stub
	}

	public Value(String tableName, String fieldName) {
		this();
		this.tableName = tableName;
		this.fieldName = fieldName;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getLiteral() {
		return literal;
	}

	public void setLiteral(String literal) {
		this.literal = literal;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

}
