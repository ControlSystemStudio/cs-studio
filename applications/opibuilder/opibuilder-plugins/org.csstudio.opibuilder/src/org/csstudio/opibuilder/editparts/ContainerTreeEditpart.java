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
import java.util.List;

import org.csstudio.opibuilder.editpolicies.WidgetContainerEditPolicy;
import org.csstudio.opibuilder.editpolicies.WidgetTreeContainerEditPolicy;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;

/**Tree Editpart for container widgets.
 * @author Xihui Chen
 *
 */
public class ContainerTreeEditpart extends WidgetTreeEditpart {

    private PropertyChangeListener childrenPropertyChangeListener;

    public ContainerTreeEditpart(AbstractContainerModel model) {
        super(model);
    }


    @Override
    protected void createEditPolicies() {
        super.createEditPolicies();
        if(getWidgetModel().isChildrenOperationAllowable()){
            installEditPolicy(EditPolicy.CONTAINER_ROLE, new WidgetContainerEditPolicy());
            installEditPolicy(EditPolicy.TREE_CONTAINER_ROLE, new WidgetTreeContainerEditPolicy());
        }
        //If this editpart is the contents of the viewer, then it is not deletable!
        if (getParent() instanceof RootEditPart)
            installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
    }

    @Override
    public void activate() {
        super.activate();

        childrenPropertyChangeListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {

                if(evt.getOldValue() instanceof Integer){
                    addChild(createChild(evt.getNewValue()), ((Integer)evt
                            .getOldValue()).intValue());
                }else if (evt.getOldValue() instanceof AbstractWidgetModel){
                    EditPart child = (EditPart)getViewer().getEditPartRegistry().get(
                            evt.getOldValue());
                    if(child != null)
                        removeChild(child);
                }else
                    refreshChildren();
                refreshVisuals();
            }
        };
        getWidgetModel().getChildrenProperty().
            addPropertyChangeListener(childrenPropertyChangeListener);

    }

    @Override
    public void deactivate() {
        super.deactivate();
        getWidgetModel().getChildrenProperty().
            removePropertyChangeListener(childrenPropertyChangeListener);
    }

    @Override
    public AbstractContainerModel getWidgetModel(){
        return (AbstractContainerModel)getModel();
    }

    @Override
    protected List<AbstractWidgetModel> getModelChildren() {
        return getWidgetModel().getChildren();
    }

    @Override
    protected void refreshVisuals() {
        super.refreshVisuals();
        for(Object child : getChildren()){
            if(child instanceof WidgetTreeEditpart)
                ((WidgetTreeEditpart)child).refreshVisuals();
        }
    }

}
