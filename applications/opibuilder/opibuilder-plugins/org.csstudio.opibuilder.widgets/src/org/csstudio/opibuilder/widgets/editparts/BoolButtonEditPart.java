/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.model.BoolButtonModel;
import org.csstudio.swt.widgets.figures.BoolButtonFigure;
import org.eclipse.draw2d.IFigure;

/**
 * Boolean Button EditPart
 * @author Xihui Chen
 *
 */
public class BoolButtonEditPart extends AbstractBoolControlEditPart{

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
    protected void registerPropertyChangeHandlers() {
        registerCommonPropertyChangeHandlers();

        //effect 3D
        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
            public boolean handleChange(final Object oldValue,
                    final Object newValue,
                    final IFigure refreshableFigure) {
                BoolButtonFigure btn = (BoolButtonFigure) refreshableFigure;
                btn.setEffect3D((Boolean) newValue);
                return true;
            }
        };
        setPropertyChangeHandler(BoolButtonModel.PROP_EFFECT3D, handler);

        //Sqaure LED
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

}
