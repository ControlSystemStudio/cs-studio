/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
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

import static org.junit.Assert.assertSame;

import org.csstudio.scan.command.DelayCommand;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.commandimpl.DelayCommandImpl;
import org.csstudio.scan.commandimpl.LoopCommandImpl;
import org.csstudio.scan.server.ScanCommandImpl;
import org.csstudio.scan.server.ScanCommandImplTool;
import org.junit.Test;

/** [Headless] JUnit Plug-In test of the {@link ScanCommandImplTool}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanCommandImplToolHeadlessTest
{
    @Test
    public void testImplementScanCommand() throws Exception
    {
        final ScanCommandImplTool tool = ScanCommandImplTool.getInstance();

        final ScanCommand delay = new DelayCommand(1.0);
        ScanCommandImpl<?> impl = tool.implement(delay);
        assertSame(delay, impl.getCommand());
        assertSame(DelayCommandImpl.class, impl.getClass());

        final LoopCommand loop = new LoopCommand("device", 1, 10, 1, delay);
        impl = tool.implement(loop);
        assertSame(loop, impl.getCommand());
        assertSame(LoopCommandImpl.class, impl.getClass());
    }
}
