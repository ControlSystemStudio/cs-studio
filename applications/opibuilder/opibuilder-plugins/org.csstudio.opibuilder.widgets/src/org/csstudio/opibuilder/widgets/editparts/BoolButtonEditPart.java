/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import java.util.List;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.model.BoolButtonModel;
import org.csstudio.simplepv.IPV;
import org.csstudio.simplepv.IPVListener;
import org.csstudio.swt.widgets.figures.BoolButtonFigure;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VType;
import org.eclipse.draw2d.IFigure;

/**
 * Boolean Button EditPart
 * @author Xihui Chen
 *
 */
public class BoolButtonEditPart extends AbstractBoolControlEditPart{

    private IPVListener loadLabelsFromPVListener;

    @Override
    protected IFigure doCreateFigure() {
        final BoolButtonModel model = getWidgetModel();

        BoolButtonFigure btn = new BoolButtonFigure();

        initializeCommonFigureProperties(btn, model);
        btn.setEffect3D(model.isEffect3D());
        btn.setSquareButton((model.isSquareButton()));
        btn.setShowLED(model.isShowLED());
        return btn;
    }

    @Override
    public BoolButtonModel getWidgetModel() {
        return (BoolButtonModel)getModel();
    }

    @Override
    protected void doActivate() {
        super.doActivate();
        // Must be called after the PV objects are created and
        // so cannot be called in the registerPropertyChangeHandlers()
        // method.
        registerLoadLabelsListener();
    }

    @Override
    protected void registerPropertyChangeHandlers() {
        registerCommonPropertyChangeHandlers();

        //effect 3D
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                BoolButtonFigure btn = (BoolButtonFigure) refreshableFigure;
                btn.setEffect3D((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(BoolButtonModel.PROP_EFFECT3D, handler);

        //Square LED
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                BoolButtonFigure btn = (BoolButtonFigure) refreshableFigure;
                btn.setSquareButton((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(BoolButtonModel.PROP_SQUARE_BUTTON, handler);

        //Show LED
        handler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                BoolButtonFigure btn = (BoolButtonFigure) refreshableFigure;
                btn.setShowLED((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(BoolButtonModel.PROP_SHOW_LED, handler);
    }

    /**
     * Load the button labels from the PV object.
     * This must be called after the PV objects are created.
     */
    private void registerLoadLabelsListener() {
        if(getExecutionMode() == ExecutionMode.RUN_MODE){
            if(getWidgetModel().isLabelsFromPV()){
                IPV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
                if(pv != null){
                    if(loadLabelsFromPVListener == null)
                        loadLabelsFromPVListener = new IPVListener.Stub() {
                            @Override
                            public void valueChanged(IPV pv) {
                                VType value = pv.getValue();
                                if (value != null && value instanceof VEnum){
                                    List<String> enumLabels = ((VEnum)value).getLabels();
                                    // This is a bool button so we require exactly two labels.
                                    if (enumLabels.size() > 0) {
                                        getWidgetModel().setPropertyValue(BoolButtonModel.PROP_OFF_LABEL, enumLabels.get(0));
                                    }
                                    if (enumLabels.size() > 1) {
                                        getWidgetModel().setPropertyValue(BoolButtonModel.PROP_ON_LABEL, enumLabels.get(1));
                                    }
                                }
                            }
                        };
                    pv.addListener(loadLabelsFromPVListener);
                }
            }
        }
    }
}
