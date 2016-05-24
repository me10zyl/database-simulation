import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
	
	public List<List<Object>> getRecords() throws IOException{
		records = new ArrayList<List<Object>>();
		String str = null;
		while((str = br.readLine()) != null){
			List<Object> columns  = new ArrayList<Object>();
			String[] split = str.split(" ");
			for(String column : split){
				columns.add(column);
			}
			records.add(columns);
		}
		return records;
	}
}
