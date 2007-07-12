package org.csstudio.workspace.ui;

import java.io.File;
import java.io.FileFilter;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** Workspace dialog that allows workspace selection limited to a
 *  given root directory.
 *  Also checks if a selected workspace is already in use,
 *  or creates a new workspace if it doesn't yet exist.
 *  
 *  <p>
 *  Refer to
 *  <code>org.eclipse.ui.internal.ide.actions.OpenWorkspaceAction</code> in
 *  plugin <code>org.eclipse.ui.ide</code> for more on this topic.
 *
 *  @author Kay Kasemir
 */
public class ChooseWorkspaceDialog  extends TitleAreaDialog 
{
    final String application_name;
    final String root_directory;

    private Combo workspace;
    private Label info;
    
    /** Constructor
     *  @param parent parent shell
     *  @param application_name App name, used to construct some messages
     *  @param root_directory Root directory of all workspaces.
     */
    public ChooseWorkspaceDialog(final Shell parent,
                                 final String application_name,
                                 final String root_directory)
    {
        super(parent);
        this.application_name = application_name;
        this.root_directory = root_directory;
    }
    
    /** Set the window title */
    @Override
    protected void configureShell(final Shell shell)
    {
        super.configureShell(shell);
        shell.setText(NLS.bind("{0} Workspace", application_name));
    }
    
    /** {@inheritDoc} */
    @Override
    protected Control createDialogArea(Composite parent)
    {
        final Composite composite = (Composite) super.createDialogArea(parent);
        setTitle("Select Workspace");
        // two lines!
        setMessage(NLS.bind("{0} stores your projects and their configuration in a \"workspace\" folder.\nPlease select an unused workspace for this instance of {0}, or create a new workspace.",
                            application_name));
        
        createBrowseArea(composite);

        Dialog.applyDialogFont(composite);
        
        fillWorkspaceCombo();
        
        return composite;
    }

    /** Create the guts of the dialog UI */
    private void createBrowseArea(Composite parent)
    {
        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        
        final Composite box = new Composite(parent, 0);
        box.setLayout(layout);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        box.setLayoutData(gd);
        
        // Root:      ______________
        // Workspace: __combo_______
        //
        // -------------------------
        // ___Info__________________
        Label l = new Label(box, 0);
        l.setText("Root:");
        l.setLayoutData(new GridData());
        
        Text t = new Text(box, SWT.BORDER);
        t.setText(root_directory);
        t.setToolTipText("All workspaces are under this root directory");
        t.setEnabled(false);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        t.setLayoutData(gd);
        
        // New Row
        l = new Label(box, 0);
        l.setText("Workspace:");
        l.setLayoutData(new GridData());

        workspace = new Combo(box, SWT.DROP_DOWN | SWT.SINGLE);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        workspace.setLayoutData(gd);
        
        // New Row
        l = new Label(box, SWT.SEPARATOR | SWT.HORIZONTAL);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessVerticalSpace = true;
        gd.verticalAlignment = SWT.BOTTOM;
        l.setLayoutData(gd);
        
        // New Row
        info = new Label(box, 0);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        info.setLayoutData(gd);
        info.setText("Please select...");        
    }

    /** Fill workspace combo box with available workspaces. */
    private void fillWorkspaceCombo()
    {
        final File root = new File(root_directory);
        if (!root.isDirectory())
        {
            setErrorMessage("Internal error: Workspace root is not a directory");
            return;
        }
        final File subdirs[] = root.listFiles(new FileFilter()
        {
            public boolean accept(File pathname)
            {
                return pathname.isDirectory() &&
                       ! pathname.getName().startsWith(".");
            }
        });
        for (File dir : subdirs)
        {
            workspace.add(dir.getName());
        }
    }

    private int fake = 0;

    
    private boolean checkWorkspace()
    {
        final String name = workspace.getText();
        if (name.length() <= 0)
        {
            setErrorMessage("No workspace selected");
            return false;
        }
        if (name.indexOf('/') >= 0  ||  name.indexOf('\\') > 0)
        {
            setErrorMessage("Workspace name should not contain path separators");
            return false;
        }

        // TODO Is workspace in use?
        if (fake == 0)
        {
            setErrorMessage(NLS.bind("Workspace is in use by another {0} instance, please select different one",
                                     application_name));
            fake = 1;
            return false;
        }

        setErrorMessage(null);

        // TODO Check for new workspace
        if (fake == 1)
        {
            if (!MessageDialog.openQuestion(this.getShell(),
                            "New Workspace?",
                            "This will create a new workspace.\nWhich is fine, I'm just asking to make sure you indeed wanted a new workspace."))
                return false;
        }
        
        return true;
    }
    
    /** When OK is pressed, check if the workspace is in use,
     *  or prompt to create new workspace.
     */
    @Override
    protected void okPressed()
    {
        if (checkWorkspace())
            super.okPressed();
    }
    
    
    
}
