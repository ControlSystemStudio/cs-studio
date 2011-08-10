/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.navigator.applaunch;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/** Replace the default LaunchConfig icon with
 *  one that is (optionally) specified within
 *  the launch config file.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class IconDecorator implements ILabelDecorator
{
	/** Cache of icon images by name */
	final private Map<String, Image> icons = new HashMap<String, Image>();
	
	/** {@inheritDoc} */
	@Override
    public void addListener(ILabelProviderListener listener)
	{
		// NOP
	}

	/** {@inheritDoc} */
    @Override
    public void removeListener(ILabelProviderListener listener)
    {
		// NOP
    }

	/** {@inheritDoc} */
	@Override
    public void dispose()
	{
		final Iterator<Image> iter = icons.values().iterator();
		while (iter.hasNext())
			iter.next().dispose();
		icons.clear();
	}

	/** {@inheritDoc} */
	@Override
    public boolean isLabelProperty(Object element, String property)
	{
		return false;
	}

	/** Check if a file describes a launch config
	 *  @param file {@link IFile} to check
	 *  @return <code>true</code> if it has the correct content type
	 */
    public static boolean isApplicationConfig(final IFile file)
	{
		IContentDescription content;
        try
        {
	        content = file.getContentDescription();
        }
        catch (CoreException e)
        {
        	return false;
        }
		if (content == null)
			return false;
		final IContentType type = content.getContentType();
		if (type == null)
			return false;
		return "org.csstudio.navigator.applaunch.application".equals(type.getId());
	}
    
    @Override
    public Image decorateImage(final Image original, final Object element)
    {
		if (! (element instanceof IFile))
			return original;

		final IFile file = (IFile) element;

		// Could check Content type...
		// but enablement markup in plugin.xml should
		// already assert that we only get the correct content type
		// if (! isApplicationConfig(file)) ...
		
		try
		{
			final LaunchConfig config = new LaunchConfig(file.getContents());
			final String icon_name = config.getIconName();
			if (icon_name.isEmpty())
				return original;
			// Try cached image
			Image image = icons.get(icon_name);
			if (image == null)
			{	// Create image
				if (icon_name.startsWith("icon:"))
					image = LaunchConfig.getBuildinIcon(icon_name.substring(5));
				else
				{	// Resolve icon name within workspace
					final IResource icon_path =
						ResourcesPlugin.getWorkspace().getRoot().findMember(icon_name);
					if (icon_path != null)
						image = new Image(Display.getCurrent(), icon_path.getLocation().toOSString());
				}
				// Add to cache
				if (image != null)
					icons.put(icon_name, image);
			}

			if (image != null)
				return image;
		}
		catch (Exception ex)
		{
		}
		return original;
    }

	/** {@inheritDoc} */
	@Override
    public String decorateText(String text, Object element)
    {
	    return null;
    }
}