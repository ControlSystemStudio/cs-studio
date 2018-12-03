/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;

import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.ComboFigure;
import org.csstudio.opibuilder.widgets.model.ComboModel;
import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.IPVListener;
import org.csstudio.simplepv.VTypeHelper;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VType;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;

/**The editpart of a combo.
 *
 * @author Xihui Chen
 *
 */
public final class ComboEditPart extends AbstractPVWidgetEditPart {
    /** Running on Linux? "gtk" or similar */
    private static final boolean is_linux = SWT.getPlatform().toLowerCase().contains("gtk");

    private IPVListener loadItemsFromPVListener;

    private Combo combo;
    private SelectionListener comboSelectionListener;

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        final ComboModel model = getWidgetModel();
        updatePropSheet(model.isItemsFromPV());
        ComboFigure comboFigure = new ComboFigure(this);
        combo = comboFigure.getSWTWidget();

        List<String> items = getWidgetModel().getItems();

        updateCombo(items);

        markAsControlPV(AbstractPVWidgetModel.PROP_PVNAME, AbstractPVWidgetModel.PROP_PVVALUE);

        return comboFigure;
    }

    /**
     * @param items
     */
    private void updateCombo(List<String> items) {
        if(items !=null && getExecutionMode() == ExecutionMode.RUN_MODE){
            combo.removeAll();

            for(String item : items){
                combo.add(item);
            }

            //write value to pv if pv name is not empty
            if(getWidgetModel().getPVName().trim().length() > 0){
                if(comboSelectionListener !=null)
                    combo.removeSelectionListener(comboSelectionListener);
                comboSelectionListener = new SelectionAdapter(){
                        @Override
                        public void widgetSelected(SelectionEvent e) {

                            // On Linux, assert the selection was accomplished
                            // by clicking on an item.
                            // Ignore scroll wheel changes.
                            // On Mac, scroll wheel does not change combo value,
                            // and as on Windows the stateMask is always 0
                            if (!is_linux  ||  e.stateMask == SWT.BUTTON1)
                                setPVValue(AbstractPVWidgetModel.PROP_PVNAME, combo.getText());
                            else
                            {   // Ignore selections from mouse wheel (stateMask == 0).
                                // Unfortunately this also ignores selections via keyboard,
                                // which has been discussed in
                                // https://github.com/ControlSystemStudio/cs-studio/issues/2276
                                // Restore current value to UI.
                                final String current = VTypeHelper.getString((VType)getPropertyValue(ComboModel.PROP_PVVALUE));
                                final int sel = Arrays.asList(combo.getItems()).indexOf(current);
                                if (sel >= 0)
                                    combo.select(sel);
                                else
                                    combo.deselectAll();
                            }
                        }
                };
                combo.addSelectionListener(comboSelectionListener);
            }

        }
    }

    @Override
    public ComboModel getWidgetModel() {
        return (ComboModel)getModel();
    }

    @Override
    protected void doActivate() {
        super.doActivate();
        registerLoadItemsListener();
    }

    /**
     *
     */
    private void registerLoadItemsListener() {
        //load items from PV
        if(getExecutionMode() == ExecutionMode.RUN_MODE){
            if(getWidgetModel().isItemsFromPV()){
                IPV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
                if(pv != null){
                    if(loadItemsFromPVListener == null)
                        loadItemsFromPVListener = new IPVListener.Stub() {
                        @Override
                            public void valueChanged(IPV pv) {
                                VType value = pv.getValue();
                                if (value != null && value instanceof VEnum){
                                    List<String> items = ((VEnum)value).getLabels();
                                        getWidgetModel().setPropertyValue(
                                                ComboModel.PROP_ITEMS, items);
                                    }
                            }
                        };
                    pv.addListener(loadItemsFromPVListener);
                }
            }
        }
    }

    @Override
    protected void doDeActivate() {
        super.doDeActivate();
        if(getWidgetModel().isItemsFromPV()){
            IPV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
            if(pv != null && loadItemsFromPVListener !=null){
                pv.removeListener(loadItemsFromPVListener);
            }
        }
//        ((ComboFigure)getFigure()).dispose();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {
        IWidgetPropertyChangeHandler pvNameHandler = new IWidgetPropertyChangeHandler() {

            @Override
            public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
                registerLoadItemsListener();
                return false;
            }
        };
        setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME, pvNameHandler);


        autoSizeWidget((ComboFigure) getFigure());
        // PV_Value
        IWidgetPropertyChangeHandler pvhandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                if(newValue != null){
                    String stringValue = VTypeHelper.getString((VType)newValue);
                    if(Arrays.asList(combo.getItems()).contains(stringValue))
                        combo.setText(stringValue);
                    else
                        combo.deselectAll();
//
//                    if(getWidgetModel().isBorderAlarmSensitve())
//                            autoSizeWidget((ComboFigure) refreshableFigure);
                }

                return true;
            }
        };
        setPropertyChangeHandler(ComboModel.PROP_PVVALUE, pvhandler);

        // Items
        IWidgetPropertyChangeHandler itemsHandler = new IWidgetPropertyChangeHandler() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                if(newValue != null && newValue instanceof List){
                    updateCombo((List<String>)newValue);
                    if(getWidgetModel().isItemsFromPV())
                        combo.setText(VTypeHelper.getString(getPVValue(AbstractPVWidgetModel.PROP_PVNAME)));
                }
                return true;
            }
        };
        setPropertyChangeHandler(ComboModel.PROP_ITEMS, itemsHandler);

        final IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                updatePropSheet((Boolean) newValue);
                return false;
            }
        };
        getWidgetModel().getProperty(ComboModel.PROP_ITEMS_FROM_PV).
            addPropertyChangeListener(new PropertyChangeListener(){
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    handler.handleChange(evt.getOldValue(), evt.getNewValue(), getFigure());
                }
        });


        //size change handlers--always apply the default height
        IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue, final Object newValue,
                    final IFigure figure) {
                autoSizeWidget((ComboFigure)figure);
                return true;
            }
        };
        setPropertyChangeHandler(AbstractWidgetModel.PROP_WIDTH, handle);
        setPropertyChangeHandler(AbstractWidgetModel.PROP_HEIGHT, handle);
        setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_STYLE, handle);
        setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_WIDTH, handle);
        setPropertyChangeHandler(ComboModel.PROP_FONT, handle);
    }

        /**
        * @param actionsFromPV
        */
    private void updatePropSheet(final boolean itemsFromPV) {
        getWidgetModel().setPropertyVisible(
                ComboModel.PROP_ITEMS, !itemsFromPV);
    }

    private void autoSizeWidget(ComboFigure comboFigure) {
        Dimension d = comboFigure.getAutoSizeDimension();
        getWidgetModel().setSize(getWidgetModel().getWidth(), d.height);
    }

    @Override
    public String getValue() {
        return combo.getText();
    }

    @Override
    public void setValue(Object value) {
        if(value instanceof String)
            combo.setText((String) value);
        else if (value instanceof Number)
            combo.select(((Number)value).intValue());
        else
            super.setValue(value);
    }

}
