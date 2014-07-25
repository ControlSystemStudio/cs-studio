package org.csstudio.askap.sb;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.askap.sb.ui.SchedulerDialog;
import org.csstudio.askap.sb.util.DataChangeEvent;
import org.csstudio.askap.sb.util.DataChangeListener;
import org.csstudio.askap.sb.util.SBDataModel;
import org.csstudio.askap.sb.util.SchedulingBlock;
import org.csstudio.askap.sb.util.SchedulingBlock.SBState;
import org.csstudio.askap.utility.AskapEditorInput;
import org.csstudio.askap.utility.AskapHelper;
import org.csstudio.askap.utility.icemanager.LogObject;
import org.csstudio.askap.utility.icemanager.MonitorPointListener;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import askap.interfaces.monitoring.MonitorPoint;

public class SBExecutionView extends EditorPart {

	public static final String ID = "org.csstudio.askap.sb.SBExecutionView";
	private static Logger logger = Logger.getLogger(SBExecutionView.class.getName());

	private static final int NUM_OF_COLUMNS = 6;
	
	Table executedTable = null;
	Table scheduleTable = null;

	Button editButton = null;
	Button stopButton = null;
	Button abortButton = null;
	Button startButton = null;
	
	Label status = null;	
	Label sbidLabel = null;
	Button stopCaptureButton = null;
	
	private static Image RED_LED_IMAGE = null;
	private static Image GREEN_LED_IMAGE = null;
	private static Image GREY_LED_IMAGE = null;
		
	
	private static final Map<SBState, Integer> STATE_COLOR_MAP = new HashMap<SBState, Integer>();
	
	private Composite parent = null;
	
	SBDataModel dataModel = new SBDataModel();
	
	static {
		STATE_COLOR_MAP.put(SBState.ERRORED, SWT.COLOR_RED);
		STATE_COLOR_MAP.put(SBState.EXECUTING, SWT.COLOR_GREEN);
		
		RED_LED_IMAGE = Activator.getDefault().getImage("icons/red_round_button.png");        	
		GREEN_LED_IMAGE = Activator.getDefault().getImage("icons/green_round_button.png");
		GREY_LED_IMAGE = Activator.getDefault().getImage("icons/grey_round_button.png");
	}
	

