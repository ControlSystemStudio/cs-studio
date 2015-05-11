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
package org.csstudio.sds.model.initializers;

import java.util.Map;

import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.sds.internal.rules.NullRule;
import org.csstudio.sds.internal.rules.ParameterDescriptor;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DynamicsDescriptor;

/**
 * Base class for widget model initializers that provides a convenient API for
 * initializing widget model for a certain control system.
 *
 * @author Sven Wende
 * @version $Revision: 1.5 $
 *
 */
abstract class AbstractInitializer {
    /**
     * The current model that is being initialized. This reference will be
     * injected at runtime.
     */
    private AbstractWidgetModel _widgetModel;

    /**
     * Setter, which injects the widget model at runtime. Should only be called
     * within this package.
     *
     * @param widgetModel
     *            the widget model
     */
    final void setWidgetModel(final AbstractWidgetModel widgetModel) {
        _widgetModel = widgetModel;
    }

    /**
     * Initializes a alias, which has widget scope.
     *
     * @param alias
     *            the alias name, e.g. "channel"
     * @param description
     *            a alias description
     *
     */
    public final void initializeAlias(final String alias,
            final String description) {
        _widgetModel.addAlias(alias, "");
    }

    /**
     * Initializes a property with a static value.
     *
     * @param propertyId
     *            the property id
     * @param value
     *            the value
     */
    public final void initializeStaticProperty(final String propertyId,
            final Object value) {
        _widgetModel.setPropertyValue(propertyId, value);
    }

    /**
     * Initializes a property with a single input and a single output channel.
     *
     * @param propertyId
     *            the property id
     * @param channelName
     *            the input channel name
     * @param outputChannelName
     *            the output channel name
     */
    public final void initializeDynamicProperty(final String propertyId,
            final String channelName, final String outputChannelName,
            final String ruleID) {
        final DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor();
        dynamicsDescriptor.addInputChannel(new ParameterDescriptor(channelName));
        if (outputChannelName != null) {
            dynamicsDescriptor.setOutputChannel(new ParameterDescriptor(
                    outputChannelName));
        }
        if (ruleID != null) {
            dynamicsDescriptor.setRuleId(ruleID);
        }
        _widgetModel.setDynamicsDescriptor(propertyId, dynamicsDescriptor);
    }

    /**
     * Initializes a property with a single input channel.
     *
     * @param propertyId
     *            the property id
     * @param channelName
     *            the input channel name
     */
    public final void initializeDynamicProperty(final String propertyId,
            final String channelName) {
        initializeDynamicProperty(propertyId, channelName, null, null);

    }

    /**
     * Initializes a property with a several input channels.
     *
     * @param propertyId
     *            the property id
     * @param channelNames
     *            the input channel names
     */
    public final void initializeDynamicProperty(final String propertyId,
            final String[] channelNames) {
        initializeDynamicProperty(propertyId, channelNames, null, null);
    }

    /**
     * Initializes a property with several input channels and a single output
     * channel.
     *
     * @param propertyId
     *            the property id
     * @param channelNames
     *            the input channel names
     * @param outputChannelName
     *            the output channel name
     */
    public final void initializeDynamicProperty(final String propertyId,
            final String[] channelNames, final String outputChannelName,
            final String ruleID) {
        final DynamicsDescriptor dynamicsDescriptor = new DynamicsDescriptor();

        for (String channelName : channelNames) {
            dynamicsDescriptor.addInputChannel(new ParameterDescriptor(
                    channelName));
        }

        if (outputChannelName != null) {
            dynamicsDescriptor.setOutputChannel(new ParameterDescriptor(
                    outputChannelName));
        }
        if (ruleID != null) {
            dynamicsDescriptor.setRuleId(ruleID);
        }
        _widgetModel.setDynamicsDescriptor(propertyId, dynamicsDescriptor);
    }

    public final void initializeDynamicPropertyForConnectionState(
            String propertyId, String channelName,
            Map<ConnectionState, Object> connectionStateDependentValues) {
        initializeDynamicPropertyForConnectionState(propertyId, channelName,
                connectionStateDependentValues, null);

    }

    public final void initializeDynamicPropertyForConnectionState(
            String propertyId, String channelName,
            Map<ConnectionState, Object> connectionStateDependentValues,
            String ruleID) {
        // create a new dynamic configuration for the specified property
        DynamicsDescriptor descriptor = new DynamicsDescriptor();

        // configure channels
        descriptor.addInputChannel(new ParameterDescriptor(channelName));

        // configure connection state dependent values
        descriptor
                .setConnectionStateDependentPropertyValues(connectionStateDependentValues);

        // configure the rule (the Null-Rule is needed so that connection states
        // dominate value changes)
        if (ruleID == null) {
            descriptor.setRuleId(NullRule.ID);
        } else {
            descriptor.setRuleId(ruleID);
        }

        _widgetModel.setDynamicsDescriptor(propertyId, descriptor);
    }

    // public final void initializeDynamicProperty(String propertyId, String
    // channelName, ConnectionState connectionState, Object value) {
    // // create a new dynamic configuration for the specified property
    // DynamicsDescriptor descriptor = new DynamicsDescriptor();
    //
    // // configure channels
    // descriptor.addInputChannel(new ParameterDescriptor(channelName));
    //
    // // configure connection state dependent values
    // Map<ConnectionState, Object> values = new HashMap<ConnectionState,
    // Object>();
    // values.put(connectionState, value);
    // descriptor.setConnectionStateDependentPropertyValues(values);
    //
    // // configure the rule (the Null-Rule is needed so that connection states
    // dominate value changes)
    // descriptor.setRuleId(NullRule.ID);
    //
    // _widgetModel.setDynamicsDescriptor(propertyId,descriptor);
    //
    // }
}
