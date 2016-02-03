/*******************************************************************************
 * Copyright (c) 2000, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.csstudio.ui.fx.util;

import static org.csstudio.ui.fx.util.FXUtilities.setGridConstraints;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;

/**
 * This class is a copy of the {@link MessageDialog}, where the SWT buttons are replaced with FX buttons. The static
 * methods of this class can be called from anywhere and will be delegated to the UI thread and block the calling thread
 * while the dialog is open.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class FXMessageDialog extends IconAndMessageDialog {

    /**
     * <code>DialogType</code> represents the possible dialog types, which define the image displayed in the dialog.
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     */
    public static enum DialogType {
        NONE, ERROR, INFORMATION, QUESTION, WARNING, CONFIRM, QUESTION_WITH_CANCEL
    }

    /**
     * Labels for buttons in the button bar (localized strings).
     */
    private final String[] buttonLabels;

    /**
     * Index into <code>buttonLabels</code> of the default button.
     */
    private final int defaultButtonIndex;

    /**
     * Dialog title (a localized string).
     */
    private final String title;

    /**
     * Dialog title image.
     */
    private final Image titleImage;

    /**
     * Image, or <code>null</code> if none.
     */
    private final Image image;

    /**
     * The custom dialog area.
     */
    private Control customArea;

    private Button[] buttons;
    private int buttonWidth = 0;

    /**
     * Create a message dialog. Note that the dialog will have no visual representation (no widgets) until it is told to
     * open.
     * <p>
     * The labels of the buttons to appear in the button bar are supplied in this constructor as an array. The
     * <code>open</code> method will return the index of the label in this array corresponding to the button that was
     * pressed to close the dialog.
     * </p>
     * <p>
     * <strong>Note:</strong> If the dialog was dismissed without pressing a button (ESC key, close box, etc.) then
     * {@link SWT#DEFAULT} is returned. Note that the <code>open</code> method blocks.
     * </p>
     *
     * @param parentShell the parent shell, or <code>null</code> to create a top-level shell
     * @param dialogTitle the dialog title, or <code>null</code> if none
     * @param dialogTitleImage the dialog title image, or <code>null</code> if none
     * @param dialogMessage the dialog message
     * @param dialogType one of the possible dialog types
     * @param dialogButtonLabels an array of labels for the buttons in the button bar
     * @param defaultIndex the index in the button label array of the default button
     */
    public FXMessageDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage,
        DialogType dialogType, String[] dialogButtonLabels, int defaultIndex) {
        super(parentShell);
        this.title = dialogTitle;
        this.titleImage = dialogTitleImage;
        this.message = dialogMessage;
        Font font = new Button().getFont();
        for (String s : dialogButtonLabels) {
            this.buttonWidth = Math.max(FXUtilities.measureStringWidth(s, font), this.buttonWidth);
        }
        this.buttonWidth += 25;

        switch (dialogType) {
            case ERROR:
                this.image = getErrorImage();
                break;
            case INFORMATION:
                this.image = getInfoImage();
                break;
            case QUESTION:
            case QUESTION_WITH_CANCEL:
            case CONFIRM:
                this.image = getQuestionImage();
                break;
            case WARNING:
                this.image = getWarningImage();
                break;
            case NONE:
            default:
                this.image = null;
                break;
        }
        this.buttonLabels = dialogButtonLabels;
        this.defaultButtonIndex = defaultIndex;
    }

    @Override
    protected void buttonPressed(int buttonId) {
        setReturnCode(buttonId);
        close();
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (title != null) {
            shell.setText(title);
        }
        if (titleImage != null) {
            shell.setImage(titleImage);
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        ((GridLayout) parent.getLayout()).numColumns++;
        new FXCanvasMaker() {
            @Override
            protected Scene createFxScene() {
                GridPane pane = new GridPane();
                pane.setHgap(10);
                buttons = new Button[buttonLabels.length];
                for (int i = 0; i < buttonLabels.length; i++) {
                    buttons[i] = new Button(buttonLabels[i]);
                    buttons[i].setPrefWidth(buttonWidth);
                    final int k = i;
                    buttons[i].setOnAction(e -> buttonPressed(k));
                    if (i == 0) {
                        setGridConstraints(buttons[i], false, false, HPos.RIGHT, VPos.CENTER, Priority.ALWAYS,
                            Priority.NEVER);
                    }
                    pane.add(buttons[i], i, 0);
                }
                return new Scene(pane);
            }
        }.createPartControl(parent);
        buttons[defaultButtonIndex].requestFocus();
        buttons[defaultButtonIndex].setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                buttonPressed(defaultButtonIndex);
            }
        });
    }

    /**
     * Creates and returns the contents of an area of the dialog which appears below the message and above the button
     * bar.
     * <p>
     * The default implementation of this framework method returns <code>null</code>. Subclasses may override.
     * </p>
     *
     * @param parent parent composite to contain the custom area
     * @return the custom area control, or <code>null</code>
     */
    protected Control createCustomArea(Composite parent) {
        return null;
    }

    /**
     * This implementation of the <code>Dialog</code> framework method creates and lays out a composite and calls
     * <code>createMessageArea</code> and <code>createCustomArea</code> to populate it. Subclasses should override
     * <code>createCustomArea</code> to add contents below the message.
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        // create message area
        createMessageArea(parent);
        // create the top level composite for the dialog area
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan = 2;
        composite.setLayoutData(data);
        // allow subclasses to add custom controls
        customArea = createCustomArea(composite);
        // If it is null create a dummy label for spacing purposes
        if (customArea == null) {
            customArea = new Label(composite, SWT.NULL);
        }
        return composite;
    }

    /**
     * Handle the shell close. Set the return code to <code>SWT.DEFAULT</code> as there has been no explicit close by
     * the user.
     *
     * @see org.eclipse.jface.window.Window#handleShellCloseEvent()
     */
    @Override
    protected void handleShellCloseEvent() {
        // Sets a return code of SWT.DEFAULT since none of the dialog buttons
        // were pressed to close the dialog.
        super.handleShellCloseEvent();
        setReturnCode(SWT.DEFAULT);
    }

    /**
     * Convenience method to open a simple dialog as specified by the <code>kind</code> flag.
     *
     * @param kind the kind of dialog to open, one of {@link #ERROR}, {@link #INFORMATION}, {@link #QUESTION},
     *            {@link #WARNING}, {@link #CONFIRM}, or {@link #QUESTION_WITH_CANCEL}.
     * @param parent the parent shell of the dialog, or <code>null</code> if none
     * @param title the dialog's title, or <code>null</code> if none
     * @param message the message
     * @param style {@link SWT#NONE} for a default dialog, or {@link SWT#SHEET} for a dialog with sheet behavior
     * @return <code>true</code> if the user presses the OK or Yes button, <code>false</code> otherwise
     * @since 3.5
     */
    public static boolean open(final DialogType kind, final Shell parent, final String title, final String message,
        final int style) {
        final int[] ans = new int[] { -1 };
        parent.getDisplay().syncExec(() -> {
            FXMessageDialog dialog = new FXMessageDialog(parent, title, null, message, kind, getButtonLabels(kind), 0);
            dialog.setShellStyle(dialog.getShellStyle() | (style & SWT.SHEET));
            ans[0] = dialog.open();
        });
        return ans[0] == 0;
    }

    private static String[] getButtonLabels(DialogType kind) {
        String[] dialogButtonLabels;
        switch (kind) {
            case ERROR:
            case INFORMATION:
            case WARNING:
                dialogButtonLabels = new String[] { trim(IDialogConstants.OK_LABEL) };
                break;
            case CONFIRM:
                dialogButtonLabels = new String[] { trim(IDialogConstants.OK_LABEL),
                    trim(IDialogConstants.CANCEL_LABEL) };
                break;
            case QUESTION:
                dialogButtonLabels = new String[] { trim(IDialogConstants.YES_LABEL), trim(IDialogConstants.NO_LABEL) };
                break;
            case QUESTION_WITH_CANCEL:
                dialogButtonLabels = new String[] { trim(IDialogConstants.YES_LABEL), trim(IDialogConstants.NO_LABEL),
                    trim(IDialogConstants.CANCEL_LABEL) };
                break;
            default:
                throw new IllegalArgumentException("Illegal value for kind in MessageDialog.open()"); //$NON-NLS-1$
        }
        return dialogButtonLabels;
    }

    private static String trim(String val) {
        return val.replace("&", "");
    }

    /**
     * Convenience method to open a Yes/No/Cancel dialog.
     *
     * @param parent the parent shell of the dialog, or <code>null</code> if none
     * @param title the dialog's title, or <code>null</code> if none
     * @param message the message
     * @return <code>true</code> if the user presses the OK button, <code>false</code> otherwise
     */
    public static int openYesNoCancel(Shell parent, String title, String message) {
        FXMessageDialog dialog = new FXMessageDialog(parent, title, null, message, DialogType.QUESTION_WITH_CANCEL,
            getButtonLabels(DialogType.QUESTION_WITH_CANCEL), 0);
        return dialog.open();
    }

    /**
     * Convenience method to open a simple confirm (OK/Cancel) dialog.
     *
     * @param parent the parent shell of the dialog, or <code>null</code> if none
     * @param title the dialog's title, or <code>null</code> if none
     * @param message the message
     * @return <code>true</code> if the user presses the OK button, <code>false</code> otherwise
     */
    public static boolean openConfirm(Shell parent, String title, String message) {
        return open(DialogType.CONFIRM, parent, title, message, SWT.NONE);
    }

    /**
     * Convenience method to open a standard error dialog.
     *
     * @param parent the parent shell of the dialog, or <code>null</code> if none
     * @param title the dialog's title, or <code>null</code> if none
     * @param message the message
     */
    public static void openError(Shell parent, String title, String message) {
        open(DialogType.ERROR, parent, title, message, SWT.NONE);
    }

    /**
     * Convenience method to open a standard information dialog.
     *
     * @param parent the parent shell of the dialog, or <code>null</code> if none
     * @param title the dialog's title, or <code>null</code> if none
     * @param message the message
     */
    public static void openInformation(Shell parent, String title, String message) {
        open(DialogType.INFORMATION, parent, title, message, SWT.NONE);
    }

    /**
     * Convenience method to open a simple Yes/No question dialog.
     *
     * @param parent the parent shell of the dialog, or <code>null</code> if none
     * @param title the dialog's title, or <code>null</code> if none
     * @param message the message
     * @return <code>true</code> if the user presses the Yes button, <code>false</code> otherwise
     */
    public static boolean openQuestion(Shell parent, String title, String message) {
        return open(DialogType.QUESTION, parent, title, message, SWT.NONE);
    }

    /**
     * Convenience method to open a standard warning dialog.
     *
     * @param parent the parent shell of the dialog, or <code>null</code> if none
     * @param title the dialog's title, or <code>null</code> if none
     * @param message the message
     */
    public static void openWarning(Shell parent, String title, String message) {
        open(DialogType.WARNING, parent, title, message, SWT.NONE);
    }

    /**
     * Return whether or not we should apply the workaround where we take focus for the default button or if that should
     * be determined by the dialog. By default only return true if the custom area is a label or CLabel that cannot take
     * focus.
     *
     * @return boolean
     */
    protected boolean customShouldTakeFocus() {
        if (customArea instanceof Label) {
            return false;
        }
        if (customArea instanceof CLabel) {
            return (customArea.getStyle() & SWT.NO_FOCUS) != 0;
        }
        return true;
    }

    @Override
    public Image getImage() {
        return image;
    }

    /**
     * An accessor for the index of the default button in the button array.
     *
     * @return The default button index.
     */
    protected int getDefaultButtonIndex() {
        return defaultButtonIndex;
    }
}
