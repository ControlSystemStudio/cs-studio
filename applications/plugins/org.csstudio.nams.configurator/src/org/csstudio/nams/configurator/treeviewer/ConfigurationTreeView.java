package org.csstudio.nams.configurator.treeviewer;

import java.util.ArrayList;

import org.csstudio.ams.configurationStoreService.declaration.ConfigurationEditingStoreService;
import org.csstudio.ams.configurationStoreService.declaration.ConfigurationStoreService;
import org.csstudio.ams.service.logging.declaration.Logger;
import org.csstudio.nams.configurator.treeviewer.actions.NewEntryAction;
import org.csstudio.nams.configurator.treeviewer.model.ConfigurationModel;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.SortGroupBean;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

public class ConfigurationTreeView extends ViewPart {

	public static final String ID = "org.csstudio.nams.configurator.views.ConfigurationTreeView";

	private TreeViewer _viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action _newAction;
	private Action doubleClickAction;

	private ConfigurationModel configurationModel;

	private static ConfigurationEditingStoreService editingStoreService;

	private static ConfigurationStoreService configurationService;

	private static Logger logger;

	/**
	 * The constructor.
	 */
	public ConfigurationTreeView() {
		// TODO: hier sollten Gruppen übergeben werden
		this.configurationModel = new ConfigurationModel(
				new ArrayList<SortGroupBean>());
	}

	/**
	 * {@inheritDoc}
	 */
	public void createPartControl(Composite parent) {
		_viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(_viewer);
		_viewer.setContentProvider(new ConfigurationContentProvider());
		_viewer.setLabelProvider(new ConfigurationLabelProvider());
		_viewer.setSorter(new ConfigurationSorter());
		_viewer.setInput(configurationModel.getChildren());
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		this.getSite().setSelectionProvider(_viewer);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ConfigurationTreeView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(_viewer.getControl());
		_viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, _viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(_newAction);
		manager.add(new Separator());
	}

	private void fillContextMenu(IMenuManager manager) {
		if (!_viewer.getSelection().isEmpty()) {
			manager.add(_newAction);
		}
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(_newAction);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
		_newAction = new NewEntryAction(_viewer, this.configurationModel);

		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = _viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				showMessage("Double-click detected on " + obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		_viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(_viewer.getControl().getShell(),
				"Configuration Tree Viewer", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		_viewer.getControl().setFocus();
	}

	protected ConfigurationStoreService getConfigurationStoreService() {
		return ConfigurationTreeView.configurationService;
	}

	protected ConfigurationEditingStoreService getConfigurationEditingStoreService() {
		return ConfigurationTreeView.editingStoreService;
	}

	protected Logger getLogger() {
		return ConfigurationTreeView.logger;
	}

	public static void staticInjectEditingStoreService(
			ConfigurationEditingStoreService editingStoreService) {
		ConfigurationTreeView.editingStoreService = editingStoreService;
	}

	public static void staticInjectStoreService(
			ConfigurationStoreService configurationService) {
		ConfigurationTreeView.configurationService = configurationService;
	}

	public static void staticInjectLogger(Logger logger) {
		ConfigurationTreeView.logger = logger;
	}
}