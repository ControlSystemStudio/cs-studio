package org.csstudio.scan.ui.scandata;

import org.csstudio.scan.server.ScanInfo;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/** Editor input wrapper for {@link ScanInfo}
 *  @author Kay Kasemir
 */
public class ScanInfoEditorInput implements IEditorInput
{
	final private ScanInfo scan;

	public ScanInfoEditorInput(final ScanInfo scan)
    {
		this.scan = scan;
    }

	public ScanInfo getScan()
    {
    	return scan;
    }

	@SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(final Class adapter)
    {
	    return null;
    }

	@Override
    public boolean exists()
    {
	    return false;
    }

	@Override
    public ImageDescriptor getImageDescriptor()
    {
	    return null;
    }

	@Override
    public String getName()
    {
	    return scan.getName();
    }

	@Override
    public IPersistableElement getPersistable()
    {
	    return null;
    }

	@Override
    public String getToolTipText()
    {
	    return NLS.bind(Messages.ScanEditorTTFmt, scan.getName());
    }
}
