/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.sds.util;

import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.internal.localization.Messages;
import org.csstudio.ui.util.composites.ResourceAndContainerGroup;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * A standard "Save As" dialog which solicits a path from the user. The
 * <code>getResult</code> method returns the path. Note that the folder at the
 * specified path might not exist and might need to be created.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 *
 * <p>
 * <b>Code is based upon <code>org.eclipse.ui.dialogs.SaveAsDialog</code> in
 * plugin <code>org.eclipse.ui.ide</code>.</b>
 * </p>
 *
 * @author Alexander Will
 * @version $Revision$
 *
 */
public final class SaveAsDialog extends TitleAreaDialog {
    /**
     * The key to store the dialog settings.
     */
    private static final String DIALOG_SETTINGS_SECTION = "SaveAsDialogSettings"; //$NON-NLS-1$

    /**
     * The original file.
     */
    private IFile _originalFile = null;

    /**
     * The original file name.
     */
    private String _originalName = null;

    /**
     * The result file.
     */
    private IPath _result;

    /**
     * The container selection group.
     */
    private ResourceAndContainerGroup _resourceGroup;

    /**
     * The OK button.
     */
    private Button _okButton;

    /**
     * The default file extension.
     */
    private String _fileExtension = ""; //$NON-NLS-1$

    /**
     * Creates a new Save As dialog for no specific file.
     *
     * @param parentShell
     *            the parent shell
     */
    public SaveAsDialog(final Shell parentShell) {
        super(parentShell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureShell(final Shell shell) {
        super.configureShell(shell);
        shell.setText(Messages.SaveAsDialog_TITLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createContents(final Composite parent) {

        Control contents = super.createContents(parent);

        initializeControls();
        validatePage();
        _resourceGroup.setFileExtension(_fileExtension);
        _resourceGroup.setFocus();
        setTitle(Messages.SaveAsDialog_TITLE);
        setMessage(Messages.SaveAsDialog_MESSAGE);

        return contents;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createButtonsForButtonBar(final Composite parent) {
        _okButton = createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createDialogArea(final Composite parent) {
        // top level composite
        Composite parentComposite = (Composite) super.createDialogArea(parent);

        // create a composite with standard margins and spacing
        Composite composite = new Composite(parentComposite, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
//        composite.setFont(parentComposite.getFont());

        Listener listener = new Listener() {
            @Override
            public void handleEvent(final Event event) {
                setDialogComplete(validatePage());
            }
        };

        _resourceGroup = new ResourceAndContainerGroup(
                composite,
                listener,
                Messages.SaveAsDialog_FILE_LABEL, Messages.SaveAsDialog_FILE);
        _resourceGroup.setAllowExistingResources(true);

        return parentComposite;
    }

    /**
     * Returns the full path entered by the user.
     * <p>
     * Note that the file and container might not exist and would need to be
     * created. See the <code>IFile.create</code> method and the
     * <code>ContainerGenerator</code> class.
     * </p>
     *
     * @return the path, or <code>null</code> if Cancel was pressed
     */
    public IPath getResult() {
        return _result;
    }

    /**
     * Initializes the controls of this dialog.
     */
    private void initializeControls() {
        if (_originalFile != null) {
            _resourceGroup.setContainerFullPath(_originalFile.getParent()
                    .getFullPath());
            _resourceGroup.setResource(_originalFile.getName());
        } else if (_originalName != null) {
            _resourceGroup.setResource(_originalName);
        }
        setDialogComplete(validatePage());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void okPressed() {
        // Get new path.
        IPath path = _resourceGroup.getContainerFullPath().append(
                _resourceGroup.getResource());

        // If the user does not supply a file extension and if the save
        // as dialog was provided a default file name append the extension
        // of the default filename to the new name
        if (path.getFileExtension() == null) {
            if (_originalFile != null
                    && _originalFile.getFileExtension() != null) {
                path = path.addFileExtension(_originalFile.getFileExtension());
            } else if (_originalName != null) {
                int pos = _originalName.lastIndexOf('.');
                if (++pos > 0 && pos < _originalName.length()) {
                    path = path.addFileExtension(_originalName.substring(pos));
                }
            }
        }

        // If the path already exists then confirm overwrite.
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
        if (file.exists()) {
            String[] buttons = new String[] { IDialogConstants.YES_LABEL,
                    IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL };
            String question = NLS.bind(Messages.SaveAsDialog_OVERWRITE_QUESTION,
                    path.toOSString());
            MessageDialog d = new MessageDialog(getShell(),
                    Messages.SaveAsDialog_QUESTION, null,
                    question, MessageDialog.QUESTION, buttons, 0);
            int overwrite = d.open();
            switch (overwrite) {
            case 0: // Yes
                break;
            case 1: // No
                return;
            case 2: // Cancel
            default:
                cancelPressed();
                return;
            }
        }

        // Store path and close.
        _result = path;
        close();
    }

    /**
     * Sets the completion state of this dialog and adjusts the enable state of
     * the Ok button accordingly.
     *
     * @param value
     *            <code>true</code> if this dialog is compelete, and
     *            <code>false</code> otherwise
     */
    protected void setDialogComplete(final boolean value) {
        _okButton.setEnabled(value);
    }

    /**
     * Sets the original file to use.
     *
     * @param originalFile
     *            the original file
     */
    public void setOriginalFile(final IFile originalFile) {
        _originalFile = originalFile;
    }

    /**
     * Set the original file name to use. Used instead of
     * <code>setOriginalFile</code> when the original resource is not an
     * IFile. Must be called before <code>create</code>.
     *
     * @param originalName
     *            default file name
     */
    public void setOriginalName(final String originalName) {
        _originalName = originalName;
    }

    /**
     * Set the file extension.
     *
     * @param fileExtension
     *            The file extension to set
     */
    public void setFileExtension(final String fileExtension) {
        _fileExtension = fileExtension;
    }

    /**
     * Returns whether this page's visual components all contain valid values.
     *
     * @return <code>true</code> if valid, and <code>false</code> otherwise
     */
    private boolean validatePage() {
        if (!_resourceGroup.areAllValuesValid()) {
            if (!_resourceGroup.getResource().equals("")) { //$NON-NLS-1$
                setErrorMessage(_resourceGroup.getProblemMessage());
            } else {
                setErrorMessage(null);
            }
            return false;
        }

        setErrorMessage(null);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        IDialogSettings settings = SdsUiPlugin.getDefault()
                .getDialogSettings();
        IDialogSettings section = settings.getSection(DIALOG_SETTINGS_SECTION);
        if (section == null) {
            section = settings.addNewSection(DIALOG_SETTINGS_SECTION);
        }
        return section;
    }
}
