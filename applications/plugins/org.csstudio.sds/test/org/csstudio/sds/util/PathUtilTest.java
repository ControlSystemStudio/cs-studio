package org.csstudio.sds.util;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

/**
 * Test class for {@link PathUtil}.
 * @author Kai Meyer (C1 WPS)
 *
 */
public final class PathUtilTest {

    /**
     * Test method for {@link PathUtil#makePathRelativToAnchor(IPath, IPath)}.
     */
    @Test
    public void testMakePathRelativToWorkspace() {
        IPath anchorPath = new Path("C:/Test/Workspace");
        IPath path = new Path("C:/Test/Workspace/SDS/Test.css-sds");
        IPath result = new Path("SDS/Test.css-sds");
        IPath relativToAnchor = PathUtil.makePathRelativToAnchor(path,
                anchorPath);
        assertEquals(result, relativToAnchor);

        path = new Path("/SDS/Test.css-sds");
        result = new Path("SDS/Test.css-sds");
        relativToAnchor = PathUtil.makePathRelativToAnchor(path,
                anchorPath);
        assertEquals(result, relativToAnchor);

        path = new Path("/SDS/New/Test.css-sds");
        result = new Path("SDS/New/Test.css-sds");
        relativToAnchor = PathUtil.makePathRelativToAnchor(path,
                anchorPath);
        assertEquals(result, relativToAnchor);

        path = new Path("C:/Test/SDS/Test.css-sds");
        result = new Path("../SDS/Test.css-sds");
        relativToAnchor = PathUtil.makePathRelativToAnchor(path,
                anchorPath);
        assertEquals(result, relativToAnchor);

        anchorPath = new Path("C:/Test/New/Folder/Workspaces/workspace/Display.css-sds");

        path = new Path("C:/Test/SDS/Test.css-sds");
        result = new Path("../../../../SDS/Test.css-sds");
        relativToAnchor = PathUtil.makePathRelativToAnchor(path,
                anchorPath);
        assertEquals(result, relativToAnchor);

        anchorPath = new Path("C:/Test/New/Folder/Workspaces/workspace");

        path = new Path("C:/Test/SDS/Test.css-sds");
        result = new Path("../../../../SDS/Test.css-sds");
        relativToAnchor = PathUtil.makePathRelativToAnchor(path,
                anchorPath);
        assertEquals(result, relativToAnchor);

        anchorPath = new Path("/Test/New/Folder/Workspaces/workspace");

        path = new Path("/Test/SDS/Test.css-sds");
        result = new Path("../../../../SDS/Test.css-sds");
        relativToAnchor = PathUtil.makePathRelativToAnchor(path,
                anchorPath);
        assertEquals(result, relativToAnchor);

        anchorPath = new Path("C:/Test/SDS/Main.css-sds");

        path = new Path("C:/Test/SDS/Test.css-sds");
        result = new Path("Test.css-sds");
        relativToAnchor = PathUtil.makePathRelativToAnchor(path,
                anchorPath);
        assertEquals(result, relativToAnchor);
    }

    /**
     * Test method for {@link PathUtil#getFullPath(IPath, IPath)}.
     */
    @Test
    public void testgetFullPath() {

        IPath anchorPath = new Path("C:/Test/Workspace");
        IPath path = new Path("SDS/Test.css-sds");
        IPath result = new Path("C:/Test/Workspace/SDS/Test.css-sds");
        IPath relativToWorkspace = PathUtil.getFullPath(path, anchorPath);
        assertEquals(result, relativToWorkspace);

        path = new Path("SDS/New/Test.css-sds");
        result = new Path("C:/Test/Workspace/SDS/New/Test.css-sds");
        relativToWorkspace = PathUtil.getFullPath(path, anchorPath);
        assertEquals(result, relativToWorkspace);

        path = new Path("../SDS/Test.css-sds");
        result = new Path("C:/Test/SDS/Test.css-sds");
        relativToWorkspace = PathUtil.getFullPath(path, anchorPath);
        assertEquals(result, relativToWorkspace);

        anchorPath = new Path("C:/Test/New/Folder/Workspaces/workspace");

        path = new Path("../../../../SDS/Test.css-sds");
        result = new Path("C:/Test/SDS/Test.css-sds");
        relativToWorkspace = PathUtil.getFullPath(path, anchorPath);
        assertEquals(result, relativToWorkspace);

        anchorPath = new Path("/Test/New/Folder/Workspaces/workspace");

        path = new Path("../../../../SDS/Test.css-sds");
        result = new Path("/Test/SDS/Test.css-sds");
        relativToWorkspace = PathUtil.getFullPath(path,
                anchorPath);
        assertEquals(result, relativToWorkspace);

        anchorPath = new Path("/User/Kai/Workspaces/css");

        path = new Path("SDS/Test.css-sds");
        result = new Path("/User/Kai/Workspaces/css/SDS/Test.css-sds");
        relativToWorkspace = PathUtil.getFullPath(path,
                anchorPath);
        assertEquals(result, relativToWorkspace);

        anchorPath = new Path("/workspaces/runtime-css.product");

        path = new Path("SDS/BlackSymphony-Somewhere.mp3");
        result = new Path("/workspaces/runtime-css.product/SDS/BlackSymphony-Somewhere.mp3");
        relativToWorkspace = PathUtil.getFullPath(path,
                anchorPath);
        assertEquals(result, relativToWorkspace);

        path = new Path("/SDS/BlackSymphony-Somewhere.mp3");
        result = new Path("/SDS/BlackSymphony-Somewhere.mp3");
        relativToWorkspace = PathUtil.getFullPath(path,
                anchorPath);
        assertEquals(result, relativToWorkspace);

        anchorPath = new Path("C:/Test");

        path = new Path("SDS/Test.css-sds");
        result = new Path("C:/Test/SDS/Test.css-sds");
        relativToWorkspace = PathUtil.getFullPath(path, anchorPath);
        assertEquals(result, relativToWorkspace);
    }

}
