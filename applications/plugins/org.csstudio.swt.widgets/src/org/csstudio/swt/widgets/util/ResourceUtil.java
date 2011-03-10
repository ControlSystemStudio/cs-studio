/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

/**Utility functions for resources.
 * @author Xihui Chen
 *
 */
public class ResourceUtil {
	/**
	 * Return the {@link InputStream} of the file that is available on the
	 * specified path.
	 * 
	 * @param path
	 *            The {@link IPath} to the file
	 * 
	 * @return The corresponding {@link InputStream} or null
	 * @throws Exception 
	 */
	public static InputStream pathToInputStream(final IPath path) throws Exception{
		InputStream result = null;
		
		IResource r = null;
		try {
			// try workspace
			r = ResourcesPlugin.getWorkspace().getRoot().findMember(
					path, false);
			if (r!= null && r instanceof IFile) {			
				result = ((IFile) r).getContents();		
				return result;
			}else
				throw new Exception();
		} catch (Exception e) {
			// try from local file system			
			try {
				result = new FileInputStream(path.toFile());
				if(result != null)
					return result;
				else
					throw new Exception();
			} catch (Exception e1) {
				try {
					//try from URL					
					String urlString = path.toString();
					if(!urlString.contains("://")) //$NON-NLS-1$
						urlString = urlString.replaceFirst(":/", "://"); //$NON-NLS-1$ //$NON-NLS-2$
					URL url = new URL(urlString);
					result = url.openStream();
					return result;
				} catch (Exception e2) {
					throw new Exception("This exception includes three sub-exceptions:\n"+ 
							e+ "\n" + e1 + "\n" + e2);
				}				
			}
		}
	}
}
