package org.csstudio.rap.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.swt.widgets.Display;

public class DisplayManager {

	private static Map<Display, DisplayResource> displayMap = new HashMap<Display, DisplayResource>();

	private static DisplayManager instance;
	
	private static List<Object> objectList =new ArrayList<Object>();

	private long beatCount;
	
	private static long uiCallbackID = 0;

	private DisplayManager() {
		RAPCorePlugin.getDefault().getServerHeartBeatThread()
				.addHeartBeatListener(new HeartBeatListener() {

					@SuppressWarnings("unchecked")
					@Override
					public void beat(long beatCount) {
						DisplayManager.this.beatCount = beatCount;						
						for (Entry<Display, DisplayResource> entry : displayMap
								.entrySet().toArray(new Entry[0])) {
							final Display display = entry.getKey();
							//if client's heart beat count has not been updated for 100 cycles, mark it as dead.
							if (beatCount - entry.getValue().heartCount > 100)
								displayMap.get(display).isLive = false;
							try {
								if(display.isDisposed() || !isDisplayAlive(display)){
									for(Runnable runnable : entry.getValue().getDisposeListeners()){
										runnable.run();
									}
									unRegisterDisplay(display);
									RAPCorePlugin.getLogger().log(Level.INFO, 
											"DisplayManager: " + display + " on " + entry.getValue().remoteHost +
											" is Disposed! Number of display: " + displayMap.size() +
											" Number of widgets: " + objectList.size());
								}else
									display.asyncExec(new Runnable() {
										
										@Override
										public void run() {
											try {
												markDisplayAlive(display);
											} catch (Exception e) {												
											}
										}
									});
							} catch (Exception e) {
								RAPCorePlugin.getLogger().log(Level.SEVERE,"", e);
							}
						}

					}
				});
	}

	public static synchronized DisplayManager getInstance() {
		if (instance == null)
			instance = new DisplayManager();
		return instance;
	}

	/**
	 * Register the display so it can be managed by RAP Core. It must be called
	 * after shell was created. The manager will automatically unregister the display
	 * if it is not alive.
	 * 
	 * @param display
	 */
	public void registerDisplay(Display display) {
		if(displayMap.containsKey(display))
			return;
		HttpServletRequest request = RWT.getRequest();
		displayMap.put(display, new DisplayResource(beatCount, true, 
				request.getRemoteHost() + " : " + request.getHeader("User-Agent"))); //$NON-NLS-1$ //$NON-NLS-2$
		
		display.asyncExec(new Runnable() {
			
			@Override
			public void run() {
				String callbackID = generateNewUICallbackID();
				UICallBack.deactivate(callbackID);
				UICallBack.activate(callbackID);				
			}
		});		
		StringBuilder sb = new StringBuilder("DisplayManger: "); //$NON-NLS-1$
		sb.append(display + " on " + request.getRemoteHost());
		sb.append(" is registered. Number of display: ");
		sb.append(displayMap.size());
		for(Entry<Display, DisplayResource> entry : displayMap.entrySet()){
			sb.append("\n");
			sb.append("Client: ");
			sb.append(entry.getValue().remoteHost);
		}
		RAPCorePlugin.getLogger().log(Level.INFO, sb.toString());
		
	}
	
	private String generateNewUICallbackID(){
		uiCallbackID++;
		return "CSS_RAP_" + uiCallbackID; //$NON-NLS-1$
	}
	
	/**Add a listener which will be executed after the display is disposed.
	 *
	 * @param display the display of the RAP client which is calling this method. 
	 * The display should be registered before by calling {@link #registerDisplay(Display)}.
	 * @param runnable the listener. It is executed in server heart beat thread, so no
	 * UI code is allowed.
	 * @throws Exception if the display has not been registered.
	 */
	public void addDisplayDisposeListener(Display display, Runnable runnable) throws Exception{
		if(checkIfDisplayRegistered(display))
			displayMap.get(display).addDisposeListener(runnable);			
	}
	
	/**Remove a display dispose listener.
	 * @param display
	 * @param runnable
	 * @throws Exception
	 */
	public void removeDisplayDisposeListener(Display display, Runnable runnable) throws Exception{
		if(checkIfDisplayRegistered(display))
			displayMap.get(display).removeDisposeListener(runnable);
	}

	private boolean checkIfDisplayRegistered(Display display) throws Exception {
		if(!displayMap.containsKey(display))
			return false;
		return true;
//			throw new Exception("The display has not been registered yet!");
	}

	private void unRegisterDisplay(final Display display) {
		displayMap.remove(display);
	}

	private void markDisplayAlive(Display display) throws Exception {
		if(checkIfDisplayRegistered(display))
			displayMap.get(display).heartCount = beatCount;
	}

	public boolean isDisplayAlive(Display display) throws Exception {
		if(checkIfDisplayRegistered(display))
			return displayMap.get(display).isLive;
		return false;
	}

	public synchronized void registerObject(Object obj){
		objectList.add(obj);
	}
	
	public synchronized void unRegisterObject(Object obj){
		objectList.remove(obj);
	}
	
	class DisplayResource {
		private long heartCount;
		private Boolean isLive;
		private List<Runnable> disposeListenerList;
		private String remoteHost;
		public DisplayResource(long beatCount, Boolean isLive, String remoteHost) {
			super();
			this.heartCount = beatCount;
			this.isLive = isLive;
			this.remoteHost = remoteHost;
		}
		
		public void addDisposeListener(Runnable disposeListener){
			if(disposeListenerList == null)
				disposeListenerList = new LinkedList<Runnable>();
			disposeListenerList.add(disposeListener);
		}
		
		public void removeDisposeListener(Runnable disposeListener){
			if(disposeListenerList == null)
				return;
			disposeListenerList.remove(disposeListener);
		}
		
		public Runnable[] getDisposeListeners(){
			if(disposeListenerList == null){
				return new Runnable[0];
			}
			return disposeListenerList.toArray(
					new Runnable[disposeListenerList.size()]);
		}

	}

}
