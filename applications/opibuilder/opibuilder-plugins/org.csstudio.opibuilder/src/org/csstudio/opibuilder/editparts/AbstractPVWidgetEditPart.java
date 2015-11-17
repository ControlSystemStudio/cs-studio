/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.editparts;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.opibuilder.dnd.DropPVtoPVWidgetEditPolicy;
import org.csstudio.simplepv.IPV;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.IFigure;
import org.diirt.vtype.VType;

/**The abstract edit part for all PV armed widgets.
 * Widgets inheritate this class will have the CSS context menu on it.
 * @author Xihui Chen
 * @author Takashi Nakamoto - general support for alarm sensitive colors
 */
public abstract class AbstractPVWidgetEditPart extends AbstractBaseEditPart implements IPVWidgetEditpart{

    protected PVWidgetEditpartDelegate delegate;

    public AbstractPVWidgetEditPart() {
        delegate = new PVWidgetEditpartDelegate(this);
    }

    @Override
    protected void doActivate() {
        super.doActivate();
        delegate.doActivate();
    }
    @Override
    public void activate() {
        super.activate();
        //PV should be started at the last step.
        delegate.startPVs();
    }

    @Override
    public void addSetPVValueListener(ISetPVValueListener listener) {
        delegate.addSetPVValueListener(listener);
    }

    @Override
    public Border calculateBorder(){
        Border border = delegate.calculateBorder();
        if(border == null)
            return super.calculateBorder();
        else
            return border;
    }

    @Override
    protected ConnectionHandler createConnectionHandler() {
        return new PVWidgetConnectionHandler(this);
    }

    @Override
    protected void createEditPolicies() {
        super.createEditPolicies();
        installEditPolicy(DropPVtoPVWidgetEditPolicy.DROP_PV_ROLE,
                new DropPVtoPVWidgetEditPolicy());
    }

    @Override
    protected void doDeActivate() {
        if(isActive()){
            delegate.doDeActivate();
            super.doDeActivate();
        }
    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class key) {
        if(key == ProcessVariable.class){
            return new ProcessVariable(getPVName());
        }
        return super.getAdapter(key);
    }

    /**
     * @return A String array with all PV names from PV properties.
     */
    public String[] getAllPVNames(){
        return delegate.getAllPVNames();
    }

    /**
     * @return the control PV. null if no control PV on this widget.
     */
    public IPV getControlPV(){
        return delegate.getControlPV();
    }

    /**Get the PV corresponding to the <code>PV Name</code> property.
     * It is same as calling <code>getPV("pv_name")</code>.
     * @return the PV corresponding to the <code>PV Name</code> property.
     * null if PV Name is not configured for this widget.
     */
    public IPV getPV(){
        return delegate.getPV();
    }

    /**Get the pv by PV property id.
     * @param pvPropId the PV property id.
     * @return the corresponding pv for the pvPropId. null if the pv doesn't exist.
     */
    public IPV getPV(String pvPropId){
        return delegate.getPV(pvPropId);
    }

    public PVWidgetEditpartDelegate getPVWidgetEditpartDelegate() {
        return delegate;
    }


    /**
     * @return the first PV name.
     */
    public String getPVName() {
        return delegate.getPVName();
    }


    /**Get value from one of the attached PVs.
     * @param pvPropId the property id of the PV. It is "pv_name" for the main PV.
     * @return the {@link IValue} of the PV.
     */
    public VType getPVValue(String pvPropId){
        return delegate.getPVValue(pvPropId);
    }

    @Override
    protected void initFigure(IFigure figure) {
        super.initFigure(figure);
        delegate.initFigure(figure);
    }


    /**For PV Control widgets, mark this PV as control PV.
     * @param pvPropId the propId of the PV.
     */
    protected void markAsControlPV(String pvPropId, String pvValuePropId){
        delegate.markAsControlPV(pvPropId, pvValuePropId);
    }

    @Override
    protected void registerBasePropertyChangeHandlers() {
        super.registerBasePropertyChangeHandlers();
        delegate.registerBasePropertyChangeHandlers();
    }

    public void setIgnoreOldPVValue(boolean ignoreOldValue) {
        delegate.setIgnoreOldPVValue(ignoreOldValue);
    }

    /**Set PV to given value. Should accept Double, Double[], Integer, String, maybe more.
     * @param pvPropId
     * @param value
     */
    public void setPVValue(String pvPropId, Object value){
        delegate.setPVValue(pvPropId, value);
    }

    @Override
    public boolean isPVControlWidget() {
        return delegate.isPVControlWidget();
    }

    /**
     * Set whether the editpart is enabled for control.
     */
    @Override
    public void setControlEnabled(boolean enabled) {
        delegate.setControlEnabled(enabled);
    }

}
