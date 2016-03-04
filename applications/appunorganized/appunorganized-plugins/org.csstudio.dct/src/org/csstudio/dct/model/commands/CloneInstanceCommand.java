package org.csstudio.dct.model.commands;

import java.util.UUID;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.internal.Instance;
import org.csstudio.dct.util.AliasResolutionUtil;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

public final class CloneInstanceCommand extends Command {
    private IInstance original;
    private IFolder targetFolder;
    private IContainer targetContainer;
    private String namePrefix;
    private CompoundCommand chain;
    private IPrototype prototype;

    /**
     * Clones the specified instance an adds it to a container.
     *
     * @param original
     *            the instance
     * @param targetContainer
     *            the target container
     * @param namePrefix
     *            optional name prefix
     */
    public CloneInstanceCommand(IInstance original, IContainer targetContainer, String namePrefix) {
        this(original, targetContainer, namePrefix, null);
    }

    /**
     * Clones the specified instance an adds it to a container.
     *
     * @param original
     *            the instance
     * @param targetContainer
     *            the target container
     * @param namePrefix
     *            optional name prefix
     * @param prototype
     *            Optional prototype, the cloned instance should be derived from
     *            (may be a copy of the original instances prototype in case the
     *            instance is copied to another project). If not specified, the
     *            cloned instance will derive from the original instances
     *            prototype.
     */
    public CloneInstanceCommand(IInstance original, IContainer targetContainer, String namePrefix, IPrototype prototype) {
        this.original = original;
        this.targetContainer = targetContainer;
        this.namePrefix = namePrefix;
        this.prototype = prototype != null ? prototype : original.getPrototype();
    }

    /**
     * Clones the specified instance an adds it to a folder.
     *
     * @param original
     *            the instance
     * @param targetContainer
     *            the target folder
     * @param namePrefix
     *            optional name prefix
     * @param prototype
     *            Optional prototype, the cloned instance should be derived from
     *            (may be a copy of the original instances prototype in case the
     *            instance is copied to another project). If not specified, the
     *            cloned instance will derive from the original instances
     *            prototype.
     */
    public CloneInstanceCommand(IInstance original, IFolder targetFolder, String namePrefix, IPrototype prototype) {
        this.original = original;
        this.targetFolder = targetFolder;
        this.namePrefix = namePrefix;
        this.prototype = prototype != null ? prototype : original.getPrototype();
        createCommandChain();
    }

    /**
     * Clones the specified instance an adds it to a container.
     *
     * @param original
     *            the instance
     * @param targetContainer
     *            the target container
     * @param namePrefix
     *            optional name prefix
     */
    public CloneInstanceCommand(IInstance original, IFolder targetFolder, String namePrefix) {
        this(original, targetFolder, namePrefix, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        if (chain == null) {
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

        // .. create the instance
        String originalName = AliasResolutionUtil.getNameFromHierarchy(original);
        IInstance instance = new Instance((namePrefix != null ? namePrefix : "") + originalName, prototype, UUID.randomUUID());

        // .. add to target folder or container
        IProject targetProject;
        if (targetFolder != null) {
            assert targetContainer == null;
            targetProject = targetFolder.getProject();
            chain.add(new AddInstanceCommand(targetFolder, instance));
        } else {
            assert targetFolder == null;
            assert targetContainer != null;
            targetProject = targetContainer.getProject();
            chain.add(new AddInstanceCommand(targetContainer, instance));
        }

        assert targetProject != null;

        // .. parameter values
        for (String pkey : original.getParameterValues().keySet()) {
            chain.add(new ChangeParameterValueCommand(instance, pkey, original.getParameterValue(pkey)));
        }

        // .. synchronize the instance structures
        //FIXME:
        chain.add(new SynchronizeInstancesCommand(original, instance, targetProject));

        // .. synchronize the record structures
        chain.add(new SynchronizeRecordsCommand(original, instance, targetProject));

        return chain;
    }

}
