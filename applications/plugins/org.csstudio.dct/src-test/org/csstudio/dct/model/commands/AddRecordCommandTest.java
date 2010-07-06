/**
 * 
 */
package org.csstudio.dct.model.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.internal.Instance;
import org.csstudio.dct.model.internal.Prototype;
import org.csstudio.dct.model.internal.RecordFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link AddRecordCommand}.
 * 
 * @author Sven Wende
 *
 */
public final class AddRecordCommandTest extends AbstractTestCommand {
	private IPrototype prototype;
	private IInstance instance;
	private IRecord record;
	private AddRecordCommand command;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void doSetUp() throws Exception {
		prototype = new Prototype("P", UUID.randomUUID());
		instance = new Instance(prototype, UUID.randomUUID());
		prototype.addDependentContainer(instance);
		record = RecordFactory.createRecord(getProject(), "ai", "r", UUID.randomUUID());
		command = new AddRecordCommand(prototype, record);
	}

	/**
	 * Test method for {@link org.csstudio.dct.model.commands.AddRecordCommand#execute()}.
	 */
	@Test
	public void testExecute() {
		verifyBeforeCommandExecution();
		command.execute();
		verifyAfterCommandExecution();
		command.undo();
		verifyBeforeCommandExecution();
	}
	

	private void verifyAfterCommandExecution() {
		assertEquals(1, prototype.getRecords().size());
		assertEquals(1, instance.getRecords().size());
		assertTrue(prototype.getRecords().contains(record));
		assertFalse(instance.getRecords().contains(record));
		IRecord instanceRecord = instance.getRecords().get(0);
		assertEquals(record, instanceRecord.getParentRecord());
		assertTrue(record.getDependentRecords().contains(instanceRecord));
	}

	private void verifyBeforeCommandExecution() {
		assertTrue(prototype.getRecords().isEmpty());
		assertTrue(instance.getRecords().isEmpty());
		assertTrue(record.getDependentRecords().isEmpty());
	}


	
	

}
