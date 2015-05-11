/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.speech;

/** Interface to an annunciator
 *  @author Katia Danilova
 *  @author Kay Kasemir
 */
public interface Annunciator
{
	/** Define a list of translations to improve the sound
	 *  of certain acronyms.
	 *  @param translations
	 */
	public void setTranslations(Translation[] translations);
	
	/** Speak some text
	 *  @param something Text to speak
	 *  @throws Exception on error
	 */
	public void say(final String something) throws Exception;

	/** Must be called when Annunciator no longer needed to release resources */
	public void close();
}
