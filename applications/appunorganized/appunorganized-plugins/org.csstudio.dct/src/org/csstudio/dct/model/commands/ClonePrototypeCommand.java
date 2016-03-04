package org.csstudio.dct.model.commands;

import java.util.UUID;

import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.internal.Parameter;
import org.csstudio.dct.model.internal.Prototype;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

public final class ClonePrototypeCommand extends Command {
    private IPrototype original;
    private IFolder targetFolder;
    private String namePrefix;
    private CompoundCommand chain;

    public ClonePrototypeCommand(IPrototype original, IFolder targetFolder, String namePrefix) {
        this.original = original;
        this.targetFolder = targetFolder;
        this.namePrefix = namePrefix;
        createCommandChain();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        chain.execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() {
        chain.undo();
    }

    private void createCommandChain() {
        chain = new CompoundCommand();

        // we create a copy and chain all necessary commands
        IPrototype prototype = new Prototype(namePrefix + original.getName(), UUID.randomUUID());
        chain.add(new AddPrototypeCommand(targetFolder, prototype));

        // .. parameters
        for (Parameter parameter : original.getParameters()) {
            chain.add(new AddParameterCommand(prototype, parameter));
        }

        // .. records
        for (IRecord r : original.getRecords()) {
            chain.add(new CloneRecordCommand(r, prototype, null));
        }

        // .. instances
        for (IInstance i : original.getInstances()) {
            chain.add(new CloneInstanceCommand(i, prototype, ""));
        }
    }

}
