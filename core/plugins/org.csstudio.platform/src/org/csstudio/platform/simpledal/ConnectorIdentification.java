/**
 * 
 */
package org.csstudio.platform.simpledal;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;

public class ConnectorIdentification {
	private IProcessVariableAddress _processVariableAddress;
	private ValueType _valueType;

	public ConnectorIdentification(IProcessVariableAddress processVariableAddress,
			ValueType valueType) {
		_processVariableAddress = processVariableAddress.deriveNoCharacteristicPart();
		_valueType = valueType;
	}

	public IProcessVariableAddress getProcessVariableAddress() {
		return _processVariableAddress;
	}
	
	public ValueType getValueType() {
		return _valueType;
	}
	
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