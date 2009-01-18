package org.csstudio.dct.model.commands;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.csstudio.dct.model.IPropertyContainer;
import org.csstudio.dct.model.internal.Prototype;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link ChangePropertyValueCommand}.
 * 
 * @author Sven Wende
 * 
 */
public final class ChangePropertyValueCommandTest {
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
	 * {@link org.csstudio.dct.model.commands.ChangePropertyValueCommand#execute()}
	 * .
	 */
	@Test
	public void testExecute() {
		String id = "test";
		String value = "value";
		String newValue = "newvalue";

		// .. before
		container.addProperty(id, value);
		assertEquals(value, container.getProperty(id));

		// .. execute
		ChangePropertyValueCommand cmd = new ChangePropertyValueCommand(container, id, newValue);
		cmd.execute();
		assertEquals(newValue, container.getProperty(id));

		// .. undo
		cmd.undo();
		assertEquals(value, container.getProperty(id));

	}
}
