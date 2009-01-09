/**
 * 
 */
package org.csstudio.dct.model.commands;

import org.csstudio.dct.DctActivator;
import org.csstudio.dct.metamodel.IDatabaseDefinition;
import org.csstudio.dct.metamodel.IRecordDefinition;
import org.csstudio.dct.model.internal.Project;
import org.eclipse.gef.commands.Command;

/**
 * Undoable command that changes the database definition (dbd) reference of a
 * {@link Project}.
 * 
 * @author Sven Wende
 * 
 */
public class ChangeDbdFileCommand extends Command {
	private Project project;
	private String currentPath;
	private String oldPath;

	public ChangeDbdFileCommand(Project project, String path) {
		this.project = project;
		this.currentPath = path;
		this.oldPath = project.getDbdPath();
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void execute() {
		setPath(currentPath);
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void undo() {
		setPath(oldPath);
	}

	private void setPath(String path) {
		// .. store the path
		project.setDbdPath(path);

		// .. try to read the definition from file
		IDatabaseDefinition databaseDefinition = path != null ? DctActivator.getDefault().getPersistenceService().loadDatabaseDefinition(path) : null;

		// .. set the definition
		project.setDatabaseDefinition(databaseDefinition);

		// .. invalidate the old base records
		for (String name : project.getBaseRecords().keySet()) {
			project.getBaseRecord(name).setRecordDefinition(null);
		}

		// .. refresh the base records
		if (databaseDefinition != null) {
			for (IRecordDefinition rd : databaseDefinition.getRecordDefinitions()) {
				project.getBaseRecord(rd.getType()).setRecordDefinition(rd);
			}
		}
	}

}