/**
 * 
 */
package org.csstudio.dct.model.commands;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.internal.RecordFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link ChangeFieldValueCommand}.
 * 
 * @author Sven Wende
 * 
 */
public final class ChangeFieldValueCommandTest extends AbstractTestCommand{
	private IRecord record;

	/**	
	 * @throws java.lang.Exception
	 */
	@Before
	public void doSetUp() throws Exception {
		record = RecordFactory.createRecord(getProject(), "ai", "test", UUID.randomUUID());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.model.commands.ChangeFieldValueCommand#execute()}
	 * .
	 */
	@Test
	public void testExecute() {
		String id = "test";
		String value = "a";
		String newValue = "b";

		// .. before
		record.addField(id, value);
		assertEquals(value, record.getField(id));

		// .. execute
		ChangeFieldValueCommand cmd = new ChangeFieldValueCommand(record, id, newValue);
		cmd.execute();
		assertEquals(newValue, record.getField(id));

		// .. undo
		cmd.undo();
		assertEquals(value, record.getField(id));

	}

}
