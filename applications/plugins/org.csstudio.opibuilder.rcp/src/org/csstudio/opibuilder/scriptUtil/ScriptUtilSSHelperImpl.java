package org.csstudio.opibuilder.scriptUtil;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.csstudio.apputil.ui.elog.ElogDialog;
import org.csstudio.logbook.ILogbook;
import org.csstudio.opibuilder.actions.SendToElogAction;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**Implementation of {@link ScriptUtilSSHelper}
 * @author Xihui Chen
 *
 */
public class ScriptUtilSSHelperImpl extends ScriptUtilSSHelper {

	public static final String ID = "org.csstudio.opibuilder.scriptUtil.ScriptUtilSSHelper";

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
	    				new ByteArrayInputStream(sb.toString().getBytes("UTF-8")), true, false, null);	  //$NON-NLS-1$
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
	public void makeElogEntry(String filePath) {
	    final Shell shell = Display.getCurrent().getActiveShell();
		if(!SendToElogAction.isElogAvailable()){
			 MessageDialog.openError(shell, "Error", "No Elog support is available.");
			 return;
		}
		 // Display dialog, create entry
        try
        {	String systemFilePath;
	            IPath path = ResourceUtil.getPathFromString(filePath);
	            try {
		            // try workspace
		  			IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(
		     					path, false);
		        	if (r!= null && r instanceof IFile) {
		            		systemFilePath = ((IFile)r).getLocation().toOSString();
		            }else
		            	throw new Exception();
	           	} catch (Exception e) {
	            	systemFilePath = filePath;
	        }
	        final String finalfilePath = systemFilePath;
            final ElogDialog dialog =
                new ElogDialog(shell, "Send To Logbook",
                        "Elog Entry from BOY",
                        "See attached image",
                        finalfilePath)
            {
                @Override
                public void makeElogEntry(final String logbook_name, final String user,
                        final String password, final String title, final String body, final String images[])
                        throws Exception
                {
                    final Job create = new Job("Creating log entry.")
                    {
						@Override
						protected IStatus run(final IProgressMonitor monitor)
						{
							try
							{
							    final ILogbook logbook = getLogbook_factory()
							            .connect(logbook_name, user, password);
								logbook.createEntry(title, body, images);
								logbook.close();
							}
							catch (final Exception ex)
							{
                                shell.getDisplay().asyncExec(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        ExceptionDetailsErrorDialog.openError(shell, "Error", ex);
                                    }
                                });
							}
							return Status.OK_STATUS;
						}
					};
					create.setUser(true);
					create.schedule();
                }
            };
            dialog.open();
        }
        catch (Exception ex)
        {
            MessageDialog.openError(null, "Error", ex.getMessage());
        }
	}

}
