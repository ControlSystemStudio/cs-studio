package org.csstudio.dct.util;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.internal.Record;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link AliasResolutionUtil}.
 * 
 * @author Sven Wende
 * 
 */
public final class AliasResolutionUtilTest {
	private IRecord record1;
	private IRecord record2;
	private IRecord record3;

	/**
	 * Setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		record1 = new Record(null, UUID.randomUUID());
		record2 = new Record(record1, UUID.randomUUID());
		record3 = new Record(record2, UUID.randomUUID());
	}

	/**
	 * Test method for
	 * {@link AliasResolutionUtil#getPropertyViaHierarchy(org.csstudio.dct.model.IElement, String)}
	 * ;
	 */
	@Test
	public void testGetPropertyViaHierarchy() {
		record1.setName("x1");
		assertEquals("x1", AliasResolutionUtil.getPropertyViaHierarchy(record1, "name"));
		assertEquals("x1", AliasResolutionUtil.getPropertyViaHierarchy(record2, "name"));
		assertEquals("x1", AliasResolutionUtil.getPropertyViaHierarchy(record3, "name"));

		record2.setName("x2");
		assertEquals("x1", AliasResolutionUtil.getPropertyViaHierarchy(record1, "name"));
		assertEquals("x2", AliasResolutionUtil.getPropertyViaHierarchy(record2, "name"));
		assertEquals("x2", AliasResolutionUtil.getPropertyViaHierarchy(record3, "name"));

		record3.setName("x3");
		assertEquals("x1", AliasResolutionUtil.getPropertyViaHierarchy(record1, "name"));
		assertEquals("x2", AliasResolutionUtil.getPropertyViaHierarchy(record2, "name"));
		assertEquals("x3", AliasResolutionUtil.getPropertyViaHierarchy(record3, "name"));

		record1.setEpicsName("epics1");
		record2.setEpicsName("epics2");
		record3.setEpicsName("epics3");

		assertEquals("epics1", AliasResolutionUtil.getPropertyViaHierarchy(record1, "epicsName"));
		assertEquals("epics2", AliasResolutionUtil.getPropertyViaHierarchy(record2, "epicsName"));
		assertEquals("epics3", AliasResolutionUtil.getPropertyViaHierarchy(record3, "epicsName"));
	}

}
