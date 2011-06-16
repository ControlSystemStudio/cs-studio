package org.csstudio.utility.casnooper;

import java.util.Timer;
import java.util.TimerTask;
import org.csstudio.utility.casnooper.channel.*;

public class TimerProcessor {

	private static long delay;
	private static long interval;
	private static SnooperServer server = null;
	private static volatile double broadcastDoubleValuePerSecond = 0L;
	private static boolean firstMap = true;
	private static ChannelCollector channel;

	TimerProcessor( SnooperServer server, long delay, long interval) {
		TimerProcessor.delay = delay;
		TimerProcessor.interval = interval;
		TimerProcessor.server = server;
		
		execute();
	}
	

    static class ProcessOnTime extends TimerTask {
        public void run() {
            int broadcastIntValue = 0;
            broadcastDoubleValuePerSecond = 0.0;
            
//        	System.out.println("MyTimerTask was called.");
        	channel.setMapToUse(firstMap);
        	firstMap = !firstMap;
        	broadcastIntValue = server.getBroadcastCounterAndZero();
            server.getNumberOfBroadcastsChannel().setIntValue(broadcastIntValue);
            broadcastDoubleValuePerSecond = (double)broadcastIntValue * 1000.0 / (double)getInterval();
            server.getNumberOfBroadcastsPerSecondChannel().setDoubleValue(broadcastDoubleValuePerSecond);
//            System.out.println( "broadcasts/sec: " + broadcastDoubleValuePerSecond);
            SnooperServer.getCaBroadcastCollector().setValue(broadcastDoubleValuePerSecond);
        }
    }

    public void execute() {
        Timer timer = new Timer();
        channel = ChannelCollector.getInstance();
        server.setListnere(channel);
        TimerTask processOnTime = new ProcessOnTime();
        timer.scheduleAtFixedRate(processOnTime, getDelay(), getInterval());
//	        LOG.info(timer, "start TimerProcessor");
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

	public static double getBroadcastDoubleValuePerSecond() {
		return broadcastDoubleValuePerSecond;
	}

	public static void setBroadcastDoubleValuePerSecond(
			double broadcastDoubleValuePerSecond) {
		TimerProcessor.broadcastDoubleValuePerSecond = broadcastDoubleValuePerSecond;
	}
} 