package org.csstudio.ui.fx.util;

import static org.csstudio.ui.fx.util.FXUtilities.setGridConstraints;

import java.util.Optional;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.util.Geometry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 *
 * <code>FXBaseDialog</code> is the Jface base dialog, which provides facilities to add JavaFX components. This dialog
 * should be subclassed when a javaFX style input is required.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 * @param <T> the type of value selectable in this dialog
 */
public abstract class FXBaseDialog<T> extends Dialog implements ControlListener {

    private final String title;
    private final String message;
    protected T value;
    private final InputValidator<T> validator;
    private Text errorMessageText;
    private String errorMessage;
    private boolean allowedToContinue;
    protected Button okButton;

    private Composite parent;

    /**
     * Creates an input dialog with OK and Cancel buttons. Note that the dialog will have no visual representation (no
     * widgets) until it is told to open.
     *
     * @param parentShell the parent shell, or <code>null</code> to create a top-level shell
     * @param dialogTitle the dialog title, or <code>null</code> if none
     * @param dialogMessage the dialog message, or <code>null</code> if none
     * @param initialValue the initial input value, or <code>null</code> if none (equivalent to the empty string)
     * @param validator an input validator, or <code>null</code> if none
     */
    public FXBaseDialog(Shell parentShell, String dialogTitle, String dialogMessage, T initialValue,
        InputValidator<T> validator) {
        super(parentShell);
        this.title = dialogTitle;
        this.message = dialogMessage;
        this.value = initialValue;
        this.validator = validator;
        setBlockOnOpen(true);
    }

    /**
     * Returns the top composite of this dialog.
     *
     * @return the main composite
     */
    public Composite getTopComposite() {
        return parent;
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            value = getValueFromComponent();
        } else {
            value = null;
        }
        super.buttonPressed(buttonId);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (title != null) {
            shell.setText(title);
        }
        shell.addControlListener(this);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        ((GridLayout) parent.getLayout()).numColumns++;
        new FXCanvasMaker() {
            @Override
            protected Scene createFxScene() {

                okButton = new Button(IDialogConstants.OK_LABEL);
                okButton.setOnAction(e -> buttonPressed(IDialogConstants.OK_ID));
                Button cancelButton = new Button(IDialogConstants.CANCEL_LABEL);
                cancelButton.setOnAction(e -> buttonPressed(IDialogConstants.CANCEL_ID));
                int size = FXUtilities.measureStringWidth("Cancel", cancelButton.getFont()) + 25;
                okButton.setPrefWidth(size);
                cancelButton.setPrefWidth(size);

                GridPane pane = new GridPane();
                pane.setHgap(10);
                setGridConstraints(okButton, false, false, HPos.RIGHT, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
                pane.add(okButton, 0, 0);
                pane.add(cancelButton, 1, 0);
                return new Scene(pane);
            }
        }.createPartControl(parent);

        setFocus();
        if (value != null) {
            setValueToComponent(value);
        }
        validateInput();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        this.parent = parent;
        // create composite
        Composite composite = (Composite) super.createDialogArea(parent);
        // create message
        if (message != null) {
            Label label = new Label(composite, SWT.WRAP);
            label.setText(message);
            GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL
                | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            label.setLayoutData(data);
            label.setFont(parent.getFont());
        }
        Composite fxComposite = new Composite(composite, SWT.NONE);
        fxComposite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
        fxComposite.setLayout(new GridLayout());
        new FXCanvasMaker() {
            @Override
            protected Scene createFxScene() {
                return getScene();
            }
        }.createPartControl(fxComposite);

        if (validator != null) {
            errorMessageText = new Text(composite, SWT.READ_ONLY | SWT.WRAP);
            errorMessageText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
            errorMessageText.setBackground(errorMessageText.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            // Set the error message text
            // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=66292
            setErrorMessage(errorMessage, allowedToContinue);
        }

        applyDialogFont(composite);
        return composite;
    }

    protected void setFocus() {

    }

    /**
     * @return the value selected in the fx component
     */
    protected abstract T getValueFromComponent();

    /**
     * @param value set the value to the fx component
     */
    protected abstract void setValueToComponent(T value);

    /**
     * @return the scene containing all components
     */
    protected abstract Scene getScene();

    /**
     * Returns the string typed into this input dialog.
     *
     * @return the input string
     */
    public Optional<T> getValue() {
        return Optional.ofNullable(value);
    }

    /**
     * Opens the dialog and returns the selected item if it exists.
     *
     * @return the selected item
     */
    public Optional<T> openAndWait() {
        if (open() == IDialogConstants.OK_ID) {
            return getValue();
        } else {
            return Optional.empty();
        }
    }

    /**
     * Validates the input.
     * <p>
     * The default implementation of this framework method delegates the request to the supplied input validator object;
     * if it finds the input invalid, the error message is displayed in the dialog's message line. This hook method is
     * called whenever the text changes in the input field.
     * </p>
     */
    protected void validateInput() {
        String errorMessage = null;
        boolean allowedToContinue = false;
        if (validator != null) {
            T value = getValueFromComponent();
            errorMessage = validator.validate(value);
            allowedToContinue = validator.isAllowedToProceed(value);
        }
        setErrorMessage(errorMessage, allowedToContinue);
    }

    /**
     * Sets or clears the error message. If not <code>null</code>, the OK button is disabled.
     *
     * @param errorMessage the error message, or <code>null</code> to clear
     * @param allowedToContinue true if the okButton is enabled even if the error message is non null
     * @since 3.0
     */
    protected void setErrorMessage(String errorMessage, boolean allowedToContinue) {
        this.errorMessage = errorMessage;
        this.allowedToContinue = allowedToContinue;
        if (errorMessageText != null && !errorMessageText.isDisposed()) {
            errorMessageText.setText(errorMessage == null ? " \n " : errorMessage); //$NON-NLS-1$
            boolean hasError = errorMessage != null && (StringConverter.removeWhiteSpaces(errorMessage)).length() > 0;
            errorMessageText.setEnabled(hasError);
            errorMessageText.setVisible(hasError);
            errorMessageText.getParent().update();
            if (okButton != null) {
                okButton.setDisable(errorMessage != null && !allowedToContinue);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.dialogs.Dialog#getInitialLocation(org.eclipse.swt.graphics.Point)
     */
    @Override
    protected Point getInitialLocation(Point initialSize) {
        Composite parent = getShell().getParent();

        Monitor monitor = getShell().getDisplay().getPrimaryMonitor();
        if (parent != null) {
            monitor = parent.getMonitor();
        }

        Rectangle monitorBounds = monitor.getClientArea();
        Point centerPoint;
        if (parent != null) {
            centerPoint = Geometry.centerPoint(parent.getBounds());
        } else {
            centerPoint = Geometry.centerPoint(monitorBounds);
        }

        return new Point(centerPoint.x - (initialSize.x / 2), Math.max(monitorBounds.y,
            Math.min(centerPoint.y - (initialSize.y / 2), monitorBounds.y + monitorBounds.height - initialSize.y)));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.swt.events.ControlListener#controlMoved(org.eclipse.swt.events.ControlEvent)
     */
    @Override
    public void controlMoved(ControlEvent e) {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.swt.events.ControlListener#controlResized(org.eclipse.swt.events.ControlEvent)
     */
    @Override
    public void controlResized(ControlEvent e) {
    }
}
