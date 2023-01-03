package diskscheduling;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiskController {

	private String fileName;
	private StringBuilder sequence;
	
	private FileReader file;
	private	BufferedReader reader;
	
	private List<Integer> cylinders;
	private int totalCylinders, head;
	
	public DiskController(String fileName, int head, int total) {
		this.totalCylinders = total;
		this.fileName = fileName;
		this.cylinders = new ArrayList<Integer>();
		this.head = head;
		
		try {
			file = new FileReader(this.fileName);
			reader = new BufferedReader(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void readFile() {
		
		String line;
		try {
			while((line = this.reader.readLine()) != null) {
				String[] numString = line.split(" ");
				for(int i=0; i<numString.length; i++) 
					this.cylinders.add(Integer.parseInt(numString[i]));
			}
			
			this.reader.close();
			this.file.close();
		
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}	
	}
	
	private int getShortest(List<Integer> list, int head) {
		
		int minValue = Math.abs(list.get(0) - head), index = 0;
		
		for(int i=1; i<list.size(); i++) {
			int diff = Math.abs(list.get(i) - head);
			if(diff < minValue) {
				minValue = diff;
				index = i;
			}
		}
		
		return index;
	}
	
	private int FCFS() {
		this.sequence = new StringBuilder();
		
		int currHead = this.head;
		int total = 0;
		
		for(int i=0; i<this.cylinders.size(); i++) {
			total += Math.abs(this.cylinders.get(i) - currHead);
			currHead = this.cylinders.get(i);
			sequence.append(this.cylinders.get(i) + " ");
		}
		return total;
	}

	private int SSTF() {
		
		this.sequence = new StringBuilder();
		List<Integer> myList = new ArrayList<>(this.cylinders); 
		
		int currHead = this.head;
		int total = 0;
		
		while(!myList.isEmpty()) {
			int index = getShortest(myList, currHead);
			total += Math.abs(myList.get(index) - currHead);
			this.sequence.append(myList.get(index) + " ");
			
			currHead = myList.get(index);
			myList.remove(index);
		}
		return total;
	}
	
	private int SCAN(char direction) {
		
		this.sequence = new StringBuilder();
		int total = 0, currHead = this.head, start=0;
		
		List<Integer> myList = new ArrayList<>(this.cylinders);
		Collections.sort(myList);

		if(currHead < 0 || currHead>=this.totalCylinders)
			return -1;
		
		
		// locate the start cylinder 
		if(currHead >= myList.get(myList.size()-1))
			start = myList.size();
		else if(currHead <= myList.get(0))
			start = 0;
		for(int i=0; i<myList.size(); i++) {
			if(myList.get(i) > currHead) {
				start = i;
				break;
			}
		}
		
		if(direction == 'L') {
			start--;
			// if the currHead after the last cylinder
			if(start == myList.size()-1)
				total += Math.abs(currHead - myList.get(0));
			else
				total += Math.abs(currHead - 0);
			
			// append to the sequence 
			for(int i=start; i>=0; i--)
				this.sequence.append(myList.get(i) + " ");
			
			// reverse the head
			start++;
			if(start >= myList.size())
				return total;
			total += myList.get(start);
			total += Math.abs(myList.get(myList.size()-1) - myList.get(start));
			for(int i=start; i<myList.size(); i++)
				this.sequence.append(myList.get(i) + " ");
		}else {
			if(start == 0)
				total += Math.abs(currHead - myList.get(myList.size() - 1));
			else
				total += Math.abs(currHead - (this.totalCylinders));
			for(int i=start; i<myList.size(); i++)
				this.sequence.append(myList.get(i) + " ");
			
			// reverse the head
			start--;
			if(start < 0)
				return total;
			total += Math.abs(myList.get(start) - (this.totalCylinders));
			total += Math.abs(myList.get(0) - myList.get(start));
				
			for(int i=start; i>=0; i--)
				this.sequence.append(myList.get(i) + " ");
		}
		
		return total;
	}
	
	private int C_SCAN(char direction) {
		this.sequence = new StringBuilder();
		int total = 0, currHead = this.head, start=0;
		
		List<Integer> myList = new ArrayList<>(this.cylinders);
		Collections.sort(myList);

		if(currHead < 0 || currHead>=this.totalCylinders)
			return -1;
		
		
		// locate the start cylinder 
		if(currHead >= myList.get(myList.size()-1))
			start = myList.size();
		else if(currHead <= myList.get(0))
			start = 0;
		for(int i=0; i<myList.size(); i++) {
			if(myList.get(i) > currHead) {
				start = i;
				break;
			}
		}
		
		if(direction == 'L') {
			start--;
			// if the currHead after the last cylinder
			if(start == myList.size()-1)
				total += Math.abs(currHead - myList.get(0));
			else
				total += Math.abs(currHead - 0);
			
			// append to the sequence 
			for(int i=start; i>=0; i--)
				this.sequence.append(myList.get(i) + " ");
			
			// start from the second end
			start++;
			if(start >= myList.size())
				return total;
			total += this.totalCylinders;
			total += Math.abs((this.totalCylinders) - myList.get(start));
			for(int i=myList.size()-1; i>=start; i--)
				this.sequence.append(myList.get(i) + " ");
		}else {
			if(start == 0)
				total += Math.abs(currHead - myList.get(myList.size() - 1));
			else
				total += Math.abs(currHead - (this.totalCylinders));
			for(int i=start; i<myList.size(); i++)
				this.sequence.append(myList.get(i) + " ");
			
			// start from the second end
			start--;
			if(start < 0)
				return total;
			total += this.totalCylinders;
			total += myList.get(start);
				
			for(int i=0; i<=start; i++)
				this.sequence.append(myList.get(i) + " ");
		}
		
		return total;
	}
	 
	private int LOOK(char direction) {
		this.sequence = new StringBuilder();
		int total = 0, currHead = this.head, start=0;
		
		List<Integer> myList = new ArrayList<>(this.cylinders);
		Collections.sort(myList);

		if(currHead < 0 || currHead>=this.totalCylinders)
			return -1;
		
		
		// locate the start cylinder 
		if(currHead >= myList.get(myList.size()-1))
			start = myList.size();
		else if(currHead <= myList.get(0))
			start = 0;
		for(int i=0; i<myList.size(); i++) {
			if(myList.get(i) > currHead) {
				start = i;
				break;
			}
		}
		
		if(direction == 'L') {
			
			start--;
			total += Math.abs(currHead - myList.get(0));
			
			// append to the sequence 
			for(int i=start; i>=0; i--)
				this.sequence.append(myList.get(i) + " ");
			
			// reverse the head
			start++;
			if(start >= myList.size())
				return total;
			total += Math.abs(myList.get(myList.size()-1) - myList.get(0));
			for(int i=start; i<myList.size(); i++)
				this.sequence.append(myList.get(i) + " ");
		}else {
			
			total += Math.abs(currHead -  myList.get(myList.size() - 1));
			for(int i=start; i<myList.size(); i++)
				this.sequence.append(myList.get(i) + " ");
			
			// reverse the head
			start--;
			if(start < 0)
				return total;
			total += Math.abs(myList.get(start) - myList.get(myList.size() - 1));
			total += Math.abs(myList.get(0) - myList.get(start));
				
			for(int i=start; i>=0; i--)
				this.sequence.append(myList.get(i) + " ");
		}
		
		return total;
	}
	
	private int C_LOOK(char direction) {
		this.sequence = new StringBuilder();
		int total = 0, currHead = this.head, start=0;
		
		List<Integer> myList = new ArrayList<>(this.cylinders);
		Collections.sort(myList);

		if(currHead < 0 || currHead>=this.totalCylinders)
			return -1;
		
		
		// locate the start cylinder 
		if(currHead >= myList.get(myList.size()-1))
			start = myList.size();
		else if(currHead <= myList.get(0))
			start = 0;
		for(int i=0; i<myList.size(); i++) {
			if(myList.get(i) > currHead) {
				start = i;
				break;
			}
		}
		
		if(direction == 'L') {
			start--;
			total += Math.abs(currHead - myList.get(0));
			
			// append to the sequence 
			for(int i=start; i>=0; i--)
				this.sequence.append(myList.get(i) + " ");
			
			// start from the second end
			start++;
			if(start >= myList.size())
				return total;
			total += Math.abs(myList.get(myList.size()-1) - myList.get(0));
			total += Math.abs(myList.get(myList.size()-1) - myList.get(start));
			for(int i=myList.size()-1; i>=start; i--)
				this.sequence.append(myList.get(i) + " ");
		}else {
			
			total += Math.abs(currHead - myList.get(myList.size() - 1));
			for(int i=start; i<myList.size(); i++)
				this.sequence.append(myList.get(i) + " ");
			
			// start from the second end
			start--;
			if(start < 0)
				return total;
			total += Math.abs(myList.get(myList.size()-1) - myList.get(0));;
			total += Math.abs(myList.get(start) - myList.get(0));
				
			for(int i=0; i<=start; i++)
				this.sequence.append(myList.get(i) + " ");
		}
		
		return total;
	}
	
	private int OPTIMIZED() {
		this.sequence = new StringBuilder();
		
		List<Integer> myList = new ArrayList<>(this.cylinders);
		Collections.sort(myList);
		
		int currHead = 0, total = 0;
		
		for(int i=0; i<myList.size(); i++) {
			total += myList.get(i) - currHead;
			currHead = myList.get(i);
			this.sequence.append(myList.get(i) + " ");
		}
		return total;
	}
	
	public void startSimulation() {
		this.readFile();
		System.out.println("Shortest Seek Time First: " + this.SSTF() + " Moves. " + " The sequence is: " + this.sequence);
		System.out.println("First Come First Served: " + this.FCFS() + " Moves. " + " The sequence is: " + this.sequence);
		System.out.println("SCAN-LEFT: " + this.SCAN('L') + " Moves. " + " The sequence is: " + this.sequence);
		System.out.println("SCAN-RIGHT: " + this.SCAN('R') + " Moves. " + " The sequence is: " + this.sequence);
		System.out.println("C-SCAN-LEFT: " + this.C_SCAN('L') + " Moves. " + " The sequence is: " + this.sequence);
		System.out.println("C-SCAN-RIGHT: " + this.C_SCAN('R') + " Moves. " + " The sequence is: " + this.sequence);
		System.out.println("LOOK-LEFT: " + this.LOOK('L') + " Moves. " + " The sequence is: " + this.sequence);
		System.out.println("LOOK-RIGHT: " + this.LOOK('R') + " Moves. " + " The sequence is: " + this.sequence);
		System.out.println("C-LOOK-LEFT: " + this.C_LOOK('L') + " Moves. " + " The sequence is: " + this.sequence);
		System.out.println("C-LOOK-RIGHT: " + this.C_LOOK('R') + " Moves. " + " The sequence is: " + this.sequence);
		System.out.println("New Optimized: " + this.OPTIMIZED()+ " Moves. " + " The sequence is: " + this.sequence);
	}
	
	public static void main(String[] args) {
		
		DiskController d = new DiskController("E:\\file.txt",53,200);
		d.startSimulation();
	}
}
