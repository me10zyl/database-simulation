package model.condition;

import java.io.Serializable;

public class Operator implements Serializable{
	public static final String EQ = "=";
	public static final String LT = "<";
	public static final String GT = ">";
	public static final String GT_EQ = ">=";
	public static final String LT_EQ = ">=";
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
