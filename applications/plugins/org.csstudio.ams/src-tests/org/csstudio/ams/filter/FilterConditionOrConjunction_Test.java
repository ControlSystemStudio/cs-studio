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

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

public class FilterConditionOrConjunction_Test extends AbstractConjunctionFilterConditionTestCase {

	private FilterConditionOrConjunction _filterConditionOrConjunction;
	private FilterConditionMock _firstOperand;
	private FilterConditionMock _secondOperand;

	@Override
	protected void doAdditionalSetUp() throws Throwable {
		_filterConditionOrConjunction.doInit(_firstOperand , _secondOperand );
	}

	@Override	
	protected void doAdditionalTearDown() throws Throwable  {
		_filterConditionOrConjunction = null;
		_firstOperand = null;
		_secondOperand = null;
	}

	@Test
	public void testMatch() {
		_firstOperand.setNextResult(false);
		_secondOperand.setNextResult(false);
		
		assertFalse(_filterConditionOrConjunction.match(new MockMapMessage() {}));
		
		_firstOperand.setNextResult(true);
		_secondOperand.setNextResult(false);
		assertTrue(_filterConditionOrConjunction.match(new MockMapMessage() {}));
		
		_firstOperand.setNextResult(false);
		_secondOperand.setNextResult(true);
		assertTrue(_filterConditionOrConjunction.match(new MockMapMessage() {}));
		
		_firstOperand.setNextResult(true);
		_secondOperand.setNextResult(true);
		assertTrue(_filterConditionOrConjunction.match(new MockMapMessage() {}));
		
		_firstOperand.throwSomethingOnNextMatch(new RuntimeException("Just a test!"));
		assertTrue(_filterConditionOrConjunction.match(new MockMapMessage() {}));
		
		_secondOperand.throwSomethingOnNextMatch(new RuntimeException("Just a test!"));
		assertTrue(_filterConditionOrConjunction.match(new MockMapMessage() {}));
	}

	@Override
	public AbstractConjunctionFilterCondition getNewFilterConjunctionConditionInstance() {
		_filterConditionOrConjunction = new FilterConditionOrConjunction();
		return _filterConditionOrConjunction;
	}

	@Override
	public IFilterCondition getNewFirstOperandInstance() {
		_firstOperand = new FilterConditionMock();
		return _firstOperand;
	}

	@Override
	public IFilterCondition getNewSecondOperandInstance() {
		_secondOperand = new FilterConditionMock();
		return _secondOperand;
	}
}
