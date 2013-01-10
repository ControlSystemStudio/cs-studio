/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.navigator.applaunch;

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

/** UI for editnig a LaunchConfig
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LaunchConfigUI
{
	final private static String[] icon_names = new String[]
	{
		"run",
		"text", 
		"console", 
		"edit", 
		"work", 
		"clipboard", 
	};

	final private LaunchConfig config;

	private Text command;
	private Button[] icons;
	private Image[] icon_images;
	private Text custom_icon;
	
	public LaunchConfigUI(final LaunchConfig config)
	{
		this.config = config;
	}
	
    public Composite createControl(final Composite parent)
    {
		final Composite box = new Composite(parent, 0);
		box.setLayout(new GridLayout(2, false));
		box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		// Command: __command__
		Label l = new Label(box, 0);
		l.setText(Messages.CommandLbl);
		l.setLayoutData(new GridData());
		
		command = new Text(box, SWT.BORDER);
		command.setLayoutData(new GridData(SWT.FILL, 0, true, false));
		command.setToolTipText(Messages.CommandTT);
		command.setText(config.getCommand());
		
		// Icon:   (*) run
		//         ( ) edit
		//         ( ) custom
		//         ___custom___
		l = new Label(box, 0);
		l.setText(Messages.IconLbl);
		l.setLayoutData(new GridData(0, SWT.TOP, false, true, 1, icon_names.length+2));
		
		icons = new Button[icon_names.length+1];
		icon_images = new Image[icon_names.length];
		final String icon_name = config.getIconName();
		final String builtin_icon;
		if (icon_name.startsWith("icon:"))
			builtin_icon = icon_name.substring(5);
		else
			builtin_icon = null;
		for (int i=0; i<icon_names.length; ++i)
		{
			icons[i] = new Button(box, SWT.RADIO);
			icons[i].setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
			icons[i].setText(icon_names[i]);
			icon_images[i] = LaunchConfig.getBuildinIcon(icon_names[i]);
			icons[i].setImage(icon_images[i]);

			if (icon_names[i].equals(builtin_icon))
				icons[i].setSelection(true);
		}
		icons[icon_names.length] = new Button(box, SWT.RADIO);
		icons[icon_names.length].setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
		icons[icon_names.length].setText(Messages.CustomIconLbl);

		custom_icon = new Text(box, SWT.BORDER);
		custom_icon.setLayoutData(new GridData(SWT.FILL, 0, true, false));
		custom_icon.setToolTipText(Messages.CustomIconTT);
		if (builtin_icon == null)
		{
			icons[icon_names.length].setSelection(true);
			custom_icon.setText(icon_name);
		}

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
		
		return box;
    }
	
	/** @return Icon that the user selected or entered for custom icon */
    private String getIconName()
	{
		for (int i=0; i<icon_names.length; ++i)
			if (icons[i].getSelection())
				return "icon:" + icon_names[i];
		return custom_icon.getText().trim();
	}

    /** @return LaunchConfig that the user selected */
    public LaunchConfig getConfig()
    {
    	config.setCommand(command.getText().trim());
    	config.setIconName(getIconName());
    	return config;
    }
}
