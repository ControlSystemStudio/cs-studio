/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.product;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.java.string.StringSplitter;
import org.eclipse.core.runtime.Path;

/** Information about a "linked folder"
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LinkedResource
{
	final private String file_path;
	final private String resource;

	/** Parse linked resource option string
	 *
	 *  <p>Option string is a comma-separated list of values.
	 *
	 *  <p>Each value is of the form "path=resource",
	 *  where "path" is the path to a folder in the file system,
	 *  and "resource" is the resource to create within the workspace.
	 *
	 *  <p>If only a file system path is provided, the resource defaults to "/CSS/Share".
	 *
	 *  <p>File system path and resource can be enclosed in double-quotes.
	 *
	 *  <p>Examples:
	 *
	 *  <ul>
	 *  <li><code>/path/to/share</code><br>
	 *      A default resource with name "/CSS/Share" in the workspace will
	 *      link to /path/to/share in the file system.
	 *  <li><code>/path/to/share,"/path/to/an other"=/MyProject/Other</code><br>
	 *      As before, and in addition a project "MyProject" will be
	 *      created with linked folder "Other" that points to
	 *      "/path/to/an other" in the file system.
	 *      The file system path must be enclosed in double-quotes because
	 *      it contains a space.
	 *  </ul>
	 *
	 *  <p>The resource name should always be in "Unix" notation with "/"
	 *  as the path element separator.
	 *  If an initial "/" is missing, it will be added.
	 *
	 *  <p>The file system path can be in "Unix" or "Windows" notation.
	 *  It is not checked in any way, simply passed on when creating the
	 *  shared resource.
	 *
	 *  @param option Option string
	 *  @return {@link LinkedResource}s
	 *  @throws Exception on parsing error
	 */
	public static LinkedResource[] fromString(final String option) throws Exception
	{
		final String[] links = StringSplitter.splitIgnoreInQuotes(option, ',', true);

		final List<LinkedResource> items = new ArrayList<LinkedResource>();
		for (String link : links)
		{
			final String[] path_folder = StringSplitter.splitIgnoreInQuotes(link, '=', true);

			final LinkedResource share_link = new LinkedResource(path_folder[0],
						path_folder.length > 1 ? path_folder[1] : "/CSS/Share");
			items.add(share_link);
		}
		return items.toArray(new LinkedResource[items.size()]);
	}

	/** Initialize
	 *
	 *  @param path File system path
	 *  @param resource Resource within workspace
	 */
	public LinkedResource(final String path, final String resource)
    {
		this.file_path = path;
		if (resource.startsWith("/"))
			this.resource = resource;
		else
			this.resource = "/" + resource;
    }

	/** @return File system path of linked folder */
	public String getFileSystemName()
    {
    	return file_path;
    }

	/** @return Name of linked folder resource in workspace */
	public String getResourceName()
    {
    	return resource;
    }

	/** @return <code>true</code> if the resource is a project,
	 *          <code>false</code> if it is a folder below a project
	 */
	public boolean isProject()
	{
		final Path path = new Path(resource);
		return path.segmentCount() == 1;
	}

	/** @return Debug representation */
    @Override
    public String toString()
	{
		return "Linked folder '" + resource + "' = Location '" + file_path + "'";
	}
}
