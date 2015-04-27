/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.speech;

/** Hacks required to run FreeTTS
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
class FreeTTSHacks
{
    /** Apply property settings for FreeTTS */
    public static void perform()
    {
        // While this is not GUI code, the FreeTTS library uses AWT for its timing.
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

        // If audio line is not available, try again after short delay.
        // Keep the delay short.
        // If too long, it can miss a short break in the output of
        // another program where the audio line would have become available
        System.setProperty("com.sun.speech.freetts.audio.AudioPlayer.openFailDelayMs",
			"100");
        // .. and keep trying for a total of 30 seconds
        System.setProperty("com.sun.speech.freetts.audio.AudioPlayer.totalOpenFailDelayMs",
        	"30000");

        // In extreme cases, debug info may help
        // This shows that we use the 'JavaStreamingAudioPlayer'
        //        System.setProperty("com.sun.speech.freetts.audio.AudioPlayer.debug",
        //    		"true");

        // Setting this property eliminates the need for voices.txt
        System.setProperty("freetts.voices",
                "com.sun.speech.freetts.en.us.cmu_time_awb.AlanVoiceDirectory," +
                "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
    }
}
