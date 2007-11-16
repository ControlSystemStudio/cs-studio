package org.csstudio.platform.ui.composites;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Provides workspace resources as content for a tree viewer.
 * 
 * <p>Code is based upon
 * <code>org.eclipse.ui.internal.ide.misc.ContainerContentProvider</code>
 * in plugin <code>org.eclipse.ui.ide</code></p>
 * 
 * @author Alexander Will, Joerg Rathlev
 */
final class WorkspaceResourceContentProvider implements
		ITreeContentProvider {
	/**
	 * Flag that signals if closed projects should be included as well.
	 */
	private boolean _showClosedProjects = true;
	
	/**
	 * File extensions of files to include in the result lists.
	 */
	private String[] _fileExtensions;

	/**
	 * Creates a new <code>WorkspaceResourcesContentProvider</code>.
	 * 
	 * @param fileExtensions
	 *            The file extensions of file resources to include in the
	 *            contents provided by the content provider. Use
	 *            <code>null</code> or an empty array to create a content
	 *            provider that provides only container resources (projects
	 *            and folders).
	 */
	public WorkspaceResourceContentProvider(String[] fileExtensions) {
		if (fileExtensions != null) {
			_fileExtensions = new String[fileExtensions.length];
			System.arraycopy(fileExtensions, 0, _fileExtensions, 0, fileExtensions.length);
		} else {
			_fileExtensions = new String[0];
		}
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
						if (includeResource(member)) {
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
	 * Returns whether the given resource should be included in the contents
	 * this content provider returns.
	 * 
	 * @param resource
	 *            the resource.
	 * @return <code>true</code> if the resource should be included,
	 *         <code>false</code> otherwise.
	 */
	private boolean includeResource(IResource resource) {
		if (resource.getType() != IResource.FILE) {
			// non-files are always included
			return true;
		} else {
			// files are included if their extension is in the list
			// of accepted extensions
			for (String ext : _fileExtensions) {
				if (ext != null
						&& ext.equals(resource.getFileExtension())) {
					return true;
				}
			}
			return false;
		}
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