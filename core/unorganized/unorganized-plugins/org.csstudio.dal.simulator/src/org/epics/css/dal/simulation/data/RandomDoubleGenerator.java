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

import java.util.Random;

import org.csstudio.dal.DataExchangeException;
import org.epics.css.dal.simulation.ValueProvider;

/**
 * Generator for random double values.
 * 
 * @author swende
 * 
 */
public class RandomDoubleGenerator implements ValueProvider<Double> {
	private double min;
	private double max;
	private Random random;

	/**
	 * Constructor.
	 * @param options (min, max)
	 */
	public RandomDoubleGenerator(String[] options) {
		init(options);
		random = new Random(System.currentTimeMillis());
	}

	protected void init(String[] options) {
		try {
			min = Double.parseDouble(options[0]);
		} catch (NumberFormatException nfe) {
			min = 0;
		}

		try {
			max = Double.parseDouble(options[1]);
		} catch (NumberFormatException nfe) {
			max = 1;
		}

		if (min > max) {
			double tmp = min;
			min = max;
			max = tmp;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.simulation.ValueProvider#get()
	 */
	public Double get() throws DataExchangeException {
		return min + ((max - min) * random.nextDouble());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.simulation.ValueProvider#set(java.lang.Object)
	 */
	public void set(Double value) throws DataExchangeException {
		//ignore; this is random number generator
	}
}
