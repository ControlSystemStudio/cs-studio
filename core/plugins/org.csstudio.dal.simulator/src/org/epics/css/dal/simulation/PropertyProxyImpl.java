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

/**
 *
 */
package org.epics.css.dal.simulation;

import java.beans.PropertyChangeEvent;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.csstudio.dal.DataExchangeException;
import org.csstudio.dal.DoubleProperty;
import org.csstudio.dal.DynamicValueAdapter;
import org.csstudio.dal.DynamicValueEvent;
import org.csstudio.dal.DynamicValueState;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.Request;
import org.csstudio.dal.ResponseListener;
import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.impl.DefaultApplicationContext;
import org.csstudio.dal.impl.RequestImpl;
import org.csstudio.dal.impl.ResponseImpl;
import org.csstudio.dal.proxy.AbstractPropertyProxyImpl;
import org.csstudio.dal.proxy.DirectoryProxy;
import org.csstudio.dal.proxy.MonitorProxy;
import org.csstudio.dal.proxy.PropertyProxy;
import org.csstudio.dal.proxy.SyncPropertyProxy;
import org.csstudio.dal.spi.LinkPolicy;
import org.epics.css.dal.simulation.data.DataGeneratorInfo;


/**
 * Simulations implementations of proxy.
 *
 * @author ikriznar
 *
 */
