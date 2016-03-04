package org.csstudio.dct.model.commands;

import org.csstudio.dct.model.internal.Project;
import org.csstudio.dct.model.internal.ProjectFactory;
import org.csstudio.dct.model.persistence.IPersistenceService;
import org.csstudio.dct.model.persistence.internal.PersistenceService;
import org.junit.Before;

/**
 * Base class for command tests.
 *
 * @author Sven Wende
 *
 */
public abstract class AbstractTestCommand {
    private Project project;
    private IPersistenceService persistenceService;
    /**
     * @throws java.lang.Exception
     */
    @Before
    public final void setUp() throws Exception {
        persistenceService = new PersistenceService();
        project = ProjectFactory.createNewDCTProject();
        persistenceService.loadDatabaseDefinition(getClass().getResource("test.dbd").getFile());

        doSetUp();
    }

    /**
     * Template method. Subclasses may override for doing setup.
     *
     * @throws Exception
     */
    protected abstract void doSetUp() throws Exception;

    /**
     * Returns a dummy project that is already equipped with a meta model from a
     * dbd file.
     *
     * @return a dummy project that is already equipped from a dbd file
     */
    public final Project getProject() {
        return project;
    }
}
