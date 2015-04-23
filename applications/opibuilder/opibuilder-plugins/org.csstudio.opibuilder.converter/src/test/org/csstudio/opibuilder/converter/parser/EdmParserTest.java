/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.parser;

import junit.framework.TestCase;

import org.csstudio.opibuilder.converter.model.EdmException;

public class EdmParserTest extends TestCase {

	public void testFileDoesNotExist() {
		String fileName = "test.edl";
		
		try {
			@SuppressWarnings("unused")
			EdmParser edmParser = new EdmParser(fileName);
		}
		catch (Exception e){
			assertTrue(e instanceof EdmException);
			EdmException edmException = (EdmException)e;
			assertEquals(edmException.getType(), EdmException.FILE_NOT_FOUND);
			assertEquals("FILE_NOT_FOUND exception: File " + fileName + " does not exist.",
					edmException.getMessage());
		}
	}
}
