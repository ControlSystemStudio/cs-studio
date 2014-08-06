package org.csstudio.askap.sb;

import java.util.logging.Logger;

import org.csstudio.askap.utility.AskapHelper;
import org.csstudio.askap.utility.icemanager.LogObject;
import org.csstudio.askap.utility.icemanager.LogObject.LogComparatorField;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

public class ExecutiveLogView extends ViewPart {

	private static Logger logger = Logger.getLogger(ExecutiveLogView.class.getName());
		
	private static final int NUM_OF_COLUMNS = 6;

	public static final String ID = "org.csstudio.askap.sb.ExecutiveLogView";

	private boolean isScrolling = false;
	
	private TableViewer table;

	private Composite parent = null;
	private Label messageCountLabel = null;
	
	private int numberOfMessage = 0;

	private boolean isDisposed = false;
	
	
	class LogMessageColorProvider extends ColumnLabelProvider implements IColorProvider {

		@Override
		public Color getForeground(Object element) {
			return null;
		}
		
		@Override
		public Color getBackground(Object element) {
			LogObject log = (LogObject) element;
			Integer color = LogObject.getLogLevelColor(log.getLogLevel());
			if (color!=null)
				return Display.getCurrent().getSystemColor(color);					
			
			return null;
		}
	}
	
	public ExecutiveLogView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		createLogTable(parent);
	}

	@Override
	public void setFocus() {
	}
	
	public Control createLogTable(Composite comp) {
		this.parent = comp;
				
		final Composite page = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(NUM_OF_COLUMNS, false);
		gridLayout.horizontalSpacing = 20;
		page.setLayout(gridLayout);

		final Button scroll = new Button(page, SWT.CHECK);
		scroll.setText("Scroll");
		scroll.setSelection(isScrolling);
		scroll.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
				isScrolling = scroll.getSelection();
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		messageCountLabel = new Label(page, SWT.NONE);
		messageCountLabel.setText("" + numberOfMessage + " messages");
		
		table = new TableViewer (page, SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER);
		table.getTable().setLinesVisible (true);
		table.getTable().setHeaderVisible (true);

		createColumns(page, table);		
				
		GridData tableGridData = new GridData();
		tableGridData.horizontalAlignment = GridData.FILL;	
		tableGridData.verticalAlignment = GridData.FILL;	
		tableGridData.grabExcessHorizontalSpace = true;
		tableGridData.grabExcessVerticalSpace = true;
		tableGridData.horizontalSpan = NUM_OF_COLUMNS;

		table.getControl().setLayoutData(tableGridData);
		
		table.setContentProvider(new ArrayContentProvider());
		
		setTableSize();		
		page.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				setTableSize();
			}
		});
		
		page.pack();
		
		return page;
	}
	
	
	public void logMessage(LogObject logObject) {
		if (logObject!=null) {
			numberOfMessage++;
			messageCountLabel.setText("" + numberOfMessage + " Messages" );
			table.insert(logObject, 0);
		}
		if (isScrolling)
			table.reveal(table.getElementAt(table.getTable().getItemCount()-1));
	}

	public void setTableSize() {
		Rectangle area = table.getTable().getParent().getClientArea();
		Point size = table.getTable().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		ScrollBar vBar = table.getTable().getVerticalBar();
		int width = area.width - table.getTable().computeTrim(0,0,0,0).width - vBar.getSize().x;
		if (size.y > area.height + table.getTable().getHeaderHeight()) {
			// Subtract the scrollbar width from the total column width
			// if a vertical scrollbar will be required
			Point vBarSize = vBar.getSize();
			width -= vBarSize.x;
		}
		Point oldSize = table.getTable().getSize();
		if (oldSize.x > area.width) {
			// table is getting smaller so make the columns 
			// smaller first and then resize the table to
			// match the client area width
			table.getTable().getColumn(0).setWidth(200);
			table.getTable().getColumn(1).setWidth(150);
			table.getTable().getColumn(2).setWidth(50);
			table.getTable().getColumn(3).setWidth(50);
			table.getTable().getColumn(4).setWidth(width-450);
			table.getTable().setSize(area.width, area.height);
		} else {
			// table is getting bigger so make the table 
			// bigger first and then make the columns wider
			// to match the client area width
			table.getTable().setSize(area.width, area.height);
			table.getTable().getColumn(0).setWidth(200);
			table.getTable().getColumn(1).setWidth(150);
			table.getTable().getColumn(2).setWidth(50);
			table.getTable().getColumn(3).setWidth(50);
			table.getTable().getColumn(4).setWidth(width-450);
		}
	}
	
	
	private void createColumns(final Composite parent, final TableViewer table) {		

		TableViewerColumn col = createTableViewerColumn("Timestamp", LogComparatorField.timeStamp);		
		col.setLabelProvider(new LogMessageColorProvider() {		
			@Override
			public String 	getText(Object element)  {
				LogObject log = (LogObject) element;
				return AskapHelper.getFormatedData(log.getTimeStamp(), null);
			}
		});
		
		col = createTableViewerColumn("Origin", LogComparatorField.origin);		
		col.setLabelProvider(new LogMessageColorProvider() {
			@Override
			public String 	getText(Object element)  {
				LogObject log = (LogObject) element;
				return log.getOrigin();
			}
		});
		
		col = createTableViewerColumn("Host", LogComparatorField.hostName);		
		col.setLabelProvider(new LogMessageColorProvider() {
			@Override
			public String 	getText(Object element)  {
				LogObject log = (LogObject) element;
				return log.getHostName();
			}
		});
		
		col = createTableViewerColumn("Level", LogComparatorField.logLevel);		
		col.setLabelProvider(new LogMessageColorProvider() {
			@Override
			public String 	getText(Object element)  {
				LogObject log = (LogObject) element;
				return log.getLogLevel();
			}
		});
		
		col = createTableViewerColumn("Message", LogComparatorField.logMessage);		
		col.setLabelProvider(new LogMessageColorProvider() {
			@Override
			public String 	getText(Object element)  {
				LogObject log = (LogObject) element;
				return log.getLogMessage();
			}
		});		
	}

	private TableViewerColumn createTableViewerColumn(String title, final LogComparatorField compField) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(table,
				SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setResizable(true);
		column.setMoveable(true);
		
		return viewerColumn;
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
		isDisposed  = true;
	}
	
	public boolean isDisposed() {
		return isDisposed;
	}
}
