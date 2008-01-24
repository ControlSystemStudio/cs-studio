package org.csstudio.sds.internal.connection;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;

/**
 * A connector factory should provide read and write connectors for a certain
 * connection architecture, e.g. DAL.
 * 
 * @author Sven Wende
 */
public interface IConnectorFactory {

	/**
	 * Creates a read connector.
	 * 
	 * @param processVariable
	 *            the process variable, the created connector should connect to
	 * @return a read connector
	 */
	Connector createConnector(final IProcessVariableAddress processVariable);

}
