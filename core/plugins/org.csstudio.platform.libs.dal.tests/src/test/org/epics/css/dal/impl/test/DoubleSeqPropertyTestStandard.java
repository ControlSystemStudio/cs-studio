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

package org.epics.css.dal.impl.test;

import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DoubleSeqProperty;
import org.epics.css.dal.NumericPropertyCharacteristics;


public abstract class DoubleSeqPropertyTestStandard
	extends DynamicValuePropertyTest
{
	public DoubleSeqPropertyTestStandard()
	{
		super(DoubleSeqProperty.class);
	}

	protected abstract int getSequenceLength();

	@Override
	public Object getRandomValue()
	{
		int length = getSequenceLength();
		double[] dSeq = new double[length];

		for (int i = 0; i < length; i++) {
			dSeq[i] = (Math.random() * 10);
		}

		return dSeq;
	}

	@Override
	public boolean matchValue(Object expected, Object got)
	{
		assertEquals(got.getClass(), double[].class);

		int length = getSequenceLength();

		for (int i = 0; i < length; i++) {
			assertEquals(((double[])expected)[i], ((double[])got)[i], 4);
		}

		return true;
	}

	@Override
	public void testGetDataType()
	{
		assertEquals(getProperty().getDataType(), double[].class);
	}
	
	public void testGetCharacteristics() throws Exception, InterruptedException {
		super.testGetCharacteristics();
		DoubleSeqProperty property = (DoubleSeqProperty)getProperty();
		
		Double max = property.getMaximum();
		Object expectedCharacteristicValue = getExpectedCharacteristicValue(NumericPropertyCharacteristics.C_MAXIMUM);
		if (expectedCharacteristicValue != null) {
			assertEquals(expectedCharacteristicValue, max);
		}
		
		Double min = property.getMinimum();
		expectedCharacteristicValue = getExpectedCharacteristicValue(NumericPropertyCharacteristics.C_MINIMUM);
		if (expectedCharacteristicValue != null) {
			assertEquals(expectedCharacteristicValue, min);
		}
		
	}
}

/* __oOo__ */
