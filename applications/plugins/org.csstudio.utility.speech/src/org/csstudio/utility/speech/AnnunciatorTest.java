/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.speech;

import org.junit.Test;

/** Unit-test of the Annunciator
 *  @author Katia Danilova
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AnnunciatorTest
{
    final private Translation translations[] = new Translation[]
   	{
			
   			new Translation("MEBT", ",mebbit"),
   			new Translation("HEBT", ",hebbit"),
   			new Translation("LEBT", ",lebbit"),
   			new Translation("Vac", ",vacuum"),
   			new Translation("Diag", ",diagnostics"),
   			new Translation("RFQ", "R F Q"),
   			new Translation("Tgt","target"),
   			new Translation("Util","utility"),
   			new Translation("Ctl","control"),
   			new Translation("Lin","linac"),
   			new Translation("Dplate","dee plate"),
   			new Translation("DTL","D T L"),
   			new Translation("SCL","S C L"),
   			new Translation("CCL","C C L"),
   			new Translation("_"," "),
   			new Translation(":"," "),
			
   	};

	@Test
	public void test() throws Exception
	{		
        final Annunciator talker;

        if (false)
        {
            // Create FreeTTSAnnunciator, list voices
    		final FreeTTSAnnunciator tts = new FreeTTSAnnunciator("kevin16");
    		final String[] voices = FreeTTSAnnunciator.getVoiceNames();
    		for (String voice : voices)
            {
    		    System.out.println("Voice '" + voice + "'");
            }
    		talker = tts;
        }
        else
        {
            // Create FreeTTS_JSAPI_Annunciator
            talker = new FreeTTS_JSAPI_Annunciator();
        }
		// From now on, only use the Annunciator interface
        talker.say("SNS DTL MEBT Vac Diag RFQ Tgt Util Ctl PPS MPS Lin");
		talker.setTranslations(translations);
		talker.say("SNS DTL MEBT Vac Diag RFQ Tgt Util Ctl PPS MPS Lin");

		talker.close();
	}
	
	
}
