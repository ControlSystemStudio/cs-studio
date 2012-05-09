package org.csstudio.scan.ui.scandata;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.SpreadsheetScanDataIterator;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;
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
public class ScanDataEditor extends EditorPart implements ScanDataModelListener
{
	final public static String ID = "org.csstudio.scan.ui.scandata.display";
	private Text content;
	private ScanDataModel scan_data_model;

	/** {@inheritDoc} */
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

	/** Create GUI elements.
	 * {@inheritDoc}
	 */
	@Override
    public void createPartControl(final Composite parent)
    {
		parent.setLayout(new FillLayout());

		content = new Text(parent, 0);

		final ScanInfo scan = getScanInfo();
		content.setText(NLS.bind("Fetching data from {0}...", scan));

		try
		{
			scan_data_model = new ScanDataModel(scan, this);
		}
		catch (Exception ex)
		{
			ExceptionDetailsErrorDialog.openError(parent.getShell(), "Cannot obtain scan data", ex);
		}
    }

	/** {@inheritDoc} */
	@Override
    public void setFocus()
    {
		content.setFocus();
    }

	/** {@inheritDoc} */
	@Override
    public boolean isDirty()
    {	// Read-only, never gets 'Dirty'
        return false;
    }

	/** {@inheritDoc} */
	@Override
    public boolean isSaveAsAllowed()
    {	// Read-only, cannot save
        return false;
    }

	/** {@inheritDoc} */
	@Override
    public void doSave(final IProgressMonitor monitor)
    {
        // Should not be called
    }

	/** {@inheritDoc} */
	@Override
    public void doSaveAs()
    {
	    // Should not be called
    }

	/** Update display with newly received scan data
	 *  @see ScanDataModelListener
	 */
	@Override
    public void updateScanData(final ScanData data)
    {
		// Transform data in update thread
		final ByteArrayOutputStream buf = new ByteArrayOutputStream();
		new SpreadsheetScanDataIterator(data).dump(new PrintStream(buf));

		// Update display in UI thread
		if (content.isDisposed())
			return;
		content.getDisplay().asyncExec(new Runnable()
		{
			@Override
            public void run()
			{
				if (content.isDisposed())
					return;
				content.setText(buf.toString());
			}
		});
    }
}
