/**
 * Owned by DESY.
 */
package org.csstudio.platform.internal.model;

import static org.junit.Assert.*;

import org.csstudio.platform.model.IProcessVariable;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link ProcessVariableFactory}.
 * 
 * @author Sven Wende
 * 
 */
public final class ProcessVariableFactoryTest {

	/**
	 * Sample factory.
	 */
	private ProcessVariableFactory _factory;

	/**
	 * Sample process variable.
	 */
	private IProcessVariable _processVariable;

	/**
	 */
	@Before
	public void setUp() {
		_factory = new ProcessVariableFactory();
		_processVariable = new ProcessVariable("pv");
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.internal.model.ProcessVariableFactory#createStringRepresentationFromItem(org.csstudio.platform.model.IProcessVariable)}.
	 */
	@Test
	public void testCreateStringRepresentationFromItemIProcessVariable() {
		String s = _factory
				.createStringRepresentationFromItem(_processVariable);
		assertNotNull(s);
	}

	/**
	 * Test method for
	 * {@link org.csstudio.platform.internal.model.ProcessVariableFactory#createItemFromStringRepresentation(java.lang.String)}.
	 */
	@Test
	public void testCreateItemFromStringRepresentationString() {
		String s = _factory
				.createStringRepresentationFromItem(_processVariable);
		assertNotNull(s);
		IProcessVariable processVariable = _factory
				.createItemFromStringRepresentation(s);
		assertNotNull(processVariable);
		
		assertEquals(_processVariable.getName(), processVariable.getName());

	}

}
