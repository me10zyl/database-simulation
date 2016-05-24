package model.condition;

public class Value {
	private String tableName;
	private String fieldName;
	private String literal;

	public Value() {
		// TODO Auto-generated constructor stub
	}
	
	public Value(String tableName, String fieldName) {
		super();
		this.tableName = tableName;
		this.fieldName = fieldName;
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
