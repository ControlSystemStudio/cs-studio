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
package org.csstudio.sds.internal.model.logic;

import java.util.HashMap;

import org.csstudio.sds.internal.rules.ParameterDescriptor;

/**
 * RuleState information for the <code>RuleEngine</code>.
 *
 * @author Alexander Will & Sven Wende
 * @version $Revision: 1.4 $
 *
 */
final class RuleState {
    /**
     * References to all input channels.
     */
    private ParameterDescriptor[] _parameters;

    /**
     * Contains the latest values for the input channels.
     */
    private HashMap<ParameterDescriptor, Object> _cachedValues;

    /**
     * Standard constructor.
     *
     * @param parameters
     *            Parameter descriptions for the inialisation of this state
     *            object.
     */
    public RuleState(final ParameterDescriptor[] parameters) {
        _parameters = parameters;

        // prepare cache
        _cachedValues = new HashMap<ParameterDescriptor, Object>();

        for(ParameterDescriptor p : parameters) {
            _cachedValues.put(p, p.getValue());
        }
    }

    /**
     * Cache the latest value for a given channel name in this state.
     *
     * @param parameter
     *            A channel name.
     * @param value
     *            The latest value from the given channel.
     */
    public synchronized void cacheParameterValue(final ParameterDescriptor parameter,
            final Object value) {
        if (_cachedValues.containsKey(parameter)) {
            _cachedValues.put(parameter, value);
        }
    }

    /**
     * Return the most recent parameter values of this state.
     *
     * @return The most recent parameter values of this state.
     */
    public Object[] getRecentParameterValues() {
        Object[] result = new Object[_parameters.length];
        for (int i = 0; i < _parameters.length; i++) {
            result[i] = _cachedValues.get(_parameters[i]);
        }

        return result;
    }
}
