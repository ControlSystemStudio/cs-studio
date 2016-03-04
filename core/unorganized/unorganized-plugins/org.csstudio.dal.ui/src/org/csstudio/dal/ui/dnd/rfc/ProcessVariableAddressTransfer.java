/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.dal.ui.dnd.rfc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * Transfer class for {@link IProcessVariableAddress} objects.
 *
 * @author Sven Wende
 *
 */
public class ProcessVariableAddressTransfer extends ByteArrayTransfer {

    /**
     * The time period in which a local selection expires. Needed to handle
     * cases, where data is dragged between different JVMs.
     */
    private static final int SELECTION_EXPIRATION_PERIOD = 10000;

    private static final String TYPE_NAME = "dnd_process_variablev_name";

    private static final int TYPE_ID = registerType(TYPE_NAME);

    private static ProcessVariableAddressTransfer _instance = new ProcessVariableAddressTransfer();

    /**
     * The items that were selected during the latest DnD operation.
     */
    private List<IProcessVariableAddress> _selectedItems;

    /**
     * The event time of the latest selection.
     */
    private long _selectionSetTime;

    /**
     * Hidden constructor.
     */
    private ProcessVariableAddressTransfer() {
        _selectionSetTime = 0;
    }

    /**
     * Returns the instance of this ProcessVariableAddressTransfer.
     *
     * @return ProcessVariableAddressTransfer The instance of this
     *         ProcessVariableAddressTransfer
     */
    public static ProcessVariableAddressTransfer getInstance() {
        return _instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void javaToNative(Object object, TransferData transferData) {
        if (object == null
                || !(object instanceof IProcessVariableAddress[] || object instanceof List))
            return;
        if (isSupportedType(transferData)) {
            IProcessVariableAddress[] pvs;
            if (object instanceof List) {
                pvs = (IProcessVariableAddress[]) ((List) object)
                        .toArray(new IProcessVariableAddress[((List) object)
                                .size()]);
            } else {
                pvs = (IProcessVariableAddress[]) object;
            }
            try {
                // write data to a byte array and then ask super to convert to
                // pMedium
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                DataOutputStream writeOut = new DataOutputStream(out);
                for (int i = 0, length = pvs.length; i < length; i++) {
                    byte[] buffer = pvs[i].getRawName().getBytes();
                    writeOut.writeInt(buffer.length);
                    writeOut.write(buffer);
                }
                byte[] buffer = out.toByteArray();
                writeOut.close();

                super.javaToNative(buffer, transferData);

            } catch (IOException e) {
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object nativeToJava(TransferData transferData) {
        if (isSupportedType(transferData)) {
            byte[] buffer = (byte[]) super.nativeToJava(transferData);
            if (buffer == null)
                return null;

            IProcessVariableAddress[] processVariables = new IProcessVariableAddress[0];
            try {
                ByteArrayInputStream in = new ByteArrayInputStream(buffer);
                DataInputStream readIn = new DataInputStream(in);
                while (readIn.available() > 0) {
                    int size = readIn.readInt();
                    byte[] pathBytes = new byte[size];
                    readIn.read(pathBytes);
                    String fullPath = new String(pathBytes);

                    IProcessVariableAddress pv = ProcessVariableAdressFactory
                            .getInstance()
                            .createProcessVariableAdress(fullPath);
                    // ProcessVariable pv = new ProcessVariable(controlSystem,
                    // device, property, characteristic);
                    IProcessVariableAddress[] newProcessVariables = new IProcessVariableAddress[processVariables.length + 1];
                    System.arraycopy(processVariables, 0, newProcessVariables,
                            0, processVariables.length);
                    newProcessVariables[processVariables.length] = pv;
                    processVariables = newProcessVariables;
                }
                readIn.close();
            } catch (IOException ex) {
                return null;
            }
            return processVariables;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String[] getTypeNames() {
        return new String[] { TYPE_NAME };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int[] getTypeIds() {
        return new int[] { TYPE_ID };
    }

    /**
     * Gets the transfer data for local use.
     *
     * @return List of {@link IProcessVariableAddress} The transfer data for
     *         local use.
     */
    public List<IProcessVariableAddress> getSelectedItems() {
        long dist = new Date().getTime() - _selectionSetTime;

        if (dist > SELECTION_EXPIRATION_PERIOD) {
            return null;
        }
        return _selectedItems;
    }

    /**
     * Sets the transfer data for local use.
     *
     * @param selectedItems
     *            the transfer data
     */
    public void setSelectedItems(
            final List<IProcessVariableAddress> selectedItems) {
        _selectedItems = selectedItems;
        _selectionSetTime = new Date().getTime();
    }
}
