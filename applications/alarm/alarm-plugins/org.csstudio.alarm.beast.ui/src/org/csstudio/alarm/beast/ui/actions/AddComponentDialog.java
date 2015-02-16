/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.actions;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreeRoot;
import org.csstudio.alarm.beast.ui.Activator;
import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** Dialog that allows addition of an alarm tree component.
 *  For AlarmTreeComponent items, it can add a PV.
 *  For other items, it can add a new AlarmTreeComponent or even
 *  a new AlarmTreeFacility
 *  @author Kay Kasemir
 */
public class AddComponentDialog extends TitleAreaDialog
{
    final private AlarmClientModel model;
    final private AlarmTreeItem parent_item;
    private Button type_facility;
    private Button type_component;
    private Button type_pv;
    private Text name;

    /** Initialize
     *  @param shell Shell
     *  @param model Alarm model
     *  @param parent Parent component
     */
    public AddComponentDialog(final Shell shell, final AlarmClientModel model,
            final AlarmTreeItem parent)
    {
        super(shell);
        this.model = model;
        this.parent_item = parent;
    }

    @Override
    protected boolean isResizable()
    {
        return true;
    }

    /** @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell) */
    @Override
    protected void configureShell(Shell shell)
    {
        super.configureShell(shell);
        shell.setText(Messages.AddComponent);
    }

    /** @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite) */
    @Override
    protected Control createDialogArea(final Composite parent_widget)
    {
        final Composite parent_composite = (Composite) super.createDialogArea(parent_widget);
        // Create box for widgets we're about to add
        final Composite box = new Composite(parent_composite, 0);
        box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        final GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        box.setLayout(layout);
        GridData gd;

        // Set title & image, arrange for disposal of image
        setTitle(Messages.AddComponentTT);
        setMessage(Messages.AddComponentDialog_Guidance);
        final Image title_image =
            Activator.getImageDescriptor("icons/config_image.png").createImage(); //$NON-NLS-1$
        setTitleImage(title_image);
        parent_widget.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
                title_image.dispose();
            }
        });

        // Type: ( ) Facility ( ) Component () PV
        Label l = new Label(box, 0);
        l.setText(Messages.AddComponentDialog_Type);
        l.setLayoutData(new GridData());

        type_facility = new Button(box, SWT.RADIO);
        type_facility.setText(Messages.AlarmArea);
        type_facility.setLayoutData(new GridData());

        type_component = new Button(box, SWT.RADIO);

        // Is the parent of this a top-level element,
        // a "Facility" just above root?
        final boolean haveTopLevelParent =
            parent_item.getParent() instanceof AlarmTreeRoot;
        type_component.setText(Messages.AlarmComponent);
        type_component.setLayoutData(new GridData());

        type_pv = new Button(box, SWT.RADIO);
        type_pv.setText(Messages.AlarmPV);
        type_pv.setLayoutData(new GridData());

        // Force 'area' on first level of hierarchy below 'root'
        final boolean isAtRoot = parent_item instanceof AlarmTreeRoot;
        if (isAtRoot)
        {
            type_facility.setSelection(true);
            type_component.setEnabled(false);
            type_pv.setEnabled(false);
        }
        else if (haveTopLevelParent)
        {   // Assume component
            type_component.setSelection(true);
            // Used to not allow PV on this level, but now that's OK, too
            //type_pv.setEnabled(false);
        }
        else // Assume PV
            type_pv.setSelection(true);

        // Parent: ___ parent ___
        l = new Label(box, 0);
        l.setText(Messages.AddComponentDialog_Parent);
        l.setLayoutData(new GridData());

        final Label parent_path = new Label(box, SWT.BORDER);
        if (isAtRoot)
            parent_path.setText(NLS.bind(Messages.RootElementFMT, parent_item.getPathName()));
        else
            parent_path.setText(parent_item.getPathName());
        gd = new GridData();
        gd.horizontalSpan =layout.numColumns-1;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        parent_path.setLayoutData(gd);

        // Name: ___name__
        l = new Label(box, 0);
        l.setText(Messages.AddComponentDialog_Name);
        l.setLayoutData(new GridData());

        name = new Text(box, SWT.BORDER);
        name.setToolTipText(Messages.AddComponentDialog_NameTT);
        gd = new GridData();
        gd.horizontalSpan =layout.numColumns-1;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        name.setLayoutData(gd);

        // Dynamically update help
        type_facility.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                setMessage(Messages.AddComponentDialog_FacilityTT);
            }
        });
        type_component.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if (haveTopLevelParent)
                    setMessage(NLS.bind(Messages.AddComponentDialog_ComponentTT, parent_item.getName()));
                else
                    setMessage(NLS.bind(Messages.AddComponentDialog_SubComponentTT, parent_item.getName()));
            }
        });
        type_pv.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                setMessage(NLS.bind(Messages.AddComponentDialog_PV_TT, parent_item.getName()));
            }
        });

        name.setFocus();
        
        return parent_composite;
    }

    /** Save user values
     *  @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    protected void okPressed()
    {
        final String name = this.name.getText().trim();
        if (name.length() <= 0)
        {
            setErrorMessage(Messages.AddComponentDialog_EmptyNameError);
            return;
        }
        try
        {
            if (type_facility.getSelection())
                model.addComponent(model.getConfigTree(), name);
            else if (type_component.getSelection())
                model.addComponent(parent_item, name);
            else
                model.addPV(parent_item, name);
        }
        catch (Throwable ex)
        {
            setErrorMessage(NLS.bind(Messages.AddComponentDialog_CannotAddError,
                        new String[]
                        { name, parent_item.getName(), ex.getMessage() }));
            return;
        }
        super.okPressed();
    }
}
