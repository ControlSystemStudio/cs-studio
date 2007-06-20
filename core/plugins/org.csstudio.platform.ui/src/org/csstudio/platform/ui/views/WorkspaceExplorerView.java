package org.csstudio.platform.ui.views;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.internal.localization.Messages;
import org.csstudio.platform.ui.workbench.FileEditorInput;
import org.csstudio.platform.ui.workbench.IWorkbenchIds;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
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

/** A view, which is used to display, navigate and open synoptic displays.
 *  @author Alexander Will
 *  @author Kay Kasemir: hide .project file
 *  @version $Revision$
 */
public final class WorkspaceExplorerView extends ViewPart {
    /** Name of the ".project" file that we want to hide */
	private static final String PROJECT_FILE_NAME = ".project"; //$NON-NLS-1$

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

        // Use standard workbench support for displaying whatever
        // adapts to IWorkbenchAdapter.
        // plugin.xml registers an adapter factory that adapts
        // IResource, which includes the workspace root, to IWorkbenchAdapter.
		_treeViewer.setContentProvider(new BaseWorkbenchContentProvider());
		_treeViewer.setLabelProvider(new WorkbenchLabelProvider());
		_treeViewer.setInput(ResourcesPlugin.getWorkspace().getRoot());
        _treeViewer.addFilter(new ViewerFilter()
        {
            /** Hide the .project file */
            @Override
            public boolean select(Viewer viewer, Object parent, Object element)
            {
                if (parent != null  &&  element != null
                    && parent instanceof IProject
                    && element instanceof IFile
                    && ((IFile)element).getName().equals(PROJECT_FILE_NAME))
                    return false;
                return true;
            }
        });

		getViewSite().setSelectionProvider(_treeViewer);

		// update resources viewer, when there are filesystem changes
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
        new IResourceChangeListener()
        {
            public void resourceChanged(
                            final IResourceChangeEvent event)
            {
                // Notification can originate from non-UI thread,
                // for example from a "New..." Wizard thread,
                // so use asyncExec.
                Display.getDefault().asyncExec(new Runnable()
                {
                    public void run()
                    {
                        if (!_treeViewer.getTree().isDisposed())
                        {
                            _treeViewer.refresh();
                        }
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
	public void setFocus()
    {
        _treeViewer.getTree().setFocus();
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
	private void configureContextMenu()
    {
		final MenuManager menuMgr = new MenuManager("", VIEW_ID); //$NON-NLS-1$
        // Re-popolate menu each time it's shown to get current object contribs
		menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener()
        {
            public void menuAboutToShow(IMenuManager manager)
            {
                // Region for actions that create new stuff
                menuMgr.add(new GroupMarker(IWorkbenchActionConstants.NEW_EXT));
                menuMgr.add(new Separator());
                // Region(s) for actions that open existing resources
                menuMgr.add(new GroupMarker(IWorkbenchIds.GROUP_CSS_MB3));
                menuMgr.add(new GroupMarker(IWorkbenchActionConstants.OPEN_EXT));
                menuMgr.add(new Separator());
                // Region(s) for actions that rename/delete existing resources
                menuMgr.add(new GroupMarker(IWorkbenchActionConstants.EDIT_START));
                menuMgr.add(new GroupMarker(IWorkbenchActionConstants.EDIT_END));
                menuMgr.add(new Separator());
                // Whatever's left
                menuMgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
            }
        });

		final Tree tree = _treeViewer.getTree();
        Menu contextMenu = menuMgr.createContextMenu(tree);
		tree.setMenu(contextMenu);

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
