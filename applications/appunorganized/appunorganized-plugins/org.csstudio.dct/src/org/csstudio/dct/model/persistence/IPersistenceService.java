package org.csstudio.dct.model.persistence;

import java.io.InputStream;

import org.csstudio.dct.metamodel.IDatabaseDefinition;
import org.csstudio.dct.model.internal.Project;
import org.eclipse.core.resources.IFile;

/**
 * Persistence services for CSS-DCT.
 *
 * @author Sven Wende
 *
 */
public interface IPersistenceService {

    /**
     * Save the specified project to a file.
     *
     * @param file
     *            the file
     * @param project
     *            the project
     *
     * @throws Exception
     */
    void saveProject(IFile file, Project project) throws Exception;

    /**
     * Returns the specified project as input stream of xml data.
     * @param project the project
     * @return an xml input stream
     * @throws Exception
     */
    InputStream getAsStream(Project project) throws Exception;

    /**
     * Loads a project from the specified file.
     *
     * @param file
     *            the file
     * @return the project or null
     *
     * @throws Exception
     */
    Project loadProject(IFile file) throws Exception;

    /**
     * Loads a database definition (meta model) from a file (dbd).
     *
     * @param path
     *            the file path (workspace relative or absolute)
     *
     * @return the database definition or null
     */
    IDatabaseDefinition loadDatabaseDefinition(String path);

}
