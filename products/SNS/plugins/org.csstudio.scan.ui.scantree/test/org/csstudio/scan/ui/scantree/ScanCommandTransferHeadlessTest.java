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
import static org.junit.Assert.assertSame;

import org.csstudio.scan.command.LogCommand;
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
        final LogCommand command = new LogCommand("device1", "device2");
        System.out.println("Original: " + command);

        final TransferData data = ScanCommandTransfer.getInstance().getSupportedTypes()[0];
        ScanCommandTransfer.getInstance().javaToNative(command, data);
        final Object copy = ScanCommandTransfer.getInstance().nativeToJava(data);

        System.out.println("Transfer: " + copy);


        assertNotNull(copy);
        assertSame(LogCommand.class, copy.getClass());
        final LogCommand command2 = (LogCommand) copy;
        assertArrayEquals(command.getDeviceNames(), command2.getDeviceNames());
        // assertEquals(command, copy);
    }
}
