package org.csstudio.dct.model.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.internal.Folder;
import org.csstudio.dct.model.internal.Instance;
import org.csstudio.dct.model.internal.Prototype;
import org.csstudio.dct.model.internal.RecordFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link RemoveInstanceCommand}.
 * 
 * @author Sven Wende
 * 
 */
public class RemoveInstanceCommandTest {
	private IPrototype prototypeA;
	private IPrototype prototypeB;
	private IInstance instanceA;
	private IFolder folder;

	@Before
	public void setUp() throws Exception {
		prototypeA = new Prototype("prototypeA");
		
		new AddRecordCommand(prototypeA, RecordFactory.createRecord("ai", "RecordA1")).execute();
		
		instanceA = new Instance(prototypeA);
		prototypeB = new Prototype("prototypeB");
		folder = new Folder("folder");
	}

	/**
	 * Test method for {@link RemoveInstanceCommand}.
	 */
	@Test
	public final void testRemoveFromFolder() {
		// .. prepare
		folder.addMember(instanceA);
		instanceA.setParentFolder(folder);
		
		// .. before
		assertEquals(folder, instanceA.getParentFolder());
		assertTrue(folder.getMembers().contains(instanceA));
		assertNull(instanceA.getContainer());
		
		// .. execute
		RemoveInstanceCommand cmd = new RemoveInstanceCommand(instanceA);
		cmd.execute();

		assertNull(instanceA.getParentFolder());
		assertFalse(folder.getMembers().contains(instanceA));
		assertNull(instanceA.getContainer());
		
		// .. undo
		cmd.undo();
		
		assertEquals(folder, instanceA.getParentFolder());
		assertTrue(folder.getMembers().contains(instanceA));
		assertNull(instanceA.getContainer());
	}
	

	/**
	 * Test method for {@link RemoveInstanceCommand}.
	 */
	@Test
	public final void testRemoveFromContainer() {
		// .. prepare
		new AddInstanceCommand(prototypeB, instanceA).execute();
		
		// .. before
		assertNull(instanceA.getParentFolder());
		assertEquals(prototypeB, instanceA.getContainer());
		assertTrue(prototypeB.getInstances().contains(instanceA));
		
		// .. execute
		RemoveInstanceCommand cmd = new RemoveInstanceCommand(instanceA);
		cmd.execute();
		
		assertNull(instanceA.getParentFolder());
		assertNull(instanceA.getContainer());
		assertFalse(prototypeB.getInstances().contains(instanceA));
		
		// .. undo
		cmd.undo();

		assertNull(instanceA.getParentFolder());
		assertEquals(prototypeB, instanceA.getContainer());
		assertTrue(prototypeB.getInstances().contains(instanceA));
	}
}
