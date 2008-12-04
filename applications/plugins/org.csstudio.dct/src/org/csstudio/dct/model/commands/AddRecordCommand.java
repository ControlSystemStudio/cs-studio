package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IRecordContainer;

/**
 * Command that adds a {@link IRecord} to a {@link IRecordContainer}.
 * 
 * @author Sven Wende
 * 
 */
public class AddRecordCommand extends AbstractRecordCommand {

	private IRecordContainer container;
	private IRecord record;

	public AddRecordCommand(IRecordContainer container, IRecord record) {
		this.container = container;
		this.record = record;
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void execute() {
		addRecord(container, record);
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void undo() {
		removeRecord(container, record);
	}
}
