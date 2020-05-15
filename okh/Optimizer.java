import java.util.ArrayList;
import java.util.LinkedList;

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

	public static void TabuSearch(String dir_stu, String dir_crs) {
		CourseSet cs = new CourseSet(dir_crs);
		ConflictMatrix cm = new ConflictMatrix(dir_stu, cs.getSize());
		
		int jumlahStudent = cm.getJumlahStudent();
		double bestPenalty;

		//init sol = timeslotTabuSeacrh
		int [][] conflict_matrix = cm.getConflictMatrix();
		int[][] jadwal = Scheduler.getSaturationSchedule(cs.getSize(), cm.getDegree(), conflict_matrix);
		double penalty = Utils.getPenalty(conflict_matrix, jadwal, jumlahStudent);
		
		//Utils.copySolution = Evaluator.getTimeslot
		int[][] bestTimeslot = Utils.copySolution(jadwal); // handle current best timeslot
    	int[][] bestcandidate  = Utils.copySolution(jadwal);
		int [][] timeslotTabuSearchSementara = Utils.copySolution(jadwal);

		//inisiasi tabulist
		LinkedList<int[][]> tabulist = new LinkedList<int[][]>();
		int maxtabusize = 10;
		tabulist.addLast(Utils.copySolution(jadwal));		

		//inisiasi iterasi
		int maxiteration = 1000;
		int iteration=0;

		//inisasi itung penalty
		double penalty1 = 0;
		double penalty2 = 0;
		double penalty3 = 0;
		
		boolean terminate = false;
		
		while(!terminate){
			iteration++;
			ArrayList<int[][]> sneighborhood = new ArrayList<>();

			Utils lowLevelHeuristics = new Utils(conflict_matrix);
        	timeslotTabuSearchSementara = lowLevelHeuristics.move(timeslotTabuSearchSementara,1);
        	sneighborhood.add(timeslotTabuSearchSementara);
        	timeslotTabuSearchSementara = lowLevelHeuristics.swap(timeslotTabuSearchSementara,2);
        	sneighborhood.add(timeslotTabuSearchSementara);
        	timeslotTabuSearchSementara = lowLevelHeuristics.move(timeslotTabuSearchSementara,2);
        	sneighborhood.add(timeslotTabuSearchSementara);
        	timeslotTabuSearchSementara = lowLevelHeuristics.swap(timeslotTabuSearchSementara,3);
        	sneighborhood.add(timeslotTabuSearchSementara);
        	timeslotTabuSearchSementara = lowLevelHeuristics.move(timeslotTabuSearchSementara,3);
			sneighborhood.add(timeslotTabuSearchSementara);
			
			//membandingkan neighbor, pilih best neighbor, membandingkan juga apa ada di tabu list
			int j = 0;
			while (sneighborhood.size() > j) {
	 //        	   penalty2 = Evaluator.getPenalty(conflict_matrix, sneighborhood.get(j), jumlahmurid);
	 //               penalty1 = Evaluator.getPenalty(conflict_matrix, bestcandidate, jumlahmurid);
				if( !(tabulist.contains(sneighborhood.get(j))) && 
						Utils.getPenalty(conflict_matrix, sneighborhood.get(j), jumlahStudent) < Utils.getPenalty(conflict_matrix, bestcandidate, jumlahStudent))
				  bestcandidate = sneighborhood.get(j);
					 
				j++;
			}

			sneighborhood.clear();

			//bandingkan best neighbor dengan best best solution
			if(Utils.getPenalty(conflict_matrix, bestcandidate, jumlahStudent) < Utils.getPenalty(conflict_matrix, jadwal, jumlahStudent))
			jadwal = Utils.copySolution(bestcandidate);

			//masukkan best neighbor tadi ke tabu
			tabulist.addLast(bestcandidate);
			if(tabulist.size() > maxtabusize)
			   tabulist.removeFirst();

			if (iteration == maxiteration) 
			   terminate = true;
		}
		bestPenalty = Utils.getPenalty(conflict_matrix, jadwal, jumlahStudent);
		System.out.println("Penalty Terbaik : " + bestPenalty); // print best penalty


		/*int jumlahStudent = cm.getJumlahStudent();
		
		int[][] jadwalTemp = new int[jadwal.length][2];
		
		for(int i = 0; i < jadwalTemp.length; i++) {
			jadwalTemp[i][0] = jadwal[i][0];
			jadwalTemp[i][1] = jadwal[i][1];
		}
		//init penalty, ga dipake
		double penalty = Utils.getPenalty(conflict_matrix, jadwal, jumlahStudent);

		Solution bestSolution = new Solution(jadwal);
		int max_timeslot = bestSolution.getJumlahTimeslot();
		*/
	}	
}
