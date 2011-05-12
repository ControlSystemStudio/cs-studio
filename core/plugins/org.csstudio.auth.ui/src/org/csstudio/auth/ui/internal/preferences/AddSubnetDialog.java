/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
 package org.csstudio.auth.ui.internal.preferences;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.csstudio.auth.internal.subnet.Subnet;
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
import org.eclipse.swt.widgets.Text;

/**
 * Dialog to add a subnet to the Onsite Subnet preferences page.
 * 
 * @author Joerg Rathlev
 */
class AddSubnetDialog extends TitleAreaDialog {
	
	/**
	 * The network address input field.
	 */
	private Text _networkAddress;
	
	/**
	 * The netmask input field.
	 */
	private Text _netmask;
	
	/**
	 * The subnet that was entered by the user. This gets set when the user
	 * presses the Ok button.
	 */
	private Subnet _result;
	
	/**
	 * Regular expression that matches IPv4 addresses. (Note: this expression
	 * allows numbers up to 299, so it is not guaranteed that a string is a
	 * valid IP address if it matches the expression.)
	 */
	private static final String IPV4_ADDRESS_REGEX =
		"([12]?[0-9]?[0-9]\\.){3}([12]?[0-9]?[0-9])";
	
	/**
	 * Creates a new Add Subnet Dialog.
	 * @param parentShell the parent shell.
	 */
	AddSubnetDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/**
	 * Returns the subnet the user entered in this dialog.
	 * @return the subnet the user entered in this dialog, or <code>null</code>
	 *         if the user did not enter a valid subnet.
	 */
	public Subnet getSubnet() {
		return _result;
	}
	
	/**
	 * Sets the result of this dialog (the subnet that was entered by the user).
	 */
	@Override
	protected void okPressed() {
		try {
			InetAddress address = InetAddress.getByName(_networkAddress.getText());
			InetAddress netmask = InetAddress.getByName(_netmask.getText());
			_result = new Subnet(address, netmask);
		} catch (UnknownHostException e) {
			_result = null;
		} catch (IllegalArgumentException e) {
			_result = null;
		}
		super.okPressed();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Add Subnet");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite parentComposite = (Composite) super.createDialogArea(parent);
		
		setTitle("Add Subnet");
		setMessage("Please enter the subnet address and netmask.");
		
		Composite contents = new Composite(parentComposite, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.numColumns = 2;
        contents.setLayout(layout);
        contents.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true));
        contents.setFont(parentComposite.getFont());
        
        Label addressLabel = new Label(contents, SWT.NULL);
        addressLabel.setText("Subnet address:");
        _networkAddress = new Text(contents, SWT.SINGLE | SWT.BORDER);
        _networkAddress.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        _networkAddress.addModifyListener(new ModifyListener() {
        	public void modifyText(ModifyEvent e) {
        		if (e.widget == _networkAddress) {
        			autocompleteNetmask();
        			checkValid();
        		}
        	}
        });
        
        Label netmaskLabel = new Label(contents, SWT.NULL);
        netmaskLabel.setText("Subnet address:");
        _netmask = new Text(contents, SWT.SINGLE | SWT.BORDER);
        _netmask.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        _netmask.addModifyListener(new ModifyListener() {
        	public void modifyText(ModifyEvent e) {
        		if (e.widget == _netmask) {
        			checkValid();
        		}
        	}
        });
		
		return contents;
	}
	
	/**
	 * Checks if a valid network address and netmask were entered. Enables
	 * or disables the Ok button based on the validity, and displays error
	 * messages in this dialog's message area as appropriate.
	 */
	private void checkValid() {
		boolean addressValid = _networkAddress.getText().matches(IPV4_ADDRESS_REGEX);
		
		boolean netmaskValid;
		if (_netmask.getText().matches(IPV4_ADDRESS_REGEX)) {
			try {
				InetAddress netmask = InetAddress.getByName(_netmask.getText());
				netmaskValid = Subnet.isValidNetmask(netmask);
			} catch (UnknownHostException e) {
				netmaskValid = false;
			}
		} else {
			netmaskValid = false;
		}
		
		if (!addressValid) {
			setErrorMessage("Invalid subnet address.");
		} else if (!netmaskValid) {
			setErrorMessage("Invalid netmask.");
		} else {
			setErrorMessage(null);
		}
		
		getButton(IDialogConstants.OK_ID).setEnabled(
				addressValid && netmaskValid);
	}
	
	/**
	 * Automatically enters a netmask based on the network address. The netmask
	 * is only entered automatically if no netmask was entered yet.
	 */
	private void autocompleteNetmask() {
		// no autocompletion unless netmask is still empty
		if (!_netmask.getText().equals(""))
			return;
		
		// no automcomletion if the subnet address does not look like a standard
		// IPv4 address
		if (!(_networkAddress.getText().matches(IPV4_ADDRESS_REGEX)))
			return;
		
		try {
			InetAddress address = InetAddress.getByName(_networkAddress.getText());
			byte[] addrBytes = address.getAddress();
			// only autocomplete if length == 4 (IPv4 address)
			if (addrBytes.length == 4) {
				StringBuilder netmask = new StringBuilder();
				for (int i = 0; i < 4; i++) {
					netmask.append(addrBytes[i] == 0 ? "0" : "255");
					if (i < 3)
						netmask.append(".");
				}
				_netmask.setText(netmask.toString());
			}
		} catch (UnknownHostException e) {
			// do nothing
		}
	}
	
    /**
     * Creates the standard dialog buttons OK and Cancel and disables the OK
     * button.
     * @param parent the button bar composite.
     */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
    	getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
}
