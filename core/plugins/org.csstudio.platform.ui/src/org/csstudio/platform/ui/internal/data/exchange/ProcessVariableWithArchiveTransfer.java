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

/** Drag-and-Drop transfer type for <code>IProcessVariableWithArchive</code>.
 *  <p>
 *  This transfer type expects the data to transfer to implement the
 *  <code>IProcessVariableWithArchive</code> interface, and the resulting
 *  data is provided as an array of <code>ProcessVariableWithArchive</code>.
 *  <p>
 *  Most of this implementation is from the javadoc for ByteArrayTransfer.
 *  @author Kay Kasemir
 */
public class ProcessVariableWithArchiveTransfer extends ByteArrayTransfer
{
    private static final String TYPE_NAME = "pv_with_archive_data_source";

    private static final int TYPE_ID = registerType(TYPE_NAME);

    private static ProcessVariableWithArchiveTransfer singleton_instance
        = new ProcessVariableWithArchiveTransfer();

    /** Hidden contructor.
     *  @see #getInstance()
     */
    private ProcessVariableWithArchiveTransfer()
    {}

    /** @return The singleton instance of the ArchiveDataSourceTransfer. */
    public static ProcessVariableWithArchiveTransfer getInstance()
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
        if (!isSupportedType(transferData))
            return;
        
        IProcessVariableWithArchive[] data;
        if (object == null)
            return;
        if (object instanceof IProcessVariableWithArchive[])
            data = (IProcessVariableWithArchive[]) object;
        else if (object instanceof ArrayList)
        {   // Table selection seems to use ArrayList
            ArrayList list = (ArrayList) object;
            data = new IProcessVariableWithArchive[list.size()];
            for (int i = 0; i < data.length; i++)
                data[i] = (IProcessVariableWithArchive) list.get(i);
        }
        else
            return;
        try
        {
            // write data to a byte array and then ask super to convert to
            // pMedium
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DataOutputStream writeOut = new DataOutputStream(out);
            for (int i = 0; i < data.length; ++i)
            {
                byte[] buffer = data[i].getName().getBytes();
                writeOut.writeInt(buffer.length);
                writeOut.write(buffer);

                IArchiveDataSource archive = data[i].getArchiveDataSource();
                buffer = archive.getUrl().getBytes();
                writeOut.writeInt(buffer.length);
                writeOut.write(buffer);
                
                writeOut.writeInt(archive.getKey());
                
                buffer = archive.getName().getBytes();
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
        if (!isSupportedType(transferData))
            return null;

        byte[] buffer = (byte[]) super.nativeToJava(transferData);
        if (buffer == null)
            return null;

        Vector<ProcessVariableWithArchive> received =
            new Vector<ProcessVariableWithArchive>();
        try
        {
            ByteArrayInputStream in = new ByteArrayInputStream(buffer);
            DataInputStream readIn = new DataInputStream(in);
            // URL length, key, name length = 12?
            while (readIn.available() > 12)
            {
                int size = readIn.readInt();
                byte[] bytes = new byte[size];
                readIn.read(bytes);
                String pv = new String(bytes);
                
                size = readIn.readInt();
                bytes = new byte[size];
                readIn.read(bytes);
                String url = new String(bytes);
                
                int key = readIn.readInt();
                
                size = readIn.readInt();
                bytes = new byte[size];
                readIn.read(bytes);
                String arch_name = new String(bytes);
                
                received.add(
                     new ProcessVariableWithArchive(pv, url, key, arch_name));
            }
            readIn.close();
        }
        catch (IOException ex)
        {
            return null;
        }
        ProcessVariableWithArchive array[] = 
            new ProcessVariableWithArchive[received.size()];
        return received.toArray(array);
    }
}
