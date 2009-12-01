package org.csstudio.opibuilder.converter.model;

import junit.framework.TestCase;

public class EdmWidgetTest extends TestCase {

	public void testEdmWidget() throws EdmException {
		
		EdmEntity e = new EdmEntity("test");
		
		EdmWidget w = new EdmWidget(e);
		assertNotNull(w);
		assertTrue(w instanceof EdmEntity);
	}
}
