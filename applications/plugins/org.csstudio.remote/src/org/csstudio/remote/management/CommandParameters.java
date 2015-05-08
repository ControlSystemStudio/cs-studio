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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Contains the actual parameters passed to a management command.
 *
 * @author Joerg Rathlev
 */
public final class CommandParameters implements Serializable {

    private static final long serialVersionUID = 2L;

    private final Map<String, Serializable> _values;

    /**
     * Creates a new <code>CommandParameters</code> object.
     */
    public CommandParameters() {
        _values = new HashMap<String, Serializable>();
    }

    /**
     * Sets a parameter value. The caller is responsible for ensuring that the
     * value is a legal value for the specified parameter.
     *
     * @param parameterId
     *            the identifier of the parameter.
     * @param value
     *            the parameter value.
     * @see CommandParameterDefinition#isLegalParameterValue(Object)
     */
    public void set(final String parameterId, final Serializable value) {
        if ((parameterId == null) || (value == null)) {
            throw new NullPointerException("parameterId and value must not be null");
        }

        _values.put(parameterId, value);
    }

    /**
     * Returns a parameter value.
     *
     * @param parameterId
     *            the identifier of the parameter.
     * @return the parameter value, or <code>null</code> if no value has been
     *         set for the specified parameter.
     */
    public Serializable get(final String parameterId) {
        if (parameterId == null) {
            throw new NullPointerException("parameterId must not be null");
        }

        return _values.get(parameterId);
    }

    /**
     * Returns the parameter identifiers of the parameters that are set.
     *
     * @return an unmodifiable set containing the parameter identifiers.
     */
    public Set<String> identifiers() {
        return Collections.unmodifiableSet(_values.keySet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (final Map.Entry<String, Serializable> entry : _values.entrySet()) {
            if (!first) {
                builder.append(", ");
            }
            builder.append(entry.getKey());
            builder.append("=");
            builder.append(entry.getValue().toString());
            first = false;
        }
        return builder.toString();
    }
}
