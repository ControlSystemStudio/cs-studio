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

import java.util.Arrays;
import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.GroupingContainerModel;
import org.csstudio.sds.model.commands.SetPropertyCommand;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.internal.commands.AddWidgetCommand;
import org.csstudio.sds.ui.internal.commands.DeleteWidgetsCommand;
import org.csstudio.sds.ui.internal.commands.SetSelectionCommand;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.ui.IWorkbenchPart;

/**
 * An Action to remove the selected widgets from their surrounding container.
 *
 * @author Kai Meyer & Sven Wende
 *
 */
public final class RemoveGroupAction extends AbstractWidgetSelectionAction {

    public static final String ID = "org.csstudio.sds.ui.internal.actions.RemoveGroupAction";

    public RemoveGroupAction(IWorkbenchPart workbenchPart, GraphicalViewer viewer) {
        super(workbenchPart, viewer);
        setId(ID);
        setText("Remove Group");
        setImageDescriptor(CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID, "/icons/removegroup.gif"));
    }

    @Override
    protected boolean doCalculateEnabled(List<AbstractWidgetModel> selectedWidgets) {
        return selectedWidgets.size() == 1 && selectedWidgets.get(0) instanceof GroupingContainerModel;
    }

    @Override
    protected Command doCreateCommand(List<AbstractWidgetModel> selectedWidgets) {
        GroupingContainerModel container = (GroupingContainerModel) selectedWidgets.get(0);
        assert container != null;

        CompoundCommand cmd = new CompoundCommand();
        cmd.setLabel("Remove Group");

        List<AbstractWidgetModel> widgets = container.getWidgets();

        // remove widgets from container
        cmd.add(new DeleteWidgetsCommand(null, container, widgets));

        // adjust widget positions
        for (AbstractWidgetModel w : widgets) {
            Point p = adaptWidgetPosition(w, container);
            cmd.add(new SetPropertyCommand(w, AbstractWidgetModel.PROP_POS_X, p.x));
            cmd.add(new SetPropertyCommand(w, AbstractWidgetModel.PROP_POS_Y, p.y));
        }

        // add widgets to surrounding container
        cmd.add(new AddWidgetCommand(container.getParent(), widgets));

        // select the widgets
        cmd.add(new SetSelectionCommand(getGraphicalViewer(), widgets));

        // delete the container
        cmd.add(new DeleteWidgetsCommand(null, container.getParent(), Arrays.asList((AbstractWidgetModel) container)));

        return cmd;
    }

    private Point adaptWidgetPosition(final AbstractWidgetModel widgetModel, GroupingContainerModel container) {
        int x = widgetModel.getX() + container.getX();
        int y = widgetModel.getY() + container.getY();
        return new Point(x, y);
    }

}
