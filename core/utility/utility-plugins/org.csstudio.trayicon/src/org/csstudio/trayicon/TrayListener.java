package org.csstudio.trayicon;


import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class TrayListener implements EventHandler {

    private TrayIcon trayIcon;

    @Inject
    private IEventBroker broker;

    @PostConstruct
    public void lazyLoadInContributorPerspective() {
        // Subscribe to any part (Editor or View) being activated.
        broker.subscribe(UIEvents.UILifeCycle.ACTIVATE, this);
    }

    @Override
    public void handleEvent(Event event) {
        // When an event is received, maximise the window.
        if (trayIcon != null && trayIcon.isMinimized()) {
            trayIcon.unminimize();
        }
    }

    public void setTrayIcon(TrayIcon trayIcon) {
        this.trayIcon = trayIcon;
    }

}
