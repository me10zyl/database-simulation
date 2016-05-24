import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import model.Column;
import model.Row;
import model.Table;

public class DBReader {
	private BufferedReader br;
	public static final String PATH = "db";
	private List<List<Object>> records;

	public DBReader(String tablename) throws UnsupportedEncodingException, FileNotFoundException {
		// TODO Auto-generated constructor stub
		br = new BufferedReader(new InputStreamReader(new FileInputStream(PATH + "/" + tablename), "UTF-8"));
		records = new ArrayList<List<Object>>();
	}

	public String readRecord() throws IOException {
		return br.readLine();
	}

	public void close() throws IOException {
		br.close();
	}
	
	private List<List<Object>> getRecords() throws IOException{
		records = new ArrayList<List<Object>>();
		String str = null;
		while((str = br.readLine()) != null){
			List<Object> columns  = new ArrayList<Object>();
			String[] split = str.split("\\s+");
			for(String column : split){
				columns.add(column);
			}
			records.add(columns);
		}
		return records;
	}
	
	public List<Row> getRows() throws IOException{
		List<List<Object>> recordsTemp = getRecords();
		close();
		List<Object> colNames = recordsTemp.get(0);
		recordsTemp = recordsTemp.subList(1, recordsTemp.size());
		List<Row> rows = new ArrayList<Row>();
		for(List<Object> r : recordsTemp){
			Row row = new Row();
			int i = 0;
			for(Object c : r){
				row.add(new Column(c, colNames.get(i).toString()));
				i++;
			}
			rows.add(row);
		}
		return rows;
	}
	
	public void readTable(Table table) throws IOException{
		List<List<Object>> recordsTemp = getRecords();
		close();
		List<Object> colNamesObj = recordsTemp.get(0);
		List<String> colNames = new ArrayList<>();
		List<Row> rows = new ArrayList<>();
		for(Object o : colNamesObj){
			colNames.add(o.toString());
		}
		recordsTemp = recordsTemp.subList(1, recordsTemp.size());
		table.setFieldNames(colNames);
		table.setRows(rows);
		for(List<Object> r : recordsTemp){
			Row row = new Row();
			int i = 0;
			for(Object c : r){
				row.add(new Column(c, colNames.get(i).toString()));
				i++;
			}
			rows.add(row);
		}
	}
}
