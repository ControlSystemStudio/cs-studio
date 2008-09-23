package org.csstudio.debugging.jmsmonitor;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/** Eclipse View for the JMS Monitor
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class JMSMonitorView extends ViewPart
{
    /** View ID defined in plugin.xml */
    final public static String ID = "org.csstudio.debugging.jmsmonitor.view";

    /** Memento tag */
    final private static String TAG_TOPIC = "topic";

    private GUI gui;

    private IMemento memento;
    
    /** @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento) */
    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException
    {
        super.init(site, memento);
        this.memento = memento;
    }

    /** @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento) */
    @Override
    public void saveState(IMemento memento)
    {
        super.saveState(memento);
        memento.putString(TAG_TOPIC, gui.getTopic());
    }

    /** {@inheritDoc} */
    @Override
    public void createPartControl(final Composite parent)
    {
        final IPreferencesService preferences = Platform.getPreferencesService();
        final String url = 
            preferences.getString(Activator.ID, "jms_url", null, null);
        gui = new GUI(url, parent);
        if (memento == null)
            return;
        gui.setTopic(memento.getString(TAG_TOPIC));
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        gui.setFocus();
    }
}
