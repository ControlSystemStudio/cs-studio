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

package org.csstudio.remote.internal.management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.remote.management.CommandDescription;
import org.csstudio.remote.management.CommandParameterDefinition;
import org.csstudio.remote.management.CommandParameterEnumValue;
import org.csstudio.remote.management.CommandParameterType;
import org.csstudio.remote.management.IDynamicParameterValues;
import org.csstudio.remote.management.IManagementCommand;

/**
 * <p>
 * A management command contributed through the <code>managementCommands</code>
 * extension point. Instances of this class contain a description of the command
 * and a reference to its implementation.
 * </p>
 *
 * <p>
 * Instances of this class are created using the Builder pattern.
 * </p>
 *
 * @author Joerg Rathlev
 */
final class CommandContribution {
    /*
     * Why does this class use the builder pattern? The idea is that this
     * resolves two design goals: on the one hand, we want to have only complete
     * CommandDescriptions, CommandContributions etc.; on the other hand, the
     * configuration elements are read one after another, so the information
     * must be stored somewhere until all elements are read. Doing that is the
     * responsibility of the Builder.
     */

    private final CommandDescription _description;
    private final IManagementCommand _implementation;
    private final Map<String, IDynamicParameterValues> _dynamicParameterValues;

    /**
     * Private constructor which is called only by the {@link Builder}.
     *
     * @param builder
     *            the builder.
     */
    private CommandContribution(Builder builder) {
        _dynamicParameterValues = new HashMap<String, IDynamicParameterValues>();
        List<CommandParameterDefinition> parameterList =
            new ArrayList<CommandParameterDefinition>();
        for (ParameterContribution param : builder._parameters) {
            parameterList.add(param._definition);
            if (param._dynamicValues != null) {
                _dynamicParameterValues.put(param._definition.getIdentifier(),
                        param._dynamicValues);
            }
        }
        CommandParameterDefinition[] parameters =
            (CommandParameterDefinition[]) parameterList
                .toArray(new CommandParameterDefinition[parameterList.size()]);
        _description = new CommandDescription(builder._id, builder._label, parameters);
        _implementation = builder._implementation;
    }

    /**
     * Returns the description of the command.
     *
     * @return the description of the command.
     */
    CommandDescription getDescription() {
        return _description;
    }

    /**
     * Returns the implementation of the command.
     *
     * @return the implementation of the command.
     */
    IManagementCommand getCommandImplementation() {
        return _implementation;
    }

    /**
     * Returns the dynamic enumeration values for the specified parameter.
     *
     * @param parameterId
     *            the parameter identifier.
     * @return an array of enumeration values.
     * @throws IllegalArgumentException
     *             if the parameter is not a dynamic enumeration parameter.
     */
    CommandParameterEnumValue[] getDynamicEnumerationValues(String parameterId) {
        IDynamicParameterValues dynamicValues = _dynamicParameterValues.get(parameterId);
        if (dynamicValues != null) {
            return dynamicValues.getEnumerationValues();
        } else {
            throw new IllegalArgumentException(parameterId +
                    " is not a dynamic enumeration parameter");
        }
    }

    /**
     * Instances of this class are responsible for associating a parameter
     * definition with the relevant {@link IDynamicParameterValues}.
     */
    private static final class ParameterContribution {
        private final CommandParameterDefinition _definition;
        private final IDynamicParameterValues _dynamicValues;

        /**
         * Creates a new parameter contribution.
         *
         * @param definition
         *            the parameter definition.
         * @param dynamicValues
         *            the dynamic values, or <code>null</code> if the parameter
         *            is not of type
         *            {@link CommandParameterType#DYNAMIC_ENUMERATION}.
         */
        ParameterContribution(CommandParameterDefinition definition,
                IDynamicParameterValues dynamicValues) {
            _definition = definition;
            _dynamicValues = dynamicValues;
        }
    }

    /**
     * Builder for {@link CommandContribution} objects.
     */
    static final class Builder {
        private String _id;
        private String _label;
        private IManagementCommand _implementation;
        private List<ParameterContribution> _parameters;

        /**
         * Creates a new {@link CommandContribution} builder.
         */
        Builder() {
            _parameters = new ArrayList<ParameterContribution>();
        }

        /**
         * Sets the identifier of the command.
         *
         * @param id
         *            the identifier.
         * @return this builder.
         */
        Builder setIdentifier(String id) {
            _id = id;
            return this;
        }

        /**
         * Sets the label of the command.
         *
         * @param label
         *            the label.
         * @return this builder.
         */
        Builder setLabel(String label) {
            _label = label;
            return this;
        }

        /**
         * Sets the implementation of the management command.
         *
         * @param implementation
         *            the implementation.
         * @return this builder.
         */
        Builder setCommandImplementation(IManagementCommand implementation) {
            _implementation = implementation;
            return this;
        }

        /**
         * Adds a parameter to the management command.
         *
         * @param definition
         *            the parameter definition.
         * @param dynamicValues
         *            the dynamic values, or <code>null</code> if the parameter
         *            is not of type
         *            {@link CommandParameterType#DYNAMIC_ENUMERATION}.
         * @return this builder.
         * @throws IllegalArgumentException
         *             if <code>definition</code> is <code>null</code>, or if
         *             the parameter is of type <code>DYNAMIC_ENUMERATION</code>
         *             and <code>dynamicValues</code> is <code>null</code>.
         */
        Builder addParameter(CommandParameterDefinition definition,
                IDynamicParameterValues dynamicValues) {
            if (definition == null) {
                throw new IllegalArgumentException("definition was null");
            }
            if (definition.getType() == CommandParameterType.DYNAMIC_ENUMERATION
                    && dynamicValues == null) {
                throw new IllegalArgumentException(
                        "dynamicValues was null for a parameter of type DYNAMIC_ENUMERATION");
            }
            _parameters.add(new ParameterContribution(definition, dynamicValues));
            return this;
        }

        /**
         * Creates the command contribution based on the settings of this
         * builder.
         *
         * @return the command contribution.
         * @throws IllegalStateException
         *             if not all settings required to build a
         *             <code>CommandContribution</code> have been set. The
         *             required settings are the identifier, the label and the
         *             implementation.
         */
        CommandContribution build() {
            if (_id == null) {
                throw new IllegalStateException("no identifier set");
            } else if (_label == null) {
                throw new IllegalStateException("no label set");
            } else if (_implementation == null) {
                throw new IllegalStateException("no implementation set");
            }
            return new CommandContribution(this);
        }
    }
}
