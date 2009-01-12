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
	
	   /** Set volume
     *  @param volume Volume level 0...1
     */
    public void setVolume(final float volume);
	
	/** Speak some text
	 *  @param something Text to speak
	 */
	public void say(final String something);

	/** Must be called when Annunciator no longer needed to release resources */
	public void close();
}