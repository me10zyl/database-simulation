import java.io.IOException;

public class DBSimulation {
	
	public static void main(String args[]) throws IOException{
		SQLParser sqlParser = new SQLParser();
		ResultSet resultSet = sqlParser.executeQuery("select * from student join school on student.school_id = school.id right join sex on sex.id = student.sex_id");
		while(resultSet.hasNext()){
			System.out.println(resultSet.next());
		}
	}
}
