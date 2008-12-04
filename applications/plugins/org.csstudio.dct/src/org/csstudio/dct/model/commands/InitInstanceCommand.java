package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.internal.Instance;
import org.csstudio.dct.model.internal.Record;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

public class InitInstanceCommand extends Command {
	private CompoundCommand internalCmd;
	private IInstance instance;

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
				Record record = new Record(r);
				internalCmd.add(new AddRecordCommand(instance, record));
			}

			// inherit all instances
			for (IInstance pInstance : parent.getInstances()) {
				Instance iInstance = new Instance(pInstance);
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
