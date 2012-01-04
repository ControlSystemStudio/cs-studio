package org.csstudio.ui.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import org.csstudio.ui.util.AdapterUtil;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Abstract class for all commands that use AdapterUtil for conversion
 * and displays the exception in a suitable dialog.
 * 
 * @author carcassi
 *
 */
public abstract class AbstractAdaptedHandler<T> extends AbstractHandler {

	private final Class<T> clazz;
	
	public AbstractAdaptedHandler(Class<T> dataType) {
		this.clazz = dataType;
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		try {
			execute(Arrays.asList(AdapterUtil.convert(selection, clazz)), event);
		} catch (Exception ex) {

			IStatus status = new Status(IStatus.ERROR, Activator.ID, ex.getLocalizedMessage(), ex);
			ExceptionDetailsErrorDialog.openError(HandlerUtil.getActiveShell(event),
					"Error executing command...", null, 
					status);
		}
		return null;
	}

	/**
	 * Implements the command. The selection is already converted to the target class.
	 * 
	 * @param data data in the selection
	 * @param event event of the command
	 */
	protected abstract void execute(List<T> data, ExecutionEvent event)
	throws Exception;
	

}