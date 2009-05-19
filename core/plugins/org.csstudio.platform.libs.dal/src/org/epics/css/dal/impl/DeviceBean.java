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

package org.epics.css.dal.impl;

import com.cosylab.util.CommonException;

import org.epics.css.dal.context.AbstractApplicationContext;
import org.epics.css.dal.context.Connectable;
import org.epics.css.dal.context.ConnectionException;
import org.epics.css.dal.context.ConnectionListener;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.context.LinkBlocker;
import org.epics.css.dal.context.RemoteInfo;
import org.epics.css.dal.spi.DefaultDeviceFactoryService;
import org.epics.css.dal.spi.DeviceFactory;
import org.epics.css.dal.spi.Plugs;


/**
 * <code>DeviceBean</code> is implementation of <code>AbstractDevice</code>
 * whih implements <code>ContextBean</code> and <code>Connectable</code>
 * interface. This means that in contrast to  AbstractDevice this devic is not
 * only created by factory, but can be also created as JavaBean: with
 * contructor withouth parameters and initialized later.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public class DeviceBean extends AbstractDeviceImpl implements Connectable
{
	protected AbstractApplicationContext ctx;
	protected RemoteInfo rinfo;
	protected DeviceFactory deviceFactory;

	/**
	     * Creates new named instance of device bean.
	     */
	public DeviceBean()
	{
		super(null);
	}

	/**
	     * Creates new named instance of device bean.
	     * @param name device name
	     */
	public DeviceBean(String name)
	{
		super(name);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Connectable#addConnectionListener(org.epics.css.dal.context.ConnectionListener)
	 */
	public void addConnectionListener(ConnectionListener l)
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Connectable#asyncConnect()
	 */
	public synchronized void asyncConnect()
		throws ConnectionException, IllegalStateException
	{
		checkInitialized();

		if (connectionState != ConnectionState.DISCONNECTED
		    && connectionState != ConnectionState.READY) {
			throw new IllegalStateException("Connectable '" + getUniqueName()
			    + "' is not in DISCONNECTED or READY state but in "
			    + connectionState + ".");
		}

		setConnectionState(ConnectionState.CONNECTING);
		deviceFactory.reconnectDevice(this);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Connectable#connect()
	 */
	public void connect() throws ConnectionException, IllegalStateException
	{
		asyncConnect();
		LinkBlocker.blockUntillConnected(this,
		    Plugs.getConnectionTimeout(ctx.getConfiguration(),30000), true);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Connectable#destroy()
	 */
	public void destroy()
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Connectable#disconnect()
	 */
	public void disconnect()
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Connectable#getConnectionState()
	 */
	public ConnectionState getConnectionState()
	{
		return connectionState;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Connectable#getRemoteInfo()
	 */
	public RemoteInfo getRemoteInfo()
	{
		return rinfo;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Connectable#removeConnectionListener(org.epics.css.dal.context.ConnectionListener)
	 */
	public void removeConnectionListener(ConnectionListener l)
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Connectable#setRemoteInfo(org.epics.css.dal.context.RemoteInfo)
	 */
	public void setRemoteInfo(RemoteInfo rinfo) throws IllegalArgumentException
	{
		if (connectionState != ConnectionState.DISCONNECTED
		    && connectionState != ConnectionState.READY
		    && connectionState != ConnectionState.INITIAL) {
			throw new IllegalStateException("Connectable '" + getUniqueName()
			    + "' is not in DISCONNECTED, INITIAL or READY state but in "
			    + connectionState + ".");
		}

		if (rinfo == null) {
			this.rinfo = null;
			this.name = null;
			setConnectionState(ConnectionState.INITIAL);

			return;
		}

		this.rinfo = rinfo;
		this.name = rinfo.getRemoteName();
		setConnectionState(ConnectionState.READY);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.ContextBean#getApplicationContext()
	 */
	public AbstractApplicationContext getApplicationContext()
	{
		return ctx;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.ContextBean#initialize(org.epics.css.dal.context.AbstractApplicationContext)
	 */
	public void initialize(AbstractApplicationContext ctx)
		throws CommonException
	{
		// TODO: at some point this initialization must be changed. 
		// device factory should be obtained only after RemoteInfo is set and correct plug type is known, 
		// now it is used default plug tipe from ctx
		deviceFactory = DefaultDeviceFactoryService.getDeviceFactoryService()
			.getDeviceFactory(ctx, null);

		if (deviceFactory == null) {
			throw new CommonException(this, "Failed to create device factory");
		}

		this.ctx = ctx;
	}

	/**
	 * Checks if device is properly initialized.
	 *
	 * @throws IllegalStateException if it is not initialized
	 *
	 * @see #initialize(AbstractApplicationContext)
	 */
	protected void checkInitialized() throws IllegalStateException
	{
		if (deviceFactory == null) {
			throw new IllegalStateException("Device '" + getUniqueName()
			    + "' is not initialized.");
		}
	}

	/**
	 * Sets connection state and fires event.
	 *
	 * @param s new state
	 */
	protected void setConnectionState(ConnectionState s)
	{
		super.setConnectionState(s);

		// TODO: should fire event here to connection listeners
	}
}

/* __oOo__ */
