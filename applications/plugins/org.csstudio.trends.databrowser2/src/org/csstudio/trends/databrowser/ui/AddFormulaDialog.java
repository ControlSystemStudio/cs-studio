package org.csstudio.trends.databrowser.ui;

import org.csstudio.trends.databrowser.Activator;
import org.csstudio.trends.databrowser.Messages;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** Dialog for creating a new Formula Item: Get name
 *  @author Kay Kasemir
 */
public class AddFormulaDialog  extends TitleAreaDialog
{
    /** Existing names that will be prohibited for the new PV */
    final private String[] existing_names;

    // GUI elements
    private Text txt_name;
    
    /** Entered name */
    private String name = null;
    
    /** Initialize
     *  @param shell Shell
     *  @param existing_names Existing names that will be prohibited for the new PV
     */
    public AddFormulaDialog(final Shell shell, final String existing_names[])
    {
        super(shell);
        this.existing_names = existing_names;
        setShellStyle(getShellStyle() | SWT.RESIZE);
        setHelpAvailable(false);
    }
    
    /** @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell) */
    @Override
    protected void configureShell(Shell shell)
    {
        super.configureShell(shell);
        shell.setText(Messages.AddFormula);
    }
    
    /** @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite) */
    @Override
    protected Control createDialogArea(final Composite parent_widget)
    {
        final Composite parent_composite = (Composite) super.createDialogArea(parent_widget);

        // Title & Image
        setTitle(Messages.AddFormula);
        setMessage(Messages.AddFormulaMsg);
        setTitleImage(Activator.getDefault().getImage("icons/config_image.png")); //$NON-NLS-1$

        // Create box for widgets we're about to add
        final Composite box = new Composite(parent_composite, 0);
        box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        box.setLayout(layout);
        
        // PV Name              : _____________________
        Label l = new Label(box, 0);
        l.setText(Messages.Name);
        l.setLayoutData(new GridData());
        
        txt_name = new Text(box, SWT.BORDER);
        txt_name.setToolTipText(Messages.AddFormula_NameTT);
        txt_name.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        if (name != null)
            txt_name.setText(name);
        
        return parent_composite;
    }
    
    /** Save user values
     *  @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    protected void okPressed()
    {
        // Valid name?
        name = txt_name.getText().trim();
        if (name.length() <= 0)
        {
            setErrorMessage(Messages.EmptyNameError);
            return;
        }
        // Duplicate name?
        for (int i=0; i<existing_names.length; ++i)
            if (existing_names[i].equals(name))
            {
                setErrorMessage(NLS.bind(Messages.DuplicateItemFmt, name));
                return;
            }
        // All OK
        super.okPressed();
    }

    /** Set initial name. Only effective when called before dialog is opened.
     *  @param name Suggested name
     */
    public void setName(final String name)
    {
        this.name = name;
    }

    /** @return Entered PV name */
    public String getName()
    {
        return name;
    }
}
