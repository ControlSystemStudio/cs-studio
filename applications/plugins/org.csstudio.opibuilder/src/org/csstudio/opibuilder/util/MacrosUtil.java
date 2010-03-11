package org.csstudio.opibuilder.util;

import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.preferences.PreferencesHelper;

class WidgetMacroTableProvider implements IMacroTableProvider{
	private AbstractWidgetModel widgetModel;
	private Map<String, String> macroMap;
	public WidgetMacroTableProvider(AbstractWidgetModel widgetModel) {
		this.widgetModel = widgetModel;
		macroMap = MacrosUtil.getWidgetMacroMap(widgetModel);
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

/**The utility functions for macros operations.
 * @author Xihui Chen
 *
 */
public class MacrosUtil {

	
	private static final String MACRO_RIGHT_PART = "[)}]"; //$NON-NLS-1$

	private static final String MACRO_LEFT_PART = "\\$[{(]"; //$NON-NLS-1$
	

	/**Replace the macros in the input with the real value.  Simply calls the three argument version below
	 * @param input the raw string which include the macros string $(macro)
	 * @return the string in which the macros have been replaced with the real value.
	 */
	public static String replaceMacros(AbstractWidgetModel widgetModel, String input){
		
		try {
			return MacrosUtil.replaceMacros(input, new WidgetMacroTableProvider(widgetModel));
		} catch (InfiniteLoopException e) {
			ConsoleService.getInstance().writeWarning(e.getMessage());
			return input;
		}
	}
	
	
	/**Replace macros in String.
	 * @param input the input string to be parsed
	 * @param macroTableProvider the macro table provider
	 * @return
	 * @throws InfiniteLoopException when infinite loop is detected. For example, for a macro table
	 * "a=$(b), b=$(a)", this string "$(a)" will result in infinite loop.
	 */
	public static String replaceMacros(String input, IMacroTableProvider macroTableProvider) throws InfiniteLoopException {
		StringBuilder stringBuilder = new StringBuilder();
		Stack<Integer> stack = new Stack<Integer>();
		boolean lockStack = false; //lock the stack to prevent pushing new element
		int scanPosition = 0;
		for(int i=0; i<input.length(); i++){
			if(!lockStack){
				if(input.charAt(i) == '$' && i<input.length()-1
						&& (input.charAt(i+1) == '(' || input.charAt(i+1)=='{')){
					stack.push(i);
					continue;
				}
			}
			if(stack.size() > 0 && (input.charAt(i) == ')' || input.charAt(i)=='}')){
				try {
					lockStack = true; //lock the stack until it is poped out.
					int start = stack.pop();
					if(stack.size() == 0){  //arrived the most out, we got a macro
						String macro = input.substring(start, i+1);						
						String macroValue = parseMacro(macro, macroTableProvider);
						stringBuilder.append(input.substring(scanPosition, start) + macroValue);
						scanPosition = i+1;
						lockStack = false;
					}
				} catch (EmptyStackException e) {
					lockStack =false;
				}
				
			}								
		}
		//if there is more chars behind the last macro
		if(scanPosition < input.length() -1)
			stringBuilder.append(input.substring(scanPosition));		
		 
		return stringBuilder.toString();
	}
	
	public static String parseMacro(String input, IMacroTableProvider macroTableProvider) throws InfiniteLoopException{
		return parseMacro(input, macroTableProvider, new HashSet<String>());
	}
	
	/**Parse a macro unit(${...}) and replace the macro with value from provider. 
	 * It supports recursive macros, for example ${${...}}.
	 * @param input the input macro unit which has a format like ${...} or $(...)
	 * @param macroTableProvider the macro table provider
	 * @param parsedMacros the parsed macros history in the recursive stack.
	 * @return the result of parsing.
	 * @throws InfiniteLoopException 
	 */
	private static String parseMacro(String input, IMacroTableProvider macroTableProvider, 
			Set<String> parsedMacros) throws InfiniteLoopException{
		//if there is no macro in the input, return
		if(!input.matches(MACRO_LEFT_PART + ".+" + MACRO_RIGHT_PART))
			return input;
		String result = input;
		
		int innerStart = -1;
		for(int i=0; i<input.length(); i++){
			
				if(input.charAt(i) == '$' && i<input.length()-1
						&& (input.charAt(i+1) == '(' || input.charAt(i+1)=='{')){
					innerStart=i;
					continue;
				}
			
			if(input.charAt(i) == ')' || input.charAt(i)=='}'){
					if(innerStart == -1)
						return result;									
					String macroName = input.substring(innerStart+2, i);
					//if it has been parsed before, stop parse to prevent infinite loop
					if(!parsedMacros.add(macroName)){
						throw new InfiniteLoopException(
								"Infinite loop was detected when parsing the macro: " + macroName);
					}	
					String macroValue = macroTableProvider.getMacroValue(macroName);
					if(macroValue == null)
						return result;
					else
						result = input.substring(0, innerStart) + macroValue + input.substring(i+1);
						return parseMacro(result, macroTableProvider, parsedMacros);				
				
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
