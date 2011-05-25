package org.csstudio.ui.menu.app.handlers;

import java.io.File;
import java.io.IOException;

import org.csstudio.platform.workspace.WorkspaceInfo;
import org.csstudio.ui.menu.app.Messages;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

/** A dialog that prompts for a directory to use as a workspace.
 *  <p>
 *  <b>Code is based upon
 *  <code>org.eclipse.ui.internal.ide.ChooseWorkspaceDialog</code> in plugin
 *  <code>org.eclipse.ui.ide</code>.</b>
 *  </p>
 * @author Alexander Will
 * @author Kay Kasemir
 */
public class WorkspaceDialog extends TitleAreaDialog
{
    /** Workspace information */
    final private WorkspaceInfo info;

    /** Include the "show again" checkbox? */
    final private boolean with_show_again_option;

    /** Combo with selected and recent workspaces */
    private Combo workspaces;

    /** Constructor
     *  @param parent Parent shell or <code>null</code>
     *  @param info WorkspaceInfo
     *  @param with_show_again_option Include the "show again" checkbox?
     */
    public WorkspaceDialog(final Shell parent, final WorkspaceInfo info,
            final boolean with_show_again_option)
    {
        super(parent);
        this.info = info;
        this.with_show_again_option = with_show_again_option;
    }

    /** Fill the 'upper' part of the dialog */
    @Override
    protected Control createDialogArea(final Composite parent)
    {
        String productName = null;
        final IProduct product = Platform.getProduct();
        if (product != null)
            productName = product.getName();
        if (productName == null)
            productName = Messages.Workspace_DefaultProduct;

        // This composite should have GridLayout w/ single column
        final Composite parent_composite = (Composite) super.createDialogArea(parent);
        setTitle(Messages.Workspace_DialogTitle);
        setMessage(NLS.bind(Messages.Workspace_DialogMessage, productName));

        addCustomElements(parent_composite);
        return parent_composite;
    }

    /** Add workspace selector and other GUI elements */
    private void addCustomElements(final Composite parent_composite)
    {
        //  ____workspaces________ [Browse]
        // [x] ask again
        final Composite composite = new Composite(parent_composite, 0);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        composite.setLayout(layout);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        composite.setLayoutData(gd);

        workspaces = new Combo(composite, SWT.DROP_DOWN);
        workspaces.setToolTipText(Messages.Workspace_ComboTT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        workspaces.setLayoutData(gd);
        // Fill w/ current workspace history, select the first one
        for (int i=0; i<info.getWorkspaceCount(); ++i)
            workspaces.add(info.getWorkspace(i));
        workspaces.select(0);

        final Button browse = new Button(composite, SWT.PUSH);
        browse.setText(Messages.Workspace_Browse);
        browse.setToolTipText(Messages.Workspace_BrowseTT);
        browse.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                final DirectoryDialog dialog = new DirectoryDialog(getShell());
                dialog.setText(Messages.Workspace_BrowseDialogTitle);
                dialog.setMessage(Messages.Workspace_BrowseDialogMessage);
                dialog.setFilterPath(getInitialBrowsePath());
                final String dir = dialog.open();
                if (dir != null)
                    workspaces.setText(dir);
            }
        });

        // Pro choice, allow to _not_ show the dialog the next time around?
        if (with_show_again_option)
            createShowDialogButton(composite);
        else // Always show
            info.setShowDialog(true);
    }

    /** Add 'show dialog?' button */
    private void createShowDialogButton(Composite composite)
    {
        final Button show_dialog = new Button(composite, SWT.CHECK);
        show_dialog.setText(Messages.Workspace_AskAgain);
        show_dialog.setToolTipText(Messages.Workspace_AskAgainTT);
        show_dialog.setSelection(true);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.LEFT;
        show_dialog.setLayoutData(gd);
        show_dialog.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                info.setShowDialog(show_dialog.getSelection());
            }
        });
    }

    /** @return Directory name close to the currently entered workspace */
    @SuppressWarnings("nls")
    private String getInitialBrowsePath()
    {
        File dir = new File(workspaces.getText());
        // Go one up
        if (dir != null)
            dir = dir.getParentFile();
        // Go further up until we find something that actually exists
        while ((dir != null) && !dir.exists())
            dir = dir.getParentFile();
        if (dir == null)
            return System.getProperty("user.dir");
        return dir.getAbsolutePath();
    }

    /** Handle "OK" button */
    @Override
    protected void okPressed()
    {
        final String workspace = workspaces.getText().trim();

        // Must not be empty
        if (workspace.length() <= 0)
        {
            setErrorMessage(Messages.Workspace_EmptyError);
            return;
        }

        // Check if this workspace is inside another workspace...
        final File ws_file = new File(workspace);
        try
        {
            File parent = ws_file.getParentFile();
            while (parent != null)
            {   // Is there a .metadata file?
                final File meta = new File(parent.getCanonicalPath()
                        + File.separator + ".metadata"); //$NON-NLS-1$
                if (meta.exists())
                {
                   setErrorMessage(NLS.bind(Messages.Workspace_NestedErrorFMT, parent.getName()));
                   return;
                }
                // OK, go one up
                parent = parent.getParentFile();
            }
        }
        catch (IOException ex)
        {
            setErrorMessage(NLS.bind(Messages.Workspace_Error, ex.getMessage()));
            return;
        }

        // Looks good so far, so report the selected workspace.
        info.setSelectedWorkspace(workspace);
        super.okPressed();
    }
}
