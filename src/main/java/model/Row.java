package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Row implements Cloneable, Serializable {
	private List<Column> columns;

	public Row() {
		columns = new ArrayList<Column>();
	}

	public void add(Column column) {
		this.columns.add(column);
	}

	public void addAll(List<Column> columns) {
		this.columns.addAll(columns);
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	@Override
	public String toString() {
		return columns.toString();
	}

	public void subColumns(int index, int toIndex) {
		columns = columns.subList(index, toIndex);
	}

	@Override
	public Row clone() {
		// TODO Auto-generated method stub
		Row o = null;
		try {
			if (this != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(this);
				oos.close();
				ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
				ObjectInputStream ois = new ObjectInputStream(bais);
				o = (Row) ois.readObject();
				ois.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return o;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		Row row = (Row) obj;
		boolean eq = true;
		if (columns.size() != row.getColumns().size()) {
			return false;
		}
		for (int i = 0; i < columns.size(); i++) {
			if (!columns.get(i).equals(row.getColumns().get(i))) {
				eq = false;
				break;
			}
		}
		return eq;
	}
}
