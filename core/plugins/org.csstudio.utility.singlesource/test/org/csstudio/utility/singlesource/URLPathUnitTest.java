/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.singlesource;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

/** JUnit test of the URLPath
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class URLPathUnitTest
{
    @Test
    public void demoOriginalPath()
    {
        // File path works fine
        IPath path = new Path("c:/folder/path");
        assertThat(path.toOSString(), equalTo("c:/folder/path"));

        // .. but URL gets damaged
        path = new Path("http://host:80/page");
        assertThat(path.toString(), equalTo("http:/host:80/page"));
    }

    @Test
    public void testURL() throws Exception
    {
        // URL is preserved
        IPath path = new URLPath("http://host:80/some/page");
        assertThat(path.toString(), equalTo("http://host:80/some/page"));

        // Handle :.."
        path = new URLPath("http://host:80/some/other/../page");
        assertThat(path.toString(), equalTo("http://host:80/some/page"));
        
        assertThat(path.segmentCount(), equalTo(2));
        assertThat(path.segment(0), equalTo("some"));
        assertThat(path.lastSegment(), equalTo("page"));
        
        // Remove segments
        assertThat(path.removeFirstSegments(1).toString(), equalTo("http://host:80/page"));
        assertThat(path.removeLastSegments(1).toString(), equalTo("http://host:80/some"));
        assertThat(path.removeLastSegments(1).toString(), equalTo("http://host:80/some"));

        // Add segments
        assertThat(path.append("there").toString(), equalTo("http://host:80/some/page/there"));
        assertThat(path.append("/there").toString(), equalTo("http://host:80/some/page/there"));
        assertThat(path.append("..").toString(), equalTo("http://host:80/some"));
    }

    @Test
    public void testURLHandling() throws Exception
    {
        final ResourceHelper helper = new ResourceHelper();

        IPath path = helper.newPath("c:/folder/path");
        assertThat(path, instanceOf(Path.class));
        assertThat(path.toString(), equalTo("c:/folder/path"));

        path = helper.newPath("http://host:80/page");
        assertThat(path, instanceOf(URLPath.class));
        assertThat(path.toString(), equalTo("http://host:80/page"));
        
        // "Device"
        assertThat(path.getDevice(), equalTo("http://host:80"));
        
        // End
        path = new Path("/path/to/file");
        path = path.addTrailingSeparator();
        assertThat(path.toString(), equalTo("/path/to/file/"));
        path = path.removeTrailingSeparator();
        assertThat(path.toString(), equalTo("/path/to/file"));

        path = new URLPath("ftp://host/file");
        path = path.addTrailingSeparator();
        assertThat(path.toString(), equalTo("ftp://host/file/"));
        path = path.removeTrailingSeparator();
        assertThat(path.toString(), equalTo("ftp://host/file"));
        
        // File extensions
        path = new Path("/path/to/file");
        path = path.addFileExtension("ext");
        assertThat(path.toString(), equalTo("/path/to/file.ext"));
        assertThat(path.getFileExtension(), equalTo("ext"));
        path = path.removeFileExtension();
        assertThat(path.toString(), equalTo("/path/to/file"));

        path = new URLPath("ftp://host/file");
        path = path.addFileExtension("ext");
        assertThat(path.toString(), equalTo("ftp://host/file.ext"));
        assertThat(path.getFileExtension(), equalTo("ext"));
        path = path.removeFileExtension();
        assertThat(path.toString(), equalTo("ftp://host/file"));

        // File access
        path = new Path("/path/to/file.ext");
        assertThat(path.toFile().getAbsolutePath(), equalTo("/path/to/file.ext"));

        path = new URLPath("file://path/to/file.ext");
        assertThat(path.toFile().getAbsolutePath(), equalTo("/path/to/file.ext"));
    }
    
    @Test
    public void testURLCombinations() throws Exception
    {
        IPath path = new URLPath("http://host:80/some/page");
        
        IPath other = new URLPath("http://host:80/some");
        assertThat(other.isPrefixOf(path), equalTo(true));
        assertThat(other.matchingFirstSegments(path), equalTo(1));
        
        IPath combined = other.makeRelativeTo(path);
        assertThat(combined.toString(), equalTo("http://host:80/some/page/some"));
    }

    @Test
    public void testURLAccess() throws Exception
    {
        final ResourceHelper helper = new ResourceHelper();
        final IPath path = new URLPath("http://www.google.com");
        assertThat(helper.exists(path), equalTo(true));
    }
}
