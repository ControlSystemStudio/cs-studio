/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/** JUnit test of the {@link CommentCommand}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CommentCommandUnitTest
{
    @Test
    public void testComment() throws Exception
    {
        final ScanCommand command = new CommentCommand("Test is OK");

        final String xml = XMLCommandWriter.toXMLString(Arrays.asList(command));

        final List<ScanCommand> commands = new XMLCommandReader(new SimpleScanCommandFactory()).readXMLString(xml);
        assertEquals(1, commands.size());
        assertSame(CommentCommand.class, commands.get(0).getClass());
        final CommentCommand comment = (CommentCommand) commands.get(0);
        System.out.println(comment);
        assertEquals("Test is OK", comment.getComment());
    }
}
