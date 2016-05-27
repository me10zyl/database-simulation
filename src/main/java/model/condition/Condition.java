package model.condition;

import java.io.Serializable;

public class Condition implements Serializable {
	private Value leftValue;
	private Value rightValue;
	private Operator operator;

	public Condition() {
		// TODO Auto-generated constructor stub
	}

	public Condition(Value leftValue, Value rightValue, Operator operator) {
		super();
		this.leftValue = leftValue;
		this.rightValue = rightValue;
		this.operator = operator;
	}

	public Value getLeftValue() {
		return leftValue;
	}

	public void setLeftValue(Value leftValue) {
		this.leftValue = leftValue;
	}

	public Value getRightValue() {
		return rightValue;
	}

	public void setRightValue(Value rightValue) {
		this.rightValue = rightValue;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

}
