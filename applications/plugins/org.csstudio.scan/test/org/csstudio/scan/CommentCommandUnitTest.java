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
package org.csstudio.scan;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.csstudio.scan.command.CommentCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.SimpleScanCommandFactory;
import org.csstudio.scan.command.XMLCommandReader;
import org.csstudio.scan.command.XMLCommandWriter;
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
        assertThat(commands.size(), equalTo(1));
        assertThat(commands.get(0).getClass().getName(), equalTo(CommentCommand.class.getName()));
        final CommentCommand comment = (CommentCommand) commands.get(0);
        System.out.println(comment);
        assertThat(comment.getComment(), equalTo("Test is OK"));
    }
}
