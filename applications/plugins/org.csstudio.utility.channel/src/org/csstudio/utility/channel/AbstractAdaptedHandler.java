package org.csstudio.utility.channel;

import java.util.Arrays;
import java.util.List;

import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Abstract class for all commands that use AdapterUtil for conversion.
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
		execute(Arrays.asList(AdapterUtil.convert(selection, clazz)), event);
		return null;
	}

	/**
	 * Implements the command. The selection is already converted to the target class.
	 * 
	 * @param data data in the selection
	 * @param event event of the command
	 */
	protected abstract void execute(List<T> data, ExecutionEvent event);
	

}