import java.util.Iterator;
import java.util.List;

import model.Row;

public class ResultSet {
	private List<Row> resultSet;
	private Iterator<Row> iterator;

	public List<Row> getResultSet() {
		return resultSet;
	}

	public void setResultSet(List<Row> resultSet) {
		this.resultSet = resultSet;
	}

	public boolean hasNext() {
		if (iterator == null) {
			if (resultSet != null) {
				iterator = resultSet.iterator();
			} else {
				System.out.println("not set ResultSet");
			}
		}
		return iterator.hasNext();
	}

	public Row next() {
		if (iterator == null) {
			if (resultSet != null) {
				iterator = resultSet.iterator();
			} else {
				System.out.println("not set ResultSet");
			}
		}
		return iterator.next();
	}
}
