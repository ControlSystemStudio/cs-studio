/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.platform.simpledal;

import java.util.List;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.model.pvs.ValueType;

/**
 * A service which can be used for easy access to control system channels.
 *
 * A control system channel is addressed via a {@link IProcessVariableAddress}.
 * Please use {@link ProcessVariableAdressFactory} to create these addresses.
 *
 * The service is designed to save system resources and share physical channels
 * whenever possible.
 *
 * The same physical connection (in case of EPICs, TINE, TANGO) is reused in the
 * following cases:
 * <li>for all connections to the same process variable with the same value
 * type</li>
 * <li>for all connections to characteristics of the same process variable</li>
 *
 * You may use the {@link ProcessVariableConnectionServiceFactory} to create
 * instances of this service.
 *
 * @author Sven Wende, Matthias Zeimer
 */
public interface IProcessVariableConnectionService {

    /**
     * Asynchronously writes the specified value to the channel addressed by the
     * the given process variable address.
     *
     * @param processVariableAddress
     *            the address
     * @param value
     *            the value
     * @param listener TODO
     *
     * @return true, if the value was set successful, false otherwise
     */
    void writeValueAsynchronously(IProcessVariableAddress processVariableAddress, Object value,
            ValueType expectedValueType, IProcessVariableWriteListener listener);

    /**
     * Synchronously writes the specified value to the channel addressed by the
     * the given process variable address.
     *
     * @param processVariableAddress
     *            the address
     *
     * @param value
     *            the value
     *
     * @return true, if the value was set successful, false otherwise
     */
    boolean writeValueSynchronously(IProcessVariableAddress processVariableAddress, Object value,
            ValueType expectedValueType) throws ConnectionException;

    /**
     * Asynchronously reads a value from the channel addressed by the the given
     * process variable address. When the value was received, the
     * {@link IProcessVariableValueListener#valueChanged(Object)} method will be
     * called on the given listener.
     *
     * @param processVariableAddress
     *            the process variable address
     * @param valueType
     *            the expected value type
     * @param listener
     *            the call-back listener
     */
    void readValueAsynchronously(IProcessVariableAddress processVariableAddress,
            ValueType valueType, IProcessVariableValueListener listener);

    /**
     * Synchronously reads a value from the channel addressed by the given
     * process variable address.
     *
     * @param processVariableAddress
     *            the process variable address
     *
     * @return the current value
     */
    <E> E readValueSynchronously(
            IProcessVariableAddress processVariableAddress, ValueType valueType)
            throws ConnectionException;

    /**
     * Starts listening permanently on the channel addressed by the specified
     * process variable address. The corresponding channel will be open as long
     * as any permanent listener is alive. For convenience this service
     * references the specified listener only weakly which means that it will be
     * garbage collected when its no longer referenced outside this service. If
     * you want to stop listening explicitly please use the
     * {@link #unregister(IProcessVariableValueListener)} method.
     *
     * To stop listening just
     *
     * Use the {@link #unregister(IProcessVariableValueListener)}
     *
     * @param listener
     *            the listener that will receive the value change notifications
     * @param pv
     *            the process variable address
     *
     * @param valueType
     *            the expected value type
     */
    @SuppressWarnings("unchecked")
    void register(IProcessVariableValueListener listener,
            IProcessVariableAddress pv, ValueType valueType);

    /**
     * Stops the specified permanent listener.
     *
     * @param listener
     *            the listener
     */
    @SuppressWarnings("unchecked")
    void unregister(IProcessVariableValueListener listener);

    /**
     * Checks whether the current user is allowed to write values to
     * the specified channel.
     *
     * @param pv
     *            the process variable address
     *
     * @return one of {@link SettableState#values()}
     */
    SettableState checkWriteAccessSynchronously(IProcessVariableAddress pv);

    /**
     * Returns all living connector.
     *
     * @return all living connectors
     */
    List<IConnector> getConnectors();

    int getNumberOfActiveConnectors();
}
