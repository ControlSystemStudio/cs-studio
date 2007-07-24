package org.csstudio.swt.chart;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.FileDialog;

/** An Action for saving the current image to a file.
 *  <p>
 *  Suggested use is in the context menu of an editor or view that
 *  uses the InteractiveChart.
 *  
 *  @author Kay Kasemir
 */
public class SaveCurrentImageAction extends Action
{
    private final InteractiveChart chart;

    public SaveCurrentImageAction(InteractiveChart chart)
    {
        super(Messages.SaveImage_ActionName,
              Activator.getImageDescriptor("icons/chart.gif")); //$NON-NLS-1$
        this.chart = chart;
        setToolTipText(Messages.SaveImage_ActionName_TT);
    }
    
    @Override
    public void run()
    {
        final Image snapshot = chart.getChart().createSnapshot();
        if (snapshot == null)
            return;
        try
        {
            FileDialog dlg = new FileDialog(chart.getShell(), SWT.SAVE);
            dlg.setText(Messages.SaveImage_ActionTitle);
            dlg.setFilterExtensions(new String[] { "*.jpg", "*.jpeg" }); //$NON-NLS-1$ //$NON-NLS-2$
            dlg.setFilterNames(new String[] { "JPEG", "*JPEG" }); //$NON-NLS-1$ //$NON-NLS-2$
            final String filename = dlg.open();
            if (filename == null)
                return;
            try
            {
                ImageLoader loader = new ImageLoader();
                loader.data = new ImageData[] { snapshot.getImageData() };
                // PNG: Unsupported format
                // PNG: Wrong depth
                loader.save(filename, SWT.IMAGE_JPEG);
            }
            catch (Exception ex)
            {
                MessageDialog.openError(chart.getShell(), Messages.SaveImage_ErrorTitle,
                NLS.bind(Messages.SaveImage_ErrorMessage,
                filename, ex.getMessage()));
            }
        }
        finally
        {
            snapshot.dispose();
        }
    }
}
