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
    private int index=-1;

    public AddRecordCommand(IContainer container, IRecord record, int index) {
        this.container = container;
        this.record = record;
        this.index=index;
    }

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
        addRecord(container, record, index>-1?index:container.getRecords().size());
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void undo() {
        removeRecord(container, record);
    }
}
