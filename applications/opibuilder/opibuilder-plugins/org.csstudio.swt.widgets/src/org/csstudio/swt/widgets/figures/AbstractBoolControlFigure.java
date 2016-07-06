/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.swt.widgets.datadefinition.IManualValueChangeListener;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

/**
 * Abstract boolean control figure for bool button, toggle switch...
 * @author Xihui Chen
 *
 */
public class AbstractBoolControlFigure extends AbstractBoolFigure {

    public enum ShowConfirmDialog{
        NO("No"),
        Both("Both"),
        PUSH("Push"),
        RELEASE("Release");

        String description;

        private ShowConfirmDialog(String desc) {
            this.description = desc;
        }

        public static String[] stringValues(){
            String[] sv = new String[values().length];
            int i=0;
            for(ShowConfirmDialog p : values())
                sv[i++] = p.toString();
            return sv;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    class ButtonPresser extends MouseListener.Stub {
        private boolean canceled = false;
            @Override
            public void mousePressed(MouseEvent me) {
                final Figure figure = (Figure) me.getSource();
                // Check location to ignore bogus mouse clicks,
                // see https://github.com/ControlSystemStudio/cs-studio/issues/1818
                if (me.button != 1  ||
                    ! figure.containsPoint(me.getLocation()))
                    return;
                boolean isOpen = false;
                if(runMode){
                     if(toggle){
                         switch (showConfirmDialog) {
                        case Both:
                            isOpen = true;
                            break;
                        case NO:
                            isOpen = false;
                            break;
                        case PUSH:
                            isOpen = !booleanValue;
                            break;
                        case RELEASE:
                            isOpen = booleanValue;
                            break;
                        default:
                            break;
                        }
                         if(!isOpen || (isOpen && openConfirmDialog()))
                                 fireManualValueChange(!booleanValue);
                    }
                    else{
                         switch (showConfirmDialog) {
                        case Both:
                        case PUSH:
                        case RELEASE:
                            isOpen = true;
                            break;
                        case NO:
                            isOpen = false;
                            break;
                        default:
                            break;
                        }
                        if(!isOpen || (isOpen && openConfirmDialog())){
                            canceled = false;
                            fireManualValueChange(true);
                            if(isOpen)
                                Display.getCurrent().timerExec(100, new Runnable(){
                                    @Override
                                    public void run() {
                                        fireManualValueChange(false);
                                    }
                                });
                        }else
                            canceled = true;
                    }
                     if(!isOpen || !SWT.getPlatform().startsWith("rap")) //$NON-NLS-1$
                         me.consume();
                    repaint();
                }
            }
            @Override
            public void mouseReleased(MouseEvent me) {
                if (me.button != 1)
                    return;
                if(!toggle && runMode && !canceled){
                    fireManualValueChange(false);
                    me.consume();
                    repaint();
                }
            }
    }

    protected boolean toggle = false;

    protected ShowConfirmDialog showConfirmDialog = ShowConfirmDialog.Both;


    protected String password = "";

    protected String confirmTip = "Are you sure you want to do this?";

    protected boolean runMode = false;

    protected ButtonPresser buttonPresser;
    protected final static Color DISABLE_COLOR = CustomMediaFactory.getInstance().getColor(
            CustomMediaFactory.COLOR_GRAY);

    /** The alpha (0 is transparency and 255 is opaque) for disabled paint */
    protected static final int DISABLED_ALPHA = 100;

    /**
     * Listeners that react on manual boolean value change events.
     */
    private List<IManualValueChangeListener> boolControlListeners =
        new ArrayList<IManualValueChangeListener>();

    public AbstractBoolControlFigure() {
        super();
        buttonPresser = new ButtonPresser();
    }


    /**add a boolean control listener which will be executed when pressed or released
     * @param listener the listener to add
     */
    public void addManualValueChangeListener(final IManualValueChangeListener listener){
        boolControlListeners.add(listener);
    }

    public void removeManualValueChangeListener(final IManualValueChangeListener listener){
        if(boolControlListeners.contains(listener))
            boolControlListeners.remove(listener);
    }

    /**
     * Inform all boolean control listeners, that the manual value has changed.
     *
     * @param newManualValue
     *            the new manual value
     */
    protected void fireManualValueChange(final boolean newManualValue) {

        booleanValue = newManualValue;
        updateValue();
        if(runMode){
            for (IManualValueChangeListener l : boolControlListeners) {
                    l.manualValueChanged(value);
            }
        }
    }
    /**
     * @return the confirmTip
     */
    public String getConfirmTip() {
        return confirmTip;
    }
    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    /**
     * @return the runMode
     */
    public boolean isRunMode() {
        return runMode;
    }
    /**{@link Deprecated} use {@link #getShowConfirmDialog()}
     * @return the showConfirmDialog
     */
    @Deprecated
    public boolean isShowConfirmDialog() {
        return showConfirmDialog != ShowConfirmDialog.NO;
    }

    /**
     * @return the condition when confirm dialog should be shown.
     */
    public ShowConfirmDialog getShowConfirmDialog() {
        return showConfirmDialog;
    }
    /**
     * @return the toggle
     */
    public boolean isToggle() {
        return toggle;
    }

    /**
     * open a confirm dialog.
     *
     * @return false if user canceled, true if user pressed OK or no confirm
     *         dialog needed.
     */
    private boolean openConfirmDialog() {
        // confirm & password input dialog
        if (password == null || password.equals("")) {
            MessageBox mb = new MessageBox(Display.getCurrent()
                    .getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
            mb.setMessage(confirmTip);
            mb.setText("Confirm Dialog");
            int val = mb.open();
            if (val == SWT.YES)
                return true;
        } else {
            InputDialog dlg = new InputDialog(Display.getCurrent()
                    .getActiveShell(), "Password Input Dialog",
                    "Please input the password", "", new IInputValidator() {
                        @Override
                        public String isValid(String newText) {
                            if (newText.equals(password))
                                return null;
                            else
                                return "Password error!";
                        }
                    }) {
                @Override
                protected int getInputTextStyle() {
                    return SWT.SINGLE | SWT.PASSWORD;
                }
            };
            dlg.setBlockOnOpen(true);
            int val = dlg.open();
            if (val == Window.OK)
                return true;
        }
        return false;
    }

    /**
     * @param confirmTip the confirmTip to set
     */
    public void setConfirmTip(String confirmTip) {
        this.confirmTip = confirmTip;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param runMode the runMode to set
     */
    public void setRunMode(boolean runMode) {
        this.runMode = runMode;
    }



    /**Deprecated. Use {@link #setShowConfirmDialog(ShowConfirmDialog)}
     * @param showConfirmDialog the showConfirmDialog to set
     */
    @Deprecated
    public void setShowConfirmDialog(boolean showConfirmDialog) {
        this.showConfirmDialog = ShowConfirmDialog.Both;
    }

    public void setShowConfirmDialog(ShowConfirmDialog showConfirm) {
        this.showConfirmDialog = showConfirm;
    }

    /**
     * @param toggle the toggle to set
     */
    public void setToggle(boolean toggle) {
        this.toggle = toggle;
    }


}
