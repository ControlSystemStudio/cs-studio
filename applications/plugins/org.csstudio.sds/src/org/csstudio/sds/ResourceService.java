/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.sds;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;

/**
 * This services provides some basic workspace operations.
 *
 * @author Alexander Will
 * @version $Revision$
 *
 */
public final class ResourceService {
    /**
     * The shared instance.
     */
    private static ResourceService _instance;

    /**
     * List that holds all listeners that will be informed about resource
     * changes.
     */
    private ArrayList<IResourceChangeListener> _targetResourceChangeListeners;

    /**
     * The change listener that is used to centrally collect workspace changes.
     */
    private IResourceChangeListener _resourceChangeListener;

    /**
     * Return the shared instance.
     *
     * @return The shared instance.
     */
    public static ResourceService getInstance() {
        if (_instance == null) {
            _instance = new ResourceService();
        }

        return _instance;
    }

    /**
     * Private constructor due to singleton pattern.
     */
    private ResourceService() {
        _targetResourceChangeListeners = new ArrayList<IResourceChangeListener>();
        _resourceChangeListener = new CssResourceChangeListener();
    }

    /**
     * Create a workspace project with the given name (if it does not exist
     * already).
     *
     * @param projectName
     *            The name of the project.
     * @return a handle to the given project.
     */
    public IProject createWorkspaceProject(final String projectName) {
        IProject result = ResourcesPlugin.getWorkspace().getRoot().getProject(
                projectName);
        if (!result.exists()) {
            try {
                result.create(null);
                result.open(null);
            } catch (CoreException e) {
            }
        }

        return result;
    }

    /**
     * Copy the resourcces from the given folder in the given bundle into the
     * given workspace project.
     *
     * @param targetProject
     *            workspace project to copy the resources to.
     * @param sourceBundle
     *            source bundle.
     * @param sourceFolderName
     *            source folder in the source bundle.
     */
    @SuppressWarnings("unchecked")
    public void copyResources(final IProject targetProject,
            final Bundle sourceBundle, final String sourceFolderName) {

        Enumeration fileEntries = sourceBundle.findEntries(sourceFolderName,
                "*.*", false); //$NON-NLS-1$

        while (fileEntries.hasMoreElements()) {
            URL url = (URL) fileEntries.nextElement();
            String filePath = url.getFile();
            String fileName = filePath
                    .substring(filePath.indexOf(sourceFolderName + "/") + sourceFolderName.length() + 1); //$NON-NLS-1$

            IFile file = targetProject.getFile(fileName);
            if (!file.exists()) {
                try {
                    file.create(FileLocator.openStream(sourceBundle, new Path(
                            filePath), false), true, null);
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Add a resource change listener.
     *
     * @param listener
     *            the resource change listener to add.
     */
    public void addResourceChangeListener(final IResourceChangeListener listener) {
        if (!_targetResourceChangeListeners.contains(listener)) {
            _targetResourceChangeListeners.add(listener);

            if (_targetResourceChangeListeners.size() == 1) {
                ResourcesPlugin.getWorkspace().addResourceChangeListener(
                        _resourceChangeListener);
            }
        }
    }

    /**
     * Remove a resource change listener.
     *
     * @param listener
     *            the resource change listener to remove.
     */
    public void removeResourceChangeListener(
            final IResourceChangeListener listener) {
        if (_targetResourceChangeListeners.contains(listener)) {
            _targetResourceChangeListeners.remove(listener);

            if (_targetResourceChangeListeners.size() == 0) {
                ResourcesPlugin.getWorkspace().removeResourceChangeListener(
                        _resourceChangeListener);
            }
        }
    }

    /**
     * Central resource change listener that forwards workspace changes to the
     * registered listeners.
     *
     * @author Alexander Will
     * @version $Revision$
     */
    private class CssResourceChangeListener implements IResourceChangeListener {
        public void resourceChanged(IResourceChangeEvent event) {
            for (IResourceChangeListener listener : _targetResourceChangeListeners) {
                listener.resourceChanged(event);
            }
        }
    }
}
