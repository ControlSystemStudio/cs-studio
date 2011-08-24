package org.csstudio.sns.mpsbypasses.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.sns.mpsbypasses.Plugin;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/** Command handler that displays the MPS Bypass 'Editor'
 *  @author Kay Kasemir
 */
public class ShowHandler extends AbstractHandler
{
	/** Editor 'input' that represents the MPS bypass table */
	final private static IEditorInput input = new IEditorInput()
	{
		@Override
        public String getName()
        {
	        return "MPS Bypasses";
        }

		@Override
        public String getToolTipText()
        {
	        return getName();
        }

		@SuppressWarnings("rawtypes")
        @Override
        public Object getAdapter(Class adapter)
        {
	        return null;
        }

		@Override
        public boolean exists()
        {
	        return true;
        }

		@Override
        public ImageDescriptor getImageDescriptor()
        {
	        return null;
        }


		@Override
        public IPersistableElement getPersistable()
        {
	        return null;
        }
	};
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
        final Editor editor;
        try
        {
        	final IWorkbench workbench = PlatformUI.getWorkbench();
        	final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        	final IWorkbenchPage page = window.getActivePage();
            editor = (Editor) page.openEditor(input, Editor.ID);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Plugin.ID).log(Level.SEVERE, "Cannot create MPS Table", ex);
            return null;
        }
        return editor;
	}
}
