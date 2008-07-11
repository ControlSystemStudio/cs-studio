package org.csstudio.nams.common.service;

/**
 * This runnable is to be used for step-by-step processing based operations.
 * 
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 */
public abstract class StepByStepProcessor implements Runnable {

	private volatile Thread executionThread = null;
	private volatile boolean continueRunning = true;

	public final void run() {
		if (this.executionThread != null) {
			throw new IllegalThreadStateException(
					"This runnable is already running!");
		}
		this.executionThread = Thread.currentThread();
		this.continueRunning = true;

		while (this.continueRunning) {
			try {
				this.runOneSingleStep();
				Thread.yield();
			} catch (final InterruptedException e) {
				// This is expected behavior to stop this runnable, so nothing
				// to do here!
			}
		}

		this.executionThread = null;
	}

	public final boolean isCurrentlyRunning() {
		return this.executionThread != null;
	}

	/**
	 * Runs one and only one unit of work which is able to be executed as one
	 * transaction (that may be interrupted!).
	 * 
	 * @throws Throwable
	 *             An unexpected error/exception occured.
	 * @throws InterruptedException
	 *             Indicates that processing was "normally" interrupted.
	 */
	protected abstract void doRunOneSingleStep() throws Throwable,
			InterruptedException;

	/**
	 * Called internally to indicate that no further steps are required.
	 */
	protected void done() {
		this.continueRunning = false;
	}
	
	/**
	 * Executes exactly one step. Should not be called directly outside from
	 * tests. Call {@link #done()} to complete.
	 * 
	 * @throws RuntimeException
	 *             An unexpected error/exception occurred which will be cause of
	 *             the thrown {@link RuntimeException}.
	 * @throws InterruptedException
	 *             Indicates that processing was "normally" interrupted.
	 */
	public final void runOneSingleStep() throws RuntimeException,
			InterruptedException {
		if (this.isCurrentlyRunning()
				&& !(this.getCurrentOwnerThread()
						.equals(Thread.currentThread()))) {
			throw new IllegalThreadStateException(
					"This runnable is already running for another thread,"
							+ " step by step processing is currently only"
							+ " permitted to the current owner Thread!");
		}
		try {
			this.doRunOneSingleStep();
		} catch (final InterruptedException ie) {
			ie.fillInStackTrace();
			throw ie;
		} catch (final Throwable caughtThrowable) {
			throw new RuntimeException(
					"failure in one step of step-by-step processing!",
					caughtThrowable);
		}
	}

	/**
	 * Gets the {@link Thread} that currently runns this
	 * {@link StepByStepProcessor}.
	 * 
	 * @return The {@link Thread} or null, if this processor is currently not
	 *         running.
	 * @see #isCurrentlyRunning()
	 */
	public final Thread getCurrentOwnerThread() {
		return this.executionThread;
	}

	/**
	 * Stops the work of this processor and blocks until pending work-step is
	 * done. A running step will may receive an {@link InterruptedException}
	 * 
	 * @throws SecurityException
	 *             If interrupting of the execution thread of this processor is
	 *             not permitted for the calling {@link Thread}.
	 */
	public final void stopWorking() throws SecurityException {
		this.continueRunning = false;
		if (this.executionThread != null) {
			this.executionThread.interrupt();
		}
		while (this.isCurrentlyRunning()) {
			Thread.yield();
		}
	}

	/**
	 * Waits to this processor to completely finishing work or to be interrupted
	 * on work by another {@link Thread}.
	 * 
	 * @throws InterruptedException
	 *             If waiting has been interupted by another {@link Thread}
	 *             holding waitings {@link Thread} monitor.
	 */
	public void joinThread() throws InterruptedException {
		while (this.continueRunning && !this.isCurrentlyRunning()) {
			Thread.yield();
		}
		if (this.isCurrentlyRunning()) {
			this.executionThread.join();
		}
	}
}
