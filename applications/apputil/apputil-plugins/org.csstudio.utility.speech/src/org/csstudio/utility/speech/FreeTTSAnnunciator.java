/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.speech;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

/** Implementation of the Annunciator based on FreeTTS, using FreeTTS API
 *  @author Katia Danilova
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class FreeTTSAnnunciator extends BaseAnnunciator
{
	final public static String DEFAULT_VOICE = "kevin16";

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
        FreeTTSHacks.perform();

    	// The VoiceManager manages all the voices for FreeTTS.
    	final VoiceManager voiceManager = VoiceManager.getInstance();
        voice = voiceManager.getVoice(voice_name);
        if (voice == null)
        	throw new Exception("Cannot find a voice named " + voice_name);
        // voice.setVerbose(true);

        // Allocate resources for the voice.
        voice.allocate();
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

    /** {@inheritDoc} */
    @Override
    public void say(final String something)
    {
    	voice.speak(applyTranslations(something));
    }

    /** {@inheritDoc} */
    @Override
    public void close()
    {
        voice.deallocate();
    }
}
