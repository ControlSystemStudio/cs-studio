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

package org.csstudio.alarm.table;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.csstudio.alarm.dbaccess.ArchiveDBAccess;
import org.csstudio.alarm.dbaccess.FilterItem;
import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.csstudio.alarm.table.dataModel.JMSMessageList;
import org.csstudio.alarm.table.expertSearch.ExpertSearchDialog;
import org.csstudio.alarm.table.internal.localization.Messages;
import org.csstudio.alarm.table.logTable.JMSLogTableViewer;
import org.csstudio.alarm.table.preferences.LogArchiveViewerPreferenceConstants;
import org.csstudio.alarm.table.preferences.LogViewerPreferenceConstants;
import org.csstudio.alarm.table.readDB.DBAnswer;
import org.csstudio.alarm.table.readDB.AccessDBJob;
import org.csstudio.apputil.time.StartEndTimeParser;
import org.csstudio.apputil.ui.time.StartEndDialog;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.model.IProcessVariable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

/**
 * View for message read from oracel DB.
 * 
 * @author jhatje
 * @author $Author$
 * @version $Revision$
 * @since 19.07.2007
 */
public class ViewArchive extends ViewPart implements Observer {

	/** The Id of this Object. */
	public static final String ID = ViewArchive.class.getName();

	/** The Parent Shell. */
	private Shell _parentShell = null;

	/** The JMS message list. */
	private JMSMessageList _jmsMessageList = null;

	/** The JMS Logtable Viewer. */
	private JMSLogTableViewer _jmsLogTableViewer = null;

	/** An Array whit the name of the Columns. */
	private String[] _columnNames;

	/** Textfield witch contain the "from time". */
	private Text _timeFrom;
	/** Textfield witch contain the "to time". */
	private Text _timeTo;

	/** The selectet "from time". */
	private ITimestamp _fromTime;
	/** The selectet "to time". */
	private ITimestamp _toTime;

	/** The column property change listener. */
	private ColumnPropertyChangeListener _columnPropertyChangeListener;

	/** The default / last filter. */
	private String _filter = ""; //$NON-NLS-1$

	/**
	 * The Answer from the DB.
	 */
	private DBAnswer _dbAnswer = null;

	/** The Display. */
	private Display _disp;

	/** The count of results. */
	private Label _countLabel;

	/**
	 * Current settings of the filter that they are available to delete the
	 * displayed messages.
	 */
	private GregorianCalendar _from;
	private GregorianCalendar _to;
	private ArrayList<FilterItem> _filterSettings;
	
	private AccessDBJob dbReader = new AccessDBJob("DBReader");

	/**
	 * The Show Property View action.
	 */
	private Action _showPropertyViewAction;

	/**
	 * The ID of the property view.
	 */
	private static final String PROPERTY_VIEW_ID = "org.eclipse.ui.views.PropertySheet";

	public ViewArchive() {
		super();
		_dbAnswer = new DBAnswer();
		_dbAnswer.addObserver(this);
	}

	/** {@inheritDoc} */
	public final void createPartControl(final Composite parent) {

		_disp = parent.getDisplay();

		_columnNames = JmsLogsPlugin.getDefault().getPluginPreferences()
				.getString(LogArchiveViewerPreferenceConstants.P_STRINGArch)
				.split(";"); //$NON-NLS-1$
		_jmsMessageList = new JMSMessageList(_columnNames);

		_parentShell = parent.getShell();

		GridLayout grid = new GridLayout();
		grid.numColumns = 1;
		parent.setLayout(grid);
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
		comp.setLayout(new GridLayout(5, false));

		Group buttons = new Group(comp, SWT.LINE_SOLID);
		buttons.setText(Messages.getString("LogViewArchive_period")); //$NON-NLS-1$
		buttons.setLayout(new GridLayout(5, true));
		GridData gd = new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1);
		gd.minimumHeight = 60;
		gd.minimumWidth = 300;
		buttons.setLayoutData(gd);

		create24hButton(buttons);
		create72hButton(buttons);
		createWeekButton(buttons);
		createFlexButton(buttons);
		createSearchButton(buttons);

