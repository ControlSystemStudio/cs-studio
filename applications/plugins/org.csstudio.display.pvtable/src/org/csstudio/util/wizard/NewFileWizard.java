package org.csstudio.util.wizard;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.csstudio.display.pvtable.Plugin;
import org.csstudio.platform.ui.workbench.FileEditorInput;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;

// Originally created with the PDE wizard

/** File/New wizard for new XML files.
 *  <p>
 *  The default content passed to the constructor
 *  is added to the newly created file.
 *  It needs to be such that the workbench
 *  recognizes the file type and opens the correct editor.
 *
 *  @author Kay Kasemir
 */
public class NewFileWizard extends Wizard implements INewWizard
{
    private final String plugin_id;
    private final String editor_ID;
    private final String title, default_filename, default_content;
    
    private NewFileWizardPage page;

    private ISelection selection;

    /** Constructor
     *  @param title The title and "Create new ... file" text piece.
     *  @param default_contents Initial XML content for new files.
     */
    public NewFileWizard(Plugin plugin, String editor_ID, String title,
            String default_filename, String default_content)
    {
        super();
        plugin_id = plugin.getBundle().getSymbolicName();
        this.editor_ID = editor_ID;
        this.title = title;
        this.default_filename = default_filename;
        this.default_content = default_content;
        setNeedsProgressMonitor(true);
    }

    /** Remember selection from workbench to see if we can initialize from it.
     *  @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
        this.selection = selection;
    }
    
    /** Adding the page to the wizard. */
    public void addPages()
    {
        page = new NewFileWizardPage(title, default_filename, selection);
        addPage(page);
    }

    /** Called when 'Finish' button is pressed in the wizard. We
     *  will create an operation and run it using wizard as execution context.
     */
    public boolean performFinish()
    {
        final String containerName = page.getContainerName();
        final String fileName = page.getFileName();
        IRunnableWithProgress op = new IRunnableWithProgress()
        {
            public void run(IProgressMonitor monitor)
                    throws InvocationTargetException
            {
                try
                {
                    doFinish(containerName, fileName, monitor);
                }
                catch (CoreException e)
                {
                    throw new InvocationTargetException(e);
                }
                finally
                {
                    monitor.done();
                }
            }
        };
        try
        {
            getContainer().run(true, false, op);
        }
        catch (InterruptedException e)
        {
            return false;
        }
        catch (InvocationTargetException e)
        {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), Messages.Error, realException
                    .getMessage());
            return false;
        }
        return true;
    }

    /** The worker method. It will find the container, create the file if missing
     *  or just replace its contents, and open the editor on the newly created
     *  file.
     */
    private void doFinish(String containerName, String fileName,
            IProgressMonitor monitor) throws CoreException
    {
        // create a sample file
        monitor.beginTask(Messages.Creating___ + fileName, 2);
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IResource resource = root.findMember(new Path(containerName));
        if (!resource.exists() || !(resource instanceof IContainer))
        {
            throwCoreException(Messages.ContainerNotFound + containerName);
        }
        IContainer container = (IContainer) resource;
        final IFile file = container.getFile(new Path(fileName));
        try
        {
            InputStream stream =
                new ByteArrayInputStream(default_content.getBytes());
            if (file.exists())
                file.setContents(stream, true, true, monitor);
            else
                file.create(stream, true, monitor);
            stream.close();
        }
        catch (IOException e)
        {
        }
        monitor.worked(1);
        monitor.setTaskName(Messages.OpeningFile___);
        getShell().getDisplay().asyncExec(new Runnable()
        {
            public void run()
            {
                try
                {
                    IWorkbenchPage page = PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getActivePage();

                    // This works, but adds an IDE dependency
                    //IDE.openEditor(page, file, true);

                    // Code based on email from Alexander Will,
                    // replacing the IDE dependency IDE.openEditor(...)
                    // Instead of the correct editor based on
                    // cregistered content types,
                    // it always gets a Text Editor.
                    /*
                    IEditorInput editorInput = new FileEditorInput(file);
                    IEditorRegistry editorRegistry =
                            PlatformUI.getWorkbench().getEditorRegistry();
                    IEditorDescriptor descriptor =
                            editorRegistry.getDefaultEditor(file.getName());
                    if (descriptor == null || editorInput == null)
                        throw new Exception(Messages.NoEditorFound);
                    page.openEditor(editorInput, descriptor.getId());
                    */
                    IEditorInput editorInput = new FileEditorInput(file);
                    page.openEditor(editorInput, editor_ID, true);
                }
                catch (Exception e)
                {
                    Plugin.logException(Messages.CannotOpenEditor, e);
                }
            }
        });
        monitor.worked(1);
    }

    private void throwCoreException(String message) throws CoreException
    {
        IStatus status = new Status(IStatus.ERROR, plugin_id,
                IStatus.OK, message, null);
        throw new CoreException(status);
    }
}
