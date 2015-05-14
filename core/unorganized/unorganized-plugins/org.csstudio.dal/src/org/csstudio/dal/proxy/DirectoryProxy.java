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

import org.csstudio.dal.DataExchangeException;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.Request;
import org.csstudio.dal.ResponseListener;
import org.csstudio.dal.SimpleProperty;


/**
 * This interface encapsulate access to introspection part of remote property. Each instance is asociated with
 * particular unique remote name and all in and out names are relative to this name.
 *
 * @author Blaz Hostnik
 */
public interface DirectoryProxy<P extends AbstractPlug> extends Proxy<P>
{
    /**
     * Returns the unique name. This name is used to initiate
     * connection to remote object and can be regardes as remote name.
     *
     * @return String unique remote name for this property
     */
    public String getUniqueName();

    /**
     * Destroys object and releases all remote and local allocated resources.
     * <p><b>NOTE</b></br>
     * Only plug which created this proxy can call this method since lifecycle is controled by the plug.
     * </p>
     */
    public void destroy();

    /**
     * Returns names of all characteristics for this proxy. Return value will
     * be an array of non-null characteristic names.
     * @return an array of non-null characteristic names
     * @throws DataExchangeException if operation fails
     */
    public String[] getCharacteristicNames() throws DataExchangeException;

    /**
     * Returns available command names, if this proxy represents device.
     * @return all available command names
     * @throws DataExchangeException if remote request fails
     */
    public String[] getCommandNames() throws DataExchangeException;

    /**
     * Accesses asynchronously the complete map of characteristics for this proxy.
     * This asynchronous request is considered as multiple request where each characteristic
     * name can be treated as separate single request. As consequence, responses to this
     * request are returned independantly. For each charactetistic name one response is returned,
     * where characteristic name is defined by ID tag of response and response value is value of
     * characteristic. Last response is marked as last (isLast() method returns true).
     *
     * @param characteristics list of requested characteristics names.
     * @param callback a callback listener, which will receive all responses
     *
     * @return a Request, which identifies incoming responses.
     *
     * @throws DataExchangeException if operation failes
     */
    public Request<? extends Object> getCharacteristics(String[] characteristics,
        ResponseListener<? extends Object> callback) throws DataExchangeException;

    /**
     * Returns the value of the characteristic. If the characteristic with such name does not
     * exist this method returns <code>null</code>. If the characteristic exists but the
     * value is unknown, <code>CharacteristicContext.UNINITIALIZED</code> is returned.
     *
     * @param characteristicName the name of the characteristic, may not be <code>null</code> or an
     *            empty string
     * @return Object the value of the characteristic or <code>null</code> if unknown
     * @exception DataExchangeException when the query for the characteristic value on the
     *                 data source fails
     */
    public Object getCharacteristic(String characteristicName)
        throws DataExchangeException;

    /**
     * Returns names of properties if this proxy represents device proxy. Names are relative name,
     * which are valid only in context of this device proxy.
     *
     * @return names of properties in this device proxy
     * @throws RemoteException if remote request was issued and was not successfull
     */
    public String[] getPropertyNames() throws RemoteException;

    /**
     * Returns data access interface class (extended from SimpleProperty interface) of property in device.
     * Valid only if this directory proxy represents device proxy. Name is relative,
     * valid only in context of this device proxy.
     *
     * @param propertyName name of the property
     * @return data access inerface class
     * @throws RemoteException if remote request was issued and was not successfull
     */
    public Class<? extends SimpleProperty<?>> getPropertyType(String propertyName) throws RemoteException;

    public void refresh();
}

/* __oOo__ */
