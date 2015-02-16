/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.client;

/**
 * The data structure for Automated Actions
 * @author Fred Arnaud (Sopra Group)
 */
public class AADataStructure {
	/** Maximum length of title used for the 'teaser' */
    final private static int MAX_TEASER = 30;
    
    /**The brief description of the Automated Action, 
     * which will be displayed in the context menu. */
    final private String title;
    
    /**The details text under the title. You must use empty string ("") not null if there is no details 
     * under the title. */
    final private String details;
    
    /**The delay for the action. */
    final private int delay;
    
    /**
     * Set title and details in the structure
     * @param title The brief description of the Guidance/Display/Command, 
     * which will be displayed in the context menu.
     * @param details The details text under the title. 
     * You must use empty string ("") <b>not</b> null if there is no details under the title.
     */
    public AADataStructure(final String title, final String details, final int delay)
    {
        this.title = title;
        this.details = details;
        this.delay = delay;
    }
    
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Get short version of title that's suitable for action text shown in
	 * context menu. Matches the title unless the title is too long.
	 * 
	 * @return Teaser string
	 */
	public String getTeaser() {
		if (title.length() > MAX_TEASER)
			return title.substring(0, MAX_TEASER) + "..."; //$NON-NLS-1$
		return title;
	}
    
	/**
	 * @return the details
	 */
	public String getDetails() {
		return details;
	}
	
	/**
	 * @return the delay
	 */
	public int getDelay() {
		return delay;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof AADataStructure)
			return (((AADataStructure) obj).getTitle().equals(title) && ((AADataStructure) obj)
					.getDetails().equals(details));
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = title.hashCode();
		result = prime * result + details.hashCode();
		return result;
	}

	/** @return String representation for debugging */
	@Override
	public String toString() {
		return String.format("Title '%s', Details '%s', Delay %ds", title, details, delay); //$NON-NLS-1$
	}
}
