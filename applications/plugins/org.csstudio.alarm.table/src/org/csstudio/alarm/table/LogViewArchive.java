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

import org.csstudio.alarm.dbaccess.ArchiveDBAccess;
import org.csstudio.alarm.dbaccess.archivedb.ILogMessageArchiveAccess;
import org.csstudio.alarm.table.dataModel.JMSMessageList;
import org.csstudio.alarm.table.expertSearch.ExpertSearchDialog;
import org.csstudio.alarm.table.logTable.JMSLogTableViewer;
import org.csstudio.alarm.table.preferences.LogArchiveViewerPreferenceConstants;
import org.csstudio.alarm.table.preferences.LogViewerPreferenceConstants;
import org.csstudio.alarm.table.internal.localization.Messages;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.util.time.StartEndTimeParser;
import org.csstudio.util.time.swt.StartEndDialog;

import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;


/**
 * Simple view more like console, used to write log messages
 */
public class LogViewArchive extends ViewPart {

	public static final String ID = LogViewArchive.class.getName();

	private Shell parentShell = null;

	private JMSMessageList jmsml = null;

	private JMSLogTableViewer jlv = null;

	private String[] columnNames;

	private Text timeFrom;
	private Text timeTo;

	private Date fromTime;
	private Date toTime;
	
	private ColumnPropertyChangeListener cl;

	public void createPartControl(Composite parent) {

		columnNames = JmsLogsPlugin.getDefault().getPluginPreferences()
				.getString(LogArchiveViewerPreferenceConstants.P_STRINGArch)
				.split(";"); //$NON-NLS-1$
		jmsml = new JMSMessageList(columnNames);

		parentShell = parent.getShell();

		GridLayout grid = new GridLayout();
		grid.numColumns = 1;
		parent.setLayout(grid);
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1));
		comp.setLayout(new GridLayout(4, true));

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
		from.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		from.setLayout(new GridLayout(1, true));

		timeFrom = new Text(from, SWT.SINGLE);
		timeFrom.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,1,1));

		timeFrom.setEditable(false);
		timeFrom.setText("                            "); //$NON-NLS-1$
		Group to = new Group(comp, SWT.LINE_SOLID);
		to.setText(Messages.getString("LogViewArchive_to")); //$NON-NLS-1$
		to.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		to.setLayout(new GridLayout(1, true));

		timeTo = new Text(to, SWT.SINGLE);
		timeTo.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,1,1));
		timeTo.setEditable(false);
