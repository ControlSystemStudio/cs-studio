package org.csstudio.utility.speech;

/** Translate some part of a string
 *  @author ek5
 */
class Translation
{
	private final String pattern;
	private final String replacement;
	
	Translation(String pattern, String replacement)
	{
		this.pattern = pattern;
		this.replacement = replacement;
	}
	
	public String apply(final String input)
	{
		return input.replaceAll(pattern, replacement);
	}
}