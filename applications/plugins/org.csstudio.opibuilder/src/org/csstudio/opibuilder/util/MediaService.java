package org.csstudio.opibuilder.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.DataFormatException;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

/**A service help to maintain the color macros.
 * @author Xihui Chen
 *
 */
public final class MediaService {

	/**
	 * The shared instance of this class.
	 */
	private static MediaService instance = null;
	
	private Map<String, OPIColor> colorMap;
	private Map<String, OPIFont> fontMap;
	
	private IPath colorFilePath;
	private IPath fontFilePath;
	
	public final static RGB DEFAULT_UNKNOWN_COLOR= new RGB(0,0,0);
	
	public final static FontData DEFAULT_UNKNOWN_FONT = CustomMediaFactory.FONT_ARIAL;
	
	
	/**
	 * @return the instance
	 */
	public synchronized static final MediaService getInstance() {
		if(instance == null)
			instance = new MediaService();
		return instance;
	}	

	public MediaService() {
		colorMap = new LinkedHashMap<String, OPIColor>();
		fontMap = new LinkedHashMap<String, OPIFont>();
		loadColorFile();			
		loadFontFile();
	}

	private void loadColorFile() {
		colorFilePath = PreferencesHelper.getColorFilePath();		
		if(colorFilePath == null){
			CentralLogger.getInstance().warn(this, "No color definition file was found.");
			return;
		}
			
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
	
	private void loadFontFile() {
		fontFilePath = PreferencesHelper.getFontFilePath();		
		if(fontFilePath == null){
			CentralLogger.getInstance().warn(this, "No font definition file was found.");
			return;
		}
			
		try {
			//read file				
			InputStream inputStream = ResourceUtil.pathToInputStream(fontFilePath);
			
			BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream));
			String line;
			//fill the font map.
			while((line = reader.readLine()) != null){
				int i;
				if((i = line.indexOf('=')) != -1){
					String name = line.substring(0, i).trim();
					try{
						FontData fontdata = StringConverter.asFontData(line.substring(i+1).trim());
						
						fontMap.put(name, new OPIFont(name, fontdata));
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
	 * @return the RGB color, or the default RGB value if the name doesn't exist in the color file.
	 */
	public RGB getColor(String name){
		if(colorMap.containsKey(name))
			return colorMap.get(name).getRGBValue();
		return DEFAULT_UNKNOWN_COLOR;
	}
	
	public OPIColor getOPIColor(String name){
		if(colorMap.containsKey(name))
			return colorMap.get(name);		
		return new OPIColor(name, DEFAULT_UNKNOWN_COLOR);
	}
	
	public OPIColor[] getAllPredefinedColors(){
		OPIColor[] result = new OPIColor[colorMap.size()];
		int i=0;
		for(OPIColor c : colorMap.values()){
			result[i++] = c; 
		}
		return result;
	}
	
	
	/**Get the font from the predefined font map, which is defined in the font file.
	 * @param name the predefined name of the font.
	 * @return the FontData, or the default font if the name doesn't exist in the font file.
	 */
	public FontData getFontData(String name){
		if(fontMap.containsKey(name))
			return fontMap.get(name).getFontData();
		return DEFAULT_UNKNOWN_FONT;
	}
	
	public OPIFont getOPIFont(String name){
		if(fontMap.containsKey(name))
			return fontMap.get(name);		
		return new OPIFont(name, DEFAULT_UNKNOWN_FONT);
	}
	
	public OPIFont[] getAllPredefinedFonts(){
		OPIFont[] result = new OPIFont[fontMap.size()];
		int i=0;
		for(OPIFont c : fontMap.values()){
			result[i++] = c; 
		}
		return result;
	}
	
	
}
