/**
 * 
 */
package org.csstudio.platform.ui.dnd;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class.
 * 
 * @see {@link ProcessVariableTransfer}.
 * @author swende
 * 
 */
public final class ProcessVariableTransferTest {
	/**
	 * Test method for
	 * {@link org.csstudio.platform.ui.dnd.ProcessVariableTransfer#getTypeIds()}.
	 */
	@Test
	public void testGetTypeIds() {
		int[] typeIds = ProcessVariableTransfer.getInstance().getTypeIds();
		assertNotNull(typeIds);
		assertTrue(typeIds.length > 0);
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.ui.dnd.ProcessVariableTransfer#getInstance()}.
	 */
	@Test
	public void testGetInstance() {
		assertNotNull(ProcessVariableTransfer.getInstance());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.ui.dnd.ProcessVariableTransfer#getTypeNames()}.
	 */
	@Test
	public void testGetTypeNames() {
		String[] typeNames = ProcessVariableTransfer.getInstance()
				.getTypeNames();
		assertNotNull(typeNames);
		assertTrue(typeNames.length > 0);

		for (String name : typeNames) {
			assertNotNull(name);
		}
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.ui.dnd.ProcessVariableTransfer#javaToNative(java.lang.Object, org.eclipse.swt.dnd.TransferData)}.
	 */
	@Test
	public void testJavaToNativeObjectTransferData() {
		// not safely testable, because of platform specific needs
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.ui.dnd.ProcessVariableTransfer#nativeToJava(org.eclipse.swt.dnd.TransferData)}.
	 */
	@Test
	public void testNativeToJavaTransferData() {
		// not safely testable, because of platform specific needs
	}

}
