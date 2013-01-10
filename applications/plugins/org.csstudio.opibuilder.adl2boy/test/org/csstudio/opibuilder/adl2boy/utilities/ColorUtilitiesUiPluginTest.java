/**
 * 
 */
package org.csstudio.opibuilder.adl2boy.utilities;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

import junit.framework.TestCase;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.adl2boy.ADL2BOYPlugin;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.RGB;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author hammonds
 * 
 */
public class ColorUtilitiesUiPluginTest extends TestCase {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject("org.csstudio.opibuilder.adl2boy");
		if (!project.exists()) {
			project.create(new NullProgressMonitor());
		}
		project.open(new NullProgressMonitor());
		IPreferenceStore store = OPIBuilderPlugin.getDefault()
				.getPreferenceStore();
		
		String colorFileName = "org.csstudio.opibuilder.adl2boy/color.def";

		// Create a default map
		StringBuffer sb = new StringBuffer();
		sb.append("Red = 255, 0, 0\n");
		sb.append("Green = 0,255,0\n");
		sb.append("Blue = 0,0,255\n");
		ByteArrayInputStream bais = new ByteArrayInputStream(sb.toString()
				.getBytes());

		IPath colorFilePath = ResourceUtil.getPathFromString( colorFileName);
		System.out.println("colorFileName " + colorFileName + "\n colorFilePath" + colorFilePath);
		IFile colorFile = ResourcesPlugin.getWorkspace().getRoot().getFile(colorFilePath);
		System.out.println("colorFile " + colorFile + ", " + colorFile.getFullPath());
		colorFile.create(bais, true, null);
		store.setValue(org.csstudio.opibuilder.preferences.PreferencesHelper.COLOR_FILE, colorFileName );
		
		// copy a default file from the plugin
		URL adlURL = FileLocator.toFileURL(FileLocator.find(ADL2BOYPlugin.getDefault().getBundle(), new Path ("resources/test.adl"), null));
		File adlFile = new File(adlURL.getPath());
		IPath adlFilePath = ResourceUtil.getPathFromString( "org.csstudio.opibuilder.adl2boy/test.adl");
		IFile adlIFile = ResourcesPlugin.getWorkspace().getRoot().getFile(adlFilePath);
		adlIFile.create(new FileInputStream(adlFile), true, new NullProgressMonitor());
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject("org.csstudio.opibuilder.adl2boy");
		project.delete(true, new NullProgressMonitor());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.opibuilder.adl2boy.utilities.ColorUtilities#loadToBOY(org.eclipse.core.resources.IFile)}
	 * .
	 */
	@Test
	public void testLoadToBOY() {


		IPath testFilePath = ResourceUtil
				.getPathFromString("/org.csstudio.opibuilder.adl2boy/test.adl");
		ColorUtilities.loadToBOY(testFilePath);

		OPIColor[] colors = ColorUtilities.getTableColors();
		assertEquals("NumColors", 68, colors.length);
	}

	/**
	 * Test method for
	 * {@link org.csstudio.opibuilder.adl2boy.utilities.ColorUtilities#loadToBoyColorTable(java.lang.String)}
	 * .
	 */
	@Test
	public void testLoadToBoyColorTable() {
		ColorUtilities
				.loadToBoyColorTable("platform:/base/plugin/org.csstudio.opibuilder.adl2boy/resources/color.def");
		OPIColor[] tableColors = ColorUtilities.getTableColors();
		assertEquals("ColorTableLength ", 3, tableColors.length);
		assertEquals("Color 1 Name", "Red", tableColors[0].getColorName());
		assertEquals("Color 1 RGB Red", new RGB(255, 0, 0),
				tableColors[0].getRGBValue());
		assertEquals("Color 2 Name", "Green", tableColors[1].getColorName());
		assertEquals("Color 2 RGB Green", new RGB(0, 255, 0),
				tableColors[1].getRGBValue());
		assertEquals("Color 3 Name", "Blue", tableColors[2].getColorName());
		assertEquals("Color 3 RGB Blue", new RGB(0, 0, 255),
				tableColors[2].getRGBValue());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.opibuilder.adl2boy.utilities.ColorUtilities#matchToTableColor(org.eclipse.swt.graphics.RGB)}
	 * .
	 */
	@Test
	public void testMatchToTableColor() {
		ColorUtilities
				.loadToBoyColorTable("platform:/base/plugin/org.csstudio.opibuilder.adl2boy/resources/color.def");
		OPIColor color = ColorUtilities.matchToTableColor(new RGB(255, 0, 0));
		assertEquals("Match Red ",
				new OPIColor("Red", new RGB(255, 0, 0), true), color);
		assertFalse("Match Red", new OPIColor(new RGB(255, 0, 0)).equals(color));
		color = ColorUtilities.matchToTableColor(new RGB(128, 128, 128));
		assertEquals("Match Grey ", new OPIColor(new RGB(128, 128, 128)), color);

	}

	/**
	 * Test method for
	 * {@link org.csstudio.opibuilder.adl2boy.utilities.ColorUtilities#getColorMap(org.csstudio.utility.adlparser.fileParser.ADLWidget)}
	 * .
	 */
//	@Test
//	public void testGetColorMap() {
//		fail("Not yet implemented");
//	}

}
