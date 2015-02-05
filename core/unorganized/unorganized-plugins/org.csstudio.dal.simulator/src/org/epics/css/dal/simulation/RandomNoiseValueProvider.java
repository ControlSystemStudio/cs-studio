/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

/**
 *
 */
package org.epics.css.dal.simulation;

import org.csstudio.dal.DataExchangeException;


/**
 * @author ikriznar
 *
 */
public class RandomNoiseValueProvider<T> implements ValueProvider<T>
{
	public double changeScale = 0.1;
	public Double dval;
	public Long lval;

	/**
	 * Random value provider for Double values.
	 */
	public RandomNoiseValueProvider(Double value, double scale)
	{
		super();
		dval = value;
		changeScale = scale;
	}

	/**
	 * Random value provider for Long values.
	 */
	public RandomNoiseValueProvider(Long value, double scale)
	{
		super();
		lval = value;
		changeScale = scale;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.simulation.ValueProvider#get()
	 */
	public T get() throws DataExchangeException
	{
		boolean positive = Math.random() > 0.5;
		double random = Math.random();

		if (dval != null) {
			if (positive) {
				return (T)new Double(dval * (1.0 + random * changeScale));
			}

			return (T)new Double(dval * (1.0 - random * changeScale));
		}

		long step = (long)(Math.max(1, (long)(lval * changeScale)) * random);

		if (positive) {
			return (T)new Long(lval + step);
		}

		return (T)new Long(lval - step);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.simulation.ValueProvider#set(T)
	 */
	public void set(T value) throws DataExchangeException
	{
		if (dval != null) {
			dval = (Double)value;
		}

		lval = (Long)value;
	}
}

/* __oOo__ */
