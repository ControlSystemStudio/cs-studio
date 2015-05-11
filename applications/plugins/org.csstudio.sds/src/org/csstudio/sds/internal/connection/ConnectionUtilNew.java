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
package org.csstudio.sds.internal.connection;

import java.util.Map;

import org.csstudio.auth.security.ActivationService;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.eventhandling.AbstractBehavior;
import org.csstudio.sds.internal.model.logic.RuleEngine;
import org.csstudio.sds.internal.rules.ParameterDescriptor;
import org.csstudio.sds.internal.rules.RuleDescriptor;
import org.csstudio.sds.internal.rules.RuleService;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.dal.simple.ConnectionParameters;
import org.csstudio.dal.simple.RemoteInfo;
import org.csstudio.dal.simple.SimpleDALBroker;
import org.csstudio.domain.common.strings.Strings;

/**
 * Utility class that provides facilities to connect SDS widgets to a control
 * system using {@link SimpleDALBroker}.
 *
 * @author swende
 *
 */
public final class ConnectionUtilNew {

    public static void connectToWidgetManagementApi(
            final AbstractWidgetModel widgetModel) {
        final String permissionId = widgetModel.getPermissionID();
        if ((permissionId != null) && (permissionId.length() > 0)) {
            ActivationService.getInstance().registerObject(permissionId,
                    widgetModel);
        }
    }

