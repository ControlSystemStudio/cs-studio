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
package org.csstudio.sds.ui.internal.editor.dnd;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dal.ui.dnd.rfc.ProcessVariableExchangeUtil;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * Base class for drop target listeners that are registered for a SDS display
 * editor.
 *
 * @author Sven Wende
 */
abstract class AbstractDropTargetListener<E extends Transfer> extends AbstractTransferDropTargetListener {

    AbstractDropTargetListener(final EditPartViewer viewer, E transfer) {
        super(viewer, transfer);
    }

    @Override
    protected final void updateTargetRequest() {
        ((DropPvRequest) getTargetRequest()).setLocation(getDropLocation());
    }

    @Override
    protected final Request createTargetRequest() {
        final DropPvRequest request = new DropPvRequest();
        return request;
    }

    @Override
    protected final void handleDragOver() {
        getCurrentEvent().detail = DND.DROP_COPY;
        super.handleDragOver();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected final void handleDrop() {
        String[] droppedPvNames = translate((E) getTransfer(), this.getCurrentEvent().currentDataType);

        if (droppedPvNames != null) {
            List<IProcessVariableAddress> pvs = new ArrayList<IProcessVariableAddress>();

            for (String droppedPvName : droppedPvNames) {
                IProcessVariableAddress pv = ProcessVariableExchangeUtil.parseProcessVariableAdress(droppedPvName, true);
                if (pv != null) {
                    pvs.add(pv);
                }
            }

            if (!pvs.isEmpty()) {
                ((DropPvRequest) getTargetRequest()).setDroppedProcessVariables(pvs);
                super.handleDrop();
            }
        }
    }

    protected abstract String[] translate(E transfer, TransferData transferData);
}
