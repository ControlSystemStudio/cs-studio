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

package org.csstudio.dal.group;

import org.csstudio.dal.DataExchangeException;
import org.csstudio.dal.DynamicValueProperty;

import java.util.concurrent.locks.Condition;


/**
 * The GroupsAccess enables access of data in group of properties of same type
 * in packed operations.
 * <p>
 * Group access is in the same time property collections, since group data type
 * is tied to the type of contained properties.
 * </p>
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public interface GroupDataAccess<T, P extends DynamicValueProperty<?>>
    extends PropertyCollection<P>, GroupUpdateable
{
    /**
     * Adds a dynamic value listener. In response, the data access will
     * immediately dispatch an event with the current dynamic value (even if
     * it is <code>UNINITIALIZED</code>). Afterwards, if the implementation
     * allows, the event delivery may be controlled by the listener.
     *
     * @param l the listener object
     */
    public void addDynamicValuesListener(DynamicValuesListener<T> l);

    /**
     * Removes a dynamic value listener. No further notifications will
     * be sent. The implementation may release the resources connected to the
     * subscription if this is necessary.
     *
     * @param l the listener object
     */
    public void removeDynamicValuesGroupListener(DynamicValuesListener<T> l);

    /**
     * Returns a list of all dynamic value listeners.
     *
     * @return DynamicValueListener[] a list of all active listeners
     */
    public DynamicValuesListener<T>[] getDynamicValuesListeners();

    /**
     * Returns the type of the dynamic value accessible through this
     * data access.
     *
     * @return dynamic value type
     */
    public Class<T> getDataType();

    /**
     * Returns whether the values can be set. If false, all methods
     * that perform set may be ignored. It returns false if at least one
     * property in group is not settable;
     *
     * @return True if value can be set.
     */
    boolean isSettable();

    /**
     * Sets the value in the data source. This method executes
     * synchronously, i.e. after its completion, the value must be set in the
     * primary data source.
     *
     * @param value the new value to set
     *
     * @exception DataExchangeException if the set operation fails for
     *            whichever reason
     */
    public void setValues(T value) throws DataExchangeException;

    /**
     * Returns array of values from the group of data sources. This
     * method executes synchronously by its definition. Data is souce are in
     * same order as properties in corelated group
     *
     * @return the values in the data source
     *
     * @exception DataExchangeException if the get operation fails for
     *            whichever reason
     */
    public T getValues() throws DataExchangeException;

    /**
     * Returns the latest known value in the data source, obtained
     * implicitly by whatever means. The latest received value is updated
     * whenever a get is requested or when a new subscription notification is
     * available for this data access. Methods in <code>Updateable</code>
     * interface further define this value (by its latest timestamp and
     * response).
     *
     * @return double the latest known dynamic value
     */
    public T getLatestReceivedValues();

    /**
     * Returns property at given index.
     *
     * @param index the index of property
     *
     * @return property at given index
     */
    public P getProperty(int index);

    /**
     * Returns property name at given index.
     *
     * @param index the index of property
     *
     * @return property name at given index
     */
    public String getPropertyName(int index);

    /**
     * Returns properties which are in timeout. This is convenience
     * method.
     *
     * @return properties which are in timeout
     */
    public DynamicValueProperty<T>[] getTimeoutProperties();

    /**
     * Returns properties which are in timelag. This is convenience
     * method.
     *
     * @return properties which are in timelag
     */
    public DynamicValueProperty<T>[] getTimelagProperties();

    /**
     * Returns remote data conditions for properties in group.
     *
     * @return remote data conditions for properties in group
     */
    public Condition[] getConditions();
} /* __oOo__ */


/* __oOo__ */
