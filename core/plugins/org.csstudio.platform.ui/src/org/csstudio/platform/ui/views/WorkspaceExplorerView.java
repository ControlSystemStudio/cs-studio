package org.csstudio.platform.ui.views;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.internal.localization.Messages;
import org.csstudio.platform.ui.workbench.FileEditorInput;
import org.csstudio.platform.ui.workbench.IWorkbenchIds;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;

/**
 * A view, which is used to display, navigate and open synoptic displays.
 * 
 * @author Alexander Will
 * @version $Revision$
 */
public final class WorkspaceExplorerView extends ViewPart {

	/**
	 * The ID of this view as configured in the plugin manifest.
	 */
	public static final String VIEW_ID = "org.csstudio.platform.ui.views.WorkspaceExplorerView"; //$NON-NLS-1$

	/**
	 * A treeviewer, which is used to display the resources.
	 */
	private TreeViewer _treeViewer;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		_treeViewer = new TreeViewer(parent);

		_treeViewer.setContentProvider(new BaseWorkbenchContentProvider());
		_treeViewer.setLabelProvider(new WorkbenchLabelProvider());
		_treeViewer.setInput(ResourcesPlugin.getWorkspace().getRoot());

		getViewSite().setSelectionProvider(_treeViewer);

		// update resources viewer, when there are filesystem changes
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				new IResourceChangeListener() {
					public void resourceChanged(final IResourceChangeEvent event) {
						Display.getCurrent().asyncExec(new Runnable() {
							public void run() {
								_treeViewer.refresh();
							}
						});
					}
				});
		// add drag support
		// addDragSupport();

		// add listeners
		configureListeners();

		// create context menu
		configureContextMenu();
	}

	// /**
	// * Equip the tree viewer with drag&drop support.
	// */
	// private void addDragSupport() {
	// FilteredDragSourceAdapter dragSourceListener = new
	// FilteredDragSourceAdapter(
	// new Class[] { IProcessVariable.class, IArchiveDataSource.class,
	// TextContainer.class }) {
	// public List getCurrentSelection() {
	// return ((IStructuredSelection) _treeViewer.getSelection())
	// .toList();
	// }
	// };
	//
	// DnDUtil.enableForDrag(_treeViewer.getTree(), DND.DROP_MOVE
	// | DND.DROP_COPY, dragSourceListener);
	// }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
	}

	/**
	 * Configures all listeners for the TreeViewer.
	 */
	private void configureListeners() {
		_treeViewer.addOpenListener(new IOpenListener() {
			public void open(final OpenEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();

				Object element = selection.getFirstElement();

				if (element instanceof IFile) {
					openEditor((IFile) element);

				}
			}
		});
	}

	/**
	 * Configures all listeners for the TreeViewer.
	 */
	private void configureContextMenu() {
		MenuManager menuMgr = new MenuManager("", VIEW_ID); //$NON-NLS-1$
		menuMgr.add(new GroupMarker(IWorkbenchIds.GROUP_CSS_MB3));
		menuMgr.add(new Separator());
		menuMgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

		menuMgr.setRemoveAllWhenShown(true);

		Menu contextMenu = menuMgr.createContextMenu(_treeViewer.getTree());
		_treeViewer.getTree().setMenu(contextMenu);

		// Register viewer with site. This has to be done before making the
		// actions.
		getViewSite().registerContextMenu(menuMgr, _treeViewer);
		getViewSite().setSelectionProvider(_treeViewer);
	}

	/**
	 * Opens an appropriate editor for the specified file.
	 * 
	 * @param file
	 *            the file
	 */
	private void openEditor(final IFile file) {
		IEditorInput editorInput = new FileEditorInput(file);

		IEditorRegistry editorRegistry = PlatformUI.getWorkbench()
				.getEditorRegistry();
		IEditorDescriptor descriptor = editorRegistry.getDefaultEditor(file
				.getName());

		if (descriptor != null && editorInput != null) {
			IWorkbenchPage page = getSite().getPage();
			try {
				page.openEditor(editorInput, descriptor.getId());
			} catch (PartInitException e) {
				CentralLogger
						.getInstance()
						.error(
								Messages
										.getString("WorkspaceExplorerView.CANNOT_OPEN_EDITOR"), e); //$NON-NLS-1$
			}
		} else {
			MessageDialog.openInformation(getSite().getShell(), Messages
					.getString("WorkspaceExplorerView.ERROR_TITLE"), //$NON-NLS-1$
					Messages.getString("WorkspaceExplorerView.ERROR_MESSAGE")); //$NON-NLS-1$
		}
	}
}
