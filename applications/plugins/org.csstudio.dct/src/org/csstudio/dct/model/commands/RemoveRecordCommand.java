package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IRecordContainer;

/**
 * Command that removes a {@link IRecord} from a {@link IRecordContainer}.
 * 
 * @author Sven Wende
 * 
 */
public class RemoveRecordCommand extends AbstractRecordCommand {

	private IRecordContainer container;
	private IRecord record;

	public RemoveRecordCommand(IRecord record) {
		this(record.getContainer(), record);
	}
	
	/**
	 * @deprecated
	 * @param container
	 * @param record
	 */
	public RemoveRecordCommand(IRecordContainer container, IRecord record) {
		assert container != null;
		assert record != null;
		this.container = container;
		this.record = record;
		assert container==record.getContainer();
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void execute() {
		removeRecord(container, record);
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void undo() {
		addRecord(container, record);
	}
}
