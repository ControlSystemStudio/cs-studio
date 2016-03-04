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
 package org.csstudio.sds.ui.internal.properties;

import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.dialogs.SdsResourceSelectionDialog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * A table cell editor for values of type PointList.
 *
 * @author Kai Meyer
 */
public final class ResourceCellEditor extends AbstractDialogCellEditor {

    /**
     * The current IPath.
     */
    private IPath _path;

    /**
     * The filter path for the dialog.
     */
    private String _filterPath = System.getProperty("user.home"); //$NON-NLS-1$

    /**
     * The accepted file extensions.
     */
    private String[] _fileExtensions;
    /**
     * The original file extensions.
     */
    private String[] _orgFileExtensions;

    private boolean _onlyWorkSpace = true;

    /**
     * The {@link IPropertyChangeListener} for the workspace settings.
     */
    private IPropertyChangeListener _useWorkspaceListener;

    /**
     * Creates a new string cell editor parented under the given control. The
     * cell editor value is a PointList.
     *
     * @param parent
     *            The parent table.
     * @param fileExtensions
     *            The accepted file extensions
     */
    public ResourceCellEditor(final Composite parent,
            final String[] fileExtensions) {
        super(parent, "Open File");
        _orgFileExtensions = fileExtensions;
        _useWorkspaceListener = new UseWorkspaceListener();
        SdsUiPlugin.getCorePreferenceStore().addPropertyChangeListener(
                _useWorkspaceListener);
        convertFileExtensions();
    }

    /**
     * Converts the file extensions. Adds '*.' to every extension if it doesn't
     * start with it
     */
    private void convertFileExtensions() {
        if (_onlyWorkSpace) {
            _fileExtensions = _orgFileExtensions;
        } else {
            if (_orgFileExtensions.length > 0) {
                _fileExtensions = new String[_orgFileExtensions.length];
                for (int i = 0; i < _fileExtensions.length; i++) {
                    if (_orgFileExtensions[i].startsWith("*.")) {
                        _fileExtensions[i] = _orgFileExtensions[i];
                    } else {
                        _fileExtensions[i] = "*." + _orgFileExtensions[i];
                    }
                }
            }
        }
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
        if (_onlyWorkSpace) {
            SdsResourceSelectionDialog rsd = new SdsResourceSelectionDialog(
                    parentShell);
//            rsd.setSelectedResource(_path);
            if (rsd.open() == Window.OK) {
                if (rsd.getSelectedPath() != null) {
                    _path = rsd.getSelectedPath();
                }
            }
        } else {
            FileDialog dialog = new FileDialog(parentShell, SWT.OPEN
                    | SWT.MULTI);
            dialog.setText(dialogTitle);
            if (_path != null) {
                _filterPath = _path.toString();
            }
            dialog.setFilterPath(_filterPath);
            dialog.setFilterExtensions(_fileExtensions);
            dialog.open();
            String name = dialog.getFileName();
            _filterPath = dialog.getFilterPath();
            _path = new Path(_filterPath + Path.SEPARATOR + name);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldFireChanges() {
        return _path != null;
    }

    /**
     * A listener for the preference page.
     *
     * @author Kai Meyer
     *
     */
    private final class UseWorkspaceListener implements
            org.eclipse.jface.util.IPropertyChangeListener {

        /**
         * {@inheritDoc}
         */
        public void propertyChange(final PropertyChangeEvent event) {
            if (event.getProperty().equals("useWorkspaceAsRoot")) {
                if (event.getNewValue() instanceof Boolean) {
                    _onlyWorkSpace = (Boolean) event.getNewValue();
                    convertFileExtensions();
                }
            }
        }
    }

}
