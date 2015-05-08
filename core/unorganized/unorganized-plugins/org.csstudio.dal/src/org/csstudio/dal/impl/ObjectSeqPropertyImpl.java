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
import org.csstudio.dal.ObjectSeqProperty;
import org.csstudio.dal.SequencePropertyCharacteristics;
import org.csstudio.dal.context.PropertyContext;


/**
 * Default implementation of ObjectSeqProperty
 *
  */
public class ObjectSeqPropertyImpl extends DynamicValuePropertyImpl<Object[]>
    implements ObjectSeqProperty
{
    private int sequenceLength;
    private boolean chInitialized;

    /**
     * Creates a new ObjectSeqPropertyImpl object.
     *
     * @param name property name
     * @param propertyContext property context
     */
    public ObjectSeqPropertyImpl(String name, PropertyContext propertyContext)
    {
        super(Object[].class, name, propertyContext);
    }

    private void readCharacteristics() throws DataExchangeException
    {
        if (chInitialized)
            return;

        Integer length = null;
        length = (Integer)getCharacteristic(SequencePropertyCharacteristics.C_SEQUENCE_LENGTH);
        sequenceLength = length.intValue();
        chInitialized = true;

    }

    /*
     *  (non-Javadoc)
     * @see org.csstudio.dal.SequenceAccess#getSequenceLength()
     */
    public int getSequenceLength() throws DataExchangeException
    {
        if (!chInitialized)
            readCharacteristics();
        return sequenceLength;
    }
}

/* __oOo__ */
