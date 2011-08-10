
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.ams;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class StringAreaEditor extends FieldEditor
{
    public static final int VALIDATE_ON_KEY_STROKE = 0;
    public static final int VALIDATE_ON_FOCUS_LOST = 1;
    public static int UNLIMITED = -1;
    private boolean isValid;
    private String oldValue;
    Text textField;
    private int widthInChars = UNLIMITED;
    private int textLimit = UNLIMITED;
    private String errorMessage;
    private boolean emptyStringAllowed = true;
    private int validateStrategy = VALIDATE_ON_KEY_STROKE;
    
    protected StringAreaEditor() {
        // Nothing to do
    }
    
    /**
     * Constructor of StringAreaEditor.
     * 
     * @param name			String
     * @param labelText		String
     * @param width			int
     * @param strategy		int
     * @param parent		Composite
     */
    public StringAreaEditor(String name, String labelText, int width,
            int strategy, Composite parent)
    {
        init(name, labelText);
        widthInChars = width;
        setValidateStrategy(strategy);
        isValid = false;
        errorMessage = JFaceResources
                .getString("StringFieldEditor.errorMessage");//$NON-NLS-1$
        createControl(parent);
    }
    
    /**
     * Constructor of StringAreaEditor,
     * initializes <code>strategy</code>
     * with <code>VALIDATE_ON_KEY_STROKE</code>.
     * 
     * @param name			String
     * @param labelText		String
     * @param width			int
     * @param parent		Composite
     * @see #StringAreaEditor(String, String, int, int, Composite)
     */
    public StringAreaEditor(String name, String labelText, int width,
            Composite parent)
    {
        this(name, labelText, width, VALIDATE_ON_KEY_STROKE, parent);
    }
    
    /**
     * Constructor of StringAreaEditor,
     * initializes <code>width</code> with <code>UNLIMITED</code> and
     * initializes <code>strategy</code>
     * with <code>VALIDATE_ON_KEY_STROKE</code>.
     * 
     * @param name			String
     * @param labelText		String
     * @param parent		Composite
     * @see #StringAreaEditor(String, String, int, Composite)
     */
    public StringAreaEditor(String name, String labelText, Composite parent)
    {
        this(name, labelText, UNLIMITED, parent);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void adjustForNumColumns(int numColumns)
    {
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
    protected boolean checkState()
    {
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
    protected void doFillIntoGrid(Composite parent, int numColumns)
    {
    	Label myLabel = getLabelControl(parent);
        GridData gd = new GridData();
        gd.verticalAlignment = SWT.TOP;
        myLabel.setLayoutData(gd);

        textField = getTextControl(parent);
        gd = new GridData();
        gd.horizontalSpan = numColumns - 1;
        if (widthInChars != UNLIMITED) {
            GC gc = new GC(textField);
            try {
                Point extent = gc.textExtent("X");//$NON-NLS-1$
                gd.widthHint = widthInChars * extent.x;
            } finally {
                gc.dispose();
            }
        } else {
            gd.horizontalAlignment = GridData.FILL;
            gd.grabExcessHorizontalSpace = true;
        }
        textField.setLayoutData(gd);
    }
    
    /**
     * Sets the field editor's value.
     */
    @Override
    protected void doLoad()
    {
        if (textField != null) {
            String value = getPreferenceStore().getString(getPreferenceName());
            textField.setText(value);
            oldValue = value;
        }
    }
    
    /**
     * Sets the default field editor's value.
     */
    @Override
    protected void doLoadDefault()
    {
        if (textField != null) {
            String value = getPreferenceStore().getDefaultString(
                    getPreferenceName());
            textField.setText(value);
        }
        valueChanged();
    }
    
    /**
     * Saves the field editor's value.
     */
    @Override
    protected void doStore() {
        getPreferenceStore().setValue(getPreferenceName(), textField.getText());
    }
    
    /**
     * Returns the error message that will be displayed when and if 
     * an error occurs.
     *
     * @return the error message, or <code>null</code> if none
     */
    public String getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfControls() {
        return 2;
    }
    
    /**
     * Returns the field editor's value.
     *
     * @return the current value
     */
    public String getStringValue()
    {
        if (textField != null) {
			return textField.getText();
		}
        
        return getPreferenceStore().getString(getPreferenceName());
    }
    
    /**
     * Returns this field editor's text control.
     *
     * @return the text control, or <code>null</code> if no
     * text field is created yet
     */
    protected Text getTextControl() {
        return textField;
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
    public Text getTextControl(Composite parent)
    {
        if (textField == null) 
        {
            textField = new Text(parent, SWT.MULTI | SWT.BORDER);
            textField.setFont(parent.getFont());
            switch (validateStrategy) {
            case VALIDATE_ON_KEY_STROKE:
                textField.addKeyListener(new KeyAdapter() {

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public void keyReleased(KeyEvent e) {
                        valueChanged();
                    }
                });

                break;
            case VALIDATE_ON_FOCUS_LOST:
                textField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        clearErrorMessage();
                    }
                });
                textField.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        refreshValidState();
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        valueChanged();
                        clearErrorMessage();
                    }
                });
                break;
            default:
                Assert.isTrue(false, "Unknown validate strategy");//$NON-NLS-1$
            }
            textField.addDisposeListener(new DisposeListener() {
                @Override
                public void widgetDisposed(DisposeEvent event) {
                    textField = null;
                }
            });
            if (textLimit > 0) {//Only set limits above 0 - see SWT spec
                textField.setTextLimit(textLimit);
            }
        } else {
            checkParent(textField, parent);
        }
        return textField;
    }
    
    /**
     * Returns whether an empty string is a valid value.
     *
     * @return <code>true</code> if an empty string is a valid value, and
     *  <code>false</code> if an empty string is invalid
     * @see #setEmptyStringAllowed
     */
    public boolean isEmptyStringAllowed() {
        return emptyStringAllowed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() {
        return isValid;
    }

    /**
     * {@inheritDoc}
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
    
    /**
     * Sets the error message that will be displayed when and if 
     * an error occurs.
     *
     * @param message the error message
     */
    public void setErrorMessage(String message) {
        errorMessage = message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus()
    {
        if (textField != null) {
            textField.setFocus();
        }
    }
    
    /**
     * Sets this field editor's value.
     *
     * @param value the new value, or <code>null</code> meaning the empty string
     */
    public void setStringValue(String value)
    {
        if (textField != null) {
            if (value == null) {
				value = "";//$NON-NLS-1$
			}
            oldValue = textField.getText();
            if (!oldValue.equals(value)) {
                textField.setText(value);
                valueChanged();
            }
        }
    }
    
    /**
     * Sets this text field's text limit.
     *
     * @param limit the limit on the number of character in the text
     *  input field, or <code>UNLIMITED</code> for no limit
     */
    public void setTextLimit(int limit)
    {
        textLimit = limit;
        if (textField != null) {
			textField.setTextLimit(limit);
		}
    }
    
    /**
     * Sets the strategy for validating the text.
     * <p>
     * Calling this method has no effect after <code>createPartControl</code>
     * is called. Thus this method is really only useful for subclasses to call
     * in their constructor. However, it has public visibility for backward 
     * compatibility.
     * </p>
     *
     * @param value either <code>VALIDATE_ON_KEY_STROKE</code> to perform
     *  on the fly checking (the default), or <code>VALIDATE_ON_FOCUS_LOST</code> to
     *  perform validation only after the text has been typed in
     */
    public void setValidateStrategy(int value)
    {
        Assert.isTrue(value == VALIDATE_ON_FOCUS_LOST
                || value == VALIDATE_ON_KEY_STROKE);
        validateStrategy = value;
    }
    
    /**
     * Shows the error message set via <code>setErrorMessage</code>.
     */
    public void showErrorMessage() {
        showErrorMessage(errorMessage);
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
    protected void valueChanged()
    {
        setPresentsDefaultValue(false);
        boolean oldState = isValid;
        refreshValidState();

        if (isValid != oldState) {
			fireStateChanged(IS_VALID, oldState, isValid);
		}

        String newValue = textField.getText();
        if (!newValue.equals(oldValue))
        {
            fireValueChanged(VALUE, oldValue, newValue);
            oldValue = newValue;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(boolean enabled, Composite parent)
    {
        super.setEnabled(enabled, parent);
        getTextControl(parent).setEnabled(enabled);
    }
}
