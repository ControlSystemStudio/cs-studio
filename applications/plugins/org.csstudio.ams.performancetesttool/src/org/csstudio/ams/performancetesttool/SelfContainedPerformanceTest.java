package org.csstudio.ams.performancetesttool;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executors;

import org.csstudio.nams.application.department.decision.office.decision.AlarmEntscheidungsBuero;
import org.csstudio.nams.common.DefaultExecutionService;
import org.csstudio.nams.common.decision.Eingangskorb;
import org.csstudio.nams.common.decision.EingangskorbBeobachter;
import org.csstudio.nams.common.decision.ExecutorBeobachtbarerEingangskorb;
import org.csstudio.nams.common.decision.StandardAblagekorb;
import org.csstudio.nams.common.decision.Vorgangsmappe;
import org.csstudio.nams.common.decision.Vorgangsmappenkennung;
import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.material.regelwerk.Regelwerk;
import org.csstudio.nams.common.material.regelwerk.StandardRegelwerk;
import org.csstudio.nams.common.material.regelwerk.StringRegel;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class SelfContainedPerformanceTest {

	
	
	
	public static void main(String[] args) {
		SelfContainedCommandLineArgs arguments = new SelfContainedCommandLineArgs();
		new JCommander(arguments, args);
        try {
            new SelfContainedPerformanceTest().run(arguments);
        } catch (Exception e) {
            System.err.println("An error occured, the test was aborted");
            System.err.println(e.getMessage());
        }

	}

	private int messageCounter;
	
	public void run(SelfContainedCommandLineArgs arguments) throws UnknownHostException, InterruptedException {
		messageCounter = 0;
		Regelwerk regelwerk = new StandardRegelwerk(Regelwerkskennung.valueOf("Regel1"), new StringRegel(StringRegelOperator.OPERATOR_TEXT_EQUAL, MessageKeyEnum.NAME, "TEST"));
		Eingangskorb<Vorgangsmappe> alarmVorgangEingangskorb = new StandardAblagekorb<Vorgangsmappe>();
		final ExecutorBeobachtbarerEingangskorb<Vorgangsmappe> alarmVorgangAusgangskorb = new ExecutorBeobachtbarerEingangskorb<Vorgangsmappe>(Executors.newFixedThreadPool(1));
		
		alarmVorgangAusgangskorb.setBeobachter(new EingangskorbBeobachter() {
			
			@Override
			public void neuerEingang() {
				try {
					messageCounter += 1;
					System.out.println("hallo "+alarmVorgangAusgangskorb.entnehmeAeltestenEingang());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		AlarmEntscheidungsBuero alarmEntscheidungsBuero = new AlarmEntscheidungsBuero(new DefaultExecutionService(), 
																						new Regelwerk[]{ regelwerk }, 
																						alarmVorgangEingangskorb, 
																						alarmVorgangAusgangskorb, 
																						arguments.threads);
		Vorgangsmappe[] vorgangsmappen = erzeugeVorgangsmappen(arguments.count);
		for (Vorgangsmappe vorgangsmappe : vorgangsmappen) {
			alarmVorgangEingangskorb.ablegen(vorgangsmappe);
		}
		while(messageCounter<arguments.count) {
			Thread.sleep(500);
		}
		alarmEntscheidungsBuero.beendeArbeitUndSendeSofortAlleOffeneneVorgaenge();
	}
	
	private Vorgangsmappe[] erzeugeVorgangsmappen(int anzahlAnMappen) throws UnknownHostException {
		Vorgangsmappe[] result = new Vorgangsmappe[anzahlAnMappen];
		for(int index = 0; index < anzahlAnMappen; index++) {
			HashMap<MessageKeyEnum, String> mapMessage = new HashMap<MessageKeyEnum, String>(1);
			mapMessage.put(MessageKeyEnum.NAME, "TEST");
			result[index] = new Vorgangsmappe(Vorgangsmappenkennung.createNew(InetAddress.getLocalHost(), new Date()), new AlarmNachricht(mapMessage));
		}
		return result;
	}
	
	private static class SelfContainedCommandLineArgs {
		
	    @Parameter(names = "-threads", description = "Number of threads to process alarm filters")
	    public int threads = 1;
	    
	    @Parameter(names = "-count", description = "Number of messages to send")
	    public int count = 1;
	    
	    @Parameter(names = "-rate", description = "Limit the send rate of messages to less than this number of messages per second. (0..1000, 0 = unlimited)")
	    public int rate = 0;

	}
	
}
