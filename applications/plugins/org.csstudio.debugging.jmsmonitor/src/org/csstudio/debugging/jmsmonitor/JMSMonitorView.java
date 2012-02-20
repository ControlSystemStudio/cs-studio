/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.debugging.jmsmonitor;

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
        gui = new GUI(Preferences.getJMS_URL(),
                      Preferences.getJMS_User(),
                      Preferences.getJMS_Password(),
                      parent);
        if (memento == null)
            return;
        final String topic = memento.getString(TAG_TOPIC);
        if (topic.isEmpty())
            gui.setTopic(Messages.EnterTopicMsg);
        else
            gui.setTopic(topic);
        getSite().setSelectionProvider(gui.getSelectionProvider());
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        gui.setFocus();
    }
}
