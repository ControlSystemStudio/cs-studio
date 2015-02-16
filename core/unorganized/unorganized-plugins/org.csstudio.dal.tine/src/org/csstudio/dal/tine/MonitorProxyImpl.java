/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.dal.tine;

import java.util.Arrays;
import java.util.EnumSet;

import org.csstudio.dal.DataExchangeException;
import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.DynamicValueState;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.Request;
import org.csstudio.dal.ResponseListener;
import org.csstudio.dal.Timestamp;
import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.impl.RequestImpl;
import org.csstudio.dal.impl.ResponseImpl;
import org.csstudio.dal.proxy.MonitorProxy;

import de.desy.tine.client.TCallback;
import de.desy.tine.client.TLink;
import de.desy.tine.dataUtils.TDataType;
import de.desy.tine.definitions.TAccess;
import de.desy.tine.definitions.TMode;

/**
 * 
 * @author Jaka Bobnar, Cosylab
 *
 */
public class MonitorProxyImpl<T> extends RequestImpl<T> implements MonitorProxy, TCallback {

	public static final int DEFAULT_TIME_TRIGGER = 1000;
	private long timeTrigger = MonitorProxyImpl.DEFAULT_TIME_TRIGGER;
	private boolean heartbeat = true;
	private boolean destroyed = false;
	private PropertyProxyImpl<T> proxy;
	private TLink tLink;
	private T oldData;
	private Object dataObject;
	private boolean normal=false;
	
	public MonitorProxyImpl(PropertyProxyImpl<T> source, ResponseListener<T> l) {
		super(source, l);
		this.proxy = source;
		proxy.addMonitor(this);
	}
		
	public void initialize(final Object data) throws RemoteException {
		try {	        	
        	if (this.tLink != null) {
	        	this.tLink.close();
	        }
			
			this.dataObject = data;
			TDataType dout = PropertyProxyUtilities.toTDataType(dataObject,PropertyProxyUtilities.getObjectSize(data),true);
			TDataType din = new TDataType();
			short access = TAccess.CA_READ;
	        	        
	        this.tLink = new TLink(this.proxy.getDeviceName(),this.proxy.getDissector().getDeviceProperty(),dout,din,access);
	        	        
	        short mode;
	        if (this.heartbeat) {
	        	mode = TMode.CM_POLL;
	        } else {
	        	mode = TMode.CM_REFRESH;
	        }
	        
	        int handle = 0;
        	handle = this.tLink.attach(mode,this,(int)this.timeTrigger);
        	
        	
        	if (handle < 0) {
        		throw new ConnectionFailed(this.tLink.getError(-handle));
        	}
        	
        } catch (Exception e) {
        	DynamicValueCondition condition = new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR),null,"Error initializing proxy");
			this.proxy.setCondition(condition);
			throw new RemoteException(this.proxy,"Cannot create monitor.",e);
        }
	}

	/*
	 * (non-Javadoc)
	 * @see org.csstudio.dal.proxy.MonitorProxy#getRequest()
	 */
	public Request getRequest() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.csstudio.dal.proxy.MonitorProxy#refresh()
	 */
	public void refresh() {
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.csstudio.dal.SimpleMonitor#destroy()
	 */
	public synchronized void destroy() {
		if (this.tLink != null) {
			this.tLink.close();
			this.tLink=null;
		}
		this.destroyed = true;
		this.proxy.removeMonitor(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.csstudio.dal.SimpleMonitor#getDefaultTimerTrigger()
	 */
	public long getDefaultTimerTrigger() throws DataExchangeException {
		return MonitorProxyImpl.DEFAULT_TIME_TRIGGER;
	}

	/*
	 * (non-Javadoc)
	 * @see org.csstudio.dal.SimpleMonitor#getTimerTrigger()
	 */
	public long getTimerTrigger() throws DataExchangeException {
		return this.timeTrigger;
	}

	/*
	 * (non-Javadoc)
	 * @see org.csstudio.dal.SimpleMonitor#isDefault()
	 */
	public boolean isDefault() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.csstudio.dal.SimpleMonitor#isDestroyed()
	 */
	public boolean isDestroyed() {
		return this.destroyed;
	}

	/*
	 * (non-Javadoc)
	 * @see org.csstudio.dal.SimpleMonitor#isHeartbeat()
	 */
	public boolean isHeartbeat() {
		return this.heartbeat;
	}

	/*
	 * (non-Javadoc)
	 * @see org.csstudio.dal.SimpleMonitor#setHeartbeat(boolean)
	 */
	public void setHeartbeat(boolean heartbeat) throws DataExchangeException, UnsupportedOperationException {
		if (this.heartbeat == heartbeat) {
			return;
		}
		this.heartbeat = heartbeat;
		try {
			initialize(this.dataObject);
		} catch (RemoteException e) {
			throw new DataExchangeException(getSource(), "Cannot set heartbeat", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.csstudio.dal.SimpleMonitor#setTimerTrigger(long)
	 */
	public void setTimerTrigger(long trigger) throws DataExchangeException, UnsupportedOperationException {
		if (this.timeTrigger == trigger) {
			return;
		}
		this.timeTrigger = trigger;		
		try {
			initialize(this.dataObject);
		} catch (RemoteException e) {
			throw new DataExchangeException(getSource(), "Cannot set timer trigger", e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.desy.tine.client.TCallback#callback(int, int)
	 */
	public void callback(int linkIndex, int linkStatus) {
		int statusCode = this.tLink.getLinkStatus();
		Exception e = null;
        if (statusCode > 0 || linkStatus > 0) {
        	e = new RemoteException(this.proxy,this.tLink.getLastError());
        	this.normal=false;
        	this.proxy.setConnectionState(ConnectionState.CONNECTION_LOST,e);
        } else {
        	this.proxy.setConnectionState(ConnectionState.CONNECTED);
        }
        
        TDataType dout = this.tLink.getOutputDataObject();
        T data = this.proxy.extractData(dout);
        Timestamp timestamp = new Timestamp(this.tLink.getLastTimeStamp(),0);
        ResponseImpl<T> response = new ResponseImpl<T>(getSource(),this,data,"",true, e,null,timestamp,false);

        this.proxy.updateValueReponse(response);
        
        if (this.heartbeat) {
			addResponse(response);
			if (e == null && !this.normal) {
				this.proxy.setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.NORMAL),timestamp,"Value updated."));
				this.normal=true;
			}
		} else {
			//only if value has changed!
			if (!(data instanceof Object[] && this.oldData instanceof Object[] && Arrays.equals((Object[])data, (Object[])this.oldData)) || (this.oldData==null || !data.equals(this.oldData))) {
				addResponse(response);
				if (e == null && !this.normal) {
					this.proxy.setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.NORMAL),timestamp,"Value updated."));
					this.normal=true;
				}
			}
			this.oldData = data;
		}
	}
}
