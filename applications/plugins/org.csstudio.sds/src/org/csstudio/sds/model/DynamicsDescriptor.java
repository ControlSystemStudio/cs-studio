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
package org.csstudio.sds.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.sds.internal.rules.DirectConnectionRule;
import org.csstudio.sds.internal.rules.ParameterDescriptor;
import org.csstudio.dal.DynamicValueState;

/**
 * Descriptor for the dynamic behavior of <code>ElementProperties</code>.
 *
 * @author Alexander Will, Stefan Hofer, Sven Wende
 * @version $Revision: 1.20 $
 *
 */
public final class DynamicsDescriptor implements Cloneable {
    /**
     * The ID of the associated rule.
     */
    private String _ruleId;

    /**
     * The registered input channels.
     */
    private List<ParameterDescriptor> _inputChannels;

    /**
     * The output channel. Might be null.
     */
    private ParameterDescriptor _outputChannel;

    /**
     * Standard constructor.
     */
    public DynamicsDescriptor() {
        this(DirectConnectionRule.TYPE_ID);
    }

    /**
     * Standard constructor.
     *
     * @param ruleId
     *            The ID of the associated rule.
     */
    public DynamicsDescriptor(final String ruleId) {
        assert ruleId != null;
        _inputChannels = new ArrayList<ParameterDescriptor>();
        _ruleId = ruleId;
        _outputChannel = null;
    }

    /**
     * Return the output channel.
     *
     * @return The output channel.
     */
    public ParameterDescriptor getOutputChannel() {
        return _outputChannel;
    }

    /**
     * Set the output channel.
     *
     * @param outputChannel
     *            The output channel
     */
    public void setOutputChannel(final ParameterDescriptor outputChannel) {
        _outputChannel = outputChannel;
    }

    /**
     * Add an input channel.
     *
     * @param inputChannel
     *            An input channel.
     */
    public void addInputChannel(final ParameterDescriptor inputChannel) {
        assert inputChannel != null;
        _inputChannels.add(inputChannel);
    }

    /**
     * Removes the specified input channel.
     *
     * @param inputChannel
     *            The input channel to remove.
     */
    public void removeInputChannel(final ParameterDescriptor inputChannel) {
        assert inputChannel != null;
        if (hasInputChannel(inputChannel)) {
            _inputChannels.remove(inputChannel);
        }
    }

    /**
     * Check whether the given input channel does already belong to this
     * dynamics descriptor.
     *
     * @param inputChannel
     *            An input channel.
     * @return True, if the given input channel does already belong to this
     *         dynamics descriptor.
     */
    public boolean hasInputChannel(final ParameterDescriptor inputChannel) {
        assert inputChannel != null;
        return _inputChannels.contains(inputChannel);
    }

    /**
     * Return the input channels.
     *
     * @return The input channels.
     */
    public ParameterDescriptor[] getInputChannels() {
        return _inputChannels.toArray(new ParameterDescriptor[_inputChannels
                .size()]);
    }

    /**
     * Return the ID of the associated rule.
     *
     * @return The ID of the associated rule.
     */
    public String getRuleId() {
        return _ruleId;
    }

    /**
     * Contains the property values, which should be applied for certain
     * connection states.
     */
    private Map<ConnectionState, Object> _connectionStateDependentPropertyValues;

    /**
     * Contains the property values, which should be applied for certain
     * condition states.
     */
    private Map<DynamicValueState, Object> _conditionStateDependentPropertyValues;

    /**
     * Indicates whether this {@link DynamicsDescriptor} should only use the
     * connection states.
     */
    private boolean _useOnlyConnectionStates;

    /**
     * Returns the property values, which should be applied for certain
     * connection states.
     *
     * @return the property values, which should be applied for certain
     *         connection states
     */
    public Map<ConnectionState, Object> getConnectionStateDependentPropertyValues() {
        return _connectionStateDependentPropertyValues;
    }

    /**
     * Sets the property values, which should be applied for certain connection
     * states.
     *
     * @param values
     *            the property values, which should be applied for certain
     *            connection states
     */
    public void setConnectionStateDependentPropertyValues(
            final Map<ConnectionState, Object> values) {
        assert values != null;
        for (ConnectionState state : values.keySet()) {
            assert state != null : "state != null";
            assert values.get(state) != null : "values.get("+ state +") != null";
        }

        _connectionStateDependentPropertyValues = values;
    }

    /**
     * Returns the property values, which should be applied for certain
     * condition states.
     *
     * @return the property values, which should be applied for certain
     *         condition states
     */
    public Map<DynamicValueState, Object> getConditionStateDependentPropertyValues() {
        return _conditionStateDependentPropertyValues;
    }

    /**
     * Sets the property values, which should be applied for certain condition
     * states.
     *
     * @param values
     *            the property values, which should be applied for certain
     *            condition states
     */
    public void setConditionStateDependentPropertyValues(
            final Map<DynamicValueState, Object> values) {
        assert values != null;
        _conditionStateDependentPropertyValues = values;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DynamicsDescriptor clone() {
        DynamicsDescriptor clone = new DynamicsDescriptor(new String(_ruleId));

        // clone input channels
        List<ParameterDescriptor> clonedParameters = new ArrayList<ParameterDescriptor>();

        for (ParameterDescriptor parameter : _inputChannels) {
            clonedParameters.add(parameter.clone());
        }

        clone._useOnlyConnectionStates = this._useOnlyConnectionStates;

        clone._inputChannels = clonedParameters;

        // clone output channels
        if (_outputChannel != null) {
            clone._outputChannel = _outputChannel.clone();
        } else {
            clone._outputChannel = null;
        }

        // clone connection state values
        if (_connectionStateDependentPropertyValues != null) {
            HashMap<ConnectionState, Object> clonedConnectionValues = new HashMap<ConnectionState, Object>();
            for (ConnectionState key : _connectionStateDependentPropertyValues
                    .keySet()) {
                // FIXME: Sven Wende: Clonen der Werte!
                clonedConnectionValues.put(key,
                        _connectionStateDependentPropertyValues.get(key));
            }
            clone._connectionStateDependentPropertyValues = clonedConnectionValues;
        }

        // clone condition state values
        if (_conditionStateDependentPropertyValues != null) {
            HashMap<DynamicValueState, Object> clonedConditionValues = new HashMap<DynamicValueState, Object>();
            for (DynamicValueState key : _conditionStateDependentPropertyValues
                    .keySet()) {
                // FIXME: Sven Wende: Clonen der Werte!
                clonedConditionValues.put(key,
                        _conditionStateDependentPropertyValues.get(key));
            }
            clone._conditionStateDependentPropertyValues = clonedConditionValues;
        }
        return clone;
    }

    /**
     * Set the ID of the associated rule.
     *
     * @param ruleId
     *            The rule ID to set.
     */
    public void setRuleId(final String ruleId) {
        _ruleId = ruleId;
    }

    /**
     * Sets whether this {@link DynamicsDescriptor} should only use the
     * connection states.
     *
     * @param choice
     *            The choice
     */
    public void setUsingOnlyConnectionStates(final boolean choice) {
        _useOnlyConnectionStates = choice;
    }

    /**
     * Return if this {@link DynamicsDescriptor} uses only the connection
     * states.
     *
     * @return <code>true</code> if this {@link DynamicsDescriptor} uses only
     *         the connection states, <code>false</code> otherwise
     */
    public boolean isUsingOnlyConnectionStates() {
        return _useOnlyConnectionStates;
    }
}
