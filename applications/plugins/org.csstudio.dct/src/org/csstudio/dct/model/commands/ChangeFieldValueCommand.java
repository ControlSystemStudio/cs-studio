/**
 * 
 */
package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IRecord;
import org.eclipse.gef.commands.Command;

/**
 * Undoable command that changes to value of a record field.
 * 
 * @author Sven Wende
 */
public class ChangeFieldValueCommand extends Command {
	private IRecord record;
	private String key;
	private Object value;
	private Object oldValue;

	public ChangeFieldValueCommand(IRecord record, String key, Object value) {
		this.record = record;
		this.key = key;
		this.value = value;
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void execute() {
		IRecord parentRecord = record.getParentRecord();

		Object parentValue = parentRecord != null ? parentRecord.getFinalFields().get(key) : "";

		oldValue = record.getField(key);

		if (value == null || "".equals(value) || value.equals(parentValue)) {
			record.removeField(key);
		} else {
			record.addField(key, value);
		}
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void undo() {
		if (oldValue != null) {
			record.addField(key, oldValue);
		} else {
			record.removeField(key);
		}
	}

}