package org.csstudio.platform.ui.internal.data.exchange;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IArchiveDataSource;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/** Drag-and-Drop transfer type for <code>IArchiveDataSource</code>.
 *  <p>
 *  This transfer type expects the data to transfer to implement the
 *  <code>IArchiveDataSource</code> interface, and the resulting
 *  data is provided as an array of <code>ArchiveDataSource</code>.
 *  <p>
 *  Most of this implementation is from the javadoc for ByteArrayTransfer.
 *  @author Kay Kasemir
 */
public class ArchiveDataSourceTransfer extends ByteArrayTransfer
{
    private static final String TYPE_NAME = "archive_data_source";

    private static final int TYPE_ID = registerType(TYPE_NAME);

    private static ArchiveDataSourceTransfer singleton_instance
        = new ArchiveDataSourceTransfer();

    /** Hidden contructor.
     *  @see #getInstance()
     */
    private ArchiveDataSourceTransfer()
    {}

    /** @return The singleton instance of the ArchiveDataSourceTransfer. */
    public static ArchiveDataSourceTransfer getInstance()
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
        
        IArchiveDataSource[] archives;
        if (object == null)
            return;
        if (object instanceof IArchiveDataSource[])
            archives = (IArchiveDataSource[]) object;
        else if (object instanceof ArrayList)
        {   // Table selection seems to use ArrayList
            ArrayList list = (ArrayList) object;
            archives = new IArchiveDataSource[list.size()];
            for (int i = 0; i < archives.length; i++)
                archives[i] = (IArchiveDataSource) list.get(i);
        }
        else
            return;
        try
        {
            // write data to a byte array and then ask super to convert to
            // pMedium
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DataOutputStream writeOut = new DataOutputStream(out);
            for (int i = 0; i < archives.length; ++i)
            {
                byte[] buffer = archives[i].getUrl().getBytes();
                writeOut.writeInt(buffer.length);
                writeOut.write(buffer);
                
                writeOut.writeInt(archives[i].getKey());
                
                buffer = archives[i].getName().getBytes();
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

        Vector<IArchiveDataSource> received = new Vector<IArchiveDataSource>();
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
                String url = new String(bytes);
                
                int key = readIn.readInt();
                
                size = readIn.readInt();
                bytes = new byte[size];
                readIn.read(bytes);
                String name = new String(bytes);
                
                received.add(CentralItemFactory.createArchiveDataSource(url, key, name));
            }
            readIn.close();
        }
        catch (IOException ex)
        {
            return null;
        }
        IArchiveDataSource array[] = new IArchiveDataSource[received.size()];
        return received.toArray(array);
    }
}
