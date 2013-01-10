package org.csstudio.display.pvmanager.pvtable.handlers;

import org.csstudio.display.pvmanager.pvtable.editors.PVTableEditor;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandlerListener;

/**
 * 
 */

/**
 * @author shroffk
 *
 */
public class OpenEmptyPVTable extends AbstractHandler {

	
	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		return PVTableEditor.createPVTableEditor();
	}

}
