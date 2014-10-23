package org.csstudio.askap.sb;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
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
	
	// keep a map of pointName and its corresponding tableItem, so when an point value is updated,
	// its corresponding table cell is also updated
	private Map<String, TableItem> pointItemTable = new HashMap<String, TableItem>();
	
    Table table = null;
    
    private ProgressBar bar = null;
    private TableItem isExecutiveRunningItem = null;	


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
	    pointItemTable.put("schedblock.id", item);	    
	    
	    item = new TableItem(table, SWT.NULL);	    
	    item.setText(0, "Scheduling Block Alias");
	    pointItemTable.put("schedblock.alias", item);	    
	    
	    item = new TableItem(table, SWT.NULL);	    
	    item.setText(0, "Scheduling Block Template");
	    pointItemTable.put("schedblock.template", item);	    
	    
	    item = new TableItem(table, SWT.NULL);
	    
	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Scan Number");
	    pointItemTable.put("scan", item);
	    
	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Scan Duration");
	    pointItemTable.put("duration", item);

	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Scan Progress");
	    
	    bar = new ProgressBar(table, SWT.NONE);
	    bar.setSelection(5);
        TableEditor editor = new TableEditor(table);
        editor.grabHorizontal = editor.grabVertical = true;
        editor.setEditor(bar, item, 1);

	    item = new TableItem(table, SWT.NULL);
	    
	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Target Name");
	    pointItemTable.put("target.name", item);

	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Target Frequency");
	    pointItemTable.put("target.frequency", item);

	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Target Direction");
	    pointItemTable.put("target.direction", item);

	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Target Position 1 (eg: RA or Az)");
	    pointItemTable.put("target.pos1", item);

	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Target Position 2 (eg: Dec or El)");
	    pointItemTable.put("target.pos2", item);
	    
	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Target Frame");
	    pointItemTable.put("target.farme", item);

	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Target Roll Mode");
	    pointItemTable.put("target.roll_mode", item);

	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Target Roll Angle");
	    pointItemTable.put("target.roll_angle", item);

	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Target Phase Direction");
	    pointItemTable.put("target.phase_direction", item);
	    
		parent.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				setTableSize();
			}
		});

	    setTableSize();
	    parent.pack();
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
			
		} else {
			TableItem item = pointItemTable.get(point.name);
			if (item != null) {
				item.setText(1, point.value.toString());
			}
		}
	}
}
