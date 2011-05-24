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
package org.csstudio.ui.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

/**
 * This class is for creating new IResource-objects.
 * 
 * @author Kai Meyer
 *
 */

//TODO: Copied from org.csstudio.platform.ui. Review is needed.
public final class ResourceUtil {
	
	/**
	 * Result identifier: Okay.
	 */
	public static final int OK = 0;
	/**
	 * Result identifier: An error occured.
	 */
	public static final int ERROROCCURED = 1;
	/**
	 * Result identifier: Name was NULL.
	 */
	public static final int NAMEWASNULL = 2;
	/**
	 * Result identifier: Folder exists.
	 */
	public static final int FOLDEREXISTS = 3;
	/**
	 * Result identifier: Project exists.
	 */
	public static final int PROJECTEXISTS = 4;
	
	/**
	 * The instance of this class.
	 */
	private static ResourceUtil _instance;
	
	/**
	 * Construktor.
	 */
	private ResourceUtil() {
	}
	
	/**
	 * Delivers the instance of this class.
	 * 
	 * @return IResourceUtil
	 */
	public static ResourceUtil getInstance() {
		if (_instance==null) {
			_instance = new ResourceUtil();
		}
		return _instance;
	}
	
	/**
	 * Creates a new Folder in the parentContainer.
	 * @param parentContainer
	 * 			The IContainer, where the new folder is built in
	 * @param folderName
	 * 			The name of the folder
	 * @return int
	 * 			The result-status
	 */
	public int createFolder(final IContainer parentContainer, final String folderName) {
		if (folderName != null && folderName.trim().length()>0) {
			IFolder folder = parentContainer.getFolder(new Path(folderName));
			if (folder.exists()) {
				return FOLDEREXISTS;
			} else {
				try {
					folder.create(true, true, null);
				} catch (CoreException e) {
					Logger.getLogger(getClass().getName()).log(Level.SEVERE, "", e); //$NON-NLS-1$
					return ERROROCCURED;
				}
			}
			return OK;
		}
		return NAMEWASNULL;
	}
	
	/**
	 * Creates a new Project.
	 * @param projectName
	 * 			The name of the project
	 * @return int
	 * 			The result-status
	 */
	public int createProject(final String projectName) {
		if (projectName!=null && projectName.trim().length()>0) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			if (project.exists()) {
				return PROJECTEXISTS;
			} else {
				try {
					project.create(null);
					project.open(null);
				} catch (CoreException e) {
					Logger.getLogger(getClass().getName()).log(Level.SEVERE, "", e); //$NON-NLS-1$
					return ERROROCCURED;
				}
			}
			return OK;
		}
		return NAMEWASNULL;
	}

}
