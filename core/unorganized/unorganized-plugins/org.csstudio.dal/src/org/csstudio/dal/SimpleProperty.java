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

import org.csstudio.dal.context.Identifiable;
import org.csstudio.dal.simple.AnyDataChannel;


/**
 * <code>SimpleProperty</code> is context for dynamic data channel with
 * characteristics and data state qualification. Supports only simple data
 * monitoring and manipulating methods and accessors. It does not provide asynchronous access.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 */
public interface SimpleProperty<T> extends DataAccess<T>, CharacteristicContext,
    PropertyCharacteristics, ValueUpdateable<T>, Identifiable, AnyDataChannel
{
    /**
     * Returns the Dynamic Value Unique Name. The name syntax is not
     * specified by Datatypes; the value is used as unique identifier. This
     * name is used to initiate  connection to remote object and can be
     * regardes as remote name.
     *
     * @return String DVUN for this property
     */
    @Override
    public String getUniqueName();

    /**
     * Returns name of this property within some wider property
     * context. If this property is in flat hirarchy and does not have
     * immediate parent object, which reflects remote connection, then this
     * name is same as unique name. If this property is eg. child propety of
     * device, than this  name is name of the property contianed inside the
     * device. In this case unique name may contain  device name and property
     * name and this name is unique name stripped of device name: unique name
     * is  "PS1:current" and name is "current";
     *
     * @return String DVUN for this property
     */
    public String getName();

    /**
     * Returns a list of available renderings (or data access modes)
     * for this property. Each rendering provides an additional view of
     * dynamic value by providing access to it in a different Java type
     * instance. Moreover - although this is not recommended in general - the
     * dynamic value returned can depend in some cases on the type of the data
     * access selected. The returned array of <code>Class</code> instances
     * represents possible data access types, i.e. interfaces that extend the
     * <code>DataAccess</code> interface.
     *
     * @return Class[] available data access modes for this property
     */
    public Class<?extends DataAccess<?>>[] getAccessTypes();

    /**
     * Returns an instance of data access for this property, given a
     * data access type. If the type is the same as the primary type of the
     * property, the property will return itself. Otherwise a new access will
     * be instantiated and returned. Multiple calls for the same data access
     * type will return the same instance. The returned data access will
     * satisfy the following condition:
     * <code>type.isAssignableFrom(property.getDataAccess().getClass()) ==
     * true</code>, i.e. the returned instance will be instance of the type
     * parameter.
     *
     * @param <D> requested type of returned adata access instance
     * @param type the type of data access to create
     *
     * @return DataAccess the data access that renders dynamic value of this
     *         property into a given Java type.
     *
     * @exception IllegalViewException if the requested data access mode is not
     *            available for this property
     */
    public <D extends DataAccess<?>> D getDataAccess(Class<D> type)
        throws IllegalViewException;

    /**
     * Returns default instance of data access for this property:
     * itself.
     *
     * @return DataAccess the dafault data access that renders dynamic value of
     *         this property
     */
    public DataAccess<T> getDefaultDataAccess();

    /**
     * <p>Returns a string rather long description of this property.
     * This is a statically declared characteristic of this property. The
     * description will probably be provided by the underlying data source.</p>
     *  <p>GUI component might use this string for tooltip text.</p>
     *
     * @return String description of this property
     *
     * @exception DataExchangeException if the query to the data source fails
     */
    public String getDescription() throws DataExchangeException;

    /**
     * Returns current condition of dynamic data source. This is mix of
     * states, which describes quality of dynamic value.
     *
     * @return quality of data
     */
    public DynamicValueCondition getCondition();

    /**
     * If returned <code>true</code> then property is currently in
     * timeout.
     *
     * @return flasg if property in timeput
     */
    public boolean isTimeout();

    /**
     * If returned <code>true</code> then property is currently in
     * timelag.
     *
     * @return flasg if property in timelag
     */
    public boolean isTimelag();
}

/* __oOo__ */
