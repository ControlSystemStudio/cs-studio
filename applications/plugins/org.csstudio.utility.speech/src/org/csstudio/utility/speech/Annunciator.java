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
	public abstract void setTranslations(Translation[] translations);
	
	/** Speak some text
	 *  @param something Text to speak
	 */
	public abstract void say(final String something);

	/** Must be called when Annunciator no longer needed to release resources */
	public abstract void close();
}