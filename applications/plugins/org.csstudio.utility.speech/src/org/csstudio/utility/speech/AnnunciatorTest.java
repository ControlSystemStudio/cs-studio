package org.csstudio.utility.speech;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
		final Annunciator talker = new FreeTTSAnnunciator();
		talker.say("SNS DTL MEBT Vac Diag RFQ Tgt Util Ctl PPS MPS Lin");
		talker.setTranslations(translations);
		talker.say("SNS DTL MEBT Vac Diag RFQ Tgt Util Ctl PPS MPS Lin");

		talker.close();
	}
	
	
}