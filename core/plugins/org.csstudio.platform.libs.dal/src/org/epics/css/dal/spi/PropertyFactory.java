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

package org.epics.css.dal.spi;

import org.epics.css.dal.RemoteException;
import org.epics.css.dal.SimpleProperty;
import org.epics.css.dal.context.AbstractApplicationContext;
import org.epics.css.dal.context.LinkListener;
import org.epics.css.dal.context.PlugContext;
import org.epics.css.dal.context.PropertyFamily;
import org.epics.css.dal.context.RemoteInfo;

import javax.naming.directory.DirContext;


/**
 * <code>PropertyFactory</code> creates new property instances in various
 * flavors. This interface rather defines convenience than actual new
 * functionality of Abeans. All creation is directed trhough
 * <code>Library</code> interface anyhow. Returned properties are linked or in
 * process of linking.
 *
 * <p>
 * Implementations of factory, which can be parents of new properties may
 * automatically incude new properties as their children and perform some
 * specific intialization (like synchronous aor asynchronous connect).
 * </p>
 *
 * <p>
 * Requested properties may be created from new, or returned from internal
 * cache if facctory supports it.
 * </p>
 *
 * <p>
 * Factory notifys listeners when factory has linked or released a property.
 * This comes very usefull when factory is used for asynchronous property
 * creation and linking.
 * </p>
 *
 * @author <a href="mailto:igor.kriznar@cosylab.com">Igor Kriznar</a>
 *
 * @see abeans.models.Library
 * @see abeans.datatypes.TypelessProperty
 */
public interface PropertyFactory
{
	/**
	 * Return property with defined name. This method does not link property if
	 * it is not linked yet. Name is first converted to
	 * <code>RemoteInfo</code> in order to determine actual property creation
	 * parameters from <code>DistributedDirectory</code> service.
	 *
	 * @param uniqueName any name, which can be trasformed to
	 *        <code>RemoteInfo</code> and refers to modelling element.
	 *
	 * @return new or cached property
	 *
	 * @throws InstantiationException if instantiation fails
	 * @throws RemoteException if connection fails
	 */
	public SimpleProperty getProperty(String uniqueName)
		throws InstantiationException, RemoteException;

	/**
	 * Return property with defined <code>RemoteInfo</code>. This method does
	 * not link property if it is not linked yet.
	 *
	 * @param ri remote info of requested property
	 *
	 * @return new or cached property
	 *
	 * @throws InstantiationException if instantiation fails
	 * @throws RemoteException if connection fails
	 */
	public SimpleProperty getProperty(RemoteInfo ri)
		throws InstantiationException, RemoteException;

	/**
	 * Return property with specified name and impementation class. This method
	 * does not link property if it is not linked yet. Name is first converted
	 * to <code>RemoteInfo</code> in order to determine actual property
	 * creation parameters from <code>DistributedDirectory</code> service.
	 *
	 * @param uniqueName any name, which can be trasformed to
	 *        <code>RemoteInfo</code> and refers to modelling element.
	 * @param type implementation type of returned property, if <code>null</code> then factory tries to
	 *        guess type which best match the remote property
	 * @param l link listener, which is registered at property after property
	 *        is created and receives link events, when property is linked
	 *
	 * @return new or cached property
	 *
	 * @throws InstantiationException if instantiation fails or property can
	 *         not be cast to provided implementation type
	 * @throws RemoteException if connection fails
	 */
	public <P extends SimpleProperty> P getProperty(String uniqueName,
	    Class<P> type, LinkListener l)
		throws InstantiationException, RemoteException;

