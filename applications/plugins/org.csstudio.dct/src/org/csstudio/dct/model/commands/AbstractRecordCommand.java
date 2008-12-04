package org.csstudio.dct.model.commands;

import java.util.ArrayList;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IRecordContainer;
import org.csstudio.dct.model.internal.Record;
import org.eclipse.gef.commands.Command;

/**
 * Base class for {@link AddRecordCommand} and {@link RemoveRecordCommand}.
 * 
 * @author Sven Wende
 * 
 */
abstract class AbstractRecordCommand extends Command {

	/**
	 * Adds a record to a container.
	 * 
	 * @param container
	 *            the container
	 * @param record
	 *            the record
	 */
	protected void addRecord(IRecordContainer container, IRecord record) {
		container.addRecord(record);

		// ... link physical container
		record.setContainer(container);

		// ... link to super
		record.getParentRecord().addDependentRecord(record);

		// ... add-push to model elements that inherit from here
		for (IRecordContainer c : container.getDependentRecordContainers()) {
			IRecord pushedRecord = new Record(record);
			addRecord(c, pushedRecord);
		}
	}

	/**
	 * Removes a record from a container.
	 * 
	 * @param container
	 *            the container
	 * @param record
	 *            the record
	 */
	protected void removeRecord(IRecordContainer container, IRecord record) {
		container.removeRecord(record);

		// ... unlink container
		record.setContainer(null);

		// ... unlink from parent
		if (record.getParentRecord() != null) {
			record.getParentRecord().removeDependentRecord(record);
		}

		// ... remove dependent records
		for (IRecord dr : new ArrayList<IRecord>(record.getDependentRecords())) {
			removeRecord(dr.getContainer(), dr);
		}

		assert record.getDependentRecords().isEmpty();
	}
}
