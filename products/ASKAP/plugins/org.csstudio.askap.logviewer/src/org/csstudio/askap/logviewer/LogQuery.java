package org.csstudio.askap.logviewer;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.askap.logviewer.util.LogQueryDataModel;
import org.csstudio.askap.utility.AskapHelper;
import org.csstudio.askap.utility.icemanager.LogObject.LogQueryObject;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class LogQuery extends ViewPart {
	
	private static final Logger logger = Logger.getLogger(LogQuery.class.getName());
	
	public static final String ID = "org.csstudio.askap.logviewer.LogQuery";
	private static final int NUM_OF_COLUMNS = 2;

	private LogQueryObject query = new LogQueryObject();

	public LogQuery() {
	}

	@Override
	public void createPartControl(final Composite parent) {
		final Composite page = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(NUM_OF_COLUMNS, false);
		gridLayout.horizontalSpacing = 20;
		page.setLayout(gridLayout);
		
		Label originLabel = new Label(page, SWT.NONE);
		originLabel.setText("Origin");		
		final Text originText = new Text(page, SWT.NONE);
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;	
		gridData1.grabExcessHorizontalSpace = true;
		originText.setLayoutData(gridData1);	
		
		Label hostnameLabel = new Label(page, SWT.NONE);
		hostnameLabel.setText("Host Name");
		final Text hostText = new Text(page, SWT.NONE);
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;	
		gridData2.grabExcessHorizontalSpace = true;
		hostText.setLayoutData(gridData2);

		Label tagLabel = new Label(page, SWT.NONE);
		tagLabel.setText("Tag");
		final Text tagText = new Text(page, SWT.NONE);
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = GridData.FILL;	
		gridData4.grabExcessHorizontalSpace = true;
		tagText.setLayoutData(gridData4);
		
	    Label logLabel = new Label(page, SWT.NONE);
		logLabel.setText("Log Levels");
	    GridData gridData = new GridData();
		gridData.verticalSpan=Preferences.LOG_LEVELS.length;
		logLabel.setLayoutData(gridData);

	    final List logLevels = new List(page, SWT.BORDER | SWT.MULTI);	    
	    logLevels.setItems(Preferences.LOG_LEVELS);
	    GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.FILL;	
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.verticalSpan=Preferences.LOG_LEVELS.length;
		logLevels.setLayoutData(gridData3);
		logLevels.selectAll();
		
		Label dateRangeLabel = new Label(page, SWT.NONE);
		dateRangeLabel.setText("Date range");
		
		Group dateRangeGroup = new Group(page, SWT.SHADOW_OUT);
		Layout dateLayout = new GridLayout(2, false);
		dateRangeGroup.setLayout(dateLayout);
		
		final Button enableDate = new Button(dateRangeGroup, SWT.CHECK | SWT.MULTI | SWT.BORDER);
		enableDate.setText("Specify date range\n(if disabled date range will not be used in the query)");
		GridData g10 = new GridData();
		g10.horizontalSpan=2;
		enableDate.setLayoutData(g10);

		Label minDateLabel = new Label(dateRangeGroup, SWT.LEFT);
		minDateLabel.setText("Minimium Date Time: ");
		GridData g11 = new GridData();
		g11.horizontalAlignment = GridData.FILL;	
		g11.horizontalSpan=2;
		minDateLabel.setLayoutData(g11);		
		
	    final DateTime minDate = new DateTime (dateRangeGroup, SWT.DATE | SWT.MEDIUM | SWT.BORDER);
	    final DateTime minTime = new DateTime (dateRangeGroup, SWT.TIME  | SWT.MEDIUM | SWT.BORDER);
	    int dateFields[]= AskapHelper.getDate(System.currentTimeMillis());
		minDate.setDate(dateFields[0], dateFields[1], dateFields[2]);
		minTime.setTime(dateFields[3]-1, dateFields[4], dateFields[5]);

	    Label maxDateLabel = new Label(dateRangeGroup, SWT.LEFT);
		maxDateLabel.setText("Maximium Date Time: ");
		GridData g12 = new GridData();
		g12.horizontalAlignment = GridData.FILL;	
		g12.horizontalSpan=2;
		maxDateLabel.setLayoutData(g12);		
		
		final DateTime maxDate = new DateTime (dateRangeGroup, SWT.DATE  | SWT.MEDIUM | SWT.BORDER);
		final DateTime maxTime = new DateTime (dateRangeGroup, SWT.TIME  | SWT.MEDIUM | SWT.BORDER);
		maxDate.setDate(dateFields[0], dateFields[1], dateFields[2]);
		maxTime.setTime(dateFields[3]+1, dateFields[4], dateFields[5]);
	
		
		minDate.setEnabled(false);
		minTime.setEnabled(false);		
		maxDate.setEnabled(false);
		maxTime.setEnabled(false);

		GridData g5 = new GridData();
		g5.verticalSpan=3;
		dateRangeGroup.setLayoutData(g5);

		enableDate.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent arg0) {
				if (enableDate.getSelection()) {
					minDate.setEnabled(true);
					minTime.setEnabled(true);
					
					maxDate.setEnabled(true);
					maxTime.setEnabled(true);
				} else {
					minDate.setEnabled(false);
					minTime.setEnabled(false);
					
					maxDate.setEnabled(false);
					maxTime.setEnabled(false);
				}
					
			}
			
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
	
	    Button queryButton = new Button(page, SWT.PUSH);
	    queryButton.setText("Query");
	    GridData gridData5 = new GridData();
		gridData5.horizontalAlignment = GridData.FILL;	
		gridData5.grabExcessHorizontalSpace = true;
		gridData5.horizontalSpan=4;
		queryButton.setLayoutData(gridData5);
		
		queryButton.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
				query.hostName = hostText.getText();
				query.origin = originText.getText();
				query.tag = tagText.getText();
				query.logLevel = logLevels.getSelection();
				
				if (enableDate.getSelection()) {
					query.minTime = getDate(minDate, minTime);
					query.maxTime = getDate(maxDate, maxTime);
				} else {
					query.minTime = null;
					query.maxTime = null;
				}
				
				query.startIndex = 0;
				try {
					LogQueryResultViewer viewer = ((LogQueryResultViewer)LogQueryResultViewer.openLogResultViewer());
					LogQueryDataModel dataModel = viewer.getDataModel();
					dataModel.clear();
					dataModel.getLogMessage(query);
					viewer.updateTable();
				} catch (Exception ex) {
					logger.log(Level.WARNING, "Could not query log server for messages", ex);
					
		            ExceptionDetailsErrorDialog.openError(parent.getShell(),
		                    "ERROR",
		                    "Could not query log server for messages",
		                    ex);
				} 
				
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});		
	}
	
	protected Date getDate(DateTime date, DateTime time) {
		long millisec = AskapHelper.getDate(date.getYear(), date.getMonth(), 
												date.getDay(), time.getHours(), 
												time.getMinutes(), time.getSeconds());
		return new Date(millisec);
	}	

	
	public static Object openLogQueryView() {
		try{
			if (PlatformUI.getWorkbench() != null
					&& PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null
					&& PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage() != null) {
										
				return PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow()
						.getActivePage().showView(LogQuery.ID);
			}
				
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot create LogQueryResultViewer", ex);
		}
		return null;
	}


	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
}
