package org.csstudio.nams.common.material.regelwerk;

import java.util.HashSet;
import java.util.Set;

import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.testhelper.ForTesting;

public abstract class AbstractNodeVersandRegel implements VersandRegel {

//	public void setHistoryService(HistoryService historyService) {
//		super.setHistoryService(historyService);
//		for (VersandRegel regel : children) {
//			regel.setHistoryService(historyService);
//		}
//		
//	}

	public AbstractNodeVersandRegel(VersandRegel[] versandRegeln) {
		for (VersandRegel versandRegel : versandRegeln) {
			addChild(versandRegel);
		}
	}
	
	@Deprecated
	@ForTesting
	public AbstractNodeVersandRegel() {
		
	}
	
	// public Millisekunden gibverbleibendeWartezeit(
	// Millisekunden bereitsVerstricheneWarteZeit) {
	// Millisekunden kuerzesteWartezeit = Millisekunden.valueOf(Long.MAX_VALUE);
	// for (VersandRegel regel : childs) {
	// Millisekunden wartezeit =
	// regel.gibverbleibendeWartezeit(bereitsVerstricheneWarteZeit);
	// if (kuerzesteWartezeit.istGroesser(wartezeit)){
	// kuerzesteWartezeit = wartezeit;
	// }
	// }
	// return kuerzesteWartezeit;
	// }

	@Deprecated
	@ForTesting
	public Set<VersandRegel> children = new HashSet<VersandRegel>();

	@Deprecated
	@ForTesting
	public void addChild(VersandRegel child) {
		children.add(child);
	}
	@Deprecated
	@ForTesting
	public Set<RegelErgebnis> gibKinderErgebnisse(Pruefliste pruefliste) {
		Set<RegelErgebnis> ergebnis = new HashSet<RegelErgebnis>();

		for (VersandRegel regel : children) {
			ergebnis.add(pruefliste.gibErgebnisFuerRegel(regel));
		}

		return ergebnis;
	}

	/**
	 * Berechnet anhand der ergebnisListe das Ergebnis dieser Regel.
	 * 
	 * @param ergebnisListe
	 * @return
	 */
	@Deprecated
	@ForTesting
	public abstract RegelErgebnis auswerten(Pruefliste ergebnisListe);

	/**
	 * 
	 * @param warteZeiten
	 *            eine Menge von Wartezeiten
	 * @return die kuerzeste Wartezeit > 0 oder 0 wenn alle Wartezeiten == 0
	 *         sind.
	 */
	private Millisekunden gibKuerzesteWartezeit(Set<Millisekunden> warteZeiten) {
		Millisekunden kuerzesteWartezeit = Millisekunden
				.valueOf(Long.MAX_VALUE);
		for (Millisekunden millisekunden : warteZeiten) {
			if (millisekunden.alsLongVonMillisekunden() > 0
					&& kuerzesteWartezeit.alsLongVonMillisekunden() > millisekunden
							.alsLongVonMillisekunden()) {
				kuerzesteWartezeit = millisekunden;
			}
		}
		if (kuerzesteWartezeit.alsLongVonMillisekunden() == Long.MAX_VALUE) {
			kuerzesteWartezeit = null;
		}
		return kuerzesteWartezeit;
	}

	public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
			AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
		if (!bisherigesErgebnis.gibErgebnisFuerRegel(this).istEntschieden()) {
			for (VersandRegel regel : children) {
				regel.pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
						nachricht, bisherigesErgebnis);
			}
			RegelErgebnis ergebnis = auswerten(bisherigesErgebnis);
			bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
					ergebnis);
		}
	}

	public Millisekunden pruefeNachrichtAufTimeOuts(
			Pruefliste bisherigesErgebnis,
			Millisekunden zeitSeitErsterEvaluation, AlarmNachricht initialeNachricht) {
		Set<Millisekunden> warteZeiten = new HashSet<Millisekunden>();
		if (!bisherigesErgebnis.gibErgebnisFuerRegel(this).istEntschieden()) {
			for (VersandRegel child : children) {
				Millisekunden wartezeit = child.pruefeNachrichtAufTimeOuts(
						bisherigesErgebnis, zeitSeitErsterEvaluation, initialeNachricht);
				if (wartezeit != null) {
					warteZeiten.add(wartezeit);
				}
			}
			RegelErgebnis ergebnis = auswerten(bisherigesErgebnis);
			bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
					ergebnis);
		}
		return gibKuerzesteWartezeit(warteZeiten);
	}

	public Millisekunden pruefeNachrichtErstmalig(AlarmNachricht nachricht,
			Pruefliste ergebnisListe) {
		Set<Millisekunden> warteZeiten = new HashSet<Millisekunden>();
		if (!ergebnisListe.gibErgebnisFuerRegel(this).istEntschieden()) {
			for (VersandRegel child : children) {
				Millisekunden wartezeit = child.pruefeNachrichtErstmalig(nachricht,
						ergebnisListe);
				if (wartezeit != null) {
					warteZeiten.add(wartezeit);
				}
			}
			RegelErgebnis ergebnis = auswerten(ergebnisListe);
			ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this, ergebnis);
		}
		return gibKuerzesteWartezeit(warteZeiten);
	}

}
