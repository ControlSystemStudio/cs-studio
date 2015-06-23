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
 /**
 *
 */
package org.csstudio.dal.ui.internal.dnd;

import org.csstudio.dal.ui.dnd.rfc.IProcessVariableAdressReceiver;
import org.csstudio.dal.ui.dnd.rfc.IShowControlSystemDialogStrategy;
import org.csstudio.dal.ui.dnd.rfc.ProcessVariableAddressTransfer;
import org.csstudio.dal.ui.dnd.rfc.ProcessVariableExchangeUtil;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;

public class ProcessVariableAdressDropTargetAdapter extends DropTargetAdapter {
    private IProcessVariableAdressReceiver _receiver;

    private IShowControlSystemDialogStrategy _showControlSystemDialogStrategy;

    public ProcessVariableAdressDropTargetAdapter(
            IProcessVariableAdressReceiver pvCallback,
            IShowControlSystemDialogStrategy showControlSystemDialogStrategy) {
        assert pvCallback != null;
        assert showControlSystemDialogStrategy != null;
        _receiver = pvCallback;
        _showControlSystemDialogStrategy = showControlSystemDialogStrategy;
    }

    @Override
    public void drop(final DropTargetEvent event) {
        IProcessVariableAddress[] pvs = new IProcessVariableAddress[0];

        if (ProcessVariableAddressTransfer.getInstance().isSupportedType(event.currentDataType)) {
            pvs = (IProcessVariableAddress[]) ProcessVariableAddressTransfer.getInstance()
                    .nativeToJava(event.currentDataType);
        } else if (TextTransfer.getInstance().isSupportedType(
                event.currentDataType)) {
            String rawName = (String) TextTransfer.getInstance().nativeToJava(
                    event.currentDataType);

            // TODO: Strategy einbauen, an welcher sich entscheidet, ob nach dem
            // Control-Systeme gefragt wird (z.B. sollte beim Drop von Strings
            // mit Aliasen nicht
            // auf Krampf versucht werden, eine PV aufzulösen)
            IProcessVariableAddress pv = ProcessVariableExchangeUtil
                    .parseProcessVariableAdress(rawName,
                            _showControlSystemDialogStrategy
                                    .showControlSystem(rawName));

            pvs = new IProcessVariableAddress[] { pv };
        }

        if (pvs.length > 0) {
            _receiver.receive(pvs, event);
        }
    }

    @Override
    public void dropAccept(final DropTargetEvent event) {
        if (!isSupportedType(event)) {
            event.detail = DND.DROP_NONE;
        } else {
            event.detail = DND.DROP_COPY;
        }
    }

    @Override
    public void dragEnter(final DropTargetEvent event) {
        if (!isSupportedType(event)) {
            event.detail = DND.DROP_NONE;
        } else {
            event.detail = DND.DROP_COPY;
        }
    }

    @Override
    public void dragOver(final DropTargetEvent event) {
        if (!isSupportedType(event)) {
            event.detail = DND.DROP_NONE;
        } else {
            event.detail = DND.DROP_COPY;
        }
    }

    private boolean isSupportedType(DropTargetEvent event) {
        boolean supported = (ProcessVariableAddressTransfer.getInstance().isSupportedType(
                event.currentDataType) || TextTransfer.getInstance()
                .isSupportedType(event.currentDataType));

        return supported;
    }
}