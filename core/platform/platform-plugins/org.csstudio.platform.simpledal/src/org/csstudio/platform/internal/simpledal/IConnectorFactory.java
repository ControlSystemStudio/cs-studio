package org.csstudio.platform.internal.simpledal;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ValueType;

/**
 * Factory that creates connectors. Connectors are generally derived from
 * {@link AbstractConnector}.
 *
 * @author Sven Wende
 *
 */
interface IConnectorFactory {
    AbstractConnector createConnector(IProcessVariableAddress pv, ValueType valueType);
}