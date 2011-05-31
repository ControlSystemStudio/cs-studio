/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.speech;

import java.util.Locale;

import javax.speech.EngineCreate;
import javax.speech.EngineList;
import javax.speech.EngineModeDesc;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.jsapi.FreeTTSEngineCentral;

/** Implementation of the Annunciator based on FreeTTS but using JSAPI
 *  as much as possible
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class FreeTTS_JSAPI_Annunciator extends BaseAnnunciator
{
    final private static boolean debug = false;
    final private Synthesizer synthesizer;

    public FreeTTS_JSAPI_Annunciator() throws Exception
    {
        FreeTTSHacks.perform();

        // Start the synthesizer
        synthesizer = createSynthesizer();
        synthesizer.allocate();
        synthesizer.resume();

        // List/set voice
        if (debug)
            listTTSVoices();
        // "kevin" - default
        // "kevin16" - sounds clearer
        // "alan" -falls back to "kevin" ?
        final Voice voice = new Voice(
                "kevin16", Voice.AGE_DONT_CARE,
                 Voice.GENDER_DONT_CARE, null);
        synthesizer.getSynthesizerProperties().setVoice(voice);

        if (debug)
            showVoice();
    }

    /** Print list of TTS voices */
    private void listTTSVoices()
    {
        // TTS API, not JSAPI
        System.out.println("Available FreeTTS Voices:");
        final VoiceManager voiceManager = VoiceManager.getInstance();
        final com.sun.speech.freetts.Voice[] voices = voiceManager.getVoices();
        for (int i = 0; i < voices.length; i++)
        {
            System.out.println(voices[i].getName() +
                    ": " + voices[i].getGender() + ", " + voices[i].getAge());
        }
    }

    /** Print info about currently active voice */
    private void showVoice()
    {
        final Voice voice = synthesizer.getSynthesizerProperties().getVoice();
        System.out.print("Voice: " + voice.getName());
        switch (voice.getGender())
        {
        case Voice.GENDER_DONT_CARE:
            System.out.print(", no gender, ");
            break;
        case Voice.GENDER_NEUTRAL:
            System.out.print(", neutral, ");
            break;
        case Voice.GENDER_FEMALE:
            System.out.print(", female, ");
            break;
        case Voice.GENDER_MALE:
            System.out.print(", male, ");
            break;
        }
        switch (voice.getAge())
        {
        case Voice.AGE_DONT_CARE:
            System.out.println("ageless");
            break;
        case Voice.AGE_NEUTRAL:
            System.out.println("neutral");
            break;
        case Voice.AGE_CHILD:
            System.out.println("child");
            break;
        case Voice.AGE_TEENAGER:
            System.out.println("teenager");
            break;
        case Voice.AGE_YOUNGER_ADULT:
            System.out.println("youngster");
            break;
        case Voice.AGE_MIDDLE_ADULT:
            System.out.println("adult");
            break;
        case Voice.AGE_OLDER_ADULT:
            System.out.println("old fart");
            break;
        }
    }

    /** Create the Synthesizer
     *  @return Synthesizer
     *  @throws Exception on error
     */
    private Synthesizer createSynthesizer() throws Exception
    {
        // (Vague) Description of desired Synthesizer
        // For FreeTTS, use 'general' or 'time', but the latter only works
        // for annunciating time?
        final SynthesizerModeDesc desc = new SynthesizerModeDesc(
                null, "general", Locale.US, null, null);

        // JSAPI way, requires the "speech.properties" file
        // synthesizer = Central.createSynthesizer(modeDesc);

        // FreeTTS way that avoids "speech.properties" file
        final FreeTTSEngineCentral central = new FreeTTSEngineCentral();
        final EngineList list = central.createEngineList(desc);
        if (list.size() <= 0)
            throw new Exception("No speech engine available");

        final EngineCreate creator = (EngineCreate) list.get(0);
        final Synthesizer synthesizer = (Synthesizer) creator.createEngine();
        if (synthesizer == null)
            throw new Exception("Cannot create Synthesizer. Need \"speech.properties\"?");

        if (debug)
        {   // Dump description of actual Synthesizer
            EngineModeDesc description = synthesizer.getEngineModeDesc();
            System.out.println("Synthesizer: " + description.getEngineName() + ", " +
                               description.getModeName() + ", " + description.getLocale());
        }
        return synthesizer;
    }

    /** {@inheritDoc}
     *  @throws Exception on error
     */
    @Override
    public void say(final String text) throws Exception
    {
        synthesizer.speakPlainText(applyTranslations(text), null);
        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
    }

    /** {@inheritDoc} */
    @Override
    public void close()
    {
        try
        {
            synthesizer.deallocate();
        }
        catch (Throwable ex)
        {
            System.out.println("Error while closing synthesizer:");
            ex.printStackTrace();
        }
    }
}
