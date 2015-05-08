package org.csstudio.dct.model.commands;

import java.util.UUID;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.internal.Instance;
import org.csstudio.dct.model.internal.Record;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * Command the initializes an instance.
 *
 * @author Sven Wende
 *
 */
public final class InitInstanceCommand extends Command {
    private CompoundCommand internalCmd;
    private IInstance instance;

    /**
     * Constructor.
     * @param instance the instance
     */
    public InitInstanceCommand(IInstance instance) {
        this.instance = instance;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void execute() {
        internalCmd = new CompoundCommand();

        IContainer parent = instance.getParent();

        if (parent != null) {
            // inherit all records from parent
            for (IRecord r : parent.getRecords()) {
                Record record = new Record(r, UUID.randomUUID());
                internalCmd.add(new AddRecordCommand(instance, record));
            }

            // inherit all instances
            for (IInstance pInstance : parent.getInstances()) {
                Instance iInstance = new Instance(pInstance, UUID.randomUUID());
                internalCmd.add(new AddInstanceCommand(instance, iInstance));
            }
        }

        internalCmd.execute();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void undo() {
        internalCmd.undo();
    }

}
