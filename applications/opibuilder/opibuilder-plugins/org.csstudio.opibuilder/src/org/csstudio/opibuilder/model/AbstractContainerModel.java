/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.MacrosProperty;
import org.csstudio.opibuilder.properties.UnsavableListProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.MacrosInput;
import org.eclipse.draw2d.geometry.Dimension;

/**The model which could contain children.
 * @author Xihui Chen
 *
 */
public abstract class AbstractContainerModel extends AbstractWidgetModel {

    public static final String PROP_CHILDREN = "children"; //$NON-NLS-1$

    public static final String PROP_SELECTION = "selection"; //$NON-NLS-1$

    /**
     * Macros of the container, which will be available to its children.
     */
    public static final String PROP_MACROS = "macros"; //$NON-NLS-1$


    final private AbstractWidgetProperty childrenProperty;

    final private AbstractWidgetProperty selectionProperty;

    final private List<AbstractWidgetModel> childrenList = new LinkedList<AbstractWidgetModel>();

    private Map<String, String> macroMap = new LinkedHashMap<String, String>();

    private AbstractLayoutModel layoutWidget;


    public AbstractContainerModel() {
        super();
        childrenProperty = new UnsavableListProperty(
                PROP_CHILDREN, "children", WidgetPropertyCategory.Behavior, childrenList);

        selectionProperty = new UnsavableListProperty(
                PROP_SELECTION, "selection", WidgetPropertyCategory.Behavior, null);
    }

    /**add child to the end of the children list.
     * @param child the widget to be added
     * @param changeParent true if the widget's parent should be changed.
     */
    public synchronized void addChild(AbstractWidgetModel child, boolean changeParent){
        if(child != null && !childrenList.contains(child)){
            int newIndex = -1;
            if(layoutWidget != null){
                newIndex = childrenList.size() -1;
                childrenList.add(newIndex, child);
            }
            else
                childrenList.add(child);
            if(child instanceof AbstractLayoutModel)
                layoutWidget = (AbstractLayoutModel) child;
            if(changeParent)
                child.setParent(this);
            childrenProperty.firePropertyChange(newIndex, child);
        }

    }

    public synchronized void addChildren(List<AbstractWidgetModel> children, boolean changeParent){
        ArrayList<AbstractWidgetModel> oldList = new ArrayList<AbstractWidgetModel>(childrenList);
        for (AbstractWidgetModel child : children) {
            if (child != null && !childrenList.contains(child)) {
                int newIndex = -1;
                if (layoutWidget != null) {
                    newIndex = childrenList.size() - 1;
                    childrenList.add(newIndex, child);
                } else
                    childrenList.add(child);
                if (child instanceof AbstractLayoutModel)
                    layoutWidget = (AbstractLayoutModel) child;
                if (changeParent)
                    child.setParent(this);
            }
        }
        childrenProperty.firePropertyChange(oldList, children);
    }

    public void addChild(AbstractWidgetModel child){
        addChild(child, true);
    }

    public synchronized void addChild(int index, AbstractWidgetModel child){
        if(index < 0){
            addChild(child);
            return;
        }
        if(child != null && !childrenList.contains(child)){
            if(child instanceof AbstractLayoutModel){
                layoutWidget = (AbstractLayoutModel) child;
                index = childrenList.size();
            }else if(layoutWidget != null && index == childrenList.size()){
                index -=1;
            }
            childrenList.add(index, child);
            child.setParent(this);
            childrenProperty.firePropertyChange(index, child);
        }
    }

    public AbstractLayoutModel getLayoutWidget() {
        return layoutWidget;
    }

    public synchronized void removeChild(AbstractWidgetModel child){
        if(child != null && childrenList.remove(child)) {
            if(child instanceof AbstractLayoutModel)
                layoutWidget = null;
            child.setParent(null);
            childrenProperty.firePropertyChange(child, null);
        }
    }

