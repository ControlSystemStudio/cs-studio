package org.csstudio.trends.databrowser.archiveview;

import org.csstudio.archive.ArchiveServer;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

public class InfoDialog extends Dialog
{
    private final ArchiveServer server;
    
    InfoDialog(Shell shell, ArchiveServer server)
    {
        super(shell);
        this.server = server;
    }
    
    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite composite = (Composite) super.createDialogArea(parent);
        // We happen to know that it's a GridLayout.
        // Not sure if that'll be guaranteed forever.
        GridLayout layout = (GridLayout) composite.getLayout();
        layout.numColumns = 2;
        
        // Title
        Label l = new Label(composite, SWT.SHADOW_IN);
        l.setText(Messages.Info_Title);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = layout.numColumns;
        gd.horizontalAlignment = SWT.FILL;
        l.setLayoutData(gd);
        
        // URL: ...
        l = new Label(composite, 0);
        l.setText(Messages.URL);
        l.setLayoutData(new GridData());
        
        l = new Label(composite, SWT.BORDER);
        l.setText(server.getURL());
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        l.setLayoutData(gd);

        // Version: ...
        l = new Label(composite, 0);
        l.setText(Messages.Info_Version);
        l.setLayoutData(new GridData());
        
        l = new Label(composite, SWT.BORDER);
        l.setText(Integer.toString(server.getVersion()));
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        l.setLayoutData(gd);
                
        // Description: ...
        l = new Label(composite, 0);
        l.setText(Messages.Info_Description);
        gd = new GridData();
        gd.verticalAlignment = SWT.TOP;
        l.setLayoutData(gd);
        
        l = new Label(composite, SWT.BORDER);
        l.setText(server.getDescription());
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.TOP;
        l.setLayoutData(gd);
        
        // Methods
        l = new Label(composite, 0);
        l.setText(Messages.Info_Requests);
        gd = new GridData();
        gd.verticalAlignment = SWT.TOP;
        l.setLayoutData(gd);
        
        List lst = new List(composite, SWT.BORDER);
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
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        // create OK and Cancel buttons by default
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
                true);
    }

}
