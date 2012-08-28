package org.csstudio.utility.casnooper;

import org.csstudio.remote.management.CommandParameters;
import org.csstudio.remote.management.CommandResult;
import org.csstudio.remote.management.IManagementCommand;



public class StopCaSnooper implements IManagementCommand {


    public CommandResult execute(CommandParameters parameters) {
        SnooperServer.getInstance().destroy();
        return CommandResult.createMessageResult("DONE");
    }
}