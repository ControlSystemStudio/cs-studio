/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgetActions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * The abstract widget action, which can be executed from the widget by click or context menu.
 * @author Xihui Chen
 *
 */
public abstract class AbstractWidgetAction implements IAdaptable {


    private Map<String, AbstractWidgetProperty> propertyMap;

    private boolean enabled = true;

    private AbstractWidgetModel widgetModel;
    public static final String PROP_DESCRIPTION = "description";//$NON-NLS-1$

    public AbstractWidgetAction() {
        propertyMap = new LinkedHashMap<String, AbstractWidgetProperty>();
        configureProperties();
        addProperty(new StringProperty(PROP_DESCRIPTION, "Description",
                WidgetPropertyCategory.Basic, ""));        //$NON-NLS-1$
    }

    /**Add a property to the widget.
     * @param property the property to be added.
     */
    public void addProperty(final AbstractWidgetProperty property){
        Assert.isNotNull(property);
        property.setWidgetModel(getWidgetModel());
        propertyMap.put(property.getPropertyID(), property);
    }

    protected abstract void configureProperties();

    public String getDefaultDescription(){
        return getActionType().getDescription();
    }

    public String getDescription(){
        String description = (String)getPropertyValue(PROP_DESCRIPTION);
        if(description.trim().length()==0)
            description = getDefaultDescription();
        return description;
    }

    public abstract void run();


    public abstract ActionType getActionType();

    public AbstractWidgetProperty[] getAllProperties(){
        AbstractWidgetProperty[] propArray = new AbstractWidgetProperty[propertyMap.size()];
        int i=0;
        for(AbstractWidgetProperty p : propertyMap.values())
            propArray[i++] = p;
        return propArray;
    }


    public Object getPropertyValue(Object id) {
        Assert.isTrue(propertyMap.containsKey(id));
        return propertyMap.get(id).getPropertyValue();
    }

    public void setPropertyValue(Object id, Object value) {
        Assert.isTrue(propertyMap.containsKey(id));
        propertyMap.get(id).setPropertyValue(value);
    }



    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
        if(adapter == IWorkbenchAdapter.class)
            return new IWorkbenchAdapter() {

                @Override
                public Object getParent(Object o) {
                    return null;
                }

                @Override
                public String getLabel(Object o) {
                    return getActionType().getDescription();
                }

                @Override
                public ImageDescriptor getImageDescriptor(Object object) {
                    return getActionType().getIconImage();
                }

                @Override
                public Object[] getChildren(Object o) {
                    return new Object[0];
                }
            };

        return null;
    }

    public AbstractWidgetAction getCopy() {
        AbstractWidgetAction action = WidgetActionFactory.createWidgetAction(getActionType());
        for(String id : propertyMap.keySet()){
            action.setPropertyValue(id, getPropertyValue(id));
        }
        return action;
    }

    public Set<String> getAllPropertyIDs() {
        return propertyMap.keySet();
    }

    public AbstractWidgetProperty getProperty(String propId) {
        return propertyMap.get(propId);
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param widgetModel the widgetModel to set
     */
    public void setWidgetModel(AbstractWidgetModel widgetModel) {
        this.widgetModel = widgetModel;
        for(AbstractWidgetProperty property : getAllProperties())
            property.setWidgetModel(widgetModel);
    }

    /**
     * @return the widgetModel
     */
    public AbstractWidgetModel getWidgetModel() {
        return widgetModel;
    }

    /**
     * Dispose of all resources allocated by this action.
     */
    public void dispose() {

    }

}
