package model;

import java.util.ArrayList;
import java.util.List;

public class Row {
	private List<Column> columns;

	public Row() {
		columns = new ArrayList<Column>();
	}

	public void add(Column column) {
		this.columns.add(column);
	}
	
	public void addAll(List<Column> columns){
		this.columns.addAll(columns);
	}
	
	public List<Column> getColumns() {
		return columns;
	}

	@Override
	public String toString() {
		return columns.toString();
	}
}
