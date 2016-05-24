package model;

public class Column {
	private Object value;
	private String field;

	public Column() {
	}

	public Column(Object value) {
		super();
		this.value = value;
	}

	public Column(Object value, String field) {
		super();
		this.value = value;
		this.field = field;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	@Override
	public String toString() {
		return value.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return value.equals(((Column)obj).value);
	}
}
