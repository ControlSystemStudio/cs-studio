package org.csstudio.ams.performancetesttool;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.csstudio.nams.application.department.decision.office.decision.AlarmEntscheidungsBuero;
import org.csstudio.nams.common.DefaultExecutionService;
import org.csstudio.nams.common.decision.Eingangskorb;
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
import org.csstudio.nams.common.material.regelwerk.UndVersandRegel;
import org.csstudio.nams.common.material.regelwerk.VersandRegel;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class SelfContainedPerformanceTest {

	private volatile long[] erzeugungsZeiten;
	private long[] empfangsZeiten;
	private final SelfContainedCommandLineArgs arguments;
	
	public SelfContainedPerformanceTest(final SelfContainedCommandLineArgs arguments) {
		this.arguments = arguments;
		erzeugungsZeiten = new long[arguments.messageCount];
		empfangsZeiten = new long[arguments.messageCount];
	}
	
	public void run() throws UnknownHostException, InterruptedException {
		int messageCounter = 0;
		final Eingangskorb<Vorgangsmappe> alarmVorgangEingangskorb = new StandardAblagekorb<Vorgangsmappe>();
		final StandardAblagekorb<Vorgangsmappe> alarmVorgangAusgangskorb = new StandardAblagekorb<Vorgangsmappe>();
		
		AlarmEntscheidungsBuero alarmEntscheidungsBuero = new AlarmEntscheidungsBuero(new DefaultExecutionService(), 
																erzeugeRegelwerke(arguments.ruleCount), 
																alarmVorgangEingangskorb, 
																alarmVorgangAusgangskorb, 
																arguments.threads);
		
		final Vorgangsmappe[] vorgangsmappen = erzeugeVorgangsmappen(arguments.messageCount);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int index = 0; index < vorgangsmappen.length; index++) {
					try {
						Vorgangsmappe vorgangsmappe = vorgangsmappen[index];
						erzeugungsZeiten[index] = System.currentTimeMillis();
						alarmVorgangEingangskorb.ablegen(vorgangsmappe);
						if (arguments.rate > 0) {
							Thread.sleep(1000 / arguments.rate);
						}
						else {
							Thread.yield();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		int percentCounter = 1;
		while(messageCounter<arguments.messageCount) {
			Vorgangsmappe vorgangsmappe = alarmVorgangAusgangskorb.entnehmeAeltestenEingang();
			WeiteresVersandVorgehen gesamtErgebnis = vorgangsmappe.gibPruefliste().gesamtErgebnis();
			if (gesamtErgebnis.equals(WeiteresVersandVorgehen.VERSENDEN)) {
				empfangsZeiten[messageCounter] = System.currentTimeMillis();
				messageCounter += 1;
				if (messageCounter%(arguments.messageCount/100) == 0) {
					System.out.println(percentCounter + "% bearbeitet");
					percentCounter += 1;
				}
			}
		}
		
		long latencySum = 0;
		for (int index = 0; index < empfangsZeiten.length; index++) {
			long latency = empfangsZeiten[index] - erzeugungsZeiten[index];
			System.out.println(latency);
			latencySum += latency;
		}
		System.out.println("Durchschnittliche Latenz: "+latencySum/empfangsZeiten.length);
		System.out.println("Durchschnittliche Nachrichten / Sekunde: "+1000.0/(1.0*(empfangsZeiten[empfangsZeiten.length-1]-erzeugungsZeiten[0])/empfangsZeiten.length));
		alarmEntscheidungsBuero.beendeArbeitUndSendeSofortAlleOffeneneVorgaenge();
		System.exit(0);
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
	
	private Regelwerk[] erzeugeRegelwerke(int anzahlAnRegelwerken) {
		assert anzahlAnRegelwerken >= 1;
		Regelwerk[] result = new Regelwerk[anzahlAnRegelwerken];
		for(int index = 0; index < anzahlAnRegelwerken; index++) {
			StringRegel stringRegel1 = new StringRegel(StringRegelOperator.OPERATOR_TEXT_EQUAL, MessageKeyEnum.SEVERITY, "Sehr hoch");
			StringRegel stringRegel2 = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_GT, MessageKeyEnum.EVENTTIME, "" + System.currentTimeMillis());
			VersandRegel undRegel = new UndVersandRegel(new VersandRegel[] { stringRegel1, stringRegel2 });
			result[index] = new StandardRegelwerk(Regelwerkskennung.valueOf("Regel"+index), undRegel);
		}
		result[anzahlAnRegelwerken-1] = new StandardRegelwerk(Regelwerkskennung.valueOf("Regel0"), new StringRegel(StringRegelOperator.OPERATOR_TEXT_EQUAL, MessageKeyEnum.NAME, "TEST"));
		return result;
	}
	
	public static void main(String[] args) {
		SelfContainedCommandLineArgs arguments = new SelfContainedCommandLineArgs();
		new JCommander(arguments, args);
        try {
            new SelfContainedPerformanceTest(arguments).run();
        } catch (Exception e) {
            System.err.println("An error occured, the test was aborted");
            System.err.println(e.getMessage());
        }
	}
	
	private static class SelfContainedCommandLineArgs {
		
	    @Parameter(names = { "--threads","-t" }, description = "Number of threads to process alarm filters")
	    public int threads = 1;
	    
	    @Parameter(names = { "--messagecount","-mc" }, description = "Number of messages to send")
	    public int messageCount = 1;
	    
	    @Parameter(names = { "--rate","-r" }, description = "Limit the send rate of messages to less than this number of messages per second. (0..1000, 0 = unlimited)")
	    public int rate = 0;
	    
	    @Parameter(names = { "--rulecount","-rc" }, description = "Number of filter rules to be used by filter manager. (>=1)")
	    public int ruleCount = 1;
	}
	
}
