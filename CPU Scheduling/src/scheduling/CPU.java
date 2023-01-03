package scheduling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class CPU {

	private int contextSwitch;
	
	private List<Process> readyProcesses = new ArrayList<>();
	private List<Process> deadProcesses = new ArrayList<>();
	private List<Process> waitingProcesses = new ArrayList<>();
	private Process runningProcess;
	
	public CPU(int cs, List<Process> ready) {
		this.contextSwitch = cs;
		this.readyProcesses = ready;
	}
	
	private List<Process> findProcess(int time) {
		List<Process> list = new ArrayList<>();
		for(Process p : readyProcesses) {
			if(p.arrivalTime == time)
				list.add(p);
		}
		return list;
	}
		
	public void SJF() {
		int time = 0;
		while(deadProcesses.size() != readyProcesses.size()) {
			if(findProcess(time).size() != 0) {
				List<Process> list =findProcess(time); 
				for(int i=0; i<list.size(); i++)
					waitingProcesses.add(list.get(i));
				Collections.sort(waitingProcesses);
			}
			if(runningProcess == null && waitingProcesses.size()!=0) {
				// solve starvation
				for(Process p : waitingProcesses) {
					if(time-p.arrivalTime-p.count > 2*p.burstTime) {
						runningProcess = p;
						waitingProcesses.remove(p);
					}
				}
				if(runningProcess == null) {
					runningProcess = waitingProcesses.remove(0);
				}
			}
			if(runningProcess != null) {
				runningProcess.count++;
				if(runningProcess.burstTime == runningProcess.count) {
					runningProcess.turnAroundTime = time - runningProcess.arrivalTime + 1;
					runningProcess.waitingTime = runningProcess.turnAroundTime - runningProcess.burstTime;
					deadProcesses.add(runningProcess);
					runningProcess = null;
				}
			}
			time++;
		}
	}
	
	public void SRJF() {
		int time= 0, context=0;
		while(deadProcesses.size() != readyProcesses.size()) {
			if(findProcess(time).size() != 0) {
				List<Process> list =findProcess(time); 
				for(int i=0; i<list.size(); i++) {
					// find preemptive process
					if(runningProcess != null && runningProcess.burstTime - runningProcess.count >list.get(i).burstTime - list.get(i).count) {
						Process temp = runningProcess;
						runningProcess = list.get(i);
						waitingProcesses.add(temp);
					}else
						waitingProcesses.add(list.get(i));
				}
				context = 0;
				Collections.sort(waitingProcesses);
			}
				if(runningProcess == null && waitingProcesses.size()!=0) {
				// solve starvation
				for(Process p : waitingProcesses) {
					if(time-p.arrivalTime-p.count > 2*p.burstTime) {
						runningProcess = p;
					}
				}
				if(runningProcess == null)
					runningProcess = waitingProcesses.remove(0);
				else
					waitingProcesses.remove(runningProcess);
			}
			if(this.contextSwitch == context && this.contextSwitch != 0) {
				if(waitingProcesses.size() != 0) {
					Process temp = runningProcess;
					runningProcess = waitingProcesses.remove(0);
					waitingProcesses.add(temp);
					Collections.sort(waitingProcesses);
				}
				context=0;
			}
			if(runningProcess != null) {
				runningProcess.count++;					
				if(runningProcess.burstTime == runningProcess.count) {
					runningProcess.turnAroundTime = time - runningProcess.arrivalTime + 1;
					runningProcess.waitingTime = runningProcess.turnAroundTime - runningProcess.burstTime;
					deadProcesses.add(runningProcess);
					runningProcess = null;
					context=0;
				}
			}
			time++;
			context++;
		}
	}
	
	public void average() {
		int sum = 0;
		for(Process p : deadProcesses)
			sum += p.waitingTime;
		double avg = (double) sum/deadProcesses.size();
		System.out.println("AVG waiting time = " + avg);
	}
	public String toString() {
		return deadProcesses.toString();
	}
}
