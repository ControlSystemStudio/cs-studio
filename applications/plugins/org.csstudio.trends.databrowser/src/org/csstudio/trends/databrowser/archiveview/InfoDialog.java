package org.csstudio.trends.databrowser.archiveview;

import org.csstudio.archive.ArchiveServer;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** Dialog for displaying archive server info.
 *  @author Kay Kasemir
 */
public class InfoDialog extends Dialog
{
    private final ArchiveServer server;
    
    InfoDialog(Shell shell, ArchiveServer server)
    {
        super(shell);
        this.server = server;
    }
    
    /** Create the UI elements */
    @Override
    protected Control createDialogArea(Composite parent)
    {
        final Composite composite = (Composite) super.createDialogArea(parent);
        
        // We happen to know that it's a GridLayout.
        // Not sure if that'll be guaranteed forever.
        final GridLayout comp_layout = (GridLayout) composite.getLayout();
        
        // Group box for the rest.
        final Group group = new Group(composite, SWT.SHADOW_IN);
        group.setText(Messages.Info_Title);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = comp_layout.numColumns;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessVerticalSpace = true;
        gd.verticalAlignment = SWT.FILL;
        group.setLayoutData(gd);
        
        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        group.setLayout(layout);
        
        // URL: ...
        Label l = new Label(group, 0);
        l.setText(Messages.URL);
        l.setLayoutData(new GridData());
        
        Text t = new Text(group, SWT.READ_ONLY);
        t.setText(server.getURL());
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        t.setLayoutData(gd);

        // Version: ...
        l = new Label(group, 0);
        l.setText(Messages.Info_Version);
        l.setLayoutData(new GridData());
        
        t = new Text(group, SWT.READ_ONLY);
        t.setText(Integer.toString(server.getVersion()));
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        t.setLayoutData(gd);
                
        // Description: ...
        l = new Label(group, 0);
        l.setText(Messages.Info_Description);
        gd = new GridData();
        gd.verticalAlignment = SWT.TOP;
        l.setLayoutData(gd);
        
        t = new Text(group, SWT.READ_ONLY | SWT.WRAP);
        t.setText(server.getDescription());
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessVerticalSpace = true;
        gd.verticalAlignment = SWT.FILL;
        t.setLayoutData(gd);
        
        // Methods:
        l = new Label(group, 0);
        l.setText(Messages.Info_Requests);
        gd = new GridData();
        gd.verticalAlignment = SWT.TOP;
        l.setLayoutData(gd);
        
        List lst = new List(group, 0);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.TOP;
        lst.setLayoutData(gd);
        String requests[] = server.getRequestTypes();
        for (int i = 0; i < requests.length; ++i)
            lst.add(requests[i]);
        
        return composite;
    }
    
    /** Create an OK button.
     *  (default would have been OK and cancel)
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent)
    {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
                     true);
    }
}
