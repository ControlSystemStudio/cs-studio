/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.navigator.applaunch;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/** Wizard page to configure the new launch config
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LaunchConfigWizardContentPage extends WizardPage
{
	final private static String[] icon_names = new String[]
	{
		"run",
		"console", 
		"edit", 
		"work", 
		"clipboard", 
	};
	private Text command;
	private Button[] icons;
	private Image[] icon_images;
	private Text custom_icon;
	
	public LaunchConfigWizardContentPage()
    {
	    super(Messages.LaunchConfigTitle);
	    setTitle(Messages.LaunchConfigTitle);
	    setDescription(Messages.ConfigureDescr);
    }

	@Override
    public void createControl(final Composite parent)
    {
		final Composite box = new Composite(parent, 0);
		box.setLayout(new GridLayout(2, false));
		
		// Command: __command__
		Label l = new Label(box, 0);
		l.setText(Messages.CommandLbl);
		l.setLayoutData(new GridData());
		
		command = new Text(box, SWT.BORDER);
		command.setLayoutData(new GridData(SWT.FILL, 0, true, false));
		command.setToolTipText(Messages.CommandTT);
		
		// Icon:   (*) run
		//         ( ) edit
		//         ( ) custom
		//         ___custom___
		l = new Label(box, 0);
		l.setText(Messages.IconLbl);
		l.setLayoutData(new GridData(0, SWT.TOP, false, true, 1, icon_names.length+2));
		
		icons = new Button[icon_names.length+1];
		icon_images = new Image[icon_names.length];
		for (int i=0; i<icon_names.length; ++i)
		{
			icons[i] = new Button(box, SWT.RADIO);
			icons[i].setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
			icons[i].setText(icon_names[i]);
			icon_images[i] = LaunchConfig.getBuildinIcon(icon_names[i]);
			icons[i].setImage(icon_images[i]);
		}
		icons[icon_names.length] = new Button(box, SWT.RADIO);
		icons[icon_names.length].setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
		icons[icon_names.length].setText(Messages.CustomIconLbl);

		custom_icon = new Text(box, SWT.BORDER);
		custom_icon.setLayoutData(new GridData(SWT.FILL, 0, true, false));
		custom_icon.setToolTipText(Messages.CustomIconTT);
		
		// Select first icon
		icons[0].setSelection(true);

		// Enable the 'custom_icon' text only when selected
		final SelectionListener enablement = new SelectionAdapter()
		{
			@Override
            public void widgetSelected(SelectionEvent e)
            {
				custom_icon.setEnabled(icons[icon_names.length].getSelection());
            }
		};
		for (Button icon : icons)
			icon.addSelectionListener(enablement);
		enablement.widgetSelected(null);
		
		parent.addDisposeListener(new DisposeListener()
		{
			@Override
			public void widgetDisposed(DisposeEvent e)
			{
				for (Image image : icon_images)
					image.dispose();
				icon_images = null;
			}
		});
		
		// Have to do this, see API of createControl()
		setControl(box);
    }
	
	/** @return Command that the user entered */
	public String getCommand()
	{
		return command.getText().trim();
	}
	
	/** @return Icon that the user selected or entered for custom icon */
    public String getIconName()
	{
		for (int i=0; i<icon_names.length; ++i)
			if (icons[i].getSelection())
				return "icon:" + icon_names[i];
		return custom_icon.getText().trim();
	}
}
