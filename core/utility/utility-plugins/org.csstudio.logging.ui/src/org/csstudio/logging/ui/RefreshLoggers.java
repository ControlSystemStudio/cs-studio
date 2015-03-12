package org.csstudio.logging.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.ISharedImages;

/**
 * Rebuild the logging configuration tree by reloading all the loggers
 * 
 * @author Kunal Shroff
 *
 */
public class RefreshLoggers extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
	FXLogginConfiguration.updateLoggerMap();
	return null;
    }

}
