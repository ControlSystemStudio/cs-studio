/**
 *
 */
package org.csstudio.platform.internal.simpledal;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ValueType;

/**
 * Identifies a unique connector. A connector is unique by the process variable
 * it is connected to and the expected return type for values.
 *
 * Connecting to the same process variable with a different expected return type
 * will result in a second physical connection.
 *
 * Addressing characteristics does not lead to different connections - in fact
 * the same connector (with Object as expected return type) will be used for all
 * characteristics.
 *
 * @author Sven Wende
 *
 */
class ConnectorIdentification {
    private IProcessVariableAddress _processVariableAddress;
    private ValueType _valueType;

    /**
     * Constructor.
     *
     * @param processVariableAddress
     *            the process variable address
     * @param valueType
     *            the expected return type
     */
    public ConnectorIdentification(
            IProcessVariableAddress processVariableAddress, ValueType valueType) {
        assert processVariableAddress != null;
        assert valueType != null;
        _processVariableAddress = processVariableAddress
                .deriveNoCharacteristicPart();
        _valueType = valueType;
    }

    /**
     * Returns the process variable address.
     *
     * @return the process variable address
     */
    public IProcessVariableAddress getProcessVariableAddress() {
        return _processVariableAddress;
    }

    /**
     * The expected value type.
     *
     * @return the expected value type
     */
    public ValueType getValueType() {
        return _valueType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((_processVariableAddress == null) ? 0
                        : _processVariableAddress.hashCode());
        result = prime * result
                + ((_valueType == null) ? 0 : _valueType.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj != null && obj instanceof ConnectorIdentification) {
            ConnectorIdentification other = (ConnectorIdentification) obj;

            if (other._valueType == _valueType
                    && other._processVariableAddress
                            .equals(_processVariableAddress)) {
                result = true;
            }
        }

        return result;
    }

}