package org.csstudio.opibuilder.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.opibuilder.model.AbstractContainerModel;
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
	

	/**Replace the macros in the input with the real value.  Simply calls the three argument version below
	 * @param input the raw string which include the macros string $(macro)
	 * @return the string in which the macros have been replaced with the real value.
	 */
	public static String replaceMacros(AbstractWidgetModel widgetModel, String input){
		return MacrosUtil.replaceMacros(widgetModel, input, 0);
	}
	
	/**Replace the macros in the input with the real value. This is typically only called by the above two argument 
	 * version to iterate over macros included in the macro itself 
		 * @param input the raw string which include the macros string $(macro)
		 * @param level tells how many times this has iterated through this routine
		 * @return the string in which the macros have been replaced with the real value.
		 */
		public static String replaceMacros(AbstractWidgetModel widgetModel, String input, int level){
			String result = input;
			if(!input.contains(DOLLARCHAR)) //$NON-NLS-1$
				return result;
			//replace with predefined constant macros
			Map<String, String> macroMap;
			
			macroMap = getWidgetMacroMap(widgetModel);
			
			for(String macroName : macroMap.keySet()){
				// Create pattern to match either "${name}" or "$(name)".
				// Will actually also match a mixed case like "${name)".
				Pattern pattern = Pattern.compile(MACRO_LEFT_PART + macroName + MACRO_RIGHT_PART);
				if(!macroMap.get(macroName).contains(DOLLARCHAR))
					result = pattern.matcher(result).replaceAll(macroMap.get(macroName));
				else{
					//TODO Add a preference to allow the user how deep to allow macro substitution
					if (level+1 <3){
						String intResult = MacrosUtil.replaceMacros(widgetModel, macroMap.get(macroName), level+1);
						intResult = pattern.matcher(intResult).replaceAll(macroMap.get(macroName));
						result = pattern.matcher(result).replaceAll(intResult);

					}
					else {
					CentralLogger.getInstance().warn(null, DOLLARCHAR + " is not allowed in macros");
					}
				}
					}
			
			
			//replace with properties macro
			for(String propId : widgetModel.getAllPropertyIDs()){
				Pattern pattern = Pattern.compile(MACRO_LEFT_PART + propId + MACRO_RIGHT_PART);
				Matcher m = pattern.matcher(result);
				if(m.find()){		
					Object propertyValue = widgetModel.getProperty(propId).getPropertyValue();
					if(propertyValue != null){
						String ps = propertyValue.toString();
						if(!ps.contains(DOLLARCHAR))
							result = m.replaceAll(ps);
						else
							CentralLogger.getInstance().warn(null, "The macros in " + propertyValue +
									" cannot be replaced for the property: " + widgetModel.getName() + "." + propId);					
					}
				}
			}
			
			return result;
		}

		/**
		 * @param widgetModel
		 * @return the predefined macro map of the widget.
		 * This is the intrinsic map from the widget. Be careful to change the map contents.
		 */
		public static Map<String, String> getWidgetMacroMap(
				AbstractWidgetModel widgetModel) {
			Map<String, String> macroMap;
			if(widgetModel instanceof AbstractContainerModel)
				macroMap = ((AbstractContainerModel)widgetModel).getMacroMap();
			else {
				if(widgetModel.getParent() != null)
					macroMap = widgetModel.getParent().getMacroMap();
				else
					macroMap = PreferencesHelper.getMacros();
			}
			return macroMap;
		}
		
	
}
