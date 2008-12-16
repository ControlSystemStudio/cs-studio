package org.csstudio.dct.model.commands;

import java.util.UUID;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.internal.Instance;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

public class AddInstanceCommand extends Command {
	private CompoundCommand internalCommand;
	protected IContainer container;
	protected IFolder folder;
	protected IInstance instance;
	
	public AddInstanceCommand(IFolder folder, IInstance instance) {
		assert instance.getParentFolder() == folder;
		assert instance.getContainer() == null;
		this.instance = instance;
		this.folder = folder;
	}

	public AddInstanceCommand(IContainer container, IInstance instance) {
		assert instance.getParentFolder() == null;
		assert instance.getContainer() == container;
		this.instance = instance;
		this.container = container;
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void execute() {
		internalCommand = new CompoundCommand();

		internalCommand.add(new InitInstanceCommand(instance));

		if (folder != null) {
			folder.addMember(instance);
			instance.setParentFolder(folder);
		} else {
			container.addInstance(instance);

			// ... link physical container
			instance.setContainer(container);

			// ... add-push to model elements that inherit from here
			for (IContainer c : container.getDependentContainers()) {
				Instance pushedInstance = new Instance(instance, UUID.randomUUID());
				internalCommand.add(new AddInstanceCommand(c, pushedInstance));
			}
		}
		
		// ... link to super
		instance.getParent().addDependentContainer(instance);

		internalCommand.execute();
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void undo() {
		internalCommand.undo();
		if (folder != null) {
			folder.removeMember(instance);
			instance.setParentFolder(null);
		} else {
			container.removeInstance(instance);
			instance.setContainer(null);
		}
		instance.getParent().removeDependentContainer(instance);
	}

}
