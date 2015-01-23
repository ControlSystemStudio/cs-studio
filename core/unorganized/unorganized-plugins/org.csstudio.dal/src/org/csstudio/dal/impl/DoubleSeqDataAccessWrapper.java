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

package org.csstudio.dal.impl;

import org.csstudio.dal.DataAccess;
import org.csstudio.dal.DataExchangeException;
import org.csstudio.dal.DoubleSeqAccess;
import org.csstudio.dal.SequenceAccess;


public class DoubleSeqDataAccessWrapper extends AbstractDataAccessWrapper<double[]>
	implements DoubleSeqAccess
{
	private static final int NUMBER_TO_DOUBLE = 1;

	public DoubleSeqDataAccessWrapper(DataAccess sourceDA)
	{
		super(double[].class, sourceDA);
	}

	protected int getConversion()
	{
		if (valClass.equals(double[].class)) {
			if (sourceDA.getDataType().equals(long[].class)) {
				return NUMBER_TO_DOUBLE;
			}
		}

		return UNKNOWN;
	}

	@Override
	protected Object convertToOriginal(double[] value, DataAccess dataAccess)
	{
		if (value == null) {
			return null;
		}

		long[] l= new long[value.length];
		
		for (int i = 0; i < l.length; i++) {
			l[i]=(long)value[i];
		}
		
		return l;
	}

	@Override
	protected double[] convertFromOriginal(Object value, DataAccess dataAccess)
	{
		if (value == null) {
			return null;
		}
		
		if (value instanceof long[]) {
			long[] l= (long[])value;
			
			double[] d= new double[l.length];
			
			for (int i = 0; i < l.length; i++) {
				d[i]=(double)l[i];
			}
			return d;
		}

		return null;
	}
	
	public int getSequenceLength() throws DataExchangeException {
		return ((SequenceAccess<?>)sourceDA).getSequenceLength();
	}
}

/* __oOo__ */
