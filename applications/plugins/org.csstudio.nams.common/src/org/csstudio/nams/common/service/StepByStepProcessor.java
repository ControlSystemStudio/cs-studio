package org.csstudio.nams.common.service;

/**
 * This runnable is to be used for step-by-step processing based operations.
 * 
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * 
 * XXX This class is in draft state
 */
public abstract class StepByStepProcessor implements Runnable {

	private volatile Thread executionThread = null;
	private volatile boolean continueRunning = true;

	public final void run() {
		if (executionThread != null) {
			throw new IllegalThreadStateException(
					"This runnable is already running!");
		}
		executionThread = Thread.currentThread();
		continueRunning = true;

		while (continueRunning) {
			try {
				runOneSingleStep();
				Thread.yield();
			} catch (InterruptedException e) {
				// This is expected behaviour to stop this runnable, so nothing
				// to do here!
			}
		}

		executionThread = null;
	}

	public final boolean isCurrentlyRunning() {
		return executionThread != null;
	}

	protected abstract void doRunOneSingleStep() throws Throwable;

	public final void runOneSingleStep() throws RuntimeException,
			InterruptedException {
		if (isCurrentlyRunning()
				&& !(getCurrentOwnerThread().equals(Thread.currentThread()))) {
			throw new IllegalThreadStateException(
					"This runnable is already running for another thread,"
							+ " step by step processing is currently only"
							+ " permitted to the current owner Thread!");
		}
		try {
			doRunOneSingleStep();
		} catch (Throwable caughtThrowable) {
			throw new RuntimeException(
					"failure in one step of step-by-step processing!",
					caughtThrowable);
		}
	}

	public final Thread getCurrentOwnerThread() {
		return executionThread;
	}

	/**
	 * 
	 * @throws SecurityException
	 *             if interrupting the execution thread is not permitted.
	 */
	public final void stopWorking() throws SecurityException {
		this.continueRunning = false;
		if (executionThread != null) {
			this.executionThread.interrupt(); // FIXME : Nicht sich selbst
												// interrupten!!!! Execution
												// anders lagern! (siehe
												// benutzung im DDA)
		}
		while (isCurrentlyRunning()) {
			Thread.yield();
		}
	}

	/**
	 * Wartet bis fertig.
	 * 
	 * @throws InterruptedException
	 */
	public void joinThread() throws InterruptedException {
		while (continueRunning && !isCurrentlyRunning()) {
			Thread.yield();
		}
		if (isCurrentlyRunning()) {
			executionThread.join();
		}
	}
}
