package org.csstudio.channelfinder.views;

import static gov.bnl.channelfinder.api.ChannelUtil.*;

import gov.bnl.channelfinder.api.Channel;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
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

//	private Collection<Channel> channelsList = new HashSet<Channel>();
	private Collection<ChannelItem> channelsList = new HashSet<ChannelItem>();
	// union of all the properties/tags of all channels.
//	private Collection<String> allProperties = new TreeSet<String>();
//	private Collection<String> allTags = new TreeSet<String>();


	private Action doubleClickAction;

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
		PlatformUI
				.getWorkbench()
				.getHelpSystem()
				.setHelp(viewer.getControl(),
						"org.csstudio.channelfinder.viewer");
		hookContextMenu();
		getSite().setSelectionProvider(viewer);
	}
	
	/** @return a new view instance */
    public static String createNewInstance()
    {
        ++instance;
        return Integer.toString(instance);
    }


	private void createGUI(Composite parent) {
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
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		GridData viewerGD = new GridData();
		viewerGD.horizontalSpan = 2;
		viewerGD.grabExcessHorizontalSpace = true;
		viewerGD.grabExcessVerticalSpace = true;
		viewerGD.horizontalAlignment = SWT.FILL;
		viewerGD.verticalAlignment = SWT.FILL;
		viewer.getControl().setLayoutData(viewerGD);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);

		new StringColumnSorter(viewer, createTableColumn(viewer,
				"Channel Name", "channel name", SWT.DOWN, false));

		new StringColumnSorter(viewer, createTableColumn(viewer, "Owner",
				"Owner Name", SWT.DOWN, false));

//		viewer.setContentProvider(new ChannelFinderViewContentProvider(
//				channelsList));
		viewer.setContentProvider(new ChannelFinderViewContentProvider());
		viewer.setInput(channelsList);
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

	private TableViewerColumn createTableColumn(TableViewer viewer,
			String text, String tooltip, int initialDirection,
			boolean keepDirection) {
		TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setText(text);
		column.getColumn().setWidth(100);
		column.getColumn().setToolTipText(tooltip);
		column.getColumn().setResizable(true);
		return column;
	}

	private void search(String text) {
		if (text.equalsIgnoreCase("*"))
        {
            if (! MessageDialog.openConfirm(getSite().getShell(),
                "Confirm Search",
                "Are you sure you want to search for all channels?"))
                return;
        }
		
		Job job = new SearchChannels("search", text, this);
		job.schedule();
//		BusyIndicator.showWhile(viewer.getControl().getDisplay(), (Runnable) job);
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

	private void makeActions() {
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				showMessage("Double-click detected on " + obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(),
				"Channel Finder View", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public synchronized void updateList(Collection<ChannelItem> channels) {
		// Clear the channel list;
		channelsList.clear();			
		channelsList.addAll(channels);
		
		// Remove all old columns
		// TDB add the additional columns in the correct sorted order.
		while (viewer.getTable().getColumnCount() > 2) {
			viewer.getTable().getColumn(viewer.getTable().getColumnCount() - 1)
					.dispose();
		}

		// Add a new column for each property
		for (String propertyName : getAllPropertyNames(channelsList)) {
			new PropertySorter(propertyName, viewer, createTableColumn(viewer,
					propertyName, propertyName, SWT.DOWN, false));
		}
		// Add a new column for each Tag
		for (String tagName : getAllTagNames(channelsList)) {
			new TagSorter(tagName, viewer, createTableColumn(viewer, tagName,
					tagName, SWT.DOWN, false));
		}

		viewer.setLabelProvider(new ChannelFinderViewLabelProvider(
				getAllPropertyNames(channelsList), getAllTagNames(channelsList)));
	}
	
	private Collection<String> getAllTagNames(Collection<ChannelItem> channelItems){
		Collection<String> tagNames = new HashSet<String>();
		for (ChannelItem channelItem : channelItems) {
			tagNames.addAll(getTagNames(channelItem.getChannel()));
		}
		return tagNames;
		
	}
	
	private Collection<String> getAllPropertyNames(Collection<ChannelItem> channelItems){
		Collection<String> propertyNames = new HashSet<String>();
		for (ChannelItem channelItem : channelItems) {
			propertyNames.addAll(getPropertyNames(channelItem.getChannel()));
		}
		return propertyNames;
	}
}