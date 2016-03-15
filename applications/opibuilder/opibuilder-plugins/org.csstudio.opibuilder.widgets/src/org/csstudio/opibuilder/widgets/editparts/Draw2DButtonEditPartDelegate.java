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
import java.util.List;

import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.csstudio.opibuilder.widgetActions.OpenDisplayAction;
import org.csstudio.opibuilder.widgets.model.ActionButtonModel;
import org.csstudio.swt.widgets.figures.ActionButtonFigure;
import org.csstudio.swt.widgets.figures.ActionButtonFigure.ButtonActionListener;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.SWT;

/**
 * EditPart controller for the ActioButton widget. The controller mediates
 * between {@link ActionButtonModel} and {@link ActionButtonFigure2}.
 * @author Sven Wende (class of same name in SDS)
 * @author Xihui Chen
 *
 */
public class Draw2DButtonEditPartDelegate implements IButtonEditPartDelegate{


    private ActionButtonEditPart editpart;

    public Draw2DButtonEditPartDelegate(ActionButtonEditPart editpart) {
        this.editpart = editpart;
    }


    /* (non-Javadoc)
     * @see org.csstudio.opibuilder.widgets.editparts.IButtonEditPartDelegate#doCreateFigure()
     */
    @Override
    public IFigure doCreateFigure() {
        ActionButtonModel model = editpart.getWidgetModel();

        final ActionButtonFigure buttonFigure = new ActionButtonFigure(editpart.getExecutionMode() == ExecutionMode.RUN_MODE);
        buttonFigure.setText(model.getText());
        buttonFigure.setToggleStyle(model.isToggleButton());
        buttonFigure.setImagePath(model.getImagePath());
        editpart.updatePropSheet();
        return buttonFigure;
    }

    /* (non-Javadoc)
     * @see org.csstudio.opibuilder.widgets.editparts.IButtonEditPartDelegate#hookMouseClickAction()
     */
    @Override
    public void hookMouseClickAction() {
        ((ActionButtonFigure)editpart.getFigure()).addActionListener(new ButtonActionListener(){
            @Override
            public void actionPerformed(int mouseEventState) {
                List<AbstractWidgetAction> actions = editpart.getHookedActions();
                if(actions!= null){
                    for(AbstractWidgetAction action: actions){
                        if (action instanceof OpenDisplayAction)
                            ((OpenDisplayAction) action).runWithModifiers((mouseEventState & SWT.CONTROL) != 0,
                                                                          (mouseEventState & SWT.SHIFT)   != 0);
                        else
                            action.run();
                    }
                }
            }
        });
    }


    /* (non-Javadoc)
     * @see org.csstudio.opibuilder.widgets.editparts.IButtonEditPartDelegate#deactivate()
     */
    @Override
    public void deactivate() {
        ((ActionButtonFigure)editpart.getFigure()).dispose();
    }




    /* (non-Javadoc)
     * @see org.csstudio.opibuilder.widgets.editparts.IButtonEditPartDelegate#registerPropertyChangeHandlers()
     */
    @Override
    public void registerPropertyChangeHandlers() {

        // text
        IWidgetPropertyChangeHandler textHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                ActionButtonFigure figure = (ActionButtonFigure) refreshableFigure;
                figure.setText(newValue.toString());
                figure.calculateTextPosition();
                return true;
            }
        };
        editpart.setPropertyChangeHandler(ActionButtonModel.PROP_TEXT, textHandler);

        //image
        IWidgetPropertyChangeHandler imageHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                ActionButtonFigure figure = (ActionButtonFigure) refreshableFigure;
                IPath absolutePath = (IPath)newValue;
                if(absolutePath != null && !absolutePath.isEmpty() && !absolutePath.isAbsolute())
                    absolutePath = ResourceUtil.buildAbsolutePath(
                            editpart.getWidgetModel(), absolutePath);
                figure.setImagePath(absolutePath);
                return true;
            }
        };
        editpart.setPropertyChangeHandler(ActionButtonModel.PROP_IMAGE, imageHandler);

        // width
        IWidgetPropertyChangeHandler widthHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                ActionButtonFigure figure = (ActionButtonFigure) refreshableFigure;
                Integer height = (Integer) editpart.getPropertyValue(ActionButtonModel.PROP_HEIGHT);
                figure.calculateTextPosition((Integer) newValue, height);
                return true;
            }
        };
        editpart.setPropertyChangeHandler(ActionButtonModel.PROP_WIDTH, widthHandler);

        // height
        IWidgetPropertyChangeHandler heightHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                ActionButtonFigure figure = (ActionButtonFigure) refreshableFigure;
                Integer width = (Integer) editpart.getPropertyValue(ActionButtonModel.PROP_WIDTH);
                figure.calculateTextPosition(width, (Integer) newValue);
                return true;
            }
        };
        editpart.setPropertyChangeHandler(ActionButtonModel.PROP_HEIGHT, heightHandler);

        // button style
        final IWidgetPropertyChangeHandler buttonStyleHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                ActionButtonFigure figure = (ActionButtonFigure) refreshableFigure;
                figure.setToggleStyle((Boolean) newValue);
                editpart.updatePropSheet();
                return true;
            }


        };
        editpart.getWidgetModel().getProperty(ActionButtonModel.PROP_TOGGLE_BUTTON).
            addPropertyChangeListener(new PropertyChangeListener(){
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    buttonStyleHandler.handleChange(evt.getOldValue(), evt.getNewValue(), editpart.getFigure());
                }
            });
    }




    /* (non-Javadoc)
     * @see org.csstudio.opibuilder.widgets.editparts.IButtonEditPartDelegate#setValue(java.lang.Object)
     */
    @Override
    public void setValue(Object value) {
        ((ActionButtonFigure)editpart.getFigure()).setText(value.toString());
    }

    /* (non-Javadoc)
     * @see org.csstudio.opibuilder.widgets.editparts.IButtonEditPartDelegate#getValue()
     */
    @Override
    public Object getValue() {
        return ((ActionButtonFigure)editpart.getFigure()).getText();
    }


    @Override
    public boolean isSelected() {
        return ((ActionButtonFigure)editpart.getFigure()).isSelected();
    }


}
