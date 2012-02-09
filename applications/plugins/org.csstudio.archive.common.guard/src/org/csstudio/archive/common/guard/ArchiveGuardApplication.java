package org.csstudio.archive.common.guard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.concurrent.GuardedBy;

import org.csstudio.domain.desy.time.TimeInstant;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class ArchiveGuardApplication  implements IApplication {

    @GuardedBy("this")
    private boolean _run = true;

    private static final Logger LOG = Logger.getLogger(ArchiveGuardApplication.class.getName());
    
	@Override
	public Object start(IApplicationContext context) throws Exception {
		LOG.info("Guard START");
		ConnectionHandler connectionHandler = new ConnectionHandler();
		ArchiveGuard guard = new ArchiveGuard(connectionHandler);
		List<PvIntervalList> listOfPvIntervals = new ArrayList<>();
		listOfPvIntervals = guard.checkForLostSamples(listOfPvIntervals);
		connectionHandler.close();
		Evaluater eval = new Evaluater();
		listOfPvIntervals = eval.removeEmptyPvLists(listOfPvIntervals);
		listOfPvIntervals = eval.removeSmallAvgPvLists(listOfPvIntervals, 5);
		eval.printAverageAndVariance(listOfPvIntervals);
		eval.printLostHourSamples(listOfPvIntervals);
//		eval.printIntervals(listOfPvIntervals);
//		Map<TimeInstant, List<String>> aggregateGapsForRange = eval.aggregateGapsForRange(listOfPvIntervals);
//		eval.printAggregatedGaps(aggregateGapsForRange);
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
