package org.csstudio.archive.reader.kblog;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.data.values.IDoubleValue;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;

/**
 * Averaging sample iterator for KBLog.
 * 
 * This iterator reads values from "kblogrd" and calculate average/min/max values of each time step,
 * and return them.
 *
 * @author Takashi Nakamoto
 */
public class KBLogAveragedValueIterator implements KBLogValueIterator {
	private KBLogRawValueIterator base;
	private int stepSecond;
	private ITimestamp currentTime;
	private ITimestamp endTime;
	private IValue nextBaseValue;
	private IValue nextAverageValue;
	
	/**
	 * Constructor of KBLogValueIterator.
	 * 
	 * @param base Instance of KBLogValueIterator.
	 * @param startTime The beginning time of the time range.
	 * @param stepSecond Time step.
	 */
	public KBLogAveragedValueIterator(KBLogRawValueIterator base, ITimestamp startTime, ITimestamp endTime, int stepSecond) {
		this.base = base;
		this.currentTime = startTime;
		this.endTime = endTime;
		
		if (stepSecond < 1)
			this.stepSecond = 1;
		else
			this.stepSecond = stepSecond;
		
		try {
			if (base.hasNext())
				this.nextBaseValue = base.next();
			
			this.nextAverageValue = calculateNextValue();
		} catch (Exception ex) {
			this.nextBaseValue = null;
			this.nextAverageValue = null;
		}
	}
	
	private synchronized IValue calculateNextValue() {
		if (nextBaseValue == null)
			return null;
		
		if (currentTime.isGreaterOrEqual(endTime)) {
			nextBaseValue = null;
			return null;
		}
		
		double avg = 0.0;
		long count = 0;
		double min = 0;
		double max = 0;
		boolean connected = false;
		
		ITimestamp nextTime = TimestampFactory.createTimestamp(currentTime.seconds() + stepSecond, currentTime.nanoseconds());

		try {
			while (nextBaseValue != null) {
				ITimestamp time = nextBaseValue.getTime();
				
				if (time.isLessThan(currentTime)) {
					// A value archived earlier than this time step is found.
					// Ignore this value and continue averaging.
					Logger.getLogger(Activator.ID).log(Level.WARNING,
							"The value transferred from " + base.getPathToKBLogRD() + " (" + base.getCommandID() + ") is not ordered in time.");

					if (base.hasNext())
						nextBaseValue = base.next();
					else
						nextBaseValue = null;
					
					continue;
				}
				
				if (time.isGreaterOrEqual(nextTime)) {
					// The obtained value is the data in the next step, which will be processed
					// when this method is called next time.
					break;
				}
				
				if (nextBaseValue.getSeverity().hasValue()) {
					if (nextBaseValue instanceof IDoubleValue) {
						double val = ((IDoubleValue) nextBaseValue).getValue();
						
						if (count == 0) {
							avg = val;
							max = val;
							min = val;
							count = 1;
						} else {
							avg = (count * avg + val) / (count + 1.0);
							if (val > max)
								max = val;
							if (val < min)
								min = val;
							
							count++;
						}
					} else {
						// TODO support other data types
					}
				}
				
				if (nextBaseValue.getStatus().equals(KBLogMessages.StatusConnected))
					connected = true;
				
				if (base.hasNext())
					nextBaseValue = base.next();
				else
					nextBaseValue = null;
			}
		} catch (Exception ex) {
			Logger.getLogger(Activator.ID).log(Level.SEVERE,
					"Fatal error while calculating average values.", ex);				
			
			nextBaseValue = null;
			return null;
		}

		// Middle time of this time step
		ITimestamp midTime = TimestampFactory.fromDouble(currentTime.toDouble() + (double)stepSecond / 2.0);		
		
		// Next time step
		currentTime = nextTime;
		
		if (count == 0) {
			// No value in this time step
			if (connected) {
				return ValueFactory.createMinMaxDoubleValue(midTime,
						KBLogSeverityInstances.connected,
						KBLogMessages.StatusConnected,
						null,
						IValue.Quality.Interpolated,
						new double[] { 0.0 }, 0.0, 0.0);
			} else {
				return null;
			}
		} else {
			String status = KBLogMessages.StatusNormal;
			if (connected)
				status = KBLogMessages.StatusConnected;
			
			return ValueFactory.createMinMaxDoubleValue(midTime,
					ValueFactory.createOKSeverity(),
					status,
					null,
					IValue.Quality.Interpolated,
					new double[] { avg }, min, max);
		}
	}

	@Override
	public synchronized boolean hasNext() {
		return !(nextAverageValue == null && nextBaseValue == null);
	}

	@Override
	public synchronized IValue next() throws Exception {
		IValue ret = nextAverageValue;
		
		// calculate the average value of the next time step
		nextAverageValue = calculateNextValue();
		
		// If the next time step does not have any value, skip that time step and
		// calculate the average value of next next time step.  
		while (nextAverageValue == null && nextBaseValue != null)
			nextAverageValue = calculateNextValue();
		
		return ret;
	}

	@Override
	public synchronized void close() {
		base.close();
	}
	
	@Override
	public synchronized boolean isClosed() {
		return base.isClosed();
	}
}
