package org.csstudio.utility.quickstart;

import org.csstudio.desy.startuphelper.startupservice.IStartupServiceListener;

public class StartupServiceListener implements IStartupServiceListener {

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        final DisplayAutoStart autoStart = new DisplayAutoStart();
        final Thread t = new Thread(autoStart);
        t.start();
    }

}
