package org.csstudio.dct.model.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.UUID;

import org.csstudio.dct.model.IPropertyContainer;
import org.csstudio.dct.model.internal.Prototype;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link ChangePropertyKeyCommand}.
 * 
 * @author Sven Wende
 * 
 */
public final class ChangePropertyKeyCommandTest {
	private IPropertyContainer container;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		container = new Prototype("test", UUID.randomUUID());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.model.commands.ChangePropertyKeyCommand#execute()}
	 * .
	 */
	@Test
	public void testExecute() {
		String id = "test";
		String newId = "newId";
		String value = "a";

		// .. before
		container.addProperty(id, value);
		assertEquals(value, container.getProperty(id));
		assertNull(container.getProperty(newId));
		
		// .. execute
		ChangePropertyKeyCommand cmd = new ChangePropertyKeyCommand(container, id, newId);
		cmd.execute();
		assertNull(container.getProperty(id));
		assertEquals(value, container.getProperty(newId));

		// .. undo
		cmd.undo();
		assertEquals(value, container.getProperty(id));
		assertNull(container.getProperty(newId));

	}
}
