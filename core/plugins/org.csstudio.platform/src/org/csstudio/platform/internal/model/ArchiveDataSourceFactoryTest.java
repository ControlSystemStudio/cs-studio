/**
 * Owned by DESY.
 */
package org.csstudio.platform.internal.model;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

import org.csstudio.platform.model.IArchiveDataSource;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link ArchiveDataSourceFactory}.
 * 
 * @author Sven Wende
 * 
 */
public final class ArchiveDataSourceFactoryTest {

	/**
	 * Sample factory.
	 */
	private ArchiveDataSourceFactory _factory;

	/**
	 * Sample process variable.
	 */
	private IArchiveDataSource _archiveDataSource;

	/**
	 */
	@Before
	public void setUp() {
		_factory = new ArchiveDataSourceFactory();
		_archiveDataSource = new ArchiveDataSource("url1", 1, "name1");
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.internal.model.ProcessVariableFactory#createStringRepresentationFromItem(org.csstudio.platform.model.IProcessVariable)}.
	 */
	@Test
	public void testCreateStringRepresentationFromItemIProcessVariable() {
		String s = _factory
				.createStringRepresentationFromItem(_archiveDataSource);
		assertNotNull(s);
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.internal.model.ProcessVariableFactory#createItemFromStringRepresentation(java.lang.String)}.
	 */
	@Test
	public void testCreateItemFromStringRepresentationString() {
		String s = _factory
				.createStringRepresentationFromItem(_archiveDataSource);
		assertNotNull(s);
		IArchiveDataSource archiveDataSource = _factory
				.createItemFromStringRepresentation(s);
		assertNotNull(archiveDataSource);
		assertEquals(_archiveDataSource.getName(), archiveDataSource.getName());
		assertEquals(_archiveDataSource.getUrl(), archiveDataSource.getUrl());
		assertTrue(_archiveDataSource.getKey()==_archiveDataSource.getKey());
		
	}

}
