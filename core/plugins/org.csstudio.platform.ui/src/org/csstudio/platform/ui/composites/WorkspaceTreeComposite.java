package org.csstudio.platform.ui.composites;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.DrillDownComposite;

/**
 * This class creates a tree, which shows the workspace entries.
 * @author Kai Meyer
 *
 */
public class WorkspaceTreeComposite {

	/**
	 * The tree widget.
	 */
	private TreeViewer _treeViewer;

	/**
	 * Sizing constant for the width of the tree.
	 */
	private static final int SIZING_SELECTION_PANE_WIDTH = 320;
	/**
	 * Constant for the width of the tree.
	 */
	private static final int HEIGHTHINT = 200;

	/**
	 * The accepted file extensions.
	 */
	private String[] _fileExtensions;

	/**
	 * Constant for the show closed projects option.
	 */
	private static final boolean SHOW_CLOSED_PROJECTS = true;

	/**
	 * Constructor.
	 * @param parent The parent for the tree
	 * @param style The style for the tree
	 * @param fileExtensions The extensions of the files, which should be displayed. 
	 */
	public WorkspaceTreeComposite(Composite parent, int style, String[] fileExtensions) {
		this(parent, style, SIZING_SELECTION_PANE_WIDTH, HEIGHTHINT, fileExtensions);
	}

	/**
	 * Constructor.
	 * @param parent The parent for the tree
	 * @param style The style for the tree
	 * @param width The width for the tree
	 * @param height The height for the tree
	 * @param fileExtensions The extensions of the files, which should be displayed. 
	 */
	public WorkspaceTreeComposite(Composite parent, int style, final int width,
			final int height, String[] fileExtensions) {
		_fileExtensions = fileExtensions;
		DrillDownComposite drillDown = new DrillDownComposite(parent, SWT.BORDER);
		GridData spec = new GridData(SWT.FILL, SWT.FILL, true, true);
		spec.widthHint = width;
		spec.heightHint = height;
		drillDown.setLayoutData(spec);

		// Create tree viewer inside drill down.
		_treeViewer = new TreeViewer(drillDown, style);
		drillDown.setChildTree(_treeViewer);
		ContainerContentProvider cp = new ContainerContentProvider();
		cp.showClosedProjects(SHOW_CLOSED_PROJECTS);
		_treeViewer.setContentProvider(cp);
		_treeViewer.setLabelProvider(WorkbenchLabelProvider
				.getDecoratingWorkbenchLabelProvider());
		_treeViewer.setSorter(new ViewerSorter());
		_treeViewer.setUseHashlookup(true);
		_treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(final DoubleClickEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof IStructuredSelection) {
					Object item = ((IStructuredSelection) selection)
							.getFirstElement();
					if (item == null) {
						return;
					}
					if (_treeViewer.getExpandedState(item)) {
						_treeViewer.collapseToLevel(item, 1);
					} else {
						_treeViewer.expandToLevel(item, 1);
					}
				}
			}
		});

		// This has to be done after the viewer has been laid out
		_treeViewer.setInput(ResourcesPlugin.getWorkspace());
	}

	/**
	 * Returns the {@link IPath} to the currently selected {@link IResource}.
	 * @return The {@link IPath} to the currently selected {@link IResource}
	 */
	public IPath getSelectedPath() {
		IStructuredSelection selection = (IStructuredSelection) _treeViewer
				.getSelection();
		IPath path;
		if (selection == null || selection.getFirstElement() == null) {
			path = new Path("");
		} else {
			path = ((IResource) selection.getFirstElement()).getFullPath();
		}
		return path;
	}

	/**
	 * Initializes the expand state for the given path.
	 * 
	 * @param path
	 *            The path to a specific item
	 */
	public void initExpandState(final IPath path) {
		assert path != null : "Precondition violated: path!=null";
		_treeViewer.expandAll();
		_treeViewer.collapseAll();
		TreeItem[] items = _treeViewer.getTree().getItems();
		for (int i = 0; i < path.segmentCount(); i++) {
			String segment = path.segment(i);
			for (TreeItem item : items) {
				Object data = item.getData();
				if (data instanceof IResource) {
					if (((IResource) data).getName().equals(segment)) {
						if (item.getItemCount() == 0) {
							ISelection selection = new StructuredSelection(item
									.getData());
							_treeViewer.setSelection(selection, true);
						} else {
							item.setExpanded(true);
							items = item.getItems();
						}
						break;
					}
				}
			}
		}
	}

	/**
	 * Returns the internally used {@link TreeViewer}.
	 * @return The used {@link TreeViewer}
	 */
	public TreeViewer getTreeViewer() {
		return _treeViewer;
	}

	/**
	 * Provides content for a tree viewer that shows only containers.
	 * 
	 * <p>
	 * <b>Code is based upon
	 * <code>org.eclipse.ui.internal.ide.misc.ContainerContentProvider</code>
	 * in plugin <code>org.eclipse.ui.ide</code>.</b>
	 * </p>
	 * 
	 * @author Alexander Will
	 * @version $Revision$
	 */
	private final class ContainerContentProvider implements
			ITreeContentProvider {
		/**
		 * Flag that signals if closed projects should be included as well.
		 */
		private boolean _showClosedProjects = true;

		/**
		 * Creates a new ContainerContentProvider.
		 */
		public ContainerContentProvider() {
		}

		/**
		 * The visual part that is using this content provider is about to be
		 * disposed. Deallocate all allocated SWT resources.
		 */
		public void dispose() {
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("unchecked")
		public Object[] getChildren(final Object element) {
			if (element instanceof IWorkspace) {
				// check if closed projects should be shown
				IProject[] allProjects = ((IWorkspace) element).getRoot()
						.getProjects();
				if (_showClosedProjects) {
					return allProjects;
				}

				ArrayList accessibleProjects = new ArrayList();
				for (int i = 0; i < allProjects.length; i++) {
					if (allProjects[i].isOpen()) {
						accessibleProjects.add(allProjects[i]);
					}
				}
				return accessibleProjects.toArray();
			} else if (element instanceof IContainer) {
				IContainer container = (IContainer) element;
				if (container.isAccessible()) {
					try {
						List<IResource> children = new ArrayList<IResource>();
						IResource[] members = container.members();
						for (IResource member : members) {
							if (member.getType() == IResource.FILE
									&& _fileExtensions != null
									&& _fileExtensions.length > 0) {
								for (String extension : _fileExtensions) {
									if (extension != null
											&& extension.equals(member
													.getFileExtension())) {
										children.add(member);
										break;
									}
								}
							} else {
								children.add(member);
							}
						}
						return children.toArray();
					} catch (CoreException e) {
						CentralLogger.getInstance().error(this, e);
					}
				}
			}
			return new Object[0];
		}

		/**
		 * {@inheritDoc}
		 */
		public Object[] getElements(final Object element) {
			return getChildren(element);
		}

		/**
		 * {@inheritDoc}
		 */
		public Object getParent(final Object element) {
			if (element instanceof IResource) {
				return ((IResource) element).getParent();
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasChildren(final Object element) {
			return getChildren(element).length > 0;
		}

		/**
		 * {@inheritDoc}
		 */
		public void inputChanged(final Viewer viewer, final Object oldInput,
				final Object newInput) {
		}

		/**
		 * Specify whether or not to show closed projects in the tree viewer.
		 * Default is to show closed projects.
		 * 
		 * @param show
		 *            boolean if false, do not show closed projects in the tree
		 */
		public void showClosedProjects(final boolean show) {
			_showClosedProjects = show;
		}
	}

}
