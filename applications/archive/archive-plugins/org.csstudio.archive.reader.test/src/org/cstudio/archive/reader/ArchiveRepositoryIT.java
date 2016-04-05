/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.cstudio.archive.reader;

import static org.junit.Assert.assertTrue;

import org.csstudio.archive.reader.ArchiveRepository;
import org.junit.Test;

/** [Headless] JUnit Plug-in Test of the ArchiveRepository
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ArchiveRepositoryHeadlessTest
{
    @Test
    public void testArchiveRepository() throws Exception
    {
        final ArchiveRepository archives = ArchiveRepository.getInstance();
        System.out.println("Located support for these archive URL prefixes:");
        final String prefixes[] = archives.getSupportedPrefixes();
        for (String prefix : prefixes)
            System.out.println(prefix);
        assertTrue(prefixes.length > 0);
    }
}
