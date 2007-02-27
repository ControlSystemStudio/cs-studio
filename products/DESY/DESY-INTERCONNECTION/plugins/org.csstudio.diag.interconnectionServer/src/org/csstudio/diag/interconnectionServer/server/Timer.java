package org.csstudio.diag.interconnectionServer.server;
import java.util.Comparator;
import java.util.Date;
import java.util.TreeSet;

/**
 * This class is a simple implementation of the Java 1.3 java.util.Timer API
 **/
public class Timer 
{
	
	public static final int scan01Sec = 1;
	public static final int scan02Sec = 2;
	public static final int scan05Sec = 3;
	public static final int scan1Sec = 5;
	public static final int scan2Sec = 6;
	public static final int scan5Sec = 7;
	public static final int scan10Sec = 10;
	public static final int scan20Sec = 20;
	public static final int scan50Sec = 50;
	public static final int scan100Sec = 100;
	public static final int scan200Sec = 200;
	public static final int scan500Sec = 500;
	public static final int scan1000Sec = 1000;
	public static final int scan2000Sec = 2000;
	
    //
    // connect to JoiMint registry
    //
    // public static registry registry = new registry();
    // public static randomNumber randomNumber = new randomNumber();

    // This sorted set stores the tasks that this Timer is responsible for.
    // It uses a comparator (defined below) to sort the task by execution time.
    TreeSet tasks = new TreeSet(new TimerTaskComparator());

    // This is the thread the timer uses to execute the tasks
    TimerThread timer;

    /** This constructor create a Timer that does not use a daemon thread */
    public Timer()
    { 
        this(false); 
    }

    /** The main constructor: the internal thread is a daemon if specified */
    public Timer(boolean isDaemon) 
    {
    	System.out.println ("in timer");
    	timer = new TimerThread(isDaemon);  // TimerThread is defined below
    	
	    timer.start();                      // Start the thread running
    }

    /** Stop the timer thread, and discard all scheduled tasks */
    public void cancel() 
    {
	    synchronized(tasks) {     // Only one thread at a time!
	        timer.pleaseStop();   // Set a flag asking the thread to stop
	        tasks.clear();        // Discard all tasks
	        tasks.notify();       // Wake up the thread if it is in wait().
	    }
    }

    /** Schedule a single execution after delay milliseconds */
    public void schedule(TimerTask task, long delay) 
    {
	    task.schedule(System.currentTimeMillis() + delay, 0, false);
	    schedule(task);
    }

    /** Schedule a single execution at the specified time */
    public void schedule(TimerTask task, Date time) 
    {
	    task.schedule(time.getTime(), 0, false);
	    schedule(task);
    }

    /** Schedule a periodic execution starting at the specified time */
    public void schedule(TimerTask task, Date firstTime, long period) 
    {
	    task.schedule(firstTime.getTime(), period, false);
	    schedule(task);
    }

    /** Schedule a periodic execution starting after the specified delay */
    public void schedule(TimerTask task, long delay, long period) 
    {
	    task.schedule(System.currentTimeMillis() + delay, period, false);
	    schedule(task);
    }

    /** 
     * Schedule a periodic execution starting after the specified delay.
     * Schedule fixed-rate executions period ms after the start of the last.
     * Instead of fixed-interval executions measured from the end of the last.
     **/
    public void scheduleAtFixedRate(TimerTask task, long delay, long period) 
    {
	    task.schedule(System.currentTimeMillis() + delay, period, true);
	    schedule(task);
    }

