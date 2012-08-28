package org.csstudio.cagateway.management;

import org.csstudio.cagateway.CaServer;
import org.csstudio.remote.management.CommandParameters;
import org.csstudio.remote.management.CommandResult;
import org.csstudio.remote.management.IManagementCommand;

/**
 * Management command which stops the Interconnection Server.
 */
public class StopServer implements IManagementCommand {

    @Override
    public CommandResult execute(final CommandParameters parameters) {
        CaServer.getGatewayInstance().stop();
        return CommandResult.createSuccessResult();
    }

}
