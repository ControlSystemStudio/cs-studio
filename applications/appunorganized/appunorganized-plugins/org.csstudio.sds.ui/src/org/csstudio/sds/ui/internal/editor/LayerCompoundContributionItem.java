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
 package org.csstudio.sds.ui.internal.editor;

import org.csstudio.sds.ui.internal.actions.MoveToLayerAction;
import org.csstudio.sds.ui.internal.layers.ILayerManager;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;

/**
 * A {@link CompoundContributionItem}, which contains a {@link MoveToLayerAction} for
 * every Layer of the {@link DisplayEditor}.
 * @author Kai Meyer
 */
public final class LayerCompoundContributionItem extends CompoundContributionItem {

    /**
     * The {@link ILayerManager}.
     */
    private ILayerManager _layerManager;
    /**
     * The {@link DisplayEditor}.
     */
    private DisplayEditor _displayEditor;

    /**
     * Constructor.
     */
    public LayerCompoundContributionItem() {
        super("Layers");
        IWorkbenchPart activePart = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getPartService().getActivePart();
        if (activePart instanceof DisplayEditor) {
            _displayEditor = (DisplayEditor) activePart;
            _layerManager = (ILayerManager) activePart
                .getAdapter(ILayerManager.class);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IContributionItem[] getContributionItems() {
        IContributionItem[] items = new IContributionItem[_layerManager.getLayerSupport().getLayers().size()];
        for (int i=0;i<_layerManager.getLayerSupport().getLayers().size();i++) {
            MoveToLayerAction moveToLayerAction = new MoveToLayerAction(_displayEditor, _layerManager.getLayerSupport().getLayers().get(i).getDescription());
            moveToLayerAction.update();
            items[i] = new ActionContributionItem(moveToLayerAction);
        }
        return items;
    }

}
