import java.util.Iterator;
import java.util.List;

public class ResultSet {
	private List<List<Object>> resultSet;
	private Iterator<List<Object>> iterator;

	public List<List<Object>> getResultSet() {
		return resultSet;
	}

	public void setResultSet(List<List<Object>> resultSet) {
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

	public List<Object> next() {
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
