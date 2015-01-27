/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;

/**
 * Information about an {@link AlarmTreeItem}.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class ItemInfo {
	
	final int id;
	final private String name, path, description;
	final private boolean enabled, isPV, latching;
	
	private static Pattern IMPPattern = Pattern.compile("^\\ *\\*?\\!.*");

	public static ItemInfo fromItem(final AlarmTreeItem item) 
    {
		final int id = item.getID();
		final String name = item.getName();
		final String path = item.getPathName();
		if (item instanceof AlarmTreePV) {
			AlarmTreePV pv = (AlarmTreePV) item;
			final String description = pv.getDescription();
			final boolean enabled = pv.isEnabled();
			final boolean latching = pv.isLatching();
			return new ItemInfo(id, name, path, description, true, enabled, latching);
		} else {
			return new ItemInfo(id, name, path, null, false, false, false);
		}
    }
	
	public ItemInfo(final int id,
			final String name,
			final String path,
            final String description,
            final boolean isPV,
            final boolean enabled,
            final boolean latching)
    {
        this.id = id;
        this.name = name;
        this.path = path; 
        this.description = description;
        this.isPV = isPV;
        this.enabled = enabled;
        this.latching = latching;
    }
	
	public boolean isImportant() {
		if (description == null || "".equals(description))
			return false;
		Matcher IMPMatcher = IMPPattern.matcher(description);
		if (IMPMatcher.matches())
			return true;
		return false;
	}

	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getPath() {
		return path;
	}
	public String getDescription() {
		return description;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public boolean isPV() {
		return isPV;
	}
	public boolean isLatching() {
		return latching;
	}
	
}
