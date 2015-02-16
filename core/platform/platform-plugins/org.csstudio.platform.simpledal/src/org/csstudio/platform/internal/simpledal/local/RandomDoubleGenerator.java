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
package org.csstudio.platform.internal.simpledal.local;

import java.util.Random;

/**
 * Generator for random double values.
 * 
 * @author swende
 * 
 */
public class RandomDoubleGenerator extends AbstractDataGenerator<Double> {
	private double _min;
	private double _max;
	private Random _random;

	/**
	 * Constructor.
	 * @param localChannel the local channel
	 * @param defaultPeriod the default period
	 * @param options
	 */
	public RandomDoubleGenerator(LocalChannel localChannel, int defaultPeriod,
			String[] options) {
		super(localChannel, defaultPeriod, options);
		_random = new Random(System.currentTimeMillis()
				+ localChannel.hashCode());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void init(String[] options) {
		try {
			_min = Double.parseDouble(options[0]);
		} catch (NumberFormatException nfe) {
			_min = 0;
		}

		try {
			_max = Double.parseDouble(options[1]);
		} catch (NumberFormatException nfe) {
			_max = 1;
		}

		try {
			int period = Integer.parseInt(options[2]);
			setPeriod(period);
		} catch (NumberFormatException nfe) {
			// ignore
		}

		if (_min > _max) {
			double tmp = _min;
			_min = _max;
			_max = tmp;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Double generateNextValue() {
		double d = _random.nextDouble();

		double result = _min + ((_max - _min) * d);

		return result;
	}

}
