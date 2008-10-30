package org.csstudio.utility.ldapUpdater;

import java.util.Timer;
import java.util.TimerTask;

public class TimerProcessor {

	private static long delay;
	private static long interval;
// / 	private static volatile double broadcastDoubleValuePerSecond = 0L;
// /	private static boolean firstMap = true;
// /	private static ChannelCollector channel;

	TimerProcessor( long delay, long interval) {
		TimerProcessor.delay = delay;
		TimerProcessor.interval = interval;
		
		execute();
	}
	

    static class ProcessOnTime extends TimerTask {
        public void run() {
        	
        	LdapUpdater ldapUpdater=LdapUpdater.getInstance();
        	try {
        		if (!ldapUpdater.busy){
 				ldapUpdater.start();
 				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    public void execute() {
        Timer timer = new Timer();
        TimerTask processOnTime = new ProcessOnTime();
        timer.scheduleAtFixedRate(processOnTime, getDelay(), getInterval());
    }

    public static long getInterval() {
		return interval;
	}

    public static void setInterval(long interval) {
		TimerProcessor.interval = interval;
	}

    public static long getDelay() {
		return delay;
	}

    public static void setDelay(long delay) {
		TimerProcessor.delay = delay;
	}
} 