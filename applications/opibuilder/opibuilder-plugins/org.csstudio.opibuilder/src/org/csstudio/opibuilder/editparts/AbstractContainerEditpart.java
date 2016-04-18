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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.opibuilder.dnd.DropPVtoContainerEditPolicy;
import org.csstudio.opibuilder.dnd.DropPVtoPVWidgetEditPolicy;
import org.csstudio.opibuilder.editpolicies.WidgetContainerEditPolicy;
import org.csstudio.opibuilder.editpolicies.WidgetXYLayoutEditPolicy;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.scriptUtil.WidgetUtil;
import org.csstudio.opibuilder.util.GeometryUtil;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.simplepv.IPV;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.CompoundSnapToHelper;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.SnapToGuides;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.editpolicies.SnapFeedbackPolicy;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.osgi.util.NLS;

/**The editpart for {@link AbstractContainerModel}
 * @author Xihui Chen, Sven Wende (class of same name in SDS)
 *
 */
public abstract class AbstractContainerEditpart extends AbstractBaseEditPart {

    private PropertyChangeListener childrenPropertyChangeListener;
    private PropertyChangeListener selectionPropertyChangeListener;
    private boolean isArrayValue = true;

    @Override
    protected void createEditPolicies() {
        super.createEditPolicies();

        installEditPolicy(EditPolicy.CONTAINER_ROLE, new WidgetContainerEditPolicy());
        installEditPolicy(EditPolicy.LAYOUT_ROLE,
                getExecutionMode() == ExecutionMode.EDIT_MODE ? new WidgetXYLayoutEditPolicy() : null);

        //the snap feedback effect
        installEditPolicy("Snap Feedback", new SnapFeedbackPolicy()); //$NON-NLS-1$
        if(getExecutionMode() == ExecutionMode.EDIT_MODE)
            installEditPolicy(DropPVtoPVWidgetEditPolicy.DROP_PV_ROLE, new DropPVtoContainerEditPolicy());
    }

    /**Get a child of this container by name.
     * @param name the name of the child widget
     * @return the widgetController of the child. null if the child doesn't exist.
     */
    public AbstractBaseEditPart getChild(String name){
        List<?> children = getChildren();
        for(Object o : children){
            if(o instanceof AbstractBaseEditPart){
                if(((AbstractBaseEditPart)o).getWidgetModel().getName().equals(name))
                    return (AbstractBaseEditPart)o;
            }
        }
        return null;
    }

    /**Get the widget which is a descendant of the container by name.
     * @param name the name of the widget.
     * @return the widget controller.
     * @throws Exception If widget with this name doesn't exist
     */
    public EditPart getWidget(String name) throws Exception{
        AbstractBaseEditPart widget = searchWidget(name);
        if(widget == null)
            throw new Exception("Widget with name \"" + name + "\" does not exist!");
        else
            return widget;
    }

    /**Recursively search the widget
     * @param name
     * @return
     */
    private AbstractBaseEditPart searchWidget(String name){
        AbstractBaseEditPart child = getChild(name);
        if(child != null)
            return child;
        else {
            for(Object obj : getChildren()){
                if(obj instanceof AbstractContainerEditpart){
                    AbstractContainerEditpart containerChild = (AbstractContainerEditpart)obj;
                    AbstractBaseEditPart widget = containerChild.searchWidget(name);
                    if(widget != null)
                        return widget;
                }
            }
        }
        return null;
    }

    /**
     * @return all pv names attached to this container and its children at runtime.
     */
    public Set<String> getAllRuntimePVNames(){
        Set<String> result = new HashSet<String>();
        Map<String, IPV> allPVs = getAllPVs();
        if(allPVs != null && !allPVs.isEmpty())
            result.addAll(getAllPVs().keySet());
        for(Object child: getChildren()){
            if(child instanceof AbstractContainerEditpart){
                result.addAll(((AbstractContainerEditpart)child).getAllRuntimePVNames());
            }else if(child instanceof AbstractBaseEditPart){
                allPVs = ((AbstractBaseEditPart)child).getAllPVs();
                if(allPVs != null && !allPVs.isEmpty())
                    result.addAll(allPVs.keySet());
            }
        }
        return result;
    }

    /**Add a child widget to the container.
     * @param widgetModel model of the widget to be added.
     * @see WidgetUtil#createWidgetModel(String)
     */
    public void addChild(AbstractWidgetModel widgetModel){
        getWidgetModel().addChild(widgetModel);
    }

    /**Add a child widget to the right of the container.
     * @param widgetModel model of the widget to be added.
     * @see WidgetUtil#createWidgetModel(String)
     */
    public void addChildToRight(AbstractWidgetModel widgetModel){
        Rectangle range = GeometryUtil.getChildrenRange(this);
        widgetModel.setX(range.x + range.width);
        addChild(widgetModel);
    }

