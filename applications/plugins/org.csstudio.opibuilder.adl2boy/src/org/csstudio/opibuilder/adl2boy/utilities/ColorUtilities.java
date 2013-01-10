/**
 * 
 */
package org.csstudio.opibuilder.adl2boy.utilities;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.adl2boy.ADL2BOYPlugin;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.MediaService;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.ColorMap;
import org.csstudio.utility.adlparser.fileParser.ParserADL;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.RGB;

/**
 * @author hammonds
 * 
 */
public class ColorUtilities {

	public static void loadToBOY(IPath selectedFile) {
		IPreferenceStore store = OPIBuilderPlugin.getDefault()
				.getPreferenceStore();

		String colorFileName = store
				.getString(org.csstudio.opibuilder.preferences.PreferencesHelper.COLOR_FILE);
		if (!colorFileName.contains(":/")) {
			colorFileName = Platform.getLocation() + colorFileName;
		}
		IPreferenceStore adl2boyStore = ADL2BOYPlugin.getDefault()
				.getPreferenceStore();
		String colorPrefix = adl2boyStore
				.getString(org.csstudio.opibuilder.adl2boy.preferences.PreferenceConstants.P_COLOR_PREFIX);
		ADLWidget root = ParserADL.getNextElement(new File(Platform
				.getLocation() + selectedFile.toPortableString()));
		// Get the color map
		RGB[] colorMap = ColorUtilities.getColorMap(root);
		// Configure the display
		IPath colorFilePath = PreferencesHelper.getColorFilePath()
				.makeRelativeTo(Platform.getLocation());
		IFile colorIFile = getIFileFromIPath(colorFilePath);
		StringBuffer sb = new StringBuffer();
		
		for (int ii = 0; ii < colorMap.length; ii++) {
				sb.append("\n" + colorPrefix + ii + " = " + colorMap[ii].red
						+ "," + colorMap[ii].green + "," + colorMap[ii].blue);
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(sb.toString().getBytes());
		try {
			colorIFile.appendContents(bais, true, true, null);
		} catch (CoreException e1) {
			ConsoleService.getInstance().writeError(
					"loadToBOY: IOException: Trouble closing to File "
							+ colorFilePath);
			e1.printStackTrace();
		}
		
		
		MediaService.getInstance().reload();
	}

	/**
	 * @return
	 */
	public static OPIColor[] getTableColors() {
		OPIColor[] tableColors;

		OPIColor[] colors = MediaService.getInstance().getAllPredefinedColors();
		tableColors = new OPIColor[colors.length - 4];
		for (int i = 0; i < tableColors.length; i++) {
			tableColors[i] = colors[i + 4];
		}
		return tableColors;
	}

	/**
	 * @param store
	 * @param colorFileToLoad
	 */
	public static void loadToBoyColorTable(String colorFileToLoad) {

		IPreferenceStore store = OPIBuilderPlugin.getDefault()
				.getPreferenceStore();
		store.setDefault(PreferencesHelper.COLOR_FILE, colorFileToLoad);
		MediaService.getInstance().reload();
	}

	/**
	 * @param rgbColor
	 * @return
	 */
	public static OPIColor matchToTableColor(RGB rgbColor) {
		OPIColor[] tableColors = getTableColors();
		Iterator<OPIColor> colorIterator = Arrays.asList(tableColors)
				.iterator();
		boolean colorFound = false;
		OPIColor foundColor = new OPIColor(rgbColor);
		while (colorIterator.hasNext() && !colorFound) {
			OPIColor tableColor = colorIterator.next();
			if (tableColor.getRGBValue().equals(rgbColor)) {
				foundColor = tableColor;
			}
		}
		return foundColor;
	}

	/**
	 * Get the colorMap from an ADLroot ADLWidget
	 * 
	 * @param root
	 * @return
	 */
	public static RGB[] getColorMap(ADLWidget root) {
		RGB colorMap[] = new RGB[0];
		for (ADLWidget adlWidget : root.getObjects()) {
			String widgetType = adlWidget.getType();
			try {
				if (widgetType.equals("color map")) {
					ColorMap tempColorMap = new ColorMap(adlWidget);
					colorMap = tempColorMap.getColors();
				}
			} catch (Exception ex) {
				ConsoleService.getInstance().writeError(
						"ColorUtilities.getColorMap: Error reading ColorMap");
				System.out.println("Error reading ColorMap");
				ex.printStackTrace();
			}
		}
		return colorMap;
	}

	/**Get the IFile from IPath.
	 * @param path Path to file in workspace
	 * @return the IFile. <code>null</code> if no IFile on the path, file does not exist, internal error.
	 */
	private static IFile getIFileFromIPath(final IPath path)
	{
	    try
	    {
    		final IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(
    				path, false);
    		if (r!= null && r instanceof IFile)
		    {
    		    final IFile file = (IFile) r;
    		    if (file.exists())
    		        return file;
		    }
	    }
	    catch (Exception ex)
	    {
	        // Ignored
	    }
	    return null;
	}
}
