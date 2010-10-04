/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

/** Helper for handling the path names of alarm tree elements.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AlarmTreePath
{
    /** Separator used to create path names to items in the alarm tree */
    final private static String PATH_SEP = "/";

    /** Build path name
     *  @param path Parent path
     *  @param item Name of item at end of path
     *  @return Full path name to item
     */
    public static String makePath(final String path, final String item)
    {
    	// Escape any path-seps inside item with backslashes
        return path + PATH_SEP + item.replace("/", "\\/");
    }

    /** Build path name
     *  @param path_items Path elements
     *  @param length_to_use How many elements of path_items to use,
     *                       0 ... path_items.length-1
     *  @return Full path name to item
     */
    public static String makePath(final String path_items[], int length_to_use)
    {
        final int N = Math.min(path_items.length, length_to_use);
        final StringBuilder path = new StringBuilder();
        for (int i=0; i<N; ++i)
        {
            if (i > 0)
                path.append(PATH_SEP);
        	// Escape any path-seps inside item with backslashes
            path.append(path_items[i].replace("/", "\\/"));
        }
        return path.toString();
    }
    
    /** Split full path into pieces
     *  @param path Full path to an item
     *  @return Path elements
     */
    public static String[] splitPath(final String path)
    {
    	// Split on '/', but only those that are NOT preceded by '\'.
    	// '(?<!x)' means 'not preceded by x',
    	// and in this case the x=\ must be escaped twice:
    	// Once to get into the Java string, once more to pass to the regex.
        final String[] items = path.split("(?<!\\\\)/");
        // Un-escape any PATH_SEP that's inside each item
        for (int i = 0; i < items.length; ++i)
        	items[i] = items[i].replace("\\/", PATH_SEP);
		return items;
    }
}