	public SBExecutionView() {
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
        setSite(site);
        setPartName(input.getName());
    	setInput(input);
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	private Composite getParent() {
		return parent;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		setupExecutiveListener();
		this.parent = parent;
		
		Composite page = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(NUM_OF_COLUMNS, false);
		page.setLayout(gridLayout);				
		
		Label executedTitle = new Label(page, SWT.NONE);
		executedTitle.setText("Executing and executed Scheduling Block (last " + Preferences.getSBExecutionMaxNumberSB()  + " scheduling blocks):");
		GridData gridData = new GridData();
		gridData.horizontalSpan = NUM_OF_COLUMNS;
		executedTitle.setLayoutData(gridData);
		
		executedTable = new Table(page, SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER | SWT.VIRTUAL);
		executedTable.setLinesVisible (true);
		executedTable.setHeaderVisible (true);
		
		TableColumn column = new TableColumn (executedTable, SWT.NONE);
		column.setText ("Status");

		column = new TableColumn (executedTable, SWT.NONE);
		column.setText ("ID");
		
		column = new TableColumn (executedTable, SWT.NONE);
		column.setText ("Alias Name");

		column = new TableColumn (executedTable, SWT.NONE);
		column.setText ("Template Name");
		
		column = new TableColumn (executedTable, SWT.NONE);
		column.setText ("Template Version");
		
		column = new TableColumn (executedTable, SWT.NONE);
		column.setText ("Last Run Time");

		column = new TableColumn (executedTable, SWT.NONE);
		column.setText ("Last Run Duration");
		
		column = new TableColumn (executedTable, SWT.NONE);
		column.setText ("Error Message");
		
		executedTable.setItemCount(0);
		setTableSize(executedTable);
		
		GridData g3 = new GridData();
		g3.horizontalAlignment = GridData.FILL;	
		g3.verticalAlignment = GridData.FILL;	
		g3.grabExcessHorizontalSpace = true;
		g3.grabExcessVerticalSpace = true;
		g3.horizontalSpan = NUM_OF_COLUMNS;
		executedTable.setLayoutData(g3);
		
		executedTable.addListener(SWT.SetData, new Listener() {
			public void handleEvent(Event event) {
				TableItem item = (TableItem)event.item;
				int index = event.index;
				SchedulingBlock sb = dataModel.getExecutedSBAt(index);
				if (sb != null) {
					String executeTime = "";
					if (sb.getLastExecutedDate()!=null)
						executeTime = sb.getLastExecutedDate();
					
					String duration = getStringDuration(sb.getLastExecutionDuration());
					
					item.setText(new String[]{sb.getState().toString(), "" + sb.getId(),  sb.getAliasName(), sb.getTemplateName(), sb.getExecutedVersion(), 
							executeTime, duration, sb.getErrorMessage()});
					item.setData(sb.getId());
					
					if (STATE_COLOR_MAP.get(sb.getState())!=null)
						item.setBackground(getParent().getDisplay().getSystemColor(STATE_COLOR_MAP.get(sb.getState())));
					else
						item.setBackground(null);						
					
				}				
			}
		});
	
		
		Label statusText = new Label(page, 0);
		statusText.setText("Executive Status: ");
		
		status = new Label(page, 0);		
		g3 = new GridData();
		g3.horizontalAlignment = GridData.FILL;	
		g3.grabExcessHorizontalSpace = true;
		g3.horizontalAlignment = SWT.LEFT;
		status.setLayoutData(g3);
		status.setImage(GREY_LED_IMAGE);

		
		startButton = new Button(page, SWT.PUSH);
		startButton.setText("Start Executive");
		startButton.addSelectionListener(new SelectionListener() {			
			public void widgetSelected(SelectionEvent arg0) {
				startExecutive();
			}
			
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		
		stopButton = new Button(page, SWT.PUSH);
		stopButton.setText("Stop Executive");
		stopButton.addSelectionListener(new SelectionListener() {		
			public void widgetSelected(SelectionEvent arg0) {
				stopExecutive();
			}	
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		abortButton = new Button(page, SWT.PUSH);
		abortButton.setText("Abort Executive");
		abortButton.addSelectionListener(new SelectionListener() {		
			public void widgetSelected(SelectionEvent arg0) {
				abortExecutive();
			}	
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		
		editButton = new Button(page, SWT.PUSH);
		editButton.setText("Modify Scheduling List");
		editButton.setEnabled(false);
		editButton.addSelectionListener(new SelectionListener() {	
			public void widgetSelected(SelectionEvent event) {
				try {
					reschedule();
				} catch (Exception e) {
					logger.log(Level.WARNING, "Could not reschedule", e);
		            ExceptionDetailsErrorDialog.openError(getParent().getShell(),
		                    "ERROR",
		                    "Could not reschedule",
		                    e);

				}
			}
			
			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});
		
		Label scheduleTitle = new Label(page, SWT.NONE);
		scheduleTitle.setText("Scheduled Blocks:");
		gridData = new GridData();
		gridData.horizontalSpan = NUM_OF_COLUMNS;
		scheduleTitle.setLayoutData(gridData);
		
		scheduleTable = new Table(page, SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER | SWT.VIRTUAL);
		scheduleTable.setLinesVisible (true);
		scheduleTable.setHeaderVisible (true);

		column = new TableColumn (scheduleTable, SWT.NONE);
		column.setText ("ID");
		
		column = new TableColumn (scheduleTable, SWT.NONE);
		column.setText ("Alias");

		column = new TableColumn (scheduleTable, SWT.NONE);
		column.setText ("Template Name");
				
		column = new TableColumn (scheduleTable, SWT.NONE);
		column.setText ("Template Version");
		
		column = new TableColumn (scheduleTable, SWT.NONE);
		column.setText ("Scheduled Time");
		
		scheduleTable.setItemCount(dataModel.getScheduledSBCount());
		setTableSize(scheduleTable);
		
		GridData g4 = new GridData();
		g4.horizontalAlignment = GridData.FILL;	
		g4.verticalAlignment = GridData.FILL;	
		g4.grabExcessHorizontalSpace = true;
		g4.grabExcessVerticalSpace = true;
		g4.horizontalSpan = NUM_OF_COLUMNS;
		scheduleTable.setLayoutData(g4);
		
		scheduleTable.addListener(SWT.SetData, new Listener() {
			public void handleEvent(Event event) {
				TableItem item = (TableItem)event.item;
				int index = event.index;
				SchedulingBlock sb = dataModel.getScheduledSBAt(index);
				long scheduledTime = sb.getScheduledTime();

				if (sb != null) {
					item.setText(new String[]{"" + sb.getId(), sb.getAliasName(), sb.getTemplateName(), 
							"" + sb.getMajorVersion(), AskapHelper.getFormatedData(new Date(scheduledTime), null) });
					item.setData(sb.getId());
				}
			}
		});

		scheduleTable.setToolTipText("You have to stop the Executive to enable '" + editButton.getText() + "' button to reschedule SB");

		page.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				setTableSize(executedTable);
				setTableSize(scheduleTable);
			}
		});
		
		executedTable.addSelectionListener(new SelectionListener() {		
			public void widgetSelected(SelectionEvent arg0) {
			}
			
			public void widgetDefaultSelected(SelectionEvent event) {
				TableItem item = (TableItem) event.item;
				long sbId = (Long) item.getData();
				
				openSBView(sbId, item.getText(2));
			}
		});
		
		scheduleTable.addSelectionListener(new SelectionListener() {		
			public void widgetSelected(SelectionEvent arg0) {
			}
			
			public void widgetDefaultSelected(SelectionEvent event) {
				TableItem item = (TableItem) event.item;
				long sbId = (Long) item.getData();
				
				openSBView(sbId, item.getText(1));
			}
		});
		
		page.pack();
		
		setupListener();
		disableAllButtons();
	}

	private void setupListener() {
        getSite().getPage().addPartListener(new IPartListener2() {
        	
            private boolean isThisEditor(final IWorkbenchPartReference part) {
                return (part.getPart(false) instanceof SBExecutionView);
            }

			
			@Override
			public void partVisible(IWorkbenchPartReference partRef) {
			}
			
			@Override
			public void partOpened(IWorkbenchPartReference partRef) {
				if (isThisEditor(partRef))
					start();
			}
			
			@Override
			public void partClosed(IWorkbenchPartReference partRef) {
				if (isThisEditor(partRef))
					stop();
			}

			@Override
			public void partHidden(IWorkbenchPartReference partRef) {
			}
			

			@Override
			public void partInputChanged(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub			
			}
			
			@Override
			public void partDeactivated(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public void partBroughtToTop(IWorkbenchPartReference partRef) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void partActivated(IWorkbenchPartReference partRef) {
			}
		});
	}

	
	public void stop() {
		dataModel.stopUpdates();
	}
	
	protected void start() {
		dataModel.startSBPollingThread(new DataChangeListener() {
			public void dataChanged(final DataChangeEvent event) {
				getParent().getDisplay().asyncExec(new Runnable() {					
					public void run() {
						executedTable.clearAll();
						executedTable.setItemCount(dataModel.getExecutedSBCount());
						scheduleTable.clearAll();
						scheduleTable.setItemCount(dataModel.getScheduledSBCount());
						
						executedTable.redraw();
						scheduleTable.redraw();
					}
				});
			}
		});

		dataModel.addPointListener(new String[]{Preferences.getExecutiveMonitorPointName()},				
			new MonitorPointListener() {
				@Override
				public void onUpdate(MonitorPoint point) {
					getParent().getDisplay().asyncExec(new Runnable() {					
						public void run() {
							setupButtons(true);
						}
					});
				}
				
				@Override
				public void disconnected(String pointName) {
					getParent().getDisplay().asyncExec(new Runnable() {					
						public void run() {
							disableAllButtons();
						}
					});
				}});
		
		dataModel.addPointListener(ExecutiveSummaryView.POINT_NAMES,				
				new MonitorPointListener() {
					public void onUpdate(final MonitorPoint point) {
						getParent().getDisplay().asyncExec(new Runnable() {					
							public void run() {
								ExecutiveSummaryHelper.getInstance().updateValue(point);
							}
						});
					}

					@Override
					public void disconnected(final String pointName) {
						getParent().getDisplay().asyncExec(new Runnable() {					
							public void run() {
								ExecutiveSummaryHelper.getInstance().disconnected(pointName);
							}
						});
					}
			});	
			
		dataModel.startExecutiveLogSubscriber(new DataChangeListener() {
			
			@Override
			public void dataChanged(DataChangeEvent e) {
				LogObject logObj = (LogObject) e.getChange();
				if (logObj!=null)
					ExecutiveLogHelper.getInstance().writeLog(logObj);
			}
			
		});
	}

	@Override
	public void setFocus() {
	}

	/**
	 * @param sbId
	 */
	protected void openSBView(long sbId, String templateName) {
		try {
			SBTemplateView sbView = (SBTemplateView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(SBTemplateView.ID);				
			sbView.refreshAndSelect(templateName, sbId);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not open SB view for " + sbId, e);
		}
	}


	protected void setupExecutiveListener() {
		dataModel.setDataChangeListener(new DataChangeListener() {			
			public void dataChanged(final DataChangeEvent event) {
				getParent().getDisplay().asyncExec(new Runnable() {		
					public void run() {
						Exception e = (Exception) event.getChange();				
						if (e !=null ) {
							logger.log(Level.WARNING, "Could not stop/abort Executive Service", e);
				            ExceptionDetailsErrorDialog.openError(getParent().getShell(),
				                    "ERROR",
				                    "Could not stop/abort Executive Service",
				                    e);
						}
					}
				});
				
			}
		});
	}
	
	protected void setupButtons(boolean isRunning) {

		if (isRunning) {
			scheduleTable.setToolTipText("You have to stop the Executive to enable '" + editButton.getText() + "' button to reschedule SB");
			startButton.setEnabled(false);
			editButton.setEnabled(false);
			
			stopButton.setEnabled(true);
			abortButton.setEnabled(true);
			
			return;
			
		} else {
			startButton.setEnabled(true);
			editButton.setEnabled(true);

			scheduleTable.setToolTipText("You have to start the Executive to execute the SB");
			
			stopButton.setEnabled(false);
			abortButton.setEnabled(false);
			
		}
	}
	
	
	protected void disableAllButtons() {
		startButton.setEnabled(false);
		editButton.setEnabled(false);
		
		stopButton.setEnabled(false);
		abortButton.setEnabled(false);		
	}
	
	protected void stopExecutive() {
		try {
			dataModel.stop();
			disableAllButtons();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not stop 'Executive Service'", e);
            ExceptionDetailsErrorDialog.openError(getParent().getShell(),
                    "ERROR",
                    "Could not stop Executive Service",
                    e);

		}
	}

	protected void startExecutive() {
		try {
			dataModel.start();

			disableAllButtons();

	        MessageBox messageBox = new MessageBox(getParent().getShell(), SWT.ICON_INFORMATION | SWT.OK);
	        messageBox.setMessage("Executive service started");
	        messageBox.open();
	        
	        dataModel.interruptPollingThread();
	        
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not start 'Executive Service'", e);
            ExceptionDetailsErrorDialog.openError(getParent().getShell(),
                    "ERROR",
                    "Could not start Executive Service",
                    e);
		}
	}
	
	protected void abortExecutive() {
		try {
			dataModel.abort();
			disableAllButtons();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not abort 'Executive Service'", e);
            ExceptionDetailsErrorDialog.openError(getParent().getShell(),
                    "ERROR",
                    "Could not abot Executive Service",
                    e);
		}
	}
	
	protected void reschedule() throws Exception {
		List<SchedulingBlock> toList = dataModel.getSBByState(new SBState[]{SchedulingBlock.SBState.SCHEDULED});
		List<SchedulingBlock> fromList = dataModel.getSBByState(new SBState[]{SchedulingBlock.SBState.SUBMITTED});
		List<SchedulingBlock> allList = new ArrayList<SchedulingBlock>();
		allList.addAll(fromList);
		allList.addAll(toList);
		
		SchedulerDialog dialog = new SchedulerDialog(getParent().getShell(), fromList, toList);
		toList = dialog.open();		
		
		// since the order in which the scheduling block are set to SCHEDULED, we need to change all the status 
		// to SUBMITTED first, the set the SCHEDULED ones
		if (toList != null) {
			for (SchedulingBlock sb : allList) {
				dataModel.setSBState(sb.getId(), SBState.SUBMITTED);
			}
			
			for (SchedulingBlock sb : toList) {
				dataModel.setSBState(sb.getId(), SBState.SCHEDULED);
			}
		}
		
        dataModel.interruptPollingThread();
	}
	
	/**
	 * @param table
	 */
	private void setTableSize(Table table) {
			Rectangle area = table.getParent().getClientArea();
			Point size = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			ScrollBar vBar = table.getVerticalBar();
			int width = area.width - table.computeTrim(0,0,0,0).width - vBar.getSize().x;
			if (size.y > area.height + table.getHeaderHeight()) {
				// Subtract the scrollbar width from the total column width
				// if a vertical scrollbar will be required
				Point vBarSize = vBar.getSize();
				width -= vBarSize.x;
			}
			Point oldSize = table.getSize();
			if (oldSize.x > area.width) {
				// table is getting smaller so make the columns 
				// smaller first and then resize the table to
				// match the client area width
				if (table.getColumnCount()==5) {
					table.getColumn(0).setWidth(width * 20/100);
					table.getColumn(1).setWidth(width * 20/100);					
					table.getColumn(2).setWidth(width * 20/100);					
					table.getColumn(3).setWidth(width * 20/100);
					table.getColumn(4).setWidth(width * 20/100);
				} else {
					table.getColumn(0).setWidth(width * 10/100);
					table.getColumn(1).setWidth(width * 20/100);
					table.getColumn(2).setWidth(width * 15/100);
					table.getColumn(3).setWidth(width * 5/100);
					table.getColumn(4).setWidth(width * 20/100);
					table.getColumn(5).setWidth(width * 10/100);
					table.getColumn(6).setWidth(width * 10/100);
					table.getColumn(7).setWidth(width * 10/100);
				}
				table.setSize(area.width, area.height);
			} else {
				// table is getting bigger so make the table 
				// bigger first and then make the columns wider
				// to match the client area width
				table.setSize(area.width, area.height);
				if (table.getColumnCount()==5) {
					table.getColumn(0).setWidth(width * 20/100);
					table.getColumn(1).setWidth(width * 20/100);					
					table.getColumn(2).setWidth(width * 20/100);					
					table.getColumn(3).setWidth(width * 20/100);
					table.getColumn(4).setWidth(width * 20/100);
				} else {
					table.getColumn(0).setWidth(width * 10/100);
					table.getColumn(1).setWidth(width * 10/100);
					table.getColumn(2).setWidth(width * 20/100);
					table.getColumn(3).setWidth(width * 10/100);
					table.getColumn(4).setWidth(width * 10/100);
					table.getColumn(5).setWidth(width * 10/100);
					table.getColumn(6).setWidth(width * 10/100);
					table.getColumn(7).setWidth(width * 20/100);
				}
			}
			table.pack();
	}	
	
	public static SBExecutionView openSBExecutionView() {
        try {
        	final IWorkbench workbench = PlatformUI.getWorkbench();
        	final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        	final IWorkbenchPage page = window.getActivePage();
        	
            return (SBExecutionView) page.openEditor(new AskapEditorInput("SB Execution View"), ID);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "SB Execution View", ex);
		}
        return null;
	}
	
	public static String getStringDuration(long elapsedTime) {
		String format = String.format("%%0%dd", 2);
		elapsedTime = elapsedTime / 1000;
		String seconds = String.format(format, elapsedTime % 60);
		String minutes = String.format(format, (elapsedTime % 3600) / 60);
		String hours = String.format(format, elapsedTime / 3600);
		String time = hours + ":" + minutes + ":" + seconds;
		return time;
	}
}
