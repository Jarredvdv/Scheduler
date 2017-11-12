import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class HPRN extends FCFS {
	ArrayList<Process> hprn_list = new ArrayList<>();
	
	public HPRN() {
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
            process.penalty_ratio = (float)(process.wait_time + process.burst_time)/(Math.max(1, process.tot_CPUtime));
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
    
    void proc_hprnhelper() {
        Collections.sort(hprn_list, new Comparator<Process>() {
            //Since we're working with rr, we must override the compare implemented in the process class which allows us to compare rr pids
        	@Override
            public int compare(Process a, Process b) {
                return Float.compare(a.penalty_ratio, b.penalty_ratio);
            }
        });
        ready_proc.addAll(hprn_list);
        hprn_list.clear();
    }
    
    void execute_cycle() {
        proc_blocked();
        proc_arrival();
        proc_running();
        proc_hprnhelper();
        proc_ready();
    }
}

