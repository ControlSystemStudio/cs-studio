package org.csstudio.dct.model.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.internal.Folder;
import org.csstudio.dct.model.internal.Prototype;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link AddPrototypeCommand}.
 * 
 * @author Sven Wende
 * 
 */
public final class AddPrototypeCommandTest extends AbstractTestCommand {
	private IPrototype prototype;
	private IFolder folder;

	/**
	 *{@inheritDoc}
	 */
	@Before
	public void doSetUp() throws Exception {
		prototype = new Prototype("test", UUID.randomUUID());
		folder = new Folder("test");
	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.model.commands.AddPrototypeCommand#execute()} .
	 */
	@Test
	public void testExecute() {
		// .. before
		assertNull(prototype.getParentFolder());
		assertFalse(folder.getMembers().contains(prototype));

		// .. execute
		AddPrototypeCommand cmd = new AddPrototypeCommand(folder, prototype);
		cmd.execute();
		assertEquals(folder, prototype.getParentFolder());
		assertTrue(folder.getMembers().contains(prototype));

		// undo
		cmd.undo();
		assertNull(prototype.getParentFolder());
		assertFalse(folder.getMembers().contains(prototype));
	}

}
