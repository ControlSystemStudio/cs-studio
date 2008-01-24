package org.csstudio.sds.internal.connection.dal;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.sds.internal.connection.Connector;
import org.csstudio.sds.internal.connection.IConnectorFactory;

/**
 * A communication provider implementation that deals with the Data Access Layer
 * API (DAL).
 * 
 * @author swende
 * 
 */
public final class DalConnectorFactory implements IConnectorFactory {

	/**
	 * {@inheritDoc}
	 */
	public Connector createConnector(final IProcessVariableAddress processVariable) {
		return new DalConnector(processVariable);
	}
}
