/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.util.dnd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.ui.util.ReflectUtil;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * Drag-and-Drop Transfer for any serializable object.
 *
 * @author Gabriele Carcassi
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SerializableItemTransfer extends ByteArrayTransfer
{
    /** Type handled by this Transfer */
	final private String className;

	/** Name of the type handled by this Transfer ('java:' + class name) */
    final private String typeName;

    /** Drag-and-Drop type ID for the <code>typeName</code> */
    final private int typeId;

    /** Cache of types to the SerializableItemTransfer for that type */
    final private static Map<String, SerializableItemTransfer> instances =
        new HashMap<String, SerializableItemTransfer>();

    /** @param classes Types to be transferred
     *  @return Transfers for those types
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Transfer[] getTransfers(Class[] classes)
    {
    	final Transfer[] transfers = new Transfer[classes.length];
    	for (int i = 0; i < classes.length ; i++)
    	{
    		Transfer transfer = getTransfer(classes[i]);
    		transfers[i] = transfer;
    	}
    	return transfers;
    }

    public static Collection<Transfer> getTransfers(Collection<String> classeNames) {
    	Collection<Transfer> transfers = new ArrayList<Transfer>();
    	for (String className : classeNames) {
    		transfers.add(getTransfer(className));
    	}
    	return transfers;
    }

    public static SerializableItemTransfer getTransfer(Class<? extends Serializable> clazz) {
    	return getTransfer(clazz.getName());
    }

    /** @param clazz Type to be transferred
     *  @return Transfer for that type
     */
    public static SerializableItemTransfer getTransfer(String className) {
    	SerializableItemTransfer transfer = instances.get(className);
    	if (transfer == null) {
    		transfer = new SerializableItemTransfer(className);
    		instances.put(className, transfer);
    	}
    	return transfer;
    }

    /** Initialize
     *  @param clazz Type handled by this Transfer
     */
    private SerializableItemTransfer(final String className)
    {
    	this.className = className;
    	typeName = "java:" + className;
    	typeId = registerType(typeName);
    }

    /** {@inheritDoc} */
    @Override
    protected int[] getTypeIds()
    {
        return new int[] { typeId };
    }

    /** {@inheritDoc} */
    @Override
    protected String[] getTypeNames()
    {
        return new String[] { typeName };
    }

    public String getClassName() {
		return className;
	}

    /** Serialize item
     *  {@inheritDoc}
     */
    @Override
    public void javaToNative (final Object object, final TransferData transferData)
    {
    	// Check that it's an object of the right type
    	if (!ReflectUtil.isInstance(object, getClassName())) {
    		throw new IllegalArgumentException("Trying to serialize and object of the wrong type");
        }

        try
        {
            // Write data to a byte array
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(object);
            final byte[] buffer = out.toByteArray();
            oos.close();

            // ByteArrayTransfer converts to medium
            super.javaToNative(buffer, transferData);
        }
        catch (IOException ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Serialization failed", ex);
        }
    }

    /** De-serialize items
     *  {@inheritDoc}
     */
    @Override
    public Object nativeToJava(final TransferData transferData)
    {
        if (!isSupportedType(transferData))
            return null;

        final byte[] buffer = (byte[]) super.nativeToJava(transferData);
        if (buffer == null)
            return null;


        final Object obj;
        try
        {
            final ByteArrayInputStream in = new ByteArrayInputStream(buffer);
            final ObjectInputStream readIn = new ObjectInputStreamWithOsgiClassResolution(in);
            obj = readIn.readObject();
            readIn.close();
            return obj;
        }
        catch (Exception ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "De-Serialization failed", ex);
        }
        return null;
    }

    @Override
    public String toString()
    {
        return "SerializableItemTransfer for " + typeName;
    }
}
