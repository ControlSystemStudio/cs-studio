/**
 * 
 */
package org.csstudio.dct.model.commands;

import java.util.UUID;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.internal.RecordFactory;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;

public final class SynchronizeRecordsCommand extends Command {
	private IInstance target;
	private IInstance original;
	private CompoundCommand commandChain;
	private IProject project;
	
	/**
	 * Constructor.
	 * @param delegate the object
	 * @param propertyName the name of the property
	 * @param value the new value
	 */
	public SynchronizeRecordsCommand(IInstance original, IInstance target, IProject project) {
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
		
		int i = 0;

		for (IRecord cr : original.getRecords()) {
			if (cr.isInherited()) {
				IRecord r = target.getRecords().get(i);
				i++;

				commandChain.add(new ChangeBeanPropertyCommand(r, "epicsName", cr.getEpicsName()));
				commandChain.add(new ChangeBeanPropertyCommand(r, "disabled", cr.getDisabled()));
				commandChain.add(new ChangeBeanPropertyCommand(r, "name", cr.getName()));

				for (String key : cr.getFields().keySet()) {
					commandChain.add(new ChangeFieldValueCommand(r, key, cr.getField(key)));
				}
			}
		}

		// .. add additional records that were not inherited from a prototype
		for (IRecord cr : original.getRecords()) {
			if (!cr.isInherited()) {
				IRecord nr = RecordFactory.createRecord(project, cr.getType(), cr.getName(), UUID.randomUUID());

				commandChain.add(new ChangeBeanPropertyCommand(nr, "epicsName", cr.getEpicsName()));
				commandChain.add(new ChangeBeanPropertyCommand(nr, "disabled", cr.getDisabled()));
				commandChain.add(new ChangeBeanPropertyCommand(nr, "name", cr.getName()));

				commandChain.add(new AddRecordCommand(target, nr));

				for (String key : cr.getFields().keySet()) {
					commandChain.add(new ChangeFieldValueCommand(nr, key, cr.getField(key)));
				}
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
