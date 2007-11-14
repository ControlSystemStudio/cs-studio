package org.csstudio.platform.simpledal;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;

public interface IProcessVariableConnectionService {
	void registerForIntValues(IProcessVariableValueListener<Integer> listener,
			IProcessVariableAddress pv) throws Exception;

	void registerForDoubleValues(
			IProcessVariableValueListener<Double> listener,
			IProcessVariableAddress pv) throws Exception;
}
