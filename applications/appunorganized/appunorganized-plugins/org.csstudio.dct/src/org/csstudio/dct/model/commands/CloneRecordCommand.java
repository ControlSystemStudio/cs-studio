package org.csstudio.dct.model.commands;

import java.util.UUID;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.internal.RecordFactory;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

public final class CloneRecordCommand extends Command {
    private IRecord original;
    private IContainer targetContainer;
    private String namePrefix;
    private CompoundCommand chain;

    public CloneRecordCommand(IRecord original, IContainer targetContainer, String namePrefix) {
        this.original = original;
        this.targetContainer = targetContainer;
        this.namePrefix = namePrefix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        if(chain==null) {
            chain = createCommandChain();
        }
        chain.execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        chain.undo();
    }

    private CompoundCommand createCommandChain() {
        CompoundCommand chain = new CompoundCommand();

        IRecord record = RecordFactory.createRecord(targetContainer.getProject(), original.getType(), (namePrefix!=null?namePrefix:"")+original.getName(), UUID.randomUUID());

        chain.add(new ChangeBeanPropertyCommand(record, "epicsName", original.getEpicsName()));
        chain.add(new ChangeBeanPropertyCommand(record, "disabled", original.getDisabled()));
        chain.add(new ChangeBeanPropertyCommand(record, "name", original.getName()));

        chain.add(new AddRecordCommand(targetContainer, record));

        for (String key : original.getFields().keySet()) {
            chain.add(new ChangeFieldValueCommand(record, key, original.getField(key)));
        }

        return chain;
    }

}
