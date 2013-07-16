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
package org.csstudio.scan.ui.scantree;

import static org.junit.Assert.assertTrue;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.ui.scantree.gui.CommandsInfo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.junit.Test;

/** Headless JUnit Plug-in test of the command registry lookup
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CommandsInfoHeadlessTest
{
    @Test
    public void listCommands() throws Exception
    {
        // Create display, required by ImageRegistry used by CommandsInfo
        Display.getDefault();
        
        final CommandsInfo info = CommandsInfo.getInstance();
        final ScanCommand[] commands = info.getCommands();
        
        for (ScanCommand command : commands)
        {
            System.out.println(command.getClass().getName() + ":");
            command.writeXML(System.out, 1);

            final Image image = info.getImage(command);
            System.out.println("Icon: " +
            image.getBounds().width + " x " + image.getBounds().height);
            System.out.println();
        }
        assertTrue(commands.length > 0);
    }
}
