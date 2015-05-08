/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.remote.management;

import java.io.Serializable;

/**
 * Value of a command parameter of type {@link CommandParameterType#ENUMERATION}
 * or {@link CommandParameterType#DYNAMIC_ENUMERATION}.
 *
 * @author Joerg Rathlev
 */
public final class CommandParameterEnumValue implements Serializable {

    private static final long serialVersionUID = 2L;

    private final String _label;

    private final Serializable _value;

    /**
     * Creates a new enumeration value.
     *
     * @param value
     *            the value. Must not be null.
     * @param label
     *            the label to be displayed in the user interface for this
     *            value. Must not be null.
     */
    public CommandParameterEnumValue(final Serializable value, final String label) {
        if ((value == null) || (label == null)) {
            throw new NullPointerException("value and label must not be null");
        }

        _value = value;
        _label = label;
    }

    /**
     * Returns the value.
     *
     * @return the value.
     */
    public Serializable getValue() {
        return _value;
    }

    /**
     * Returns the label.
     *
     * @return the label.
     */
    public String getLabel() {
        return _label;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return _label;
    }
}
