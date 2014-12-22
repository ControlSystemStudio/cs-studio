package org.csstudio.askap.sb;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.askap.sb.util.SBDataModel;
import org.csstudio.askap.sb.util.TypedValueConverter;
import org.csstudio.askap.utility.icemanager.MonitorPointListener;
import org.csstudio.ui.util.thread.UIBundlingThread;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import askap.interfaces.TypedValueBool;
import askap.interfaces.monitoring.MonitorPoint;

public class ExecutiveSummaryView extends ViewPart {

	public static final String ID= "org.csstudio.askap.sb.ExecutiveSummaryView";
	private static Logger logger = Logger.getLogger(ExecutiveSummaryView.class.getName());

	public static final String[] POINT_NAMES = {"schedblock.id",
												"schedblock.alias",
												"schedblock.template",
												"scan",
												"progress",
												"duration",
												"target.name",
												"target.frequency",
												"target.direction",
												"target.frame",
												"target.pos1",
												"target.pos2",
												"target.roll_mode",
												"target.roll_angle",
												"target.phase_direction"};
	
    Table table = null;
    
    private ProgressBar progressBar = null;
    private TableItem isExecutiveRunningItem = null;	

	SBDataModel dataModel = new SBDataModel();

	ExecutiveSummaryListener executiveSummaryListener = new ExecutiveSummaryListener();

	public class ExecutiveSummaryListener implements MonitorPointListener {
		public void onUpdate(final MonitorPoint point) {
			Display.getDefault().asyncExec(new Runnable() {					
				public void run() {
					update(point);
				}
			});
		}

		@Override
		public void disconnected(final String pointName) {
			Display.getDefault().asyncExec(new Runnable() {					
				public void run() {
					disconnected(pointName);
				}
			});
		}
	}
	

	public ExecutiveSummaryView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		
		GridLayout gridLayout = new GridLayout(1, false);
		parent.setLayout(gridLayout);
		
		GridData tableGridData = new GridData();
		tableGridData.horizontalAlignment = GridData.FILL;	
		tableGridData.verticalAlignment = GridData.FILL;
		tableGridData.grabExcessHorizontalSpace = true;
		tableGridData.grabExcessVerticalSpace = true;
		
		// create table
	    table = new Table(parent, SWT.BORDER );
	    table.setHeaderVisible(false);
	    
	    table.setLayoutData(tableGridData);
	    
	    // add two columns
	    TableColumn column = new TableColumn(table, SWT.NULL);
	    column = new TableColumn(table, SWT.NULL);
	    
	    TableItem item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Executive Running");
	    isExecutiveRunningItem = item;
		isExecutiveRunningItem.setImage(1, Activator.GREY_LED_IMAGE);

	    item = new TableItem(table, SWT.NULL);

	    item = new TableItem(table, SWT.NULL);	    
	    item.setText(0, "Scheduling Block ID");
	    item.setData("schedblock.id");
	    
	    item = new TableItem(table, SWT.NULL);	    
	    item.setText(0, "Scheduling Block Alias");
	    item.setData("schedblock.alias");	    
	    
	    item = new TableItem(table, SWT.NULL);	    
	    item.setText(0, "Scheduling Block Template");
	    item.setData("schedblock.template");	    
	    
	    item = new TableItem(table, SWT.NULL);
	    
	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Scan Number");
	    item.setData("scan");
	    
	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Scan Duration");
	    item.setData("duration");

	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Scan Progress");
	    
	    progressBar = new ProgressBar(table, SWT.NONE);
	    progressBar.setMaximum(100);
        TableEditor editor = new TableEditor(table);
        editor.grabHorizontal = editor.grabVertical = true;
        editor.setEditor(progressBar, item, 1);
        progressBar.setVisible(false);

