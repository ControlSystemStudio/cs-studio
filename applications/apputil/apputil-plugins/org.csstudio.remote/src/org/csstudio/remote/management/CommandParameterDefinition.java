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
import java.util.ArrayList;
import java.util.List;

/**
 * Definition of a parameter accepted by a management command.
 *
 * @author Joerg Rathlev
 */
public final class CommandParameterDefinition implements Serializable {

    private static final long serialVersionUID = 2L;

    private final CommandParameterType _type;
    private final String _id;
    private final String _label;
    private final int _minimum;
    private final int _maximum;
    private final CommandParameterEnumValue[] _enumValues;

    /**
     * Private constructor, called only by the {@link Builder}.
     *
     * @param builder
     *            the builder.
     */
    private CommandParameterDefinition(Builder builder) {
        if (builder._id == null || builder._label == null) {
            throw new IllegalArgumentException("id and label must not be null");
        }

        _id = builder._id;
        _label = builder._label;
        _type = builder._type;

        switch (builder._type) {
        case STRING:
        case DYNAMIC_ENUMERATION:
            _minimum = 0;
            _maximum = 0;
            _enumValues = null;
            break;
        case INTEGER:
            _minimum = builder._minimum;
            _maximum = builder._maximum;
            if (_maximum <= _minimum) {
                throw new IllegalArgumentException("minimum must be less than maximum");
            }
            _enumValues = null;
            break;
        case ENUMERATION:
            _minimum = 0;
            _maximum = 0;
            if (builder._enumValues.isEmpty()) {
                throw new IllegalArgumentException("enumeration must have at least one value");
            }
            _enumValues = (CommandParameterEnumValue[]) builder._enumValues
                    .toArray(new CommandParameterEnumValue[builder._enumValues.size()]);
            break;
        default:
            throw new IllegalArgumentException("unknown parameter type");
        }
    }

    /**
     * Returns the identifier of this parameter.
     *
     * @return the identifier of this parameter.
     */
    public String getIdentifier() {
        return _id;
    }