	/**
	 * Return property with defined <code>RemoteInfo</code> and implementation
	 * class. This method does not link property if it is not linked yet.
	 *
	 * @param ri remote info of requested property
	 * @param type implementation type of returned property, if <code>null</code> then factory tries to
	 *        guess type which best match the remote property
	 * @param l link listener, which is registered at property after property
	 *        is created and receives link events, when property is linked
	 *
	 * @return new or cached property
	 *
	 * @throws InstantiationException if instantiation fails or property can
	 *         not be cast to provided implementation type
	 * @throws RemoteException if connection fails
	 */
	public <P extends SimpleProperty> P getProperty(RemoteInfo ri,
	    Class<P> type, LinkListener l)
		throws InstantiationException, RemoteException;

	/**
	 * Asynchronously starts property creation and linking. When property is
	 * created and linked an <code>LinkEstablished</code> event dispatched to
	 * link listeners registered at this property factory and to provided link
	 * listener.
	 *
	 * @param name <code>RemoteInfo</code> to which property is linked
	 * @param type implementation type of returned property, if <code>null</code> then factory tries to
	 *        guess type which best match the remote property
	 * @param l link listener, which is registered at property after property
	 *        is created and receives link events, when property is linked
	 *
	 * @return <code>RemoteInfo</code> which for which property is created. Can
	 *         be used to distinguish which link event from this factory is
	 *         associated with requested property
	 *
	 * @throws InstantiationException if creation fails
	 * @throws RemoteException if connection fails
	 */
	public RemoteInfo asyncLinkProperty(RemoteInfo name,
	    Class<?extends SimpleProperty> type, LinkListener l)
		throws InstantiationException, RemoteException;

	/**
	 * Asynchronously starts property creation and linking. When property is
	 * created and linked an <code>LinkEstablished</code> event dispatched to
	 * link listeners registered at this property factory and to provided link
	 * listener.
	 *
	 * @param name unique name which can be transformated to RI and property
	 *        linked to
	 * @param l link listener, which is registered at property after property
	 *        is created and receives link events, when property is linked
	 *
	 * @return <code>RemoteInfo</code> which for which property is created. Can
	 *         be used to distinguish which link event from this factory is
	 *         associated with requested property
	 *
	 * @throws InstantiationException if creation fails
	 * @throws RemoteException if connection fails
	 */
	public RemoteInfo asyncLinkProperty(String name,
	    Class<?extends SimpleProperty> type, LinkListener l)
		throws InstantiationException, RemoteException;

	/**
	 * Returns link policy code. This policy affects how
	 * <code>getProperty</code> methods are behaving when new property is
	 * created. It can be immediatelly synchronousli or asynchronously linked
	 * or left as it tis without linking.
	 *
	 * @return link policy code, valid values are <code>NO_LINK_POLICY</code>,
	 *         <code>SYNC_LINK_POLICY</code> and
	 *         <code>ASYNC_LINK_POLICY</code>.
	 */
	public LinkPolicy getLinkPolicy();

	/**
	 * All properties created by this factory are automatically added to this family.
	 * If factory is requested property, it first searches it inside this family,
	 * before creating new one. Family binds lifecycle of propeties with lifecycle of
	 * application context.
	 * @return default family for all created properties
	 */
	public PropertyFamily getPropertyFamily();

	public AbstractApplicationContext getApplicationContext();

	/**
	 * Must be called by DeviceFactoryService before factory is used. Can be called only once.
	 * @param policy
	 */
	public void initialize(AbstractApplicationContext ctx, LinkPolicy policy);

	/**
	 * Return plug type which is used for connection. If this factory serves as facade for multiple plugs,
	 * than default type must be returned. This is convenience method,type is obtained from plug.
	 *
	 * @return plug type which is used for connection
	 */
	public String getPlugType();

	/**
	 * Returns a default Directory Context. This is convenience method, directory is obtained from PlugContext.
	 *
	 * @return default directory from PlugContext
	 */
	public DirContext getDefaultDirectory();

	/**
	 * Return plug which is used for connection. If this factory serves as facade for multiple plugs,
	 * than default plug must be returned.
	 *
	 * @return plug which is used for connection
	 */
	public PlugContext getPlug();
}

/* __oOo__ */
