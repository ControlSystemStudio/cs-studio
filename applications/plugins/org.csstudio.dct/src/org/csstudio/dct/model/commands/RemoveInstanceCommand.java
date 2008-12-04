package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IInstance;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

/**
 * Undoable command the removes an {@link IInstance} from a {@link IFolder} or a
 * {@link IContainer}.
 * 
 * @author Sven Wende
 * 
 */
public class RemoveInstanceCommand extends Command {
	private CompoundCommand internalCommand;
	protected IContainer container;
	protected IFolder folder;
	protected IInstance instance;

	public RemoveInstanceCommand(IInstance instance) {
		assert instance != null;
		assert instance.getParentFolder() != null || instance.getContainer() != null;

		this.instance = instance;
		this.folder = instance.getParentFolder();
		this.container = (IContainer) instance.getContainer();
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void execute() {
		internalCommand = new CompoundCommand();

//		internalCommand.add(new InitInstanceCommand(instance));

		if (folder != null) {
			folder.removeMember(instance);
			instance.setParentFolder(null);
		} else {
			container.removeInstance(instance);
			instance.setContainer(null);

			for (IContainer c : instance.getDependentContainers()) {
				internalCommand.add(new RemoveInstanceCommand((IInstance) c));
			}
		}

		// ... link to super
		instance.getParent().removeDependentContainer(instance);

		internalCommand.execute();
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void undo() {
		if (folder != null) {
			folder.addMember(instance);
			instance.setParentFolder(folder);
		} else {
			container.addInstance(instance);
			instance.setContainer(container);
		}
		internalCommand.undo();
	}

}
