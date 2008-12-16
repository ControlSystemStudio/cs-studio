package org.csstudio.dct.model.commands;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.internal.Prototype;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link ChangeNameCommand}.
 * 
 * @author Sven Wende
 * 
 */
public class ChangeNameCommandTest {
	private IElement element;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		element = new Prototype("test", UUID.randomUUID());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.model.commands.ChangeNameCommand#execute()} .
	 */
	@Test
	public final void testExecute() {
		String name = "name";
		String newName = "newname";

		// .. before
		element.setName(name);
		assertEquals(name, element.getName());

		// .. execute
		ChangeNameCommand cmd = new ChangeNameCommand(element, newName);
		cmd.execute();
		assertEquals(newName, element.getName());

		// .. undo
		cmd.undo();
		assertEquals(name, element.getName());

	}
}
