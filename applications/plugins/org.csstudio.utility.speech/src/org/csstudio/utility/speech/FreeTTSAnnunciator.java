package org.csstudio.utility.speech;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

/** Implementation of the Annunciator based on FreeTTS
 *  @author Katia Danilova
 *  @author Kay Kasemir
 */
public class FreeTTSAnnunciator implements Annunciator
{
	final public static String DEFAULT_VOICE = "kevin16";

	private Translation translations[] = null;

	final private Voice voice;

	/** Construct annunciator with default voice
	 *  @throws Exception on error
	 */
	public FreeTTSAnnunciator() throws Exception
	{
		this(DEFAULT_VOICE);
	}
	
	/** Construct annunciator with certain voice
	 *  @param voice_name Name of the voice
	 *  @throws Exception on error
	 *  @see #getVoiceNames()
	 */
	public FreeTTSAnnunciator(final String voice_name) throws Exception
	{
    	// The VoiceManager manages all the voices for FreeTTS.
    	final VoiceManager voiceManager = VoiceManager.getInstance();
        voice = voiceManager.getVoice(voice_name);
        if (voice == null)
        	throw new Exception("Cannot find a voice named " + voice_name);

        // Allocate resources for the voice.
        voice.allocate();
	}
	
	public void setTranslations(Translation[] translations)
	{
		this.translations = translations;
	}
	
	/** @return Array of voice names */
    public static String[] getVoiceNames()
    {
    	final VoiceManager voiceManager = VoiceManager.getInstance();
    	final Voice[] voices = voiceManager.getVoices();
    	final String names[] = new String[voices.length];
        for (int i = 0; i < voices.length; i++)
        	names[i] = voices[i].getName();
        return names;
    }

    /* (non-Javadoc)
	 * @see org.csstudio.utility.speech.Annunciator#say(java.lang.String)
	 */
    public void say(final String something)
    {
    	voice.speak(applyTranslations(something));
    }

    /** Apply all translations to input */
    private String applyTranslations(String something)
    {
    	if (translations == null)
    		return something;
    	for (Translation translation : translations)
    		something = translation.apply(something);
    	return something;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.utility.speech.Annunciator#close()
	 */
    public void close()
    {
        voice.deallocate();
    }
}