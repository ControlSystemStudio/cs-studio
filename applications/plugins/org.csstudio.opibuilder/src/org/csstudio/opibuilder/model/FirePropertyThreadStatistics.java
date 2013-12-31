package org.csstudio.opibuilder.model;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FirePropertyThreadStatistics {

	private static Map<Thread, AtomicInteger> counters = new ConcurrentHashMap<Thread, AtomicInteger>();
	
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
	}
	
	public static void printStats() {
		for (Map.Entry<Thread, AtomicInteger> entry : counters.entrySet()) {
			Thread thread = entry.getKey();
			AtomicInteger integer = entry.getValue();
			System.out.println(integer + " - " + thread);
		}
	}

}
