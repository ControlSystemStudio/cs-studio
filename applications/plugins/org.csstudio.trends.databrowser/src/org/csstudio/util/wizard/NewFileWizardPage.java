package org.csstudio.util.wizard;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ResourceSelectionDialog;

/** The "New" wizard page allows setting the container for the new file as well
 *  as the file name. The page will only accept file name without the extension
 *  OR with the extension that matches the expected one (xml).
 *  
 *  @author Kay Kasemir
 */
public class NewFileWizardPage extends WizardPage
{
    private Text containerText;

    private Text fileText;

    private ISelection selection;

    private String default_filename;

    /**
     * Constructor for SampleNewWizardPage.
     * 
     * @param title Used for the title and "Create a new ... file"
     * @param pageName
     */
    public NewFileWizardPage(String title, String default_filename, 
            ISelection selection)
    {
        super("wizardPage"); //$NON-NLS-1$
        setTitle(title);
        setDescription(Messages.CreateNew___ + title + Messages.___TypeFile);
        this.default_filename = default_filename;
        this.selection = selection;
    }

    /** @see IDialogPage#createControl(Composite) */
    public void createControl(Composite parent)
    {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 3;
        layout.verticalSpacing = 9;
        Label label = new Label(container, SWT.NULL);
        label.setText(Messages.Folder);

        ModifyListener runDialogChanged = new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                dialogChanged();
            }
        };
        
        containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        containerText.setLayoutData(gd);
        containerText.addModifyListener(runDialogChanged);

        Button button;
        button = new Button(container, SWT.PUSH);
        button.setText(Messages.Browse);
        button.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                handleBrowse();
            }
        });

        label = new Label(container, SWT.NULL);
        label.setText(Messages.Filename);

        fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        fileText.setLayoutData(gd);
        fileText.addModifyListener(runDialogChanged);

        button = new Button(container, SWT.PUSH);
        button.setText(Messages.Open);
        button.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                handleOpen();
            }
        });
        
        initialize();
        dialogChanged();
        setControl(container);
    }

    /** Tests if the current workbench selection is a suitable container to use. */
    private void initialize()
    {
        if (selection != null && selection.isEmpty() == false
                && selection instanceof IStructuredSelection)
        {
            IStructuredSelection ssel = (IStructuredSelection) selection;
            if (ssel.size() > 1)
                return;
            Object obj = ssel.getFirstElement();
            if (obj instanceof IResource)
            {
                IContainer container;
                if (obj instanceof IContainer)
                    container = (IContainer) obj;
                else
                    container = ((IResource) obj).getParent();
                containerText.setText(container.getFullPath().toString());
            }
        }
        fileText.setText(default_filename);
    }

    /** Uses the standard container selection dialog to choose the new value for
     *  the container field.
     */
    private void handleBrowse()
    {
        ContainerSelectionDialog dialog = new ContainerSelectionDialog(
                getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
                Messages.SelectFolder);
        if (dialog.open() != ContainerSelectionDialog.OK)
            return;
        Object[] result = dialog.getResult();
        if (result.length == 1)
        {
            containerText.setText(((Path) result[0]).toString());
        }
    }

    /** Get an existing file name. */
    private void handleOpen()
    {
        // TODO: Use FileDialog?
        // The problem with that:
        // How to restrict it to the files in the workspace,
        // or how to handle files outside of the workspace.
        // The ResourceSelectionDialog sucks usage-wise, but at least
        // it keeps the selection to files inside the workspace.
        ResourceSelectionDialog dialog
            = new ResourceSelectionDialog(getShell(),
                    ResourcesPlugin.getWorkspace().getRoot(),
                    Messages.SelectFile);
            if (dialog.open() != ResourceSelectionDialog.OK)
            return;
        Object results[] = dialog.getResult();
        if (results.length != 1)
            return;
        if (! (results[0] instanceof IFile))
            return;
        // Update container & file name with the selected file info.
        IPath path = ((IFile) results[0]).getFullPath();
        containerText.setText(path.removeLastSegments(1).toOSString());
        fileText.setText(path.segment(path.segmentCount()-1));
    }
    
    /** Ensures that both text fields are set. */
    private void dialogChanged()
    {
        IResource container = ResourcesPlugin.getWorkspace().getRoot()
                .findMember(new Path(getContainerName()));
        String fileName = getFileName();

        if (getContainerName().length() == 0)
        {
            updateStatus(Messages.NoFolderSelected);
            return;
        }
        if (container == null
                || (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0)
        {
            updateStatus(Messages.FolderNotFound);
            return;
        }
        if (!container.isAccessible())
        {
            updateStatus(Messages.ReadonlyProject);
            return;
        }
        if (fileName.length() == 0)
        {
            updateStatus(Messages.NoFilenameSelected);
            return;
        }
        if (fileName.replace('\\', '/').indexOf('/', 1) > 0)
        {
            updateStatus(Messages.InvalidFilename);
            return;
        }
        int dotLoc = fileName.lastIndexOf('.');
        if (dotLoc != -1)
        {
            String ext = fileName.substring(dotLoc + 1);
            if (ext.equalsIgnoreCase("xml") == false) //$NON-NLS-1$
            {
                updateStatus(Messages.NeedXML);
                return;
            }
        }
        updateStatus(null);
    }

    private void updateStatus(String message)
    {
        setErrorMessage(message);
        setPageComplete(message == null);
    }

    public String getContainerName()
    {
        return containerText.getText();
    }

    public String getFileName()
    {
        return fileText.getText();
    }
}
