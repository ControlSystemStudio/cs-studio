package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IRecord;

/**
 * Command that adds a {@link IRecord} to a container.
 * 
 * @author Sven Wende
 * 
 */
public final class AddRecordCommand extends AbstractRecordCommand {

	private IContainer container;
	private IRecord record;

	/**
	 * Constructor.
	 * @param container the record container
	 * @param record the record
	 */
	public AddRecordCommand(IContainer container, IRecord record) {
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
