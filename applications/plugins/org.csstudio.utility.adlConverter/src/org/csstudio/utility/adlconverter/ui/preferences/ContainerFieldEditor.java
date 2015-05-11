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
package org.csstudio.utility.adlconverter.ui.preferences;


import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 11.08.2008
 *
 * The class is based on the @link DirectoryFieldEditor
 */

/**
 * A field editor for a directory path type preference. A standard directory
 * dialog appears when the user presses the change button.
 */
public class ContainerFieldEditor extends StringButtonFieldEditor {
    /**
     * Creates a new container field editor.
     */
    protected ContainerFieldEditor() {
    }

    /**
     * Creates a directory field editor.
     *
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
    public ContainerFieldEditor(final String name, final String labelText, final Composite parent) {
        init(name, labelText);
        setEmptyStringAllowed(true);
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
    protected String changePressed() {
        Path path = new Path(getTextControl().getText());
        if (path.isEmpty()) {
            path = null;
        }
        Path d = getDirectory(path);
        if (d == null) {
            return null;
        }
        return d.toPortableString();
    }

    /* (non-Javadoc)
     * Method declared on StringFieldEditor.
     * Checks whether the text input field contains a valid directory.
     */
    protected boolean doCheckState() {
        String fileName = getTextControl().getText();
        fileName = fileName.trim();
        if (fileName.length() == 0 && isEmptyStringAllowed()) {
            return true;
        }
        //TODO: Check for valid Project or Path
//        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
//        Path path = new Path(fileName);
//
//        try {
//            IProject project = root.getProject(fileName);
//        }catch (IllegalArgumentException e) {
//            IFolder file3 = root.getFolder(path);
//            return file3.exists();
//            // TODO: handle exception
//        }
        return true;
    }

    /**
     * Helper that opens the directory chooser dialog.
     * @param startingDirectory The directory the dialog will open in.
     * @return File File or <code>null</code>.
     *
     */
    private Path getDirectory(final Path startingDirectory) {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        //TODO: gibt nicht den gewünschten Container.
        IContainer container;
        if(startingDirectory==null){
            container = root.getContainerForLocation(new Path(""));
        }else{
            container = root.getContainerForLocation(startingDirectory);
        }
        ContainerSelectionDialog csd = new ContainerSelectionDialog(getShell(),container,true,null);
        int buttonId = csd.open();
        if(buttonId==0){
            Object[] result1 = csd.getResult();
            if(result1!=null&&result1.length>0){
                Path path = (Path) result1[0];
                return path;
            }
        }
        return null;
    }
}
