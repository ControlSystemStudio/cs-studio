/*
 * Copyright (c) 2009 CSIRO Australia Telescope National Facility (ATNF) Commonwealth
 * Scientific and Industrial Research Organisation (CSIRO) PO Box 76, Epping NSW 1710,
 * Australia atnf-enquiries@csiro.au
 *
 * This file is part of the ASKAP software distribution.
 *
 * The ASKAP software distribution is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite
 * 330, Boston, MA 02111-1307 USA
 */

package org.csstudio.askap.logviewer.ui;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.askap.logviewer.Preferences;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author wu049
 * @created Sep 6, 2010
 * 
 */
public class RealtimeTopicSelectionDialog extends Dialog {
	
	private static final Logger logger = Logger.getLogger(RealtimeTopicSelectionDialog.class.getName());
	
	Shell shell = null;

	private Combo topicNameCombo;
	
	private String topicName;
	private String[] topicNames;
	
	/**
	 * InputDialog constructor
	 * 
	 * @param parent
	 *            the parent
	 * @param style
	 *            the style
	 */
	public RealtimeTopicSelectionDialog(Shell parent) {
		super(parent, SWT.APPLICATION_MODAL);
	}
	
	/**
	 * Opens the dialog and returns the input
	 * 
	 * @return String
	 */
	public String open() {
		topicNames = getTopicNames();
		
		// if no topic names configure, error
		if (topicNames==null) {
			logger.log(Level.WARNING, "No realtime logger topic names configured");
            ExceptionDetailsErrorDialog.openError(shell,
                    "ERROR",
                    "Could not get categories", 
                    new Exception("No realtime logger topic names configured"));
            
            return null;
		}
		
		// if there is only one topic configured, just return that
		if (topicNames.length==1) {
			return topicNames[0];
		}
		
		// Otherwise user has to choose one
		// Create the dialog window
		shell = new Shell(getParent(), getStyle());
		shell.setText("Realtime Log Viewere Topic Selection");
		createContents(shell);
		shell.setSize(250, 75);
		
		Display display = getParent().getDisplay();
		Rectangle screen = display.getMonitors()[0].getBounds();
		shell.setBounds((screen.width-250)/2, (screen.height-100)/2, 250, 100);
		
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		// Return the entered value, or null
		return topicName;
	}
	
	/**
	 * Creates the dialog's contents
	 * 
	 * @param shell
	 *            the dialog window
	 */
	private void createContents(final Shell page) {
		
		page.setBackground(new Color(null, 150, 255, 255));
		
		GridLayout gridLayout = new GridLayout(2, false);
		page.setLayout(gridLayout);			

		Label dummy = new Label(page, 0);
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		dummy.setLayoutData(gridData);

		
		topicNameCombo = new Combo(page, SWT.PUSH);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		topicNameCombo.setLayoutData(gridData);

		topicNameCombo.add("Please select a topic name ...");
		
		for (String s : topicNames)
			topicNameCombo.add(s);
		
		topicNameCombo.select(0);
		
		final Button okButton = new Button(page, SWT.PUSH);
		okButton.setText("OK");
		okButton.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent event) {
				int selection = topicNameCombo.getSelectionIndex();
				topicName = topicNames[--selection];
				page.close();	
			}
			
			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});
		okButton.setEnabled(false);
		
		final Button cancelButton = new Button(page, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent event) {
				topicName = null;
				page.close();	
			}
			
			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});
					
		topicNameCombo.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selection = topicNameCombo.getSelectionIndex();
				if (selection>0)
					okButton.setEnabled(true);
				else
					okButton.setEnabled(false);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		

		page.pack();
	}
	
	private String[] getTopicNames() {
		String topicNames[] = null;
		String topicNameStr = Preferences.getLogMessageTopicName();
		
		if (topicNameStr==null || topicNameStr.trim().length()==0) {
			return null;
		}
		
		StringTokenizer tokenizer = new StringTokenizer(topicNameStr, ";");
		topicNames = new String[tokenizer.countTokens()];

		for (int i=0; tokenizer.hasMoreTokens(); i++) {
			topicNames[i] = tokenizer.nextToken();
		}
		
		return topicNames;
	}

}