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

package org.csstudio.management.contactscommands.ui;

import java.io.Serializable;
import java.util.HashMap;

import org.csstudio.remote.management.CommandDescription;
import org.csstudio.remote.management.CommandParameterDefinition;
import org.csstudio.remote.management.CommandParameterEnumValue;
import org.csstudio.remote.management.CommandParameters;
import org.csstudio.remote.management.IManagementCommandService;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog which prompts the user for the parameters to a management command.
 * 
 * @author Joerg Rathlev
 */
public class ManagementCommandParametersDialog extends TitleAreaDialog {
	
	private final CommandDescription _command;
	private final IManagementCommandService _commandService;
	private final HashMap<CommandParameterDefinition,Serializable> _parameterValues;

	/**
	 * Creates the dialog.
	 * 
	 * @param parentShell
	 *            the parent shell.
	 * @param command
	 *            the command whose parameters the dialog will ask for.
	 * @param commandService
	 *            the service which this dialog will use if the command defines
	 *            dynamic paramters.
	 */
	public ManagementCommandParametersDialog(Shell parentShell,
			CommandDescription command,
			IManagementCommandService commandService) {
		super(parentShell);
		_command = command;
		_commandService = commandService;
		_parameterValues = new HashMap<CommandParameterDefinition, Serializable>();
		
		// Set all parameter values to 'null' by default. As long as one of the
		// values is still 'null', the OK button will not be enabled.
		for (CommandParameterDefinition def : _command.getParameters()) {
			_parameterValues.put(def, null);
		}
	}
	
	/**
	 * Returns the actual command paramters that the user selected in this
	 * dialog.
	 * 
	 * @return a new array containing the actual command parameters. This dialog
	 *         does not keep a reference to the returned array.
	 */
	public CommandParameters getCommandParameters() {
		CommandParameters result = new CommandParameters();
		for (CommandParameterDefinition def : _parameterValues.keySet()) {
			result.set(def.getIdentifier(), _parameterValues.get(def));
		}
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Execute Management Command");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		updateOKButtonEnablement();
	}
	
	/**
	 * Enables or disables the OK button based on whether all parameter values
	 * have been provided by the user.
	 */
	private void updateOKButtonEnablement() {
		boolean enabled = !_parameterValues.containsValue(null);
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
		
		setTitle(_command.getLabel());
		setMessage("Enter the parameter values for the command.");
		
		createParameterControls(composite);
		
		return composite;
	}

	/**
	 * Creates the labels and input controls for the command parameters.
	 * 
	 * @param parent
	 *            the parent composite to which the controls will be added. The
	 *            composite should have a two-column grid layout.
	 */
	private void createParameterControls(Composite parent) {
		for (CommandParameterDefinition parameter : _command.getParameters()) {
			Label label = new Label(parent, SWT.None);
			label.setText(parameter.getLabel() + ":");
			
			Control control = null;
			switch (parameter.getType()) {
			case STRING:
				control = createStringParameterControl(parent, parameter);
				break;
			case INTEGER:
				control = createIntegerParamterControl(parent, parameter);
				break;
			case ENUMERATION:
				control = createEnumParamterControl(parent, parameter,
						parameter.getEnumerationValues());
				break;
			case DYNAMIC_ENUMERATION:
				// TODO: do not call remote service in UI thread
				CommandParameterEnumValue[] values =
					_commandService.getDynamicEnumerationValues(
							_command.getIdentifier(), parameter.getIdentifier());
				control = createEnumParamterControl(parent, parameter, values);
				break;
			}
			control.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		}
	}

	/**
	 * Creates an input control for an enumeration paramter. This method works
	 * for both static and dynamic enumeration values because the values are
	 * passed to this method as a method parameter.
	 * 
	 * @param parent
	 *            the composite to which the new control is added.
	 * @param parameter
	 *            the parameter.
	 * @param enumValues
	 *            the enumeration values.
	 * @return the input control.
	 */
	private Control createEnumParamterControl(Composite parent,
			final CommandParameterDefinition parameter,
			final CommandParameterEnumValue[] enumValues) {
		final Combo control = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		String[] labels = new String[enumValues.length];
		for (int i = 0; i < enumValues.length; i++) {
			labels[i] = enumValues[i].getLabel();
		}
		control.setItems(labels);
		control.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				_parameterValues.put(parameter,
						enumValues[control.getSelectionIndex()].getValue());
				updateOKButtonEnablement();
			}
		});
		return control;
	}

	/**
	 * Creates an input control for an integer paramter.
	 * 
	 * @param parent
	 *            the composite to which the new control is added.
	 * @param parameter
	 *            the parameter.
	 * @return the input control.
	 */
	private Control createIntegerParamterControl(Composite parent,
			final CommandParameterDefinition parameter) {
		final Spinner control = new Spinner(parent, SWT.BORDER);
		final int minimum = parameter.getMinimum();
		final int maximum = parameter.getMaximum();
		control.setValues(minimum, minimum, maximum, 0, 1, 10);
		control.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				String text = control.getText();
				try {
					int value = Integer.parseInt(text);
					_parameterValues.put(parameter, Integer.valueOf(value));
					// TODO: display error message if value is out of bounds
				} catch (NumberFormatException e) {
					// TODO: error handling
				}
			}
		});
		_parameterValues.put(parameter, minimum); // default value
		return control;
	}

	/**
	 * Creates an input control for a string paramter.
	 * 
	 * @param parent
	 *            the composite to which the new control is added.
	 * @param parameter
	 *            the parameter.
	 * @return the text input control.
	 */
	private Text createStringParameterControl(Composite parent,
			final CommandParameterDefinition parameter) {
		final Text control = new Text(parent, SWT.BORDER);
		control.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String text = control.getText();
				_parameterValues.put(parameter, text);
			}
		});
		_parameterValues.put(parameter, ""); // default value
		return control;
	}
}
