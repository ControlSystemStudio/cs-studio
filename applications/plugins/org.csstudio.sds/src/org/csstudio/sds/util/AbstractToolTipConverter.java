package org.csstudio.sds.util;

/**
 * An abstract class to convert a parameterized String.
 * @author Kai Meyer
 *
 */
public abstract class AbstractToolTipConverter {
	
	/**
	 * The sign, which is before a parameter.
	 */
	public static final String START_SEPARATOR = "${";
	/**
	 * The sign, which is after a parameter.
	 */
	public static final String END_SEPARATOR = "}$";
	
	/**
	 * Replaces all parameters (encapsulated by '${' and '}$').
	 * @param toolTip The tooltip to convert
	 * @return The converted tooltip
	 */
	public final String convertToolTip(final String toolTip) {
		if (toolTip.contains(START_SEPARATOR) && toolTip.contains(END_SEPARATOR)) {
			int end = 0;
			int start = 0;
			StringBuffer buffer = new StringBuffer();
			while (start<toolTip.length() && end<toolTip.length()-2 && toolTip.indexOf(START_SEPARATOR, end)>-1) {
				start = toolTip.indexOf(START_SEPARATOR, end)+2;
				buffer.append(toolTip.substring(end, start));
				end = toolTip.indexOf(END_SEPARATOR, start);
				if (end > -1 && start > -1 && start<end) {
					String parameter = toolTip.substring(start, end);
					buffer.append(this.getReplacementForParameter(parameter));
				} else {
					break;
				}
			}
			if (end>-1 && end <toolTip.length()) {
				buffer.append(toolTip.substring(end));
			}
			return buffer.toString();
		}
		return toolTip;
	}
	
	/**
	 * Returns the replacement for a parameter.
	 * @param parameter The parameter to replace
	 * @return The replacement
	 */
	protected abstract String getReplacementForParameter(final String parameter);

}
