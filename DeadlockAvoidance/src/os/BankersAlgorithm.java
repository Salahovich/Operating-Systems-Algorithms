package os;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Scanner;

public class BankersAlgorithm {

	private int processes = 5;
	private int resources = 3;
	private Scanner scan;
	private int[][] max;
	private int[][] need;
	private int[][] allocation;
	private int[] available;
	private BufferedReader reader;
	private String maxFile, allocationFile, availableFile;
	private StringBuilder processSequence;

	/**
	 * 
	 * @param maxFile
	 * @param allocFile
	 * @param availFile
	 * 
	 * normal constructor to initialize the file objects.
	 */
	public BankersAlgorithm(String maxFile, String allocFile, String availFile, int processNum, int resourceNum) {
		
		// Initialize Files
		this.maxFile = maxFile;
		this.allocationFile = allocFile;
		this.availableFile = availFile;
		this.processSequence = new StringBuilder();
		
		// Creating Matrices
		this.processes = processNum;
		this.resources = resourceNum;
		this.max = new int[processes][resources];
		this.need = new int[processes][resources];
		this.allocation = new int[processes][resources];
		this.available = new int[resources];

		
		//Initialize all Matrices
		initialize();
	}
	
	/**
	 * 
	 * @param matrix
	 * @param file
	 * 
	 * reading multiple line file
	 */
	private void readFileOfMulti(int[][] matrix, String file) {
		try{
			reader = new BufferedReader(new FileReader(file));
			String line = "";
			int i = 0;
			while((line = reader.readLine()) != null) {
				String[] numbersAsString = line.split(" ");
				for(int j = 0; j<resources; j++)
					matrix[i][j] = Integer.parseInt(numbersAsString[j]);
				i++;
			}
			reader.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param vector
	 * @param file
	 * 
	 * reading single line file
	 */
	private void readFileOfSingle(int[] vector, String file) {
		try{
			reader = new BufferedReader(new FileReader(file));
			String line = "";
			while((line = reader.readLine()) != null) {
				String[] numbersAsString = line.split(" ");
				for(int i = 0; i<resources; i++)
					vector[i] = Integer.parseInt(numbersAsString[i]);
			}
			reader.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param processIndex
	 * @param values
	 * 
	 * method for a process request to a specific resources
	 */
	private void request(int processIndex, int[] values) {
		int[] processNeed = this.need[processIndex];
		this.processSequence = new StringBuilder("The process sequence is: ");

		//checking if the request < need;
		for(int i=0; i<resources; i++) {
			if(values[i] > processNeed[i]) {
				System.out.println("The procees P" + processIndex + " requesting resources greater than its maximum need");
				return;
			}
			if(values[i] > this.available[i]) {
				System.out.println("The system has not enough resources for the process P" + processIndex + " in the current moment");
				return;
			}
		}
		
		if(safetyChecking(processIndex, values)) {
			System.out.println("The resources have been allocated to the process and the system state is SAFE");
			System.out.println(this.processSequence);
		}
		else {
			System.out.println("The system is in DeadLock state we will try to recover!");
			while(!recovery(processIndex, values)) {}
			System.out.println("The resources have been allocated to the process and the system state is SAFE");
			System.out.println(this.processSequence);
		}
	}
	
	/**
	 * @param processIndex
	 * @param values
	 * 
	 * method for a process release to a specific resources
	 */
	private void release(int processIndex, int[] values) {
		int[] processNeed = this.need[processIndex];
	
		if(!comparingVector(values, this.allocation[processIndex])) {
			System.out.println("UNCOMPATABLE VALUES!");
			return;
		}
		// incrementing the need vector for the process and decrementing the allocation vector;
		for(int i=0; i<resources; i++) {
			processNeed[i] += values[i];
			this.allocation[processIndex][i] -= values[i];
			this.available[i] += values[i];
		}
			
	}
	
	/**
	 * reading all files and initialize the data
	 */
	private void initialize() {
		readFileOfSingle(this.available,this.availableFile);
		readFileOfMulti(this.max, this.maxFile);
		readFileOfMulti(this.allocation, this.allocationFile);
		
		// calculate need matrix by subtracting max - allocation
		for(int i=0; i<processes; i++) {
			for(int j=0; j<resources; j++) {
				need[i][j] = max[i][j] - allocation[i][j];
			}
		}
	}
	
	private boolean recovery(int processIndex, int[] values) {
		int releasedProcess =  getVictim();
		this.processSequence = new StringBuilder("The process sequence is: ");

		int[] resourcesToRelease = new int[this.resources];
		for(int i=0; i<this.resources; i++) {
			if(this.allocation[releasedProcess][i] >= 1)
				resourcesToRelease[i] = 1;
		}
		
		release(releasedProcess, resourcesToRelease);

		System.out.println("The Process " + releasedProcess + " has been released the resources " + Arrays.toString(resourcesToRelease) + " !");

		if(safetyChecking(processIndex, values))
			return true;
		return false;
	}
	
	private int getVictim() {
		int index = 0;
		int sum = 0;
		int max = 0;
		
		for(int i=0; i<this.processes; i++) {
			sum = 0;
			for(int j=0; j<this.resources; j++)
				sum += this.allocation[i][j];
			
			if(sum > max) {
				max = sum;
				index = i;
			}
		}
		return index;
	}
	/**
	 * @param matrix
	 * 
	 * printing the matrix using Arrays class
	 */
	private void printMatrix(int[][] matrix) {
		for(int i=0; i<matrix.length; i++)
			System.out.println(Arrays.toString(matrix[i]));
	}
	
	/**
	 * printing the status of the system in current time
	 */
	private void printReport() {
		System.out.println("Max Matrix = ");
		printMatrix(this.max);
		
		System.out.println("Allocation Matrix = ");
		printMatrix(this.allocation);
		
		System.out.println("Need Matrix = ");
		printMatrix(this.need);
		
		System.out.println("Available Resources = ");
		System.out.println(Arrays.toString(this.available));
	}
	
	/**
	 * @return int[]
	 * 
	 * reading an array of resources from the console
	 */
	private int[] readVector() {
		int[] values = new int[this.resources];
		int i = 0;
		while(i<values.length) {
			values[i] = scan.nextInt();
			i++;
		}
		return values;
	}
	
	private boolean comparingVector(int[] first, int[] second) {
		for(int i=0; i<first.length; i++) {
			if(first[i] > second[i])
				return false;
		}
		return true;
	}
	/**
	 * @param processIndex
	 * @param values
	 * @return boolean
	 * 
	 * the banker's algorithm implementation
	 */
	private boolean safetyChecking(int processIndex, int[] values) {
		
		// pretending to allocate the process request
		for(int j=0; j<this.resources; j++) {
			this.available[j] -= values[j];
			this.need[processIndex][j] -= values[j];
			this.allocation[processIndex][j] += values[j];
		}
		
		// initialize work with available
		int[] work = Arrays.copyOf(this.available, this.resources);
		boolean[] finish = new boolean[this.processes];
				
		int i=0;
		boolean repeat = false;
		while(i<this.processes) {
			
			if(!finish[i] && comparingVector(this.need[i], work)) {
				// mark the process as finished
				finish[i] = true;
				repeat = true;
				
				// increment work with allocation
				for(int j=0; j<this.resources; j++)
					work[j] += this.allocation[i][j];
				
				// add the process to the sequence
				this.processSequence.append("P" + i +" ");
			}
			i++;
			// start the loop again
			if(i==this.processes && repeat) {
				i=0;
				repeat = false;
			}
		}
		
		// check the boolean array for the safety if one is false then release and return false
		for(int m=0; m<this.processes; m++) {
			if(!finish[m]) {
				release(processIndex, values);
				return false;				
			}
		}
		return true;
	}
	
	/**
	 * method to start the program and interact with the user.
	 */
	public void start() {
		scan = new Scanner(System.in);
		while(true) {				
			printReport();
			if(!safetyChecking(0, new int[this.resources])) {
				System.out.println("The system is UNSAFE sate");
				break;
			}
			System.out.println("Choose your command");
			System.out.println("1- Request a Resource");
			System.out.println("2- Release a Resource");
			System.out.println("3- Halt the programe");
			int userInput = scan.nextInt();
			
			if(userInput == 1) {
				System.out.println("Enter the procees number 0 based: ");
				int processNumber = scan.nextInt();
				
				// if the process number entered is non valid then continue
				if(processNumber >= this.processes) {
					System.out.println("Wrong process number!");
					continue;
				}
				
				// reading a vector and passing it to the request method to handle
				System.out.println("Enter the values for the resources space separated:");
				int[] values = readVector();
				this.request(processNumber, values);
				
			}else if(userInput == 2) {
				System.out.println("Enter the procees number 0 based: ");
				int processNumber = scan.nextInt();

				// if the process number entered is non valid then continue
				if(processNumber >= this.processes) {
					System.out.println("Wrong process number!");
					continue;
				}
				
				// reading a vector and passing it to the release method to handle
				System.out.println("Enter the values for the resources space separated:");
				int[] values = readVector();
				this.release(processNumber, values);
				
			}else if(userInput == 3) {
				System.out.println("The System has been halted succesfully");
				scan.close();
				break;
			}else {
				System.out.println("Wrong choice, please choice a valid command");
			}
		}
	}
	
	/**
	 * @param args
	 * 
	 * normal main method
	 */
	public static void main(String[] args) {
		
		// p1 1 0 2
		// p4 3 3 0
		// p0 0 2 0
		
		
		// assign the file paths
		String maxFile = "E:\\max.txt";
		String allocFile = "E:\\allocation.txt";
		String availFile = "E:\\available.txt";
		
		// creating an object and start the program
		BankersAlgorithm test = new BankersAlgorithm(maxFile, allocFile, availFile,5,3);
		test.start();
	}
}
