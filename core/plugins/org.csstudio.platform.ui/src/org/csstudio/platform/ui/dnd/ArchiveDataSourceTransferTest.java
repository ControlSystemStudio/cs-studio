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
