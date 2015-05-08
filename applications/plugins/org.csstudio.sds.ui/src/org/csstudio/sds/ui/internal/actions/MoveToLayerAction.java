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

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.csstudio.sds.model.commands.SetPropertyCommand;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.ui.IWorkbenchPart;

/**
 * The Action to move the selected Widgets to the specified layer.
 * @author Kai Meyer
 *
 */
public final class MoveToLayerAction extends AbstractOrderAction {

    /**
     * Action ID of this action.
     */
    public static final String ID = "org.csstudio.sds.ui.internal.actions.MoveToLayerAction";
    /**
     * The name of the Layer.
     */
    private final String _layerName;

    /**
     * Constructor.
     * @param workbenchPart
     *             The {@link IWorkbenchPart} for this Action
     * @param layerName
     *             The name of the new layer for the widgets
     */
    public MoveToLayerAction(final IWorkbenchPart workbenchPart, final String layerName) {
        super(workbenchPart);
        _layerName = layerName;
        setId(ID);
        setText("Move to Layer '"+_layerName+"'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command createCommand(final ContainerModel container,
            final AbstractWidgetModel widget) {
        return new SetPropertyCommand(widget, AbstractWidgetModel.PROP_LAYER, _layerName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        super.update();
        this.setChecked(false);
        for (Object selObject : getSelectedObjects()) {
            if (selObject instanceof AbstractWidgetEditPart) {
                AbstractWidgetModel model = ((AbstractWidgetEditPart)selObject).getWidgetModel();
                if (model.getLayer().equals(_layerName)) {
                    this.setChecked(true);
                }
            }
        }
    }

}
