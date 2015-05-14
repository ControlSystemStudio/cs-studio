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


/**
 * <code>DataAccess</code> is an interface that encapsulates the access to
 * dynamic value. As such, it supports the addition and removal of listeners
 * through which interested parties are informed about the change of value or
 * status of the dynamic value. This interface serves as a base interface for
 * typed data access interfaces, for example <code>DoubleAccess</code> etc.
 * Each typed data access interface provides one data access mode or rendering
 * of dynamic value into a specific Java type. Note that this interface alone
 * does not fully specify its dynamic value, because it lacks methods for
 * accessing the unique name of the dynamic value. Data access should,
 * therefore, be considered as a transient data access object without its own
 * lifecylce, which is created and managed by some other object (considering
 * that there may be many data accesses for a single dynamic value, this is a
 * natural approach). As an example, the relation of data access to the creator
 * of data access is similar to relation of a hashmap and its entry set, key set
 * and value set: all sets are views of a hashmap and their lifecycle is bound
 * to the hashmap lifecycle.
 *  *
 * <p>
 * <code>DataAccess</code> specializations for different data types <b>should
 * be limited to a small number</b>. This is of primary importance. If the data
 * accesses proliferate, all data handling in the data flow after the data
 * access must be duplicated for the type as well. It is suggested that <b>only</b>
 * one floating point type, one integer type, one string type, one bit-pattern
 * type and one object type are supported, along with (optional) paired and
 * array types.
 * </p>
 */
public interface DataAccess<T>
{
    /**
     * Adds a dynamic value listener. In response, the data access will
     * immediately dispatch an event with the current dynamic value (even if
     * it is <code>UNINITIALIZED</code>). Afterwards, if the implementation
     * allows, the event delivery may be controlled by the listener.
     *
     * @param l the listener object
     */
    public <P extends SimpleProperty<T>> void addDynamicValueListener(DynamicValueListener<T, P> l);

    /**
     * Removes a dynamic value listener. No further notifications will
     * be sent. The implementation may release the resources connected to the
     * subscription if this is necessary.
     *
     * @param l the listener object
     */
    public <P extends SimpleProperty<T>> void removeDynamicValueListener(DynamicValueListener<T, P> l);

    /**
     * Returns a list of all dynamic value listeners.
     *
     * @return DynamicValueListener[] a list of all active listeners
     */
    public DynamicValueListener<T, ? extends SimpleProperty<T>>[] getDynamicValueListeners();

    /**
     * Returns <code>true</code> if has any registered dynamic value listener.
     * This is optimization method, faster then calling up array of listeners an checking if 0.
     *
     * @return <code>true</code> if has any registered dynamic value listener
     */
    public boolean hasDynamicValueListeners();

    /**
     * Returns the type of the dynamic value accessible through this
     * data access.
     *
     * @return dynamic value type
     */
    public Class<T> getDataType();

    /**
     * Returns whether the value can be set. If false, all methods that
     * perform set may be ignored.
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
    public void setValue(T value) throws DataExchangeException;

    /**
     * Returns the value in the data source. This method executes
     * synchronously by its definition.
     *
     * @return double the value in the data source
     *
     * @exception DataExchangeException if the get operation fails for
     *            whichever reason
     */
    public T getValue() throws DataExchangeException;

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
    public T getLatestReceivedValue();
} /* __oOo__ */


/* __oOo__ */
