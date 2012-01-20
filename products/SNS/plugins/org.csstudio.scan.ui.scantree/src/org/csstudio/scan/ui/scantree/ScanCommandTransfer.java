/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.XMLCommandReader;
import org.csstudio.scan.command.XMLCommandWriter;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

/** Drag-and-drop transfer for {@link ScanCommand}s
 * 
 *  <p>Internally transfers the XML representation of a command.
 *  
 *  @author Kay Kasemir
 */
public class ScanCommandTransfer extends ByteArrayTransfer
{
    final private static String NAME = "ScanCommand"; //$NON-NLS-1$
    final private static int ID = registerType(NAME);
    final private static ScanCommandTransfer instance = new ScanCommandTransfer();
    
    /** Prevent instantiation */
    private ScanCommandTransfer()
    {
    }
    
    /** @return Singleton instance */
    public static ScanCommandTransfer getInstance()
    {
        return instance;
    }

    /** {@inheritDoc} */
    @Override
    protected int[] getTypeIds()
    {
        return new int[] { ID };
    }

    /** {@inheritDoc} */
    @Override
    protected String[] getTypeNames()
    {
        return new String[] { NAME };
    }

    /** Convert ScanCommand to XML and send via ByteArrayTransfer */
    @Override
    protected void javaToNative(final Object object, final TransferData transfer)
    {
        if (! (object instanceof ScanCommand))
            DND.error(DND.ERROR_INVALID_DATA);
        final ScanCommand command = (ScanCommand) object;

        try
        {
            final ByteArrayOutputStream buf = new ByteArrayOutputStream();
            XMLCommandWriter.write(buf, Arrays.asList(command));
            buf.close();
            super.javaToNative(buf.toByteArray(), transfer);
        }
        catch (Exception ex)
        {
            // Ignore
            ex.printStackTrace();
        }
    }

    /** Receive XML via ByteArrayTransfer, decode ScanCommand */
    @Override
    protected Object nativeToJava(TransferData transferData)
    {
        try
        {
            final byte[] bytes = (byte[]) super.nativeToJava(transferData);
            final ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            final List<ScanCommand> commands = XMLCommandReader.readXMLStream(stream);
            stream.close();
            
            if (commands.size() >= 1)
                return commands.get(0);
        }
        catch (Exception ex)
        {
            // Ignore
            ex.printStackTrace();
        }
        return null;
    }
}
