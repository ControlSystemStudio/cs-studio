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

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.NativeTextFigure;
import org.csstudio.opibuilder.widgets.model.TextInputModel;
import org.csstudio.opibuilder.widgets.util.SingleSourceHelper;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * The editpart delegate for native text input widget.
 *
 * @author Xihui Chen
 *
 */
public class NativeTextEditpartDelegate implements ITextInputEditPartDelegate {


    private TextInputEditpart editpart;
    private TextInputModel model;
    private Text text;



    public NativeTextEditpartDelegate(TextInputEditpart editpart,
            TextInputModel model) {
        this.editpart = editpart;
        this.model = model;
    }

    protected void setText(Text text) {
        this.text = text;
    }

    /**
     *
     * @return the SWT style for the text widget (based on the model settings)
     */
    protected int getTextFigureStyle() {
        int style=SWT.NONE;
        if(model.isShowNativeBorder())
            style |= SWT.BORDER;
        if(model.isMultilineInput()){
            style |= SWT.MULTI;
            if(model.isShowHScroll())
                style |= SWT.H_SCROLL;
            if(model.isShowVScroll())
                style |= SWT.V_SCROLL;
            if(model.isWrapWords())
                style |= SWT.WRAP;
        }else{
            style |= SWT.SINGLE;
            if(model.isPasswordInput())
                style |= SWT.PASSWORD;
        }
        if(model.isReadOnly())
            style |= SWT.READ_ONLY;
        switch (model.getHorizontalAlignment()) {
        case CENTER:
            style |= SWT.CENTER;
            break;
        case LEFT:
            style |= SWT.LEFT;
            break;
        case RIGHT:
            style |= SWT.RIGHT;
        default:
            break;
        }

        return style;
    }

    @Override
    public IFigure doCreateFigure() {
        int style = getTextFigureStyle();

        final NativeTextFigure figure = new NativeTextFigure(editpart, style);
        setText(figure.getSWTWidget());

        if(!model.isReadOnly()){
            if(model.isMultilineInput()){
                text.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent keyEvent) {
                        if (keyEvent.character == '\r') { // Return key
                            if (text != null && !text.isDisposed()
                                    && (text.getStyle() & SWT.MULTI) != 0) {
                                if ((keyEvent.stateMask & SWT.CTRL) != 0) {
                                    outputText(text.getText());
                                  keyEvent.doit=false;
                                  text.getShell().setFocus();
                                }
                            }

                        }
                    }
                });
            }else {
                text.addListener (SWT.DefaultSelection, new Listener () {
                    public void handleEvent (Event e) {
                        outputText(text.getText());
                        switch (model.getFocusTraverse()) {
                        case LOSE:
                             text.getShell().setFocus();
                             break;
                        case NEXT:
                            SingleSourceHelper.swtControlTraverse(text, SWT.TRAVERSE_TAB_PREVIOUS);
                            break;
                        case PREVIOUS:
                            SingleSourceHelper.swtControlTraverse(text, SWT.TRAVERSE_TAB_NEXT);
                            break;
                        case KEEP:
                        default:
                            break;
                        }
                    }
                });
            }
            //Recover text if editing aborted.
            text.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent keyEvent) {
                    if(keyEvent.character == SWT.ESC){
                        text.setText(model.getText());
                    }
                }
            });
            text.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    //On mobile, lost focus should output text since there is not enter hit or ctrl key.
                    if(editpart.getPV() != null && !OPIBuilderPlugin.isMobile(text.getDisplay()))
                        text.setText(model.getText());
                    else if(figure.isEnabled())
                        outputText(text.getText());
                }
            });
        }
        return figure;
    }

    protected void outputText(String newValue) {
        if(editpart.getPV() == null){
            editpart.setPropertyValue(TextInputModel.PROP_TEXT, newValue);
            editpart.outputPVValue(newValue);
        }
        else{
            //PV may not be changed instantly, so recover it to old text first.
            text.setText(model.getText());
            //Write PV and update the text with new PV value if writing succeed.
            editpart.outputPVValue(newValue);
        }
    }

    @Override
    public void updatePropSheet() {
        boolean isMulti = model.isMultilineInput();
        model.setPropertyVisible(TextInputModel.PROP_SHOW_H_SCROLL, isMulti);
        model.setPropertyVisible(TextInputModel.PROP_SHOW_V_SCROLL, isMulti);
        model.setPropertyVisible(TextInputModel.PROP_WRAP_WORDS, isMulti);
        model.setPropertyVisible(TextInputModel.PROP_PASSWORD_INPUT, !isMulti);
    }


    @Override
    public void createEditPolicies() {
        if(editpart.getExecutionMode()==ExecutionMode.RUN_MODE)
            editpart.installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,    null);
    }


    public void setFigureText(String text) {
        this.text.setText(text);
    }

    @Override
    public void registerPropertyChangeHandlers() {
        editpart.removeAllPropertyChangeHandlers(TextInputModel.PROP_ALIGN_H);

        PropertyChangeListener updatePropSheetListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                updatePropSheet();
            }
        };

        model.getProperty(TextInputModel.PROP_MULTILINE_INPUT)
            .addPropertyChangeListener(updatePropSheetListener);

        IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {

            @Override
            public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
                AbstractContainerModel parent = model.getParent();
                parent.removeChild(model);
                parent.addChild(model);
                parent.selectWidget(model, true);
                return false;
            }
        };
        editpart.setPropertyChangeHandler(TextInputModel.PROP_SHOW_NATIVE_BORDER, handler);
        editpart.setPropertyChangeHandler(TextInputModel.PROP_MULTILINE_INPUT, handler);
        editpart.setPropertyChangeHandler(TextInputModel.PROP_WRAP_WORDS, handler);
        editpart.setPropertyChangeHandler(TextInputModel.PROP_SHOW_H_SCROLL, handler);
        editpart.setPropertyChangeHandler(TextInputModel.PROP_SHOW_V_SCROLL, handler);
        editpart.setPropertyChangeHandler(TextInputModel.PROP_PASSWORD_INPUT, handler);
        editpart.setPropertyChangeHandler(TextInputModel.PROP_ALIGN_H, handler);

    }


    public void performAutoSize() {
        model.setSize(((NativeTextFigure)editpart.getFigure()).
                getAutoSizeDimension());
    }

    public String getValue() {
        return text.getText();
    }

}
