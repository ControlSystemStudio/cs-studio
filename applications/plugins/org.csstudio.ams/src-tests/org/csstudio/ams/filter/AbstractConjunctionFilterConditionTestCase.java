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
 package org.csstudio.ams.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

abstract public class AbstractConjunctionFilterConditionTestCase {

	private AbstractConjunctionFilterCondition _filterCondition;
	private IFilterCondition _currentFirstOperandInstance;
	private IFilterCondition _currentSecondOperandInstance;

	@Before
	public final void setUp() throws Throwable {
		_filterCondition = getNewFilterConjunctionConditionInstance();
		_currentFirstOperandInstance = getNewFirstOperandInstance();
		_currentSecondOperandInstance = getNewSecondOperandInstance();
		_filterCondition.doInit(_currentFirstOperandInstance,
				_currentSecondOperandInstance);
		doAdditionalSetUp();
	}

	protected void doAdditionalSetUp() throws Throwable {
		// By default nothing to do.
	}

	protected void doAdditionalTearDown() throws Throwable  {
		// By default nothing to do.
	}

	public abstract IFilterCondition getNewFirstOperandInstance();

	public abstract IFilterCondition getNewSecondOperandInstance();

	public abstract AbstractConjunctionFilterCondition getNewFilterConjunctionConditionInstance();

	@After
	public final void tearDown() throws Throwable {
		doAdditionalTearDown();
		_filterCondition = null;
		_currentFirstOperandInstance = null;
		_currentSecondOperandInstance = null;
	}

	@Test
	public final void testGetCunjunctedOperands() {
		IFilterCondition[] condiotions = _filterCondition
				.getCunjunctedOperands();
		assertEquals(2, condiotions.length);

		List<IFilterCondition> arrayAsList = new ArrayList<IFilterCondition>(2);
		arrayAsList.add(condiotions[0]);
		arrayAsList.add(condiotions[1]);

		assertTrue(arrayAsList.contains(_currentFirstOperandInstance));
		assertTrue(arrayAsList.contains(_currentSecondOperandInstance));
	}

	public AbstractConjunctionFilterCondition getFilterCondition() {
		return _filterCondition;
	}

	public IFilterCondition getCurrentFirstOperandInstance() {
		return _currentFirstOperandInstance;
	}

	public IFilterCondition getCurrentSecondOperandInstance() {
		return _currentSecondOperandInstance;
	}
}
