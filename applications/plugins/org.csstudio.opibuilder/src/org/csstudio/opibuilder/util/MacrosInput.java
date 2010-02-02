package org.csstudio.opibuilder.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.csstudio.opibuilder.properties.MacrosProperty;
import org.csstudio.platform.util.StringUtil;

/**The value type definition for {@link MacrosProperty}, which describes the input
 * for a Macros Property.
 * @author Xihui Chen
 *
 */
public class MacrosInput {

	private Map<String, String> macrosMap;
	
	private boolean include_parent_macros;	
	
	private static final char ITEM_SEPARATOR = ','; //$NON-NLS-1$
	private static final char MACRO_SEPARATOR = '='; //$NON-NLS-1$
	private static final char QUOTE = '\"'; //$NON-NLS-1$
	
	public MacrosInput(Map<String, String> macros, boolean include_parent_macros) {
		this.macrosMap = macros;
		this.include_parent_macros = include_parent_macros;
	}

	/**
	 * @return the macrosMap
	 */
	public final Map<String, String> getMacrosMap() {
		return macrosMap;
	}

	/**
	 * @param macrosMap the macrosMap to set
	 */
	public final void setMacrosMap(Map<String, String> macrosMap) {
		this.macrosMap = macrosMap;
	}

	/**
	 * @return the include_parent_macros
	 */
	public final boolean isInclude_parent_macros() {
		return include_parent_macros;
	}

	/**
	 * @param include_parent_macros the include_parent_macros to set
	 */
	public final void setInclude_parent_macros(boolean include_parent_macros) {
		this.include_parent_macros = include_parent_macros;
	}
	
	public MacrosInput getCopy(){
		MacrosInput result = new MacrosInput(
				new HashMap<String, String>(), include_parent_macros);
		result.getMacrosMap().putAll(macrosMap);
		return result;		
	}
	
	@Override
	public String toString() {
		return (include_parent_macros? "{" + "Parent Macros" + //$NON-NLS-1$ 
				"} " : "") + macrosMap.toString();		
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof MacrosInput){
			MacrosInput input = (MacrosInput)obj;
			if(input.isInclude_parent_macros() == include_parent_macros &&
					macrosMap.equals(input.getMacrosMap()))
				return true;
			else 
				return false;
		}else
			return false;
		
	}
	
	
	/**
	 * @return a String with format like this: "true", "macro1 = hello", "macro2 = hello2"
	 */
	public String toPersistenceString(){
		StringBuilder result = new StringBuilder();
		result.append(QUOTE + Boolean.toString(include_parent_macros) + QUOTE);
		for(String key : macrosMap.keySet()){
			result.append(ITEM_SEPARATOR + "" + QUOTE + key + //$NON-NLS-1$
					MACRO_SEPARATOR + macrosMap.get(key) + QUOTE);
		}
		return result.toString();
	}
	
	/**Parse MacrosInput from persistence string.
	 * @param s
	 * @return
	 * @throws Exception
	 */
	public static MacrosInput recoverFromString(String s) throws Exception{
		String[] items = StringUtil.splitIgnoreInQuotes(s, ITEM_SEPARATOR, true);	
		MacrosInput macrosInput = new MacrosInput(new LinkedHashMap<String, String>(), true);
		for(int i= 0; i<items.length; i++){
			if(i == 0)
				macrosInput.setInclude_parent_macros(Boolean.valueOf(items[i]));
			else{
				String[] macro = StringUtil.splitIgnoreInQuotes(items[i], MACRO_SEPARATOR, true);
				if(macro.length == 2)
					macrosInput.getMacrosMap().put(macro[0], macro[1]);
			}				
		}
		return macrosInput;
	}
	
}
