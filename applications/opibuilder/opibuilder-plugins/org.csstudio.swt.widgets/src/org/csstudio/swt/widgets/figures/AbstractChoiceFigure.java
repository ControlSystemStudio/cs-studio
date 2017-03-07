/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import org.csstudio.swt.widgets.introspection.DefaultWidgetIntrospector;
import org.csstudio.swt.widgets.introspection.Introspectable;
import org.csstudio.ui.util.ColorConstants;
import org.eclipse.draw2d.ButtonGroup;
import org.eclipse.draw2d.ChangeEvent;
import org.eclipse.draw2d.ChangeListener;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Toggle;
import org.eclipse.draw2d.ToggleModel;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

/**The abstract figure for widget which can perform choice action.
 * @author Xihui Chen
 *
 */
public abstract class AbstractChoiceFigure extends Figure implements Introspectable{

    public interface IChoiceButtonListener extends EventListener {
        /**Executed when one of the choice button pressed.
         * @param index the index of the button.
         * @param value the string value of the state.
         */
        public void buttonPressed(int index, String value);
    }

    private ButtonGroup buttonGroup;
    private List<String> states;
    private List<Toggle> toggles;
    private List<ToggleModel> models;

    private List<IChoiceButtonListener> listeners;
    private boolean fromSetState = false;

    private boolean isHorizontal = false;


    protected Color selectedColor = ColorConstants.black;

    protected boolean runMode;

    public AbstractChoiceFigure(boolean runMode) {
        this.runMode = runMode;
        buttonGroup = new ButtonGroup();
        states = new ArrayList<String>();
        toggles = new ArrayList<Toggle>();
        models = new ArrayList<ToggleModel>();
        listeners = new ArrayList<IChoiceButtonListener>();
    }

    public void addChoiceButtonListener(IChoiceButtonListener listener){
        if(listener != null)
            listeners.add(listener);
    }

    public void removeChoiceButtonListener(IChoiceButtonListener listener){
        if(listeners.contains(listener))
            listeners.remove(listener);
    }

    protected abstract Toggle createToggle(String text);

    private void fireButtonPressed(int index, String value){
        for(IChoiceButtonListener listener : listeners){
            listener.buttonPressed(index, value);
        }
    }

    @Override
    public void setEnabled(boolean value) {
        super.setEnabled(value);

        for(Toggle toggle : toggles){
            toggle.setEnabled(value);
        }
        repaint();
    }

    /**
     * @return the selectedColor
     */
    public Color getSelectedColor() {
        return selectedColor;
    }

    public synchronized String getState(){
        return states.get(models.indexOf(buttonGroup.getSelected()));
    }

    /**Get all states.
     * @return all states.
     */
    public List<String> getStates() {
        return states;
    }

    /**
     * @return the isHorizontal
     */
    public boolean isHorizontal() {
        return isHorizontal;
    }

    @Override
    protected void layout() {
        super.layout();
        if(states.size() >0){
            Rectangle clientArea = getClientArea();
            if (isHorizontal){
                int avgWidth = clientArea.width/states.size();
                int startX = clientArea.x;
                for(Object child : getChildren()){
                    ((Figure)child).setBounds(new Rectangle(
                            startX, clientArea.y, avgWidth, clientArea.height));
                    startX += avgWidth;
                }
            }
            else {
                int avgHeight = clientArea.height/states.size();
                int startY = clientArea.y;
                for(Object child : getChildren()){
                    ((Figure)child).setBounds(new Rectangle(
                            clientArea.x, startY, clientArea.width, avgHeight));
                    startY += avgHeight;
                }
            }
        }
    }


    @Override
    protected void paintClientArea(Graphics graphics) {
        super.paintClientArea(graphics);
        if(states.size() <=0){
            graphics.fillRectangle(getClientArea());
        }
    }

    public void setHorizontal(boolean newValue) {
        if(this.isHorizontal == newValue)
            return;
        isHorizontal = newValue;
        revalidate();
    }

    public void setSelectedColor(Color checkedColor) {
        if(this.selectedColor != null && this.selectedColor.equals(checkedColor))
            return;
        this.selectedColor = checkedColor;
        repaint();
    }

    public synchronized void setState(int stateIndex){
        if(stateIndex < states.size()){
            fromSetState = true;
            buttonGroup.setSelected(models.get(stateIndex));
            fromSetState = false;
        }

    }

    public synchronized void setState(String state){
        fromSetState = true;
        if(states.contains(state)){
            buttonGroup.setSelected(models.get(
                states.indexOf(state)));
        }else
            buttonGroup.setSelected(null);
        fromSetState = false;
    }

    /**Set all the state string values.
     * @param states the states
     */
    public void setStates(List<String> states) {
        this.states = states;
        removeAll();
        for(Object model : buttonGroup.getElements().toArray()){
            buttonGroup.remove((ToggleModel) model);
        }
        toggles.clear();
        models.clear();
        int i=0;
        for(final String state : states){
            final int index = i++;
            ToggleModel toggleModel = new ToggleModel();
            final Toggle toggle = createToggle(state);
            if(!runMode)
                toggle.setEventHandler(null);
            toggleModel.addChangeListener(new ChangeListener() {

                public void handleStateChanged(ChangeEvent event) {
                    if(event.getPropertyName().equals(ToggleModel.SELECTED_PROPERTY) &&
                            toggle.isSelected()){
                        if(fromSetState)
                            fromSetState = false;
                        else
                            fireButtonPressed(index, state);
                    }
                }
            });

            buttonGroup.add(toggleModel);
            toggle.setModel(toggleModel);
            toggle.setEnabled(isEnabled());
            toggles.add(toggle);
            models.add(toggleModel);
            add(toggle);
        }
    }

    public BeanInfo getBeanInfo() throws IntrospectionException {
        return new DefaultWidgetIntrospector().getBeanInfo(this.getClass());
    }

    @Override
    public void setFocusTraversable(boolean focusTraversable) {
        super.setFocusTraversable(focusTraversable);
        for (Toggle toggle : toggles) {
            toggle.setFocusTraversable(focusTraversable);
        }
    }

    @Override
    public void setRequestFocusEnabled(boolean requestFocusEnabled) {
        super.setRequestFocusEnabled(requestFocusEnabled);
        for (Toggle toggle : toggles) {
            toggle.setRequestFocusEnabled(requestFocusEnabled);
        }
    }

}
