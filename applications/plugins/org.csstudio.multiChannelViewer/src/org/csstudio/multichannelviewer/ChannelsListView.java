package org.csstudio.multichannelviewer;

import static org.csstudio.utility.channel.CSSChannelUtils.getCSSChannelPropertyNames;
import static org.csstudio.utility.channel.CSSChannelUtils.getCSSChannelTagNames;

import java.util.List;

import org.csstudio.multichannelviewer.model.CSSChannelGroup;
import org.csstudio.multichannelviewer.model.IChannelGroup;
import org.csstudio.utility.channel.ICSSChannel;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.data.VMultiDouble;

import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

public class ChannelsListView extends MultiChannelPlotAwareView implements
		ListEventListener<ICSSChannel> {

	public static final String ID = "org.csstudio.multichannelviewer.ChannelListView";

	private static int instance;
	// Model
	private IChannelGroup channelsGroup;
	// UI components
	private GridLayout layout;
	private TableViewer viewer;

	public ChannelsListView() {
	}

	public void setChannelsGroup(IChannelGroup channelsGroup) {
		this.channelsGroup.addChannels(channelsGroup.getList());
	}

	/** @return a new view instance */
	public static String createNewInstance() {
		++instance;
		return Integer.toString(instance);
	}

	private void createGUI(Composite parent) {
		if (channelsGroup == null) {
			channelsGroup = new CSSChannelGroup("Empty Group");
		}

		layout = new GridLayout(1, false);
		parent.setLayout(layout);
		viewer = new TableViewer(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION );
		GridData viewerGD = new GridData();
		viewerGD.grabExcessHorizontalSpace = true;
		viewerGD.grabExcessVerticalSpace = true;
		viewerGD.horizontalAlignment = SWT.FILL;
		viewerGD.verticalAlignment = SWT.FILL;
		viewer.getControl().setLayoutData(viewerGD);

		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		createTable(viewer);

		viewer.setContentProvider(new ChannelGroupContentProvider());
		viewer.setLabelProvider(new ChannelGroupLabelProvider(channelsGroup));
		viewer.setInput(channelsGroup.getList());
		viewer.setItemCount(channelsGroup.getList().size());
		viewer.refresh();

		channelsGroup.addEventListListener(this);
		// context menu
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(viewer.getTable());
		// Set the MenuManager
		viewer.getTable().setMenu(menu);
		getSite().registerContextMenu(menuManager, viewer);
		getSite().setSelectionProvider(viewer);
	}

	private void createNewTable(TableViewer viewer) {
		// TODO
		while (viewer.getTable().getColumnCount() > 0) {
			viewer.getTable().getColumn(viewer.getTable().getColumnCount() - 1)
					.dispose();
		}
		createTable(viewer);
	}

	private void createTable(TableViewer viewer) {
		int col = 0;
		createTableColumn(viewer, "Channel Name", "channel name", col, 100,
				new GlazedSortNameComparator(col, SWT.NONE));
		col++;
		createTableColumn(viewer, "Owner", "owner", col, 100,
				new GlazedSortOwnerComparator(col, SWT.NONE));
		// Add a new column for each property
		for (String propertyName : getCSSChannelPropertyNames(channelsGroup
				.getList())) {
			col++;
			createTableColumn(viewer, propertyName, propertyName, col, 100,
					new GlazedSortPropertyComparator(propertyName, col,
							SWT.NONE));
		}
		// Add a new column for each Tag
		for (String tagName : getCSSChannelTagNames(channelsGroup.getList())) {
			col++;
			createTableColumn(viewer, tagName, tagName, col, 100,
					new GlazedSortTagComparator(tagName, col, SWT.NONE));
		}

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
		channelsGroup.setCompatator(comparator);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public void listChanged(ListEvent<ICSSChannel> listChanges) {
		// TODO move this to the channelsGroup and provide a separate event.
		if (viewer.getTable().getColumnCount() != channelsGroup
				.getUniqueColCount()) {
			createNewTable(viewer);
			viewer.setInput(channelsGroup.getList());
			viewer.setItemCount(channelsGroup.getList().size());
			viewer.setLabelProvider(new ChannelGroupLabelProvider(channelsGroup));
		}
		try {
			viewer.getTable().setRedraw(false);
			// get the list PRIOR to looping, otherwise it won't be the same
			// list as it's modified continuously
			final List<ICSSChannel> changeList = listChanges.getSourceList();

			while (listChanges.next()) {
				int sourceIndex = listChanges.getIndex();
				int changeType = listChanges.getType();
				switch (changeType) {
				case ListEvent.DELETE:
					// note the remove of the object fetched from the event list
					// here, we need to remove by index which the viewer does
					// not support and we're removing from the raw list, not the
					// filtered list.
					viewer.remove(channelsGroup.getElementAtIndex(sourceIndex));
					viewer.refresh(
							channelsGroup.getElementAtIndex(sourceIndex), true);
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
			viewer.setItemCount(channelsGroup.getList().size());
			viewer.getTable().setRedraw(true);
			// we could do detailed refreshes, but this isn't much of a
			// performance hit
			viewer.refresh(true);
		}
	}

	@Override
	protected void doCreatePartControl(Composite parent) {
		createGUI(parent);
	}

	@Override
	protected void updateChannelGroup(CSSChannelGroup oldGroup,
			CSSChannelGroup newGroup) {
		// System.out
		// .println("Channels control view update channel group event recieved");
		this.channelsGroup = (newGroup == null) ? new CSSChannelGroup(
				"Empty Group") : newGroup;
		createNewTable(viewer);

		viewer.setLabelProvider(new ChannelGroupLabelProvider(channelsGroup));
		viewer.setInput(channelsGroup.getList());
		viewer.setItemCount(channelsGroup.getList().size());
		viewer.refresh();

		channelsGroup.addEventListListener(this);
	}

	@Override
	protected void updatePV(PV<VMultiDouble> oldPv, PV<VMultiDouble> newPv) {
		// nothing todo since this view does not display any live data
	}

	@Override
	public void dispose() {
		channelsGroup.removeEventListListener(this);
		super.dispose();
	}

}
