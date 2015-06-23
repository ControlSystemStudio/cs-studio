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

package org.csstudio.dal.impl;

import org.csstudio.dal.DataExchangeException;
import org.csstudio.dal.NumericProperty;
import org.csstudio.dal.context.PropertyContext;


/**
 * Default NumericProperty implementation
 *
 */
public class NumericPropertyImpl<T,Ts> extends DynamicValuePropertyImpl<T>
    implements NumericProperty<T,Ts>
{
    /**
     * Creates a new NumericPropertyImpl object.
     *
     * @param valClass value datatype class
     * @param name property name
     * @param propertyContext property context
     */
    public NumericPropertyImpl(Class<T> valClass, String name,
        PropertyContext propertyContext)
    {
        super(valClass, name, propertyContext);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.NumericSimpleProperty#getFormat()
     */
    public String getFormat() throws DataExchangeException
    {
        return (String)directoryProxy.getCharacteristic(C_FORMAT);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.NumericSimpleProperty#getMaximum()
     */
    public Ts getMaximum() throws DataExchangeException
    {
        // Override this method is <Ts> in not same as <T>
        return (Ts)directoryProxy.getCharacteristic(C_MAXIMUM);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.NumericSimpleProperty#getMinimum()
     */
    public Ts getMinimum() throws DataExchangeException
    {
        // Override this method is <Ts> in not same as <T>
        return (Ts)directoryProxy.getCharacteristic(C_MINIMUM);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.NumericSimpleProperty#getUnits()
     */
    public String getUnits() throws DataExchangeException
    {
        return (String)directoryProxy.getCharacteristic(C_UNITS);
    }
}

/* __oOo__ */
