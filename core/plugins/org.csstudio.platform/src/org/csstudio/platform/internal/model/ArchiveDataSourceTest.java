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

import static org.junit.Assert.*;

import org.csstudio.platform.model.IArchiveDataSource;
import org.junit.Test;

/**
 * Test class for {@link org.csstudio.platform.internal.model.ArchiveDataSource}.
 * 
 * @author awill
 * 
 */
public class ArchiveDataSourceTest {

	/**
	 * Test method for
	 * {@link org.csstudio.platform.internal.model.ArchiveDataSource#toString()}.
	 */
	@Test
	public final void testToString() {
		IArchiveDataSource ads = new ArchiveDataSource(
				"test_url", 0, "test_name"); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Archive 'test_url' (0, 'test_name')", ads.toString()); //$NON-NLS-1$
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.internal.model.ArchiveDataSource#getUrl()}.
	 */
	@Test
	public final void testGetUrl() {
		IArchiveDataSource ads = new ArchiveDataSource(
				"test_url", 0, "test_name"); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("test_url", ads.getUrl()); //$NON-NLS-1$
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.internal.model.ArchiveDataSource#getKey()}.
	 */
	@Test
	public final void testGetKey() {
		IArchiveDataSource ads = new ArchiveDataSource(
				"test_url", 0, "test_name"); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(0, ads.getKey());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.internal.model.ArchiveDataSource#getTypeId()}.
	 */
	@Test
	public final void testGetTypeId() {
		IArchiveDataSource ads = new ArchiveDataSource(
				"test_url", 0, "test_name"); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(IArchiveDataSource.TYPE_ID, ads.getTypeId());
	}

}
