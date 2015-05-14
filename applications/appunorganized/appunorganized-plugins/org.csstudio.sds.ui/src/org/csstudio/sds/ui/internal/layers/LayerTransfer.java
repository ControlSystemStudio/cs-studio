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
 package org.csstudio.sds.ui.internal.layers;

import org.csstudio.sds.internal.model.Layer;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.Policy;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * Transfer handler for layers. Implements a pattern similar to
 * {@link LocalSelectionTransfer}.
 *
 * @author swende
 *
 */
final class LayerTransfer extends ByteArrayTransfer {

    /**
     * Type name.
     */
    private static final String TYPE_NAME = "sds.layertransfer";

    /**
     * Type identifier.
     */
    private static final int TYPE_ID = registerType(TYPE_NAME);

    /**
     * Singleton instance.
     */
    private static LayerTransfer _instance;

    /**
     * Currently selected layer.
     */
    private Layer _selectedLayer;

    /**
     * Private constructor (singleton pattern).
     */
    private LayerTransfer() {

    }

    /**
     * Returns the singleton instance.
     *
     * @return the singleton instance
     */
    public static LayerTransfer getInstance() {
        if (_instance == null) {
            _instance = new LayerTransfer();
        }
        return _instance;
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
     * {@inheritDoc}
     */
    @Override
    public void javaToNative(final Object object,
            final TransferData transferData) {
        byte[] check = TYPE_NAME.getBytes();
        super.javaToNative(check, transferData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object nativeToJava(final TransferData transferData) {
        Object result = super.nativeToJava(transferData);
        if (isInvalidNativeType(result)) {
            Policy
                    .getLog()
                    .log(
                            new Status(
                                    IStatus.ERROR,
                                    Policy.JFACE,
                                    IStatus.ERROR,
                                    JFaceResources
                                            .getString("LocalSelectionTransfer.errorMessage"), null)); //$NON-NLS-1$
        }
        return _selectedLayer;
    }

    /**
     * Tests whether native drop data matches this transfer type.
     *
     * @param result
     *            result of converting the native drop data to Java
     * @return true if the native drop data does not match this transfer type.
     *         false otherwise.
     */
    private boolean isInvalidNativeType(final Object result) {
        return !(result instanceof byte[])
                || !TYPE_NAME.equals(new String((byte[]) result));
    }

    /**
     * Sets the transfer data for local use.
     *
     * @param selectedLayer
     *            the transfered layer
     */
    public void setSelectedLayer(final Layer selectedLayer) {
        _selectedLayer = selectedLayer;
    }
}
