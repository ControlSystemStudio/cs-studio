package org.csstudio.rap.core;

import java.util.concurrent.CopyOnWriteArrayList;

public class ServerHeartBeatThread implements Runnable{	
	
	
	private CopyOnWriteArrayList<HeartBeatListener> listeners = 
			new CopyOnWriteArrayList<HeartBeatListener>();
	private long beatCount=0;
	private HeartBeatListener[] listenerArrays = new HeartBeatListener[]{};
	boolean changed;

	private Thread thread;
	private static ServerHeartBeatThread instance;
	
	private ServerHeartBeatThread(){
		thread = new Thread(this, "CSS RAP Heart Beat");
		thread.setDaemon(true);
		thread.start();
	}
	
	static synchronized ServerHeartBeatThread getInstance(){
		if(instance == null)
			instance = new ServerHeartBeatThread();
		return instance;
	}
	
	public void run() {
		while (true) {
			beatCount++;
			synchronized (this) {
				if(changed){
					listenerArrays = listeners.toArray(new HeartBeatListener[]{});
					changed = false;
				}
			}
			
			for(HeartBeatListener listener : listenerArrays){
				listener.beat(beatCount);
			}			
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {				
			}
			
		}
		
	}
	
	public synchronized void addHeartBeatListener(HeartBeatListener listener){
		listeners.add(listener);
		changed = true;
	}	


	public synchronized void removeHeartBeatListener(HeartBeatListener dataSourceListener) {
		listeners.remove(dataSourceListener);
		changed = true;
	}
	
	
}
