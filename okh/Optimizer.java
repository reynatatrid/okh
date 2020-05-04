//import okh.Utils;

public class Optimizer {
	private static int[][] jadwalTheBest;
	
	private static void setJadwal(int[][] jadwal) {
		jadwalTheBest = jadwal;
	}
	
	public static int[][] getJadwal() {
		return jadwalTheBest;
	}
	
	public static void randomSearch(String dir_stu, String dir_crs, int timeslot) {
		CourseSet cs = new CourseSet(dir_crs);
		ConflictMatrix cm = new ConflictMatrix(dir_stu, cs.getSize());
		
		int [][] copyGraph = Utils.copyArray(cm.getConflictMatrix());
		int [][] graph = cm.getRandomMatrix();
		
		int jumlahTimeslot = timeslot;
		Scheduler scheduler = new Scheduler(cs.getSize());
		scheduler.timesloting(graph, jumlahTimeslot);
		
		scheduler.printSchedule(cm.getRandomIndex(graph.length));
		int jumlah = cm.getJumlahStudent();
		int[][] jadwal = scheduler.getSchedule();
		
		int[][] gr = cm.getLargestDegree(copyGraph);
		
		double penalty = Utils.getPenalty(gr, jadwal, jumlah);
		System.out.println(penalty);
		for(int i = 0; i < 1000; i++) {
			CourseSet csIter = new CourseSet(dir_crs);
			ConflictMatrix cmIter = new ConflictMatrix(dir_stu, cs.getSize());
			
			int [][] copyGraphIter = Utils.copyArray(cmIter.getConflictMatrix());
			int [][] graphIter = cm.getRandomMatrix();
			
			Scheduler schedulerIter = new Scheduler(csIter.getSize());
			
			schedulerIter.timesloting(graphIter, jumlahTimeslot);
			schedulerIter.printSchedule(cm.getRandomIndex(graphIter.length));
			int[][] jadwalIter = schedulerIter.getSchedule();
			
			int[][] grIter = cm.getLargestDegree(copyGraphIter);
			
			double penalty2 = Utils.getPenalty(grIter, jadwalIter, jumlah);
			
			if(penalty > penalty2)
				penalty = penalty2;
			
			System.out.println("Iterasi "+(i+1)+" - Penalty : "+penalty);
		}
	}
	
	public static void hillClimbing(String dir_stu, String dir_crs, int timeslot, int iterasi) {
		CourseSet cs = new CourseSet(dir_crs);
		ConflictMatrix cm = new ConflictMatrix(dir_stu, cs.getSize());
		
		int [][] conflict_matrix = cm.getConflictMatrix();
		int[][] jadwal = Scheduler.getSaturationSchedule(cs.getSize(), cm.getDegree(), conflict_matrix);
		
		int jumlahStudent = cm.getJumlahStudent();
		
		int[][] jadwalTemp = new int[jadwal.length][2];
		
		for(int i = 0; i < jadwalTemp.length; i++) {
			jadwalTemp[i][0] = jadwal[i][0];
			jadwalTemp[i][1] = jadwal[i][1];
		}
		
		double penalty = Utils.getPenalty(conflict_matrix, jadwal, jumlahStudent);
		
		Solution bestSolution = new Solution(jadwal);
		int max_timeslot = bestSolution.getJumlahTimeslot();
		
		for(int i = 0; i < iterasi; i++) {			
			int randomCourseIndex = Utils.getRandomNumber(0, conflict_matrix.length-1);
			int randomTimeslot = Utils.getRandomNumber(0, max_timeslot-1);
		
			if (Scheduler.checkRandomTimeslot(randomCourseIndex, randomTimeslot, conflict_matrix, jadwalTemp)) {	
				jadwalTemp[randomCourseIndex][1] = randomTimeslot;
				double penalty2 = Utils.getPenalty(conflict_matrix, jadwalTemp, jumlahStudent);
				
				if(penalty > penalty2) {
					penalty = Utils.getPenalty(conflict_matrix, jadwalTemp, jumlahStudent);
					jadwal[randomCourseIndex][1] = jadwalTemp[randomCourseIndex][1];
					bestSolution.setSolution(jadwal);
					bestSolution.setPenalty(penalty);
				} else {
					jadwalTemp[randomCourseIndex][1] = jadwal[randomCourseIndex][1];
				}
			}
		}
		
		setJadwal(jadwal);
		System.out.println(bestSolution.getPenalty());
	}	
}
