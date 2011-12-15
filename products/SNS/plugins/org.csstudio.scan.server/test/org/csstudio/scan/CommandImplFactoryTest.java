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

import static org.junit.Assert.*;

import org.csstudio.scan.command.CommandImpl;
import org.csstudio.scan.command.CommandImplFactory;
import org.csstudio.scan.command.DelayCommand;
import org.csstudio.scan.command.DelayCommandImpl;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.LoopCommandImpl;
import org.csstudio.scan.command.ScanCommand;
import org.junit.Test;

/** JUnit test of the {@link CommandImplFactory}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CommandImplFactoryTest
{
    @Test
    public void testImplementScanCommand() throws Exception
    {
        final ScanCommand delay = new DelayCommand(1.0);
        CommandImpl<?> impl = CommandImplFactory.implement(delay);
        assertSame(delay, impl.getCommand());
        assertSame(DelayCommandImpl.class, impl.getClass());

        final LoopCommand loop = new LoopCommand("device", 1, 10, 1, delay);
        impl = CommandImplFactory.implement(loop);
        assertSame(loop, impl.getCommand());
        assertSame(LoopCommandImpl.class, impl.getClass());
    }
}
