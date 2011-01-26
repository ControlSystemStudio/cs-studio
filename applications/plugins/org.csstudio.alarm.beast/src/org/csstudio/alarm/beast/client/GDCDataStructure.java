/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.client;

/**
 * The common data structure for Guidance/Display/Command
 * @author Xihui Chen, Kay Kasemir
 */
public class GDCDataStructure {
    /** Maximum length of title used for the 'teaser' */
    final private static int MAX_TEASER = 30;
    
    /**The brief description of the Guidance/Display/Command, 
     * which will be displayed in the context menu. */
    final private String title;
    
    /**The details text under the title. You must use empty string ("") not null if there is no details 
     * under the title. */
    final private String details;
    
    /**
     * Set title and details in the structure
     * @param title The brief description of the Guidance/Display/Command, 
     * which will be displayed in the context menu.
     * @param details The details text under the title. 
     * You must use empty string ("") <b>not</b> null if there is no details under the title.
     */
    public GDCDataStructure(final String title, final String details)
    {
        this.title = title;
        this.details = details;
    }
    
    /**
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }
    
    /** Get short version of title that's suitable for action text shown
     *  in context menu. Matches the title unless the title is too long.
     *  @return Teaser string
     */
    public String getTeaser()
    {
        if (title.length() > MAX_TEASER)
            return title.substring(0, MAX_TEASER) + "..."; //$NON-NLS-1$
        return title;
    }
    
    /**
     * @return the details
     */
    public String getDetails()
    {
        return details;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj)
    {
        if (obj instanceof GDCDataStructure)
            return (((GDCDataStructure)obj).getTitle().equals(title) && 
                   ((GDCDataStructure)obj).getDetails().equals(details));
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = title.hashCode();
        result = prime * result + details.hashCode();
        return result;
    }
    
    /** @return String representation for debugging */
    @Override
    public String toString()
    {
        return String.format("Title '%s', Details '%s'", title, details); //$NON-NLS-1$
    }
}
