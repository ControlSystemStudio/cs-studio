package org.csstudio.sds.components.ui.internal.figures;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.components.ui.internal.editparts.IBoolControlListener;
import org.csstudio.ui.util.CustomMediaFactory;
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

    protected boolean toggle = false;

    protected boolean showConfirmDialog = false;

    protected String password = "";

    protected String confirmTip = "Are you sure you want to do this?";

    protected boolean runMode = false;

    protected ButtonPresser buttonPresser;

    protected final static Color DISABLE_COLOR = CustomMediaFactory.getInstance().getColor(
            CustomMediaFactory.COLOR_GRAY);
    /** The alpha (0 is transparency and 255 is opaque) for disabled paint */
    protected static final int DISABLED_ALPHA = 100;

    public AbstractBoolControlFigure() {
        super();
        buttonPresser = new ButtonPresser();
    }

    class ButtonPresser extends MouseListener.Stub {
        private boolean canceled = false;
            @Override
            public void mousePressed(MouseEvent me) {
                if (me.button != 1)
                    return;
                if(runMode){
                     if(toggle){
                        if(openConfirmDialog())
                            fireManualValueChange(!boolValue);
                    }
                    else{
                        if(openConfirmDialog()){
                            canceled = false;
                            fireManualValueChange(true);
                            if(showConfirmDialog)
                                Display.getCurrent().timerExec(100, new Runnable(){
                                    @Override
                                    public void run() {
                                        fireManualValueChange(false);
                                    }
                                });
                        }else
                            canceled = true;
                    }
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

    /**
     * Listeners that react on manual boolean value change events.
     */
    private List<IBoolControlListener> boolControlListeners =
        new ArrayList<IBoolControlListener>();


    /**add a boolean control listener which will be executed when pressed or released
     * @param listener the listener to add
     */
    public void addBoolControlListener(final IBoolControlListener listener){
        boolControlListeners.add(listener);
    }
    /**
     * @param toggle the toggle to set
     */
    public void setToggle(boolean toggle) {
        this.toggle = toggle;
    }

    /**
     * @param showConfirmDialog the showConfirmDialog to set
     */
    public void setShowConfirmDialog(boolean showConfirmDialog) {
        this.showConfirmDialog = showConfirmDialog;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param confirmTip the confirmTip to set
     */
    public void setConfirmTip(String confirmTip) {
        this.confirmTip = confirmTip;
    }

    /**
     * @param runMode the runMode to set
     */
    public void setRunMode(boolean runMode) {
        this.runMode = runMode;
    }

    /**
     * Inform all boolean control listeners, that the manual value has changed.
     *
     * @param newManualValue
     *            the new manual value
     */
    protected void fireManualValueChange(final boolean newManualValue) {

        boolValue = newManualValue;
        updateValue();
        if(runMode){
            for (IBoolControlListener l : boolControlListeners) {
                    l.valueChanged(boolValue);
            }
        }
    }

    /**open a confirm dialog.
     * @return false if user canceled, true if user pressed OK.
     */
    private boolean openConfirmDialog() {
        //confirm & password input dialog
        if(showConfirmDialog && runMode){
            if(password == null || password.equals("")){
                MessageBox mb = new MessageBox(Display.getCurrent().getActiveShell(),
                        SWT.ICON_QUESTION | SWT.YES | SWT.NO);
                mb.setMessage(confirmTip);
                mb.setText("Confirm Dialog");
                int val = mb.open();
                if(val == SWT.NO)
                    return false;
            }else {
                InputDialog  dlg = new InputDialog(Display.getCurrent().getActiveShell(),
                        "Password Input Dialog", "Please input the password", "",
                        new IInputValidator(){
                            @Override
                            public String isValid(String newText) {
                                if (newText.equals(password))
                                    return null;
                                else
                                    return "Password error!";
                            }
                        }){@Override
                        protected int getInputTextStyle() {
                            return SWT.SINGLE | SWT.PASSWORD;
                        }};
                dlg.setBlockOnOpen(true);
                int val = dlg.open();
                if(val == Window.CANCEL)
                    return false;
            }
        }
        return true;
    }


}
