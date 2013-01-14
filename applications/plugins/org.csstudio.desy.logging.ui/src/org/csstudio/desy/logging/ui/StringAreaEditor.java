/* 
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.desy.logging.ui;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringAreaEditor extends FieldEditor {
    
    private static final Logger LOG = LoggerFactory.getLogger(StringAreaEditor.class);    

    public static int UNLIMITED = -1;
    private Text textField;
    private int widthInChars = UNLIMITED;
    private int textLimit = UNLIMITED;
    private String filePath = null;
    private String defaultText = null;
    
    /**
     * Constructor of StringAreaEditor,
     * initializes <code>width</code> with <code>UNLIMITED</code>
     * 
     * @param name			String
     * @param labelText		String
     * @param parent		Composite
     */
    public StringAreaEditor(String name, String labelText, Composite parent, String filePath, String defaultText) {
        init(name, labelText);
        widthInChars = UNLIMITED;
        createControl(parent);
        this.filePath = filePath;
        this.defaultText = defaultText;
    }
    
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
     * Fills this field editor's basic controls into the given parent.
     * <p>
     * The string field implementation of this <code>FieldEditor</code>
     * framework method contributes the text field. Subclasses may override
     * but must call <code>super.doFillIntoGrid</code>.
     * </p>
     */
    @Override
    protected void doFillIntoGrid(Composite parent, int numColumns) {
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
    public void load() {
        textField.setText(readTextFromFile());
    }
    
    @Override
    protected void doLoad() {
        // nothing to do, see load()
    }
    
    private String readTextFromFile() {
        StringBuffer result = new StringBuffer();
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                result.append(line);
                result.append("\n");
                line = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            result.append("\nFile not found\n");
        } catch (IOException e) {
            result.append("\nI/O-Error while reading file\n");
        }
        finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return result.toString();
    }

    /**
     * Sets the default field editor's value.
     */
    @Override
    public void loadDefault() {
        writeTextToFile(defaultText);
        textField.setText(readTextFromFile());
    }
    
    @Override
    protected void doLoadDefault() {
        // nothing to do, see loadDefault()
    }
    
    /**
     * Saves the field editor's value.
     */
    @Override
    public void store() {
        writeTextToFile(textField.getText());
    }
    
    @Override
    protected void doStore() {
        // nothing to do, see store()
    }
    
    private void writeTextToFile(String text) {
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(text);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            LOG.error("Cannot save log4j properties file", e);
        }
    }

    @Override
    public int getNumberOfControls() {
        return 2;
    }
    
    /**
     * Returns the field editor's value.
     *
     * @return the current value
     */
    public String getStringValue() {
        return textField.getText();
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
    public Text getTextControl(Composite parent) {
        if (textField == null) {
            textField = new Text(parent, SWT.MULTI | SWT.BORDER);
            textField.setFont(parent.getFont());
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
    
    @Override
    public boolean isValid() {
        return true;
    }
    
    @Override
    protected void refreshValidState() {
        // nothing to do, always valid
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
        if (textField != null) {
            textField.setFocus();
        }
    }
    
    /**
     * Sets this text field's text limit.
     *
     * @param limit the limit on the number of character in the text
     *  input field, or <code>UNLIMITED</code> for no limit
     */
    public void setTextLimit(int limit) {
        textLimit = limit;
        if (textField != null) {
            textField.setTextLimit(limit);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(boolean enabled, Composite parent) {
        super.setEnabled(enabled, parent);
        getTextControl(parent).setEnabled(enabled);
    }
    
}
