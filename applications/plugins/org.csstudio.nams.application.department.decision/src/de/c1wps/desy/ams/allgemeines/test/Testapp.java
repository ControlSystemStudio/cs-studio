package de.c1wps.desy.ams.allgemeines.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.regelwerk.OderVersandRegel;
import org.csstudio.nams.common.material.regelwerk.Regelwerk;
import org.csstudio.nams.common.material.regelwerk.Regelwerkskennung;
import org.csstudio.nams.common.material.regelwerk.StandardRegelwerk;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.common.material.regelwerk.StringRegel;
import org.csstudio.nams.common.material.regelwerk.TimeBasedRegel;
import org.csstudio.nams.common.material.regelwerk.UndVersandRegel;
import org.csstudio.nams.common.material.regelwerk.VersandRegel;

import de.c1wps.desy.ams.alarmentscheidungsbuero.AlarmEntscheidungsBuero;
import de.c1wps.desy.ams.alarmentscheidungsbuero.DokumentVerbraucherArbeiter;
import de.c1wps.desy.ams.alarmentscheidungsbuero.DokumentenBearbeiter;
import de.c1wps.desy.ams.allgemeines.Eingangskorb;
import de.c1wps.desy.ams.allgemeines.StandardAblagekorb;
import de.c1wps.desy.ams.allgemeines.Vorgangsmappe;
import de.c1wps.desy.ams.allgemeines.Vorgangsmappenkennung;

public class Testapp implements Runnable, ActionListener {

//	static class SehrSimpleTextRegel implements VersandRegel {
//
//		private final String muster;
//
//		public SehrSimpleTextRegel(String muster) {
//			this.muster = muster;
//		}
//
//		public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
//				AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
//			if (!bisherigesErgebnis.gibErgebnisFuerRegel(this).istEntschieden()) {
//				pruefeNachrichtErstmalig(nachricht, bisherigesErgebnis);
//			}
//		}
//
//		public Millisekunden pruefeNachrichtAufTimeOuts(
//				Pruefliste bisherigesErgebnis,
//				Millisekunden verstricheneZeitSeitErsterPruefung) {
//			return null;
//		}
//
//		public Millisekunden pruefeNachrichtErstmalig(AlarmNachricht nachricht,
//				Pruefliste bisherigesErgebnis) {
//			if (muster.equals(nachricht.gibNachrichtenText())) {
//				bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
//						RegelErgebnis.ZUTREFFEND);
//			} else {
//				bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
//						RegelErgebnis.NICHT_ZUTREFFEND);
//			}
//			return null;
//		}
//
//	}

	private JTextArea textArea;
	private Eingangskorb<Vorgangsmappe> alarmVorgangEingangskorb;

	public static void main(String[] args) {
		new Testapp().run();
	}

