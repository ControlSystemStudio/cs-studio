package org.csstudio.platform.ui.composites.resourcefilter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Provides workspace filtering resources as content for a tree viewer.
 *
 * @author SOPRA Group
 */

final class WorkspaceResourceContentProvider implements ITreeContentProvider {
    /**
     * Flag that signals if closed projects should be included as well.
     */
    private boolean showClosedProjects = true;

    /**
     * File extensions of files to include in the result lists.
     */
    private String[] filters;

    /**
     * Creates a new <code>WorkspaceResourcesContentProvider</code>.
     *
     * @param filters
     */
    public WorkspaceResourceContentProvider(String[] filters) {
        if (filters != null) {
            this.filters = new String[filters.length];
            System.arraycopy(filters, 0, this.filters, 0,
                    filters.length);
        } else {
            this.filters = new String[0];
        }
    }

    /**
     * The visual part that is using this content provider is about to be
     * disposed. Deallocate all allocated SWT resources.
     */
    @Override
    public void dispose() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] getChildren(final Object element) {
        if (element instanceof IWorkspace) {
            // check if closed projects should be shown
            IProject[] allProjects = ((IWorkspace) element).getRoot()
                    .getProjects();
            if (showClosedProjects) {
                return allProjects;
            }

            ArrayList<IProject> accessibleProjects = new ArrayList<IProject>();
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
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                            "", e);
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
        if (resource.getType() != IResource.FILE || filters == null) {
            // non-files are always included
            return true;
        } else {
            for (String filter : filters) {
                if (resource.getName().contains(filter)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] getElements(final Object element) {
        return getChildren(element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getParent(final Object element) {
        if (element instanceof IResource) {
            return ((IResource) element).getParent();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasChildren(final Object element) {
        return getChildren(element).length > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
        showClosedProjects = show;
    }
}