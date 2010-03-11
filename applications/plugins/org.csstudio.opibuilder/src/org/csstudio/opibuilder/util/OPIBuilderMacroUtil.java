package org.csstudio.opibuilder.util;

import java.util.Map;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.preferences.PreferencesHelper;


/**Selfdefined MacroUtil for opibuilder.
 * @author Xihui Chen
 *
 */
public class OPIBuilderMacroUtil {
	/**Replace the macros in the input with the real value.  Simply calls the three argument version below
	 * @param input the raw string which include the macros string $(macro)
	 * @return the string in which the macros have been replaced with the real value.
	 */
	public static String replaceMacros(AbstractWidgetModel widgetModel, String input){
		
		try {
			return MacroUtil.replaceMacros(input, new WidgetMacroTableProvider(widgetModel));
		} catch (InfiniteLoopException e) {
			ConsoleService.getInstance().writeWarning(e.getMessage());
			return input;
		}
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



/**Customized macrotable provider.
 * @author Xihui Chen
 *
 */
class WidgetMacroTableProvider implements IMacroTableProvider{
	private AbstractWidgetModel widgetModel;
	private Map<String, String> macroMap;
	public WidgetMacroTableProvider(AbstractWidgetModel widgetModel) {
		this.widgetModel = widgetModel;
		macroMap = OPIBuilderMacroUtil.getWidgetMacroMap(widgetModel);
	}
	
	public String getMacroValue(String macroName) {
		if(macroMap != null && macroMap.containsKey(macroName))
			return macroMap.get(macroName);
		else if(widgetModel.getAllPropertyIDs().contains(macroName)){
			Object propertyValue = widgetModel.getPropertyValue(macroName);
			if(propertyValue != null)
				return propertyValue.toString();
		}
		return null;
	}
}