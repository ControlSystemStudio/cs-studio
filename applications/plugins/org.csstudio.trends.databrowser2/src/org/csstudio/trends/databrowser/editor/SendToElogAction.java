package org.csstudio.trends.databrowser.editor;

import java.io.File;

import org.csstudio.apputil.ui.elog.ElogDialog;
import org.csstudio.apputil.ui.elog.SendToElogActionHelper;
import org.csstudio.logbook.ILogbook;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.trends.databrowser.Messages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Shell;

/** Action to send image of plot to logbook.
 *  @author Kay Kasemir
 */
public class SendToElogAction extends SendToElogActionHelper
{
    final private Shell shell;
    final private XYGraph graph;

    /** Initialize
     *  @param shell Parent shell
     *  @param graph Graph to print
     */
    public SendToElogAction(final Shell shell, final XYGraph graph)
    {
        this.shell = shell;
        this.graph = graph;
    }
 
    @Override
    public void run()
    {
        // Get name for snapshot file
        final File tmp_file;
        try
        {
            tmp_file = 
                File.createTempFile("DataBrowser", "png");  //$NON-NLS-1$//$NON-NLS-2$
            tmp_file.deleteOnExit();
        }
        catch (Exception ex)
        {
            final String error = "Cannot create tmp. file:\n" + ex.getMessage(); //$NON-NLS-1$
            MessageDialog.openError(shell, Messages.Error, error);
            return;
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
            return;
        }
        
        // Display dialog, create entry
        try
        {
            final ElogDialog dialog =
                new ElogDialog(shell, Messages.SendToElog,
                        Messages.LogentryDefaultTitle,
                        Messages.LogentryDefaultBody,
                        filename)
            {
                @Override
                public void makeElogEntry(final String logbook_name, final String user,
                        final String password, final String title, final String body)
                        throws Exception
                {
                    final ILogbook logbook = getLogbook_factory()
                                        .connect(logbook_name, user, password);
                    try
                    {
                        logbook.createEntry(title, body, filename);
                    }
                    finally
                    {
                        logbook.close();
                    }
                }
                
            };
            dialog.open();
        }
        catch (Exception ex)
        {
            MessageDialog.openError(shell, Messages.Error,
                    NLS.bind(Messages.ErrorFmt, ex.getMessage()));
        }
    }
}
