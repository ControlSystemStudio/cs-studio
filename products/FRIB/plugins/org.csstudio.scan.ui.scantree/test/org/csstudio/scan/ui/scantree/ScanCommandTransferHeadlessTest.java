/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.scan.command.LogCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.ui.scantree.gui.ScanCommandTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.junit.Test;

/** [Headless] JUnit test of the {@link ScanCommandTransfer}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanCommandTransferHeadlessTest
{
    @Test
    public void test()
    {
        final List<ScanCommand> commands = new ArrayList<ScanCommand>();
        final LogCommand log = new LogCommand("device1", "device2");
        commands.add(log);
        System.out.println("Original: " + commands.get(0));

        final TransferData data = ScanCommandTransfer.getInstance().getSupportedTypes()[0];
        ScanCommandTransfer.getInstance().javaToNative(commands, data);
        final Object copy = ScanCommandTransfer.getInstance().nativeToJava(data);

        System.out.println("Transfer: " + copy);

        assertNotNull(copy);
        @SuppressWarnings("unchecked")
        final List<ScanCommand> commands2 = (List<ScanCommand>) copy;

        System.out.println("Copy: " + commands2.get(0));
        final LogCommand log2 = (LogCommand) commands2.get(0);

        assertArrayEquals(log.getDeviceNames(), log2.getDeviceNames());
        // assertEquals(command, copy);
    }
}
