import java.util.ArrayList;
import java.util.Collections;

public class FCFS extends Scheduler {
	
	public int io_time;
	
    public FCFS() {
        sorted_proc = new ArrayList<Process>(); 
        sort_proc();
    }
    
    void proc_arrival() {
        for(Process process : sorted_proc) {
            if(process.cur_state == State.UNSTARTED) {
                process.down_arrival();
                if(process.cur_state == State.READY) {
                    ready_proc.add(process);
                }
            }
        }
    }
    void proc_blocked() {
        for(Process process : sorted_proc) {
            if(process.cur_state == State.BLOCKED) {
                process.increment_IO();
                io_time++;                
                if(process.cur_state == State.READY) {
                    ready_proc.add(process);
                }
            }
        }
    }

    void proc_running() {
        for(Process process : sorted_proc) {
            if(process.cur_state == State.RUNNING) {
                process.increment_CPU();
                if(process.cur_state != State.RUNNING) {
                    temp_proc = null;
                }
            }
        }
    }
    void proc_ready() {
        if(ready_proc.size() > 0) {
            Process process = ready_proc.get(0);
            if (temp_proc == null) {
                process.set_running();
                temp_proc = process;
                ready_proc.remove(0);
            }
        }
    }
    void sort_proc() {
    	for(Process process:unsorted_proc) {
            sorted_proc.add(process);
        }        
        Collections.sort(sorted_proc);
    }
    
    void execute_cycle() {
        proc_blocked();
        proc_arrival();
        proc_running();
        proc_ready();
    }
}
