package org.csstudio.dct.model.commands;

import java.util.UUID;

import org.csstudio.dct.DctActivator;
import org.csstudio.dct.model.internal.Project;
import org.junit.Before;

public abstract class AbstractCommandTest {
	protected Project project;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		project = new Project("test", UUID.randomUUID());
		DctActivator.getDefault().getPersistenceService().loadDatabaseDefinition(getClass().getResource("test.dbd").getFile());
		
		doSetUp();
	}

	protected abstract void doSetUp() throws Exception;
}
