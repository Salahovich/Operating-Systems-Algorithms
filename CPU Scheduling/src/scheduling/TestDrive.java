package scheduling;

import java.util.ArrayList;
import java.util.List;

public class TestDrive {

	public static void main(String[] args) {
		Process p1 = new Process("p1", 0, 7);
		Process p2 = new Process("p2", 1, 5);
		Process p3 = new Process("p3", 2, 3);
		
		
		List<Process> list = new ArrayList<>();
		list.add(p1);
		list.add(p2);
		list.add(p3);
		
		/*CPU myCpu = new CPU(0,list);
		myCpu.SRJF();
		System.out.println(myCpu.toString());
		myCpu.average();
		*/
		
		CPU myCpu = new CPU(0,list);
		myCpu.SJF();
		System.out.println(myCpu.toString());
		myCpu.average();
		
	}
}
