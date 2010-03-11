package org.csstudio.apputil.macros;
/**Provides value for a macro.
 * @author Xihui Chen
 *
 */
public interface IMacroTableProvider{
	/**Get value of a macro.
	 * @param macroName the name of the macro
	 * @return the value of the macro, null if no such macro exists.
	 */
	public String getMacroValue(String macroName);
}