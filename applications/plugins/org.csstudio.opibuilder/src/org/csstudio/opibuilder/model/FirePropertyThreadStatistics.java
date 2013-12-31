package org.csstudio.opibuilder.model;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FirePropertyThreadStatistics {
	
	public static class RateMeasure {
		private int counter;
		private long startTime = System.currentTimeMillis();
		private final String name;

		public RateMeasure(String name) {
			this.name = name;
		}
		
		public void track() {
			counter++;
			long diffTime = System.currentTimeMillis() - startTime;
			if (diffTime > 1000) {
				double nSec = diffTime / 1000.0;
				double rate = counter / nSec;
				System.out.println(name + " rate " + rate + "Hz");
				counter = 0;
				startTime = System.currentTimeMillis();
			}
		}
	}

	private static Map<Thread, AtomicInteger> counters = new ConcurrentHashMap<Thread, AtomicInteger>();
	private static boolean stackTraceForNonSwtNotifications = false;
	
	public static void addFireEvent(String propertyName) {
		Thread currentThread = Thread.currentThread();
		AtomicInteger counter = counters.get(currentThread);
		if (counter == null) {
			// This is not thread safe, can miss a couple of counts at the beginning
			// We don't care
			counter = new AtomicInteger();
			counters.put(currentThread, counter);
		}
		counter.incrementAndGet();
		if (counter.get() % 100 == 0) {
			printStats();
		}
		if (stackTraceForNonSwtNotifications) {
			if (!currentThread.getName().equals("main")) {
				System.out.println(currentThread.getName());
				new RuntimeException("Notification on different thread").printStackTrace();
			}
		}
	}
	
	public static void printStats() {
		for (Map.Entry<Thread, AtomicInteger> entry : counters.entrySet()) {
			Thread thread = entry.getKey();
			AtomicInteger integer = entry.getValue();
			System.out.println(integer + " - " + thread);
		}
	}

}
