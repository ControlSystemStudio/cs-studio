/**
 * 
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
