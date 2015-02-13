package org.csstudio.askap.logviewer.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.askap.logviewer.Preferences;
import org.csstudio.askap.logviewer.util.FilterObject;
import org.csstudio.askap.logviewer.util.LogMessageDataModel;
import org.csstudio.askap.logviewer.util.LogQueryDataModel;
import org.csstudio.askap.utility.AskapHelper;
import org.csstudio.askap.utility.icemanager.LogObject;
import org.csstudio.askap.utility.icemanager.LogObject.LogComparatorField;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;


public class LogMessageTable {

	private static final Logger logger = Logger.getLogger(LogMessageTable.class.getName());

	int messageCount = 0;
	
	private static final int NUM_OF_COLUMNS = 6;
	private LogMessageDataModel dataModel = null;

	private boolean isScrolling = false;
	
	private TableViewer table;

	private Composite parent = null;
	private Label messageCountLabel = null;
	
	private LogObjectViewerComparator comparator;
	private LogObjectViewerFilter logObjectViewerFilter;
	
	private FilterObject filter = new FilterObject();
	
	private boolean shouldRefresh = false;
	
	public LogMessageTable() {
	}

	public Control createLogTable(Composite comp, LogMessageDataModel model, boolean shouldRefresh) {
		this.shouldRefresh = shouldRefresh;
		this.parent = comp;
		this.dataModel = model;
		this.comparator = new LogObjectViewerComparator();
		this.logObjectViewerFilter = new LogObjectViewerFilter();
				
		final Composite page = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(NUM_OF_COLUMNS, false);
		gridLayout.horizontalSpacing = 20;
		page.setLayout(gridLayout);

		final Combo serviceFilter = new Combo (page, SWT.DROP_DOWN | SWT.READ_ONLY);
		serviceFilter.add("All Tags");
		for (int i=0; i<Preferences.SERVICE_NAMES.length; i++)
			serviceFilter.add(Preferences.SERVICE_NAMES[i]);

		serviceFilter.select(0);
		serviceFilter.addSelectionListener(new SelectionListener() {		
			public void widgetSelected(SelectionEvent e) {
				String newTagFilter = "";
				if (serviceFilter.getSelectionIndex()>0)
					newTagFilter = serviceFilter.getText();
				
				if (!filter.getTag().equals(newTagFilter)) {
					filter.setTag(newTagFilter);
					messageCount = 0;
					table.refresh();
				}
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		final Combo logLevelFilter = new Combo (page, SWT.DROP_DOWN | SWT.READ_ONLY);
		logLevelFilter.add("All log levels");
		for (int i=0; i<Preferences.LOG_LEVELS.length; i++)
			logLevelFilter.add(Preferences.LOG_LEVELS[i]);			
		
		logLevelFilter.select(0);
		logLevelFilter.addSelectionListener(new SelectionListener() {		
			public void widgetSelected(SelectionEvent e) {
				String newLogFilter = "";
				if (logLevelFilter.getSelectionIndex()>0)
					newLogFilter = logLevelFilter.getText();
				
				if (!filter.getLogLevel().equals(newLogFilter)) {
					filter.setLogLevel(newLogFilter);
					messageCount = 0;
					table.refresh();
				}
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		final Text regExpFilter = new Text(page, SWT.SEARCH | SWT.ICON_SEARCH | SWT.SINGLE);
		final Button isRegExp = new Button(page, SWT.CHECK);
		isRegExp.setText("Regular Expression");

//		regExpFilter.setText("Regexp filter");
		regExpFilter.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				String newRegex = "";
				if (regExpFilter.getText()!=null)
					newRegex = regExpFilter.getText();
				
				if (!filter.getRegex().equals(newRegex)) {
					filter.setRegex(newRegex);
					messageCount = 0;
					table.refresh();
				}
			}
		});
		
		isRegExp.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent arg0) {
				boolean newRegex = isRegExp.getSelection();
				if (filter.isRegex()!=newRegex) {
					filter.setIsRegex(newRegex);
					messageCount = 0;
					table.refresh();
				}
			}
			
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		
		
		GridData regexGridData = new GridData();
		regexGridData.horizontalAlignment = GridData.FILL;	
		regexGridData.grabExcessHorizontalSpace = true;

		regExpFilter.setLayoutData(regexGridData);
		
		
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
		messageCountLabel.setText("" + model.getSize() + " messages");
		
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
		// Get the content for the viewer, setInput will call getElements in the
		// contentProvider
		table.setInput(dataModel.getMessages());

		table.setComparator(comparator);
		logObjectViewerFilter.setFilter(filter);
		table.addFilter(logObjectViewerFilter);
		
		table.getTable().addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event event) {
				Point pt = new Point(event.x, event.y);
				TableItem item = table.getTable().getItem(pt);
				if (item == null)
					return;
				
				LogObject log = (LogObject) item.getData();
				if (log != null) {
					LogMessageDialog dialog = new LogMessageDialog(parent.getShell(), log);
					dialog.open();
				}
			}
		});
				
