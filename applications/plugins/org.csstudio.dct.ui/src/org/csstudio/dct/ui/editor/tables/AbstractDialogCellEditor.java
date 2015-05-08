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
package org.csstudio.dct.ui.editor.tables;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Base class for dialog based cell editors.
 *
 * @author Sven Wende
 *
 */
public abstract class AbstractDialogCellEditor extends CellEditor {

    private Shell _shell;
    private final String _title;
    private boolean _dialogIsOpen = false;

    /**
     * Constructor.
     *
     * @param parent
     *            the parent composite
     * @param title
     *            the dialog title
     */
    public AbstractDialogCellEditor(final Composite parent, final String title) {
        super(parent, SWT.NONE);
        _shell = parent.getShell();
        _title = title;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void activate() {
        if (!_dialogIsOpen) {
            _dialogIsOpen = true;
            this.openDialog(_shell, _title);
            _dialogIsOpen = false;
        }
    }

    /**
     * Template method. Subclasses should open a dialog within this method.
     *
     * @param shell
     *            a shell
     * @param dialogTitle
     *            the dialog title
     */
    protected abstract void openDialog(final Shell shell, final String dialogTitle);

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Control createControl(final Composite parent) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetFocus() {
        // Ignore
    }

}
