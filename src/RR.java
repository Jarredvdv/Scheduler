import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RR extends FCFS {

    ArrayList<Process> rr_list = new ArrayList<>();

    public RR() {
    	Process.quantum = 2;
    	sorted_proc = new ArrayList<Process>();

        for(Process process : unsorted_proc) {
            sorted_proc.add(process);
        }
        Collections.sort(sorted_proc);

        for(int i = 0; i < sorted_proc.size(); i++) {
            Process process = sorted_proc.get(i);
            process.rr_pid = i;
        }
    }
    
    void proc_arrival() {//Handles new processes
        for(Process process : sorted_proc) {//
            if(process.cur_state == State.UNSTARTED) {
                process.down_arrival();
                if(process.cur_state == State.READY) {
                    rr_list.add(process);
                }
            }
        }
    }
    
    void proc_blocked() {//Checks for process blocked, if so, we switch to IO
        for(Process process : sorted_proc) {
            if(process.cur_state == State.BLOCKED) {
            	io_time++;
            	process.increment_IO();
                if(process.cur_state == State.READY) {
                    rr_list.add(process);
                }
            }
        }
    }

    void proc_running() {//Check on the running process state to determine next action
        for(Process process : sorted_proc) {
            if(process.cur_state == State.RUNNING) {
                process.increment_CPU();
                if(process.cur_state != State.RUNNING) {
                    temp_proc = null;
                    if(process.cur_state == State.READY) {
                        rr_list.add(process);
                    }
                }
            }
        }
    }

    void proc_helper() {
        Collections.sort(rr_list, new Comparator<Process>() {
            //Since we're working with rr, we must override the c compare pids
        	@Override
            public int compare(Process p1, Process p2) {
                return Integer.compare(p1.rr_pid, p2.rr_pid);
            }
        });
        ready_proc.addAll(rr_list);
        rr_list.clear();
    }
    
    void execute_cycle() {
        proc_arrival();
        proc_blocked();
        proc_running();
        proc_helper();
        proc_ready();
    }

}
