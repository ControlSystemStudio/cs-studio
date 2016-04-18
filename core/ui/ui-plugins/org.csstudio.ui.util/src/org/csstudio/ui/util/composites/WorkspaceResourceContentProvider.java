/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.ui.util.composites;

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
 * Provides workspace resources as content for a tree viewer.
 *
 * <p>Code is based upon
 * <code>org.eclipse.ui.internal.ide.misc.ContainerContentProvider</code>
 * in plugin <code>org.eclipse.ui.ide</code></p>
 *
 * @author Alexander Will, Joerg Rathlev
 */

//TODO: Copied from org.csstudio.platform.ui. Review is needed.
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
            if (_showClosedProjects) {
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
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "", e); //$NON-NLS-1$
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
                        && (ext.equals(resource.getFileExtension())
                        || ext.equals("*") || ext.equals("*.*"))) {
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
        _showClosedProjects = show;
    }
}