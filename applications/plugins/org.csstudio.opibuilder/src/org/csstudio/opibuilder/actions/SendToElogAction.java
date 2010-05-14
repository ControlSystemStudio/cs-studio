package org.csstudio.opibuilder.actions;

import org.csstudio.apputil.ui.elog.ElogDialog;
import org.csstudio.apputil.ui.elog.SendToElogActionHelper;
import org.csstudio.logbook.ILogbook;
import org.csstudio.opibuilder.runmode.OPIRunner;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.dialogs.MessageDialog;

/** Action to send image of plot to logbook.
 *  @author Kay Kasemir, Xihui Chen
 */
public class SendToElogAction extends SendToElogActionHelper
{
    final private OPIRunner opiRunner;
    public static final String ID = "org.csstudio.opibuilder.actions.sendToElog";


    /** Initialize
     *  @param part Parent shell
     *  @param graph Graph to print
     */
    public SendToElogAction(final OPIRunner part)
    {
        this.opiRunner = part;
        setId(ID);
    }
 
    @Override
    public void run()
    {
        // Get name for snapshot file
        final String filename;
        try
        {
            filename = ResourceUtil.getScreenshotFile(
            		(GraphicalViewer) opiRunner.getAdapter(GraphicalViewer.class));
        }
        catch (Exception ex)
        {
            MessageDialog.openError(opiRunner.getEditorSite().getShell(), "error", ex.getMessage());
            return;
        }
        
        // Display dialog, create entry
        try
        {
            final ElogDialog dialog =
                new ElogDialog(opiRunner.getEditorSite().getShell(), "Send To Logbook",
                        opiRunner.getDisplayModel().getName(),
                        "See attached opi screenshot",
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
            MessageDialog.openError(opiRunner.getEditorSite().getShell(), "Error", ex.getMessage());
        }
    }
}
