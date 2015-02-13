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

package org.csstudio.askap.sb.ui;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.askap.sb.util.SBTemplate;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author wu049
 * @created Sep 6, 2010
 * 
 */
public class VersionDialog extends Dialog {
	private static Logger logger = Logger.getLogger(VersionDialog.class.getName());

	private String message;
	private int majorVersion = 0;
	private int minorVersion = 0;
	private Boolean isMajor = null;
	
	public static void main(String[] args) {
		final Display display = new Display();
		Shell shell = new Shell(display);

		VersionDialog dialog = new VersionDialog(shell, false, false, "0.1", "XinyuTemplate");
		dialog.open();

	}
	/**
	 * InputDialog constructor
	 * 
	 * @param parent
	 *            the parent
	 * @param style
	 *            the style
	 */
	public VersionDialog(Shell parent, SBTemplate newTemplate, SBTemplate oldTemplate) {
		// compare old and new version
		// - if they are the same as the user if they want to create a new version (major or minor) - return new version
		//   or use the existing version, return null
		// - if they are different, the ask the user to create a new major or minor version
		this(parent, 
				newTemplate.getPythonScript().equals(oldTemplate.getPythonScript()), 
				compareParamMap(newTemplate.getParameterMap(), oldTemplate.getParameterMap()),
				oldTemplate.getVersion(), oldTemplate.getName());		
	}
	
	private VersionDialog(Shell parent, boolean samePython, boolean sameParamMap, String version, String name) {
		// Let users override the default styles
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);

		String versions[] = version.split("\\.");
		this.majorVersion = Integer.parseInt(versions[0]);
		this.minorVersion = Integer.parseInt(versions[1]);
		
		setText("Template Version Dialog");
		String msg = null;
		// if versions are the same
		if (samePython && sameParamMap) {
			msg = "This template is identical to " + name + "[v" + version + "]" + ". Would you like to create a new version?";
		} else {
			msg = name + " exists already (";
			if (!sameParamMap && !samePython) {
				msg += "both schemas and scripts ";
			} else if (!sameParamMap) {
				msg += "only schemas ";
			} else {
				msg += "only scripts";
			}
			msg += "are different from latest version). Would you like to create a new version?";
		}
		setMessage(msg);
	}

	/**
	 * Gets the message
	 * 
	 * @return String
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message
	 * 
	 * @param message
	 *            the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}


	/**
	 * Opens the dialog and returns the input
	 * 
	 * @return String
	 */
	public Boolean open() {
		// Create the dialog window
		Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.setSize(300, 150);
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		// Return the entered value, or null
		return isMajor;
	}
	
	private void setVersion(Label label, Button isMajor) {
		String versionLabel = null;
		if (isMajor.getSelection())
			versionLabel = "" + (majorVersion+1) + "." + 0;
		else
			versionLabel = "" + majorVersion + "." + (minorVersion+1);
		
		label.setText(versionLabel);
	}

	/**
	 * Creates the dialog's contents
	 * 
	 * @param shell
	 *            the dialog window
	 */
	private void createContents(final Shell shell) {
		GridLayout glayout = new GridLayout(2, true);
		glayout.marginWidth = 5;
		shell.setLayout(glayout);

		// Show the message
		Label label = new Label(shell, SWT.WRAP);
		label.setText(message);
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		label.setLayoutData(gridData);

		final Button majorVersion = new Button(shell, SWT.CHECK);
		majorVersion.setText("Create Major Version");
		majorVersion.setSelection(true);
		
		final Label version = new Label(shell, SWT.NONE);
		setVersion(version, majorVersion);
		
		majorVersion.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
				setVersion(version, majorVersion);
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// Create the OK button and add a handler
		// so that pressing it will set input
		// to the entered value
		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("Create New Version");
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				isMajor = new Boolean(majorVersion.getSelection());
				shell.close();
			}
		});

		// Create the cancel button and add a handler
		// so that pressing it will set input to null
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				isMajor = null;
				shell.close();
			}
		});

		// Set the OK button as the default, so
		// user can type input and press Enter
		// to dismiss
		shell.setDefaultButton(ok);
		shell.pack();
	}
	
	static boolean compareParamMap(Map<String, String> newParamMap, Map<String, String> oldParamMap) {
		boolean result = true;
		
		for (String key : newParamMap.keySet()) {
			String newValue = newParamMap.get(key);
			String oldValue = oldParamMap.get(key);
			
			if (!newValue.equals(oldValue)) {
				logger.log(Level.WARNING, "Schema difference: key='" + key + "' new value ='" + newValue + "' old value ='" + oldValue + "'");
				result = false;
			}
		}
		
		// now need to find all the key which were only in the old map (ie ones which have been deleted)
		for (String key : oldParamMap.keySet()) {
			String newValue = newParamMap.get(key);
			String oldValue = oldParamMap.get(key);
			
			if (newValue==null) {
				logger.log(Level.WARNING, "Schema difference: key='" + key + "' new value ='" + newValue + "' old value ='" + oldValue + "'");
				result = false;
			}
		}
		return result;
	}

}