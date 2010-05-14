package org.csstudio.opibuilder.actions;

import org.csstudio.email.ui.AbstractSendEMailAction;
import org.csstudio.opibuilder.runmode.OPIRunner;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.dialogs.MessageDialog;

/** Action for e-mailing snapshot of plot
 *  @author Kay Kasemir, Xihui Chen
 */
public class SendEMailAction extends AbstractSendEMailAction
{
    final private OPIRunner opiRunner;
    public static final String ID = "org.csstudio.opibuilder.actions.sendEmail";
    /** Initialize
     *  @param shell
     *  @param graph
     */
    public SendEMailAction(final OPIRunner part)
    {
        super(part.getSite().getShell(), "opi@css",
              part.getDisplayModel().getName(),
              "See attached OPI screenshot");
        opiRunner = part;
        setId(ID);
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getImage()
    {
        try
        {
            return ResourceUtil.getScreenshotFile(
            		(GraphicalViewer) opiRunner.getAdapter(GraphicalViewer.class));
        }
        catch (Exception ex)
        {
            MessageDialog.openError(shell, "error", ex.getMessage());
            return null;
        }
    }
}
