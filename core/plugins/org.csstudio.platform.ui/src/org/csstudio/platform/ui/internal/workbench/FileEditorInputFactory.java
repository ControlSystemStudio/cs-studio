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

import org.csstudio.platform.ui.workbench.FileEditorInput;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

/**
 * This class defines a factory that is capable of recreating file editor inputs
 * stored in a memento.
 * 
 * @author swende
 * @version $Revision$
 */

public final class FileEditorInputFactory implements IElementFactory {

	/**
	 * Factory TYPE_ID (needed to establish a link to the plugin.xml).
	 */
	public static final String ID = "de.smartpls.platform.ui.common.FileEditorInputFactory"; //$NON-NLS-1$

	/**
	 * IMemento key for <code>FileEditorInput</code>'s.
	 */
	public static final String KEY = "de.smartpls.domainresources.FileIdentifier"; //$NON-NLS-1$

	/**
	 * {@inheritDoc}
	 */
	public IAdaptable createElement(final IMemento memento) {
		String identifier = memento.getString(KEY);
		IAdaptable result = null;

		if (identifier != null) {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IPath path = new Path(identifier);

			IFile file = root.getFile(path);

			result = new FileEditorInput(file);
		}

		return result;
	}

	/**
	 * Save a <code>FileEditorInput</code> into a given <code>IMemento</code>
	 * object.
	 * 
	 * @param memento
	 *            IMemento to save the file editor input in.
	 * @param input
	 *            <code>FileEditorInput</code> to save in
	 *            <code>IMemento</code>.
	 */
	public static void saveState(final IMemento memento, final FileEditorInput input) {
		memento.putString(KEY, input.getFile().getFullPath().toString());
	}
}