    /**
     * Returns the type of this parameter.
     *
     * @return the type of this parameter.
     */
    public CommandParameterType getType() {
        return _type;
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
     * Returns the minimum value. If this parameter is not of type
     * {@link CommandParameterType#INTEGER}, the returned value is meaningless.
     *
     * @return the minimum value.
     */
    public int getMinimum() {
        return _minimum;
    }

    /**
     * Returns the maximum value. If this parameter is not of type
     * {@link CommandParameterType#INTEGER}, the returned value is meaningless.
     *
     * @return the maximum value.
     */
    public int getMaximum() {
        return _maximum;
    }

    /**
     * Returns the enumeration values. If this parameter is not of type
     * {@link CommandParameterType#ENUMERATION}, returns <code>null</code>. For
     * a dynamic enumeration parameter, the enumeration values must be queried
     * from the management service.
     *
     * @return the enumeration values, or <code>null</code> if this parameter is
     *         not of an enumeration type.
     */
    public CommandParameterEnumValue[] getEnumerationValues() {
        if (_enumValues == null) {
            return null;
        }

        CommandParameterEnumValue[] result =
            new CommandParameterEnumValue[_enumValues.length];
        System.arraycopy(_enumValues, 0, result, 0, _enumValues.length);
        return result;
    }

    /**
     * <p>
     * Checks whether an object is a legal parameter value for the parameter
     * defined by this definition. What constitutes a legal parameter value
     * depends on the type of the parameter.
     * </p>
     *
     * <ul>
     * <li>For a parameter of type {@link CommandParameterType#STRING}, the
     * object must be of type {@link String}.</li>
     *
     * <li>For a parameter of type {@link CommandParameterType#INTEGER}, the
     * object must be of type {@link Integer} and the integer value must be
     * within the range specified by the minimum and maximum values of this
     * parameter definition.</li>
     *
     * <li>For a parameter of type {@link CommandParameterType#ENUMERATION}, the
     * object must be of type {@link String} and must be the value of one of the
     * enumeration values defined by this parameter definition.</li>
     *
     * <li>For a parameter of type
     * {@link CommandParameterType#DYNAMIC_ENUMERATION}, the object must be of
     * type {@link String}.</li>
     * </ul>
     *
     * @param value
     *            the parameter value.
     * @return <code>true</code> if the specified object is a legal parameter
     *         value according to this definition, <code>false</code> otherwise.
     */
    public boolean isLegalParameterValue(Object value) {
        switch (_type) {
        case STRING:
            return value instanceof String;
        case INTEGER:
            return value instanceof Integer && isWithinRange((Integer) value);
        case ENUMERATION:
            return value instanceof String
                && isDefinedEnumerationValue((String) value);
        case DYNAMIC_ENUMERATION:
            return value instanceof String;
        default:
            return false;
        }
    }

    /**
     * Checks whether the given integer value is within the minimum, maximum
     * range of this parameter definition.
     *
     * @param value
     *            the value.
     * @return <code>true</code> if the value is within the range,
     *         <code>false</code> otherwise.
     */
    private boolean isWithinRange(Integer value) {
        int i = value.intValue();
        return i >= _minimum && i <= _maximum;
    }

    /**
     * Checks whether the given enumeration value is one of the values defined
     * by this parameter definition.
     *
     * @param value
     *            the value.
     * @return <code>true</code> if the value is a defined enumeration value,
     *         <code>false</code> otherwise.
     */
    private boolean isDefinedEnumerationValue(String value) {
        CommandParameterEnumValue[] legalValues = getEnumerationValues();
        for (CommandParameterEnumValue legalValue : legalValues) {
            if (value.equals(legalValue.getValue())) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return _id;
    }

    /**
     * Builder for {@link CommandParameterDefinition} objects.
     */
    public static final class Builder {
        private CommandParameterType _type;
        private String _id;
        private String _label;
        private int _minimum = Integer.MIN_VALUE;
        private int _maximum = Integer.MAX_VALUE;
        private List<CommandParameterEnumValue> _enumValues;

        /**
         * Creates a new {@link CommandParameterDefinition} builder.
         */
        public Builder() {
            _enumValues = new ArrayList<CommandParameterEnumValue>();
        }

        /**
         * Sets the parameter type.
         *
         * @param type
         *            the type.
         * @return this builder.
         */
        public Builder setType(CommandParameterType type) {
            _type = type;
            return this;
        }

        /**
         * Sets the parameter name.
         *
         * @param id
         *            the identifier.
         * @return this builder.
         */
        public Builder setIdentifier(String id) {
            _id = id;
            return this;
        }

        /**
         * Sets the label of the parameter.
         *
         * @param label
         *            the label.
         * @return this builder.
         */
        public Builder setLabel(String label) {
            _label = label;
            return this;
        }

        /**
         * Sets the minimum parameter value for a parameter of type
         * {@link CommandParameterType#INTEGER}. For parameters of other types,
         * calling this method has no effect.
         *
         * @param minimum
         *            the minimum.
         * @return this builder.
         */
        public Builder setMinimum(int minimum) {
            _minimum = minimum;
            return this;
        }

        /**
         * Sets the maximum parameter value for a parameter of type
         * {@link CommandParameterType#INTEGER}. For parameters of other types,
         * calling this method has no effect.
         *
         * @param maximum
         *            the maximum.
         * @return this builder.
         */
        public Builder setMaximum(int maximum) {
            _maximum = maximum;
            return this;
        }

        /**
         * Adds an enumeration value to the parameter. This method should be
         * called for parameters of type
         * {@link CommandParameterType#ENUMERATION}. Calling it for parameters
         * of other types has no effect.
         *
         * @param value
         *            the enumeration value.
         * @return this builder.
         */
        public Builder addEnumerationValue(CommandParameterEnumValue value) {
            _enumValues.add(value);
            return this;
        }

        /**
         * Builds the parameter definition based on the settings of this
         * builder.
         *
         * @return the parameter definition.
         */
        public CommandParameterDefinition build() {
            return new CommandParameterDefinition(this);
        }
    }
}
