package de.desy.css.dal.tine;

import java.util.Arrays;
import java.util.EnumSet;

import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.DynamicValueState;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.Request;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.Timestamp;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.impl.RequestImpl;
import org.epics.css.dal.impl.ResponseImpl;
import org.epics.css.dal.proxy.MonitorProxy;

import de.desy.tine.client.TCallback;
import de.desy.tine.client.TLink;
import de.desy.tine.client.TLinkFactory;
import de.desy.tine.dataUtils.TDataType;
import de.desy.tine.definitions.TAccess;
import de.desy.tine.definitions.TMode;

/**
 * 
 * @author Jaka Bobnar, Cosylab
 *
 */
public class MonitorProxyImpl extends RequestImpl implements MonitorProxy, TCallback {

	public static final int DEFAULT_TIME_TRIGGER = 1000;
	private long timeTrigger = DEFAULT_TIME_TRIGGER;
	private boolean heartbeat = true;
	private boolean destroyed = false;
	private PropertyProxyImpl proxy;
	private TLink tLink;
	private Object oldData;
	private Object dataObject;
	private boolean normal=false;
	
	public MonitorProxyImpl(PropertyProxyImpl source, ResponseListener l) {
		super(source, l);
		this.proxy = source;
	}
	
	private synchronized boolean isLinkPresent(TLink[] links, TLink link) {
		for (TLink l: links) {
			if (link.equals(l)) return true;
		}
		return false;
	}
	
