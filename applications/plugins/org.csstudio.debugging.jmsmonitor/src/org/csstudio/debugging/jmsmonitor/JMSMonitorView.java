package org.csstudio.debugging.jmsmonitor;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/** Eclipse View for the JMS Monitor
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class JMSMonitorView extends ViewPart
{
    /** View ID defined in plugin.xml */
    final public static String ID = "org.csstudio.debugging.jmsmonitor.view";
    private GUI gui;

    /** {@inheritDoc} */
    @Override
    public void createPartControl(final Composite parent)
    {
        final IPreferencesService preferences = Platform.getPreferencesService();
        final String url = 
            preferences.getString(Activator.ID, "jms_url", null, null);
        gui = new GUI(url, parent);
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        gui.setFocus();
    }
}
