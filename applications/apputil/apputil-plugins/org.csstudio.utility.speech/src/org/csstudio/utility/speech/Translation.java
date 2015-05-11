/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.speech;

/** Translate some part of a string
 *  @author Katia Danilova
 *  @author Kay Kasemir
 */
public class Translation
{
	private final String pattern;
	private final String replacement;

	/** Create a Translation
	 *  @param pattern <i>Regular Expression</i> pattern to be translated
	 *  @param replacement Replacement text
	 */
	public Translation(final String pattern, final String replacement)
	{
		this.pattern = pattern;
		this.replacement = replacement;
	}
	
	/** Apply this translation to the input text
	 *  @param input Input to translate
	 *  @return Translated text
	 */
	public String apply(final String input)
	{
		return input.replaceAll(pattern, replacement);
	}
}
