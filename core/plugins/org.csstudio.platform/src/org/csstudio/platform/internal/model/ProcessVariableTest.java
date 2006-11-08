/**
 * 
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
