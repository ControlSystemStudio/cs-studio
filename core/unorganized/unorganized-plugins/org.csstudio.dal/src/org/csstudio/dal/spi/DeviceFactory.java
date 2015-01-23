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

package org.csstudio.dal.spi;

import org.csstudio.dal.RemoteException;
import org.csstudio.dal.context.ConnectionException;
import org.csstudio.dal.context.DeviceFamily;
import org.csstudio.dal.context.LinkListener;
import org.csstudio.dal.context.Linkable;
import org.csstudio.dal.device.AbstractDevice;
import org.csstudio.dal.simple.RemoteInfo;


/**
 * <code>DeviceFactory</code> creates new device instances in various
 * flavors. This interface rather defines convenience than actual new
 * functionality of Abeans. All creation is directed trhough
 * <code>Library</code> interface anyhow. Returned properties are linked or in
 * process of linking.
 *
 * <p>
 * Implementations of factory, which can be parents of new devices may
 * automatically incude new devices as their children and perform some
 * specific intialization (like synchronous aor asynchronous connect).
 * </p>
 *
 * <p>
 * Requested devices may be created from new, or returned from internal
 * cache if facctory supports it.
 * </p>
 *
 * <p>
 * Factory notifys listeners when factory has linked or released a device.
 * This comes very usefull when factory is used for asynchronous device
 * creation and linking.
 * </p>
 *
 * @author <a href="mailto:igor.kriznar@cosylab.com">Igor Kriznar</a>
 *
 */
public interface DeviceFactory extends AbstractFactory
{
	/**
	 * Return device with defined name. This method does not link device if
	 * it is not linked yet. Name is first converted to
	 * <code>RemoteInfo</code> in order to determine actual device creation
	 * parameters from <code>DistributedDirectory</code> service.
	 *
	 * @param uniqueName any name, which can be transformed to
	 *        <code>RemoteInfo</code> and refers to modelling element.
	 *
	 * @return new or cached device
	 *
	 * @throws InstantiationException if instantiation fails
	 * @throws RemoteException if connection fails
	 */
	public AbstractDevice getDevice(String uniqueName)
		throws InstantiationException, RemoteException;

	/**
	 * Return device with defined <code>RemoteInfo</code>. This method does
	 * not link device if it is not linked yet.
	 *
	 * @param ri remote info of requested device
	 *
	 * @return new or cached device
	 *
	 * @throws InstantiationException if instantiation fails
	 * @throws RemoteException if connection fails
	 */
	public AbstractDevice getDevice(RemoteInfo ri)
		throws InstantiationException, RemoteException;

	/**
	 * Return device with specified name and impementation class. This method
	 * does not link device if it is not linked yet. Name is first converted
	 * to <code>RemoteInfo</code> in order to determine actual device
	 * creation parameters from <code>DistributedDirectory</code> service.
	 *
	 * @param uniqueName any name, which can be transformed to
	 *        <code>RemoteInfo</code> and refers to modelling element.
	 * @param type implementation type of returned device, if <code>null</code> then factory tries to
	 *        guess type which best match the remote device
	 *
	 * @return new or cached device
	 *
	 * @throws InstantiationException if instantiation fails or device can
	 *         not be cast to provided implementation type
	 */
	public <D extends AbstractDevice> D getDevice(String uniqueName,
	    Class<D> type, LinkListener<? extends Linkable> l)
		throws InstantiationException, RemoteException;

	/**
	 * Return device with defined <code>RemoteInfo</code> and implementation
	 * class. This method does not link device if it is not linked yet.
	 *
	 * @param ri remote info of requested device
	 * @param type implementation type of returned device, if <code>null</code> then factory tries to
	 *        guess type which best match the remote device
	 *
	 * @return new or cached device
	 *
	 * @throws InstantiationException if instantiation fails or device can
	 *         not be cast to provided implementation type
	 * @throws RemoteException if connection fails
	 */
	public <D extends AbstractDevice> D getDevice(RemoteInfo ri, Class<D> type,
	    LinkListener<? extends Linkable> l) throws InstantiationException, RemoteException;

	/**
	 * Asynchronously starts device creation and linking. When device is
	 * created and linked an <code>LinkEstablished</code> event dispatched to
	 * link listeners registered at this device factory and to provided link
	 * listener.
	 *
	 * @param name <code>RemoteInfo</code> to which device is linked
	 * @param type implementation type of returned device, if <code>null</code> then factory tries to
	 *        guess type which best match the remote device
	 * @param l link listener, which is registered at device after device
	 *        is created and receives link events, when device is linked
	 *
	 * @return <code>RemoteInfo</code> which for which device is created. Can
	 *         be used to distinguish which link event from this factory is
	 *         associated with requested device
	 *
	 * @throws InstantiationException if creation fails
	 * @throws PlugException if connection fails
	 */
	public RemoteInfo asyncLinkDevice(RemoteInfo name,
	    Class<? extends AbstractDevice> type, LinkListener<? extends Linkable> l)
		throws InstantiationException, RemoteException;

	/**
	 * Asynchronously starts device creation and linking. When device is
	 * created and linked an <code>LinkEstablished</code> event dispatched to
	 * link listeners registered at this device factory and to provided link
	 * listener.
	 *
	 * @param name unique name which can be transformated to RI and device
	 *        linked to
	 * @param type implementation type of returned device, if <code>null</code> then factory tries to
	 *        guess type which best match the remote device
	 * @param l link listener, which is registered at device after device
	 *        is created and receives link events, when device is linked
	 *
	 * @return <code>RemoteInfo</code> which for which device is created. Can
	 *         be used to distinguish which link event from this factory is
	 *         associated with requested device
	 *
	 * @throws InstantiationException if creation fails
	 * @throws PlugException if connection fails
	 */
	public RemoteInfo asyncLinkDevice(String name,
	    Class<? extends AbstractDevice> type, LinkListener<? extends Linkable> l)
		throws InstantiationException, RemoteException;

	/**
	 * All devices created by this factory are automatically added to this family.
	 * If factory is requested devices, it first searches it inside this family,
	 * before creating new one. Family binds lifecycle of devices with lifecycle of
	 * application context.
	 * @return default family for all created devices
	 */
	public DeviceFamily getDeviceFamily();

	/**
	 * Factory accepts AbstractDevice and
	 * connects this device to new <code>DeviceProxy</code> and <code>DirectoryProxy</code>.
	 * This operation impose following desing contracts contstrains for implementation:
	 *
	 * <ul>
	 * <li>Desing intention is that this method is called from devices, which implement <code>Connectable</code>
	 * and <code>ContextBean</code> interface.</li>
	 * <li>Device MUST implement <code>Connectable</code> interface, this means that device is
	 * capable of controlling it's own connection and life cycle.</li>
	 * <li>Device is preared for such reinitialization, it must be in <code>ConnectionState.READY</code>.</li>
	 * <li>Method returns immediatelly, even if new proxy was not in connected state.</li>
	 * <li>This factory implementation reckognizes device implementation in order to perform reconnect.
	 * This means, that factory can not handle just any device but only from supported implementation. This
	 * also mean, that device implementation must know which factory to use. Practically this meant that
	 * <code>DeviceBean</code> from common glue implementation can work only with plugs which are
	 * implemented following common plug design guidelines.</li>
	 * </ul>
	 *
	 * @param device device to be reconnected
	 * @throws RemoteException if reconnect fails
	 */
	public void reconnectDevice(AbstractDevice device)
		throws ConnectionException;
}

/* __oOo__ */
