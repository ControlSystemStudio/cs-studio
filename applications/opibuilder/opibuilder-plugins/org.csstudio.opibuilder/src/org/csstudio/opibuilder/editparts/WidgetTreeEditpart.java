/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.opibuilder.actions.ShowIndexInTreeViewAction;
import org.csstudio.opibuilder.editpolicies.WidgetComponentEditPolicy;
import org.csstudio.opibuilder.editpolicies.WidgetTreeEditPolicy;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.util.WidgetDescriptor;
import org.csstudio.opibuilder.util.WidgetsService;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;

/**Basic tree editpart for all widgets.
 * @author Xihui Chen
 *
 */
public class WidgetTreeEditpart extends AbstractTreeEditPart {


    @Override
    protected void createEditPolicies() {
        super.createEditPolicies();

        installEditPolicy(EditPolicy.COMPONENT_ROLE, new WidgetComponentEditPolicy());
        installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new WidgetTreeEditPolicy());
    }

    @Override
    public void activate() {
        super.activate();
        PropertyChangeListener visualListener = new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    refreshVisuals();
                }
        };
        AbstractWidgetProperty nameProperty =
            getWidgetModel().getProperty(AbstractWidgetModel.PROP_NAME);
        if(nameProperty != null){
            nameProperty.addPropertyChangeListener(visualListener);
        }
        AbstractWidgetProperty pvNameProperty =
            getWidgetModel().getProperty(AbstractPVWidgetModel.PROP_PVNAME);
        if(pvNameProperty != null){
            pvNameProperty.addPropertyChangeListener(visualListener);
        }

    }

    public WidgetTreeEditpart(AbstractWidgetModel model) {
        super(model);
    }

    public AbstractWidgetModel getWidgetModel(){
        return (AbstractWidgetModel)getModel();
    }

    @Override
    protected Image getImage() {
        if(getWidgetModel() instanceof DisplayModel)
            return super.getImage();
        String typeID = getWidgetModel().getTypeID();
        WidgetDescriptor widgetDescriptor =
            WidgetsService.getInstance().getWidgetDescriptor(typeID);
        Image image = CustomMediaFactory.getInstance().getImageFromPlugin(
                widgetDescriptor.getPluginId(), widgetDescriptor.getIconPath());
        return image;
    }

    @Override
    protected String getText() {

        StringBuilder sb = new StringBuilder();
        Object obj = getViewer().getProperty(ShowIndexInTreeViewAction.SHOW_INDEX_PROPERTY);
        if(obj != null && obj instanceof Boolean && (Boolean)obj){
            sb.append(Integer.toString(getWidgetModel().getIndex()));
            sb.append("_"); //$NON-NLS-1$
        }
        sb.append(getWidgetModel().getName());
        if(getWidgetModel() instanceof AbstractPVWidgetModel){
             AbstractPVWidgetModel pvWidgetModel = (AbstractPVWidgetModel)getWidgetModel();
             String pvName = pvWidgetModel.getPVName();
             if(pvName != null && !pvName.trim().equals("")){
                 sb.append("("); //$NON-NLS-1$
                 sb.append(pvName);
                 sb.append(")"); //$NON-NLS-1$
             }
        }
        return sb.toString();
    }

    @Override
    protected void refreshVisuals() {
        if(getWidget() instanceof Tree)
            return;
        super.refreshVisuals();
    }


}
