package org.csstudio.askap.sb;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import askap.interfaces.monitoring.MonitorPoint;

public class ExecutiveSummaryView extends ViewPart {

	public static final String ID= "org.csstudio.askap.sb.ExecutiveSummaryView";
	private static Logger logger = Logger.getLogger(ExecutiveSummaryView.class.getName());

	public static final String[] POINT_NAMES = {"schedblock.id",
												"schedblock.target.pos1",
												"schedblock.target.pos2",
												"schedblock.target.frame",
												"schedblock.target.frequency",
												"schedblock.target.name",
												"schedblock.scan",
												"executive.running"};
	
    Table table = null;

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
	    item.setText(1, "XX");

	    item = new TableItem(table, SWT.NULL);
	    
	    item = new TableItem(table, SWT.NULL);	    
	    item.setText(0, "Scheduling Block ID");
	    item.setText(1, "5");
	    
	    item = new TableItem(table, SWT.NULL);
	    
	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Target Position 1 (eg: RA or Az)");
	    item.setText(1, "XX");

	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Target Position 2 (eg: Dec or El)");
	    item.setText(1, "XX");
	    
	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Target Coordinate System");
	    item.setText(1, "XX");
	    
	    item = new TableItem(table, SWT.NULL);
	    
	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Centre Frequency");
	    item.setText(1, "XX");
	    
	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Target Name");
	    item.setText(1, "XX");
	    
	    item = new TableItem(table, SWT.NULL);
	    item.setText(0, "Scan Number");
	    item.setText(1, "XX");
	    
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
		
	}
}