	    item = new TableItem(table, SWT.NULL);
	    
	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Target Name");
	    item.setData("target.name");

	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Target Frequency");
	    item.setData("target.frequency");

	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Target Direction");
	    item.setData("target.direction");

	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Target Position 1 (eg: RA or Az)");
	    item.setData("target.pos1");

	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Target Position 2 (eg: Dec or El)");
	    item.setData("target.pos2");
	    
	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Target Frame");
	    item.setData("target.frame");

	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Target Roll Mode");
	    item.setData("target.roll_mode");

	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Target Roll Angle");
	    item.setData("target.roll_angle");

	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Target Phase Direction");
	    item.setData("target.phase_direction");
	    
		parent.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				setTableSize();
			}
		});

	    setTableSize();
	    parent.pack();
	    
	    setupListener();
	    
		dataModel.addPointListener(Preferences.getExecutiveMonitorIceName(), 
				new String[]{Preferences.getExecutiveMonitorPointName()}, executiveSummaryListener);
		
		dataModel.addPointListener(Preferences.getOPLMonitorIceName(),
				ExecutiveSummaryView.POINT_NAMES, executiveSummaryListener);	
	}

	public void setTableSize() {
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
			table.getColumn(0).setWidth(200);
			table.getColumn(1).setWidth(width-200);
			table.setSize(area.width, area.height);
		} else {
			// table is getting bigger so make the table 
			// bigger first and then make the columns wider
			// to match the client area width
			table.setSize(area.width, area.height);
			table.getColumn(0).setWidth(200);
			table.getColumn(1).setWidth(width-200);
		}
	}
	

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	public void disconnected(String pointName) {
        progressBar.setVisible(false);
		isExecutiveRunningItem.setImage(1, Activator.GREY_LED_IMAGE);
		
		for (TableItem item : table.getItems()) {
			if (item.getData() != null) {
				item.setText(1, "");
			}
		}
	}
	
	public void update(MonitorPoint point) {
		if (point.name.equals(Preferences.getExecutiveMonitorPointName())) {
			if (point.value instanceof TypedValueBool) {
				boolean running = ((TypedValueBool) point.value).value;
				if (running) {
					// set up the icon
					isExecutiveRunningItem.setImage(1, Activator.GREEN_LED_IMAGE);
				} else {
					// set up the icon
					isExecutiveRunningItem.setImage(1, Activator.RED_LED_IMAGE);
				}
			} else {
				isExecutiveRunningItem.setImage(1, Activator.GREY_LED_IMAGE);
			}
		} else if (point.name.equals("progress")) {
	        progressBar.setVisible(true);
			String value = TypedValueConverter.convert(point.value);
			if (!value.isEmpty()) {
				double progress = Double.parseDouble(value);
				if (!progressBar.getVisible())
					progressBar.setVisible(true);
				
		        progressBar.setSelection((int) Math.floor(progress));
			} else {
				progressBar.setVisible(false);
			}
		} else {
			for (TableItem item : table.getItems()) {
				if (point.name.equals(item.getData())) {
					String value = TypedValueConverter.convert(point.value);
					item.setText(1, value);
				}
			}
		}
	}
	
	public static void popSummaryView() {
		UIBundlingThread.getInstance().addRunnable(new Runnable() {
			public void run() {
				try {
					if (PlatformUI.getWorkbench() != null
							&& PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null
							&& PlatformUI.getWorkbench().getActiveWorkbenchWindow()
									.getActivePage() != null) {
												
						PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow()
								.getActivePage().showView(ExecutiveSummaryView.ID);	
					}
				} catch (PartInitException e) {
					logger.log(Level.WARNING, "ExecutiveSummaryView activation error", e);
				}
			}
		});
	}

	
	private void setupListener() {
        getSite().getPage().addPartListener(new IPartListener2() {
        	
            private boolean isThisView(final IWorkbenchPartReference part) {
                return (part.getPart(false) instanceof ExecutiveSummaryView);
            }

			@Override
			public void partVisible(IWorkbenchPartReference partRef) {
			}
			
			@Override
			public void partOpened(IWorkbenchPartReference partRef) {
				// Open Summary view as well
				ExecutiveSummaryView.popSummaryView();
			}
			
			@Override
			public void partClosed(IWorkbenchPartReference partRef) {
				if (isThisView(partRef))
					dataModel.stopUpdates();
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
		
}
