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
	private IValue nextBaseValue;
	private IValue nextAverageValue;
	
	/**
	 * Constructor of KBLogValueIterator.
	 * 
	 * @param base Instance of KBLogValueIterator.
	 * @param startTime The beginning time of the time range.
	 * @param stepSecond Time step.
	 */
	public KBLogAveragedValueIterator(KBLogRawValueIterator base, ITimestamp startTime, int stepSecond) {
		this.base = base;
		this.currentTime = startTime;
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
		
		double avg = 0.0;
		long count = 0;
		double min = 0;
		double max = 0;
		
		ITimestamp nextTime = TimestampFactory.createTimestamp(currentTime.seconds() + stepSecond, currentTime.nanoseconds());

		while (nextBaseValue != null) {
			ITimestamp time = nextBaseValue.getTime();
			
			if (time.isLessThan(currentTime)) {
				// TODO throw exception as the data is not ordered in time.
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
			} else {
				// TODO debug here whether "Connected" entry is ignored.
			}
			
			try {
				if (base.hasNext())
					nextBaseValue = base.next();
				else
					nextBaseValue = null;
			} catch (Exception ex) {
				Logger.getLogger(Activator.ID).log(Level.SEVERE,
						"Fatal error while calculating average values.", ex);				
				
				nextBaseValue = null;
				return null;
			}
		}
		
		// Next time step
		currentTime = nextTime;
		
		if (count == 0) {
			// No value in this time step
			return null;			
		} else {
			ITimestamp midTime = TimestampFactory.fromDouble(currentTime.toDouble() + (double)stepSecond / 2.0);

			return ValueFactory.createMinMaxDoubleValue(midTime,
					ValueFactory.createOKSeverity(),
					KBLogMessages.StatusNormal,
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
		
		nextAverageValue = calculateNextValue();
		while (nextAverageValue == null && nextBaseValue != null)
			nextAverageValue = calculateNextValue();
		
		return ret;
	}

	@Override
	public synchronized void close() {
		System.err.println("KBLogAveragedValueIterator.close() is requested.");
		base.close();
	}
	
	@Override
	public synchronized boolean isClosed() {
		return base.isClosed();
	}
}