	@SuppressWarnings("serial")
	public void run() {

		initAlarmBuero();

		JFrame frame = new JFrame("Das Alarmentscheidungsbüro");

		frame.getContentPane().setLayout(new BorderLayout());

		JTextField textField = new JTextField();
		textField.addActionListener(this);
		frame.getContentPane().add(textField, BorderLayout.NORTH);

		textArea = new JTextArea(12, 96) {
			@Override
			public void setText(String t) {
				super.setText(t);
				setCaretPosition(getText().length() - 1);
			}
		};
		textArea.setEditable(false);
		frame.getContentPane().add(new JScrollPane(textArea),
				BorderLayout.CENTER);

		ImageIcon imageIcon = new ImageIcon("tree.png");
		System.out.println("Testapp.run(): " + imageIcon.toString());
		JLabel dokuLabel = new JLabel(imageIcon) {
			@Override
			public Dimension getMinimumSize() {
				return new Dimension(851,370);
			}
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(851,370);
			}
		};
		frame.getContentPane().add(dokuLabel , BorderLayout.SOUTH);
		
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private void initAlarmBuero() {

		VersandRegel sofort1 = new StringRegel(StringRegelOperator.OPERATOR_TEXT_EQUAL, "","sofort");
		VersandRegel sofort2 = new StringRegel(StringRegelOperator.OPERATOR_TEXT_EQUAL, "","sofort2");
		VersandRegel sofort3 = new StringRegel(StringRegelOperator.OPERATOR_TEXT_EQUAL, "", "*bubu*");
		VersandRegel oder = new OderVersandRegel(new VersandRegel[] { sofort1,
				sofort2, sofort3 });

		VersandRegel aufhebung = new StringRegel(StringRegelOperator.OPERATOR_TEXT_EQUAL, "","stop");
		VersandRegel bestaetigung = new StringRegel(StringRegelOperator.OPERATOR_TEXT_EQUAL, "","feuer");
		VersandRegel ausloeser = new StringRegel(StringRegelOperator.OPERATOR_TEXT_EQUAL, "","*start*");

		VersandRegel timebasedRegel = new TimeBasedRegel(ausloeser, aufhebung,
				bestaetigung, Millisekunden.valueOf(10000));
		
		VersandRegel aufhebung2 = new StringRegel(StringRegelOperator.OPERATOR_TEXT_EQUAL, "","stop2");
		VersandRegel bestaetigung2 = new StringRegel(StringRegelOperator.OPERATOR_TEXT_EQUAL, "","feuer2");
		VersandRegel ausloeser2 = new StringRegel(StringRegelOperator.OPERATOR_TEXT_EQUAL, "","*start*");

		VersandRegel timebasedRegel2 = new TimeBasedRegel(ausloeser2, aufhebung2,
				bestaetigung2, Millisekunden.valueOf(20000));

		Regelwerkskennung regelwerkskennung = Regelwerkskennung
				.valueOf("timebased");
		Regelwerk regelwerk = new StandardRegelwerk(regelwerkskennung,
				timebasedRegel);

		Regelwerkskennung regelwerkskennung2 = Regelwerkskennung
				.valueOf("oder");
		Regelwerk regelwerk2 = new StandardRegelwerk(regelwerkskennung2, oder);
		
		VersandRegel und = new UndVersandRegel(new VersandRegel[] { timebasedRegel, timebasedRegel2 });		
		
		Regelwerkskennung regelwerkskennung3 = Regelwerkskennung
		.valueOf("und");
		Regelwerk regelwerk3 = new StandardRegelwerk(regelwerkskennung3, und);

		AlarmEntscheidungsBuero buero = new AlarmEntscheidungsBuero(
				new Regelwerk[] { regelwerk, regelwerk2});
				//new Regelwerk[] { regelwerk3 });
		alarmVorgangEingangskorb = buero.gibAlarmVorgangEingangskorb();

		final StandardAblagekorb<Vorgangsmappe> alarmVorgangAusgangskorb = (StandardAblagekorb<Vorgangsmappe>) buero
				.gibAlarmVorgangAusgangskorb();
		DokumentVerbraucherArbeiter<Vorgangsmappe> dva = new DokumentVerbraucherArbeiter<Vorgangsmappe>(
				new DokumentenBearbeiter<Vorgangsmappe>() {
					synchronized public void bearbeiteVorgang(
							Vorgangsmappe vorgang) throws InterruptedException {
						String text = textArea.getText();
						text += vorgang.gibPruefliste().gibRegelwerkskennung()
								+ "\t"
								+ vorgang
										.gibAusloesendeAlarmNachrichtDiesesVorganges()
										.toString() + ":  \t"
								+ vorgang.gibPruefliste().gesamtErgebnis()
								+ "\n";
						textArea.setText(text);
					}
				}, alarmVorgangAusgangskorb);
		dva.start();
	}

	public synchronized void actionPerformed(ActionEvent e) {
		String text = textArea.getText();
		text += "******************************************************************************************************************************\n"; 
		textArea.setText(text);
		try {
			alarmVorgangEingangskorb.ablegen(new Vorgangsmappe(
					Vorgangsmappenkennung.createNew(InetAddress.getLocalHost(),
							new Date()), new AlarmNachricht(((JTextField) e
							.getSource()).getText())));
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		((JTextField) e.getSource()).setText("");
	}
}
