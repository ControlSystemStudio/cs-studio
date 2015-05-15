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

import java.util.List;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.ContainerModel;
import org.csstudio.sds.model.GroupingContainerModel;
import org.csstudio.sds.model.commands.SetPropertyCommand;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.internal.commands.AddWidgetCommand;
import org.csstudio.sds.ui.internal.commands.DeleteWidgetsCommand;
import org.csstudio.sds.ui.internal.commands.SetSelectionCommand;
import org.csstudio.sds.ui.internal.editor.WidgetCreationFactory;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.ui.IWorkbenchPart;

/**
 * An Action to create a group surrounding the selected Widgets.
 *
 * @author Kai Meyer &  Sven Wende
 *
 */
public final class CreateGroupAction extends AbstractWidgetSelectionAction {

    public static final String ID = "org.csstudio.sds.ui.internal.actions.CreateGroupAction";

    /**
     * The offset for the surrounding {@link GroupingContainerModel}.
     */
    private static final int OFFSET = 5;

    public CreateGroupAction(IWorkbenchPart workbenchPart, GraphicalViewer viewer) {
        super(workbenchPart, viewer);
        setId(ID);
        setText("Create Group");
        setImageDescriptor(CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID, "/icons/addgroup.gif"));
    }

    @Override
    protected boolean doCalculateEnabled(List<AbstractWidgetModel> selectedWidgets) {
        return selectedWidgets.size()>1;
    }

    @Override
    protected Command doCreateCommand(List<AbstractWidgetModel> widgets) {

        CompoundCommand cmd = new CompoundCommand();
        cmd.setLabel("Create Group");
        ContainerModel parentContainer = getCommonAncestor(widgets);

        // Create a new GroupingContainer
        Rectangle bounds = getFittingBounds(widgets, parentContainer);
        WidgetCreationFactory factory = new WidgetCreationFactory(GroupingContainerModel.ID);
        // create a widget
        ContainerModel container = (ContainerModel) factory.getNewObject();
        // initialize widget
        container.setLocation(bounds.x, bounds.y);
        container.setWidth(bounds.width);
        container.setHeight(bounds.height);
        container.setLayer(parentContainer.getLayerSupport().getActiveLayer().getId());

        // add new container
        cmd.add(new AddWidgetCommand(parentContainer, container));

        // select new container
        cmd.add(new SetSelectionCommand(getGraphicalViewer(), container));

        // remove widgets from surrounding container
        cmd.add(new DeleteWidgetsCommand(null, parentContainer, widgets));

        // adjust widget positions
        for (AbstractWidgetModel w : widgets) {
            Point p = this.adaptWidgetPosition(w, parentContainer, container);
            cmd.add(new SetPropertyCommand(w, AbstractWidgetModel.PROP_POS_X, p.x));
            cmd.add(new SetPropertyCommand(w, AbstractWidgetModel.PROP_POS_Y, p.y));
        }

        // add widgets to new container
        cmd.add(new AddWidgetCommand(container, widgets));


        return cmd;
    }

    /**
     * Determines the common ancestor for all given {@link AbstractWidgetModel}
     * s.
     *
     * @param widgets
     *            The {@link AbstractWidgetModel}s
     * @return The {@link ContainerModel}, which is the ancestor for all
     *         {@link AbstractWidgetModel}s
     */
    private ContainerModel getCommonAncestor(final List<AbstractWidgetModel> widgets) {
        if (widgets.size() > 0) {
            ContainerModel ancestor = widgets.get(0).getParent();
            while (ancestor != null) {
                boolean isForAllReachable = true;
                for (AbstractWidgetModel widget : widgets) {
                    if (!isAncestorReachable(ancestor, widget)) {
                        isForAllReachable = false;
                        break;
                    }
                }
                if (isForAllReachable) {
                    return ancestor;
                }
                ancestor = ancestor.getParent();
            }
        }
        return null;
    }

    /**
     * Determines if the given {@link ContainerModel} is an ancestor of this
     * model.
     *
     * @param ancestor
     *            The probably ancestor
     * @param widget
     *            The {@link AbstractWidgetModel} which ancestor should be find
     * @return true, if the given {@link ContainerModel} is an ancestor of this
     *         model, false otherwise
     */
    private boolean isAncestorReachable(final ContainerModel ancestor, final AbstractWidgetModel widget) {
        ContainerModel parent = widget.getParent();
        while (parent != null) {
            if (parent.equals(ancestor)) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    /**
     * Determines the bounds for the container, which surrounds for all selected
     * widgets.
     *
     * @param widgets
     *            The widgets, which should be added to a container
     * @return The bounds for the new container
     */
    private Rectangle getFittingBounds(final List<AbstractWidgetModel> widgets, ContainerModel parentContainer) {
        int x = widgets.get(0).getXForAncestor(parentContainer);
        int y = widgets.get(0).getYForAncestor(parentContainer);
        int absoluteWidth = widgets.get(0).getWidth() + widgets.get(0).getXForAncestor(parentContainer);
        int absoluteHeight = widgets.get(0).getHeight() + widgets.get(0).getYForAncestor(parentContainer);
        for (int i = 1; i < widgets.size(); i++) {
            AbstractWidgetModel model = widgets.get(i);
            x = Math.min(x, model.getXForAncestor(parentContainer));
            y = Math.min(y, model.getYForAncestor(parentContainer));
            absoluteWidth = Math.max(absoluteWidth, model.getXForAncestor(parentContainer) + model.getWidth());
            absoluteHeight = Math.max(absoluteHeight, model.getYForAncestor(parentContainer) + model.getHeight());
        }
        return new Rectangle(x - OFFSET, y - OFFSET, absoluteWidth - x + 2 * OFFSET, absoluteHeight - y + 2 * OFFSET);
    }

    private Point adaptWidgetPosition(final AbstractWidgetModel widgetModel, ContainerModel parentContainer, ContainerModel container) {
        int x = widgetModel.getXForAncestor(parentContainer) - container.getX();
        int y = widgetModel.getYForAncestor(parentContainer) - container.getY();
        return new Point(x, y);
    }
}
