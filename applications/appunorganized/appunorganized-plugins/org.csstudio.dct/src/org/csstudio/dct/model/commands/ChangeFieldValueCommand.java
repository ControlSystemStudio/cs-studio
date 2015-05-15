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
public final class ChangeFieldValueCommand extends Command {
    private IRecord record;
    private String key;
    private String value;
    private String oldValue;

    /**
     * Constructor.
     * @param record the record
     * @param key the field name
     * @param value the field value
     */
    public ChangeFieldValueCommand(IRecord record, String key, String value) {
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
