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
 * @see {@link ArchiveDataSourceTransfer}.
 * @author swende
 * 
 */
public final class ArchiveDataSourceTransferTest {
	/**
	 * Test method for
	 * {@link org.csstudio.platform.ui.dnd.ArchiveDataSourceTransfer#getTypeIds()}.
	 */
	@Test
	public void testGetTypeIds() {
		int[] typeIds = ArchiveDataSourceTransfer.getInstance().getTypeIds();
		assertNotNull(typeIds);
		assertTrue(typeIds.length > 0);
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.ui.dnd.ArchiveDataSourceTransfer#getInstance()}.
	 */
	@Test
	public void testGetInstance() {
		assertNotNull(ArchiveDataSourceTransfer.getInstance());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.ui.dnd.ArchiveDataSourceTransfer#getTypeNames()}.
	 */
	@Test
	public void testGetTypeNames() {
		String[] typeNames = ArchiveDataSourceTransfer.getInstance()
				.getTypeNames();
		assertNotNull(typeNames);
		assertTrue(typeNames.length > 0);

		for (String name : typeNames) {
			assertNotNull(name);
		}
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.ui.dnd.ArchiveDataSourceTransfer#javaToNative(java.lang.Object, org.eclipse.swt.dnd.TransferData)}.
	 */
	@Test
	public void testJavaToNativeObjectTransferData() {
		// not safely testable, because of platform specific needs
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.ui.dnd.ArchiveDataSourceTransfer#nativeToJava(org.eclipse.swt.dnd.TransferData)}.
	 */
	@Test
	public void testNativeToJavaTransferData() {
		// not safely testable, because of platform specific needs
	}

}
