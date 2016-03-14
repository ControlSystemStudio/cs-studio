/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.commands.SetWidgetPropertyCommand;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.model.TextInputModel;
import org.csstudio.swt.widgets.datadefinition.IManualStringValueChangeListener;
import org.csstudio.swt.widgets.figures.TextInputFigure;
import org.csstudio.swt.widgets.figures.TextInputFigure.FileReturnPart;
import org.csstudio.swt.widgets.figures.TextInputFigure.FileSource;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

/**
 * The editpart delegate for draw2d text input widget.
 *
 * @author Xihui Chen
 *
 */
public class Draw2DTextInputEditpartDelegate implements ITextInputEditPartDelegate {


    private TextInputEditpart editpart;
    private TextInputModel model;
    private TextInputFigure textInputFigure;



    /**
     * @param editpart
     * @param model
     * @param superFigure the figure created by super.doCreateFigure().
     */
    public Draw2DTextInputEditpartDelegate(TextInputEditpart editpart,
            TextInputModel model, TextInputFigure superFigure) {
        this.editpart = editpart;
        this.model = model;
        this.textInputFigure =superFigure;
    }

    @Override
    public IFigure doCreateFigure() {
        textInputFigure.setSelectorType(model.getSelectorType());
        textInputFigure.setDateTimeFormat(model.getDateTimeFormat());
        textInputFigure.setFileSource(model.getFileSource());
        textInputFigure.setFileReturnPart(model.getFileReturnPart());

        textInputFigure
                .addManualValueChangeListener(new IManualStringValueChangeListener() {

                    @Override
                    public void manualValueChanged(String newValue) {
                        outputText(newValue);
                    }


                });

        return textInputFigure;
    }

    /**Call this method when user hit Enter or Ctrl+Enter for multiline input.
     * @param newValue
     */
    protected void outputText(String newValue) {
        if (editpart.getExecutionMode() == ExecutionMode.RUN_MODE) {
            editpart.setPVValue(TextInputModel.PROP_PVNAME, newValue);
            model.setPropertyValue(TextInputModel.PROP_TEXT,
                    newValue, false);
        } else {
            editpart.getViewer()
                    .getEditDomain()
                    .getCommandStack()
                    .execute(
                            new SetWidgetPropertyCommand(model,
                                    TextInputModel.PROP_TEXT, newValue));
        }
    }


    @Override
    public void createEditPolicies() {
        editpart.installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
                new TextUpdateDirectEditPolicy());
    }

    @Override
    public void registerPropertyChangeHandlers() {

        IWidgetPropertyChangeHandler selectorTypeHandler = new IWidgetPropertyChangeHandler() {

            @Override
            public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
                ((TextInputFigure)figure).setSelectorType(model.getSelectorType());
                return false;
            }
        };

        editpart.setPropertyChangeHandler(TextInputModel.PROP_SELECTOR_TYPE, selectorTypeHandler);

        IWidgetPropertyChangeHandler dateTimeFormatHandler = new IWidgetPropertyChangeHandler() {

            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure figure) {
                ((TextInputFigure) figure).setDateTimeFormat((String) newValue);
                return false;
            }
        };
        editpart.setPropertyChangeHandler(TextInputModel.PROP_DATETIME_FORMAT,
                dateTimeFormatHandler);

        IWidgetPropertyChangeHandler fileSourceHandler = new IWidgetPropertyChangeHandler() {

            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure figure) {
                ((TextInputFigure) figure)
                        .setFileSource(FileSource.values()[(Integer) newValue]);
                return false;
            }
        };
        editpart.setPropertyChangeHandler(TextInputModel.PROP_FILE_SOURCE,
                fileSourceHandler);

        IWidgetPropertyChangeHandler fileReturnPartHandler = new IWidgetPropertyChangeHandler() {

            @Override
            public boolean handleChange(Object oldValue, Object newValue,
                    IFigure figure) {
                ((TextInputFigure) figure).setFileReturnPart(FileReturnPart
                        .values()[(Integer) newValue]);
                return false;
            }
        };
        editpart.setPropertyChangeHandler(TextInputModel.PROP_FILE_RETURN_PART,
                fileReturnPartHandler);
    }


    /**
     * @param newValue
     */
    @Override
    public void updatePropSheet() {
        switch (model.getSelectorType()) {
        case NONE:
            model.setPropertyVisible(TextInputModel.PROP_DATETIME_FORMAT, false);
            model.setPropertyVisible(TextInputModel.PROP_FILE_RETURN_PART, false);
            model.setPropertyVisible(TextInputModel.PROP_FILE_SOURCE, false);
            break;
        case DATETIME:
            model.setPropertyVisible(TextInputModel.PROP_DATETIME_FORMAT, true);
            model.setPropertyVisible(TextInputModel.PROP_FILE_RETURN_PART,
                    false);
            model.setPropertyVisible(TextInputModel.PROP_FILE_SOURCE, false);
            break;
        case FILE:
            model.setPropertyVisible(TextInputModel.PROP_DATETIME_FORMAT, false);
            model.setPropertyVisible(TextInputModel.PROP_FILE_RETURN_PART, true);
            model.setPropertyVisible(TextInputModel.PROP_FILE_SOURCE, true);
            break;
        default:
            break;
        }

    }



}
