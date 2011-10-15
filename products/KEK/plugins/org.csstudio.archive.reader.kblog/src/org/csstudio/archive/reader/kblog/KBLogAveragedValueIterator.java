package org.csstudio.archive.reader.kblog;

import java.io.InputStream;

import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;

/**
 * Averaging sample iterator for KBLog.
 * 
 * This iterator reads values from "kblogrd" and calculate average/min/max values of each time step,
 * and return them.
 *
 * @author Takashi Nakamoto
 */
public class KBLogAveragedValueIterator extends KBLogValueIterator {
	private int stepSecond;
	private ITimestamp currentTime;
	
	/**
	 * Constructor of KBLogValueIterator.
	 * 
	 * @param kblogrdStdOut InputStream obtained from the standard output of "kblogrd". This output must include all archived values in a given time range.
	 * @param name PVName
	 * @param commandId Unique ID of executed "kblogrd" command.
	 * @param startTime The beginning time of the time range.
	 * @param stepSecond Time step.
	 */
	public KBLogAveragedValueIterator(InputStream kblogrdStdOut, String name,
			int commandId, ITimestamp startTime, int stepSecond) {
		super(kblogrdStdOut, name, commandId);
		this.stepSecond = stepSecond;
		this.currentTime = startTime;
	}

	@Override
	public synchronized boolean hasNext() {
		// TODO Need to implement
		return false;
	}

	@Override
	public synchronized IValue next() throws Exception {
		// TODO Need to implement
		return null;
	}
}
