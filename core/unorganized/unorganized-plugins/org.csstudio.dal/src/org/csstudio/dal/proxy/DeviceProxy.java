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

package org.csstudio.dal.proxy;

import org.csstudio.dal.RemoteException;


/**
 * Proxy for remote device object.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 */
public interface DeviceProxy<P extends AbstractPlug> extends Proxy<P>
{
	/**
	 * Retus command if exists, othervise <code>null</code>.
	 *
	 * @param name command name
	 *
	 * @return command or <code>null</code>
	 *
	 * @throws RemoteException if operation fails
	 */
	public CommandProxy getCommand(String name) throws RemoteException;

	/**
	 * Returns <code>PropertyProxy</code> element whose partial name
	 * equals the specified name. Name of property is calculated relative to
	 * the device proxy,  that is unique name of property is combination of
	 * unique name of device plus partial  name of the property. If property
	 * with specified name is not present <code>null</code> is returned.
	 *
	 * WARNING:
	 *
	 * If LinkPolicy for the device is <code>LinkPolicy.ASYNC_LINK_POLICY</code> all properties that
	 * are created before the device gets connected must be be connected too before connection status
	 * of the device becomes <code>ConnectionStatus.CONNECTED.</code>
	 *
	 * If LinkPolicy for the device is <code>LinkPolicy.SYNC_LINK_POLICY</code> then property creation should block
	 * untill the property gets connected and all the properties that are returned by the device
	 * must have <code>ConnectionStatus.CONNECTED</code>.
	 * @param name unique name of typless property
	 *
	 * @return property proxy
	 *
	 * @throws RemoteException if remote operation fails
	 */
	public PropertyProxy<?,P> getPropertyProxy(String name)
		throws RemoteException;

	/**
	 * Returns <code>DirectoryProxy</code> element whose partial name
	 * equals the specified name. Name of property is calculated relative to
	 * the device proxy,  that is unique name of property is combination of
	 * unique name of device plus partial  name of the property. If property
	 * with specified name is not present <code>null</code> is returned.
	 *
	 * @param name unique name of typless property
	 *
	 * @return directory proxy
	 *
	 * @throws RemoteException if remote operation fails
	 */
	public DirectoryProxy<P> getDirectoryProxy(String name)
		throws RemoteException;

	public void refresh();

}

/* __oOo__ */
