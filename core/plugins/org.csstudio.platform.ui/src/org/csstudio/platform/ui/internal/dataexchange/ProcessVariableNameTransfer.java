/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.platform.ui.internal.dataexchange;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IProcessVariable;
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
    	// System.out.println("javaToNative");
        if (!isSupportedType(transferData))
            return;

        IProcessVariable[] pvs;
        if (object == null)
            return;
        if (object instanceof IProcessVariable[])
            pvs = (IProcessVariable[]) object;
        else if (object instanceof ArrayList)
        {   // Table selection seems to use ArrayList
            ArrayList list = (ArrayList) object;
            pvs = new IProcessVariable[list.size()];
            for (int i = 0; i < pvs.length; i++)
                pvs[i] = (IProcessVariable) list.get(i);
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
        if (!isSupportedType(transferData))
            return null;

        byte[] buffer = (byte[]) super.nativeToJava(transferData);
        if (buffer == null)
            return null;

        Vector<IProcessVariable> received = new Vector<IProcessVariable>();
        try
        {
            ByteArrayInputStream in = new ByteArrayInputStream(buffer);
            DataInputStream readIn = new DataInputStream(in);
            while (readIn.available() > 4)
            {
                int size = readIn.readInt();
                byte[] bytes = new byte[size];
                readIn.read(bytes);
                received.add(CentralItemFactory.createProcessVariable(new String(bytes)));
            }
            readIn.close();
        }
        catch (IOException ex)
        {
            return null;
        }
        IProcessVariable array[] = new IProcessVariable[received.size()];
        return received.toArray(array);
    }
}
