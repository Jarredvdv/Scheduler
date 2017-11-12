import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;

public class Scheduler {
    
	static ArrayList<Process> unsorted_proc = new ArrayList<Process>();
    ArrayList<Process> sorted_proc;
    ArrayList<Process> ready_proc = new ArrayList<>();
    Process temp_proc = null;

    int iteration = 0;
    int io_time = 0;
    int tot_runtime = 0;
    
    static boolean is_verbose = false;

    public static void main(String [] args){
        int num_args = 0;

        if (args[0].equals("--verbose")) {
            is_verbose = true;
            num_args++;
        }

        try(Scanner input_scan = new Scanner(new File(args[num_args]))){;
        
        int num_procs = input_scan.nextInt();
        

        for(int i = 0; i < num_procs; i++) {
        	int arrival = input_scan.nextInt();
            int rbound = input_scan.nextInt();
            int computeTime = input_scan.nextInt();
            int io_multiplier = Character.getNumericValue(input_scan.next().charAt(0));
            unsorted_proc.add(new Process(arrival, rbound, computeTime, io_multiplier, i));
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        FCFS fcfs = new FCFS();
        fcfs.run_FCFS();

        SJF sjf = new SJF();
        sjf.run_SJF();
        
        RR rr = new RR();
        rr.run_RR();
        
        HPRN hprn = new HPRN();
        hprn.run_HPRN();
    }
    

    public void run_FCFS() {
    	FCFS fcfs = new FCFS();
    	print_inputinfo();        
        if(is_verbose)
        	System.out.println("This detailed printout gives the state and remaining burst for each process\n");
        
        while(hasMoreProcesses()) {
        	for(Process process : sorted_proc) {
                if(process.cur_state == State.READY) {
                    process.wait_time++;
                }
            }

            if(is_verbose){
            	System.out.print("Before cycle\t" + tot_runtime + ":\t");
	            for(Process process : sorted_proc) {
	                process.print_detailedOutput();
	                
	            }
            System.out.println();
            }
            fcfs.execute_cycle();

            tot_runtime++;
            iteration++;
        }
        
        System.out.println("\nThe scheduling algorithm used was First Come First Served\n");

        for(int i = 0; i < sorted_proc.size(); i++) {
            System.out.println("Process " + i + ": ");
            sorted_proc.get(i).print_processInfo();
            System.out.println();
        }

        tot_runtime--;
        print_output();

        //Housekeeping which clears and resets the random integers & process variables
        Process.randInts.clear(); 
        for(Process process : unsorted_proc) {
            process.reset_variables();
        }
    }
    
    public void run_RR() {
    	RR rr = new RR();
        Process.quantum = Integer.MIN_VALUE;
        print_inputinfo();
        
        if(is_verbose)
        	System.out.println("This detailed printout gives the state and remaining burst for each process\n");
        
        while(hasMoreProcesses()) {
        	for(Process process : sorted_proc) {
        		
        		if(process.cur_state == State.READY) {
                    process.wait_time++;
                }
            }

            if(is_verbose){
            	System.out.print("Before cycle\t" + tot_runtime + ":\t");
	            for(Process process : sorted_proc) {
	                process.print_detailedOutput();
	                
	            }
            System.out.println();
            }
            
            rr.execute_cycle();
            
            tot_runtime++;
            iteration++;
        }
        
        System.out.println("\nThe scheduling algorithm used was Round Robin\n");

        for(int i = 0; i < sorted_proc.size(); i++) {
            System.out.println("Process " + i + ": ");
            sorted_proc.get(i).print_processInfo();
            System.out.println();
        }

        tot_runtime--;
        print_output();

        Process.randInts.clear(); 
        for(Process process : unsorted_proc) {
            process.reset_variables();
        }
    }
    public void run_HPRN() {
    	HPRN hprn = new HPRN();
    	print_inputinfo();
        
        if(is_verbose)
        	System.out.println("This detailed printout gives the state and remaining burst for each process\n");
        
        while(hasMoreProcesses()) {
        	for(Process process : sorted_proc) {
                if(process.cur_state == State.READY) {
                    process.wait_time++;
                }
            }

            if(is_verbose){
            	System.out.print("Before cycle\t" + tot_runtime + ":\t");
	            for(Process process : sorted_proc) {
	                process.print_detailedOutput();
	                
	            }
            System.out.println();
            }
            hprn.execute_cycle();

            tot_runtime++;
            iteration++;
        }
        
        System.out.println("\nThe scheduling algorithm used was Highest Penalty Ratio Next\n");

        for(int i = 0; i < sorted_proc.size(); i++) {
            System.out.println("Process " + i + ": ");
            sorted_proc.get(i).print_processInfo();
            System.out.println();
        }

        tot_runtime--;
        print_output();

        //Housekeeping which clears and resets the random integers & process variables
        Process.randInts.clear(); 
        for(Process process : unsorted_proc) {
            process.reset_variables();
        }
    }
    
    public void run_SJF() {
    	SJF sjf = new SJF();
    	print_inputinfo();
        
        if(is_verbose)
        	System.out.println("This detailed printout gives the state and remaining burst for each process\n");
        
        while(hasMoreProcesses()) {
        	for(Process process : sorted_proc) {
                if(process.cur_state == State.READY) {
                    process.wait_time++;
                }
            }

            if(is_verbose){
            	System.out.print("Before cycle\t" + tot_runtime + ":\t");
	            for(Process process : sorted_proc) {
	                process.print_detailedOutput();
	                
	            }
            System.out.println();
            }
            sjf.execute_cycle();

            tot_runtime++;
            iteration++;
        }
        
        System.out.println("\nThe scheduling algorithm used was Shortest Job First\n");

        for(int i = 0; i < sorted_proc.size(); i++) {
            System.out.println("Process " + i + ": ");
            sorted_proc.get(i).print_processInfo();
            System.out.println();
        }

        tot_runtime--;
        print_output();

        //Housekeeping which clears and resets the random integers & process variables
        Process.randInts.clear(); 
        for(Process process : unsorted_proc) {
            process.reset_variables();
        }
    }
  
  public void print_inputinfo(){
	  System.out.print("The original input was:\t" + unsorted_proc.size());
      for(Process process : unsorted_proc) {
          System.out.print(" (" + process.init_arrivaltime + " " + process.rbound + " " + process.req_time + " " + process.io_multiplier + ")");
      }

      System.out.print("\nThe (sorted) input is:\t" + sorted_proc.size());
      for(Process process : sorted_proc) {
          System.out.print(" (" + process.init_arrivaltime + " " + process.rbound + " " + process.req_time + " " + process.io_multiplier + ")");
      }
      System.out.println("\n");
  }
  
  public void print_output(){
    	float cpu_sum = 0;
        float turnaround_sum = 0;
        float wait_sum = 0;
        float io_sum = 0;
        
        for(Process process : sorted_proc) {
        	wait_sum += process.wait_time;
        	cpu_sum += process.tot_CPUtime;
        	io_sum += process.tot_IOtime;
            turnaround_sum += process.tot_IOtime + process.tot_CPUtime + process.wait_time;
        }
        
        //Quick calculations for each process utilization and times
        float CPUUtil = cpu_sum / tot_runtime;
        float IOUtil = io_sum / tot_runtime;
        float turnaround = turnaround_sum / sorted_proc.size();
        float throughput = ((float)sorted_proc.size() / tot_runtime)*100;
        float avg_wait = wait_sum / sorted_proc.size();
        
        
        System.out.println("Summary Data:");
        System.out.println("\t Finishing time: " + tot_runtime);
        System.out.println("\t CPU Utilization: " + CPUUtil);
        System.out.println("\t I/O Utilization: " + IOUtil);
        System.out.println("\t Throughput: " + throughput + " processes per hundred cycles");
        System.out.println("\t Average turnaround time: " + turnaround);
        System.out.println("\t Average waiting time: " + avg_wait + "\n");
       
        
    }
    
  //Checks sorted processes for any process that still exist
    public final boolean hasMoreProcesses() { 
        for(Process process : sorted_proc) {
            if(process.cur_state != State.TERMINATED)
                return true;
        }
        return false;
    }

}