		setTableSize();		
		page.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				setTableSize();
			}
		});
		
		page.pack();
		
		if (shouldRefresh)
			timerUpdateTable();
		
		return page;
	}
	
	public void timerUpdateTable() {
		if (parent==null || parent.isDisposed())
			return;
		
		parent.getDisplay().timerExec(Preferences.getLogViewRefreshPeriod(), new Runnable() {
			public void run() {
				if (parent==null || parent.isDisposed()) {
					return;
				}

				updateTable();
				
				if (shouldRefresh)
					timerUpdateTable();
			}
		});
	}
	
	public void updateTable() {
		messageCount = 0;
		LogObject[] messages = dataModel.getMessages();						
		messageCountLabel.setText("" + messages.length + " messages");
		table.setInput(messages);
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
			table.getTable().getColumn(0).setWidth(50);
			table.getTable().getColumn(1).setWidth(200);
			table.getTable().getColumn(2).setWidth(150);
			table.getTable().getColumn(3).setWidth(50);
			table.getTable().getColumn(4).setWidth(50);
			table.getTable().getColumn(5).setWidth(width-550);
			table.getTable().getColumn(6).setWidth(50);
			table.getTable().setSize(area.width, area.height);
		} else {
			// table is getting bigger so make the table 
			// bigger first and then make the columns wider
			// to match the client area width
			table.getTable().setSize(area.width, area.height);
			table.getTable().getColumn(0).setWidth(50);
			table.getTable().getColumn(1).setWidth(200);
			table.getTable().getColumn(2).setWidth(150);
			table.getTable().getColumn(3).setWidth(50);
			table.getTable().getColumn(4).setWidth(50);
			table.getTable().getColumn(5).setWidth(width-550);
			table.getTable().getColumn(6).setWidth(50);
		}
	}
	
	
	private void createColumns(final Composite parent, final TableViewer table) {		
		// First column is for the first name
		TableViewerColumn col = createTableViewerColumn("", null);
		col.setLabelProvider(new LogMessageColorProvider() {
			@Override
			public String 	getText(Object element)  {
				messageCount++;
				return "" + messageCount;
			}
		});
		
		col = createTableViewerColumn("Timestamp", LogComparatorField.timeStamp);		
		col.setLabelProvider(new LogMessageColorProvider() {		
			@Override
			public String 	getText(Object element)  {
				LogObject log = (LogObject) element;
				return AskapHelper.getFormatedData(log.getTimeStamp(), null);
			}
		});
		
		table.getTable().setSortDirection(SWT.UP);
		table.getTable().setSortColumn(col.getColumn());


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
		
		col = createTableViewerColumn("Tag", LogComparatorField.tag);		
		col.setLabelProvider(new LogMessageColorProvider() {
			@Override
			public String 	getText(Object element)  {
				LogObject log = (LogObject) element;
				return log.getTag();
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
		
		if (compField == null)
			return viewerColumn;
		
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comparator.setComparatorField(compField);
				
				int dir = table.getTable().getSortDirection();
				if (table.getTable().getSortColumn() == column) {
					dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				} else {
					dir = SWT.DOWN;
				}
				table.getTable().setSortDirection(dir);
				table.getTable().setSortColumn(column);
				table.refresh();
			}
		});
		return viewerColumn;
	}

	public void stop() {
		try {
			haltUpdates();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not stop subscribing to log messages", e);
		}
	}

	public void haltUpdates() {
		shouldRefresh = false;
	}

	public void startUpdates() {
		if (shouldRefresh)
			return;
		
		shouldRefresh = true;
		timerUpdateTable();
	}
}
