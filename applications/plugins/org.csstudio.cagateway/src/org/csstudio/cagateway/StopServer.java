package org.csstudio.cagateway;

import org.csstudio.platform.management.CommandParameters;
import org.csstudio.platform.management.CommandResult;
import org.csstudio.platform.management.IManagementCommand;

/**
 * Management command which stops the Interconnection Server.
 */
public class StopServer implements IManagementCommand {
    
    public CommandResult execute(CommandParameters parameters) {
        CaServer.getGatewayInstance().stop();
        return CommandResult.createSuccessResult();
    }
    
}
