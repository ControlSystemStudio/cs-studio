package org.csstudio.opibuilder.util;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.opibuilder.properties.MacrosProperty;

/**The value type definition for {@link MacrosProperty}, which describes the input
 * for a Macros Property.
 * @author Xihui Chen
 *
 */
public class MacrosInput {

	private Map<String, String> macrosMap;
	
	private boolean include_parent_macros;	
	
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
	
	
}
