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
 package org.csstudio.sds.ui.internal.properties.view;

/**
 * Extension to the standard <code>IPropertySource</code> interface.
 * <p>
 * This interface provides extended API to <code>IPropertySource</code> to
 * allow an easier indication of properties that have a default value and can be
 * resetted.
 * </p>
 *
 * @since 3.0
 * @see org.csstudio.sds.ui.internal.properties.view.IPropertySource
 *
 * @author Sven Wende
 */
public interface IPropertySource2 extends IPropertySource {

    /**
     * Returns whether the value of the property with the specified id is
     * resettable to a default value.
     *
     * @param id
     *            the id of the property
     * @return <code>true</code> if the property with the specified id has a
     *         meaningful default value to which it can be resetted, and
     *         <code>false</code> otherwise
     * @see IPropertySource#resetPropertyValue(Object)
     * @see IPropertySource#isPropertySet(Object)
     */
    boolean isPropertyResettable(Object id);

    /**
     * <code>IPropertySource2</code> overrides the specification of this
     * <code>IPropertySource</code> method to return <code>true</code>
     * instead of <code>false</code> if the specified property does not have a
     * meaningful default value. <code>isPropertyResettable</code> will only
     * be called if <code>isPropertySet</code> returns <code>true</code>.
     * <p>
     * Returns whether the value of the property with the given id has changed
     * from its default value. Returns <code>false</code> if this source does
     * not have the specified property.
     * </p>
     * <p>
     * If the notion of default value is not meaningful for the specified
     * property then <code>true</code> is returned.
     * </p>
     *
     * @param id
     *            the id of the property
     * @return <code>true</code> if the value of the specified property has
     *         changed from its original default value, <code>true</code> if
     *         the specified property does not have a meaningful default value,
     *         and <code>false</code> if this source does not have the
     *         specified property
     * @see IPropertySource2#isPropertyResettable(Object)
     * @see #resetPropertyValue(Object)
     * @since 3.1
     */
    boolean isPropertySet(Object id);
}
