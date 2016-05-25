import java.io.IOException;

public class DBSimulation {
	
	public static void main(String args[]) throws IOException{
		SQLParser sqlParser = new SQLParser();
		ResultSet resultSet = sqlParser.executeQuery("select * from student right join school on student.school_id = school.id");
		while(resultSet.hasNext()){
			System.out.println(resultSet.next());
		}
	}
}