		Group from = new Group(comp, SWT.LINE_SOLID);
		from.setText(Messages.getString("LogViewArchive_from")); //$NON-NLS-1$
		gd = new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1);
		gd.minimumHeight = 60;
		gd.minimumWidth = 150;
		from.setLayoutData(gd);
		from.setLayout(new GridLayout(1, true));

		_timeFrom = new Text(from, SWT.SINGLE);
		_timeFrom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));

		_timeFrom.setEditable(false);
		_timeFrom.setText("                            "); //$NON-NLS-1$
		Group to = new Group(comp, SWT.LINE_SOLID);
		to.setText(Messages.getString("LogViewArchive_to")); //$NON-NLS-1$
		gd = new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1);
		gd.minimumHeight = 60;
		gd.minimumWidth = 150;
		to.setLayoutData(gd);
		to.setLayout(new GridLayout(1, true));

		_timeTo = new Text(to, SWT.SINGLE);
		_timeTo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1,
				1));
		_timeTo.setEditable(false);
		// timeTo.setText("                              ");

		Group count = new Group(comp, SWT.LINE_SOLID);
		count.setText(Messages.getString("LogViewArchive_count")); //$NON-NLS-1$
		count.setLayout(new GridLayout(1, true));
		gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd.minimumHeight = 60;
		gd.minimumWidth = 75;
		count.setLayoutData(gd);

		Group deleteButtons = new Group(comp, SWT.LINE_SOLID);
		deleteButtons.setText("Delete Messages"); //$NON-NLS-1$
		deleteButtons.setLayout(new GridLayout(1, true));
		gd = new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1);
		gd.minimumHeight = 60;
		gd.minimumWidth = 60;
		deleteButtons.setLayoutData(gd);

		createDeleteButton(deleteButtons);

		
		_countLabel = new Label(count, SWT.RIGHT);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		_countLabel.setLayoutData(gd);
		_countLabel.setText("0"); //$NON-NLS-1$

		_jmsLogTableViewer = new JMSLogTableViewer(parent, getSite(),
				_columnNames, _jmsMessageList, 3, SWT.SINGLE
						| SWT.FULL_SELECTION);
		_jmsLogTableViewer.setAlarmSorting(false);

		getSite().setSelectionProvider(_jmsLogTableViewer);
		makeActions();
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());

		parent.pack();

		_columnPropertyChangeListener = new ColumnPropertyChangeListener(
				LogArchiveViewerPreferenceConstants.P_STRINGArch,
				_jmsLogTableViewer);

		JmsLogsPlugin.getDefault().getPluginPreferences()
				.addPropertyChangeListener(_columnPropertyChangeListener);

	}


	/**
	 * Button to delete messages currently displayed in the table.
	 * 	 * 
	 * @param comp
	 */
	private void createDeleteButton(Composite comp) {
		Button delete = new Button(comp, SWT.PUSH);
		delete.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));
		delete.setText("Delete");
		
		delete.addSelectionListener(new SelectionAdapter() {

			/**
			 * Set the 'delete' flag and use the current settings of the
			 * select statement to delete the messages. The command
			 * will delete all messages even if the table shows just
			 * a subset!!
			 */
			public void widgetSelected(final SelectionEvent e) {
				dbReader.setDeleteFlag(true);
				dbReader.schedule();
			}
		});
		
	}

	/**
	 * Creates the actions offered by this view.
	 */
	private void makeActions() {
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
	 * Adds the tool bar actions.
	 * 
	 * @param manager
	 *            the menu manager.
	 */
	private void fillLocalToolBar(final IToolBarManager manager) {
		manager.add(_showPropertyViewAction);
	}
	
	

	/**
	 * Create a Button to selet the last 24 hour.
	 * 
	 * @param comp
	 *            the parent Composite for the Button.
	 */
	private void create24hButton(final Composite comp) {
		Button b24hSearch = new Button(comp, SWT.PUSH);
		b24hSearch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));
		b24hSearch.setText(Messages.getString("LogViewArchive_day")); //$NON-NLS-1$

		b24hSearch.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(final SelectionEvent e) {
				// ILogMessageArchiveAccess adba = new ArchiveDBAccess();
				_to = new GregorianCalendar();
				_from = (GregorianCalendar) _to.clone();
				_from.add(GregorianCalendar.HOUR_OF_DAY, -24);
				callDBReadJob();
			}
		});

	}

	/**
	 * Create the a Button to selet the last 72 hour.
	 * 
	 * @param comp
	 *            the parent {@link Composite} for the Button.
	 */
	private void create72hButton(final Composite comp) {
		Button b72hSearch = new Button(comp, SWT.PUSH);
		b72hSearch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));
		b72hSearch.setText(Messages.getString("LogViewArchive_3days")); //$NON-NLS-1$

		b72hSearch.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				// ILogMessageArchiveAccess adba = new ArchiveDBAccess();
				_to = new GregorianCalendar();
				_from = (GregorianCalendar) _to.clone();
				_from.add(GregorianCalendar.HOUR_OF_DAY, -72);
				callDBReadJob();
			}
		});
	}

	/**
	 * Create a Button to selet the last week.
	 * 
	 * @param comp
	 *            the parent Composite for the Button.
	 */
	private void createWeekButton(final Composite comp) {
		Button b168hSearch = new Button(comp, SWT.PUSH);
		b168hSearch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));
		b168hSearch.setText(Messages.getString("LogViewArchive_week")); //$NON-NLS-1$

		b168hSearch.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				_to = new GregorianCalendar();
				_from = (GregorianCalendar) _to.clone();
				_from.add(GregorianCalendar.HOUR_OF_DAY, -168);
				callDBReadJob();
			}

		});

	}

	/**
	 * Create a Button that open a dialog to select required period.
	 * 
	 * @param comp
	 *            the parent Composite for the Button.
	 */
	private void createFlexButton(final Composite comp) {
		Button bFlexSearch = new Button(comp, SWT.PUSH);
		bFlexSearch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));
		bFlexSearch.setText(Messages.getString("LogViewArchive_user")); //$NON-NLS-1$

		bFlexSearch.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				StartEndDialog dlg;
				if (_fromTime != null && _toTime != null) {
					dlg = new StartEndDialog(_parentShell,
							_fromTime.toString(), _toTime.toString());
				} else {
					dlg = new StartEndDialog(_parentShell);
				}
				if (dlg.open() == StartEndDialog.OK) {
					String lowString = dlg.getStartSpecification();
					String highString = dlg.getEndSpecification();
					try {
						StartEndTimeParser parser = new StartEndTimeParser(
								lowString, highString);
						_from = (GregorianCalendar) parser.getStart();
						_fromTime = TimestampFactory.fromCalendar(_from);
						_to = (GregorianCalendar) parser.getEnd();
						_toTime = TimestampFactory.fromCalendar(_to);
						callDBReadJob();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						JmsLogsPlugin.logInfo(e1.getMessage());
					}

				}
			}
		});

	}

	/**
	 * Create a Button that open a dialog to select required period and define
	 * filters.
	 * 
	 * @param comp
	 *            the parent Composite for the Button.
	 */
	private void createSearchButton(final Composite comp) {
		Button bSearch = new Button(comp, SWT.PUSH);
		bSearch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1,
				1));
		bSearch.setText(Messages.getString("LogViewArchive_expert")); //$NON-NLS-1$

		bSearch.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(final SelectionEvent e) {
				if (_fromTime == null) {
					ITimestamp now = TimestampFactory.now();
					_fromTime = TimestampFactory.createTimestamp(now.seconds()
							- (24 * 60 * 60), now.nanoseconds()); // new
					// Timestamp(fromDate.getTime()/1000);
				}
				if (_toTime == null) {
					_toTime = TimestampFactory.now();
				}

				ExpertSearchDialog dlg = new ExpertSearchDialog(_parentShell,
						_fromTime, _toTime, _filter);

				_to = new GregorianCalendar();
				_from = (GregorianCalendar) _to.clone();
				if (dlg.open() == ExpertSearchDialog.OK) {
					_fromTime = dlg.getStart();
					_toTime = dlg.getEnd();
					double low = _fromTime.toDouble();
					double high = _toTime.toDouble();
					if (low < high) {
						_from.setTimeInMillis((long) low * 1000);
						_to.setTimeInMillis((long) high * 1000);
					} else {
						_from.setTimeInMillis((long) high * 1000);
						_to.setTimeInMillis((long) low * 1000);
					}
					_filter = dlg.getFilterString();
					callDBReadJob();
				}
			}

		});
	}

	public void readDBFromExternalCall(IProcessVariable pv) {
		_from = new GregorianCalendar();
		_to = new GregorianCalendar();
		_from.setTimeInMillis(_to.getTimeInMillis() - 1000 * 60 * 60 * 24);
		showNewTime(_from, _to);
		_filterSettings = new ArrayList<FilterItem>();
		_filterSettings.add(new FilterItem("name", pv.getName(), "END"));
		callDBReadJob();
	}

	private void callDBReadJob() {
		showNewTime(_from, _to);
		dbReader.setReadProperties(ViewArchive.this._dbAnswer, _from, _to, _filterSettings);
		_countLabel.setText("*");
		_jmsLogTableViewer.getTable().setEnabled(false);
		dbReader.schedule();

	}

	/**
	 * Set the two times from, to .
	 * 
	 * @param from
	 * @param to
	 */
	private void showNewTime(final Calendar from, final Calendar to) {
		SimpleDateFormat sdf = new SimpleDateFormat();
		try {
			sdf
					.applyPattern(JmsLogsPlugin
							.getDefault()
							.getPreferenceStore()
							.getString(
									LogArchiveViewerPreferenceConstants.DATE_FORMAT));
		} catch (Exception e) {
			sdf.applyPattern(JmsLogsPlugin.getDefault().getPreferenceStore()
					.getDefaultString(
							LogArchiveViewerPreferenceConstants.DATE_FORMAT));
			JmsLogsPlugin.getDefault().getPreferenceStore().setToDefault(
					LogArchiveViewerPreferenceConstants.DATE_FORMAT);
		}
		_timeFrom.setText(sdf.format(from.getTime()));
		_fromTime = TimestampFactory.fromCalendar(from);

		_timeTo.setText(sdf.format(to.getTime()));
		_toTime = TimestampFactory.fromCalendar(to);
		// redraw
		_timeFrom.getParent().getParent().redraw();
	}

	/** {@inheritDoc} */
	@Override
	public void setFocus() {
	}

	/** {@inheritDoc} */
	@Override
	public final void dispose() {
		super.dispose();
		ArchiveDBAccess.getInstance().close();
		JmsLogsPlugin.getDefault().getPluginPreferences()
				.removePropertyChangeListener(_columnPropertyChangeListener);
	}

	/** @return get the from Time. */
	public final Date getFromTime() {
		return _fromTime.toCalendar().getTime();

	}

	/** @return get the to Time. */
	public final Date getToTime() {
		return _toTime.toCalendar().getTime();

	}

	/**
	 * When dispose store the width for each column.
	 */
	public void saveColumn() {
		int[] width = _jmsLogTableViewer.getColumnWidth();
		String newPreferenceColumnString = ""; //$NON-NLS-1$
		String[] columns = JmsLogsPlugin.getDefault().getPluginPreferences()
				.getString(LogArchiveViewerPreferenceConstants.P_STRINGArch)
				.split(";"); //$NON-NLS-1$
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
		store.setValue(LogArchiveViewerPreferenceConstants.P_STRINGArch,
				newPreferenceColumnString);
		if (store.needsSaving()) {
			JmsLogsPlugin.getDefault().savePluginPreferences();
		}
	}

	public void update(Observable arg0, Object arg1) {
		_disp.syncExec(new Runnable() {
			public void run() {
				_jmsMessageList.clearList();
				_jmsLogTableViewer.refresh();
				ArrayList<HashMap<String, String>> answer = _dbAnswer
						.getDBAnswer();
				int size = answer.size();
				if (_dbAnswer.is_maxSize()) {
					_countLabel.setBackground(Display.getCurrent()
							.getSystemColor(SWT.COLOR_RED));
					_countLabel.setText(Integer.toString(size) + "(maximum)");
				} else {
					_countLabel.setText(Integer.toString(size));
					_countLabel.setBackground(Display.getCurrent()
							.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
				}
				_jmsLogTableViewer.getTable().setEnabled(true);
				if (size > 0) {
					_jmsMessageList.addJMSMessageList(answer);
				} else {
					String[] propertyNames = JmsLogsPlugin.getDefault()
							.getPluginPreferences().getString(
									LogViewerPreferenceConstants.P_STRING)
							.split(";"); //$NON-NLS-1$

					JMSMessage jmsMessage = new JMSMessage(propertyNames);
					String firstColumnName = _columnNames[0];
					jmsMessage.setProperty(firstColumnName,
							Messages.LogViewArchive_NoMessageInDB);
					_jmsMessageList.addJMSMessage(jmsMessage);
				}

			}
		});
	}

}
