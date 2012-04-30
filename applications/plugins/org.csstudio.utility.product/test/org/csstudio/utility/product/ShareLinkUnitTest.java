/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.product;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** JUnit test of the {@link LinkedResource}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ShareLinkUnitTest
{
    @Test
	public void testShareLinkParser() throws Exception
	{
		final String option = "/path/to/share,\"/path/to/an other\"=/CSS/Another,/mount/ops/share=/Operation";

		final LinkedResource[] links = LinkedResource.fromString(option);
		for (LinkedResource link : links)
			System.out.println(link);

		assertEquals(3, links.length);
		assertEquals("/CSS/Share", links[0].getResourceName());
		assertEquals("/path/to/an other", links[1].getFileSystemName());
		assertEquals("/Operation", links[2].getResourceName());
		assertFalse(links[0].isProject());
		assertFalse(links[1].isProject());
		assertTrue(links[2].isProject());
	}

    @Test
	public void testShareLinkErrors() throws Exception
	{
    	// Empty input -> No resources
		LinkedResource[] links = LinkedResource.fromString("");
		assertEquals(0, links.length);

		// Missing resource name in spite of '='
		links = LinkedResource.fromString("/some/path=");
		for (LinkedResource link : links)
			System.out.println(link);
		assertEquals(1, links.length);
		assertEquals("/some/path", links[0].getFileSystemName());
		assertEquals("/CSS/Share", links[0].getResourceName());

		// Add '/' to resource
		links = LinkedResource.fromString("/some/path=project/folder");
		for (LinkedResource link : links)
			System.out.println(link);
		assertEquals(1, links.length);
		assertEquals("/some/path", links[0].getFileSystemName());
		assertEquals("/project/folder", links[0].getResourceName());
	}
}