    /**
     * Connects the specified widget model to the control system. Thereby the
     * necessary listeners will be connected to control system channels and/or
     * widget properties.
     *
     * @param widgetModel
     *            the widget model
     * @param refreshRate
     *            the refresh rate
     *
     */
    public static void connectDynamizedProperties(
            final WidgetProperty property, final Map<String, String> aliases,
            final boolean writeAccessAllowed, final IListenerRegistry registry,
            final SimpleDALBroker broker) {
        // read the dynamics descriptor
        final DynamicsDescriptor dynamicsDescriptor = property
                .getDynamicsDescriptor();

        // a dynamics descriptor must not exist
        if (dynamicsDescriptor != null) {
            // get all input references
            final ParameterDescriptor[] parameters = dynamicsDescriptor
                    .getInputChannels();

            // .. connect input channels
            if (parameters.length > 0) {
                // .. find the rule
                final String ruleId = dynamicsDescriptor.getRuleId();
                final RuleDescriptor ruleDescriptor = RuleService.getInstance()
                        .getRuleDescriptor(ruleId);

                if (ruleDescriptor != null) {
                    // .. create the rule engine
                    final RuleEngine ruleEngine = new RuleEngine(ruleDescriptor
                            .getRule(), parameters);

                    for (final ParameterDescriptor p : parameters) {
                        final IProcessVariableAddress processVariable = p
                                .getPv(aliases);

                        if (processVariable != null) {
                            // .. connect to control system
                            final ValueType valueType = determineValueType(property,
                                                                     processVariable);
                            ConnectionParameters cparam;
                            // TODO 30.10.2010 (hrickens) workaround until Enum data type is introduced
                            switch (valueType) {
                                case STRING:
                                    cparam = new ConnectionParameters(translateWithoutCharacteristic(processVariable), String.class);
                                    break;
                                case LONG:
                                    cparam = new ConnectionParameters(translateWithoutCharacteristic(processVariable), Long.class);
                                    break;
                                case DOUBLE:
                                    cparam = new ConnectionParameters(translateWithoutCharacteristic(processVariable), Double.class);
                                    break;
                                default:
                                    cparam = new ConnectionParameters(translateWithoutCharacteristic(processVariable));
                                    break;
                            }

                            final ChannelInputProcessor processor = new ChannelInputProcessor(
                                    p,
                                    ruleEngine,
                                    property,
                                    dynamicsDescriptor
                                            .getConnectionStateDependentPropertyValues(),
                                    dynamicsDescriptor
                                            .getConditionStateDependentPropertyValues());

                            final String characteristic = processVariable
                                    .getCharacteristic();

                            final SinglePropertyReadConnector connector = new SinglePropertyReadConnector(
                                    processor, valueType, characteristic);

                            registry.register(cparam, connector);
                        }
                    }
                }
            }

            // .. connect output channels
            final ParameterDescriptor parameter = dynamicsDescriptor
                    .getOutputChannel();

            if ((parameter != null) && writeAccessAllowed) {
                final IProcessVariableAddress processVariable = parameter
                        .getPv(aliases);

                if (processVariable != null) {
                    final ValueType type = determineValueType(property,
                            processVariable);
                    final SinglePropertyWriteConnector connector = new SinglePropertyWriteConnector(
                            processVariable, type, broker);

                    registry.register(property, connector);
                }
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void connectToBehavior(final AbstractWidgetModel widget,
            final IListenerRegistry registry) {
        final String behaviorId = widget
                .getBehaviorProperty(AbstractWidgetModel.PROP_BEHAVIOR);
        if ((behaviorId != null) && (behaviorId.length() > 0)) {
            final AbstractBehavior behavior = SdsPlugin.getDefault()
                    .getBehaviourService().getBehavior(behaviorId,
                            widget.getTypeID());
            final IProcessVariableAddress mainPv = widget.getMainPvAdress();

            if ((behavior != null) && (mainPv != null)) {
                // .. let the behavior initialize the widget before any
                // connections are opened
                behavior.initializeWidget(widget);

                final Class javaType = widget.getJavaType();
                final ConnectionParameters connectionParameters = new ConnectionParameters(
                        new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX + "EPICS",
                                mainPv.getProperty(), null, null), javaType);

                if (connectionParameters != null) {
                    if (behavior != null) {
                        final BehaviorConnector behaviorConnector = new BehaviorConnector(
                                widget, connectionParameters, behavior);

                        // .. let the behavior initialize the widget before any
                        // connections are opened
                        // hrickens (23.07.2010): move up to initializeWidget before connect.
//                        behavior.initializeWidget(widget);

                        // .. connect to the control system to receive dynamic
                        // values and meta data
                        registry.register(connectionParameters,
                                behaviorConnector);

                        // .. connect to the widget to receive manual changes
                        // and forward them to the control system
                        final String[] settablePropertyIds = behavior
                                .getSettablePropertyIds();

                        for (final String id : settablePropertyIds) {
                            widget.addPropertyChangeListener(id,
                                    behaviorConnector);
                        }
                    }
                }
            }
        }
    }

    /**
     * Determines which {@link ValueType} is implied by the given
     * {@link WidgetProperty} and {@link IProcessVariableAddress}.
     *
     * @param property
     *            The {@link WidgetProperty}
     * @param processVariable
     *            The {@link IProcessVariableAddress}
     * @return The {@link ValueType} to use
     */
    private static ValueType determineValueType(final WidgetProperty property,
            final IProcessVariableAddress processVariable) {
        // 1. choice, is there a type hint directly on
        // the pv ?
        ValueType type = processVariable.getValueTypeHint();

//        TODO (jhatje): remove if patch in jca lib works
//        // 2 nd choise from rule
//        /*
//         * XXX hrickens 2010.11.10: ad as workarround for the DAL crash with
//         * JNI. DAL crashed in case of wrong pv request. E.g.: get a String Pv
//         * as Double.
//         */
//        if (type == null) {
//            if (isStringRecord(processVariable)) {
//                type = ValueType.STRING;
//            }
//        }

        // 3nd choice
        if (type == null) {
            // take the type hint, provided
            // by the widget
            // property
            type = property.getPropertyType().getTypeHint();
        }

        // 4rd choice, take double
        if (type == null) {
            type = ValueType.DOUBLE;
        }
        return type;
    }

//    TODO (jhatje): remove if patch in jca lib works
//    private static boolean isStringRecord(IProcessVariableAddress processVariable) {
//        ArrayList<String> recordTails = SdsPlugin.getDefault().getRecordTails();
//        for (String recTail : recordTails) {
//            if (processVariable.getProperty().endsWith(recTail)) {
//                return true;
//            }
//        }
//        ArrayList<String> recordTailsRegExp = SdsPlugin.getDefault().getRecordTailsRegExp();
//        for (String recTailRegExp : recordTailsRegExp) {
//            if (processVariable.getProperty().matches(recTailRegExp)) {
//                return true;
//            }
//        }
//        return false;
//    }


    static final RemoteInfo translateWithoutCharacteristic(final IProcessVariableAddress pv) {
        String cs = "";
        final String responsibleDalPlugId = pv.getControlSystem().getResponsibleDalPlugId();
        if((responsibleDalPlugId!=null) && !Strings.isBlank(responsibleDalPlugId)) {
            cs = RemoteInfo.DAL_TYPE_PREFIX
                + responsibleDalPlugId;
        }
        final String property = pv.getProperty();
        return new RemoteInfo(cs, property, null, null);
    }

}
