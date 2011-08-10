package org.csstudio.navigator.applaunch;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class EditLaunchConfiguration extends AbstractHandler
{
	@Override
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
		final IStructuredSelection selection =
			(IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		if (selection.isEmpty())
			return null;
		final Object element = selection.getFirstElement();
		if (! (element instanceof IFile))
			return null;
		final IFile file = (IFile) element;
		
		System.out.println("Should edit..." + file);
	    // TODO Auto-generated method stub
	    return null;
    }
}
