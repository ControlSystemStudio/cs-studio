package table_editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import table_gui.TableGui;

/** Action connected to workbench menu action set for showing the view.
 *  @@author Kay Kasemir
 */
public class ShowTableAction implements IWorkbenchWindowActionDelegate
{
   public void init(IWorkbenchWindow window)
   { /* NOP */ }

   public void selectionChanged(IAction action, ISelection selection)
    { /* NOP */ }

   public void run(IAction action)
   {
           IWorkbench workbench = PlatformUI.getWorkbench();
           IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
           try {
              IWorkbenchPage page = window.getActivePage();
              
              if (page != null) {
                 IEditorPart activeEditor = page.getActiveEditor();
                 Shell shell = window.getShell();
                 FileDialog dlg = new FileDialog(shell, SWT.OPEN);
                 dlg.setFilterExtensions (new String [] {"*.pace"}); 
                 IWorkspace workspace = ResourcesPlugin.getWorkspace();

                 IProject[] ps = workspace.getRoot().getProjects();
                 IProject p = ps[0];
                 IPath spath = p.getFullPath();
                 spath = p.getFullPath().makeAbsolute();
                 IPath location = p.getLocation();
           
                 try
                 {
                 String path = location.toString() + "/config/";
                 System.out.println(path);
                 dlg.setFilterPath (path); 
                 dlg.setFileName ("rccs.pace");
                 String file = dlg.open();
                 TableGui tg = new TableGui();
                 tg.setFilename(file);
                 IFile input = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(Path.fromOSString(file));
         
                 activeEditor = IDE.openEditor(page, input);
                 // restore the previously active editor to active
                 //    state
                 if (activeEditor != null) {
                    page.activate(activeEditor);
                 }

       }
       catch (Exception e)
       {
           e.printStackTrace();
       }
              }
           }     
              catch (Exception e)
              {
                  e.printStackTrace();
              }
   }

   public void dispose()
    { /* NOP */ }
}
