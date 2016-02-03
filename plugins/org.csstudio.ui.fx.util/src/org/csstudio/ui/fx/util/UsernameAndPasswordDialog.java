package org.csstudio.ui.fx.util;

import static org.csstudio.ui.fx.util.FXUtilities.setGridConstraints;

import java.util.Optional;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 *
 * <code>UsernameAndPasswordDialog</code> is a dialog which provides means to enter a username and password. It also
 * provides a checkbox in order to hint the receiver whether to remember the credentials for later or not.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class UsernameAndPasswordDialog extends TitleAreaDialog {

    private Button okButton;
    private PasswordField password;
    private TextField username;
    private CheckBox rememberBox;
    private Credentials value;
    private final InputValidator<String> validator = e -> (e == null || e.trim().isEmpty())
            ? "Empty username or password not allowed" : null;
    private final String currentUsername;
    private final String message;
    private final boolean remember;

    /**
     * Constructs a new dialog and sets the predefined username. If no username is provided, system user is used.
     *
     * @param shell the parent shell
     * @param username predefined username
     * @param rememeber preset value for the remember checkbox
     * @param message the message to show in the dialog
     */
    public UsernameAndPasswordDialog(Shell shell, String username, boolean remember, String message) {
        super(shell);
        this.currentUsername = username == null ? System.getProperty("user.name") : username;
        this.message = message;
        this.remember = remember;
        setBlockOnOpen(true);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     */
    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            value = new Credentials(username.getText(), password.getText().toCharArray(), rememberBox.isSelected());
        } else {
            value = null;
        }
        super.buttonPressed(buttonId);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Authentication");
    }

    /**
     * Open the dialog, wait for it to close and then return the value.
     *
     * @return the entered credentials or an empty object if cancel was pressed
     */
    public Optional<Credentials> openAndWat() {
        if (open() == IDialogConstants.OK_ID) {
            return Optional.ofNullable(value);
        } else {
            return Optional.empty();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
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
                int size = FXUtilities.measureStringWidth("Cancel",cancelButton.getFont()) + 25;
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
        validateInput();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(final Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        setTitle("Authentication");
        setMessage(message);
        new FXCanvasMaker() {
            @Override
            protected Scene createFxScene() {
                GridPane pane = new GridPane();
                pane.setPadding(new Insets(10, 10, 10, 10));
                pane.setHgap(3);
                pane.setVgap(5);
                username = new TextField();
                username.setMaxWidth(Double.MAX_VALUE);
                if (currentUsername != null) {
                    username.setText(currentUsername);
                    username.selectAll();
                }
                password = new PasswordField();
                username.setOnAction(e -> password.requestFocus());
                password.setOnAction(e -> {
                    if (!okButton.isDisable()) {
                        buttonPressed(IDialogConstants.OK_ID);
                    }
                });
                password.textProperty().addListener((a, o, n) -> validateInput());
                username.textProperty().addListener((a, o, n) -> validateInput());
                password.setMaxWidth(Double.MAX_VALUE);
                Label uLabel = new Label("Username:");
                Label pLabel = new Label("Password:");
                setGridConstraints(username, true, false, Priority.ALWAYS, Priority.NEVER);
                setGridConstraints(password, true, false, Priority.ALWAYS, Priority.NEVER);
                pane.add(uLabel, 0, 0);
                pane.add(pLabel, 0, 1);
                pane.add(username, 1, 0);
                pane.add(password, 1, 1);
                rememberBox = new UnfocusableCheckBox("Remember password for later use");
                rememberBox.setSelected(remember);
                pane.add(rememberBox, 0, 2, 2, 1);
                pane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                pane.setStyle(FXUtilities.toBackgroundColorStyle(parent.getBackground()));
                pane.setPrefWidth(getInitialSize().x - 20);
                return new Scene(pane);
            }
        }.createPartControl(composite);
        return composite;
    }

    private void validateInput() {
        String s = validator.validate(username.getText());
        if (s == null) {
            s = validator.validate(password.getText());
        }
        if (okButton != null) {
            okButton.setDisable(s != null);
        }
    }
}
