package org.csstudio.archive.common.guard;

import java.util.logging.Logger;

import javax.annotation.concurrent.GuardedBy;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class ArchiveGuardApplication  implements IApplication {

    @GuardedBy("this")
    private boolean _run = true;

    private static final Logger LOG = Logger.getLogger(ArchiveGuardApplication.class.getName());
    
	@Override
	public Object start(IApplicationContext context) throws Exception {
		LOG.info("Guard START");
		ArchiveGuard guard = new ArchiveGuard();
		guard.checkArchiveDbForGaps();
//		guard.compareArchiveDbWithLog();
        return null;
	}

	@Override
	public void stop() {
        LOG.info("Guard STOP");
	}
	
    private synchronized boolean getRun() {
        return _run;
    }
    
    private synchronized void setRun(final boolean run) {
        _run = run;
    }


}
