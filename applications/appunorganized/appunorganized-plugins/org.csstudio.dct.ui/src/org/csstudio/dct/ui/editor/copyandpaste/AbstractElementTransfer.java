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
package org.csstudio.dct.ui.editor.copyandpaste;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.csstudio.dct.model.IElement;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractElementTransfer extends ByteArrayTransfer {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractElementTransfer.class);

    private final ICopyAndPasteStrategy copyAndPasteStrategy;

    /**
     * Type name for this transfer type.
     */
    private static final String TYPENAME = "dct_record_list"; //$NON-NLS-1$

    /**
     * Type ID for this transfer type.
     */
    private static final int TYPEID = registerType(TYPENAME);

    /**
     * Private constructor (singleton pattern).
     *
     */
    AbstractElementTransfer(ICopyAndPasteStrategy strategy) {
        assert strategy != null;
        copyAndPasteStrategy = strategy;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void javaToNative(final Object object, final TransferData transferData) {
        if (!isSupportedType(transferData) || !(checkInput(object))) {
            DND.error(DND.ERROR_INVALID_DATA);
        }

        // clean up
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            ObjectOutputStream objOut = new ObjectOutputStream(new BufferedOutputStream(bos));
            objOut.writeObject(copyAndPasteStrategy.createCopyElements((List<IElement>) object));
            objOut.close();

            byte[] bytes = bos.toByteArray();

            // store the byte array
            super.javaToNative(bytes, transferData);

            bos.close();
        } catch (IOException e) {
            LOG.debug("IO Error", e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object nativeToJava(final TransferData transferData) {
        if (isSupportedType(transferData)) {
            byte[] bytes = (byte[]) super.nativeToJava(transferData);

            try {
                ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(bytes));
                Object result = objIn.readObject();
                objIn.close();

                return result;
            } catch (Exception e) {
                LOG.debug("Input Error", e);
                return null;
            }
        }
        return null;
    }

    /**
     * Checks the provided input, which must be a non-empty list that contains
     * only objects of type {@link IElement}.
     *
     * @param input
     *            the input to check
     * @return true, if the input object is valid, false otherwise
     */
    private boolean checkInput(final Object input) {
        boolean result = false;

        if (input instanceof List) {
            List list = (List) input;

            if (!list.isEmpty()) {
                result = true;
                for (Object o : list) {
                    result &= o instanceof IElement;
                }

                if (result) {
                    result &= copyAndPasteStrategy.canCopy((List<IElement>) input);
                }
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String[] getTypeNames() {
        return new String[] { TYPENAME };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int[] getTypeIds() {
        return new int[] { TYPEID };
    }

    public ICopyAndPasteStrategy getCopyAndPasteStrategy() {
        return copyAndPasteStrategy;
    }
}
