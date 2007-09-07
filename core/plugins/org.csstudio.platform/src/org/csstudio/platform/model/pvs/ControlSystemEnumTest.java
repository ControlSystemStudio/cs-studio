/**
 * 
 */
package org.csstudio.platform.model.pvs;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link ControlSystemEnum}.
 * 
 * @author Sven Wende
 * 
 */
public class ControlSystemEnumTest {

	/**
	 * Set up.
	 */
	@Before
	public void setUp() {
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.model.pvs.ControlSystemEnum#getPrefix()}.
	 */
	@Test
	public void testGetPrefix() {
		for (ControlSystemEnum cs : ControlSystemEnum.values()) {
			assertNotNull(cs.getPrefix());
		}
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.model.pvs.ControlSystemEnum#getResponsibleDalPlugId()}.
	 */
	@Test
	public void testGetResponsibleDalPlugId() {
		for (ControlSystemEnum cs : ControlSystemEnum.values()) {
			if (cs.isSupportedByDAL()) {
				assertNotNull(cs.getResponsibleDalPlugId());
			} else {
				assertNull(
						cs.name()
								+ " is not supported by DAL but provides a DAL plug ID which is "
								+ cs.getResponsibleDalPlugId(), cs.getResponsibleDalPlugId());
			}
		}
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.model.pvs.ControlSystemEnum#findByPrefix(java.lang.String)}.
	 */
	@Test
	public void testFindByPrefix() {
		for (ControlSystemEnum cs : ControlSystemEnum.values()) {
			ControlSystemEnum csFound = cs.findByPrefix(cs.getPrefix());

			assertEquals(csFound, cs);
		}
	}
}
