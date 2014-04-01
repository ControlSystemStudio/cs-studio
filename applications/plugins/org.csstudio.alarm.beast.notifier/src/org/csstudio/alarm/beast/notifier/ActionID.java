/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier;

/**
 * Unique identifier for automated actions.
 * Indeed, an item can handle many automated actions identified by their title.
 * Each item is identified by its path.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class ActionID {

	private final String itemPath;
	private final String aaTitle;
	
	public ActionID(final String itemPath, final String aaTitle) {
		this.itemPath = itemPath;
		this.aaTitle = aaTitle;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aaTitle == null) ? 0 : aaTitle.hashCode());
		result = prime * result + ((itemPath == null) ? 0 : itemPath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActionID other = (ActionID) obj;
		if (aaTitle == null) {
			if (other.aaTitle != null)
				return false;
		} else if (!aaTitle.equals(other.aaTitle))
			return false;
		if (itemPath == null) {
			if (other.itemPath != null)
				return false;
		} else if (!itemPath.equals(other.itemPath))
			return false;
		return true;
	}

	public String getItemPath() {
		return itemPath;
	}
	public String getAaTitle() {
		return aaTitle;
	}
	
}
