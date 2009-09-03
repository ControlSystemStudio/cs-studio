package org.csstudio.opibuilder.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.palette.MajorCategories;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.DataFormatException;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;

/**A service help to maintain the color macros.
 * @author Xihui Chen
 *
 */
public final class ColorService {

	/**
	 * The shared instance of this class.
	 */
	private static ColorService instance = null;
	
	private Map<String, OPIColor> colorMap;
	
	private IPath colorFilePath;
	
	public static RGB DEFAULT_UNKNOWN_COLOR= new RGB(0,0,0);
	
	
	/**
	 * @return the instance
	 */
	public synchronized static final ColorService getInstance() {
		if(instance == null)
			instance = new ColorService();
		return instance;
	}	

	public ColorService() {
		colorFilePath = new Path("/OPIBuilder/color.def");
		colorMap = new LinkedHashMap<String, OPIColor>();
		
		try {
			//read file				
			InputStream inputStream = ResourceUtil.pathToInputStream(colorFilePath);
			
			BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream));
			String line;
			//fill the color map.
			while((line = reader.readLine()) != null){
				int i;
				if((i = line.indexOf('=')) != -1){
					String name = line.substring(0, i).trim();
					try{
						RGB color = StringConverter.asRGB(line.substring(i+1).trim());
						
						colorMap.put(name, new OPIColor(name, color));
					}catch (DataFormatException e) {
						CentralLogger.getInstance().error(this, e);
					}
				}				
			}
			inputStream.close();
			reader.close();
		} catch (Exception e) {
			CentralLogger.getInstance().error(this, e);
		}			
	}
	
	/**Get the color from the predefined color map, which is defined in the color file.
	 * @param name the predefined name of the color.
	 * @return the RGB color, or null if the name doesn't exist in the color file.
	 */
	public RGB getColor(String name){
		return colorMap.get(name).getRGBValue();
	}
	
	public OPIColor getOPIColor(String name){
		OPIColor result =colorMap.get(name);		
		if(result == null)
			return new OPIColor(name + "(N/A)", DEFAULT_UNKNOWN_COLOR);
		return result;
	}
	
	public OPIColor[] getAllPredefinedColors(){
		OPIColor[] result = new OPIColor[colorMap.size()];
		int i=0;
		for(OPIColor c : colorMap.values()){
			result[i++] = c; 
		}
		return result;
	}
	
	
	
	
	
}
