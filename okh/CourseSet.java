import java.util.Set;
import java.util.TreeSet;
import java.util.HashSet;
import java.io.BufferedReader;
import java.io.FileReader;

public class CourseSet {
	Set<String> course;
	public CourseSet(String fileDir) {
		try {
			FileReader fr = new FileReader(fileDir);
			BufferedReader br = new BufferedReader(fr);
			
			readCourse(br);
		} catch(Exception e) {	
		}
	}
	
	public Set<String> getCourse() {
		return this.course;
	}
	
	public void readCourse(BufferedReader br) {
		course = new HashSet<String>();
		String courseLine = null;
		try {
			while((courseLine = br.readLine()) != null) {
				String[] arr = courseLine.split(" ");
				course.add(arr[0]);
			}			
		} catch(Exception e) {
			System.out.println("Error boss");
		}
	}
	
	public void printSet() {
		Set<String> sortedCourse = new TreeSet<String>(course); 
        System.out.println(sortedCourse);
	}
	
	public int getSize() {
		return course.size();
	}
	
}