public class PropertyProxyImpl<T> extends AbstractPropertyProxyImpl<T,SimulatorPlug,MonitorProxyImpl<T>>
	implements PropertyProxy<T,SimulatorPlug>, SyncPropertyProxy<T,SimulatorPlug>, DirectoryProxy<SimulatorPlug>
{
	
	public static void main(String[] args) throws Exception, InstantiationException {
		PropertyFactoryImpl factory = new PropertyFactoryImpl();
		factory.initialize(new DefaultApplicationContext("test"), LinkPolicy.SYNC_LINK_POLICY);
		DoubleProperty dp = factory.getProperty("sim://abc COUNTDOWN:100:0:10000:200",DoubleProperty.class,null);
		dp.addDynamicValueListener(new DynamicValueAdapter<Double,DoubleProperty>(){
			@Override
			public void valueChanged(DynamicValueEvent event) {
				System.out.println(event.getValue());
			}
			
			@Override
			public void valueUpdated(DynamicValueEvent event) {
				System.out.println(event.getValue());
			}
		});
		
		Thread.sleep(20000);
	}
	
	protected ValueProvider<T> valueProvider = new MemoryValueProvider<T>();
	protected boolean isSettable = true;
	private long refreshRate = 1000;
	/**
	 * Creates new instance.
	 * @param name
	 */
	public PropertyProxyImpl(String name, SimulatorPlug plug, Class<T> type)
	{
		this(name, plug, (Long)SimulatorUtilities.getConfiguration(SimulatorUtilities.CONNECTION_DELAY),type);
	}
	
	/**
	 * Creates new instance.
	 * @param name
	 * @param connectDelay
	 */
	public PropertyProxyImpl(String name, SimulatorPlug plug, long connectDelay, Class<T> type)
	{
		super(name,plug);
		setConnectionState(ConnectionState.READY);
		DataGeneratorInfo info = DataGeneratorInfo.getInfo(name);
		refreshRate = info.getRefreshRate(name);
		setValueProvider(info.getDataGeneratorFactory().createGenerator(type, info.getOptions(name)));
		delayedConnect(connectDelay);
		updateConditionWith("", DynamicValueState.HAS_METADATA);
	}

	public void delayedConnect(long timeout)
	{
		setConnectionState(ConnectionState.CONNECTING);
		if (timeout > 0) {
			Timer t = new Timer();
			t.schedule(new TimerTask() {
					@Override
					public void run()
					{
						if (getConnectionState()==ConnectionState.CONNECTING) {
							setConnectionState(ConnectionState.CONNECTED);
						}
					}
				}, timeout);
		} else {
			setConnectionState(ConnectionState.CONNECTED);
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#getValueAsync(org.epics.css.dal.ResponseListener)
	 */
	public Request<T> getValueAsync(ResponseListener<T> callback)
		throws DataExchangeException
	{
		if (getConnectionState() != ConnectionState.CONNECTED) {
			throw new DataExchangeException(this, "Proxy not connected");
		}

		RequestImpl<T> r = new RequestImpl<T>(this, callback);
		r.addResponse(new ResponseImpl<T>(this, r, valueProvider.get(), "value",
		        true, null, getCondition(), null, true));

		return r;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#setValueAsync(T, org.epics.css.dal.ResponseListener)
	 */
	public Request<T> setValueAsync(T value, ResponseListener<T> callback)
		throws DataExchangeException
	{
		if (getConnectionState() != ConnectionState.CONNECTED) {
			throw new DataExchangeException(this, "Proxy not connected");
		}

		setValueSync(value);

		RequestImpl<T> r = new RequestImpl<T>(this, callback);
		r.addResponse(new ResponseImpl<T>(this, r, value, "", true, null,
		        getCondition(), null, true));

		return r;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#isSettable()
	 */
	public boolean isSettable()
	{
		return isSettable;
	}

	public void setSettable(boolean settable)
	{
		this.isSettable = settable;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#createMonitor(org.epics.css.dal.ResponseListener)
	 */
	public MonitorProxy createMonitor(ResponseListener<T> callback, Map<String,Object> param)
		throws RemoteException
	{
		MonitorProxyImpl<T> m = new MonitorProxyImpl<T>(this, callback);
		m.setTimerTrigger(refreshRate);
		return m;
	}

	public void destroy()
	{
		super.destroy();

		if (connectionStateMachine.isConnected()) {
			setConnectionState(ConnectionState.DISCONNECTING);
		}
		if (connectionStateMachine.getConnectionState()==ConnectionState.DISCONNECTING) {
			setConnectionState(ConnectionState.DISCONNECTED);
		}
		setConnectionState(ConnectionState.DESTROYED);
	}

	
	/**
	 * @see DirectoryProxy#getCharacteristicNames()
	 */
	public String[] getCharacteristicNames() throws DataExchangeException
	{
		return SimulatorUtilities.getCharacteristicNames(this);
	}

	@Override
	protected Object processCharacteristicBeforeCache(Object value,
			String characteristicName) {
		return SimulatorUtilities.getCharacteristic(characteristicName, this);
	}
	
	@Override
	protected Object processCharacteristicAfterCache(Object value,
			String characteristicName) {
		return value;
	}
	
	public T getValueSync() throws DataExchangeException
	{
		if (getConnectionState() != ConnectionState.CONNECTED) {
			throw new DataExchangeException(this, "Proxy not connected");
		}

		return valueProvider.get();
	}

	public void setValueSync(T value) throws DataExchangeException
	{
		if (getConnectionState() != ConnectionState.CONNECTED) {
			throw new DataExchangeException(this, "Proxy not connected");
		}

		valueProvider.set(value);

		@SuppressWarnings("unchecked")
		MonitorProxyImpl<T>[] m = getMonitors().toArray(new MonitorProxyImpl[getMonitors().size()]);

		for (int i = 0; i < m.length; i++) {
			m[i].fireValueChange();
		}
	}

	/**
	 * @return Returns the valueProvder.
	 */
	public ValueProvider<T> getValueProvider()
	{
		return valueProvider;
	}

	/**
	 * @param valueProvder The valueProvder to set.
	 */
	public void setValueProvider(ValueProvider<T> valueProvder)
	{
		try {
			valueProvder.set(this.valueProvider.get());
		} catch (Exception e) {
			// noop
		}

		this.valueProvider = valueProvder;
	}

	public void refresh()
	{
		// Override in order to clean up cached values.
	}

	/**
	 * Simulate changes of connection state
	 * @param state Connection State
	 */
	public void simulateConnectionState(ConnectionState state)
	{
		setConnectionState(state);
	}
	
	public void simulateCharacteristicChange(String characteristicName, Object value) {
		Object old= SimulatorUtilities.putCharacteristic(characteristicName, getUniqueName(), value);
		fireCharacteristicsChanged(new PropertyChangeEvent(this,characteristicName,old,value));
	}
	
	@Override
	protected String getRemoteHostInfo() {
		return "local";
	}
}

/* __oOo__ */
