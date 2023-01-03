package scheduling;

public class Process implements Comparable<Process>{

	public  String name;
	public int count = 0;
	public int burstTime, turnAroundTime, arrivalTime, waitingTime;
	
	public Process(String name, int arrival, int burst){
		this.name = name;
		this.arrivalTime = arrival;
		this.burstTime = burst;
	}
	
	public int compareTo(Process p) {
		if(this.burstTime-this.count > p.burstTime-p.count)
			return 1;
		else if(this.burstTime-this.count < p.burstTime-p.count)
			return -1;
		else
			return 0;
	}
	
	public String toString() {
		return "Process : " + name + "\nArrivalTime: " + arrivalTime + 
			 "\nWaitingTime: " + waitingTime + "\nTurnAroundTime: " + turnAroundTime;
	}
}
