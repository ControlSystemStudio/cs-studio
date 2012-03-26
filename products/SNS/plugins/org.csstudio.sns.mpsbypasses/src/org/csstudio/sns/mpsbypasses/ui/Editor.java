package org.csstudio.sns.mpsbypasses.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.sns.mpsbypasses.Plugin;
import org.csstudio.sns.mpsbypasses.model.BypassModel;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/** Eclipse "Editor" for the MPS Bypass table
 *
 *  <p>This editor is read-only,
 *  never gets 'dirty', nor can its content
 *  be saved.
 *
 *  <p>It's treated as an editor to show
 *  in the central section of the workbench.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Editor extends EditorPart
{
	/** Editor ID defined in plugin.xml */
	final public static String ID = "org.csstudio.sns.mpsbypasses.editor";

	final private BypassModel model = new BypassModel();
	private GUI gui;

	@Override
    public void init(final IEditorSite site, final IEditorInput input)
            throws PartInitException
    {
		setSite(site);
		setInput(input);
        setPartName("MPS Bypasses");
    }

	@Override
    public void createPartControl(final Composite parent)
    {
		try
		{
			gui = new GUI(parent, model, getSite());
			gui.selectMachineMode();
		}
		catch (Exception ex)
		{
			MessageDialog.openError(parent.getShell(), "Error",
				NLS.bind("Cannot create MPS Table display.\nException: {0}", ex.getMessage()));
			Logger.getLogger(Plugin.ID).log(Level.WARNING, "MPS Table error", ex);
		}
    }

	@Override
    public void setFocus()
    {
		gui.setFocus();
    }

	@Override
	public boolean isDirty()
	{
		return false;
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	@Override
	public void doSave(IProgressMonitor monitor)
	{
		// NOP
	}

	@Override
	public void doSaveAs()
	{
		// NOP
	}
}
