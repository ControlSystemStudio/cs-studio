/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.domain.common.ui;

//import org.eclipse.core.internal.resources.File;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 11.04.2011
 */
public class WorkspaceDirectoryFieldEditor extends StringButtonFieldEditor {

    /**
     * Initial path for the Browse dialog.
     */
    private IResource _filterPath;
    private final IWorkspace _workspace;

    /**
     * Creates a new directory field editor
     */
    protected WorkspaceDirectoryFieldEditor() {
        _workspace = ResourcesPlugin.getWorkspace();
    }

    /**
     * Creates a directory field editor.
     *
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
    public WorkspaceDirectoryFieldEditor(@Nonnull final String name,
                                         @Nonnull final String labelText,
                                         @Nonnull final Composite parent) {
        _workspace = ResourcesPlugin.getWorkspace();
        init(name, labelText);
        setErrorMessage(JFaceResources
                .getString("DirectoryFieldEditor.errorMessage"));//$NON-NLS-1$
        setChangeButtonText(JFaceResources.getString("openBrowse"));//$NON-NLS-1$
        setValidateStrategy(VALIDATE_ON_FOCUS_LOST);
        createControl(parent);
    }

    /* (non-Javadoc)
     * Method declared on StringButtonFieldEditor.
     * Opens the directory chooser dialog and returns the selected directory.
     */
    @Override
    @CheckForNull
    protected String changePressed() {
        IResource findMember = _workspace.getRoot().findMember(getTextControl().getText());
        if(!findMember.exists()) {
            findMember = null;
        }
      return getDirectory(findMember);
    }

    /* (non-Javadoc)
     * Method declared on StringFieldEditor.
     * Checks whether the text input field contains a valid directory.
     */
    @Override
    protected boolean doCheckState() {
        String fileName = getTextControl().getText();
        fileName = fileName.trim();
        if (fileName.length() == 0 && isEmptyStringAllowed()) {
            return true;
        }
        final IResource findMember = _workspace.getRoot().findMember(fileName);
        if (findMember == null) {
            return false;
        }
        final int type = findMember.getType();

        boolean state = false;
        switch (type) {
            case IResource.PROJECT:
            case IResource.FOLDER:
                state = true;
                break;

            default:
                state = false;
                break;
        }
        return state;
    }

    /**
     * Helper that opens the directory chooser dialog.
     * @param startingDirectory The directory the dialog will open in.
     * @return File File or <code>null</code>.
     *
     */
    @CheckForNull
    private String getDirectory(@CheckForNull final IResource startingDirectory) {

        final ElementTreeSelectionDialog fileDialog = new ElementTreeSelectionDialog(getShell(),
                                                                         new WorkbenchLabelProvider(),
                                                                         new WorkbenchContentProvider());
        if (startingDirectory != null) {
            fileDialog.setInitialSelection(startingDirectory);
        } else if (_filterPath != null) {
            fileDialog.setInitialSelection(_filterPath.getFullPath());
        }
        fileDialog.addFilter(new ViewerFilter() {

            @Override
            public boolean select(@Nonnull final Viewer viewer,
                                  @Nonnull final Object parentElement,
                                  @Nullable final Object element) {
                return  !(element instanceof IFile);
            }
        });
        fileDialog.setInput(_workspace.getRoot());
        final int status = fileDialog.open();
        if (status == Window.OK) {
            final IResource firstResult = (IResource) fileDialog.getFirstResult();
            final IPath fullPath = firstResult.getFullPath();
            return fullPath.toString();
        }

        return null;
    }

    /**
     * Sets the initial path for the Browse dialog.
     * @param path initial path for the Browse dialog
     * @since 3.6
     */
    public void setFilterPath(@Nullable final IResource path) {
        _filterPath = path;
    }

}
