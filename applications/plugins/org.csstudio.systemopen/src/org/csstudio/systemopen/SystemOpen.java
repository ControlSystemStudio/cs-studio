package org.csstudio.systemopen;

import java.awt.Desktop;
import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class SystemOpen extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		StructuredSelection selection = (StructuredSelection) HandlerUtil
				.getActiveMenuSelection(event);
		if (selection != null) {
			for (Object item : selection.toArray()) {
				try {
					IResource resource = (IResource) item;
					System.out.println(resource.getLocationURI().toString());
					Desktop desktop = Desktop.getDesktop();
					desktop.open(new File(resource.getLocationURI()));
				} catch (Exception e) {
					System.out.println(e);
					// Skip to next
				}
			}
		}
		return null;
	}

}
