package org.csstudio.utility.speech;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

/** Implementation of the Annunciator based on FreeTTS
 *  @author Katia Danilova
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
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
	    // While this is not GUI code, the FreeTTS library somehow uses AWT.
	    // In Eclipse GUI code, that activates the SWT/AWT bridge.
	    // Under OS X, the SWT/AWT bridge is broken.
	    // Worked in 10.4 with updated JVM, but in 10.5 any speech code
	    // invoked from GUI programs would lock the whole application up.
	    // Setting
	    //     -Djava.awt.headless=true
	    // or the equivalent in here seems to avoid the hangup
	    // (from http://www.digitalsanctuary.com/tech-blog/java/eclipse-startonfirstthread-error-and-fix.html )
	    
	    // Look for something like "Mac OS X"
	    final String os = System.getProperty("os.name");
	    if (os.contains("Mac"))
	        System.setProperty("java.awt.headless", "true");
	    
		// Setting this property eliminates the need for voices.txt
		System.setProperty("freetts.voices",
				"com.sun.speech.freetts.en.us.cmu_time_awb.AlanVoiceDirectory," +
				"com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
		
    	// The VoiceManager manages all the voices for FreeTTS.
    	final VoiceManager voiceManager = VoiceManager.getInstance();
        voice = voiceManager.getVoice(voice_name);
        if (voice == null)
        	throw new Exception("Cannot find a voice named " + voice_name);
        // voice.setVerbose(true);

        // Allocate resources for the voice.
        voice.allocate();
    }
	
	/** Set volume
	 *  @param volume Volume level 0...1
	 */
	public void setVolume(final float volume)
	{
	    voice.setVolume(volume);
	}
	
	/** Define translations */
	public void setTranslations(final Translation[] translations)
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