    /**Add a child widget to the bottom of the container.
     * @param widgetModel model of the widget to be added.
     * @see WidgetUtil#createWidgetModel(String)
     */
    public void addChildToBottom(AbstractWidgetModel widgetModel){
        Rectangle range = GeometryUtil.getChildrenRange(this);
        widgetModel.setY(range.y + range.height);
        addChild(widgetModel);
    }

    /**Remove a child widget by its name.
     * @param widgetName name of the widget.
     * @throws RuntimeException if the widget name does not exist.
     */
    public void removeChildByName(String widgetName) {
        AbstractBaseEditPart child = getChild(widgetName);
        if(child != null)
            getWidgetModel().removeChild(child.getWidgetModel());
        else
             throw new RuntimeException(NLS.bind(
                     "Widget with name {0} doesn't exist!", widgetName));
    }

    /**Remove a child widget.
     * @param child the child widget.
     */
    public void removeChild(AbstractBaseEditPart child){
        getWidgetModel().removeChild(child.getWidgetModel());
    }

    /**Remove the child at index.
     * @param index index of the child.
     */
    public void removeChild(int index){
        removeChild((AbstractBaseEditPart) getChildren().get(index));
    }

    /**
     * Remove all children.
     */
    public void removeAllChildren(){
        getWidgetModel().removeAllChildren();
    }

    @Override
    public void setModel(Object model) {
        super.setModel(model);
        if(model instanceof AbstractContainerModel){
            //set macro map
            LinkedHashMap<String, String> macrosMap = new LinkedHashMap<String, String>();
            if(getWidgetModel().getMacrosInput().isInclude_parent_macros()){
                macrosMap.putAll(getWidgetModel().getParentMacroMap());
            }
            macrosMap.putAll(getWidgetModel().getMacrosInput().getMapCopy());
            getWidgetModel().setMacroMap(macrosMap);
        }

    }

    @Override
    protected void registerBasePropertyChangeHandlers() {
        super.registerBasePropertyChangeHandlers();
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler(){
            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure figure) {
                MacrosInput macrosInput = (MacrosInput)newValue;

                LinkedHashMap<String, String> macrosMap = new LinkedHashMap<String, String>();
                if(macrosInput.isInclude_parent_macros()){
                    macrosMap.putAll(getWidgetModel().getParentMacroMap());
                }
                macrosMap.putAll(macrosInput.getMapCopy());
                getWidgetModel().setMacroMap(macrosMap);
                return false;
            }
        };
        setPropertyChangeHandler(AbstractContainerModel.PROP_MACROS, handler);

        layout();

