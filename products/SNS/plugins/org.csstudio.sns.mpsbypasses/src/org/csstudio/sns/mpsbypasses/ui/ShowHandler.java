package org.csstudio.sns.mpsbypasses.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.sns.mpsbypasses.Plugin;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/** Command handler that displays the MPS Bypass 'Editor'
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ShowHandler extends AbstractHandler
{
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
        final Editor editor;
        try
        {
        	final IWorkbench workbench = PlatformUI.getWorkbench();
        	final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        	final IWorkbenchPage page = window.getActivePage();
            editor = (Editor) page.openEditor(BypassEditorInput.instance, Editor.ID);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Plugin.ID).log(Level.SEVERE, "Cannot create MPS Table", ex);
            return null;
        }
        return editor;
	}
}
