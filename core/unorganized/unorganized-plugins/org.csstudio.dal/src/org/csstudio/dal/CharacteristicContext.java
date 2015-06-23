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

package org.csstudio.dal;

import java.beans.PropertyChangeListener;

import java.util.Map;


/**
 * A <code>CharacteristicContext</code> represents a collection of characteristics.
 * Characteristics are name - value pairs that are considered static in time compared
 * to dynamic value changes. The changes in characteristic values are possible, but happen
 * mostly due to maintainance reasons (changes in values in static databases and so on).
 * The characteristics can be statically declared as JavaBeans properties or they can be
 * completely dynamic, accessible during run-time with their string name key.
 * These generic methods offer narrow access to all (dynamic and statically declared)
 * characteristics. All changes in characteristic values should be announced by
 * beans property change events. Sometimes new characteristics may become available
 * during the lifecycle of the characteristic context (while they may be unavailable from
 * the beginning). This applies to characteristic values as well as for the list of
 * characteristic names. An implementation can, however, guarantee to make available a list
 * of names and values at a certain well-defined time moment in the implementation life cycle.
 * This is a design decision of the implementation and should be well documented. An example
 * would be a context that binds to a remote server: here, characteristic values would be
 * available when the connection completes successfully.
 * A characteristic, however, may not become unavailable after it
 * has become available (i.e. after its name becomes known, it has to exist in the context
 * for the life time of the context; but of course its value may change to uninitialized
 * value).
 * <p>
 * Implementations of this interface may decide to buffer the characteristic values if
 * it is known in advance that the values will not change and the primary data source
 * is remote.
 * </p>
 * <p>
 * An existing characteristic with initialized value <b>must not</b> have a <code>null</code>
 * value.
 * </p>
 *
 * @author Gasper Tkacik
 */
public interface CharacteristicContext
{
    /** Default value when characteristic is not initialized. */
    public static final Object UNINITIALIZED = null;

    /**
     * Accesses the complete map of characteristics as name value pairs
     * for this context. This method returns a map of currently known
     * characteristics. If it is known that a given characteristic exists, it
     * has to be present in the map. If the underlying source is remote, this
     * method may optimize access to the characteristics, since underlying
     * implementations may support grouped data access requests. Primitive
     * types must be wrapped in <code>Object</code> wrappers.
     *
     * @param names array of characteristic names
     *
     * @return Map a map of characteristics for this context
     *
     * @exception DataExchangeException because this method communicates with
     *            the underlying data source, an exception may be thrown if
     *            the communication fails.
     */
    public Map<String,Object> getCharacteristics(String[] names) throws DataExchangeException;

    /**
     * Returns names of all characteristics for this context. Return
     * value will be an array of non-null characteristic names.
     *
     * @return an array of non-null characteristic names
     *
     * @throws DataExchangeException if operation fails
     */
    public String[] getCharacteristicNames() throws DataExchangeException;

    /**
     * Returns the value of the characteristic. If the characteristic
     * with such name does not exist this method returns <code>null</code>.
     *
     * @param name the name of the characteristic, may not be <code>null</code>
     *        or an empty string
     *
     * @return Object the value of the characteristic or <code>null</code> if
     *         unknown
     *
     * @exception DataExchangeException when the query for the characteristic
     *            value on the data source fails
     */
    public Object getCharacteristic(String name) throws DataExchangeException;

    /**
     * Adds a property change listener. The listeners will be notified
     * whenever the value  of the characteristic changes, or when a new
     * characteristic is added that was not present in the context before.
     *
     * @param l a listener object
     */
    public void addPropertyChangeListener(PropertyChangeListener l);

    /**
     * Removes a property change listener. The notifications about
     * characteristics will no  longer be sent.
     *
     * @param l a listener object
     *
     * @see #addPropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener l);

    /**
     * By contract with JavaBean specifications.
     *
     * @return array of registered listeners
     */
    public PropertyChangeListener[] getPropertyChangeListeners();
}

/* __oOo__ */
