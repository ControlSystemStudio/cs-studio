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


import org.csstudio.ui.util.dialogs.ResourceSelectionDialog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * A cell editor implementation that opens a dialog to let the user choose a
 * resource of the local workspace.
 *
 * @author Sven Wende
 *
 */
public final class WorkspaceResourceCellEditor extends AbstractDialogCellEditor {

    private IPath _path;
    private String[] _fileExtensions;

    /**
     * Constructor.
     *
     * @param parent a parent composite
     * @param fileExtensions accepted file extensions
     * @param title the dialog title
     */
    public WorkspaceResourceCellEditor(final Composite parent, final String[] fileExtensions, String title) {
        super(parent, title);
        _fileExtensions = fileExtensions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doGetValue() {
        return _path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetValue(final Object value) {
        if (value == null || !(value instanceof IPath)) {
            _path = new Path("");
        } else {
            _path = (IPath) value;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void openDialog(final Shell parentShell, final String dialogTitle) {
        ResourceSelectionDialog rsd = new ResourceSelectionDialog(parentShell, dialogTitle, _fileExtensions);

        if (_path != null) {
            rsd.setSelectedResource(_path);
        }

        if (rsd.open() == Window.OK) {
            if (rsd.getSelectedResource() != null) {
                _path = rsd.getSelectedResource();
            }
            fireApplyEditorValue();
        } else {
            fireCancelEditor();
        }
    }
}
