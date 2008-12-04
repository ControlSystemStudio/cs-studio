package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.internal.Folder;
import org.csstudio.dct.model.internal.Prototype;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for {@link AddPrototypeCommand}.
 * 
 * @author Sven Wende
 * 
 */
public class AddPrototypeCommandTest {
	private IPrototype prototype;
	private IFolder folder;
	
	@Before
	public void setUp() throws Exception {
		prototype = new Prototype("test");
		folder = new Folder("test");
	}
	
	/**
	 * Test method for
	 * {@link org.csstudio.dct.model.commands.AddPrototypeCommand#execute()}
	 * .
	 */
	@Test
	public final void testExecute() {
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
