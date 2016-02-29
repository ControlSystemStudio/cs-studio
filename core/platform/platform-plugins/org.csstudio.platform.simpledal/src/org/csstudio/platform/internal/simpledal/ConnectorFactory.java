package org.csstudio.platform.internal.simpledal;

import org.csstudio.platform.internal.simpledal.dal.DalConnector;
import org.csstudio.platform.internal.simpledal.local.LocalConnector;
import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ValueType;

/**
 * Standard implementation of {@link IConnectorFactory}.
 *
 * @author Sven Wende
 *
 */
public class ConnectorFactory implements IConnectorFactory {
    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractConnector createConnector(IProcessVariableAddress pv, ValueType valueType) {
        AbstractConnector connector = null;

        if (pv.getControlSystem() == ControlSystemEnum.LOCAL) {
            connector = new LocalConnector(pv, valueType);
        } else {
            connector = new DalConnector(pv, valueType);
        }

        return connector;
    }
}
