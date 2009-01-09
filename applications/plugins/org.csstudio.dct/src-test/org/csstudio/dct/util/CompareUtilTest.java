/**
 * 
 */
package org.csstudio.dct.util;

import static org.junit.Assert.*;

import java.util.UUID;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IVisitor;
import org.csstudio.dct.model.internal.AbstractElement;
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
	 * Test method for
	 * {@link org.csstudio.dct.util.CompareUtil#equals(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public final void testEquals() {
		assertTrue(CompareUtil.equals(null, null));
		assertTrue(CompareUtil.equals("", ""));
		assertTrue(CompareUtil.equals("a", "a"));
		assertFalse(CompareUtil.equals(null, ""));
		assertFalse(CompareUtil.equals("", null));
		assertFalse(CompareUtil.equals("a", null));
		assertFalse(CompareUtil.equals("a", "b"));
		assertFalse(CompareUtil.equals("b", "a"));
		assertFalse(CompareUtil.equals("", "a"));

	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.util.CompareUtil#equals(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public final void testIdsEquals() {
		UUID id1 = UUID.randomUUID();
		UUID id2 = UUID.randomUUID();
		IElement element1 = new AbstractElement("e1", id1) {
			public void accept(IVisitor visitor) {
			}
		};

		IElement element2 = new AbstractElement("e2", id2) {
			public void accept(IVisitor visitor) {
			}
		};

		assertTrue(CompareUtil.idsEqual(null, null));
		assertTrue(CompareUtil.idsEqual(element1, element1));
		assertTrue(CompareUtil.idsEqual(element2, element2));
		assertFalse(CompareUtil.idsEqual(null,element1));
		assertFalse(CompareUtil.idsEqual(element1, null));
		assertFalse(CompareUtil.idsEqual(element1, element2));
	}

}
