package org.csstudio.utility.logsender;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/** Eclipse ViewPart for the LogSender GUI
 *  @author Kay Kasemir
 */
public class LogSenderView extends ViewPart
{
    /** View ID registered in plugin.xml */
    final public static String ID =
        "org.csstudio.utility.logsender.LogSenderView"; //$NON-NLS-1$
    
    private GUI gui;

    @Override
    public void createPartControl(final Composite parent)
    {
        gui = new GUI(parent);
    }

    @Override
    public void setFocus()
    {
        gui.setFocus();
    }
}
