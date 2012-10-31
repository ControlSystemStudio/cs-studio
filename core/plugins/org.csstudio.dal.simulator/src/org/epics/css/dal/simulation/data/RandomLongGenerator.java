package org.epics.css.dal.simulation.data;

import java.util.Random;

import org.csstudio.dal.DataExchangeException;
import org.epics.css.dal.simulation.ValueProvider;

/**
 * 
 * <code>RandomLongGenerator</code> generates random long values.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class RandomLongGenerator implements ValueProvider<Long>{
	private long min;
	private long max;
	private Random random;

	/**
	 * Constructor.
	 * 
	 * @param options (min,max)
	 */
	public RandomLongGenerator(String[] options) {
		init(options);
		random = new Random(System.currentTimeMillis());
	}

	protected void init(String[] options) {
		try {
			min = Long.parseLong(options[0]);
		} catch (NumberFormatException nfe) {
			min = 0;
		}

		try {
			max = Long.parseLong(options[1]);
		} catch (NumberFormatException nfe) {
			max = 1;
		}

		if (min > max) {
			long tmp = min;
			min = max;
			max = tmp;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.simulation.ValueProvider#get()
	 */
	public Long get() throws DataExchangeException {
		return min + ((max - min) * random.nextLong());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.simulation.ValueProvider#set(java.lang.Object)
	 */
	public void set(Long value) throws DataExchangeException {
		//ignore; this is random number generator
	}
}
