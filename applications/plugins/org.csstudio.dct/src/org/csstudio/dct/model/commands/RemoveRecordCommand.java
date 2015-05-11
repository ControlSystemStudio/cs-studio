package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IRecord;

/**
 * Command that removes a {@link IRecord} from a container.
 *
 * @author Sven Wende
 *
 */
public final class RemoveRecordCommand extends AbstractRecordCommand {

    private IContainer container;
    private IRecord record;
    private int index;

    /**
     * Constructor.
     * @param record the record
     */
    public RemoveRecordCommand(IRecord record) {
        assert record != null;
        assert record.getContainer() != null;
        this.container = record.getContainer();
        this.record = record;
        assert container==record.getContainer();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void execute() {
        index = removeRecord(container, record);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void undo() {
        addRecord(container, record, index);
    }
}
