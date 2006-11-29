package org.csstudio.platform.ui.internal.data.exchange;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/** Drag-and-Drop transfer type for <code>IProcessVariableName</code>.
 *  <p>
 *  This transfer type expects the data to transfer to implement the
 *  <code>IProcessVariableName</code> interface, and the resulting
 *  data is provided as an array of <code>ProcessVariableName</code>.
 *  <p>
 *  Most of this implementation is from the javadoc for ByteArrayTransfer.
 *  @author Kay Kasemir
 */
public class ProcessVariableNameTransfer extends ByteArrayTransfer
{
    private static final String TYPE_NAME = "pv_name";

    private static final int TYPE_ID = registerType(TYPE_NAME);

    private static ProcessVariableNameTransfer singleton_instance
        = new ProcessVariableNameTransfer();

    /** Hidden contructor.
     *  @see #getInstance()
     */
    private ProcessVariableNameTransfer()
    {}

    /** @return The singleton instance of the ProcessVariableNameTransfer. */
    public static ProcessVariableNameTransfer getInstance()
    {
        return singleton_instance;
    }

    @Override
    protected String[] getTypeNames()
    {
        return new String[] { TYPE_NAME };
    }

    @Override
    protected int[] getTypeIds()
    {
        return new int[] { TYPE_ID };
    }

    @Override
    public void javaToNative(Object object, TransferData transferData)
    {
    	System.out.println("javaToNative");
        if (!isSupportedType(transferData))
            return;
        
        IProcessVariableName[] pvs;
        if (object == null)
            return;
        if (object instanceof IProcessVariableName[])
            pvs = (IProcessVariableName[]) object;
        else if (object instanceof ArrayList)
        {   // Table selection seems to use ArrayList
            ArrayList list = (ArrayList) object;
            pvs = new IProcessVariableName[list.size()];
            for (int i = 0; i < pvs.length; i++)
                pvs[i] = (IProcessVariableName) list.get(i);
        }
        else
            return;
        try
        {
            // write data to a byte array and then ask super to convert to
            // pMedium
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DataOutputStream writeOut = new DataOutputStream(out);
            for (int i = 0; i< pvs.length; ++i)
            {
                byte[] buffer = pvs[i].getName().getBytes();
                writeOut.writeInt(buffer.length);
                writeOut.write(buffer);
            }
            byte[] buffer = out.toByteArray();
            writeOut.close();

            super.javaToNative(buffer, transferData);
        }
        catch (IOException e)
        {
        }
    }

    @Override
    public Object nativeToJava(TransferData transferData)
    {
    	System.out.println("nativeToJava");
        if (!isSupportedType(transferData))
            return null;

        byte[] buffer = (byte[]) super.nativeToJava(transferData);
        if (buffer == null)
            return null;

        Vector<ProcessVariableName> received = new Vector<ProcessVariableName>();
        try
        {
            ByteArrayInputStream in = new ByteArrayInputStream(buffer);
            DataInputStream readIn = new DataInputStream(in);
            while (readIn.available() > 4)
            {
                int size = readIn.readInt();
                byte[] bytes = new byte[size];
                readIn.read(bytes);
                received.add(new ProcessVariableName(new String(bytes)));
            }
            readIn.close();
        }
        catch (IOException ex)
        {
            return null;
        }
        ProcessVariableName array[] = new ProcessVariableName[received.size()];
        return received.toArray(array);
    }
}
