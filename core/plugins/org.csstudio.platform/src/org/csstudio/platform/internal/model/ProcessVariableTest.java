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
package org.csstudio.platform.internal.model;

import static org.junit.Assert.assertEquals;

import org.csstudio.platform.model.IProcessVariable;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link org.csstudio.platform.internal.model.ProcessVariable}.
 * 
 * @author swende
 * 
 */
public final class ProcessVariableTest {

	/**
	 * @throws java.lang.Exception
	 *             an Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.internal.model.ProcessVariable#ProcessVariable(java.lang.String)}.
	 */
	@Test
	public void testProcessVariable() {

	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.internal.model.ProcessVariable#getName()}.
	 */
	@Test
	public void testGetName() {
		IProcessVariable pv;

		pv = new ProcessVariable("test1"); //$NON-NLS-1$
		assertEquals("test1", pv.getName()); //$NON-NLS-1$

		pv = new ProcessVariable("test2"); //$NON-NLS-1$
		assertEquals("test2", pv.getName()); //$NON-NLS-1$

		pv = new ProcessVariable(""); //$NON-NLS-1$
		assertEquals("", pv.getName()); //$NON-NLS-1$
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.internal.model.ProcessVariable#toString()}.
	 */
	@Test
	public void testToString() {
		IProcessVariable pv;

		pv = new ProcessVariable("test1"); //$NON-NLS-1$
		assertEquals(pv.toString(), pv.getName());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.internal.model.ProcessVariable#getTypeId()}.
	 */
	@Test
	public void testGetTypeId() {
		IProcessVariable pv;

		pv = new ProcessVariable("test1"); //$NON-NLS-1$
		assertEquals(ProcessVariable.TYPE_ID, pv.getTypeId());
	}

}