	public void initialize(final Object data) throws RemoteException {
		try {
			//monitor has to be initialized in a separate thread in order to allow enough
			//time for the old tLink to be cancelled. New link must not be established until
			//the old one has been removed from the TLinkFactory
			new Thread(new Runnable(){
				public void run() {
					MonitorProxyImpl.this.dataObject = data;
					TDataType dout = PropertyProxyUtilities.toTDataType(data,PropertyProxyUtilities.getObjectSize(data),true);
					TDataType din = new TDataType();
					short access = TAccess.CA_READ;
					
					if (tLink != null) {
			        	tLink.close();
			        	while (isLinkPresent(TLinkFactory.getInstance().getLinkTable(), tLink)) {
				        	synchronized (tLink) {
								try {
									tLink.wait(300);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
			        	}
  			        }
					
					tLink = new TLink(proxy.getDeviceName(),proxy.getDissector().getDeviceProperty(),dout,din,access);
        	        
			        short mode;
			        if (heartbeat) {
			        	mode = TMode.CM_POLL;
			        } else {
			        	mode = TMode.CM_REFRESH;
			        }
			        
			        int handle = 0;
		        	handle = tLink.attach(mode,MonitorProxyImpl.this,(int)timeTrigger);
		        		        	
		        	if (handle < 0) {
		        		DynamicValueCondition condition = new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR),System.currentTimeMillis(),"Error initializing proxy");
		    			proxy.setCondition(condition);
		        		throw new RuntimeException(tLink.getError(-handle));
		        	}
				}
			}).start();
			        	
//        	if (tLink != null) {
//	        	tLink.close();
//	        	isLinkPresent(TLinkFactory.getInstance().getLinkTable(), tLink);
//	        	Thread.sleep(3000);  	
//	        	isLinkPresent(TLinkFactory.getInstance().getLinkTable(), tLink);
//	        }
//			
//			this.dataObject = data;
//			TDataType dout = PropertyProxyUtilities.toTDataType(data,PropertyProxyUtilities.getObjectSize(data),true);
////			TDataType din = PropertyProxyUtilities.toTDataType(data,PropertyProxyUtilities.getObjectSize(data),true);
//			TDataType din = new TDataType();
//			short access = TAccess.CA_READ;
//	        	        
//	        tLink = new TLink(proxy.getDeviceName(),proxy.getDissector().getDeviceProperty(),dout,din,access);
//	        	        
//	        short mode;
//	        if (heartbeat) {
//	        	mode = PropertyProxyUtilities.makeAccessMode(AccessMode.POLL);
//	        } else {
//	        	mode = PropertyProxyUtilities.makeAccessMode(AccessMode.REFRESH);
//	        }
//	        
//	        int handle = 0;
//        	handle = tLink.attach(mode,this,(int)timeTrigger);
//        	
//        	
//        	if (handle < 0) {
//        		throw new ConnectionFailed(tLink.getError(-handle));
//        	}
        	
        } catch (Exception e) {
        	DynamicValueCondition condition = new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR),System.currentTimeMillis(),"Error initializing proxy");
			proxy.setCondition(condition);
			throw new RemoteException(proxy,"Cannot create monitor.",e);
        }
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.MonitorProxy#getRequest()
	 */
	public Request getRequest() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.MonitorProxy#refresh()
	 */
	public void refresh() {
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.SimpleMonitor#destroy()
	 */
	public synchronized void destroy() {
		if (tLink != null) {
			tLink.close();
			tLink=null;
		}
		destroyed = true;
		proxy.removeMonitor(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.SimpleMonitor#getDefaultTimerTrigger()
	 */
	public long getDefaultTimerTrigger() throws DataExchangeException {
		return DEFAULT_TIME_TRIGGER;
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.SimpleMonitor#getTimerTrigger()
	 */
	public long getTimerTrigger() throws DataExchangeException {
		return timeTrigger;
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.SimpleMonitor#isDefault()
	 */
	public boolean isDefault() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.SimpleMonitor#isDestroyed()
	 */
	public boolean isDestroyed() {
		return destroyed;
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.SimpleMonitor#isHeartbeat()
	 */
	public boolean isHeartbeat() {
		return heartbeat;
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.SimpleMonitor#setHeartbeat(boolean)
	 */
	public void setHeartbeat(boolean heartbeat) throws DataExchangeException, UnsupportedOperationException {
		if (this.heartbeat == heartbeat) return;
		this.heartbeat = heartbeat;
		try {
			initialize(dataObject);
		} catch (RemoteException e) {
			throw new DataExchangeException(getSource(), "Cannot set heartbeat", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.SimpleMonitor#setTimerTrigger(long)
	 */
	public void setTimerTrigger(long trigger) throws DataExchangeException, UnsupportedOperationException {
		if (this.timeTrigger == trigger) return;
		this.timeTrigger = trigger;		
		try {
			initialize(dataObject);
		} catch (RemoteException e) {
			throw new DataExchangeException(getSource(), "Cannot set timer trigger", e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.desy.tine.client.TCallback#callback(int, int)
	 */
	public void callback(int linkIndex, int linkStatus) {
		int statusCode = tLink.getLinkStatus();
		Exception e = null;
        if (statusCode > 0 || linkStatus > 0) {
        	proxy.setConnectionState(ConnectionState.CONNECTION_LOST);
        	e = new RemoteException(proxy,tLink.getLastError());
        	normal=false;
        } else {
        	proxy.setConnectionState(ConnectionState.CONNECTED);
        }
        
        TDataType dout = tLink.getOutputDataObject();
        Object data = proxy.extractData(dout);
        Timestamp timestamp = new Timestamp(tLink.getLastTimeStamp(),0);
        ResponseImpl response = new ResponseImpl(getSource(),this,data,"",true, e,null,timestamp,false);
        if (heartbeat) {
			addResponse(response);
			if (e == null && !normal) {
				proxy.setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.NORMAL),timestamp.getMilliseconds(),"Value updated."));
				normal=true;
			}
		} else {
			//only if value has changed!
			if (!(data instanceof Object[] && oldData instanceof Object[] && Arrays.equals((Object[])data, (Object[])oldData)) || (oldData==null || !data.equals(oldData))) {
				addResponse(response);
				if (e == null && !normal) {
					proxy.setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.NORMAL),timestamp.getMilliseconds(),"Value updated."));
					normal=true;
				}
			}
			oldData = data;
		}
	}
}
