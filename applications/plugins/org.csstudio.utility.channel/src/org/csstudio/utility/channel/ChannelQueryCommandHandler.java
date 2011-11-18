package org.csstudio.utility.channel;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;

import java.util.Arrays;
import java.util.List;

import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Abstract class for all commands that use channels.
 * 
 * @author carcassi
 *
 */
public abstract class ChannelQueryCommandHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		execute(Arrays.asList(AdapterUtil.convert(selection, ChannelQuery.class)), event);
		return null;
	}

	/**
	 * Implements the command. The selection is already converted to the target class.
	 * 
	 * @param channels channels in the selection
	 * @param event event of the command
	 */
	protected abstract void execute(List<ChannelQuery> channels, ExecutionEvent event);
	

}