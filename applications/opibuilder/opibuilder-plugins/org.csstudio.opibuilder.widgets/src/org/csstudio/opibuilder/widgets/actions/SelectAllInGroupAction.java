/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.actions;

import org.csstudio.opibuilder.actions.AbstractWidgetTargetAction;
import org.csstudio.opibuilder.widgets.editparts.GroupingContainerEditPart;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.eclipse.jface.action.IAction;

/**Select all widgets in a group
 * @author Xihui Chen
 *
 */
public class SelectAllInGroupAction extends AbstractWidgetTargetAction{


    @Override
    public void run(IAction action) {

        GroupingContainerModel containerModel = getContainerModel();
        containerModel.selectWidgets(containerModel.getChildren(), false);
    }


    /**
     * Gets the widget models of all currently selected EditParts.
     *
     * @return a list with all widget models that are currently selected
     */
    protected final GroupingContainerModel getContainerModel() {
        return ((GroupingContainerEditPart)selection.getFirstElement()).getWidgetModel();
    }




}
