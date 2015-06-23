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

/**
 *
 */
package org.csstudio.dal.group;

import org.csstudio.dal.DynamicValueProperty;


/**
 * This interface provides group access to properties. If underalaying communication layer does
 * not support protocol optimization for group access, than this access must be implemented on client side in DAL implementaion.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 */
public interface GroupDataAccessProvider
{
    /**
     * Returns data access for all properties contained by this group access provider, which can be
     * casted to provided type and data access flavor.
     *
     * @param <T> exact data type
     * @param <P> exact property type
     * @param dataType exact data type
     * @param propertyType exact property type
     *
     * @return data access for all properties contained by this group access provider
     */
    public <T, P extends DynamicValueProperty<?>> GroupDataAccess<T, P> getGroupDataAccess(
        Class<T> dataType, Class<P> propertyType);

    /**
     * Returns data access for all properties contained by this group access provider, which can be
     * casted to provided type and data access flavor and match constrained.
     *
     * @param <T> exact data type
     * @param <P> exact property type
     * @param dataType exact data type
     * @param propertyType exact property type
     * @param constrain the constrains which filter returned properties in group access
     *
     * @return data access for all properties contained by this group access provider
     */
    public <T, P extends DynamicValueProperty<T>> GroupDataAccess<T, P> getGroupDataAccess(
        Class<T> dataType, Class<P> propertyType,
        PropertyGroupConstrain constrain);
}

/* __oOo__ */
