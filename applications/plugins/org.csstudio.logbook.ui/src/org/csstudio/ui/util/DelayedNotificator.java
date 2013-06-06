package org.csstudio.ui.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

public class DelayedNotificator {

    private final long delay;
    private final TimeUnit unit;
    private ScheduledFuture<?> future;

    public DelayedNotificator(long delay, TimeUnit unit) {
	this.delay = delay;
	this.unit = unit;
    }

    public static ScheduledExecutorService exec = Executors
	    .newScheduledThreadPool(1);

    public void delayedExec(final Widget widget, final Runnable command) {
	if (future != null) {
	    if (!future.isDone()) {
		future.cancel(false);
	    }
	    future = null;
	}

	future = exec.schedule(new Runnable() {

	    @Override
	    public void run() {
		widget.getDisplay().asyncExec(command);
	    }
	}, delay, unit);
    }

}
