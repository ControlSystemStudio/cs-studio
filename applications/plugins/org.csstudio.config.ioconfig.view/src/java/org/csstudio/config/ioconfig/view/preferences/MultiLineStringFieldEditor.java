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
 * $Id: MultiLineStringFieldEditor.java,v 1.1 2009/08/26 07:09:27 hrickens Exp $
 */
package org.csstudio.config.ioconfig.view.preferences;

import javax.annotation.Nonnull;

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
 * @author $Author: hrickens $
 * @version $Revision: 1.1 $
 * @since 24.04.2009
 */
public class MultiLineStringFieldEditor extends StringFieldEditor {
    private Text _textField;
    
    public MultiLineStringFieldEditor(@Nonnull final String name, @Nonnull final String labelText, @Nonnull final Composite parent) {
        super(name, labelText, parent);
    }
    
    @Override
    @Nonnull
    public Text getTextControl(@Nonnull final Composite parent) {
        _textField = getTextControl();
        if (_textField == null) {
            // setTextLimit(75);
            _textField = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP);
            _textField.setFont(parent.getFont());
            _textField.addKeyListener(new KeyAdapter() {
                
                @SuppressWarnings("synthetic-access")
                @Override
                public void keyReleased(@Nonnull final KeyEvent e) {
                    valueChanged();
                }
            });
            
            _textField.addDisposeListener(new DisposeListener() {
                @SuppressWarnings("synthetic-access")
                @Override
                public void widgetDisposed(@Nonnull final DisposeEvent event) {
                    _textField = null;
                }
            });
        } else {
            checkParent(_textField, parent);
        }
        return _textField;
    }
    
}
