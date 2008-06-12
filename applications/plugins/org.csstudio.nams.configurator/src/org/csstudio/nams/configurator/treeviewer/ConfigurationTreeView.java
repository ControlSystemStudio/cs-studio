package org.csstudio.nams.configurator.treeviewer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;

import org.csstudio.ams.service.logging.declaration.Logger;
import org.csstudio.nams.configurator.treeviewer.actions.OpenConfigurationEditor;
import org.csstudio.nams.configurator.treeviewer.model.ConfigurationModel;
import org.csstudio.nams.configurator.treeviewer.model.IConfigurationModel;
import org.csstudio.nams.configurator.treeviewer.model.SortGroupBean;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationBean;
import org.csstudio.nams.configurator.treeviewer.model.treecomponents.IConfigurationRoot;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;

public class ConfigurationTreeView extends ViewPart {

	public static final String ID = "org.csstudio.nams.configurator.views.ConfigurationTreeView";

	private TreeViewer _viewer;

	private Action doubleClickAction;

	private ConfigurationModel configurationModel;

//	private static ConfigurationEditingStoreService editingStoreService;
//
//	private static ConfigurationStoreService configurationService;

	private static Logger logger;

	/**
	 * The constructor.
	 */
	public ConfigurationTreeView() {
		// TODO: hier sollten Gruppen übergeben werden
		this.configurationModel = new ConfigurationModel(
				new ArrayList<SortGroupBean>());

		// melde dich beim Model als Listener an
		this.configurationModel.addPropertyChangeListener(this
				.getPropertyChangeListener());
	}

	/**
	 * {@inheritDoc}
	 */
	public void createPartControl(Composite parent) {
		_viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		_viewer.setContentProvider(new ConfigurationContentProvider());
		_viewer.setLabelProvider(new ConfigurationLabelProvider());
		_viewer.setSorter(new ConfigurationSorter());
		_viewer.setInput(configurationModel.getChildren());
		_viewer.setSorter(new ConfiguratorViewerSorter());
		hookContextMenu();
		hookDoubleClickAction();

		this.getSite().setSelectionProvider(_viewer);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		Menu menu = menuMgr.createContextMenu(_viewer.getControl());
		_viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, _viewer);
	}

	private void hookDoubleClickAction() {
		_viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				// prüfe, ob ein ChildElement ausgewählt wurde
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				if (selection.getFirstElement() instanceof IConfigurationBean) {
					doubleClickAction = new OpenConfigurationEditor(
							(IConfigurationBean) selection.getFirstElement(),
							configurationModel);

					doubleClickAction.run();
				}
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		_viewer.getControl().setFocus();
	}

//	protected ConfigurationStoreService getConfigurationStoreService() {
//		return ConfigurationTreeView.configurationService;
//	}
//
//	protected ConfigurationEditingStoreService getConfigurationEditingStoreService() {
//		return ConfigurationTreeView.editingStoreService;
//	}

	public static Logger getLogger() {
		return ConfigurationTreeView.logger;
	}
//
//	public static void staticInjectEditingStoreService(
//			ConfigurationEditingStoreService editingStoreService) {
//		ConfigurationTreeView.editingStoreService = editingStoreService;
//	}
//
//	public static void staticInjectStoreService(
//			ConfigurationStoreService configurationService) {
//		ConfigurationTreeView.configurationService = configurationService;
//	}

	public static void staticInjectLogger(Logger logger) {
		ConfigurationTreeView.logger = logger;
	}

	/**
	 * Diese Methode wird von den NewEntryActions aufgerufen, um den EditorPart
	 * zu initialisieren
	 * 
	 * @return
	 */
	public IConfigurationModel getModel() {
		return this.configurationModel;
	}

	private PropertyChangeListener getPropertyChangeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				// beau TreeViewer neu auf
				final Collection<IConfigurationRoot> children = configurationModel
						.getChildren();

				Display.getCurrent().asyncExec(new Runnable() {
					public void run() {
						TreePath[] expandedTreePaths = _viewer
								.getExpandedTreePaths();
						_viewer.setInput(children);
						_viewer.setExpandedTreePaths(expandedTreePaths);
						// _viewer.refresh();
					}
				});
			}
		};
	}

//	public static ConfigurationEditingStoreService getEditingStoreService() {
//		return editingStoreService;
//	}
//
//	public static ConfigurationStoreService getConfigurationService() {
//		return configurationService;
//	}
}