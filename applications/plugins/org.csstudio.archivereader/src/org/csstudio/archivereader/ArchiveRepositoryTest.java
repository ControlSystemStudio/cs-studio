/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archivereader;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** JUnit Plug-in Test of the ArchiveRepository
 *  @author Kay Kasemir
 */
public class ArchiveRepositoryTest
{
    @SuppressWarnings("nls")
    @Test
    public void testArchiveRepository() throws Exception
    {
        // FIXME (kasemir) : Test with sysos?! Use assertions for expected prefixes
        final ArchiveRepository archives = ArchiveRepository.getInstance();
        //System.out.println("Located support for these archive URL prefixes:");
        final String prefixes[] = archives.getSupportedPrefixes();
//        for (String prefix : prefixes)
//            System.out.println(prefix);
        assertTrue(prefixes.length > 0);
    }
}
