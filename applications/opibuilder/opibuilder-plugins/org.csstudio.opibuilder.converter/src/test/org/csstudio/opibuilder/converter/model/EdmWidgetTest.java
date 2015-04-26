/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
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