        childrenPropertyChangeListener = new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {

                        if(evt.getOldValue() instanceof Integer){
                            addChild(createChild(evt.getNewValue()), ((Integer)evt
                                    .getOldValue()).intValue());
                            layout();
                        }else if (evt.getOldValue() instanceof AbstractWidgetModel){
                            EditPart child = (EditPart)getViewer().getEditPartRegistry().get(
                                    evt.getOldValue());
                            if(child != null){
                                removeChild(child);
                                layout();
                            }
                        }else
                            refreshChildren();
                    }
                };
        getWidgetModel().getChildrenProperty().
            addPropertyChangeListener(childrenPropertyChangeListener);

        if(getExecutionMode() == ExecutionMode.EDIT_MODE){
            selectionPropertyChangeListener = new PropertyChangeListener(){
                @SuppressWarnings("unchecked")
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    List<AbstractWidgetModel> widgets = (List<AbstractWidgetModel>) evt.getNewValue();
                    List<EditPart> widgetEditparts = new ArrayList<EditPart>();
                    for(AbstractWidgetModel w : widgets){
                        EditPart e = (EditPart)getViewer().getEditPartRegistry().get(w);
                        if(e != null)
                            widgetEditparts.add(e);
                    }

                    if(!(Boolean)evt.getOldValue()){ //append
                        getViewer().deselectAll();
                    }
                    for(EditPart editpart : widgetEditparts){
                        if(editpart.isSelectable())
                            getViewer().appendSelection(editpart);
                    }
                }
            };

            getWidgetModel().getSelectionProperty().addPropertyChangeListener(
                    selectionPropertyChangeListener);
        }

    }


    @Override
    public void deactivate() {
        super.deactivate();
        getWidgetModel().getChildrenProperty().
            removeAllPropertyChangeListeners();
        childrenPropertyChangeListener = null;
        getWidgetModel().getSelectionProperty().removeAllPropertyChangeListeners();
        selectionPropertyChangeListener = null;
    }

    @Override
    protected List<AbstractWidgetModel> getModelChildren() {
        return ((AbstractContainerModel)getModel()).getChildren();
    }

    @Override
    public AbstractContainerModel getWidgetModel() {
        return (AbstractContainerModel)getModel();
    }

    public AbstractLayoutEditpart getLayoutWidget(){
        for(Object child : getChildren()){
            if(child instanceof AbstractLayoutEditpart){
                return (AbstractLayoutEditpart)child;
            }
        }
        return null;
    }

    @Override
    protected void refreshChildren() {
        super.refreshChildren();
        if(isActive())
            layout();
    }

    public void layout(){
        if(getExecutionMode() != ExecutionMode.RUN_MODE)
            return;
        AbstractLayoutEditpart layoutter = getLayoutWidget();
        if(layoutter != null && layoutter.getWidgetModel().isEnabled()){
            List<AbstractWidgetModel> modelChildren = new ArrayList<AbstractWidgetModel>();
            modelChildren.addAll(getModelChildren());
            modelChildren.remove(layoutter.getWidgetModel());
            layoutter.layout(modelChildren, getFigure().getClientArea());
        }
    }

    /**
     * Automatically set the container size according its children's geography size.
     */
    public void performAutosize(){
        Rectangle childrenRange = GeometryUtil.getChildrenRange(this);
        Point tranlateSize = new Point(childrenRange.x, childrenRange.y);

        getWidgetModel().setSize(new Dimension(
                        childrenRange.width + figure.getInsets().left + figure.getInsets().right,
                        childrenRange.height + figure.getInsets().top + figure.getInsets().bottom));


        for(Object editpart : getChildren()){
            AbstractWidgetModel widget = ((AbstractBaseEditPart)editpart).getWidgetModel();
            widget.setLocation(widget.getLocation().translate(tranlateSize.getNegated()));
        }
    }

    /**
     *By default, it returns an Object Array of its children's value.
     *If {@link #setValue(Object)} was called with a non Object[] input value, it
     *will return the value of its first child.
     *
     */
    @Override
    public Object getValue() {
        if(isArrayValue && getChildren().size()>0){
            Object[] data = new Object[getChildren().size()];
            for(int i=0; i<data.length; i++){
                data[i]=((AbstractBaseEditPart)getChildren().get(i)).getValue();
            }
            return data;
        }else if(getChildren().size()>0){
            return ((AbstractBaseEditPart)getChildren().get(0)).getValue();
        }
        return null;
    }
    /**
     * If input value is instance of Object[] and its length is equal or larger than children size,
     * it will write each element of value to each child according children's order. Otherwise,
     * it will write the input value as an whole Object to every child.
     *
     */
    @Override
    public void setValue(Object value) {
        //Write array to a container will write every child
        if (value instanceof Object[]) {
            Object[] a = (Object[]) value;
            if (a.length >= getChildren().size()) {
                isArrayValue=true;
                int i = 0;
                for (Object child : getChildren()) {
                    if (child instanceof AbstractBaseEditPart)
                        ((AbstractBaseEditPart) child).setValue(a[i++]);
                }
            }
        }else{ //Write None Array to a container will write every child the same value.
            isArrayValue = false;
            for (Object child : getChildren()) {
                if (child instanceof AbstractBaseEditPart)
                    ((AbstractBaseEditPart) child).setValue(value);
            }
        }
    }



    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
        //make snap to G work
        if (adapter == SnapToHelper.class) {
            List<SnapToHelper> snapStrategies = new ArrayList<SnapToHelper>();
            Boolean val = (Boolean)getViewer().getProperty(RulerProvider.PROPERTY_RULER_VISIBILITY);
            if (val != null && val.booleanValue())
                snapStrategies.add(new SnapToGuides(this));
            val = (Boolean)getViewer().getProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED);
            if (val != null && val.booleanValue())
                snapStrategies.add(new SnapToGeometry(this));
            val = (Boolean)getViewer().getProperty(SnapToGrid.PROPERTY_GRID_ENABLED);
            if (val != null && val.booleanValue())
                snapStrategies.add(new SnapToGrid(this));

            if (snapStrategies.size() == 0)
                return null;
            if (snapStrategies.size() == 1)
                return snapStrategies.get(0);

            SnapToHelper ss[] = new SnapToHelper[snapStrategies.size()];
            for (int i = 0; i < snapStrategies.size(); i++)
                ss[i] = snapStrategies.get(i);
            return new CompoundSnapToHelper(ss);
        }
        return super.getAdapter(adapter);
    }

}
