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

import java.util.Date;

import org.csstudio.askap.logviewer.Preferences;
import org.csstudio.askap.utility.AskapHelper;
import org.csstudio.askap.utility.icemanager.LogObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author wu049
 * @created Sep 6, 2010
 * 
 */
public class LogMessageDialog extends Dialog {
	
	public static void main(String[] args) {
		final Display display = new Display();
		Shell shell = new Shell(display);

		LogObject log = new LogObject();
		log.setHostName("waratah");
		log.setOrigin("Data Service");
		log.setTag("Xinyu");
		log.setLogLevel(Preferences.LOG_LEVELS[0]);
		log.setTimeStamp(new Date());
		
		String text = "";
		for (int i=0; i<32; i++) {
			for (int j=0; j<i; j++)
				text += (i + "-This is a line of text in a widget-" + i);
			
			text += "\n";
		}

		log.setLogMessage(text);	
		
		LogMessageDialog dialog = new LogMessageDialog(shell, log);
		dialog.open();
	}

	private LogObject log;
	
	public LogMessageDialog(Shell parent, LogObject log) {
		// Let users override the default styles
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		this.log = log;
	}


	/**
	 * Opens the dialog and returns the input
	 * 
	 * @return String
	 */
	public void open() {
		// Create the dialog window
		Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.setSize(500, 500);
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	/**
	 * Creates the dialog's contents
	 * 
	 * @param shell
	 *            the dialog window
	 */
	private void createContents(final Shell shell) {
		GridLayout glayout = new GridLayout(2, false);
		glayout.marginWidth = 10;
		shell.setLayout(glayout);

		if (log==null) {
			log = new LogObject();
			log.setHostName("");
			log.setOrigin("");
			log.setTag("");
			log.setLogLevel(Preferences.LOG_LEVELS[0]);
			log.setTimeStamp(new Date());
			log.setLogMessage("");
		}
		// Show the message
		Label label = new Label(shell, SWT.BORDER);
		label.setText("Timestamp");
		Text field = new Text(shell, SWT.BORDER);
		field.setText(AskapHelper.getFormatedData(log.getTimeStamp(), null));
		field.setEditable(false);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		field.setLayoutData(gridData);
		
		label = new Label(shell, SWT.BORDER);
		label.setText("Origin");
		field = new Text(shell, SWT.BORDER);
		field.setText(log.getOrigin());
		field.setEditable(false);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		field.setLayoutData(gridData);

		label = new Label(shell, SWT.BORDER);
		label.setText("Host");
		field = new Text(shell, SWT.BORDER);
		field.setText(log.getHostName());
		field.setEditable(false);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		field.setLayoutData(gridData);

		label = new Label(shell, SWT.BORDER);
		label.setText("Tag");
		field = new Text(shell, SWT.BORDER);
		field.setText(log.getTag());
		field.setEditable(false);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		field.setLayoutData(gridData);
		
		label = new Label(shell, SWT.BORDER);
		label.setText("Level");
		field = new Text(shell, SWT.BORDER);
		field.setText(log.getLogLevel());
		field.setEditable(false);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		field.setLayoutData(gridData);
	
		label = new Label(shell, SWT.BORDER);
		label.setText("Message");
		
		field = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		field.setText(log.getLogMessage());
		field.setEditable(false);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		field.setLayoutData(gridData);
		
		
		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("OK");
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		ok.setLayoutData(gridData);
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});
		

		shell.setDefaultButton(ok);
		shell.pack();
	}
}