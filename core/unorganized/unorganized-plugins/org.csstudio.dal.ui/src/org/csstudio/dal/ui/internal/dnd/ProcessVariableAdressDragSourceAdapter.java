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
package org.csstudio.dal.ui.internal.dnd;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dal.ui.dnd.rfc.ProcessVariableAddressTransfer;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.IProcessVariableAdressProvider;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;

/**
 * This adapter class provides enhanced implementations for the methods
 * described by the <code>DragSourceListener</code> interface.
 *
 * @author Kai Meyer
 */
public  class ProcessVariableAdressDragSourceAdapter extends DragSourceAdapter {

    /**
     * A {@link IProcessVariableAdressProvider}
     */
    private IProcessVariableAdressProvider _pvProvider;

    /**
     * Constructs a drag source adapter, which only provides items during DnD,
     * that are {@link IProcessVariableAddress}s.
     *
     * @param pvProvider
     *            The provider of the {@link IProcessVariableAddress}
     */
    public ProcessVariableAdressDragSourceAdapter(
            IProcessVariableAdressProvider pvProvider) {
        _pvProvider = pvProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dragStart(final DragSourceEvent event) {
        super.dragStart(event);
        ProcessVariableAddressTransfer.getInstance().setSelectedItems(this.getProceesVariables());
    }

    /**
     * Returns a new List of all provided {@link IProcessVariableAddress}
     *
     * @return List of ProcessVariables A new List of all provided
     *         {@link IProcessVariableAddress}
     */
    private List<IProcessVariableAddress> getProceesVariables() {
        List<IProcessVariableAddress> list = new ArrayList<IProcessVariableAddress>();
        if (_pvProvider != null) {
            list.addAll(_pvProvider.getProcessVariableAdresses());
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dragSetData(final DragSourceEvent event) {
        List<IProcessVariableAddress> items = null;
        List currentSelection = this.getProceesVariables();

        if ((currentSelection != null) && (currentSelection.size() > 0)) {
            items = this.getProceesVariables();
        } else {
            items = ProcessVariableAddressTransfer.getInstance().getSelectedItems();
        }
        if (ProcessVariableAddressTransfer.getInstance().isSupportedType(event.dataType)) {
            event.data = items;
        } else if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
            StringBuffer sb = new StringBuffer();
            // concatenate a String, which contains items line by line
            for (IProcessVariableAddress item : items) {
                String path = item.getRawName();
                sb.append(path);
                sb.append("\n"); //$NON-NLS-1$
            }
            event.data = sb.toString();
        }
    }

}
