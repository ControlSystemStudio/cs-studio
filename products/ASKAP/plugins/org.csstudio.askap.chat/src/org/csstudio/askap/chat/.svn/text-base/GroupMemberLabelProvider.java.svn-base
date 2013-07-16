/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.askap.chat;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/** Label provider for table that shows chat group members
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GroupMemberLabelProvider extends CellLabelProvider
{
	private static final String ICON_NAME = "icons/person-small16.png";
	private Image icon = null;
	
	/** Dispose icon
	 *  {@inheritDoc}
	 */
    @Override
    public void dispose()
    {
    	if (icon != null)
    	{
    		icon.dispose();
    		icon = null;
    	}
	    super.dispose();
    }
    
	@Override
    public String getToolTipText(final Object element)
    {
		final String person = (String) element;
		return person;
    }

	/** Display name of member with generic icon
	 *  {@inheritDoc}
	 */
	@Override
	public void update(final ViewerCell cell)
	{
		final String person = (String) cell.getElement();
		cell.setText(person);
		
		if (icon == null)
		{
			final ImageDescriptor descr = Activator.getImage(ICON_NAME);
			if (descr == null) // Running as standalone demo?
				icon = new Image(Display.getCurrent(), ICON_NAME);
			else
				icon = descr.createImage();
		}
		cell.setImage(icon);
	}
}
