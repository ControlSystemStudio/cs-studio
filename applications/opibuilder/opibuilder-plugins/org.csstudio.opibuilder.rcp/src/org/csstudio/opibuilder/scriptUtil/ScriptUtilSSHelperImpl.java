package org.csstudio.opibuilder.scriptUtil;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.csstudio.opibuilder.actions.SendToElogAction;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.ui.util.dialogs.ResourceSelectionDialog;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SaveAsDialog;

/**Implementation of {@link ScriptUtilSSHelper}
 * @author Xihui Chen
 *
 */
public class ScriptUtilSSHelperImpl extends ScriptUtilSSHelper {

    public static final String ID = "org.csstudio.opibuilder.scriptUtil.ScriptUtilSSHelper";

    @SuppressWarnings("null")
    @Override
    public void writeTextFile(String filePath, boolean inWorkspace,
            AbstractBaseEditPart widget, String text, boolean append)
            throws Exception {

        IPath path = FileUtil.buildAbsolutePath(filePath, widget);
        if(inWorkspace){
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IWorkspaceRoot root = workspace.getRoot();
            String projectName = path.segment(0);
            IProject project = root.getProject(projectName);
            if(!(project.exists())){
                project.create(new NullProgressMonitor());
            }
            project.open(new NullProgressMonitor());
            IFolder folder = null;
            for(int i=1; i<path.segmentCount()-1; i++){
                if(i==1)
                    folder = project.getFolder(path.segment(i));
                else
                    folder = folder.getFolder(path.segment(i));
                if(!(folder.exists())){
                    folder.create(true, true, null);
                }
            }
            IContainer container;
            if(folder == null)
                container = project;
            else
                container = folder;
            IFile file = container.getFile(ResourceUtil.getPathFromString(path.lastSegment()));
            if(file.exists()){
                StringBuilder sb = new StringBuilder();
                if(append){
                    sb.append(FileUtil.readTextFile(filePath, widget));
                }
                sb.append(text);
                file.setContents(
                        new ByteArrayInputStream(sb.toString().getBytes("UTF-8")), true, false, null);      //$NON-NLS-1$
            }else {
                File sysFile = file.getLocation().toFile();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(sysFile, append), "UTF-8")); //$NON-NLS-1$
                writer.write(text);
                writer.flush();
                writer.close();
            }
        }else{
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(path.toString(), append), "UTF-8")); //$NON-NLS-1$
            writer.write(text);
            writer.flush();
            writer.close();
        }
    }

    @Override
    public String openFileDialog(boolean inWorkspace) {
        if(inWorkspace){
            ResourceSelectionDialog rsd = new ResourceSelectionDialog(
                    Display.getCurrent().getActiveShell(), "Select File", new String[]{"*"}); //$NON-NLS-2$
            if (rsd.open() == Window.OK) {
                if (rsd.getSelectedResource() != null) {
                    return rsd.getSelectedResource().toString();
                }
            }
        }else{
            FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
            return dialog.open();
        }

        return null;
    }

    @Override
    public String openFileDialog(String startingFolder) {
        FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
        dialog.setFilterPath(startingFolder);
        return dialog.open();
    }

    @Override
    public String saveFileDialog(boolean inWorkspace){
        if(inWorkspace){
            SaveAsDialog rsd = new SaveAsDialog(
                    Display.getCurrent().getActiveShell()); //$NON-NLS-2$
            if (rsd.open() == Window.OK) {
                if (rsd.getResult() != null) {
                    return rsd.getResult().toOSString();
                }
            }
        }else{
            FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
            return dialog.open();
        }

        return null;
    }

    @Override
    public String saveFileDialog(String startingFolder) {
        FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
        dialog.setFilterPath(startingFolder);
        return dialog.open();
    }

    @Override
    public void makeElogEntry(final String text, final String filePath) {
        Shell shell = Display.getDefault().getActiveShell();
        if(!SendToElogAction.isElogAvailable()){
             MessageDialog.openError(shell, "Error", "No Elog support is available.");
             return;
        }
         // Display dialog, create entry
        try
        {    String systemFilePath = null;
            if (filePath != null) {
                IPath path = ResourceUtil.getPathFromString(filePath);
                try {
                    // try workspace
                    IResource r = ResourcesPlugin.getWorkspace().getRoot()
                            .findMember(path, false);
                    if (r != null && r instanceof IFile) {
                        systemFilePath = ((IFile) r).getLocation().toOSString();
                    } else
                        throw new Exception();
                } catch (Exception e) {
                    systemFilePath = filePath;
                }
            }

            SendToElogAction.makeLogEntry(text, systemFilePath, shell);
        }
        catch (Exception ex)
        {
            MessageDialog.openError(null, "Error", ex.getMessage());
        }
    }

}
