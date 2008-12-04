/**
 * 
 */
package org.csstudio.dct.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Sven Wende
 *
 */
public class CompareUtilTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link org.csstudio.dct.util.CompareUtil#equals(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testEqualsStringString() {
		assertTrue(CompareUtil.equals(null, null));
		assertTrue(CompareUtil.equals("",""));
		assertTrue(CompareUtil.equals("a", "a"));
		assertFalse(CompareUtil.equals(null, ""));
		assertFalse(CompareUtil.equals("", null));
		assertFalse(CompareUtil.equals("a", null));
		assertFalse(CompareUtil.equals("a", "b"));
		assertFalse(CompareUtil.equals("b", "a"));
		assertFalse(CompareUtil.equals("", "a"));
		
	}

}
