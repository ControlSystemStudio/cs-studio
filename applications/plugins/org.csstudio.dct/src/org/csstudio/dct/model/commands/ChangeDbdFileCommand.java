/**
 *
 */
package org.csstudio.dct.model.commands;

import org.csstudio.dct.metamodel.IDatabaseDefinition;
import org.csstudio.dct.metamodel.IRecordDefinition;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.persistence.internal.PersistenceService;
import org.eclipse.gef.commands.Command;

/**
 * Undoable command that changes the database definition (dbd) reference of a
 * {@link IProject}.
 *
 * @author Sven Wende
 *
 */
public final class ChangeDbdFileCommand extends Command {
    private IProject project;
    private String currentPath;
    private String oldPath;

    /**
     * Constructor.
     * @param project the project
     * @param path the path to the dbd file
     */
    public ChangeDbdFileCommand(IProject project, String path) {
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
        IDatabaseDefinition databaseDefinition = path != null ? new PersistenceService().loadDatabaseDefinition(path) : null;

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
