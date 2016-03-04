/**
 *
 */
package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IProject;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * Synchronizes instance structures of two containers. Mainly for use after cloning
 * or copying an instance.
 *
 * @author Sven Wende
 *
 */
public final class SynchronizeInstancesCommand extends Command {
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
    public SynchronizeInstancesCommand(IInstance original, IInstance target, IProject project) {
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

        // .. synchronize attributes of derived instances
        int i = 0;

        for (IInstance ci : original.getInstances()) {
            if (ci.isInherited()) {
                IInstance r = target.getInstances().get(i);
                i++;

                commandChain.add(new ChangeBeanPropertyCommand(r, "name", ci.getName()));
            }
        }

        // .. add additional instances that were not inherited from a prototype
        for (IInstance originalInstance : original.getInstances()) {
            if (!originalInstance.isInherited()) {
                //FIXME: korrekten Index ermitteln
                commandChain.add(new CloneInstanceCommand(originalInstance, target, ""));
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
