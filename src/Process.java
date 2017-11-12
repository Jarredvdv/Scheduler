import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

enum State { //Enum provides a data type for known constants, which in this case is the process state
	UNSTARTED, READY, RUNNING, BLOCKED, TERMINATED
}

class Process implements Comparable<Process> {
    
	static ArrayList<Integer> randInts = new ArrayList<>(); //The list of random integers that we read in
	
	State cur_state; //Uses the enum we set up earlier to hold cur_state, useful for case switches
	
	public int arrival_time; //A
    public int rbound; //B
    public int req_time; //C
    public int io_multiplier; //M
    int init_arrivaltime;
    int cur_CPUtime, cur_IOtime = 0;
    int tot_CPUtime, tot_IOtime = 0;
    int burst_time, io_burst = 0;
    int wait_time = 0;
    int pid, rr_pid;
    float penalty_ratio;
    static int quantum = Integer.MIN_VALUE;

    public Process(int a, int b, int c, int m, int pid) { //Constructor for process objects
    	cur_state = State.UNSTARTED; //All processes begin as unstarted
    	arrival_time = init_arrivaltime = a;
        rbound = b;
        req_time = c;
        io_multiplier = m;
        this.pid = pid;       
    }
    
    //In order to sort processes, compareTo must be implemented
    public int compareTo(Process other) {//Here we run through a series of variable comparisons to determine how two processes compare
        if (arrival_time > other.arrival_time) {
            return 1;
        } else if (arrival_time < other.arrival_time) {
            return -1;
        } else if ((req_time - tot_CPUtime) > (other.req_time - other.tot_CPUtime)){
            return 1;
        } else if((req_time - tot_CPUtime) < (other.req_time - other.tot_CPUtime)) {
            return -1;
        } else if (pid > other.pid) {
            return 1;
        } else if(pid < other.pid) {
            return -1;
        }
        else if (penalty_ratio > other.penalty_ratio){
        	return 1;
        } else if (penalty_ratio < other.penalty_ratio){
        	return -1;
        } else {
        	return 0;//If all variables are the same, then they are considered equal 
        }
     }
    
    void set_running() {//Handles running processes by determing burst time from randomly generated integers
        this.cur_state = State.RUNNING;
        try (Scanner randGen = new Scanner(new File("random-numbers.txt"))){
        if(burst_time == 0) {
        	if(randInts.size() == 0) {            	 
                    while(randGen.hasNext()) {
                        randInts.add(randGen.nextInt());
                    }
                }
            }
            int rand_val = randInts.remove(0);
            burst_time = (rand_val % rbound) + 1;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    void increment_CPU() {
        cur_CPUtime++;
        tot_CPUtime++;

        if(req_time == tot_CPUtime) {
            cur_state = State.TERMINATED;
        }
        else if(cur_CPUtime == burst_time && burst_time != 0) {
        	cur_state = State.BLOCKED;
        	io_burst = io_multiplier * burst_time;
            burst_time = cur_CPUtime = 0;            
        }
        else if(cur_CPUtime % quantum == 0 && quantum > 0) {
            if(burst_time != 0 && cur_CPUtime == burst_time) {
            	cur_state = State.BLOCKED;
            	io_burst = io_multiplier * burst_time;
                burst_time = cur_CPUtime = 0;                
                
        } else {
            cur_state = State.READY;
            }
        }
    }
    
    public void increment_IO() {
    	cur_IOtime++;
        tot_IOtime++;

        if(cur_IOtime == io_burst) {//Process is ready if IO time is equivalent to the IO burst
        	cur_state = State.READY;
        	io_burst = cur_IOtime = 0;           
        }
    }

    void down_arrival() {
        arrival_time--;
        if(arrival_time < 0) {
            cur_state = State.READY;
        }
    }
    
    public void print_detailedOutput() {
        int remaining_burst = 0;
        String state = "";
        switch (cur_state) {
        	case UNSTARTED:
        		state = "unstarted";
        		break;
        	case READY:
        		state = "ready";
        		break;
        	case RUNNING:
        		state = "running";
        		if((burst_time - cur_CPUtime) < quantum)
        			remaining_burst = burst_time - cur_CPUtime;
        		else
        			remaining_burst = 0;
        		//Calculates burst for RR 
        		if(quantum > 0) { 
                	remaining_burst = quantum - (cur_CPUtime % quantum) - remaining_burst;
                } else
                	remaining_burst = burst_time - cur_CPUtime;
                break;
            case BLOCKED:
            	state = "blocked";
            	remaining_burst = io_burst - cur_IOtime;
                break;            
            case TERMINATED:
            	state = "terminated";
                break;
        }
        System.out.print(state + " " + remaining_burst + ".\t");
    }
    
    public void print_processInfo() {
        System.out.println("\t (A,B,C,M) = (" + init_arrivaltime + "," + rbound + "," + req_time + "," + io_multiplier + ")");
        System.out.println("\t Finishing time: " + (tot_IOtime + tot_CPUtime + wait_time + init_arrivaltime));
        System.out.println("\t Turnaround time: " + (tot_CPUtime + tot_IOtime + wait_time));
        System.out.println("\t I/O time: " + tot_IOtime);
        System.out.println("\t Waiting time: " + wait_time);
    }

    

    public void reset_variables() {
    	cur_state = State.UNSTARTED;
    	arrival_time = init_arrivaltime; 
    	wait_time = 0;
        burst_time = 0;
    	tot_CPUtime = 0;
        tot_IOtime = 0;
    	cur_CPUtime = 0;
        cur_IOtime = 0;     
    }
}
