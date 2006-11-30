package org.csstudio.display.pvtable.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** Configuration Dialog.
 *  <p>
 *  Unclear if this has to be that hard:
 *  Is there a better way to bind a 'double' value to a text input?
 *  Is there a better way to handle the verification?
 *  
 *  @author Kay Kasemir
 */
public class ConfigDialog extends Dialog
{
    private String description;
    private double tolerance;
    private double period;
    private Text description_text;
    private Text tolerance_text;
    private Label tolerance_help;
    private Text update_period_text;
    private Label update_period_help;

    public ConfigDialog(Shell parent, String description, double tolerance, double period)
    {
        super(parent);
        this.description = description;
        this.tolerance = tolerance;
        this.period = period;
    }
    
    /** @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell) */
    @Override
    protected void configureShell(Shell newShell)
    {
        super.configureShell(newShell);
        newShell.setText("PV Table Configuration");
    }

    /** @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite) */
    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite composite = (Composite) super.createDialogArea(parent);
        GridLayout gl = (GridLayout)composite.getLayout();
        gl.numColumns = 2;
        GridData gd;

        // Row 1
        Label l = new Label(composite, 0);
        l.setText("Description:");
        gd = new GridData();
        l.setLayoutData(gd);
        
        description_text = new Text(composite, SWT.LEFT);
        description_text.setText(description);
        gd = new GridData();
        gd.minimumWidth = 8*40;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        description_text.setLayoutData(gd);
        
        // Row 2
        l = new Label(composite, 0);
        l.setText("Tolerance:");
        gd = new GridData();
        l.setLayoutData(gd);
        
        tolerance_text = new Text(composite, SWT.LEFT);
        tolerance_text.setText(Double.toString(tolerance));
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        tolerance_text.setLayoutData(gd);

        // Row 3
        tolerance_help = new Label(composite, SWT.LEFT);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        tolerance_help.setLayoutData(gd);

        // Row 4
        l = new Label(composite, 0);
        l.setText("Update Period:");
        gd = new GridData();
        l.setLayoutData(gd);
        
        update_period_text = new Text(composite, SWT.LEFT);
        update_period_text.setText(Double.toString(period));
        
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        update_period_text.setLayoutData(gd);

        // Row 5
        update_period_help = new Label(composite, SWT.LEFT);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        update_period_help.setLayoutData(gd);
        
        ModifyListener validator = new ModifyListener()
        {
            public void modifyText(ModifyEvent event)
            {
                description = description_text.getText();
                // Nothing to check for description
                
                String txt = update_period_text.getText();                
                String help = "Enter update period in seconds";
                try
                {
                    period = Double.parseDouble(txt);
                    if (period > 0.0)
                        help = null;
                    else
                        help = "Enter positive update period in seconds";
                }
                catch (Exception e)
                {
                }
                
                boolean ok = (help == null);
                if (ok)
                    update_period_help.setText("");
                else
                    update_period_help.setText(help);
                
                txt = tolerance_text.getText();
                help = "Enter tolerance";
                try
                {
                    tolerance = Double.parseDouble(txt);
                    if (tolerance > 0.0)
                        help = null;
                    else
                        help = "Enter small, positive tolerance value";
                }
                catch (Exception e)
                {
                }
                
                if (help != null)
                {
                    ok = false;
                    tolerance_help.setText(help);
                }
                else
                    tolerance_help.setText("");
                
                getButton(IDialogConstants.OK_ID).setEnabled(ok);
            }
        };

        description_text.addModifyListener(validator);
        tolerance_text.addModifyListener(validator);
        update_period_text.addModifyListener(validator);
        
        return composite;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public double getTolerance()
    {
        return tolerance;
    }
    
    public double getUpdatePeriod()
    {
        return period;
    }
}
