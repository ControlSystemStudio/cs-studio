/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.csstudio.opibuilder.persistence.URLPath;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.junit.Ignore;
import org.junit.Test;

/** [Headless] JUnit Plug-In test
 *
 * @author Xihui Chen
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ResourceUtilIT {
    private static final String TEST_MESSAGE = "Test OK";
    public final static IPath URL_PATH = new Path("http://ics-srv-web2.sns.ornl.gov/opi");
    public final static IPath URL_PATH2 = new Path("platform:/plugin/org.csstudio.opibuilder/");
    public final static IPath LOCAL_PATH = new Path("C:\\Users\\5hz\\Desktop\\sis3302Channel.opi");
    public final static IPath LOCAL_PATH2 = new Path("file:C:\\Users\\5hz\\Desktop\\sis3302Channel.opi");
    public final static IPath WORKSCPACE_PATH = new Path("/BOY Examples/main.opi");

    /** This test requires a workspace
     *  @throws Exception on error
     */
    @Test
    public void testPathToInputStream() throws Exception
    {
        // Prepare known workspace layout
        final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        System.out.println("Workspace: " + root.getLocation());

        // Create test project
        final IProject project = root.getProject("Project");
        if (! project.exists())
            project.create(new NullProgressMonitor());
        project.open(new NullProgressMonitor());

        // Create test folder
        final IFolder folder = project.getFolder("Folder");
        if (! folder.exists())
            folder.create(true, true, new NullProgressMonitor());

        // Create test File
        final IFile file = folder.getFile("File.ext");
        if (! file.exists())
        {
            final String text = TEST_MESSAGE + "\n";
            final InputStream content = new ByteArrayInputStream(text.getBytes());
            file.create(content, true, new NullProgressMonitor());
        }

        // Actual tests

        // Read file from workspace
        Path path = new Path("Project/Folder/File.ext");
        System.out.println("Workspace path: " + path);
        InputStream stream = ResourceUtil.pathToInputStream(path);
        assertFileContent(stream);

        // Read file from local file system
        path = new Path(file.getLocation().makeAbsolute().toOSString());
        System.out.println("Local file system path: " + path);
        stream = ResourceUtil.pathToInputStream(path);
        assertFileContent(stream);

        // Read file as URL
        path = new Path("file://" + file.getLocation().makeAbsolute().toOSString());
        System.out.println("URL path: " + path);
        stream = ResourceUtil.pathToInputStream(path);
        assertFileContent(stream);

        // Read some network URL
        path = new Path("http://www.google.com");
        System.out.println("Web URL: " + path);
        stream = ResourceUtil.pathToInputStream(path);
        assertNotNull(stream);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        final String line = reader.readLine();
        // Not checking content, but there should be at least one line of text on the web page
        System.out.println(line);
        assertNotNull(line);
        reader.close();

        // Missing workspace file
        path = new Path("Project/Folder/NoSuchFile.xyz");
        System.out.println("Bad Workspace path: " + path);
        try
        {
            ResourceUtil.pathToInputStream(path);
            fail("Found missing file?");
        }
        catch (Exception ex)
        {
            final String message = ex.getMessage();
            System.out.println(message);
            assertTrue(ex.getCause() instanceof FileNotFoundException);
        }

        // URL to non-existing resource
        path = new Path("http://localhost/Folder/NoSuchFile.xyz");
        System.out.println("Bad URL: " + path);
        try
        {
            ResourceUtil.pathToInputStream(path);
            fail("Found missing file?");
        }
        catch (Exception ex)
        {
            final String message = ex.getMessage();
            System.out.println(message);
//            assertTrue(message.contains("Cannot open"));
//            assertTrue(message.contains("localhost/Folder/NoSuchFile.xyz"));
        }
    }

    /** Check content of test file */
    private void assertFileContent(final InputStream stream) throws Exception
    {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        final String line = reader.readLine();
        System.out.println(line);
        // Note: reads line without trailing "\n"
        assertEquals(line, TEST_MESSAGE);
        reader.close();
    }

    /** This could run as a plain JUnit test */
    @Test
    public void testBuildRelativePath(){
        IPath path = new URLPath("http://a/b/c/d.txt");
        IPath path2 = new URLPath("http://a/b/e.txt");
        System.out.println(Arrays.toString(path2.segments()));
        IPath path3 = path.makeRelativeTo(path2);
        assertEquals("../c/d.txt", path3.toString());
        System.out.println(Arrays.toString(new Path("../c/d/e.txt").segments()));
        IPath path4 = ResourceUtil.buildRelativePath(path2, path);
        assertEquals("../c/d.txt", path4.toString());
    }

    @Test
    @Ignore
    public void testURLPathToInputStream() throws Exception{
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ResourceUtil.pathToInputStream(URL_PATH)));
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            System.out.println(inputLine);
        in.close();
    }

    @Test
    @Ignore
    public void testLOCALPathToInputStream() throws Exception{
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ResourceUtil.pathToInputStream(LOCAL_PATH)));
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            System.out.println(inputLine);
        in.close();
    }

    @Test
    @Ignore
    public void testIsExistingWorkspaceFile(){
        assertEquals(ResourceUtil.isExistingWorkspaceFile(WORKSCPACE_PATH), true);
    }

    @Test
    public void testIsExistingLocalFile() {
        assertEquals(ResourceUtil.isExistingLocalFile(LOCAL_PATH),true);
        assertEquals(ResourceUtil.isExistingLocalFile(LOCAL_PATH2),true);

    }

    @Test
    public void testIsURL(){
        assertEquals(ResourceUtil.isURL(URL_PATH.toString()), true);
        assertEquals(ResourceUtil.isURL(LOCAL_PATH.toString()), false);
    }

    @Test
    public void testIsExistingURL(){
        assertEquals(ResourceUtil.isExistingURL(URL_PATH, true), true);
        assertEquals(ResourceUtil.isExistingURL(URL_PATH.append("main.opi"), true), true);
        assertEquals(ResourceUtil.isExistingURL(URL_PATH.append("main2.opi"), true), false);
    }

    @Test
    public void testGetFileOnSearchPath(){
        IPath p = ResourceUtil.getFileOnSearchPath(new Path("main.opi"), true);
        System.out.println(p);
        assertNotNull(p);
    }
}
