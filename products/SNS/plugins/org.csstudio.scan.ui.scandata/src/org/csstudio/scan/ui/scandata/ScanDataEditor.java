package org.csstudio.scan.ui.scandata;

import org.csstudio.scan.server.ScanInfo;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/** Eclipse "Editor" to display scan data
 *  @author Kay Kasemir
 */
public class ScanDataEditor extends EditorPart
{
	final public static String ID = "org.csstudio.scan.ui.scandata.display";
	private Text content;

	@Override
    public void init(final IEditorSite site, final IEditorInput input)
            throws PartInitException
    {
		if (! (input instanceof ScanInfoEditorInput))
			throw new PartInitException("Expecting ScanInfoEditorInput");
    	setInput(input);
    	setSite(site);
    }

	private ScanInfo getScanInfo()
	{
		return ((ScanInfoEditorInput)getEditorInput()).getScan();
	}

	@Override
    public void createPartControl(final Composite parent)
    {
		parent.setLayout(new FillLayout());

		content = new Text(parent, 0);

		content.setText("TODO: Display data from " + getScanInfo());

		// TODO Create ScanDataModel
		// TODO Update display
		// TODO Use table?
    }

	@Override
    public void setFocus()
    {
		content.setFocus();
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
    public void doSave(final IProgressMonitor monitor)
    {
        // Should not be called
    }

	@Override
    public void doSaveAs()
    {
	    // Should not be called
    }
}
