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
package org.csstudio.platform.ui.internal.workbench;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.platform.ui.CSSPlatformUiPlugin;
import org.csstudio.platform.ui.util.ImageUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;

/** A workbench adapter implementation for workspace resources.
 *  <p>
 *  Adapts IResource-based objects to the IWorkbenchAdapter interface.
 * 
 *  @author Sven Wende
 *  @author Kay Kasemir: hide the .project file
 */
public final class ResourcesWorkbenchAdapter extends WorkbenchAdapter
{
    private static final Logger LOG = Logger.getLogger(CSSPlatformUiPlugin.ID);
    
    /** Get the sub-folders or files of a given IResource.
     *  @see IWorkbenchAdapter#getChildren(Object)
	 */
	@Override
	public Object[] getChildren(final Object object)
    {
        try
        {
            // Root?
            if (object instanceof IWorkspaceRoot)
                return ((IWorkspaceRoot) object).getProjects();
            // Project?
            if (object instanceof IProject)
            {
                if (((IProject) object).isOpen())
                    return ((IContainer) object).members();
                // else: closed project has no known members
                return new Object[0];
            }
            // Plain folder
            if (object instanceof IContainer)
                return ((IContainer) object).members();
        }
        catch (Exception ex)
        {
            LOG.log(Level.FINE,"",ex);
        }
        return new Object[0];
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ImageDescriptor getImageDescriptor(final Object object) {
		ImageDescriptor result = null;
		// return the icon from the editor registry or a default icon, if none such exists
		if(object instanceof IFile) {
			result = PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor(((IFile)object).getName());
		} else if (object instanceof IContainer) {
			if (object instanceof IFolder) {
				result = ImageUtil.getInstance().getImageDescriptor(CSSPlatformUiPlugin.ID, "icons/folder.gif");  //$NON-NLS-1$
			} else
			if (object instanceof IProject) {
				IProject project = (IProject) object;
				if (project.isOpen()) {
					result = ImageUtil.getInstance().getImageDescriptor(CSSPlatformUiPlugin.ID, "icons/project_open.png");  //$NON-NLS-1$
				} else {
					result = ImageUtil.getInstance().getImageDescriptor(CSSPlatformUiPlugin.ID, "icons/project_close.png");  //$NON-NLS-1$
				}
			} else {
				result = ImageUtil.getInstance().getImageDescriptor(CSSPlatformUiPlugin.ID, "icons/folder.png");  //$NON-NLS-1$
			}
		}
		
		return result;
	}

	/** Provide label for IResource object */
	@Override
	public String getLabel(final Object object)
    {
        if (object instanceof IResource)
            return ((IResource) object).getName();
        // else
        return "<unknown object " + object + ">"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
