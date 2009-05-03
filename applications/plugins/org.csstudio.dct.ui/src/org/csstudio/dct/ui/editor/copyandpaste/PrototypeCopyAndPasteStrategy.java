package org.csstudio.dct.ui.editor.copyandpaste;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.commands.AddInstanceCommand;
import org.csstudio.dct.model.commands.AddParameterCommand;
import org.csstudio.dct.model.commands.AddPrototypeCommand;
import org.csstudio.dct.model.commands.AddRecordCommand;
import org.csstudio.dct.model.commands.ChangeFieldValueCommand;
import org.csstudio.dct.model.commands.ChangeParameterValueCommand;
import org.csstudio.dct.model.internal.Instance;
import org.csstudio.dct.model.internal.Parameter;
import org.csstudio.dct.model.internal.Prototype;
import org.csstudio.dct.model.internal.RecordFactory;
import org.csstudio.dct.model.visitors.SearchVisitor;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * Copy & Paste strategy for instances and prototypes.
 * 
 * @author Sven Wende
 * 
 */
public final class PrototypeCopyAndPasteStrategy implements ICopyAndPasteStrategy {

	/**
	 *{@inheritDoc}
	 */
	public Command createPasteCommand(List<IElement> copiedElements, IProject project, List<IElement> selectedElements) {
		assert copiedElements != null;
		assert project != null;
		assert selectedElements != null;

		Map<UUID, IPrototype> tmpPrototypes = new HashMap<UUID, IPrototype>();

		CompoundCommand cmd = new CompoundCommand();

		for (IElement c : selectedElements) {
			assert c instanceof IFolder;

			for (IElement p : copiedElements) {
				if (p instanceof IPrototype) {
					chainPrototype((IPrototype) p, cmd, tmpPrototypes, project, (IFolder) c);
				} else {
					chainInstance((IInstance) p, cmd, tmpPrototypes, project, (IFolder) c, null);
				}
			}
		}

		return cmd;
	}

	/**
	 *{@inheritDoc}
	 */
	public List<Serializable> createCopyElements(List<IElement> selectedElements) {
		Set<IContainer> items = new HashSet<IContainer>();

		for (IElement e : selectedElements) {
			assert e instanceof IContainer;
			IContainer container = (IContainer) e;
			items.add(container);
		}

		return new ArrayList<Serializable>(items);
	}

	/**
	 *{@inheritDoc}
	 */
	public boolean canCopy(List<IElement> selectedElements) {
		boolean result = false;

		if (!selectedElements.isEmpty()) {
			result = true;
			for (IElement e : selectedElements) {
				result &= e instanceof IContainer;
			}
		}

		return result;
	}

	/**
	 *{@inheritDoc}
	 */
	public boolean canPaste(List<IElement> selectedElements) {
		boolean result = false;

		if (!selectedElements.isEmpty()) {
			result = true;
			for (IElement e : selectedElements) {
				result &= e instanceof IFolder;
			}
		}

		return result;
	}

	/**
	 *{@inheritDoc}
	 */
	public String getContentDescription() {
		return "Prototypes & Instances";
	}

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
	private void chainInstance(IInstance instance2copy, CompoundCommand commandChain, Map<UUID, IPrototype> alreadyCreatedPrototypes,
			IProject project, IFolder targetFolder, IContainer targetContainer) {

		// find the prototype at first
		UUID pid = instance2copy.getPrototype().getId();

		IElement element = new SearchVisitor().search(project, pid);

		IPrototype prototype = null;

		if (element != null && element instanceof IPrototype) {
			// in case, the copy & paste occured within the same project, we can
			// simply use the prototype from the project
			prototype = (IPrototype) element;
		} else if (alreadyCreatedPrototypes.containsKey(pid)) {
			// maybe the prototype has already been copied in a previous step ?
			prototype = alreadyCreatedPrototypes.get(pid);
		} else {
			// we have to create a copy of the prototype
			prototype = chainPrototype(instance2copy.getPrototype(), commandChain, alreadyCreatedPrototypes, project, targetFolder);
		}

		// chain all necessary command to create the new instance
		IInstance instance = new Instance(prototype, UUID.randomUUID());

		if (targetContainer != null) {
			commandChain.add(new AddInstanceCommand(targetContainer, instance));
		} else {
			commandChain.add(new AddInstanceCommand(targetFolder, instance));
		}

		// .. parameter values
		for (String pkey : instance2copy.getParameterValues().keySet()) {
			commandChain.add(new ChangeParameterValueCommand(instance, pkey, instance2copy.getParameterValue(pkey)));
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
	private IPrototype chainPrototype(IPrototype prototype2Copy, CompoundCommand commandChain, Map<UUID, IPrototype> alreadyCreatedPrototypes,
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