//		timeTo.setText("                              ");

		jlv = new JMSLogTableViewer(parent, getSite(), columnNames, jmsml, 3,SWT.SINGLE | SWT.FULL_SELECTION);
		jlv.setAlarmSorting(false);
		parent.pack();
		
		cl = new ColumnPropertyChangeListener(
				LogArchiveViewerPreferenceConstants.P_STRINGArch,
				jlv);
		
		JmsLogsPlugin.getDefault().getPluginPreferences()
				.addPropertyChangeListener(cl);
		
	}

	private void create72hButton(Composite comp) {
		Button b72hSearch = new Button(comp, SWT.PUSH);
		b72hSearch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));
		b72hSearch.setText(Messages.getString("LogViewArchive_3days")); //$NON-NLS-1$

		b72hSearch.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ILogMessageArchiveAccess adba = new ArchiveDBAccess();
				GregorianCalendar to = new GregorianCalendar();
				GregorianCalendar from = (GregorianCalendar) to.clone();
				from.add(GregorianCalendar.HOUR, -72);
				showNewTime(from, to);
				ArrayList<HashMap<String, String>> am = adba.getLogMessages(
						from, to);
				jmsml.clearList();
				jlv.refresh();
				jmsml.addJMSMessageList(am);
			}
		});
	}

	private void createWeekButton(Composite comp) {
		Button b168hSearch = new Button(comp, SWT.PUSH);
		b168hSearch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));
		b168hSearch.setText(Messages.getString("LogViewArchive_week")); //$NON-NLS-1$

		b168hSearch.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ILogMessageArchiveAccess adba = new ArchiveDBAccess();
				GregorianCalendar to = new GregorianCalendar();
				GregorianCalendar from = (GregorianCalendar) to.clone();
				from.add(GregorianCalendar.HOUR, -168);
				showNewTime(from, to);
				ArrayList<HashMap<String, String>> am = adba.getLogMessages(
						from, to);
				jmsml.clearList();
				jlv.refresh();
				jmsml.addJMSMessageList(am);
			}
		});

	}

	private void createFlexButton(Composite comp) {
		Button bFlexSearch = new Button(comp, SWT.PUSH);
		bFlexSearch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));
		bFlexSearch.setText(Messages.getString("LogViewArchive_user")); //$NON-NLS-1$

		bFlexSearch.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Date fromDate, toDate;
				if((fromDate = getFromTime())==null)
					fromDate = (new Date(new Date().getTime()-24*60*60*1000));

				if((toDate = getToTime())==null)
					toDate = (new Date(new Date().getTime()));

				ITimestamp start = TimestampFactory.now(); //new Timestamp(fromDate.getTime()/1000);
				ITimestamp end = TimestampFactory.now(); //new Timestamp((toDate.getTime()) / 1000);
				StartEndDialog dlg = new StartEndDialog(parentShell);
				if (dlg.open() == StartEndDialog.OK) {
					String lowString = dlg.getStartSpecification();
					String highString = dlg.getEndSpecification();
					try {
						StartEndTimeParser parser = new StartEndTimeParser(lowString, highString);
						Calendar from = parser.getStart();
						Calendar to = parser.getEnd();
//					double low = Double.parseDouble(lowString);
//					double high = Double.parseDouble(highString);
					ILogMessageArchiveAccess adba = new ArchiveDBAccess();
//					GregorianCalendar from = new GregorianCalendar();
//					GregorianCalendar to = new GregorianCalendar();
//					if (low < high) {
//						from.setTimeInMillis((long) low * 1000);
//						to.setTimeInMillis((long) high * 1000);
//					} else {
//						from.setTimeInMillis((long) high * 1000);
//						to.setTimeInMillis((long) low * 1000);
//					}
					showNewTime(from, to);
					ArrayList<HashMap<String, String>> am = adba.getLogMessages(from, to);
					jmsml.clearList();
					jlv.refresh();
					jmsml.addJMSMessageList(am);

					} catch (Exception e1) {
						// TODO Auto-generated catch block
						JmsLogsPlugin.logInfo(e1.getMessage());
					}

				}
			}
		});

	}

	private void createSearchButton(Composite comp) {
		Button bSearch = new Button(comp, SWT.PUSH);
		bSearch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));
		bSearch.setText(Messages.getString("LogViewArchive_expert")); //$NON-NLS-1$

		bSearch.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Date fromDate, toDate;
				if((fromDate = getFromTime())==null)
					fromDate = (new Date(new Date().getTime()-24*60*60*1000));

				if((toDate = getToTime())==null)
					toDate = (new Date(new Date().getTime()));

				ITimestamp start = TimestampFactory.now(); //new Timestamp(fromDate.getTime()/1000);
				ITimestamp end = TimestampFactory.now(); //new Timestamp((toDate.getTime()) / 1000);

				ExpertSearchDialog dlg = new ExpertSearchDialog(parentShell, start, end);
				String filter= ""; //$NON-NLS-1$
				GregorianCalendar to = new GregorianCalendar();
				GregorianCalendar from = (GregorianCalendar) to.clone();
				if (dlg.open() == ExpertSearchDialog.OK) {
					double low = dlg.getStart().toDouble();
					double high = dlg.getEnd().toDouble();
					if (low < high) {
						from.setTimeInMillis((long) low * 1000);
						to.setTimeInMillis((long) high * 1000);
					} else {
						from.setTimeInMillis((long) high * 1000);
						to.setTimeInMillis((long) low * 1000);
					}
					showNewTime(from, to);

					filter = dlg.getFilterString();
				}
				ILogMessageArchiveAccess adba = new ArchiveDBAccess();
//				from.add(GregorianCalendar.HOUR, -504);
				showNewTime(from, to);
				ArrayList<HashMap<String, String>> am;
				if(filter.trim().length()>0){
					am = adba.getLogMessages(from, to, filter);
				}
				else{
					am = adba.getLogMessages(from, to);
				}
				jmsml.clearList();
				jlv.refresh();
				jmsml.addJMSMessageList(am);

			}
		});
	}


	private void create24hButton(Composite comp) {
		Button b24hSearch = new Button(comp, SWT.PUSH);
		b24hSearch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));
		b24hSearch.setText(Messages.getString("LogViewArchive_day")); //$NON-NLS-1$

		b24hSearch.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ILogMessageArchiveAccess adba = new ArchiveDBAccess();
				GregorianCalendar to = new GregorianCalendar();
				GregorianCalendar from = (GregorianCalendar) to.clone();
				from.add(GregorianCalendar.HOUR, -24);
				showNewTime(from, to);
				ArrayList<HashMap<String, String>> am = adba.getLogMessages(
						from, to);
				jmsml.clearList();
				jlv.refresh();
				jmsml.addJMSMessageList(am);

			}
		});

	}

	private void showNewTime(Calendar from, Calendar to) {
		SimpleDateFormat sdf = new SimpleDateFormat();
		try{
			sdf.applyPattern(JmsLogsPlugin.getDefault().getPreferenceStore().getString(LogArchiveViewerPreferenceConstants.DATE_FORMAT));
		}catch(Exception e){
			sdf.applyPattern(JmsLogsPlugin.getDefault().getPreferenceStore().getDefaultString(LogArchiveViewerPreferenceConstants.DATE_FORMAT));
			JmsLogsPlugin.getDefault().getPreferenceStore().setToDefault(LogArchiveViewerPreferenceConstants.DATE_FORMAT);
		}
		timeFrom.setText(sdf.format(from.getTime()));
		fromTime = from.getTime();
		timeTo.setText(sdf.format(to.getTime()));
		timeFrom.getParent().getParent().redraw();
		toTime = to.getTime();

	}

	public void setFocus() {
	}

	public void dispose() {
		super.dispose();
		JmsLogsPlugin.getDefault().getPluginPreferences()
				.removePropertyChangeListener(cl);
	}


	public Date getFromTime(){
		return fromTime;

	}
	public Date getToTime(){
		return toTime;

	}
}