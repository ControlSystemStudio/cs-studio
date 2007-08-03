package org.csstudio.platform.statistic;

import org.csstudio.platform.logging.CentralLogger;

public class BackgroundCollectorThread extends Thread{
	
	private int	timeout	= 0;
	private boolean runForever	= true;
	static final double MB = 1024.0*1024.0;
	
	BackgroundCollectorThread (  final int timeout) {
		this.timeout = timeout;
		CentralLogger.getInstance().info(this, "BackgroundCollectorThread started");
		this.start();
	}
	
public final void run() {
	
	while (runForever) {
		
		BackgroundCollector.getInstance().getMemoryAvailableApplication().setValue( new Double(Runtime.getRuntime().freeMemory()/MB));
		BackgroundCollector.getInstance().getMemoryUsedApplication().setValue( new Double(Runtime.getRuntime().totalMemory()/MB));
		BackgroundCollector.getInstance().getMemoryUsedSystem().setValue( new Double(Runtime.getRuntime().maxMemory()/MB));
//		TODO: find out how to fill these!
//		before uncommenting: enable instanciating in BackgroundCollector!!
//		BackgroundCollector.getInstance().getCpuUsedApplication().setValue
//		BackgroundCollector.getInstance().getCpuUsedSystem().setValue
		
			try {
				Thread.sleep(this.timeout);

			} catch (InterruptedException e) {
				// TODO: handle exception
			} finally {
				//clean up
			}
		}		
		CentralLogger.getInstance().info(this, "BackgroundCollectorThread stopped");
		
	}

}
