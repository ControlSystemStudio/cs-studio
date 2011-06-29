
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

package org.csstudio.ams.filter.ui;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.csstudio.ams.AMSException;
import org.csstudio.ams.Messages;
import org.csstudio.ams.dbAccess.HoldsAnDatabaseId;
import org.csstudio.ams.dbAccess.configdb.FilterConditionProcessVariableDAO;
import org.csstudio.ams.dbAccess.configdb.FilterConditionProcessVariableTObject;
import org.csstudio.ams.filter.FilterConditionProcessVariable;
import org.csstudio.ams.filter.FilterConditionProcessVariable.Operator;
import org.csstudio.ams.filter.FilterConditionProcessVariable.SuggestedProcessVariableType;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.epics.css.dal.Timestamp;

public class FilterConditionProcessVariableUI extends FilterConditionUI {

	/**
	 * Listener for the button that is used to check the connection to the
	 * process variable. When the button is pressed, this will attempt to
	 * create a connection to the process variable and will display the
	 * PV's current value if the connection is successful.
	 */
	private class VerifierButtonSelectionListener extends SelectionAdapter {
		
		/**
		 * Timeout to wait for a connection state.
		 */
		// TODO Make this configurable by a preference page.
		private static final int TIMEOUT_OF_CONNECTION_ATTEMP = 5;
		
		protected volatile boolean connected;
		protected volatile boolean checked;
		protected volatile boolean valueRecieved;
		protected volatile Object recievedValue;

		@Override
		public void widgetSelected(SelectionEvent e) {
			try {
				IProcessVariableAddress pv = ProcessVariableAdressFactory
						.getInstance().createProcessVariableAdress(
								getText(Display.getCurrent(), txtChannelValue));
				IProcessVariableConnectionService connectionService = ProcessVariableConnectionServiceFactory
						.getDefault().getProcessVariableConnectionService();
				connected = false;
				checked = false;
				valueRecieved = false;
				switch (getSelectedType()) {
				case DOUBLE:
					connectionService.register(
							new IProcessVariableValueListener<Double>() {
								public void connectionStateChanged(
										ConnectionState connectionState) {
									if (ConnectionState.CONNECTED
											.equals(connectionState)) {
										connected = true;
									}
									if (!ConnectionState.UNKNOWN.equals(connectionState)) {
										checked = true;
									}
								}

								public void valueChanged(Double value, Timestamp timestamp) {
									recievedValue = value;
									valueRecieved = true;
								}

								public void errorOccured(String error) {
									
								}

                                @SuppressWarnings("unused")
                                public void valueChanged(Double value)
                                {
                                    // TODO Auto-generated method stub
                                    
                                }
							}, pv, ValueType.DOUBLE);
					break;
				case LONG:
					connectionService.register(
							new IProcessVariableValueListener<Long>() {
								public void connectionStateChanged(
										ConnectionState connectionState) {
									if (ConnectionState.CONNECTED
											.equals(connectionState)) {
										connected = true;
									}
									if (!ConnectionState.UNKNOWN.equals(connectionState)) {
										checked = true;
									}
								}

								public void valueChanged(Long value, Timestamp timestamp) {
									recievedValue = value;
									valueRecieved = true;
								}

								public void errorOccured(String error) {
								}

                                @SuppressWarnings("unused")
                                public void valueChanged(Long value)
                                {
                                    // TODO Auto-generated method stub
                                    
                                }
							}, pv, ValueType.LONG);
					break;
				case STRING:
					connectionService.register(
							new IProcessVariableValueListener<String>() {
								public void connectionStateChanged(
										ConnectionState connectionState) {
									if (ConnectionState.CONNECTED
											.equals(connectionState)) {
										connected = true;
									}
									if (!ConnectionState.UNKNOWN.equals(connectionState)) {
										checked = true;
									}
								}

								public void valueChanged(String value, Timestamp timestamp) {
									recievedValue = value;
									valueRecieved = true;
								}

								public void errorOccured(String error) {
								}

                                @SuppressWarnings("unused")
                                public void valueChanged(String value)
                                {
                                    // TODO Auto-generated method stub
                                    
                                }
							}, pv, ValueType.STRING);
					break;
				default:
					// should never get here
					break;
				}
				int timer = 0;
				while (!checked) {
					Thread.yield();
					if (timer == TIMEOUT_OF_CONNECTION_ATTEMP)
						break;
					Thread.sleep(1000);
					timer++;
				}
				if (connected) {
					while (!valueRecieved) {
						Thread.yield();
					}
					showMessageDialog(Display.getCurrent().getActiveShell(),
							Messages.FilterConditionProcessVaribaleBasedUI_Connection_Successful + recievedValue,
							Messages.FilterConditionProcessVaribaleBasedUI_No_Error_Title,
							SWT.ICON_INFORMATION);
				} else {
					showMessageDialog(Display.getCurrent().getActiveShell(),
							Messages.FilterConditionProcessVaribaleBasedUI_Error_No_Connection, Messages.FilterConditionProcessVaribaleBasedUI_Error_Title,
							SWT.ICON_WARNING);
				}
			} catch (Exception ex) {
				String message = Messages.FilterConditionProcessVaribaleBasedUI_Error_No_Connection_With_Reason;
				CentralLogger.getInstance().error(this,
						message, ex);
				if (ex.getMessage()!=null && ex.getMessage().trim().length()>0 && !ex.getMessage().equals("null")) {
					message = message + ex.getMessage();
				} else {
					message = message + Messages.FilterConditionProcessVaribaleBasedUI_Unknown;
				}
				showMessageDialog(Display.getCurrent().getActiveShell(),
						message, Messages.FilterConditionProcessVaribaleBasedUI_Error_Title, SWT.ICON_WARNING);
			} finally {
				((Button) e.item).setSelection(false);
			}

		}
	}

