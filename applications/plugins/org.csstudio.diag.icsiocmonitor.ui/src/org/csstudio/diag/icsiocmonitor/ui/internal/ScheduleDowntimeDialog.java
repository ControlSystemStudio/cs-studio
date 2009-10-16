/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.diag.icsiocmonitor.ui.internal;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

/**
 * Dialog to input the amount of downtime that the user wants to schedule.
 * 
 * @author Joerg Rathlev
 */
class ScheduleDowntimeDialog extends TitleAreaDialog {

	private static final int MIN_DURATION = 1;
	private static final int MAX_DURATION = Integer.MAX_VALUE;
	private final String _iocName;
	private int duration = 60;

	/**
	 * Creates the dialog.
	 * 
	 * @param parentShell
	 *            the parent shell.
	 * @param iocName
	 *            the name of the IOC.
	 */
	public ScheduleDowntimeDialog(Shell parentShell, String iocName) {
		super(parentShell);
		_iocName = iocName;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Schedule IOC Downtime");
	}

	/**
	 * Updates the enablement of the OK button.
	 * 
	 * @param enabled
	 *            <code>true</code> to enable the button, <code>false</code> to
	 *            disable.
	 */
	private void okButtonSetEnabled(boolean enabled) {
		getButton(IDialogConstants.OK_ID).setEnabled(enabled);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(
				(Composite) super.createDialogArea(parent), SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		setTitle("Schedule Downtime for " + _iocName);
		setMessage("Enter the duration of the downtime.");
		
		Label label = new Label(composite, SWT.NONE);
		label.setText("Duration (seconds): ");
		
		final Spinner spinner = new Spinner(composite, SWT.BORDER);
		spinner.setValues(duration, MIN_DURATION, MAX_DURATION, 0, 1, 10);
		spinner.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				String text = spinner.getText();
				try {
					duration = Integer.parseInt(text);
					okButtonSetEnabled(true);
				} catch (NumberFormatException e) {
					okButtonSetEnabled(false);
				}
			}
		});
		
		return composite;
	}

	/**
	 * Return the duration entered by the user.
	 * 
	 * @return the duration in seconds.
	 */
	int getDuration() {
		return duration;
	}

}
