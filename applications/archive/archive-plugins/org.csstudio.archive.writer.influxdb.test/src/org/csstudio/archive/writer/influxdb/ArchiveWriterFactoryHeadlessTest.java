/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.writer.influxdb;

import static org.junit.Assert.assertNotNull;

import org.csstudio.archive.writer.ArchiveWriter;
import org.csstudio.archive.writer.ArchiveWriterFactory;
import org.junit.Test;

/** [Headless] JUnit Plug-in test/demo if the {@link ArchiveWriterFactory}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ArchiveWriterFactoryHeadlessTest
{
    @Test
    public void testChannelLookup() throws Exception
    {
        final ArchiveWriter writer = ArchiveWriterFactory.getArchiveWriter();
        assertNotNull(writer);
        System.out.println("Found " + writer.getClass().getName());
        writer.close();
    }
}
