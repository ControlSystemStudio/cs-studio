package org.csstudio.opibuilder.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Test;


public class ResourceUtilTest {
	public final static IPath URL_PATH = new Path("http://ics-srv-web2.sns.ornl.gov/opi/main.opi");
	public final static IPath LOCAL_PATH = new Path("C:\\Users\\5hz\\Desktop\\2_5_1_XY_Graph.opi");

	@Test
	public void testBuildRelativePath(){
		IPath path = new Path("http://a/b/c/d.txt");
		IPath path2 = new Path("http://a/b/e.txt");
		IPath path3 = path.makeRelativeTo(path2);
		assertEquals("../c/d.txt", path3.toString());
		IPath path4 = ResourceUtil.buildRelativePath(path2, path);
		assertEquals("../c/d.txt", path4.toString());
	}
	
	@Test
	public void testURLPathToInputStream() throws Exception{
		BufferedReader in = new BufferedReader(
				new InputStreamReader(ResourceUtil.pathToInputStream(URL_PATH)));
		String inputLine;
		while ((inputLine = in.readLine()) != null)
		    System.out.println(inputLine);
		in.close();
    }
	
	@Test
	public void testLOCALPathToInputStream() throws Exception{
		BufferedReader in = new BufferedReader(
				new InputStreamReader(ResourceUtil.pathToInputStream(LOCAL_PATH)));
		String inputLine;
		while ((inputLine = in.readLine()) != null)
		    System.out.println(inputLine);
		in.close();
    }
}
