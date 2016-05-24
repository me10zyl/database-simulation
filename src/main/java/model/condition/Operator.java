package model.condition;

public class Operator {
	public static final String EQ = "=";
	public static final String LT = "<";
	public static final String GT = ">";
	private String name;

	public Operator(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
