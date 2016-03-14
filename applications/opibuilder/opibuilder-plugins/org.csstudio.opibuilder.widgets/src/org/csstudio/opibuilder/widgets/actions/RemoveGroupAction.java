/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.actions;

import org.csstudio.opibuilder.actions.AbstractWidgetTargetAction;
import org.csstudio.opibuilder.commands.AddWidgetCommand;
import org.csstudio.opibuilder.commands.OrphanChildCommand;
import org.csstudio.opibuilder.commands.WidgetDeleteCommand;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.editparts.GroupingContainerEditPart;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.action.IAction;

/**The action will remove a group and move all the selected widgets to the group's parent.
 * @author Xihui Chen
 *
 */
public class RemoveGroupAction extends AbstractWidgetTargetAction{


    @Override
    public void run(IAction action) {
        CompoundCommand compoundCommand = new CompoundCommand("Remove Group");

        GroupingContainerModel containerModel = getSelectedContainer();

        //Orphan order should be reversed so that undo operation has the correct order.
        AbstractWidgetModel[] widgetsArray = containerModel.getChildren().toArray(
                new AbstractWidgetModel[containerModel.getChildren().size()]);
        for(int i = widgetsArray.length -1; i>=0; i--){
            compoundCommand.add(new OrphanChildCommand(containerModel, widgetsArray[i]));
        }

        Point leftCorner = containerModel.getLocation();
        for(AbstractWidgetModel widget : containerModel.getChildren()){
            compoundCommand.add(new AddWidgetCommand(containerModel.getParent(), widget,
                    new Rectangle(widget.getLocation(), widget.getSize()).translate(leftCorner)));
        }
        compoundCommand.add(new WidgetDeleteCommand(containerModel.getParent(), containerModel));
        execute(compoundCommand);

    }


    /**
     * Gets the widget models of all currently selected EditParts.
     *
     * @return a list with all widget models that are currently selected
     */
    protected final GroupingContainerModel getSelectedContainer() {
        return ((GroupingContainerEditPart)selection.getFirstElement()).getWidgetModel();
    }

}