	private static final int MINIMAL_CHANNEL_NAME_LENGTH = 1;
	/**
	 * Prefix for message text ids in messages-properties.
	 */
	private static final String ENUM_VALUE_MESSAGE_TEXT_ID_PREFIX = "FilterConditionProcessVariableUI_EnumValueText_";
	private Composite localParent;
	private Label lblChannelValue;
	private Text txtChannelValue;
	private Button butChannelVerifier;
	private Label lblSuggReturnType;
	private Combo cboSuggReturnType;
	private Label lblOperator;
	private Combo cboOperator;
	private Label lblCompareValue;
	private Text txtCompareValue;

	/**
	 * The last created/loaded {@link FilterConditionProcessVariable}.
	 */
	private FilterConditionProcessVariableTObject snapshot = null;

	@Override
    public boolean check() {
		List<String> errors = new LinkedList<String>();

		if (getText(txtChannelValue.getDisplay(), txtChannelValue).length() < MINIMAL_CHANNEL_NAME_LENGTH) {
			errors
					.add(Messages.FilterConditionProcessVaribaleBasedUI_Error_MissingValidChannelName);
		}

		SuggestedProcessVariableType suggestedType = getSelectedEnumValue(
				cboSuggReturnType, SuggestedProcessVariableType.class);

		if (suggestedType == null) {
			errors
					.add(Messages.FilterConditionProcessVaribaleBasedUI_Error_MissingSuggestedTypeSelection);
		}

		if (getSelectedEnumValue(cboOperator, Operator.class) == null) {
			errors
					.add(Messages.FilterConditionProcessVaribaleBasedUI_Error_MissingOperatorSelection);
		}

		String compValue = getText(txtCompareValue.getDisplay(),
				txtCompareValue);

		if (suggestedType != null && !suggestedType.isParsableValue(compValue)) {
			errors
					.add(Messages.FilterConditionProcessVaribaleBasedUI_Error_InvalidCompValue);
		}

		if (!errors.isEmpty()) {
			showMessageDialog(
					null,
					errors,
					Messages.FilterConditionProcessVaribaleBasedUI_Error_Dialog_Title,
					SWT.ICON_ERROR);
		}

		return errors.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @require check()
	 */
	@Override
    public void create(Connection conDb, int filterConditionID)
			throws AMSException {
		try {
			assert check() : "Precondition violated: check()";

			snapshot = collectDataToBeSaved(filterConditionID);
			FilterConditionProcessVariableDAO.insert(conDb, snapshot);
		} catch (Exception ex) {
			CentralLogger.getInstance().fatal(this,
					"Fail to create new dataset", ex);
			throw new AMSException("Fail to create new dataset", ex);
		}
	}

	/**
	 * Collects data from the UI via sych-Calls and creates a new
	 * {@link FilterConditionProcessVariableTObject} instance based on UI input.
	 * 
	 * @param filterConditionID
	 *            The filter condition id to be used in new
	 *            {@link FilterConditionProcessVariableTObject} instance
	 * @return a new {@link FilterConditionProcessVariableTObject} instance, not
	 *         null.
	 */
	private FilterConditionProcessVariableTObject collectDataToBeSaved(
			int filterConditionID) {
		String channelName = getText(txtChannelValue.getDisplay(),
				txtChannelValue);
		Operator operator = getSelectedEnumValue(cboOperator, Operator.class);
		SuggestedProcessVariableType suggestedType = getSelectedEnumValue(
				cboSuggReturnType, SuggestedProcessVariableType.class);
		String rawCompareValue = getText(txtCompareValue.getDisplay(),
				txtCompareValue);
		Object compareValue = suggestedType.parseDatabaseValue(rawCompareValue);
		FilterConditionProcessVariableTObject filterConfiguration = new FilterConditionProcessVariableTObject(
				filterConditionID, channelName, operator, suggestedType,
				compareValue);
		return filterConfiguration;
	}

	/**
	 * Determines the selected Enum-element on current selection in the
	 * combo-box. Pay attention: There is no type safty! If Combo contains a
	 * different enum-type with same ids, the result may be inconsistently; if
	 * no matching element found for selected index (id) the result is null.
	 * 
	 * @param <T>
	 *            The Enum-type.
	 * @param cboWidget
	 *            the combo.
	 * @param enumClass
	 *            the class of the enum-type.
	 * @return the enum-value or null (see above).
	 * 
	 * @require HoldsAnDazabaseId.class.isAssignableFrom(enumClass)
	 * @require Enum.class.isAssignableFrom(enumClass)
	 */
	private <T extends Enum<?> & HoldsAnDatabaseId> T getSelectedEnumValue(
			Combo cboWidget, Class<T> enumClass) {
		assert HoldsAnDatabaseId.class.isAssignableFrom(enumClass) : "Precondition violated: HoldsAnDazabaseId.class.isAssignableFrom(enumClass)";
		assert Enum.class.isAssignableFrom(enumClass) : "Precondition violated: Enum.class.isAssignableFrom(enumClass)";

		int dbId = super.getSelectedComboBoxIdValueUI(cboWidget.getDisplay(),
				cboWidget);
		T result = null;

		T[] enumElements = enumClass.getEnumConstants();

		// Log.log(Log.INFO, "Searching for enum value with id: " + dbId);

		for (T element : enumElements) {
			if (element.asDatabaseId() == dbId) {
				result = element;
				break;
			}
		}

		if (result == null) {
			CentralLogger.getInstance().warn(this, "Enum constant not found!");
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void createUI(Composite parent) {
		if (localParent == null) {
			localParent = new Composite(parent, SWT.NONE);
			createControls(localParent);
			initControls();
		}
	}

	@SuppressWarnings("unused")
    private void createControls(final Composite parent) {
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 10;
		gridLayout.verticalSpacing = 5;
		gridLayout.marginLeft = 10;
		gridLayout.marginRight = 10;
		gridLayout.marginTop = 10;

		parent.setLayout(gridLayout);

		lblChannelValue = new Label(parent, SWT.None);
		lblChannelValue.setLayoutData(getGridData(-1, -1, 1, 1, SWT.BEGINNING,
				SWT.CENTER, false, true));
		lblChannelValue
				.setText(Messages.FilterConditionProcessVaribaleBasedUI_ChannelLabelText);
		txtChannelValue = new Text(parent, SWT.BORDER | SWT.SINGLE);
		txtChannelValue.setLayoutData(getGridData(200, -1, 1, 1, SWT.BEGINNING,
				SWT.CENTER, false, false));

		// platzhalter
		new Label(parent, SWT.NONE);

		butChannelVerifier = new Button(parent, SWT.PUSH);
		butChannelVerifier
				.setText(Messages.FilterConditionProcessVaribaleBasedUI_ChannelVerifierButtonText);
		SelectionAdapter selectionAdapter = new VerifierButtonSelectionListener();
		butChannelVerifier.addSelectionListener(selectionAdapter);

		lblSuggReturnType = new Label(parent, SWT.None);
		lblSuggReturnType.setLayoutData(getGridData(-1, -1, 1, 1,
				SWT.BEGINNING, SWT.CENTER, false, false));
		lblSuggReturnType
				.setText(Messages.FilterConditionProcessVaribaleBasedUI_SuggRetTypeLabelText);
		cboSuggReturnType = new Combo(parent, SWT.BORDER | SWT.SINGLE
				| SWT.READ_ONLY);
		cboSuggReturnType.setLayoutData(getGridData(200, -1, 1, 1,
				SWT.BEGINNING, SWT.CENTER, false, false));

		lblOperator = new Label(parent, SWT.None);
		lblOperator.setLayoutData(getGridData(-1, -1, 1, 1, SWT.BEGINNING,
				SWT.CENTER, false, false));
		lblOperator
				.setText(Messages.FilterConditionProcessVaribaleBasedUI_OperatorLabelText);
		cboOperator = new Combo(parent, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		cboOperator.setLayoutData(getGridData(200, -1, 1, 1, SWT.BEGINNING,
				SWT.CENTER, false, false));

		lblCompareValue = new Label(parent, SWT.None);
		lblCompareValue.setLayoutData(getGridData(-1, -1, 1, 1, SWT.BEGINNING,
				SWT.CENTER, false, true));
		lblCompareValue
				.setText(Messages.FilterConditionProcessVaribaleBasedUI_CompareValueLabelText);
		txtCompareValue = new Text(parent, SWT.BORDER | SWT.SINGLE);
		txtCompareValue.setLayoutData(getGridData(200, -1, 1, 1, SWT.BEGINNING,
				SWT.CENTER, false, false));

		cboSuggReturnType.addSelectionListener(new SelectionAdapter() {
			@Override
            public void widgetSelected(SelectionEvent e) {
				handleSuggestedTypeChanged();
			}
		});
	}

	/**
	 * Updates the Operator combo box depending on the selected suggested type
	 * cause there are some operators, which are not assignable to any type.
	 */
	protected void handleSuggestedTypeChanged() {
		SuggestedProcessVariableType type = getSelectedType();
		initComboBoxUI(Display.getCurrent(), cboOperator,
				enumArrayToMessageList(type.getSupportedOperators()));
	}
	
	/**
	 * Returns the suggested process variable type currently selected in the
	 * combo box.
	 * @return the suggested process variable type.
	 */
	private SuggestedProcessVariableType getSelectedType() {
		int typeId = getSelectedComboBoxIdValueUI(Display.getCurrent(),
				cboSuggReturnType);
		return SuggestedProcessVariableType.findOperatorOfDBId((short) typeId);
	}

	/**
	 * Initialize the content of avail combo boxes.
	 */
	private void initControls() {
		initComboBoxUI(Display.getCurrent(), cboSuggReturnType,
				enumArrayToMessageList(SuggestedProcessVariableType.values()));
		initComboBoxUI(Display.getCurrent(), cboOperator,
				enumArrayToMessageList(Operator.values()));
	}

	/**
	 * Creates a list of combo items of an enum.
	 * 
	 * @param <T>
	 *            The Enum type; have to implement {@link HoldsAnDatabaseId}.
	 * @param array
	 *            The Array of enum values of type T.
	 * @return A list of created {@link ComboWidgetIdDataItem}s.
	 */
	private <T extends Enum<?> & HoldsAnDatabaseId> List<ComboWidgetIdDataItem> enumArrayToMessageList(
			T[] array) {
		List<ComboWidgetIdDataItem> result = new ArrayList<ComboWidgetIdDataItem>(
				array.length);
		for (T element : array) {
			String text = Messages.getString(ENUM_VALUE_MESSAGE_TEXT_ID_PREFIX
					+ element.name());
			result.add(new ComboWidgetIdDataItem(element.asDatabaseId(), text));
		}

		return result;
	}

	@Override
    public void delete(Connection conDb, int filterConditionID)
			throws AMSException {
		try {
			FilterConditionProcessVariableDAO.remove(conDb, filterConditionID);
		} catch (Exception ex) {
			CentralLogger.getInstance().fatal(
					this,
					"Could not remove FilterCondition with reference: "
							+ filterConditionID, ex);
			throw new AMSException(
					"Could not remove FilterCondition with reference: "
							+ filterConditionID, ex);
		}
	}

	@Override
    public void dispose() {
		if (localParent != null && !localParent.isDisposed()) {
			localParent.dispose();
			localParent = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public String getDisplayName() {
		return Messages.FilterConditionProcessVaribaleBasedUI_DisplayName;
	}

	@Override
    public boolean isChanged() {
		FilterConditionProcessVariableTObject newFC = collectDataToBeSaved(snapshot
				.getFilterConditionRef());
		return !newFC.equals(snapshot);
	}

	@Override
    public void load(Connection conDb, int filterConditionID)
			throws AMSException {
		snapshot = null;
		try {
			snapshot = FilterConditionProcessVariableDAO.select(conDb,
					filterConditionID);
		} catch (SQLException e) {
			CentralLogger.getInstance().fatal(this,
					"Can not load filter details!", e);
			throw new AMSException("Can not load filter details!", e);
		}

		if (snapshot != null) {
			setText(txtChannelValue.getDisplay(), txtChannelValue, snapshot
					.getProcessVariableChannelName());
			setComboBoxValueUI(cboSuggReturnType.getDisplay(),
					cboSuggReturnType, snapshot.getSuggestedType()
							.asDatabaseId());
			setComboBoxValueUI(cboOperator.getDisplay(), cboOperator, snapshot
					.getOperator().asDatabaseId());
			setText(txtCompareValue.getDisplay(), txtCompareValue, snapshot
					.getSuggestedType().toDbString(snapshot.getCompValue()));
		} else {
			reset();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void reset() {
		setText(txtChannelValue.getDisplay(), txtChannelValue, "");
		setComboBoxValueUI(cboSuggReturnType.getDisplay(), cboSuggReturnType,
				-1);
		setComboBoxValueUI(cboOperator.getDisplay(), cboOperator, -1);
		setText(txtCompareValue.getDisplay(), txtCompareValue, "");
	}

	@Override
    public void save(Connection conDb) throws AMSException {
		try {
			assert check() : "Precondition violated: check()";

			FilterConditionProcessVariableTObject filterConfiguration = collectDataToBeSaved(snapshot
					.getFilterConditionRef());
			FilterConditionProcessVariableDAO
					.update(conDb, filterConfiguration);
		} catch (Exception ex) {
			CentralLogger.getInstance().fatal(this, "Fail to save dataset", ex);
			throw new AMSException("Fail to save dataset", ex);
		}

	}

}
