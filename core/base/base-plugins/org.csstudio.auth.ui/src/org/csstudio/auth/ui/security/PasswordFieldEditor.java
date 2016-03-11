package org.csstudio.auth.ui.security;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.auth.security.SecureStorage;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/** StringFieldEditor for Passwords or the like which are stored in
 *  {@link SecureStorage}.
 *  <p>
 *  Corollary: When using the PasswordFieldEditor to edit a preference setting,
 *  you <u>must</u> use {@link SecureStorage} to read the preference setting.
 *  <p>
 *  Copied StringFieldEditor sources, stripped,
 *  replaced Text style with password option.
 *  <p>
 *  On Win32 and OS X the SWT.PASSWORD option will create a text field
 *  that only shows '*' and prohibits cut/copy to read the secret value.
 *  On Linux, however, one can still cut/copy the value out.
 *  To prevent this in all cases, this field editor will only put
 *  a FAKE_PASSWORD into its text box, not the secret preference setting.
 *  <p>
 *  Removed access to the value and text field, so that this
 *  field editor directly reads/writes from preferences or
 *  preferably secure storage.
 *  <p>
 *  In fact when 'encrypted', the field editor never reads
 *  the original value, it will only write a new value in case
 *  one was entered.
 *
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
public class PasswordFieldEditor extends FieldEditor
{
    /**
     * Cached valid state.
     */
    private boolean isValid;

    /**
     * whether the data should be encrypted or not in displaying and storage
     */
    private boolean encrypt = true;

    /**
    * Absolute or relative path to the preference node. It must be unique in the
    * secure storage.
    */
    private String nodePath;

    /**
     * Old text value.
     * @since 3.4 this field is protected.
     */
    protected String oldValue;

    /**
     * The text field, or <code>null</code> if none.
     */
    Text textField;

    /**
     * The error message, or <code>null</code> if none.
     */
    private String errorMessage;

    /**
     * Indicates whether the empty string is legal;
     * <code>true</code> by default.
     */
    private boolean emptyStringAllowed = true;

    /**
     * Fake password to be displayed or copied.
     */
    private static final String FAKE_PASSWORD = "*********"; //$NON-NLS-1$

    private boolean loadFromDefault = false;

    private static final Logger log = Logger.getLogger(PasswordFieldEditor.class.getName());

    /**
     * Creates a string field editor of unlimited width.
     * Use the method <code>setTextLimit</code> to limit the text.
     * The data in this field will be encrypted in displaying and storage.
     *
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     * @param nodePath absolute or relative path to the preference node. It must be unique in the
     *           secure storage. It is recommended to use your plugin ID.
     */
    public PasswordFieldEditor(String name, String labelText, Composite parent, String nodePath)
    {
       this(name, labelText, parent, nodePath, true);
    }

    /**
     * Creates a string field editor of unlimited width.
     * Use the method <code>setTextLimit</code> to limit the text.
     *
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     * @param qualifier absolute or relative path to the preference node. It must be unique
     *           in the secure storage. It must be same as the qualifier of the
     *           preference store of the preference page which including this field editor.
     * @param encrypt true if value is to be encrypted, false value does not need to be encrypted
     */
    public PasswordFieldEditor(String name, String labelText, Composite parent, String qualifier, boolean encrypt)
    {
        init(name, labelText);
        this.encrypt = encrypt;
        this.nodePath = qualifier;
        isValid = false;
        errorMessage = JFaceResources
                .getString("StringFieldEditor.errorMessage");//$NON-NLS-1$
        createControl(parent);
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    @Override
    protected void adjustForNumColumns(int numColumns) {
        GridData gd = (GridData) textField.getLayoutData();
        gd.horizontalSpan = numColumns - 1;
        // We only grab excess space if we have to
        // If another field editor has more columns then
        // we assume it is setting the width.
        gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
    }

    /**
     * Checks whether the text input field contains a valid value or not.
     *
     * @return <code>true</code> if the field value is valid,
     *   and <code>false</code> if invalid
     */
    protected boolean checkState() {
        boolean result = false;
        if (emptyStringAllowed) {
            result = true;
        }

        if (textField == null) {
            result = false;
        }

        String txt = textField.getText();

        result = (txt.trim().length() > 0) || emptyStringAllowed;

        // call hook for subclasses
        result = result && doCheckState();

        if (result) {
            clearErrorMessage();
        } else {
            showErrorMessage(errorMessage);
        }

        return result;
    }

    /**
     * Hook for subclasses to do specific state checks.
     * <p>
     * The default implementation of this framework method does
     * nothing and returns <code>true</code>.  Subclasses should
     * override this method to specific state checks.
     * </p>
     *
     * @return <code>true</code> if the field value is valid,
     *   and <code>false</code> if invalid
     */
    protected boolean doCheckState() {
        return true;
    }

    /**
     * Fills this field editor's basic controls into the given parent.
     * <p>
     * The string field implementation of this <code>FieldEditor</code>
     * framework method contributes the text field. Subclasses may override
     * but must call <code>super.doFillIntoGrid</code>.
     * </p>
     */
    @Override
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        getLabelControl(parent);

        textField = getTextControl(parent);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns - 1;
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        textField.setLayoutData(gd);
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    @SuppressWarnings("nls")
    @Override
    protected void doLoad()
    {
        if (textField == null)
            return;
        String value = null;
        if(!encrypt) {
             try
             {
                value = SecureStorage.getNode(nodePath).get(getPreferenceName(), null);
                if(value == null)
                    value = getPreferenceStore().getString(getPreferenceName());
             }
             catch (Exception e)
             {
                log.log(Level.SEVERE, "Error in retrieving data from secure storage. " +
                        "The default preference value of _" +
                        getPreferenceName()+ "_ will be loaded.", e);
                value = getPreferenceStore().getString(getPreferenceName());
            }
        } else
            value = FAKE_PASSWORD;
        textField.setText(value);
        oldValue = value;
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    @Override
    protected void doLoadDefault() {
        if (textField != null) {
            if(!encrypt){
                String value = getPreferenceStore().getDefaultString(
                        getPreferenceName());
                textField.setText(value);
            } else {
                textField.setText(FAKE_PASSWORD);
                loadFromDefault = true;
            }
        }
        valueChanged();
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    @Override
    protected void doStore()
    {
        final boolean encrypted_pw_changed =
            !textField.getText().equals(FAKE_PASSWORD);

        try
        {
            if (!encrypt || encrypted_pw_changed)
            {
                final ISecurePreferences node = SecureStorage.getNode(nodePath);
                node.put(getPreferenceName(), textField.getText(), encrypt);
                node.flush();
            }
            else if (encrypt && loadFromDefault && !encrypted_pw_changed)
            {
                final ISecurePreferences node = SecureStorage.getNode(nodePath);
                node.put(getPreferenceName(), getPreferenceStore().getDefaultString(
                        getPreferenceName()), encrypt);
                node.flush();
                loadFromDefault = false;
            }
        }
        catch (Exception e)
        {
            log.log(Level.SEVERE, "Error writing data from secure storage.", e);
        }
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    @Override
    public int getNumberOfControls()
    {
        return 2;
    }

    /**
     * Returns this field editor's text control.
     * <p>
     * The control is created if it does not yet exist
     * </p>
     *
     * @param parent the parent
     * @return the text control
     */
    private Text getTextControl(Composite parent) {
        if (textField == null) {
            if(encrypt) {
                textField = new Text(parent, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
            }
            else
                textField = new Text(parent, SWT.SINGLE | SWT.BORDER);
            textField.setFont(parent.getFont());
            // Note: On OS X with Eclipse 3.5, i.e. using Cocoa,
            // the SWT.PASSWORD text box doesn't seem to send out
            // key updates, so updating a 'password changed' flag
            // in the key listener as was done originally no longer works.
            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    valueChanged();
                }
            });
            textField.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent event) {
                    textField = null;
                }
            });
        } else {
            checkParent(textField, parent);
        }
        return textField;
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    @Override
    public boolean isValid() {
        return isValid;
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    @Override
    protected void refreshValidState() {
        isValid = checkState();
    }

    /**
     * Sets whether the empty string is a valid value or not.
     *
     * @param b <code>true</code> if the empty string is allowed,
     *  and <code>false</code> if it is considered invalid
     */
    public void setEmptyStringAllowed(boolean b) {
        emptyStringAllowed = b;
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    @Override
    public void setFocus() {
        if (textField != null) {
            textField.setFocus();
        }
    }

    /**
     * Informs this field editor's listener, if it has one, about a change
     * to the value (<code>VALUE</code> property) provided that the old and
     * new values are different.
     * <p>
     * This hook is <em>not</em> called when the text is initialized
     * (or reset to the default value) from the preference store.
     * </p>
     */
    protected void valueChanged() {
        setPresentsDefaultValue(false);
        boolean oldState = isValid;
        refreshValidState();

        if (isValid != oldState) {
            fireStateChanged(IS_VALID, oldState, isValid);
        }

        String newValue = textField.getText();
        if (!newValue.equals(oldValue)) {
            fireValueChanged(VALUE, oldValue, newValue);
            oldValue = newValue;
        }
    }

    /*
     * @see FieldEditor.setEnabled(boolean,Composite).
     */
    @Override
    public void setEnabled(boolean enabled, Composite parent) {
        super.setEnabled(enabled, parent);
        getTextControl(parent).setEnabled(enabled);
    }

}