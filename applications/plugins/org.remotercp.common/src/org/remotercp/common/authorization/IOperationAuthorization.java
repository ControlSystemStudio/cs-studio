package org.remotercp.common.authorization;

import org.eclipse.ecf.core.identity.ID;

public interface IOperationAuthorization {

	public boolean canExecute(ID userID, String methodID);
}
