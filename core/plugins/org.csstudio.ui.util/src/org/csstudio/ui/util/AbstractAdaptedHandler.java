package org.csstudio.ui.util;

import java.util.Arrays;
import java.util.List;

import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
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
	
	/**
	 * Searches for the view of the given class with the given view id.
	 * 
	 * @param clazz the view class
	 * @param viewId the view id
	 * @return the view
	 * @throws PartInitException if the view was not found
	 * @throws ClassCastException if the view is of a different type
	 */
	public static <T> T findView(Class<T> clazz, String viewId) throws PartInitException {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		return clazz.cast(page.showView(viewId));
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		try {
			execute(Arrays.asList(AdapterUtil.convert(selection, clazz)), event);
		} catch (Exception ex) {
			ExceptionDetailsErrorDialog.openError(HandlerUtil.getActiveShell(event),
					"Error executing command...", ex);
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