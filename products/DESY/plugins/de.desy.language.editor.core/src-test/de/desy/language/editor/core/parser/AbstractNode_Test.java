package de.desy.language.editor.core.parser;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * The base class for all Tests with nodes.
 */
public abstract class AbstractNode_Test extends TestCase {

	@Test
	public void testChildren() {
		// TODO
		fail("Not yet implemented"); //$NON-NLS-1$
	}
	
	@Test
	public void testOffsets() {
		// TODO
		fail("Not yet implemented"); //$NON-NLS-1$
	}
	
	/**
	 * Returns always a new instance of node type under test.
	 */
	protected abstract Node getNewNodeInstance();
	
	/**
	 * Returns always a new instance of possible child node type.
	 */
	protected abstract Node getNewChildrenNodeInstance();
}
