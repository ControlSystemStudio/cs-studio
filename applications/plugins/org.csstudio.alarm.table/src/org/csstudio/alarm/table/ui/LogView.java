/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

package org.csstudio.alarm.table.ui;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;

import org.csstudio.alarm.table.ColumnPropertyChangeListener;
import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.dataModel.JMSLogMessageList;
import org.csstudio.alarm.table.dataModel.JMSMessageList;
import org.csstudio.alarm.table.jms.JmsMessageReceiver;
import org.csstudio.alarm.table.logTable.JMSLogTableViewer;
import org.csstudio.alarm.table.preferences.LogViewPreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

/**
 * View with table for all log messages from JMS. Creates the TableViewer
 * <code>JMSLogTableViewer</code>, holds the model <code>JMSMessageList</code>
 * 
 * @author jhatje
 * 
 */
public class LogView extends ViewPart {

	public static final String ID = LogView.class.getName();

	public JMSMessageList _messageList = null;

	public JMSLogTableViewer _tableViewer = null;

	public String[] _columnNames;

	public ColumnPropertyChangeListener _propertyChangeListener;

	/**
	 * Action to call property view.
	 */
	private Action _showPropertyViewAction;

	/**
	 * The ID of the property view.
	 */
	private static final String PROPERTY_VIEW_ID = "org.eclipse.ui.views.PropertySheet";

	/**
	 * List of topic sets and names from preferences. Displayed in combo box.
	 */
	private HashMap<String, String> _topicListAndName;

	/**
	 * Default topic set. Try to read state 1. From previous viewPart data 2.
	 * From default marker in preferences 3. Take first set from preferences
	 */
	String _defaultTopicSet;

	JmsMessageReceiver _jmsMessageReceiver;

	public void createPartControl(Composite parent) {
		_columnNames = JmsLogsPlugin.getDefault().getPluginPreferences()
				.getString(LogViewPreferenceConstants.P_STRING).split(";"); //$NON-NLS-1$
		_messageList = new JMSLogMessageList(_columnNames);

		readPreferenceTopics(JmsLogsPlugin.getDefault().getPluginPreferences()
				.getString(LogViewPreferenceConstants.TOPIC_SET));

		GridLayout grid = new GridLayout();
		grid.numColumns = 1;
		parent.setLayout(grid);

		Composite logTableManagementComposite = new Composite(parent, SWT.NONE);
		RowLayout layout = new RowLayout();
		layout.type = SWT.HORIZONTAL;
		layout.spacing = 8;
		logTableManagementComposite.setLayout(layout);

		addJmsTopicItems(logTableManagementComposite);

		addRunningSinceGroup(logTableManagementComposite);

		_tableViewer = new JMSLogTableViewer(parent, getSite(), _columnNames,
				_messageList, 1, SWT.MULTI | SWT.FULL_SELECTION);
		_tableViewer.setAlarmSorting(false);
		parent.pack();

		getSite().setSelectionProvider(_tableViewer);

		makeActions();
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());

		_propertyChangeListener = new ColumnPropertyChangeListener(
				LogViewPreferenceConstants.P_STRING, _tableViewer);

		JmsLogsPlugin.getDefault().getPluginPreferences()
				.addPropertyChangeListener(_propertyChangeListener);
		
		_jmsMessageReceiver = new JmsMessageReceiver(_messageList);

