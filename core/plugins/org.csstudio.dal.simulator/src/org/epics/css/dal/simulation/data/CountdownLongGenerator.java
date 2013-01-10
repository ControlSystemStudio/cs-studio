package org.epics.css.dal.simulation.data;

import org.csstudio.dal.DataExchangeException;
import org.epics.css.dal.simulation.ValueProvider;

/**
 * 
 * <code>CountdownLongGenerator</code> is a countdown generator for long values.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class CountdownLongGenerator implements ValueProvider<Long> {
	private long distance;
	private long from;
	private long to;
	private long countdownPeriod;
	
	private long startMs=-1;
	
	/**
	 * Constructs a new countdown long generator.
	 * 
	 * @param options the options (start, end, period)
	 */
	public CountdownLongGenerator(String[] options) {
		init(options);
	}

	protected void init(String[] options) {
		try {
			from = Long.parseLong(options[0]);
		} catch (NumberFormatException nfe) {
			from = 0;
		}

		try {
			to = Long.parseLong(options[1]);
		} catch (NumberFormatException nfe) {
			to = 1;
		}

		try {
			countdownPeriod = Long.parseLong(options[2]);
		} catch (NumberFormatException nfe) {
			countdownPeriod = 1000;
		}

		if (from < to) {
			long tmp = from;
			from = to;
			to = tmp;
		}
	
		distance = from - to;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.simulation.ValueProvider#get()
	 */
	public Long get() throws DataExchangeException {
		long result = -1;
		
		if(startMs < 0) {
			startMs = System.currentTimeMillis();
		}
		
		long now = System.currentTimeMillis();
		long diff = now-startMs;
		
		
		if(diff>=countdownPeriod) {
			startMs = -1;
			result = from;
		} else {
			double percent = (double) diff/countdownPeriod;
			result = (long)(from - (distance * percent));
		}
		
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.simulation.ValueProvider#set(java.lang.Object)
	 */
	public void set(Long value) throws DataExchangeException {
		//ignore; data generator		
	}
}
