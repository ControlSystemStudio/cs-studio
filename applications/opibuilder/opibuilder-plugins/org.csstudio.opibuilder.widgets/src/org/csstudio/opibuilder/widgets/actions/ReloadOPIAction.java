/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.actions;
import org.csstudio.opibuilder.actions.AbstractWidgetTargetAction;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.widgets.editparts.LinkingContainerEditpart;
import org.csstudio.opibuilder.widgets.model.LinkingContainerModel;
import org.eclipse.jface.action.IAction;


/**Reload OPI to linking container.
 * @author Xihui Chen
 *
 */
public class ReloadOPIAction extends AbstractWidgetTargetAction {


    @Override
    public void run(IAction action) {
        AbstractWidgetProperty property =
            getSelectedContianerWidget().getWidgetModel().getProperty(
                    LinkingContainerModel.PROP_OPI_FILE);
        property.setPropertyValue(property.getPropertyValue(), true);
    }


    /**
     * Gets the widget models of all currently selected EditParts.
     *
     * @return a list with all widget models that are currently selected
     */
    protected final LinkingContainerEditpart getSelectedContianerWidget() {
        return (LinkingContainerEditpart)selection.getFirstElement();
    }
}
