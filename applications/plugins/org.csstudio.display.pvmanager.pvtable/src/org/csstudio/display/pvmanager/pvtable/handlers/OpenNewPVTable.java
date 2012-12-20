package org.csstudio.display.pvmanager.pvtable.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.display.pvmanager.pvtable.editors.PVTableEditor;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author shroffk
 * 
 */
public class OpenNewPVTable extends AbstractHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Get the selection		
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		PVTableEditor newEditor = PVTableEditor.createPVTableEditor(); 
		if (selection == null) {
			// create a new empty pvtable
			return newEditor;
		} else if ((selection != null)
				& (selection instanceof IStructuredSelection)) {
			// create a pvtable using the selected pv
			Collection<ProcessVariable> PVNames = new ArrayList<ProcessVariable>();
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			for (@SuppressWarnings("rawtypes")
			Iterator iterator = strucSelection.iterator(); iterator.hasNext();) {
				PVNames.add((ProcessVariable) Platform.getAdapterManager()
						.getAdapter(iterator.next(), ProcessVariable.class));
			}
			newEditor.addProcessVariables(PVNames);
			return newEditor;
		}
		return null;
	}

}
