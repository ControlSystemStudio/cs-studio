/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

/** Helper for handling the path names of alarm tree elements.
 *  Path looks like "/root/area/system/subsystem/pv_name".
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AlarmTreePath
{
    /** Separator used to create path names to items in the alarm tree */
    final private static String PATH_SEP = "/";

    /** @param path String to test
     *  @return <code>true</code> if a string is a path or just a plain name
     */
    public static boolean isPath(final String path)
    {
        return path.startsWith(PATH_SEP);
    }

    /** Build path name
     *  @param path Parent path or <code>null</code> when starting at root
     *  @param item Name of item at end of path
     *  @return Full path name to item
     */
    public static String makePath(final String path, final String item)
    {
        final StringBuilder result = new StringBuilder();
        if (path != null)
        {
            if (! isPath(path))
                result.append(PATH_SEP);
            result.append(path);
        }
        result.append(PATH_SEP);
        // Escape any path-seps inside item with backslashes
        result.append(item.replace(PATH_SEP, "\\/"));
        return result.toString();
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
            path.append(PATH_SEP);
        	// Escape any path-seps inside item with backslashes
            path.append(path_items[i].replace(PATH_SEP, "\\/"));
        }
        return path.toString();
    }

    /** Split full path into pieces
     *  @param path Full path to an item
     *  @return Path elements
     */
    public static String[] splitPath(final String path)
    {
        // Shortcut for path that's really just a name
        if (!isPath(path))
            return new String[] { path.replace("\\/", PATH_SEP) };
    	// Split on '/', but only those that are NOT preceded by '\'.
    	// '(?<!x)' means 'not preceded by x',
    	// and in this case the x=\ must be escaped twice:
    	// Once to get into the Java string, once more to pass to the regex.
        // Also skip the initial '/'
        final String[] items = path.substring(1).split("(?<!\\\\)/");
        // Un-escape any PATH_SEP that's inside each item
        for (int i = 0; i < items.length; ++i)
        	items[i] = items[i].replace("\\/", PATH_SEP);
		return items;
    }

    /** Get last path element
     *  @param path Full path to an item
     *  @return Name, i.e. last path element
     */
    public static String getName(final String path)
    {
        final String elements[] = splitPath(path);
        return elements[elements.length-1];
    }
}
