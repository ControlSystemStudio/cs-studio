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

package org.csstudio.platform.ui.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

/**
 * <p>Provides an API for opening editors on files in the workspace. This class
 * cannot be instantiated; all functionality is provided by static methods.</p>
 * 
 * <p>Note: the implementation of this class is intentionally similar to the
 * Eclipse class {@link org.eclipse.ui.ide.IDE}. Unlike {@code IDE}, this class
 * does not provide a method of opening an editor without activating it; if
 * this kind of functionality is required, it should be added in this class.</p>
 * 
 * @author Joerg Rathlev
 */
public final class EditorUtil {
	
	/**
	 * The persistent property key used on IFile resources to contain the
	 * preferred editor ID to use.
	 */
	// Note: this is from org.eclipse.ui.ide.IDE, but seems to be used by
	// non-IDE code as well. I don't know if the key is officially documented
	// anywhere.
	private static final QualifiedName EDITOR_KEY = new QualifiedName(
			"org.eclipse.ui.internal.registry.ResourceEditorRegistry", "EditorProperty");

	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private EditorUtil() {
	}
	

	/**
	 * <p>
	 * Opens an editor on the given file resource. This method will attempt
	 * to resolve the editor based on content-type bindings as well as
	 * traditional name/extension bindings.
	 * </p>
	 * 
	 * <p>
	 * If the page already has an editor open on the target object then that
	 * editor is brought to front; otherwise, a new editor is opened. If
	 * <code>activate == true</code> the editor will be activated.
	 * </p>
	 * 
	 * @param page
	 *            the page in which the editor will be opened.
	 * @param input
	 *            the editor input.
	 * @return an open editor.
	 * @throws PartInitException
	 *             if the editor could not be initialized.
	 * @see IWorkbenchPage#openEditor(org.eclipse.ui.IEditorInput, String)
	 */
	public static IEditorPart openEditor(IWorkbenchPage page, IFile input)
			throws PartInitException {
		if (page == null) {
			throw new NullPointerException();
		}
		
		IEditorDescriptor editorDesc = getEditorDescriptor(input);
		return page.openEditor(new FileEditorInput(input), editorDesc.getId());
	}
	

	/**
	 * Returns an editor descriptor appropriate for opening the given file
	 * resource.
	 * 
	 * @param file
	 *            the file.
	 * @return an editor descriptor.
	 * @throws PartInitException
	 *             if no editor can be found.
	 */
	private static IEditorDescriptor getEditorDescriptor(IFile file)
			throws PartInitException {
		IEditorDescriptor editorDesc = getDefaultEditor(file);
		if (editorDesc == null) {
			throw new PartInitException("No editor found.");
		}
		return editorDesc;
	}
	

	/**
	 * Returns the default editor for a given file. This method will attempt to
	 * resolve the editor based on content-type bindings as well as traditional
	 * name/extension bindings if <code>determineContentType</code> is
	 * <code>true</code>.
	 *  
	 * @param file
	 *            the file.
	 * @return the descriptor of the default editor, or <code>null</code>
	 *         if not found.
	 */
	public static IEditorDescriptor getDefaultEditor(IFile file) {
		IEditorRegistry editorReg =
				PlatformUI.getWorkbench().getEditorRegistry();
		
		// Try file specific editor.
		try {
			String editorId = file.getPersistentProperty(EDITOR_KEY);
			if (editorId != null) {
				IEditorDescriptor editorDesc = editorReg.findEditor(editorId);
				if (editorDesc != null) {
					return editorDesc;
				}
			}
		} catch (CoreException e) {
			// do nothing
		}
		
		IContentType contentType = getContentType(file);
		// Try lookup with filename
		return editorReg.getDefaultEditor(file.getName(), contentType);
	}

	
	/**
	 * Returns the content type for the given file.
	 * 
	 * @param file
	 *            the file to test.
	 * @return the content type, or <code>null</code> if it cannot be
	 *         determined.
	 */
	public static IContentType getContentType(IFile file) {
		try {
			IContentDescription contentDesc = file.getContentDescription();
			return contentDesc == null ? null : contentDesc.getContentType();
		} catch (CoreException e) {
			return null;
		}
	}

}
