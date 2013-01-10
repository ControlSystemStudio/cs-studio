package org.csstudio.logbook.ologviewer;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class OpenOlog extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		 OlogEditor.openOlogEditorInstance();
	        try
	        {
	        	OlogPerspective.showPerspective();
	        }
	        catch (Exception ex)
	        {
	        	
	        }
	        return null;
	}


}
