package org.csstudio.phoebus.integration;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

/**
 * Simply launch cs-studio phoebus.
 * 
 * @author Kunal Shroff
 *
 */
public class Launch extends AbstractHandler implements IHandler{

    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
        PhoebusLauncherService.launch();
        return null;
    }

}
