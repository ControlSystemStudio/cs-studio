package org.csstudio.utility.eliza;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/** Eclipse View for ElizaGUI
 *  @author Kay Kasemir
 */
public class ElizaView extends ViewPart
{
    /** View ID */
    final public static String ID = "org.csstudio.utility.eliza.ElizeView"; //$NON-NLS-1$
    
    private ElizaGUI gui;

    /** {@inheritDoc} */
    @Override
    public void createPartControl(Composite parent)
    {
        gui = new ElizaGUI(parent);
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        gui.setFocus();
    }
}
