import java.io.IOException;

public class DBSimulation {
	
	public static void main(String args[]) throws IOException{
		SQLParser sqlParser = new SQLParser();
		ResultSet resultSet = sqlParser.executeQuery("select * from table1 join table2 on table1.col3 = table2.col1");
		while(resultSet.hasNext()){
			System.out.println(resultSet.next());
		}
	}
}
