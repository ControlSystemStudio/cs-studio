package org.csstudio.utility.recordproperty;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handle activation of Record Property from the object contrib. context menu.
 * 
 * @author Kay Kasemir
 * @author Helge Rickens
 * @author Rok Povsic
 */
public class PVpopupAction extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		final ProcessVariable[] pvs = AdapterUtil.convert(selection,
				ProcessVariable.class);
		RecordPropertyView.activateWithPV(pvs[0]);
		return null;
	}
}