		_jmsMessageReceiver.initializeJMSConnection(_defaultTopicSet);
	}

	/**
	 * Parse string from properties with jms topics settings and store items in
	 * a HashMap for combo box. Each set of topic items is separated with ';'. A
	 * topic item consists of three parts separated by '?';
	 * 'default?jmsTopic1,jmsTopic2?nameOfThisTopicSet' If the topic item is not
	 * default the first part is empty: '?jmsTopic1jms...'. If there is no
	 * default the first item is taken. The default tag from the preferences is
	 * overwritten if there is a topic set from a previous session.
	 * 
	 * @param topics
	 *            raw topic string from preferences
	 * @return set of topics for initialization
	 */
	void readPreferenceTopics(String topics) {
		_topicListAndName = new HashMap<String, String>();
		String[] topicSetsAndNames = topics.split(";");
		for (String topicSet : topicSetsAndNames) {
			String[] topicSetItems = topicSet.split("\\?");
			//preference string for topic set is invalid -> next one
			if (topicSetItems.length < 3) {
				continue;
			}
			_topicListAndName.put(topicSetItems[2], topicSetItems[1]);
			if ((_defaultTopicSet == null)
					|| (topicSetItems[0].equals("default"))
					|| (_defaultTopicSet.equals(topicSetItems[1]))) {
				_defaultTopicSet = topicSetItems[1];
			}
		}
		return;
	}

	/**
	 * Add label with date and time the table is started.
	 * 
	 * @param logTableManagementComposite
	 */
	void addRunningSinceGroup(Composite logTableManagementComposite) {
		Group runningSinceGroup = new Group(logTableManagementComposite,
				SWT.NONE);

		runningSinceGroup.setText("Running since");

		RowLayout layout = new RowLayout();
		runningSinceGroup.setLayout(layout);

		GregorianCalendar currentTime = new GregorianCalendar(TimeZone
				.getTimeZone("ECT"));
		SimpleDateFormat formater = new SimpleDateFormat();
		Label runningSinceLabel = new Label(runningSinceGroup, SWT.CENTER);
		runningSinceLabel.setLayoutData(new RowData(90, 21));
		runningSinceLabel.setText(formater.format(currentTime.getTime()));
	}

	/**
	 * Add combo box to select set of topics to be monitored. The items in the
	 * combo box are names that are mapped in the preferences to sets of topics.
	 * 
	 * @param logTableManagementComposite
	 */
	void addJmsTopicItems(Composite logTableManagementComposite) {
		Group jmsTopicItemsGroup = new Group(logTableManagementComposite,
				SWT.NONE);

		jmsTopicItemsGroup.setText("Monitored JMS topics");

		RowLayout layout = new RowLayout();
		layout.type = SWT.HORIZONTAL;
		layout.spacing = 5;
		jmsTopicItemsGroup.setLayout(layout);

		final Combo topicSetsCombo = new Combo(jmsTopicItemsGroup, SWT.SINGLE);
		int i = 0;

		for (String topicSetName : _topicListAndName.keySet()) {
			topicSetsCombo.add(topicSetName);
			if (_defaultTopicSet.equals(_topicListAndName.get(topicSetName))) {
				topicSetsCombo.select(i);
			}
			i++;
		}
		topicSetsCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				_defaultTopicSet = _topicListAndName.get(topicSetsCombo
						.getItem(topicSetsCombo.getSelectionIndex()));
				_jmsMessageReceiver.initializeJMSConnection(_defaultTopicSet);
			}
		});
	}



	/**
	 * Creates action to call property view.
	 */
	void makeActions() {
		_showPropertyViewAction = new Action() {
			@Override
			public void run() {
				try {
					getSite().getPage().showView(PROPERTY_VIEW_ID);
				} catch (PartInitException e) {
					MessageDialog.openError(getSite().getShell(), "Alarm Tree",
							e.getMessage());
				}
			}
		};
		_showPropertyViewAction.setText("Properties");
		_showPropertyViewAction.setToolTipText("Show property view");

		IViewRegistry viewRegistry = getSite().getWorkbenchWindow()
				.getWorkbench().getViewRegistry();
		IViewDescriptor viewDesc = viewRegistry.find(PROPERTY_VIEW_ID);
		_showPropertyViewAction.setImageDescriptor(viewDesc
				.getImageDescriptor());
	}

	/**
	 * Add 'show property action' to tool.
	 * 
	 * @param manager
	 *            the menu manager.
	 */
	void fillLocalToolBar(final IToolBarManager manager) {
		manager.add(_showPropertyViewAction);
	}

	public void setFocus() {
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		if (memento == null) {
			return;
		}
			_defaultTopicSet = memento.getString("previousTopicSet");
		if (_defaultTopicSet == null) {
			CentralLogger.getInstance().debug(this,
					"No topic set from previous session");
		} else {
			CentralLogger.getInstance().debug(this,
					"Get topic set from previous session: " + _defaultTopicSet);
		}
	}

	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		if ((memento != null) && (_defaultTopicSet != null)) {
			memento.putString("previousTopicSet", _defaultTopicSet);
		}
	}

	public void dispose() {
		saveColumn();
		_tableViewer = null;
		JmsLogsPlugin.getDefault().getPluginPreferences()
				.removePropertyChangeListener(_propertyChangeListener);
		super.dispose();
	}

	/**
	 * When dispose store the width for each column.
	 */
	public void saveColumn() {
		int[] width = _tableViewer.getColumnWidth();
		String newPreferenceColumnString = ""; //$NON-NLS-1$
		String[] columns = JmsLogsPlugin.getDefault().getPluginPreferences()
				.getString(LogViewPreferenceConstants.P_STRING).split(";"); //$NON-NLS-1$
		if (width.length != columns.length) {
			return;
		}
		for (int i = 0; i < columns.length; i++) {
			newPreferenceColumnString = newPreferenceColumnString
					.concat(columns[i].split(",")[0] + "," + width[i] + ";"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		newPreferenceColumnString = newPreferenceColumnString.substring(0,
				newPreferenceColumnString.length() - 1);
		IPreferenceStore store = JmsLogsPlugin.getDefault()
				.getPreferenceStore();
		store.setValue(LogViewPreferenceConstants.P_STRING,
				newPreferenceColumnString);
		if (store.needsSaving()) {
			JmsLogsPlugin.getDefault().savePluginPreferences();
		}
	}
}
