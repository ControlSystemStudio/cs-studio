/**
 * 
 */
package org.csstudio.dct.model.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Sven Wende
 * 
 */
public class AbstractPropertyContainerTest {
	private static final String NAME = "test";
	private static final UUID ID = UUID.randomUUID();
	private AbstractPropertyContainer container;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		container = new AbstractPropertyContainer(NAME, ID) {
			public Map<String, Object> getFinalProperties() {
				return null;
			}
		};
		container.addProperty("p1", "v1");
		container.addProperty("p2", "v2");
	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.model.internal.AbstractPropertyContainer#addProperty(java.lang.String, java.lang.Object)}
	 * .
	 */
	@Test
	public final void testAddProperty() {
		assertNull(container.getProperty("p3"));
		container.addProperty("p3", "v3");
		assertEquals("v3", container.getProperty("p3"));
	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.model.internal.AbstractPropertyContainer#getProperty(java.lang.String)}
	 * .
	 */
	@Test
	public final void testGetProperty() {
		assertEquals("v1", container.getProperty("p1"));
		assertEquals("v2", container.getProperty("p2"));
	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.model.internal.AbstractPropertyContainer#removeProperty(java.lang.String)}
	 * .
	 */
	@Test
	public final void testRemoveProperty() {
		assertEquals("v1", container.getProperty("p1"));
		container.removeProperty("p1");
		assertNull(container.getProperty("p1"));
	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.model.internal.AbstractPropertyContainer#getProperties()}
	 * .
	 */
	@Test
	public final void testGetProperties() {
		Map<String, Object> properties = container.getProperties();
		assertNotNull(properties);
		assertFalse(properties.isEmpty());
		assertEquals(2, properties.size());
		assertTrue(properties.containsKey("p1"));
		assertTrue(properties.containsKey("p2"));
		assertEquals("v1", container.getProperty("p1"));
		assertEquals("v2", container.getProperty("p2"));
	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.model.internal.AbstractPropertyContainer#equals(Object)}
	 * .
	 */
	@Test
	public final void testEqualsHashCode() {
		AbstractPropertyContainer container2 = new AbstractPropertyContainer(NAME, ID) {
			public Map<String, Object> getFinalProperties() {
				return null;
			}
		};

		assertNotSame(container, container2);

		for (String k : container.getProperties().keySet()) {
			container2.addProperty(k, container.getProperty(k));
		}

		assertEquals(container, container2);
	}
}
