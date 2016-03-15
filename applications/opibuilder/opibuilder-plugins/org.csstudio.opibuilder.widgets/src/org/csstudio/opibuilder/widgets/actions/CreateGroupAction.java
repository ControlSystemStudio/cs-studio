/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.csstudio.opibuilder.actions.AbstractWidgetTargetAction;
import org.csstudio.opibuilder.commands.AddWidgetCommand;
import org.csstudio.opibuilder.commands.OrphanChildCommand;
import org.csstudio.opibuilder.commands.WidgetCreateCommand;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.DisplayEditpart;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.util.SchemaService;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.action.IAction;

/**The action will create a group which contains all the selected widgets.
 * @author Xihui Chen
 *
 */
public class CreateGroupAction extends AbstractWidgetTargetAction {


    @Override
    public void run(IAction action) {

        List<AbstractWidgetModel> originalSelectedWidgets = getSelectedWidgetModels();


        CompoundCommand compoundCommand = new CompoundCommand("Create Group");




        List<AbstractWidgetModel> selectedWidgets = new ArrayList<AbstractWidgetModel>();
        selectedWidgets.addAll(originalSelectedWidgets);

        //remove the selected widgets which are children of another selected widget.
        for(AbstractWidgetModel widget : originalSelectedWidgets){
            if(widget instanceof DisplayModel){
                selectedWidgets.remove(widget);
                continue;
            }
            if(widget instanceof AbstractContainerModel){
                for(AbstractWidgetModel child : originalSelectedWidgets){
                    if(((AbstractContainerModel)widget).getChildren().contains(child))
                        selectedWidgets.remove(child);
                }
            }

        }

        int minDepth = Integer.MAX_VALUE;
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE,
            maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        AbstractWidgetModel minDepthWidget = selectedWidgets.get(0);

        for(AbstractWidgetModel widget : selectedWidgets){

            int leftX = widget.getLocation().x;
            int upY = widget.getLocation().y;
            int rightX = widget.getLocation().x + widget.getSize().width;
            int bottomY = widget.getLocation().y + widget.getSize().height;
            int depth = widget.getNestedDepth();
            if( leftX<minX)
                minX = leftX;
            if( upY < minY)
                minY = upY;
            if(rightX > maxX)
                maxX =rightX;
            if(bottomY > maxY)
                maxY = bottomY;
            if(minDepth > depth){
                minDepth = depth;
                minDepthWidget = widget;
            }

        }

        //Orphan order should be reversed so that undo operation has the correct order.
        AbstractWidgetModel[] widgetsArray = selectedWidgets.toArray(
                new AbstractWidgetModel[selectedWidgets.size()]);
        for(int i = widgetsArray.length -1; i>=0; i--){
            compoundCommand.add(new OrphanChildCommand(widgetsArray[i].getParent(), widgetsArray[i]));
        }

        GroupingContainerModel groupingContainerModel = new GroupingContainerModel();
        SchemaService.getInstance().applySchema(groupingContainerModel);
        // the parent should be the widget with minimum nested depth
        AbstractContainerModel parent = minDepthWidget.getParent();

        int borderWidth = 0;

        if(groupingContainerModel.getBorderStyle()== BorderStyle.GROUP_BOX)
            borderWidth = 30;

        groupingContainerModel.setPropertyValue(GroupingContainerModel.PROP_LOCK_CHILDREN, true);
        groupingContainerModel.setPropertyValue(GroupingContainerModel.PROP_SHOW_SCROLLBAR, false);

        compoundCommand.add(new WidgetCreateCommand(groupingContainerModel,
                parent, new Rectangle(minX, minY, maxX-minX + borderWidth, maxY-minY + borderWidth), false));


        for(AbstractWidgetModel widget : selectedWidgets){
            compoundCommand.add(new AddWidgetCommand(groupingContainerModel, widget,
                    new Rectangle(widget.getLocation().translate(-minX, -minY), widget.getSize())));
        }

        execute(compoundCommand);
    }

//    /**
//     * Gets the widget models of all currently selected EditParts.
//     *
//     * @return a list with all widget models that are currently selected
//     */
//    protected final List<AbstractWidgetModel> getSelectedWidgetModels() {
//
//        List<AbstractWidgetModel> selectedWidgetModels = new ArrayList<AbstractWidgetModel>();
//
//        for (Object o : selection.toList()) {
//            if (o instanceof AbstractBaseEditPart) {
//                selectedWidgetModels.add(((AbstractBaseEditPart) o)
//                        .getWidgetModel());
//            }
//        }
//        return selectedWidgetModels;
//    }
//
    /**
     * Gets the widget models of all currently selected EditParts.
     *
     * @return a list with all widget models that are currently selected.
     * The order of the selected widgets was kept.
     */
    protected final List<AbstractWidgetModel> getSelectedWidgetModels() {
        //List selection = getSelectedObjects();

        List<AbstractWidgetModel> sameParentModels = new ArrayList<AbstractWidgetModel>();
        List<AbstractWidgetModel> differentParentModels = new ArrayList<AbstractWidgetModel>();
        List<AbstractWidgetModel> result = new ArrayList<AbstractWidgetModel>();
        AbstractContainerModel parent = null;
        for (Object o : selection.toList()) {
            if (o instanceof AbstractBaseEditPart && !(o instanceof DisplayEditpart)) {
                AbstractWidgetModel widgetModel =
                    ((AbstractBaseEditPart) o).getWidgetModel();
                if(parent == null)
                    parent = widgetModel.getParent();
                if(widgetModel.getParent() == parent)
                    sameParentModels.add(widgetModel);
                else
                    differentParentModels.add(widgetModel);
            }
        }
        //sort widgets to its original order
        if(sameParentModels.size() > 1){
            AbstractWidgetModel[] modelArray = sameParentModels.toArray(new AbstractWidgetModel[0]);

            Arrays.sort(modelArray, new Comparator<AbstractWidgetModel>(){

                @Override
                public int compare(AbstractWidgetModel o1,
                        AbstractWidgetModel o2) {
                    if(o1.getParent().getChildren().indexOf(o1) >
                        o2.getParent().getChildren().indexOf(o2))
                        return 1;
                    else
                        return -1;
                }

            });
            result.addAll(Arrays.asList(modelArray));
            if(differentParentModels.size() > 0)
                result.addAll(differentParentModels);
            return result;
        }
        if(differentParentModels.size() > 0)
            sameParentModels.addAll(differentParentModels);

        return sameParentModels;
    }

}
