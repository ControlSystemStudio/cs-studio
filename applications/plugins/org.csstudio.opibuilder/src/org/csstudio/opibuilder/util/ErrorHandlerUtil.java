package org.csstudio.opibuilder.util;

import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

/**
 * The utility class help to handle exceptions.
 * @author Xihui Chen
 *
 */
public class ErrorHandlerUtil {	
	
	/**General error handle method.
	 * @param message message of the error.
	 * @param exception the exception.
	 * @param writeToConsole true if message will output to console.
	 * @param popErrorDialog true if an error dialog will popup. Must be called in UI thread if this is true.
	 */
	public static void handleError(final String message, 
			final Throwable exception, final boolean writeToConsole, final boolean popErrorDialog){
		OPIBuilderPlugin.getLogger().log(Level.WARNING,
            message, exception); //$NON-NLS-1$	
		if(writeToConsole)
			ConsoleService.getInstance().writeError(message + "\n" + exception);
		if(popErrorDialog)
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",	message);		
	}
	
	
	/**This method will call {@link #handleError(String, Throwable, boolean, boolean)} with writeToConsole as true
	 * and popErrorDialog as false.
	 * @param message message of the error.
	 * @param exception the exception.
	 */
	public static void handleError(final String message, 
			final Exception exception){
		handleError(message, exception, true, false);
	}

}
