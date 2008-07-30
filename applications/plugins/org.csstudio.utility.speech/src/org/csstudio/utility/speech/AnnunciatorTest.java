package org.csstudio.utility.speech;

import org.junit.Test;

/** Unit-test of the Annunciator
 *  @author Katia Danilova
 *  @author Kay Kasemir
 */
public class AnnunciatorTest
{
	final private Translation translations[] = new Translation[]
   	{
   			new Translation("MEBT", ",mebbit"),
   			new Translation("Vac", ",vacuum"),
   	};

	@Test
	public void test() throws Exception
	{		
		final Annunciator talker = new FreeTTSAnnunciator();
		talker.say("SNS DTL MEBT Vac");
		talker.setTranslations(translations);
		talker.say("SNS DTL MEBT Vac");
//		talker.say("DTL_HPRF:Mod1:Fire");
//        talker.say("MEBT_Vac:PS1:P_Clc");
		talker.close();
	}
}