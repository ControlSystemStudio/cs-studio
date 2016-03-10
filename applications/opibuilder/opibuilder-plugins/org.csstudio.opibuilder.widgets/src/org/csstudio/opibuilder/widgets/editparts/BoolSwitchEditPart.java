/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.model.BoolSwitchModel;
import org.csstudio.swt.widgets.figures.BoolSwitchFigure;
import org.eclipse.draw2d.IFigure;

/**
 * Boolean Switch EditPart
 * @author Xihui Chen
 *
 */
public class BoolSwitchEditPart extends AbstractBoolControlEditPart{

    @Override
    protected IFigure doCreateFigure() {
        final BoolSwitchModel model = getWidgetModel();

        BoolSwitchFigure boolSwitch = new BoolSwitchFigure();

        initializeCommonFigureProperties(boolSwitch, model);
        boolSwitch.setEffect3D(model.isEffect3D());
        return boolSwitch;


    }

    @Override
    public BoolSwitchModel getWidgetModel() {
        return (BoolSwitchModel)getModel();
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
                BoolSwitchFigure boolSwitch = (BoolSwitchFigure) refreshableFigure;
                boolSwitch.setEffect3D((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(BoolSwitchModel.PROP_EFFECT3D, handler);


    }

}
