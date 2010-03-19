package org.csstudio.opibuilder.util;

import java.util.Timer;
import java.util.TimerTask;

/**A customized timer for the internal use of OPI builder. 
 * It adapts the java {@link Timer} by wrapping it in. 
 * It can only host one {@link TimerTask}, which will be executed when timer is due.
 * The timer will stop automatically when it is due.
 * <pre>
 *   |------------|---------------|-----------|
 * Start      Due/start task  task done  timer stop
 * </pre>
 * @author Xihui Chen
 *
 */
public class CopyOfOPITimer {

	private Timer timer;
	
	private Runnable task;
	
	private long delay;
	
	private boolean running = false;
	
	private boolean due = true;
	
	/**
     * Schedules the specified task for execution after the specified delay.
     *
     * @param task  task to be scheduled.
     * @param delay delay in milliseconds before task is to be executed.
     * @throws IllegalArgumentException if <tt>delay</tt> is negative, or
     *         <tt>delay + System.currentTimeMillis()</tt> is negative.
     * @throws IllegalStateException if task was already scheduled or
     *         cancelled, or timer was cancelled.
     */
    public synchronized void start(final Runnable task, long delay) {    	
    	this.delay = delay;
    	this.task = task;
    	if(timer != null)
    		stop();
    	timer = new Timer();
    	timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				due = true;
			}
		}, delay-1);
    	timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				task.run();
			}
		}, delay);
    	
    	//stop the timer automatically when it is due
    	timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				stop();
			}
		}, delay + 10);
    	due = false;
    	running = true;
    }
	
    /**
     * Reset the timer to start from zero again.
     */
    public synchronized void reset(){
    	if(running)
    		start(task, delay);
    }
    
    /**
     * @return true if timer is running
     */
    public synchronized boolean isRunning(){
    	if(timer == null)
    		return false;
    	return running;
    }
    
    /**
     * @return true if timer is due
     */
    public synchronized boolean isDue(){
    	return due;
    }
    
    
    
	/**
	 * Stop the timer. Cancel the scheduled task.
	 */
	public synchronized void stop(){		
		if(timer != null){			
			timer.cancel();
			timer = null;
		}
		running =false;
	}
	
}
