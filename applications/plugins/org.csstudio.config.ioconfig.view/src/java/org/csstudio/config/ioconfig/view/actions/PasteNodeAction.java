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

import java.util.List;
import java.util.SortedMap;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.csstudio.config.ioconfig.view.ProfiBusTreeView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Paste a Node to the selected node in the Tree.
 *
 * @author hrickens
 * @since 05.10.2011
 */
public class PasteNodeAction extends Action {

    protected static final Logger LOG = LoggerFactory.getLogger(PasteNodeAction.class);

    private final ProfiBusTreeView _profiBusTreeView;

    /**
     * Constructor.
     */
    public PasteNodeAction(@Nonnull final ProfiBusTreeView profiBusTreeView) {
        _profiBusTreeView = profiBusTreeView;
    }

    @Override
    public void run() {
        final Object firstElement = _profiBusTreeView.getSelectedNodes().getFirstElement();
        AbstractNodeDBO<?, ?> selectedNode;
        if (firstElement instanceof AbstractNodeDBO) {
            selectedNode = (AbstractNodeDBO<?, ?>) firstElement;
        } else {
            return;
        }

        for (final AbstractNodeDBO<?, ?> node2Copy : _profiBusTreeView
                .getCopiedNodesReferenceList()) {
            try {
                if (node2Copy instanceof FacilityDBO) {
                    copyFacility((FacilityDBO) selectedNode);
                } else if (selectedNode.getClass().isInstance(node2Copy.getParent())) {
                    copy2Parent(selectedNode, node2Copy);
                } else if (selectedNode.getClass().isInstance(node2Copy)) {
                    copy2Sibling(selectedNode, node2Copy);
                }
            } catch (final PersistenceException e) {
                DeviceDatabaseErrorDialog.open(null, "Can't copy Node! Database Error.", e);
                LOG.error("Can't copy Node. Device Database Error", e);
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void copy2Parent(@Nonnull final AbstractNodeDBO selectedNode,
                             @Nonnull final AbstractNodeDBO node2Copy) throws PersistenceException {
        AbstractNodeDBO<?, ?> copy = null;
        if (_profiBusTreeView.isMove()) {
            final AbstractNodeDBO oldParent = node2Copy.getParent();
            oldParent.removeChild(node2Copy);
            final SortedMap<Short, AbstractNodeDBO<AbstractNodeDBO<?, ?>, AbstractNodeDBO<?, ?>>> childrenAsMap = selectedNode
                    .getChildrenAsMap();
            final AbstractNodeDBO<?, ?> node = childrenAsMap.get(node2Copy.getSortIndex());
            if (node != null) {
                final int freeStationAddress = selectedNode.getfirstFreeStationAddress();
                node2Copy.setSortIndex(freeStationAddress);
            }
            selectedNode.addChild(node2Copy);
            copy = node2Copy;
            selectedNode.save();
        } else {
            // paste to a Parent
            copy = node2Copy.copyThisTo(selectedNode, "Copy of ");
            copy.setDirty(true);
            copy.setSortIndexNonHibernate(selectedNode.getfirstFreeStationAddress());
        }
        _profiBusTreeView.getViewer().refresh();
        _profiBusTreeView.getViewer().setSelection(new StructuredSelection(copy));
    }

    /**
     * @param selectedNode
     * @param node2Copy
     * @throws PersistenceException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void copy2Sibling(@Nonnull final AbstractNodeDBO<?, ?> selectedNode,
                              @Nonnull final AbstractNodeDBO node2Copy) throws PersistenceException {
        AbstractNodeDBO<?, ?> nodeCopy = null;
        if (_profiBusTreeView.isMove()) {
            final AbstractNodeDBO oldParent = node2Copy.getParent();
            oldParent.removeChild(node2Copy);
            final AbstractNodeDBO parent = selectedNode.getParent();
            node2Copy.setSortIndex((int) selectedNode.getSortIndex());
            parent.addChild(node2Copy);
            parent.save();
            nodeCopy = node2Copy;
        } else {
            // paste to a sibling
            short targetIndex = selectedNode.getSortIndex();
            targetIndex++;
            nodeCopy = node2Copy.copyThisTo(selectedNode.getParent(), "Copy of ");
            nodeCopy.moveSortIndex(targetIndex);
        }
        _profiBusTreeView.refresh();
        _profiBusTreeView.getViewer().setSelection(new StructuredSelection(nodeCopy));
    }

    private void copyFacility(@Nonnull final FacilityDBO selectedNode) throws PersistenceException {
        final FacilityDBO copy = selectedNode.copyThisTo(selectedNode.getParent(), "Copy of ");
        copy.setSortIndexNonHibernate(selectedNode.getSortIndex() + 1);
        final List<FacilityDBO> load = _profiBusTreeView.getLoad();
        load.add(copy);
        _profiBusTreeView.getViewer().setInput(load);
        _profiBusTreeView.getViewer().setSelection(new StructuredSelection(copy));
    }
}
