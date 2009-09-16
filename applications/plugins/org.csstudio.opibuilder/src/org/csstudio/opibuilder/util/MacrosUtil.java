package org.csstudio.opibuilder.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.platform.logging.CentralLogger;

/**The utility functions for macros operations.
 * @author Xihui Chen
 *
 */
public class MacrosUtil {

	
	private static final String MACRO_RIGHT_PART = "[)}]"; //$NON-NLS-1$

	private static final String MACRO_LEFT_PART = "\\$[{(]"; //$NON-NLS-1$
	
	private static final String DOLLARCHAR = "$"; //$NON-NLS-1$
	
		/**Replace the macros in the input with the real value.
		 * @param input the raw string which include the macros string $(macro)
		 * @return the string in which the macros have been replaced with the real value.
		 */
		public static String replaceMacros(AbstractWidgetModel widgetModel, String input){
			String result = input;
			if(!input.contains("$")) //$NON-NLS-1$
				return result;
			//replace with predefined constant macros
			Map<String, String> macroMap;
			if(widgetModel.getParent() != null)
				macroMap = widgetModel.getParent().getMacroMap();
			else
				macroMap = PreferencesHelper.getMacros();
			for(String macroName : macroMap.keySet()){
				// Create pattern to match either "${name}" or "$(name)".
				// Will actually also match a mixed case like "${name)".
				Pattern pattern = Pattern.compile(MACRO_LEFT_PART + macroName + MACRO_RIGHT_PART);
				if(!macroMap.get(macroName).contains(DOLLARCHAR))
					result = pattern.matcher(result).replaceAll(macroMap.get(macroName));
				else
					CentralLogger.getInstance().warn(null, DOLLARCHAR + " is not allowed in macros");
			}
			
			
			//replace with properties macro
			for(String propId : widgetModel.getAllPropertyIDs()){
				Pattern pattern = Pattern.compile(MACRO_LEFT_PART + propId + MACRO_RIGHT_PART);
				Matcher m = pattern.matcher(result);
				if(m.find()){					
					String propertyValue = widgetModel.getProperty(propId).getPropertyValue().toString();
					if(!propertyValue.contains(DOLLARCHAR))
						result = m.replaceAll(propertyValue);
					else
						CentralLogger.getInstance().warn(null, "The macros in " + propertyValue +
								" cannot be replaced for the property: " + widgetModel.getName() + "." + propId);					
				}
			}
			
			return result;
		}
		
	
}
