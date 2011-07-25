package org.csstudio.rap.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.swt.widgets.Display;

public class DisplayManager {

	private static Map<Display, DisplayResource> displayMap = new HashMap<Display, DisplayResource>();

	private static DisplayManager instance;

	private long beatCount;
	
	private static long uiCallbackID = 0;

	private DisplayManager() {
		RAPCorePlugin.getDefault().getServerHeartBeatThread()
				.addHeartBeatListener(new HeartBeatListener() {

					@SuppressWarnings("unchecked")
					@Override
					public void beat(long beatCount) {
						DisplayManager.this.beatCount = beatCount;
						if (beatCount % 500 == 0)
							RAPCorePlugin.getLogger().log(Level.INFO, "DisplayManager: Number of clients: "
									+ displayMap.size());
						for (Entry<Display, DisplayResource> entry : displayMap
								.entrySet().toArray(new Entry[0])) {
							final Display display = entry.getKey();
							//if client's heart beat count has not been updated for 100 cycles, mark it as dead.
							if (beatCount - entry.getValue().heartCount > 100)
								displayMap.get(display).isLive = false;
							try {
								if(display.isDisposed() || !isDisplayAlive(display)){
									RAPCorePlugin.getLogger().log(Level.INFO, 
											"DisplayManager: " + display + " is Disposed!");
									for(Runnable runnable : entry.getValue().getDisposeListeners()){
										runnable.run();
									}
									unRegisterDisplay(display);
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
		displayMap.put(display, new DisplayResource(beatCount, true));
		display.asyncExec(new Runnable() {
			
			@Override
			public void run() {
				String callbackID = generateNewUICallbackID();
				UICallBack.deactivate(callbackID);
				UICallBack.activate(callbackID);				
			}
		});
		RAPCorePlugin.getLogger().log(Level.INFO, 
				"DisplayManager: " + display + " is registered.");
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
		checkIfDisplayRegistered(display);
		displayMap.get(display).addDisposeListener(runnable);			
	}
	
	/**Remove a display dispose listener.
	 * @param display
	 * @param runnable
	 * @throws Exception
	 */
	public void removeDisplayDisposeListener(Display display, Runnable runnable) throws Exception{
		checkIfDisplayRegistered(display);
		displayMap.get(display).removeDisposeListener(runnable);
	}

	private void checkIfDisplayRegistered(Display display) throws Exception {
		if(!displayMap.containsKey(display))
			throw new Exception("The display has not been registered yet!");
	}

	private void unRegisterDisplay(final Display display) {
		displayMap.remove(display);
	}

	private void markDisplayAlive(Display display) throws Exception {
		checkIfDisplayRegistered(display);
		displayMap.get(display).heartCount = beatCount;
	}

	public boolean isDisplayAlive(Display display) throws Exception {
		checkIfDisplayRegistered(display);	
		return displayMap.get(display).isLive;
	}

	class DisplayResource {
		private long heartCount;
		private Boolean isLive;
		private List<Runnable> disposeListenerList;
		public DisplayResource(long beatCount, Boolean isLive) {
			super();
			this.heartCount = beatCount;
			this.isLive = isLive;
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