    /** Schedule a periodic execution starting after the specified time */
    public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period)
    {
	    task.schedule(firstTime.getTime(), period, true);
	    schedule(task);
    }


    // This internal method adds a task to the sorted set of tasks
    void schedule(TimerTask task) 
    {
	    synchronized(tasks) {  // Only one thread can modify tasks at a time!
	        tasks.add(task);   // Add the task to the sorted set of tasks
	        tasks.notify();    // Wake up the thread if it is waiting
	    }
    }

    /** 
     * This inner class is used to sort tasks by next execution time.
     **/
    static class TimerTaskComparator implements Comparator 
    {
	public int compare(Object a, Object b) {
	    TimerTask t1 = (TimerTask) a;
	    TimerTask t2 = (TimerTask) b;
	    long diff = t1.nextTime - t2.nextTime;
	    if (diff < 0) return -1;
	    else if (diff > 0) return 1;
	    else return 0;
	}
	public boolean equals(Object o) { return this == o; }
    }

    /**
     * This inner class defines the thread that runs each of the tasks at their
     * scheduled times
     **/
    class TimerThread extends Thread {
	// This flag is will be set true to tell the thread to stop running.
	// Note that it is declared volatile, which means that it may be 
	// changed asynchronously by another thread, so threads must always
	// read its true value, and not used a cached version.
	volatile boolean stopped = false;  

	// The constructor
	public TimerThread(boolean isDaemon) { setDaemon(isDaemon); }

	// Ask the thread to stop by setting the flag above
	public void pleaseStop() { stopped = true; }

	// This is the body of the thread
	public void run() {
	    
		System.out.println ("in run");
		TimerTask readyToRun = null;  // Is there a task to run right now?

	    // The thread loops until the stopped flag is set to true.
	    while(!stopped) {
		// If there is a task that is ready to run, then run it!
		if (readyToRun != null) { 
		    if (readyToRun.cancelled) {  // If it was cancelled, skip.
			readyToRun = null;
			continue;
		    }
		    // Run the task.
		    readyToRun.run();
		    // Ask it to reschedule itself, and if it wants to run 
		    // again, then insert it back into the set of tasks.
		    if (readyToRun.reschedule())
			schedule(readyToRun);
		    // We've run it, so there is nothing to run now
		    readyToRun = null;
		    // Go back to top of the loop to see if we've been stopped
		    continue;
		}

		// Now acquire a lock on the set of tasks
		synchronized(tasks) {
		    long timeout;  // how many ms 'till the next execution?

		    if (tasks.isEmpty()) {   // If there aren't any tasks
			timeout = 0;  // Wait 'till notified of a new task
		    }
		    else {
			// If there are scheduled tasks, then get the first one
			// Since the set is sorted, this is the next one.
			TimerTask t = (TimerTask) tasks.first();
			// How long 'till it is next run?
			timeout = t.nextTime - System.currentTimeMillis();
			// Check whether it needs to run now
			if (timeout <= 0) {
			    readyToRun = t;  // Save it as ready to run
			    tasks.remove(t); // Remove it from the set
			    // Break out of the synchronized section before
			    // we run the task
			    continue;
			}
		    }

		    // If we get here, there is nothing ready to run now,
		    // so wait for time to run out, or wait 'till notify() is
		    // called when something new is added to the set of tasks.
		    try { tasks.wait(timeout); }
		    catch (InterruptedException e) { System.out.println("Timer:run:"+e);
		    }

		    // When we wake up, go back up to the top of the while loop
		}
	    }
	}
    }
    
    /** This inner class defines a test program */
    public static class Start {
        
        // Create a timer, and schedule some tasks
	    static Timer timer;
	    
	    public static void init() {
	        timer = new Timer( true);
	    }
	
	public static void all() 
	{
	    //
	    // this should never be called
	    // we start only those time really necessary
	    //
	    final TimerTask t01 = new TimerTask() {
		    public void run() { 
		        SupervisoryControl.getInstance().scanManager(scan01Sec);
            }
		};
		
	    final TimerTask t02 = new TimerTask() {
		    public void run() { 
		        SupervisoryControl.getInstance().scanManager(scan02Sec);
            }
		};
		
	    final TimerTask t05 = new TimerTask() {
		    public void run() { 
		        SupervisoryControl.getInstance().scanManager(scan05Sec);
            }
		};
		
	    final TimerTask t1 = new TimerTask() {
		    public void run() { 
		        SupervisoryControl.getInstance().scanManager(scan1Sec);
		    }
		};
		
	    final TimerTask t2 = new TimerTask() {
		    public void run() { 
		        SupervisoryControl.getInstance().scanManager(scan2Sec);
    	    }
		};
		
	    final TimerTask t5 = new TimerTask() {
		    public void run() { 
		        SupervisoryControl.getInstance().scanManager(scan5Sec);
		    }
		};	  
	    
	    final TimerTask t10 = new TimerTask() {
	        public void run() {
		        SupervisoryControl.getInstance().scanManager(scan10Sec);
		    }
		};		

	    // Create a timer, and schedule some tasks
	    //final Timer timer = new Timer();
	    //timer.schedule(t01, 250,  100);    
	    //timer.schedule(t02, 500,  200);    
	    //timer.schedule(t05, 750,  500);    
	    timer.schedule(t1,  1000, 1000);
	    //timer.schedule(t2,  1250, 2000);
	    //timer.schedule(t5,  1500, 5000);
  	    timer.schedule(t10, 1750, 10000);
	}
	    
	public static void thisTimer( int scanTime) 
	{
	   //if ( DEBUG_TIMER) MainFrame.locallog( "Timer.Start.thisTimer(" + scan[ scanTime] + ") initialized");
	    switch (scanTime) {
	        
	        case scan01Sec:
	        
	            final TimerTask t01 = new TimerTask() {
		            public void run() { 
		                SupervisoryControl.getInstance().scanManager(scan01Sec);
                    }
		        };
		        timer.schedule(t01, 250,  100);  
		        break;
		        
		    case scan02Sec:
		            		
	            final TimerTask t02 = new TimerTask() {
		            public void run() { 
		                SupervisoryControl.getInstance().scanManager(scan02Sec);
                    }
		        };
		        timer.schedule(t02, 500,  200);    
		        break;
		        
		    case scan05Sec:
    		
	            final TimerTask t05 = new TimerTask() {
		            public void run() { 
		                SupervisoryControl.getInstance().scanManager(scan05Sec);
                    }
		        };
		        timer.schedule(t05, 750,  500);   
		        break;
		        
		    case scan1Sec:
    		
	            final TimerTask t1 = new TimerTask() {
		            public void run() { 
		                SupervisoryControl.getInstance().scanManager(scan1Sec);
		            }
		        };
		        timer.schedule(t1,  1000, 1000);
		        break;
		        
		    case scan2Sec:
    		
	            final TimerTask t2 = new TimerTask() {
		            public void run() { 
		                SupervisoryControl.getInstance().scanManager(scan2Sec);
    	            }
		        };
		        timer.schedule(t2,  1250, 2000);
		        break;
		        
		    case scan5Sec:
    		
	            final TimerTask t5 = new TimerTask() {
		            public void run() { 
		                SupervisoryControl.getInstance().scanManager(scan5Sec);
		            }
		        };	  
		        timer.schedule(t5,  1500, 5000);
		        break;
		        
		    case scan10Sec:
    	    
	            final TimerTask t10 = new TimerTask() {
	                public void run() {
		                SupervisoryControl.getInstance().scanManager(scan10Sec);
		            }
		        };		
		        timer.schedule(t10, 1750, 10000);
		        break;
		    /*    
		    case scanMonitor:
		    
		        // nothing to do !
		        if ( DEBUG_TIMER_ERROR_INFO) System.out.println( "Timer.start.thisTime (" + scanTime + ") : no thread created");
		        break;
		        
		    default:
		    
		        if ( DEBUG_TIMER_ERROR_INFO) System.out.println( "Timer.start.thisTime (" + scanTime + ") : not defined!");
		        break;
		        */
	        
	    } // switch
	    
	    
	    }
	    
	    public static void startTimerForDataSource ( TimerTask timerClass, int startDelay, 
	        int deltaTime, String startMessage)
	    {
	        timer.schedule(timerClass,  startDelay, deltaTime);
	        //if ( DEBUG_TIMER) System.out.println( "Timer.Start.startTimerForDataSource for " + startMessage);
	    }
    }
}
