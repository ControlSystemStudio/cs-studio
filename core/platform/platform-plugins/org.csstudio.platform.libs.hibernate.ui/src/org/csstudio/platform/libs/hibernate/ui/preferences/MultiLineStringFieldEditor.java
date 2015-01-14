/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id$
 */
package org.csstudio.platform.libs.hibernate.ui.preferences;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 24.04.2009
 */
public class MultiLineStringFieldEditor extends StringFieldEditor {
    private Text textField;

    public MultiLineStringFieldEditor(String name, String labelText, Composite parent) {
        super(name, labelText, parent);
    }

    @Override
    public Text getTextControl(Composite parent) {
        textField = getTextControl();
        if (textField == null) {
            // setTextLimit(75);
            textField = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP);
            textField.setFont(parent.getFont());
            textField.addKeyListener(new KeyAdapter() {

                /*
                 * (non-Javadoc)
                 * 
                 * @see
                 * org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt.events.KeyEvent)
                 */
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

    /**
     * Fills this field editor's basic controls into the given parent.
     * <p>
     * The string field implementation of this <code>FieldEditor</code> framework method contributes
     * the text field. Subclasses may override but must call <code>super.doFillIntoGrid</code>.
     * </p>
     */
    @Override
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        super.doFillIntoGrid(parent, numColumns);
//        getLabelControl(parent);
//
//        textField = getTextControl(parent);
//        GridData gd = new GridData();
//        gd.horizontalSpan = numColumns - 1;
//        GC gc = new GC(textField);
//        try {
//            Point extent = gc.textExtent("X");//$NON-NLS-1$
//            gd.widthHint = 75 * extent.x;
//        } finally {
//            gc.dispose();
//        }
//        // } else {
//        // gd.horizontalAlignment = GridData.FILL;
//        // gd.grabExcessHorizontalSpace = true;
//        // }
//        textField.setLayoutData(gd);
    }

}
