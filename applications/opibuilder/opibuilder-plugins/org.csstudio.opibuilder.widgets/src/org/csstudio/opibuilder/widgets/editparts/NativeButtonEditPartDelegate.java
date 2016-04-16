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

import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.csstudio.opibuilder.widgetActions.OpenDisplayAction;
import org.csstudio.opibuilder.widgets.figures.NativeButtonFigure;
import org.csstudio.opibuilder.widgets.model.ActionButtonModel;
import org.csstudio.opibuilder.widgets.util.SingleSourceHelper;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

/**
 * EditPart controller delegate for Native Button widget.
 * @author Xihui Chen
 *
 */
public final class NativeButtonEditPartDelegate implements IButtonEditPartDelegate{

    private Button button;
    private ActionButtonEditPart editpart;
    private boolean skipTraverse;

    public NativeButtonEditPartDelegate(ActionButtonEditPart editPart) {
        this.editpart = editPart;
    }

    @Override
    public IFigure doCreateFigure() {
        ActionButtonModel model = editpart.getWidgetModel();
        int style=SWT.None;
        style|= model.isToggleButton()?SWT.TOGGLE:SWT.PUSH;
        style |= SWT.WRAP;
        final NativeButtonFigure buttonFigure =
                new NativeButtonFigure(editpart, style);
        button = buttonFigure.getSWTWidget();
        button.setText(model.getText());
        button.addTraverseListener(e -> {
            if (skipTraverse) return;
            e.doit = false;
            skipTraverse = true;
            if (e.stateMask == 0) {
                SingleSourceHelper.swtControlTraverse(button, SWT.TRAVERSE_TAB_PREVIOUS);
            } else {
                SingleSourceHelper.swtControlTraverse(button, SWT.TRAVERSE_TAB_NEXT);
            }
            skipTraverse = false;
        });
        buttonFigure.setImagePath(model.getImagePath());
        return buttonFigure;
    }

    @Override
    public void hookMouseClickAction() {
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                List<AbstractWidgetAction> actions = editpart.getHookedActions();
                if(actions!= null){
                    for(AbstractWidgetAction action: actions){
                        if(action instanceof OpenDisplayAction)
                            ((OpenDisplayAction) action).runWithModifiers((e.stateMask & SWT.CTRL) !=0,
                                                                          (e.stateMask & SWT.SHIFT) !=0);
                        else
                            action.run();
                    }
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerPropertyChangeHandlers() {

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
        editpart.setPropertyChangeHandler(ActionButtonModel.PROP_TEXT, textHandler);


        //image
        IWidgetPropertyChangeHandler imageHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
                NativeButtonFigure figure = (NativeButtonFigure) refreshableFigure;
                IPath absolutePath = (IPath)newValue;
                if(absolutePath != null && !absolutePath.isEmpty() && !absolutePath.isAbsolute())
                    absolutePath = ResourceUtil.buildAbsolutePath(
                            editpart.getWidgetModel(), absolutePath);
                figure.setImagePath(absolutePath);
                return true;
            }
        };
        editpart.setPropertyChangeHandler(ActionButtonModel.PROP_IMAGE, imageHandler);

        // button style
        final IWidgetPropertyChangeHandler buttonStyleHandler = new IWidgetPropertyChangeHandler() {
            @Override
            public boolean handleChange(final Object oldValue,
                    final Object newValue, final IFigure refreshableFigure) {
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


    @Override
    public void setValue(Object value) {
        button.setText(value.toString());
    }

    @Override
    public Object getValue() {
        return button.getText();
    }

    @Override
    public void deactivate() {

    }

    @Override
    public boolean isSelected() {
        return button.getSelection();
    }
}
