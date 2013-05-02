/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.ui;

import java.util.HashMap;

import org.csstudio.trends.sscan.Activator;
import org.csstudio.trends.sscan.Messages;
import org.csstudio.trends.sscan.preferences.Preferences;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** Dialog for creating a new PV or Formula Item: Get name, axis.
 *  For PV, also scan period.
 *  
 *  @author Kay Kasemir
 *  @author Takashi Nakamoto changed AddPVDialog to update the error/warning message
 *                           dynamically as the user changes any value in this
 *                           dialog message, and to accept an existing name for
 *                           the name of newly created PV item.
 */
public class AddPVDialog  extends TitleAreaDialog
{
    /** Existing names */
    final private HashMap<String, Object> existing_names;

    /** Value axis names */
    final private String[] axes;
    
    /** Add formula, not PV? */
    final private boolean formula;
    
    // GUI elements
    private Text txt_name;
    private Text txt_period;
    private Button btn_monitor;
    private Combo axis;
    
    /** Entered name */
    private String name = null;
    
    /** Entered period */
    private double period;

    /** Selected Axis index or -1 */
    private int axis_index = -1;

    /** Initialize
     *  @param shell Shell
     *  @param existing_names Existing names that will be prohibited for the new PV
     *  @param axes Value axis names
     *  @param formula Add formula, not PV?
     */
    public AddPVDialog(final Shell shell, final String existing_names[],
            final String axes[], final boolean formula)
    {
        super(shell);

        // Push the existing names into the hash map so that it becomes
        // fast to judge whether the user input name already exists.
        this.existing_names = new HashMap<String, Object>(existing_names.length);
        for (String existing_name: existing_names)
        	this.existing_names.put(existing_name, null);

        this.axes = axes;
        this.formula = formula;
        setShellStyle(getShellStyle() | SWT.RESIZE);
        setHelpAvailable(false);
    }
    
    /** @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell) */
    @Override
    protected void configureShell(Shell shell)
    {
        super.configureShell(shell);
        shell.setText(formula ? Messages.AddFormula : Messages.AddPV);
    }
    
    /** @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite) */
    @Override
    protected Control createDialogArea(final Composite parent_widget)
    {
        final Composite parent_composite = (Composite) super.createDialogArea(parent_widget);

        // Title & Image
        setTitle(formula ? Messages.AddFormula : Messages.AddPV);
        setMessage(formula ? Messages.AddFormulaMsg : Messages.AddPVMsg);
        setTitleImage(Activator.getDefault().getImage("icons/config_image.png")); //$NON-NLS-1$

        // Create box for widgets we're about to add
        final Composite box = new Composite(parent_composite, 0);
        box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        final GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        box.setLayout(layout);
        
        // PV Name              : _____________________
        Label l = new Label(box, 0);
        l.setText(Messages.Name);
        l.setLayoutData(new GridData());
        
        txt_name = new Text(box, SWT.BORDER);
        txt_name.setToolTipText(formula ? Messages.AddFormula_NameTT : Messages.AddPV_NameTT);
        txt_name.setLayoutData(new GridData(SWT.FILL, 0, true, false, layout.numColumns-1, 1));
        txt_name.addModifyListener(new ModifyListener()
        {
			@Override
			public void modifyText(ModifyEvent e) {
		        updateAndValidate();
			}
        });
        if (name != null)
            txt_name.setText(name);
        
        if (! formula)
        {
            // Scan Period [seconds]: _____   [x] on change
            l = new Label(box, 0);
            l.setText(Messages.AddPV_Period);
            l.setLayoutData(new GridData());
    
            txt_period = new Text(box, SWT.BORDER);
            txt_period.setToolTipText(Messages.AddPV_PeriodTT);
            txt_period.setLayoutData(new GridData(SWT.FILL, 0, true, false));
            txt_period.addModifyListener(new ModifyListener()
            {
				@Override
				public void modifyText(ModifyEvent e) {
					updateAndValidate();
				}
            });
    
            btn_monitor = new Button(box, SWT.CHECK);
            btn_monitor.setText(Messages.AddPV_OnChange);
            btn_monitor.setToolTipText(Messages.AddPV_OnChangeTT);
            btn_monitor.setLayoutData(new GridData());
    
            
            // In 'monitor' mode, the period entry is disabled
            btn_monitor.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    txt_period.setEnabled(!btn_monitor.getSelection());
                    updateAndValidate();
                }
            });
        }
        
        // Value Axis:            _____
        // If there are axes to select, add related GUI
        if (axes.length > 0)
        {
            l = new Label(box, 0);
            l.setText(Messages.AddPV_Axis);
            l.setLayoutData(new GridData());
    
            axis = new Combo(box, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.SINGLE);
            axis.setToolTipText(Messages.AddPV_AxisTT);
            axis.setLayoutData(new GridData(SWT.FILL, 0, true, false));
            // First entry is 'new axis', rest actual axis names
            axis.add(Messages.AddPV_NewOrEmptyAxis);
            for (String name : axes)
                axis.add(name);
            axis.select(0);

            // Empty label to fill last column
            l = new Label(box, 0);
            l.setLayoutData(new GridData());
        }
        
        return parent_composite;
    }
    
    /** Save user values
     *  @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    protected void okPressed()
    {
    	if (!updateAndValidate())
    		return;
        
        super.okPressed();
    }

    /** Set initial name. Only effective when called before dialog is opened.
     *  @param name Suggested name
     */
    public synchronized void setName(final String name)
    {
        this.name = name;
    }

    /** @return Entered PV name */
    public synchronized String getName()
    {
        return name;
    }

    /** @return Entered scan period in seconds */
    public synchronized double getScanPeriod()
    {
        return period;
    }

    /** @return Index of Value Axis or -1 for 'create new' */
    public synchronized int getAxisIndex()
    {
        return axis_index;
    }
    
    /** Update the internal variables according to the user input and
     *  validate them.
     *  This method also updates the message shown in this dialog.
     * @return True if all the values input by the user are valid. Otherwise, false.
     */
    private synchronized boolean updateAndValidate()
    {
        // Valid name?
    	name = txt_name.getText().trim();
        if (name.length() <= 0)
        {
            setMessage(Messages.EmptyNameError, IMessageProvider.ERROR);
            return false;
        }

		// Valid scan period?
		if (!formula && !btn_monitor.getSelection())
		{
			if (btn_monitor.getSelection())
				period = 0.0;
			else
			{
				try
				{
					period = Double.parseDouble(txt_period.getText().trim());
					if (period < 0)
						throw new Exception();
				}
				catch (Throwable ex)
				{
					setMessage(Messages.InvalidScanPeriodError,
							IMessageProvider.ERROR);
					return false;
				}
			}
		}
		
		// update axis_index internally
        if (axis == null   ||   axis.getSelectionIndex() <= 0)
            axis_index = -1;
        else // entry 0 is 'no axis'
            axis_index = axis.getSelectionIndex() - 1;
		
        // Now that Model accepts multiple items with the same name,
        // there is no need to prohibit from adding a new item with the
        // existing name, but this dialog just warns that the model has
        // at least one item with the given name.
        if (existing_names.containsKey(name))
        {
            setMessage(NLS.bind(Messages.DuplicateItemFmt, name),
            		IMessageProvider.WARNING);
            return true;
        }
        
        // All OK
        setMessage(formula ? Messages.AddFormulaMsg : Messages.AddPVMsg);
        return true;
    }
}
