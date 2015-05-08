/**
 *
 */
package org.csstudio.dct.model.commands;

import java.util.UUID;

import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.internal.RecordFactory;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * Synchronizes record structures of two instances. Mainly for use after cloning
 * or copying an instance.
 *
 * @author Sven Wende
 *
 */
public final class SynchronizeRecordsCommand extends Command {
    private IInstance target;
    private IInstance original;
    private CompoundCommand commandChain;
    private IProject project;

    /**
     * Constructor.
     *
     * @param delegate
     *            the object
     * @param propertyName
     *            the name of the property
     * @param value
     *            the new value
     */
    public SynchronizeRecordsCommand(IInstance original, IInstance target, IProject project) {
        this.original = original;
        this.target = target;
        this.project = project;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void execute() {
        commandChain = new CompoundCommand();

        // .. synchronize attributes of derived records
        int i = 0;

        for (IRecord cr : original.getRecords()) {
            if (cr.isInherited()) {
                IRecord r = target.getRecords().get(i);
                i++;

                commandChain.add(new ChangeBeanPropertyCommand(r, "epicsName", cr.getEpicsName()));
                commandChain.add(new ChangeBeanPropertyCommand(r, "disabled", cr.getDisabled()));
                commandChain.add(new ChangeBeanPropertyCommand(r, "name", cr.getName()));

                for (String key : cr.getFields().keySet()) {
                    commandChain.add(new ChangeFieldValueCommand(r, key, cr.getField(key)));
                }
            }
        }

        // .. add additional records that were not inherited from a prototype
        for (IRecord originalRecord : original.getRecords()) {
            if (!originalRecord.isInherited()) {
                IRecord newRecord = RecordFactory.createRecord(project, originalRecord.getType(), originalRecord.getName(), UUID.randomUUID());

                commandChain.add(new ChangeBeanPropertyCommand(newRecord, "epicsName", originalRecord.getEpicsName()));
                commandChain.add(new ChangeBeanPropertyCommand(newRecord, "disabled", originalRecord.getDisabled()));
                commandChain.add(new ChangeBeanPropertyCommand(newRecord, "name", originalRecord.getName()));

                commandChain.add(new AddRecordCommand(target, newRecord, original.getRecords().indexOf(originalRecord)));

                for (String key : originalRecord.getFields().keySet()) {
                    commandChain.add(new ChangeFieldValueCommand(newRecord, key, originalRecord.getField(key)));
                }
            }
        }

        commandChain.execute();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void undo() {
        commandChain.undo();
    }

}
