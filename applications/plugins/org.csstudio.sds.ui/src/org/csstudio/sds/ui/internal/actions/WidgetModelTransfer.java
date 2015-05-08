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
 package org.csstudio.sds.ui.internal.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.csstudio.sds.internal.persistence.PersistenceUtil;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transfer type implementation for widget models. This implementation uses the
 * normal persistence mechanisms to convert a list of widget models to their xml
 * representation. This xml is then converted to a byte array.
 *
 * @author Sven Wende
 *
 */
public final class WidgetModelTransfer extends ByteArrayTransfer {

    private static final Logger LOG = LoggerFactory.getLogger(WidgetModelTransfer.class);

    /**
     * Type name for this transfer type.
     */
    private static final String TYPENAME = "sds_widgets_list"; //$NON-NLS-1$

    /**
     * Type ID for this transfer type.
     */
    private static final int TYPEID = registerType(TYPENAME);

    /**
     * The singleton instance.
     */
    private static WidgetModelTransfer _instance;

    /**
     * Private constructor (singleton pattern).
     *
     */
    private WidgetModelTransfer() {

    }

    /**
     * Returns the singleton instance.
     *
     * @return the singleton instance
     */
    public static WidgetModelTransfer getInstance() {
        if (_instance == null) {
            _instance = new WidgetModelTransfer();
        }
        return _instance;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void javaToNative(final Object object,
            final TransferData transferData) {
        if (!isSupportedType(transferData) || !(checkInput(object))) {
            DND.error(DND.ERROR_INVALID_DATA);
        }

        List<AbstractWidgetModel> widgets = (List<AbstractWidgetModel>) object;

        // create a temporary display model
        DisplayModel tmpModel = new DisplayModel(false);

        for (AbstractWidgetModel widget : widgets) {
            tmpModel.addWidget(widget);
        }

        // convert the temporary display model to a byte array
        InputStream is = PersistenceUtil.createStream(tmpModel);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(bos);

        int nextByte;
        try {
            while ((nextByte = is.read()) > -1) {
                writer.write(nextByte);
            }
        } catch (IOException e) {
            LOG.debug(e.toString());
        }

        try {
            writer.flush();
        } catch (IOException e) {
            LOG.debug(e.toString());
        }

        byte[] bytes = bos.toByteArray();

        // clean up
        try {
            is.close();
            bos.close();
            writer.close();
        } catch (IOException e) {
            LOG.debug(e.toString());
        }

        // store the byte array
        super.javaToNative(bytes, transferData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object nativeToJava(final TransferData transferData) {
        if (isSupportedType(transferData)) {

            byte[] bytes = (byte[]) super.nativeToJava(transferData);

            DisplayModel displayModel = new DisplayModel(false);
            PersistenceUtil.syncFillModel(displayModel,
                    new ByteArrayInputStream(bytes));

            List<AbstractWidgetModel> widgets = displayModel.getWidgets();

            return widgets;
        }

        return null;
    }

    /**
     * Checks the provided input, which must be a non-empty list that contains
     * only objects of type {@link AbstractWidgetModel}.
     *
     * @param input
     *            the input to check
     * @return true, if the input object is valid, false otherwise
     */
    private boolean checkInput(final Object input) {
        boolean result = true;

        if (input instanceof List) {
            List list = (List) input;

            if (list.size() > 0) {
                for (Object o : list) {
                    if (!(o instanceof AbstractWidgetModel)) {
                        result = false;
                    }
                }
            } else {
                result = false;
            }
        } else {
            result = false;
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

}
