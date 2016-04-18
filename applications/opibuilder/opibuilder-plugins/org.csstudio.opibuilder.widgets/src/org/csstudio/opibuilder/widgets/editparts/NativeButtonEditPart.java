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

import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.csstudio.opibuilder.widgetActions.OpenDisplayAction;
import org.csstudio.opibuilder.widgets.figures.NativeButtonFigure;
import org.csstudio.opibuilder.widgets.model.ActionButtonModel;
import org.csstudio.opibuilder.widgets.model.NativeButtonModel;
import org.csstudio.swt.widgets.figures.ITextFigure;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

/**
 * EditPart controller for the Native Button widget.
 * @author Xihui Chen
 * @deprecated This is not used anymore since the native button is merged to action button.
 */
public final class NativeButtonEditPart extends AbstractPVWidgetEditPart {

    private Button button;

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFigure doCreateFigure() {
        NativeButtonModel model = getWidgetModel();
        int style=SWT.None;
        style|= model.isToggleButton()?SWT.TOGGLE:SWT.PUSH;
        style |= SWT.WRAP;
        final NativeButtonFigure buttonFigure =
                new NativeButtonFigure(this, style);
        button = buttonFigure.getSWTWidget();
        button.setText(model.getText());
        buttonFigure.setImagePath(model.getImagePath());
        updatePropSheet(model.isToggleButton());
        markAsControlPV(AbstractPVWidgetModel.PROP_PVNAME, AbstractPVWidgetModel.PROP_PVVALUE);
        return buttonFigure;
    }

    @Override
    protected void createEditPolicies() {
        super.createEditPolicies();
        if(getExecutionMode() == ExecutionMode.EDIT_MODE)
            installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new TextDirectEditPolicy());
    }


    @Override
    public void performRequest(Request request){
        if (getExecutionMode() == ExecutionMode.EDIT_MODE &&(
                request.getType() == RequestConstants.REQ_DIRECT_EDIT ||
                request.getType() == RequestConstants.REQ_OPEN))
            new TextEditManager(this,
                    new LabelCellEditorLocator(getFigure()), false).show();
    }

    @Override
    protected void hookMouseClickAction() {
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                List<AbstractWidgetAction> actions = getHookedActions();
                if(actions!= null){
                    for(AbstractWidgetAction action: actions)
                    {
                        if(action instanceof OpenDisplayAction)
                            ((OpenDisplayAction) action).runWithModifiers((e.stateMask & SWT.CTRL) != 0,
                                                                          (e.stateMask & SWT.SHIFT) != 0);
                        else
                            action.run();
                    }
                }
            }
        });
    }

    @Override
    public List<AbstractWidgetAction> getHookedActions() {
        ActionButtonModel widgetModel = getWidgetModel();
        boolean isSelected = button.getSelection();
        return ActionButtonEditPart.getHookedActionsForButton(widgetModel, isSelected);

    }

    @Override
    public NativeButtonModel getWidgetModel() {
        return (NativeButtonModel)getModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerPropertyChangeHandlers() {

        // text
        IWidgetPropertyChangeHandler textHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                button.setText(newValue.toString());
                button.setSize(button.getSize());
                return true;
            }
        };
        setPropertyChangeHandler(ActionButtonModel.PROP_TEXT, textHandler);


        //image
        IWidgetPropertyChangeHandler imageHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                NativeButtonFigure figure = (NativeButtonFigure) refreshableFigure;
                IPath absolutePath = (IPath)newValue;
                if(absolutePath != null && !absolutePath.isEmpty() && !absolutePath.isAbsolute())
                    absolutePath = ResourceUtil.buildAbsolutePath(
                            getWidgetModel(), absolutePath);
                figure.setImagePath(absolutePath);
                return true;
            }
        };
        setPropertyChangeHandler(ActionButtonModel.PROP_IMAGE, imageHandler);

        // button style
        final IWidgetPropertyChangeHandler buttonStyleHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                updatePropSheet((Boolean) newValue);
                return true;
            }
        };
        getWidgetModel().getProperty(ActionButtonModel.PROP_TOGGLE_BUTTON).
            addPropertyChangeListener(new PropertyChangeListener(){
                public void propertyChange(PropertyChangeEvent evt) {
                    buttonStyleHandler.handleChange(evt.getOldValue(), evt.getNewValue(), getFigure());
                }
            });
    }

    /**
        * @param newValue
        */
    private void updatePropSheet(final boolean newValue) {
        getWidgetModel().setPropertyVisible(
                    ActionButtonModel.PROP_RELEASED_ACTION_INDEX, newValue);
        getWidgetModel().setPropertyDescription(ActionButtonModel.PROP_ACTION_INDEX,
                    newValue ? "Push Action Index" : "Click Action Index" );
    }


    @Override
    public void setValue(Object value) {
        button.setText(value.toString());
    }

    @Override
    public Object getValue() {
        return button.getText();
    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class key) {
        if(key == ITextFigure.class)
            return getFigure();

        return super.getAdapter(key);
    }
}
