package org.csstudio.dct.ui.editor.copyandpaste;

import java.util.Map;
import java.util.UUID;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.commands.AddParameterCommand;
import org.csstudio.dct.model.commands.AddPrototypeCommand;
import org.csstudio.dct.model.commands.AddRecordCommand;
import org.csstudio.dct.model.commands.ChangeBeanPropertyCommand;
import org.csstudio.dct.model.commands.ChangeFieldValueCommand;
import org.csstudio.dct.model.commands.CloneInstanceCommand;
import org.csstudio.dct.model.internal.Parameter;
import org.csstudio.dct.model.internal.Prototype;
import org.csstudio.dct.model.internal.RecordFactory;
import org.csstudio.dct.model.visitors.SearchVisitor;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * Base Copy & Paste strategy for instances and prototypes.
 *
 * @author Sven Wende
 *
 */
public abstract class BaseCopyAndPasteStrategy implements ICopyAndPasteStrategy {

    /**
     * Chains all necessary commands for the creation of a copy of the specified
     * instance.
     *
     * @param instance2copy
     *            the instance to copy
     * @param commandChain
     *            the compound command
     * @param alreadyCreatedPrototypes
     *            prototype that where already created in previous steps
     * @param project
     *            the target project
     * @param targetFolder
     *            the target folder
     * @param targetContainer
     *            the target folder (optional, if specified, this will be the
     *            parent container of the new instance, otherwise the new
     *            instance will be put into the targetFolder
     *
     * @return the created prototype
     */
    protected void chainInstance(IInstance instance2copy, CompoundCommand commandChain, Map<UUID, IPrototype> alreadyCreatedPrototypes,
            IProject project, IFolder targetFolder, IContainer targetContainer) {

        // find the prototype at first
        UUID pid = instance2copy.getPrototype().getId();

        IElement element = new SearchVisitor().search(project, pid);

        IPrototype prototype = null;

        if (element != null && element instanceof IPrototype) {
            // in case, the copy & paste occurred within the same project, we can
            // simply use the prototype from the project
            prototype = (IPrototype) element;
        } else if (alreadyCreatedPrototypes.containsKey(pid)) {
            // maybe the prototype has already been copied in a previous step ?
            prototype = alreadyCreatedPrototypes.get(pid);
        } else {
            // we have to create a copy of the prototype
            prototype = chainPrototype(instance2copy.getPrototype(), commandChain, alreadyCreatedPrototypes, project, targetFolder);
        }


        if (targetContainer != null) {
            commandChain.add(new CloneInstanceCommand(instance2copy, targetContainer, null, prototype));
        } else {
            commandChain.add(new CloneInstanceCommand(instance2copy, targetFolder, null, prototype));
        }

    }

    /**
     * Chains all necessary commands for the creation of a copy of the specified
     * prototype.
     *
     * @param prototype2Copy
     *            the prototype to copy
     * @param commandChain
     *            the compound command
     * @param alreadyCreatedPrototypes
     *            prototype that where already created in previous steps
     * @param project
     *            the target project
     * @param targetFolder
     *            the target folder
     *
     * @return the created prototype
     */
    protected IPrototype chainPrototype(IPrototype prototype2Copy, CompoundCommand commandChain, Map<UUID, IPrototype> alreadyCreatedPrototypes,
            IProject project, IFolder targetFolder) {
        if (alreadyCreatedPrototypes.containsKey(prototype2Copy.getId())) {
            // maybe we already created a copy of the prototype in a previous
            // step ?
            return alreadyCreatedPrototypes.get(prototype2Copy.getId());
        } else {
            // we create a copy and chain all necessary commands
            IPrototype prototype = new Prototype(prototype2Copy.getName(), UUID.randomUUID());
            commandChain.add(new AddPrototypeCommand(targetFolder, prototype));
            alreadyCreatedPrototypes.put(prototype2Copy.getId(), prototype);

            // .. parameters
            for (Parameter parameter : prototype2Copy.getParameters()) {
                commandChain.add(new AddParameterCommand(prototype, parameter));
            }

            // .. records
            for (IRecord r : prototype2Copy.getRecords()) {
                IRecord record = RecordFactory.createRecord(project, r.getType(), r.getName(), UUID.randomUUID());

                commandChain.add(new ChangeBeanPropertyCommand(record, "epicsName", r.getEpicsName()));
                commandChain.add(new ChangeBeanPropertyCommand(record, "disabled", r.getDisabled()));
                commandChain.add(new ChangeBeanPropertyCommand(record, "name", r.getName()));


                commandChain.add(new AddRecordCommand(prototype, record));

                for (String key : r.getFields().keySet()) {
                    commandChain.add(new ChangeFieldValueCommand(record, key, r.getField(key)));
                }
            }

            // .. instances
            for (IInstance in : prototype2Copy.getInstances()) {
                chainInstance(in, commandChain, alreadyCreatedPrototypes, project, targetFolder, prototype);
            }

            return prototype;

        }

    }
}
