package org.csstudio.sds.components.internal.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link MeterElement}.
 * 
 * @author Sven Wende
 *
 */
public final class MeterElementTest {

	/**
	 * A test instance.
	 */
	private MeterElement _meterElement;
	
	/**
	 * Test setup.
	 */
	@Before
	public void setUp() {
		_meterElement = new MeterElement();
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.internal.model.MeterElement#getDoubleTestProperty()}.
	 */
	@Test
	public void testGetDoubleTestProperty() {
		assertNotNull(_meterElement.getDoubleTestProperty());
		assertTrue(_meterElement.hasProperty(_meterElement.getDoubleTestProperty()));
	}

	/**
	 * Test method for {@link org.csstudio.sds.components.internal.model.MeterElement#getTypeID()}.
	 */
	@Test
	public void testGetTypeID() {
		_meterElement.getTypeID().equals(MeterElement.ID);
	}

	/**
	 * Tests, if all properties where properly installed.
	 */
	@Test
	public void testProperties () {
		assertTrue(_meterElement.hasProperty(MeterElement.PROP_VALUE));
		assertTrue(_meterElement.hasProperty(MeterElement.PROP_INTERVAL1_LOWER_BORDER));
		assertTrue(_meterElement.hasProperty(MeterElement.PROP_INTERVAL1_UPPER_BORDER));
		assertTrue(_meterElement.hasProperty(MeterElement.PROP_INTERVAL2_LOWER_BORDER));
		assertTrue(_meterElement.hasProperty(MeterElement.PROP_INTERVAL2_UPPER_BORDER));
		assertTrue(_meterElement.hasProperty(MeterElement.PROP_INTERVAL3_LOWER_BORDER));
		assertTrue(_meterElement.hasProperty(MeterElement.PROP_INTERVAL3_UPPER_BORDER));
		// Add further properties here
	}

}
