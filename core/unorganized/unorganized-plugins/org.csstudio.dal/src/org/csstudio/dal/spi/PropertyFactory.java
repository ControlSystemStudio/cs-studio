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


import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.context.LinkListener;
import org.csstudio.dal.context.PropertyFamily;
import org.csstudio.dal.simple.RemoteInfo;


/**
 * <code>PropertyFactory</code> creates new property instances in various
 * flavors. Returned properties are linked or in process of linking to remote
 * resource.
 *
 * <p>
 * Requested properties may be created from new, or returned from internal
 * cache if factory supports it.
 * </p>
 *
 * <p>
 * Factory notifies listeners when factory has linked or released a property.
 * This comes very useful when factory is used for asynchronous property
 * creation and linking.
 * </p>
 *
 * @author <a href="mailto:igor.kriznar@cosylab.com">Igor Kriznar</a>
 *
 * @see abeans.models.Library
 * @see abeans.datatypes.TypelessProperty
 */
public interface PropertyFactory extends AbstractFactory
{
    /**
     * Return property with defined name. Linking of the property
     * depends on the linking policy of the factory. Name is first converted to
     * <code>RemoteInfo</code> in order to determine actual property creation
     * parameters from <code>DistributedDirectory</code> service.
     *
     * @param uniqueName remote unique name
     *
     * @return new or cached property
     *
     * @throws InstantiationException if instantiation fails
     * @throws RemoteException if connection fails
     */
    public DynamicValueProperty<?> getProperty(String uniqueName)
        throws InstantiationException, RemoteException;

    /**
     * Return property with defined <code>RemoteInfo</code>. Linking of the property
     * depends on the linking policy of the factory.
     *
     * @param ri remote info of requested property
     *
     * @return new or cached property
     *
     * @throws InstantiationException if instantiation fails
     * @throws RemoteException if connection fails
     */
    public DynamicValueProperty<?> getProperty(RemoteInfo ri)
        throws InstantiationException, RemoteException;

    /**
     * Return property with specified name and implementation class. Linking of the property
     * depends on the linking policy of the factory. Name is first converted
     * to <code>RemoteInfo</code> in order to determine actual property
     * creation parameters from <code>DistributedDirectory</code> service.
     *
     * @param uniqueName an unique remote name
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
    public <P extends DynamicValueProperty<?>> P getProperty(String uniqueName,
        Class<P> type, LinkListener<?> l)
        throws InstantiationException, RemoteException;

    /**
     * Return property with defined <code>RemoteInfo</code> and implementation
     * class. Linking of the property depends on the linking policy of the factory.
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
    public <P extends DynamicValueProperty<?>> P getProperty(RemoteInfo ri,
        Class<P> type, LinkListener<?> l)
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
        Class<? extends DynamicValueProperty<?>> type, LinkListener<?> l)
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
        Class<?extends DynamicValueProperty<?>> type, LinkListener<?> l)
        throws InstantiationException, RemoteException;

    /**
     * All properties created by this factory are automatically added to this family.
     * If factory is requested property, it first searches it inside this family,
     * before creating new one. Family binds lifecycle of propeties with lifecycle of
     * application context.
     * @return default family for all created properties
     */
    public PropertyFamily getPropertyFamily();
}

/* __oOo__ */
