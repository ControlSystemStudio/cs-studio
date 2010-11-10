package org.csstudio.channelfinder.views;

import static gov.bnl.channelfinder.api.ChannelUtil.getPropertyNames;
import static gov.bnl.channelfinder.api.ChannelUtil.getTagNames;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.csstudio.utility.channel.ICSSChannel;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

/**
 * 
 */

public class ChannelFinderView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.csstudio.channelfinder.views.ChannelfinderView";

	private static int instance;

	private TableViewer viewer;
	private Text text;
	private Button search;
	private GridLayout layout;

	// private Collection<ICSSChannel> channelsList = new
	// HashSet<ICSSChannel>();

	private List<ICSSChannel> rootList;
	private EventList<ICSSChannel> eventList;
	private SortedList<ICSSChannel> sortedList;

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
	public ChannelFinderView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		createGUI(parent);
		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(),
				"org.csstudio.channelfinder.viewer");
		hookContextMenu();
		getSite().setSelectionProvider(viewer);
	}

	/** @return a new view instance */
	public static String createNewInstance() {
		++instance;
		return Integer.toString(instance);
	}

	private void createGUI(Composite parent) {
		rootList = new ArrayList<ICSSChannel>();
		eventList = GlazedLists.eventList(rootList);
		sortedList = new SortedList<ICSSChannel>(eventList, null);
		sortedList.addListEventListener(new ListEventListener<ICSSChannel>() {

			@Override
			public void listChanged(ListEvent<ICSSChannel> listChanges) {
				try {
					viewer.getTable().setRedraw(false);

					// get the list PRIOR to looping, otherwise it won't be the
					// same
					// list as it's modified continuously
					final List<ICSSChannel> changeList = listChanges
							.getSourceList();

					while (listChanges.next()) {
						int sourceIndex = listChanges.getIndex();
						int changeType = listChanges.getType();
						switch (changeType) {
						case ListEvent.DELETE:
							// note the remove of the object fetched from the
							// event list
							// here, we need to remove by index which the viewer
							// does
							// not support
							// and we're removing from the raw list, not the
							// filtered
							// list
							viewer.remove(eventList.get(sourceIndex));
							viewer.refresh(eventList.get(sourceIndex), true);
							break;
						case ListEvent.INSERT:
							final ICSSChannel obj = changeList.get(sourceIndex);
							viewer.insert(obj, sourceIndex);
							break;
						case ListEvent.UPDATE:
							break;
						}
					}
				} catch (Exception err) {
					err.printStackTrace();
				} finally {
					viewer.setItemCount(sortedList.size());
					viewer.getTable().setRedraw(true);
					// we could do detailed refreshes, but this isn't much of a
					// performance hit
					viewer.refresh(true);
				}
			}
		});

		layout = new GridLayout(2, false);
		parent.setLayout(layout);

		text = new Text(parent, SWT.SINGLE | SWT.BORDER | SWT.SEARCH);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		text.setLayoutData(gd);

		search = new Button(parent, SWT.PUSH);
		search.setText("Search");
		search.setToolTipText("search for channels");
		search.setLayoutData(new GridData());
		search.setEnabled(false);

		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER | SWT.VIRTUAL | SWT.FULL_SELECTION);
		GridData viewerGD = new GridData();
		viewerGD.horizontalSpan = 2;
		viewerGD.grabExcessHorizontalSpace = true;
		viewerGD.grabExcessVerticalSpace = true;
		viewerGD.horizontalAlignment = SWT.FILL;
		viewerGD.verticalAlignment = SWT.FILL;
		viewer.getControl().setLayoutData(viewerGD);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);

		int col = 0;
		createTableColumn(viewer, "Channel Name", "channel name", col, 100,
				new GlazedSortNameComparator(col, SWT.NONE));
		col++;
		createTableColumn(viewer, "Owner", "owner", col, 100,
				new GlazedSortOwnerComparator(col, SWT.NONE));

		viewer.setContentProvider(new ChannelFinderViewContentProvider(
				sortedList));
		viewer.setLabelProvider(new ChannelFinderViewLabelProvider(sortedList));
		viewer.setInput(sortedList);
		viewer.setItemCount(sortedList.size());
		viewer.refresh();

		text.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				if (e.detail == SWT.CANCEL) {
					System.out.println("Search cancelled");
				} else {
					System.out.println("Searching for: " + text.getText());
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
				// updateList(SearchChannels.testData());
				viewer.refresh();
			}
		});

	}

	private void createTableColumn(TableViewer viewer, String text,
			String tooltip, int index, int width,
			final IDirectionalComparator<ICSSChannel> comparator) {
		final int col = index;
		final TableColumn tvc = new TableColumn(viewer.getTable(), SWT.NONE);
		tvc.setText(text);
		tvc.setToolTipText(tooltip);
		tvc.setWidth(width);
		tvc.setResizable(true);
		tvc.setMoveable(false);
		tvc.pack();
		tvc.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				sortColumn(col, comparator);
			}
		});
	}

	private void sortColumn(int col,
			IDirectionalComparator<ICSSChannel> comparator) {
		int dir = SWT.UP;
		int current = viewer.getTable().getSortDirection();
		TableColumn tc = viewer.getTable().getColumn(col);
		if (viewer.getTable().getSortColumn() == tc) {
			dir = (current == SWT.UP ? SWT.DOWN : SWT.UP);
		}

		viewer.getTable().setSortColumn(tc);
		viewer.getTable().setSortDirection(dir);
		comparator.setDirection(dir);
		// now tell the sorted list we've updated
		sortedList.setComparator(comparator);
	}

	private void search(String text) {
		if (text.equalsIgnoreCase("*")) {
			if (!MessageDialog.openConfirm(getSite().getShell(),
					"Confirm Search",
					"Are you sure you want to search for all channels?"))
				return;
		}

		Job job = new SearchChannels("search", text, this);
		job.schedule();
	}

	private void hookContextMenu() {
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		Menu menu = menuManager.createContextMenu(viewer.getTable());
		// Set the MenuManager
		fillContextMenu(menuManager);
		viewer.getTable().setMenu(menu);
		getSite().registerContextMenu(menuManager, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(new Separator());
	}

	private void fillContextMenu(IMenuManager manager) {
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public synchronized void updateList(Collection<ICSSChannel> channels) {
		// Clear the channel list;
		eventList.getReadWriteLock().writeLock().lock();
		try {
			eventList.clear();
			eventList.addAll(channels);
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
		// Remove all old columns
		// TODO add the additional columns in the correct sorted order.
		while (viewer.getTable().getColumnCount() > 2) {
			viewer.getTable().getColumn(viewer.getTable().getColumnCount() - 1)
					.dispose();
		}
		int col = 1;
		// Add a new column for each property
		for (String propertyName : getAllPropertyNames(eventList)) {
			col++;
			createTableColumn(viewer, propertyName, propertyName, col, 100,
					new GlazedSortPropertyComparator(propertyName, col,
							SWT.NONE));
		}
		// Add a new column for each Tag
		for (String tagName : getAllTagNames(eventList)) {
			col++;
			createTableColumn(viewer, tagName, tagName, col, 100,
					new GlazedSortTagComparator(tagName, col, SWT.NONE));
		}

		viewer.setLabelProvider(new ChannelFinderViewLabelProvider(sortedList));
	}

	private Collection<String> getAllTagNames(
			Collection<ICSSChannel> channelItems) {
		Collection<String> tagNames = new HashSet<String>();
		for (ICSSChannel channelItem : channelItems) {
			tagNames.addAll(getTagNames(channelItem.getChannel()));
		}
		return tagNames;

	}

	private Collection<String> getAllPropertyNames(
			Collection<ICSSChannel> channelItems) {
		Collection<String> propertyNames = new HashSet<String>();
		for (ICSSChannel channelItem : channelItems) {
			propertyNames.addAll(getPropertyNames(channelItem.getChannel()));
		}
		return propertyNames;
	}
}