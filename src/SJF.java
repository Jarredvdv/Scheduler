import java.util.Collections;

public class SJF extends FCFS {

    public SJF() {
    }
    void processReady() { //Follows similar implementation as FCFS 
        Collections.sort(ready_proc); //Since we've implemented a comparison method, we can easily sort by time req
        super.proc_ready();
    }

}