    public synchronized void removeAllChildren(){
        childrenList.clear();
        layoutWidget = null;
        childrenProperty.firePropertyChange(childrenList, null);
    }

    @Override
    protected void configureBaseProperties() {
        super.configureBaseProperties();
        addProperty(new MacrosProperty(
                PROP_MACROS, "Macros", WidgetPropertyCategory.Basic,
                new MacrosInput(new LinkedHashMap<String, String>(), true)));
    }

    public List<AbstractWidgetModel> getChildren() {
        return childrenList;
    }

    /**
     * @return all descendants of this container.
     */
    public List<AbstractWidgetModel> getAllDescendants(){
        List<AbstractWidgetModel> allDescendants = new ArrayList<AbstractWidgetModel>();
        allDescendants.addAll(getChildren());
        for(AbstractWidgetModel widget : getChildren()){
            if(widget instanceof AbstractContainerModel){
                allDescendants.addAll(((AbstractContainerModel) widget).getAllDescendants());
            }
        }
        return allDescendants;
    }

    public AbstractWidgetModel getChildByName(String name){
        for(AbstractWidgetModel child : getChildren()){
            if(child.getName().equals(name))
                return child;
        }
        return null;
    }

    /**
     * @param widget
     * @return the index of the widget in the children list, which is also
     * the order of the widget in the display.
     */
    public final int getIndexOf(final AbstractWidgetModel widget){
        return childrenList.indexOf(widget);
    }

    public AbstractWidgetProperty getChildrenProperty() {
        return childrenProperty;
    }

    /**Change the order of the child.
     * @param child
     * @param newIndex
     */
    public final void changeChildOrder(final AbstractWidgetModel child, final int newIndex){
        if(childrenList.contains(child) && newIndex >= 0 && newIndex < childrenList.size()){
            if(newIndex == childrenList.indexOf(child))
                return;
            removeChild(child);
            addChild(newIndex, child);
            childrenProperty.firePropertyChange(null, childrenList);
        }
    }

    public AbstractWidgetProperty getSelectionProperty() {
        return selectionProperty;
    }

    public void selectWidgets(List<AbstractWidgetModel> widgets, boolean append){
        selectionProperty.firePropertyChange(append, widgets);
    }

    public void selectWidget(AbstractWidgetModel newWidget, boolean append) {
        selectWidgets(Arrays.asList(newWidget), append);
    }

    /** Set macro map of macro.
     *  Container keeps reference to the map, no copy.
     *  @param macroMap Map of macro name/value entries
     */
    public void setMacroMap(final Map<String, String> macroMap) {
        this.macroMap = macroMap;
    }

    /** @return Map of macro name/value entries */
    public Map<String, String> getMacroMap() {
        return macroMap;
    }

    public MacrosInput getMacrosInput(){
        return (MacrosInput)getCastedPropertyValue(PROP_MACROS);
    }

    /**Add a macro to the container.
     * This method must be called before this widget is activated.
     * @param macroName name of the macro
     * @param macroValue value of the macro
     */
    public void addMacro(String macroName, String macroValue){
        getMacrosInput().put(macroName, macroValue);
    }

    /**
     * @return the macros of its parent.
     */
    public Map<String, String> getParentMacroMap(){
        if(getParent() != null)
            return getParent().getMacroMap();
        else
            return PreferencesHelper.getMacros();
    }

    /**This is a flag to show if children operation edit policies should be installed.
     * @return true if children operation allowable.
     */
    public boolean isChildrenOperationAllowable(){
        return true;
    }



    @Override
    public void scale(double widthRatio, double heightRatio) {
        super.scale(widthRatio, heightRatio);
        scaleChildren();
    }

    public void scaleChildren() {
        Dimension size = getSize();
        double newWidthRatio = size.width/(double)getOriginSize().width;
        double newHeightRatio = size.height/(double)getOriginSize().height;
        for(AbstractWidgetModel child : getChildren()){
            child.scale(newWidthRatio, newHeightRatio);
        }
    }
}
