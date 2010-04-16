package org.csstudio.opibuilder.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.csstudio.opibuilder.persistence.URLPath;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Ignore;
import org.junit.Test;


public class ResourceUtilTest {
	public final static IPath URL_PATH = new Path("http://ics-srv-web2.sns.ornl.gov/opi/main.opi");
	public final static IPath LOCAL_PATH = new Path("C:\\Users\\5hz\\Desktop\\2_5_1_XY_Graph.opi");

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
}
