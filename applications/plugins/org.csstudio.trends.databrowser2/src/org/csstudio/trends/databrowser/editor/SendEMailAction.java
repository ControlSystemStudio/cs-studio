package org.csstudio.trends.databrowser.editor;

import java.io.File;

import org.csstudio.email.ui.AbstractSendEMailAction;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.trends.databrowser.Messages;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

/** Action for e-mailing snapshot of plot
 *  @author Kay Kasemir
 */
public class SendEMailAction extends AbstractSendEMailAction
{
    final private XYGraph graph;

    public SendEMailAction(final Shell shell, final XYGraph graph)
    {
        super(shell, "DataBrowser@css",
                Messages.LogentryDefaultTitle,
                Messages.LogentryDefaultBody);
        this.graph = graph;
    }
    
    protected String getImage()
    {
        // Get name for snapshot file
        final File tmp_file;
        try
        {
            tmp_file = File.createTempFile("DataBrowser", ".png");  //$NON-NLS-1$//$NON-NLS-2$
            tmp_file.deleteOnExit();
        }
        catch (Exception ex)
        {
            final String error = "Cannot create tmp. file:\n" + ex.getMessage(); //$NON-NLS-1$
            MessageDialog.openError(shell, Messages.Error, error);
            return null;
        }
        final String filename = tmp_file.getAbsolutePath();
        
        // Create snapshot file
        try
        {
            final ImageLoader loader = new ImageLoader();
            final Image image = graph.getImage();
            loader.data = new ImageData[]{image.getImageData()};
            image.dispose();
            loader.save(filename, SWT.IMAGE_PNG);
        }
        catch (Exception ex)
        {
            MessageDialog.openError(shell, Messages.Error,
                    NLS.bind("Cannot create snapshot in {0}:\n{1}", //$NON-NLS-1$
                            filename, ex.getMessage()));
            return null;
        }
        return filename;
    }
}
