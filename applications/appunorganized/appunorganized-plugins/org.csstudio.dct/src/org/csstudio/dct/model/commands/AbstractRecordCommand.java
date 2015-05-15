package org.csstudio.dct.model.commands;

import java.util.ArrayList;
import java.util.UUID;

import org.csstudio.dct.model.IContainer;
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
     * @param the
     *            index position
     */
    protected void addRecord(IContainer container, IRecord record, int index) {
        assert index >=0;
        container.addRecord(Math.min(index, container.getRecords().size()), record);

        // ... link physical container
        record.setContainer(container);

        // ... link to super
        record.getParentRecord().addDependentRecord(record);

        // ... add-push to model elements that inherit from here
        for (IRecordContainer c : container.getDependentRecordContainers()) {
            IRecord pushedRecord = new Record(record, UUID.randomUUID());
            addRecord((IContainer) c, pushedRecord, Math.min(index, container.getRecords().size()));
        }
    }

    /**
     * Removes a record from a container.
     *
     * @param container
     *            the container
     * @param record
     *            the record
     * @return the former list index of the record
     */
    protected int removeRecord(IRecordContainer container, IRecord record) {
        int result = container.getRecords().indexOf(record);

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

        return result;
    }
}
