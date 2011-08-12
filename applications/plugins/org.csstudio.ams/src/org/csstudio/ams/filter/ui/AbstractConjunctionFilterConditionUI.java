
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
import java.util.LinkedList;
import java.util.List;
import org.csstudio.ams.AMSException;
import org.csstudio.ams.CycleDetectionUtil;
import org.csstudio.ams.Log;
import org.csstudio.ams.Messages;
import org.csstudio.ams.dbAccess.AmsConnectionFactory;
import org.csstudio.ams.dbAccess.configdb.CommonConjunctionFilterConditionDAO;
import org.csstudio.ams.dbAccess.configdb.CommonConjunctionFilterConditionTObject;
import org.csstudio.ams.dbAccess.configdb.FilterConditionDAO;
import org.csstudio.ams.dbAccess.configdb.FilterConditionProcessVariableTObject;
import org.csstudio.ams.dbAccess.configdb.FilterConditionTObject;
import org.csstudio.ams.filter.FilterConditionProcessVariable;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * This class can be used as the superclass for a conjuncted {@link IFilterConditionUI}.
 * A conjuncted FilterCondition can be a 'OR'- or a 'AND'-FilterCondition 
 * @author C1 WPS / KM, MZ
 *
 */
public abstract class AbstractConjunctionFilterConditionUI extends
		FilterConditionUI {

	private Composite localParent;
	private Combo cboFirstFC;
	private Combo cboSecondFC;

	/**
	 * The last created/loaded {@link FilterConditionProcessVariable}.
	 */
	private CommonConjunctionFilterConditionTObject snapshot = null;

	/**
	 * Constructor.
	 */
	public AbstractConjunctionFilterConditionUI() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public boolean check() {
		List<String> errors = new LinkedList<String>();

		ComboWidgetIdDataItem firstComboBoxItemUI = (ComboWidgetIdDataItem) getSelectedComboBoxItemUI(
				cboFirstFC.getDisplay(), cboFirstFC);
		if (firstComboBoxItemUI == null || firstComboBoxItemUI.getID() < 0) {
			errors
					.add(Messages.FilterConditionConjunctionUI_Error_MissingFirstCondition);
		}

		ComboWidgetIdDataItem secondComboBoxItemUI = (ComboWidgetIdDataItem) getSelectedComboBoxItemUI(
				cboSecondFC.getDisplay(), cboSecondFC);
		if (secondComboBoxItemUI == null || secondComboBoxItemUI.getID() < 0) {
			errors
					.add(Messages.FilterConditionConjunctionUI_Error_MissingSecondCondition);
		}

		if (!errors.isEmpty()) {
			showMessageDialog(null, errors,
					Messages.FilterConditionConjunctionUI_Error_Dialog_Title,
					SWT.ICON_ERROR);
		}

		return errors.isEmpty() && validateConfigurationAndInformUser(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void create(Connection conDb, int filterConditionID)
			throws AMSException {
		try {
			assert check() : "Precondition violated: check()";

			snapshot = collectDataToBeSaved(filterConditionID);
			CommonConjunctionFilterConditionDAO.insert(conDb, snapshot);
		} catch (Exception ex) {
			Log.log(this, Log.FATAL, "Fail to create new dataset", ex);
			throw new AMSException("Fail to create new dataset", ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void createUI(Composite parent) {
		if (localParent == null) {
			localParent = new Composite(parent, SWT.NONE);

			GridLayout gridLayout = new GridLayout(2, false);
			gridLayout.horizontalSpacing = 10;
			gridLayout.verticalSpacing = 5;
			gridLayout.marginLeft = 10;
			gridLayout.marginRight = 10;
			gridLayout.marginTop = 10;

			localParent.setLayout(gridLayout);

			Label lblFirstFC = new Label(localParent, SWT.NONE);
			lblFirstFC.setLayoutData(getGridData(-1, -1, 1, 1, SWT.BEGINNING,
					SWT.CENTER, false, true));
			lblFirstFC
					.setText(Messages.FilterConditionConjunction_LABEL_FIRST_OPERAND);

			cboFirstFC = new Combo(localParent, SWT.BORDER | SWT.SINGLE
					| SWT.READ_ONLY);
			cboFirstFC.setLayoutData(getGridData(200, -1, 1, 1, SWT.BEGINNING,
					SWT.CENTER, false, false));

			Label lblSecondFC = new Label(localParent, SWT.NONE);
			lblSecondFC.setLayoutData(getGridData(-1, -1, 1, 1, SWT.BEGINNING,
					SWT.CENTER, false, true));
			lblSecondFC
					.setText(Messages.FilterConditionConjunction_LABEL_SECOND_OPERAND);

			cboSecondFC = new Combo(localParent, SWT.BORDER | SWT.SINGLE
					| SWT.READ_ONLY);
			cboSecondFC.setLayoutData(getGridData(200, -1, 1, 1, SWT.BEGINNING,
					SWT.CENTER, false, false));

			updateComboWidgests();

			new Label(localParent, SWT.NONE);

			Button butCheckConfiguration = new Button(localParent, SWT.PUSH);
			butCheckConfiguration.setLayoutData(getGridData(200, -1, 1, 1,
					SWT.BEGINNING, SWT.CENTER, false, false));
			butCheckConfiguration
					.setText(Messages.FilterConditionConjunction_CHECK_BUTTON_TEXT);
			butCheckConfiguration.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					validateConfigurationAndInformUser(false);
				}
			});
		}
	}

	/**
	 * Checks the configuration for mistakes like cycles.
	 * 
	 * @return {@code true} if configuration is valid, {@code false} otherwise.
	 */
	synchronized boolean validateConfigurationAndInformUser(final boolean onCheck) {
		boolean valid = false;
		try {
			final Connection conDb = AmsConnectionFactory.getConfigurationDB();
			List<Integer> refList = new LinkedList<Integer>();
			if (snapshot != null) {
				refList.add(snapshot.getOwnFilterConditionReference());
			}
			final List<Integer> cycleReferencesToBeShownOnUI = new LinkedList<Integer>();
			ComboWidgetIdDataItem firstComboBoxItemUI = (ComboWidgetIdDataItem) getSelectedComboBoxItemUI(
					cboFirstFC.getDisplay(), cboFirstFC);
			int firstId = firstComboBoxItemUI.getID();
			int secondId = -1;
			valid = CycleDetectionUtil.isChildConditionValid(conDb, refList, firstId, cycleReferencesToBeShownOnUI);
			if (valid) {
				ComboWidgetIdDataItem secondComboBoxItemUI = (ComboWidgetIdDataItem) getSelectedComboBoxItemUI(
						cboSecondFC.getDisplay(), cboSecondFC);
				secondId = secondComboBoxItemUI.getID();
				valid = CycleDetectionUtil.isChildConditionValid(conDb, refList, secondId, cycleReferencesToBeShownOnUI);
			}
			if (valid) {
				if (!onCheck) {
					if (firstId==secondId) {
						Display.getDefault().asyncExec(new Runnable() {
							@Override
                            public void run() {
								showMessageDialog(cboFirstFC.getShell(),
										Messages.FilterConditionConjunctionUI_Warn_Conditions_Equal,
										Messages.FilterConditionConjunctionUI_Warn_Dialog_Title,
										SWT.ICON_INFORMATION);	
							}
						});
					} else {
						Display.getDefault().asyncExec(new Runnable() {
							@Override
                            public void run() {
								showMessageDialog(cboFirstFC.getShell(),
										Messages.FilterConditionConjunctionUI_No_Error_Dialog_Message,
										Messages.FilterConditionConjunctionUI_No_Error_Dialog_Title,
										SWT.ICON_INFORMATION);	
							}
						});	
					}
				}
			} else {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
                    public void run() {
						String messageText = NLS.bind(Messages.FilterConditionConjunctionUI_Error_Cycle_Detected, CycleDetectionUtil.createCycleDetectionMessage(conDb, cycleReferencesToBeShownOnUI));
						if (onCheck) {
							messageText = Messages.FilterConditionConjunctionUI_Error_Not_Saved + messageText;
						}
						showMessageDialog(
								cboFirstFC.getShell(),
								messageText,
								Messages.FilterConditionConjunctionUI_Error_Dialog_Title,
								SWT.ICON_WARNING);
					}
				});
			}
		} catch (final SQLException e) {
			e.printStackTrace();
			valid = false;
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					showMessageDialog(cboFirstFC.getShell(),
							NLS.bind(Messages.FilterConditionConjunctionUI_Error_Reading_Database, e),
							Messages.FilterConditionConjunctionUI_Error_Dialog_Title,
							SWT.ICON_WARNING);	
				}
			});
		}
		return valid;
	}

	/**
	 * Refreshes the entries within the used {@link Combo}s.
	 */
	private void updateComboWidgests() {
		if (cboFirstFC == null || cboSecondFC == null) {
			return;
		}

		List<ComboWidgetIdDataItem> filterConditionEntries = this
				.createComboBoxFilterConditionEntries();
		initComboBoxUI(cboFirstFC.getDisplay(), cboFirstFC,
				filterConditionEntries);
		initComboBoxUI(cboSecondFC.getDisplay(), cboSecondFC,
				filterConditionEntries);
	}

	/**
	 * Creates the entries for the {@link Combo}s.
	 * @return A list of {@link ComboWidgetIdDataItem}s
	 */
	private List<ComboWidgetIdDataItem> createComboBoxFilterConditionEntries() {
		List<ComboWidgetIdDataItem> result = new LinkedList<ComboWidgetIdDataItem>();
		try {
			Connection con = AmsConnectionFactory.getConfigurationDB();
			List<FilterConditionTObject> list = FilterConditionDAO
					.selectList(con);
			for (FilterConditionTObject current : list) {
				if (snapshot == null
						|| current.getFilterConditionID() != snapshot
								.getOwnFilterConditionReference()) {
					result.add(new ComboWidgetIdDataItem(current
							.getFilterConditionID(), current.getName()));
				}
			}
		} catch (Exception ex) {
			Log.log(Log.FATAL, ex);
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void delete(Connection conDb, int filterConditionID)
			throws AMSException {
		try {
			CommonConjunctionFilterConditionDAO
					.remove(conDb, filterConditionID);
		} catch (Exception ex) {
			Log.log(this, Log.FATAL, "Could not remove FilterCondition with reference: "
							+ filterConditionID, ex);
			throw new AMSException(
					"Could not remove FilterCondition with reference: "
							+ filterConditionID, ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
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
	public boolean isChanged() {
		CommonConjunctionFilterConditionTObject newFC = collectDataToBeSaved(snapshot
				.getOwnFilterConditionReference());
		return !newFC.equals(snapshot);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void load(Connection conDb, int filterConditionID)
			throws AMSException {
		snapshot = null;
		try {
			snapshot = CommonConjunctionFilterConditionDAO.select(conDb,
					filterConditionID);
		} catch (SQLException e) {
			Log.log(this, Log.FATAL, "Can not load filter details!", e);
			throw new AMSException("Can not load filter details!", e);
		}

		if (snapshot != null) {
			updateComboWidgests();

			setComboBoxValueUI(cboFirstFC.getDisplay(), cboFirstFC, snapshot
					.getFirstFilterConditionReference());
			setComboBoxValueUI(cboSecondFC.getDisplay(), cboSecondFC, snapshot
					.getSecondFilterConditionReference());
		} else {
			reset();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void reset() {
		setComboBoxValueUI(cboFirstFC.getDisplay(), cboFirstFC, -1);
		setComboBoxValueUI(cboSecondFC.getDisplay(), cboSecondFC, -1);
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
	private CommonConjunctionFilterConditionTObject collectDataToBeSaved(
			int filterConditionID) {
		ComboWidgetIdDataItem firstComboBoxItemUI = (ComboWidgetIdDataItem) getSelectedComboBoxItemUI(
				cboFirstFC.getDisplay(), cboFirstFC);
		int firstId = firstComboBoxItemUI.getID();
		ComboWidgetIdDataItem secondComboBoxItemUI = (ComboWidgetIdDataItem) getSelectedComboBoxItemUI(
				cboSecondFC.getDisplay(), cboSecondFC);
		int secondId = secondComboBoxItemUI.getID();
		CommonConjunctionFilterConditionTObject filterConfiguration = new CommonConjunctionFilterConditionTObject(
				filterConditionID, firstId, secondId);
		return filterConfiguration;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void save(Connection conDb) throws AMSException {
		try {
			assert check() : "Precondition violated: check()";

			CommonConjunctionFilterConditionTObject filterConfiguration = collectDataToBeSaved(snapshot
					.getOwnFilterConditionReference());
			CommonConjunctionFilterConditionDAO.update(conDb,
					filterConfiguration);
		} catch (Exception ex) {
			Log.log(this, Log.FATAL, "Fail to save dataset", ex);
			throw new AMSException("Fail to save dataset", ex);
		}
	}

}