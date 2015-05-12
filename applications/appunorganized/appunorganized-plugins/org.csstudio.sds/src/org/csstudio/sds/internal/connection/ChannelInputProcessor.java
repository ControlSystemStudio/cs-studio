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
 package org.csstudio.sds.internal.connection;

import java.util.Map;

import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.sds.internal.model.logic.RuleEngine;
import org.csstudio.sds.internal.rules.ParameterDescriptor;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.dal.DynamicValueState;

/**
 * A channel input process encapsulates the logical rules that have to be
 * applied, when a dynamic value change occurs.
 *
 * Dynamic value changes are delegated to these processor. Afterwards the
 * processor processes its rules to transform the received value and then it
 * forwards the transformed value to a widget property.
 *
 * @author Sven Wende
 *
 */
public final class ChannelInputProcessor {

    /**
     * The channel, which is managed by this processor.
     */
    private ParameterDescriptor _parameter;

    /**
     * A rule engine.
     */
    private RuleEngine _ruleEngine;

    /**
     * The managed widget property.
     */
    private WidgetProperty _widgetProperty;

    /**
     * Property values, which are applied for certain connection states.
     */
    private Map<ConnectionState, Object> _connectionStatePropertyValues;

    /**
     * Property values, which are applied for certain condition states.
     */
    private Map<DynamicValueState, Object> _conditionStatePropertyValues;

    /**
     * Constructor.
     *
     * @param parameter
     *            a channel reference
     * @param ruleEngine
     *            a rule engine
     * @param widgetProperty
     *            a widget property
     * @param connectionStatePropertyValues
     *            property values, which are applied for certain connection
     *            states
     * @param conditionStatePropertyValues
     *            Property values, which are applied for certain condition
     *            states
     */
    public ChannelInputProcessor(
            final ParameterDescriptor parameter,
            final RuleEngine ruleEngine,
            final WidgetProperty widgetProperty,
            final Map<ConnectionState, Object> connectionStatePropertyValues,
            final Map<DynamicValueState, Object> conditionStatePropertyValues) {
        assert parameter != null;
        assert ruleEngine != null;
        assert widgetProperty != null;
        _parameter = parameter;
        _ruleEngine = ruleEngine;
        _widgetProperty = widgetProperty;
        _connectionStatePropertyValues = connectionStatePropertyValues;
        _conditionStatePropertyValues = conditionStatePropertyValues;
    }

    /**
     * This method is called by connectors, when a
     * dynamic value has changed.
     *
     * @param newValue
     *            the new value
     */
    public void valueChanged(final Object newValue) {
        // wenn als Value null zurückgegeben wird, dann bedeutet dies mitunter,  dass kein Wert gesetzt werden darf
        Object value = _ruleEngine.processRule(_parameter, newValue);
        if (value != null) {
            applyValueToProperty(value);
        }
    }

    @Deprecated
    public void connectionStateChanged(final ConnectionState state) {
        if (_connectionStatePropertyValues != null
                && _connectionStatePropertyValues.containsKey(state)) {
            applyValueToProperty(_connectionStatePropertyValues.get(state));
        }
    }

    /**
     * This method is called by connectors when the
     * connection state changes.
     *
     * @param state
     *            the current connection state
     */
    public void connectionStateChanged(org.csstudio.dal.context.ConnectionState state) {
        if (_connectionStatePropertyValues != null
                && _connectionStatePropertyValues.containsKey(state)) {
            applyValueToProperty(_connectionStatePropertyValues.get(state));
        }
    }

    /**
     * This method is called by connectors when the
     * condition state changes.
     *
     * @param state
     *            the current condition state
     *
     * // FIXME: {@link org.csstudio.platform.simpledal.ConnectionState} verwenden!
     */
    public void conditionStateChanged(final DynamicValueState state) {
        if (_conditionStatePropertyValues != null
                && _conditionStatePropertyValues.containsKey(state)) {
            applyValueToProperty(_conditionStatePropertyValues.get(state));
        }
    }

    /**
     * Applies the specified value to the widget property.
     *
     * @param value
     *            the new value
     */
    private void applyValueToProperty(final Object value) {
        _widgetProperty.setPropertyValue(value);
    }

}
