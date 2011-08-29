package org.csstudio.channelviewer.views;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelFinderException;
import gov.bnl.channelfinder.api.ChannelUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.csstudio.channelviewer.util.FindChannels;
import org.csstudio.utility.channelfinder.Activator;
import org.csstudio.utility.channelfinder.ChannelQuery;
import org.csstudio.utility.channelfinder.ChannelQueryListener;
import org.csstudio.utility.channelfinder.ChannelQuery.Builder;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.swtdesigner.TableViewerColumnSorter;

/**
 * 
 */

public class ChannelsView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.csstudio.channelfinder.views.ChannelsView";
	private static Logger log = Logger.getLogger(ID);

	private static int instance;
	private Text text;
	private Button search;
	private GridLayout layout;

	// private Collection<ICSSChannel> channelsList = new

	private Table table;
	private TableViewer tableViewer;

	// Simple Model
	Collection<Channel> channels = new ArrayList<Channel>();

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */

	/**
	 * The constructor.
	 */
	public ChannelsView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		createGUI(parent);
		tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.MULTI | SWT.VIRTUAL);
		table = tableViewer.getTable();
		table.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent e) {
			}
		});
		// Make the Columns stretch with the table
		table.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(ControlEvent e) {
				Rectangle area = table.getClientArea();
				Point size = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				ScrollBar vBar = table.getVerticalBar();
				int width = area.width - table.computeTrim(0, 0, 0, 0).width
						- vBar.getSize().x;
				if (size.y > area.height + table.getHeaderHeight()) {
					// Subtract the scrollbar width from the total column width
					// if a vertical scrollbar will be required
					Point vBarSize = vBar.getSize();
					width -= vBarSize.x;
				}
				Point oldSize = table.getSize();
				TableColumn[] columns;
				if (oldSize.x > area.width) {
					// table is getting smaller so make the columns
					// smaller first and then resize the table to
					// match the client area width
					columns = table.getColumns();
					int newWidth = area.width / columns.length >= 100 ? area.width
							/ columns.length
							: 100;
					for (TableColumn tableColumn : columns) {
						tableColumn.setWidth(newWidth);
					}
				} else {
					// table is getting bigger so make the table
					// bigger first and then make the columns wider
					// to match the client area width
					columns = table.getColumns();
					int newWidth = area.width / columns.length >= 100 ? area.width
							/ columns.length
							: 100;
					for (TableColumn tableColumn : columns) {
						tableColumn.setWidth(newWidth);
					}
				}
			}
		});
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		TableViewerColumn channelNameColumn = new TableViewerColumn(
				tableViewer, SWT.NONE);
		channelNameColumn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				cell.setText(((Channel) cell.getElement()).getName());
			}
		});
		new TableViewerColumnSorter(channelNameColumn) {
			@Override
			protected Object getValue(Object o) {
				return ((Channel) o).getName();
			}
		};
		TableColumn tblclmnChannelName = channelNameColumn.getColumn();
		tblclmnChannelName.setWidth(100);
		tblclmnChannelName.setText("Channel Name");

		TableViewerColumn channelOwnerColumn = new TableViewerColumn(
				tableViewer, SWT.NONE);
		channelOwnerColumn.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				cell.setText(((Channel) cell.getElement()).getOwner());
			}
		});
		new TableViewerColumnSorter(channelOwnerColumn) {
			@Override
			protected Object getValue(Object o) {
				return ((Channel) o).getOwner();
			}
		};
		TableColumn tblclmnOwner = channelOwnerColumn.getColumn();
		tblclmnOwner.setWidth(100);
		tblclmnOwner.setText("Owner");
		tableViewer.setContentProvider(new ChannelContentProvider());
		// Add this table as a selection provider
		getSite().setSelectionProvider(tableViewer);
		// Add Context menu
		MenuManager menuManager = new MenuManager();
		menuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(new Separator(
						IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});
		Menu menu = menuManager.createContextMenu(table);
		table.setMenu(menu);
		getSite().registerContextMenu(menuManager, tableViewer);
	}

	/** @return a new view instance */
	public static String createNewInstance() {
		++instance;
		return Integer.toString(instance);
	}

	private void createGUI(Composite parent) {
		layout = new GridLayout(2, false);
		parent.setLayout(layout);

		text = new Text(parent, SWT.SINGLE | SWT.BORDER | SWT.SEARCH);
		text.setToolTipText("space seperated search criterias, patterns may include * and ? wildcards\r\nchannelNamePatter\r\npropertyName=propertyValuePattern1,propertyValuePattern2\r\nTags=tagNamePattern\r\nEach criteria is logically ANDed and , or || seperated values are logically ORed\r\n");
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		text.setLayoutData(gd);

		search = new Button(parent, SWT.PUSH);
		search.setText("Search");
		search.setToolTipText("search for channels");
		search.setEnabled(false);
		GridData viewerGD = new GridData();
		viewerGD.horizontalSpan = 2;
		viewerGD.grabExcessHorizontalSpace = true;
		viewerGD.grabExcessVerticalSpace = true;
		viewerGD.horizontalAlignment = SWT.FILL;
		viewerGD.verticalAlignment = SWT.FILL;

		int col = 0;
		col++;

		text.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				if (e.detail == SWT.CANCEL) {
					log.info("Search cancelled");
				} else {
					log.info("Searching for: " + text.getText());
					search(text.getText());
				}
			}
		});

		text.addListener(SWT.KeyUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (text.getText().toCharArray().length > 0) {
					search.setEnabled(true);
				} else {
					search.setEnabled(false);
				}
			}
		});

		search.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				search(text.getText());
			}
		});

	}

	private void search(String text) {
		if (text.equalsIgnoreCase("*")) {
			if (!MessageDialog.openConfirm(getSite().getShell(),
					"Confirm Search",
					"Are you sure you want to search for all channels?"))
				return;
		}
		final ChannelQuery query = Builder.query(text).create();
		final ChannelsView view = this;
		query.addChannelQueryListener(new ChannelQueryListener() {

			@Override
			public void getQueryResult() {
				PlatformUI.getWorkbench().getDisplay()
						.asyncExec(new Runnable() {
							@Override
							public void run() {
								Exception e = query.getLastException();
								if (e == null) {
									view.updateList(query.getResult());
								} else if (e instanceof ChannelFinderException) {
									e.printStackTrace();
									Status status = new Status(Status.ERROR,
											Activator.PLUGIN_ID,
											((ChannelFinderException) e)
													.getStatus()
													.getStatusCode(), e
													.getMessage(), e.getCause());
									ErrorDialog.openError(view.getSite()
											.getShell(),
											"Error retrieving channels", e
													.getMessage(), status);
								} else {
									e.printStackTrace();
									Status status = new Status(Status.ERROR,
											Activator.PLUGIN_ID,
											e.getMessage(), e);
									ErrorDialog.openError(view.getSite()
											.getShell(),
											"Error retrieving channels", e
													.getMessage(), status);

								}
							}
						});
			}
		});
		query.execute();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		tableViewer.getControl().setFocus();
	}

	public synchronized void updateList(Collection<Channel> newChannels) {
		// Clear the channel list;
		channels.clear();
		channels.addAll(newChannels);
		tableViewer.setInput(channels.toArray());
		tableViewer.setItemCount(channels.size());
		// Remove all old columns
		// TODO add the additional columns in the correct sorted order.
		while (tableViewer.getTable().getColumnCount() > 2) {
			tableViewer.getTable().getColumn(table.getColumnCount() - 1)
					.dispose();
		}
		// Add a new column for each property
		for (String propertyName : ChannelUtil.getPropertyNames(newChannels)) {
			// Property Column
			TableViewerColumn channelPropertyColumn = new TableViewerColumn(
					tableViewer, SWT.NONE);
			channelPropertyColumn
					.setLabelProvider(new PropertyCellLabelProvider(
							propertyName));
			new TableViewerChannelPropertySorter(channelPropertyColumn,
					propertyName);
			TableColumn tblclmnNumericprop = channelPropertyColumn.getColumn();
			// tcl_composite.setColumnData(tblclmnNumericprop, new
			// ColumnPixelData(
			// 100, true, true));

			tblclmnNumericprop.setText(propertyName);
			tblclmnNumericprop.setWidth(100);
		}
		// Add a new column for each Tag
		for (String tagName : ChannelUtil.getAllTagNames(newChannels)) {
			// Tag Column
			TableViewerColumn channelTagColumn = new TableViewerColumn(
					tableViewer, SWT.NONE);
			channelTagColumn
					.setLabelProvider(new TagCellLabelProvider(tagName));
			new TableViewerChannelTagSorter(channelTagColumn, tagName);
			TableColumn tblclmnNumericprop = channelTagColumn.getColumn();
			// tcl_composite.setColumnData(tblclmnNumericprop, new
			// ColumnPixelData(
			// 100, true, true));
			tblclmnNumericprop.setText(tagName);
			tblclmnNumericprop.setWidth(100);
		}
		// calculate column size since adding removing colums does not trigger a control resize event.
		Rectangle area = table.getClientArea();
		Point size = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		ScrollBar vBar = table.getVerticalBar();
		int width = area.width - table.computeTrim(0, 0, 0, 0).width
				- vBar.getSize().x;
		if (size.y > area.height + table.getHeaderHeight()) {
			// Subtract the scrollbar width from the total column width
			// if a vertical scrollbar will be required
			Point vBarSize = vBar.getSize();
			width -= vBarSize.x;
		}
		Point oldSize = table.getSize();
		TableColumn[] columns;
		if (oldSize.x > area.width) {
			// table is getting smaller so make the columns
			// smaller first and then resize the table to
			// match the client area width
			columns = table.getColumns();
			int newWidth = area.width / columns.length >= 100 ? area.width
					/ columns.length : 100;
			for (TableColumn tableColumn : columns) {
				tableColumn.setWidth(newWidth);
			}
		} else {
			// table is getting bigger so make the table
			// bigger first and then make the columns wider
			// to match the client area width
			columns = table.getColumns();
			int newWidth = area.width / columns.length >= 100 ? area.width
					/ columns.length : 100;
			for (TableColumn tableColumn : columns) {
				tableColumn.setWidth(newWidth);
			}
		}
		tableViewer.refresh();
		// table.notifyListeners(0, //new Event()));
	}
}