package org.csstudio.trends.databrowser.editor;

import org.csstudio.email.ui.AbstractSendEMailAction;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.trends.databrowser.Messages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/** Action for e-mailing snapshot of plot
 *  @author Kay Kasemir
 */
public class SendEMailAction extends AbstractSendEMailAction
{
    final private XYGraph graph;

    /** Initialize
     *  @param shell
     *  @param graph
     */
    public SendEMailAction(final Shell shell, final XYGraph graph)
    {
        super(shell, Messages.EMailDefaultSender,
              Messages.LogentryDefaultTitle,
              Messages.LogentryDefaultBody);
        this.graph = graph;
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getImage()
    {
        try
        {
            return new Screenshot(graph).getFilename();
        }
        catch (Exception ex)
        {
            MessageDialog.openError(shell, Messages.Error, ex.getMessage());
            return null;
        }
    }
}
