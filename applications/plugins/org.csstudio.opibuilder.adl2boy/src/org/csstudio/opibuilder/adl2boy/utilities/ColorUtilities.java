/**
 * 
 */
package org.csstudio.opibuilder.adl2boy.utilities;

import java.util.Arrays;
import java.util.Iterator;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.util.MediaService;
import org.csstudio.opibuilder.util.OPIColor;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.RGB;

/**
 * @author hammonds
 *
 */
public class ColorUtilities {

	public static void loadToBOY(IFile selectedFile) {
		IPreferenceStore store = OPIBuilderPlugin.getDefault().getPreferenceStore();
		
		String colorFileName = store.getString(org.csstudio.opibuilder.preferences.PreferencesHelper.COLOR_FILE);
		System.out.println("ColorFile " + colorFileName);
	}

	/**
	 * @return
	 */
	public static OPIColor[] getTableColors() {
		OPIColor[] tableColors;
		IPreferenceStore store = OPIBuilderPlugin.getDefault().getPreferenceStore();


		OPIColor[] colors = MediaService.getInstance().getAllPredefinedColors();
		tableColors = new OPIColor[colors.length-4];
		for (int i = 0; i < tableColors.length; i++){
			tableColors[i] = colors[i+4];
		}
		return tableColors;
	}

	/**
	 * @param store
	 * @param colorFileToLoad
	 */
	public static void loadToBoyColorTable(String colorFileToLoad) {

		IPreferenceStore store = OPIBuilderPlugin.getDefault().getPreferenceStore();
		store.setDefault(PreferencesHelper.COLOR_FILE, colorFileToLoad);
		MediaService.getInstance().reload();
	}

	/**
	 * @param rgbColor
	 * @return
	 */
	public static OPIColor matchToTableColor(RGB rgbColor) {
		OPIColor[] tableColors = getTableColors();
		Iterator<OPIColor> colorIterator = Arrays.asList(tableColors).iterator();
		boolean colorFound = false;
		OPIColor foundColor = new OPIColor(rgbColor);
		while (colorIterator.hasNext() && !colorFound){
			OPIColor tableColor = colorIterator.next();
			if ( tableColor.getRGBValue().equals(rgbColor) ){
				foundColor = tableColor;
			}
		}
		return foundColor;
	}

}
