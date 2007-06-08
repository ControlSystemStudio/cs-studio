package org.csstudio.platform.statistic;

import java.util.Vector;

public class CollectorSupervisor {
	
	private static CollectorSupervisor thisCollectorSupervisor= null;
	
	private Vector<Collector> collectorVector = null;
	
	public CollectorSupervisor () {
		
		collectorVector= new Vector<Collector>();
	}
	
	public static CollectorSupervisor getInstance() {
		//
		// get an instance of our sigleton
		//
		if ( thisCollectorSupervisor == null) {
			synchronized (CollectorSupervisor.class) {
				if (thisCollectorSupervisor == null) {
					thisCollectorSupervisor = new CollectorSupervisor();
				}
			}
		}
		return thisCollectorSupervisor;
	}
	
	public void printCollection () {
		/*
		 * print all actuall collections
		 */
		System.out.println("======== Collection Supervisor - Printout overview  ================");
		System.out.println("Vector-Size: " + collectorVector.size());
		String singleStatus = null;
		for ( int i = 0; i< collectorVector.size(); i++) {
			singleStatus = collectorVector.elementAt(i).getCollectorStatus();
			System.out.print(singleStatus);
		}
	}

	public Vector<Collector> getCollectorVector() {
		return collectorVector;
	}

	public void setCollectorVector(Vector<Collector> collectorVector) {
		this.collectorVector = collectorVector;
	}
	
	public void addCollector( Collector collector) {
		collectorVector.add( collector);
	}

}
