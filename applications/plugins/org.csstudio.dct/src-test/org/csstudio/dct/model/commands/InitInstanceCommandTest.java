package org.csstudio.dct.model.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.internal.Instance;
import org.csstudio.dct.model.internal.Prototype;
import org.csstudio.dct.model.internal.RecordFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link InitInstanceCommand}.
 * 
 * @author Sven Wende
 * 
 */
public final class InitInstanceCommandTest extends AbstractTestCommand {
	private Prototype prototypeA;
	private Prototype prototypeB;
	private IRecord recordA;
	private IRecord recordB;
	private IInstance instanceA;
	private IInstance instanceB;
	private InitInstanceCommand command;

	/**
	 *{@inheritDoc}
	 */
	@Before
	public void doSetUp() throws Exception {
		prototypeA = new Prototype("A", UUID.randomUUID());
		prototypeB = new Prototype("B", UUID.randomUUID());

		recordA = RecordFactory.createRecord(getProject(), "ai", "rA", UUID.randomUUID());
		new AddRecordCommand(prototypeA, recordA).execute();
		recordB = RecordFactory.createRecord(getProject(), "ai", "rB", UUID.randomUUID());
		new AddRecordCommand(prototypeB, recordB).execute();

		instanceA = new Instance(prototypeA, UUID.randomUUID());
		new AddInstanceCommand(prototypeB, instanceA).execute();

		instanceB = new Instance(prototypeB, UUID.randomUUID());
		command = new InitInstanceCommand(instanceB);
	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.model.commands.InitInstanceCommand#execute()}.
	 */
	@Test
	public void testExecute() {
		verifyAlways();
		verifyBeforeCommandExecution();
		command.execute();
		verifyAlways();
		verifyAfterCommandExecution();
		command.undo();
		verifyAlways();
		verifyBeforeCommandExecution();
	}

	private void verifyAlways() {
		assertNotNull(prototypeA);
		assertNotNull(prototypeB);
		assertNotNull(recordA);
		assertNotNull(recordB);
		assertNotNull(instanceA);
		assertNotNull(instanceB);
		assertEquals(prototypeA, instanceA.getParent());
		assertEquals(prototypeB, instanceB.getParent());
		assertEquals(prototypeB, instanceA.getContainer());
		assertEquals(1, prototypeA.getRecords().size());
		assertEquals(1, prototypeB.getRecords().size());
		assertTrue(prototypeA.getRecords().contains(recordA));
		assertTrue(prototypeB.getRecords().contains(recordB));
		assertTrue(prototypeA.getInstances().isEmpty());
		assertEquals(1, prototypeB.getInstances().size());
		assertTrue(prototypeB.getInstances().contains(instanceA));
		assertEquals(1, instanceA.getRecords().size());
		assertEquals(recordA, instanceA.getRecords().get(0).getParentRecord());
		assertEquals(0, instanceA.getInstances().size());
	}

	private void verifyBeforeCommandExecution() {
		assertEquals(0, instanceB.getRecords().size());
		assertEquals(0, instanceB.getInstances().size());
	}

	private void verifyAfterCommandExecution() {
		assertEquals(1, instanceB.getRecords().size());
		assertEquals(recordB, instanceB.getRecords().get(0).getParentRecord());
		assertEquals(1, instanceB.getInstances().size());
		assertEquals(instanceA, instanceB.getInstance(0).getParent());
	}

}
