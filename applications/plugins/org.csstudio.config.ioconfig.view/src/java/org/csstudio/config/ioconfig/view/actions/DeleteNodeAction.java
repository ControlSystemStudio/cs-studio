/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.config.ioconfig.view.actions;

import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.config.view.helper.ProfibusHelper;
import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.view.ProfiBusTreeView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * Delete the selected Nodes.
 *
 * @author hrickens
 * @since 05.10.2011
 */
public class DeleteNodeAction extends Action {
    private final ProfiBusTreeView _profiBusTreeView;

    public DeleteNodeAction(@Nonnull final ProfiBusTreeView profiBusTreeView) {
        _profiBusTreeView = profiBusTreeView;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        final String errMsg = "Device Data Base (DDB) Error\nCan't delete the %1s '%2s' (ID: %3s)";
        final String errMsgHead = "Device Database Error";
        final String message = String.format("Delete %1s: %2s", _profiBusTreeView
                .getSelectedNodes().toArray()[0].getClass().getSimpleName(), _profiBusTreeView
                .getSelectedNodes());
        final boolean openConfirm = MessageDialog.openConfirm(_profiBusTreeView.getShell(),
                                                              "Delete Node",
                                                              message);
        if (openConfirm) {
            AbstractNodeDBO<?, ?> parent = null;
            NamedDBClass dbClass = null;
            final Iterator<NamedDBClass> iterator = _profiBusTreeView.getSelectedNodes().iterator();
            while (iterator.hasNext()) {
                dbClass = iterator.next();
                if (dbClass instanceof FacilityDBO) {
                    deleteFacility(errMsg, errMsgHead, dbClass);
                } else if (dbClass instanceof AbstractNodeDBO) {
                    try {
                        parent = ((AbstractNodeDBO<?, ?>) dbClass).delete();
                    } catch (final PersistenceException e) {
                        ProfibusHelper.openErrorDialog(_profiBusTreeView.getSite().getShell(),
                                                       errMsgHead,
                                                       errMsg,
                                                       dbClass,
                                                       e);
                    }
                }
            }
            if (parent != null) {
                _profiBusTreeView.setSelectedNode(new StructuredSelection(parent));
                _profiBusTreeView.refresh(parent);
                _profiBusTreeView.getTreeViewer()
                        .setSelection(_profiBusTreeView.getSelectedNodes(), true);
            } else {
                _profiBusTreeView.refresh();
            }
            _profiBusTreeView.getEditNodeAction().run();
        }
    }

    /**
     * @param errMsg
     * @param errMsgHead
     * @param dbClass
     */
    private void deleteFacility(@Nonnull final String errMsg,
                                @Nullable final String errMsgHead,
                                @Nonnull final NamedDBClass dbClass) {
        final FacilityDBO fac = (FacilityDBO) dbClass;
        try {
            Repository.removeNode(fac);
            _profiBusTreeView.getLoad().remove(fac);
            _profiBusTreeView.getViewer().remove(_profiBusTreeView.getLoad());
        } catch (final Exception e) {
            ProfibusHelper.openErrorDialog(_profiBusTreeView.getSite().getShell(), errMsgHead, errMsg, fac, e);
            return;
        }
    }
}
