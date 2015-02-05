/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.epics.css.dal.simulation.data;

import org.csstudio.dal.DataExchangeException;
import org.epics.css.dal.simulation.ValueProvider;

/**
 * Generator for countdown of double values.
 * 
 * @author swende
 * 
 */
public class CountdownDoubleGenerator implements ValueProvider<Double> {
	private double distance;
	private double from;
	private double to;
	private long countdownPeriod;
	
	private long startMs=-1;
	
	/**
	 * Constructs a new double countdown generator.
	 * 
	 * @param options the options (start, end, period)
	 */
	public CountdownDoubleGenerator(String[] options) {
		init(options);
	}

	protected void init(String[] options) {
		try {
			from = Double.parseDouble(options[0]);
		} catch (NumberFormatException nfe) {
			from = 0;
		}

		try {
			to = Double.parseDouble(options[1]);
		} catch (NumberFormatException nfe) {
			to = 1;
		}

		try {
			countdownPeriod = Long.parseLong(options[2]);
		} catch (NumberFormatException nfe) {
			countdownPeriod = 1000;
		}

		if (from < to) {
			double tmp = from;
			from = to;
			to = tmp;
		}
	
		distance = from - to;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.simulation.ValueProvider#get()
	 */
	public Double get() throws DataExchangeException {
		double result = -1;
		
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
			result = from - (distance * percent);
		}
		
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.simulation.ValueProvider#set(java.lang.Object)
	 */
	public void set(Double value) throws DataExchangeException {
		//ignore; data generator		
	}

}
