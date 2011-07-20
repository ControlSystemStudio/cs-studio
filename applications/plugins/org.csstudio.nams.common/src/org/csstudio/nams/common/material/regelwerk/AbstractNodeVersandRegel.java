
package org.csstudio.nams.common.material.regelwerk;

import java.util.HashSet;
import java.util.Set;

import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.testhelper.ForTesting;

public abstract class AbstractNodeVersandRegel implements VersandRegel {

	// public void setHistoryService(HistoryService historyService) {
	// super.setHistoryService(historyService);
	// for (VersandRegel regel : children) {
	// regel.setHistoryService(historyService);
	// }
	//		
	// }

	@Deprecated
	@ForTesting
	public Set<VersandRegel> children = new HashSet<VersandRegel>();

	@Deprecated
	@ForTesting
	public AbstractNodeVersandRegel() {
	    // Nothing to do here
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

	public AbstractNodeVersandRegel(final VersandRegel[] versandRegeln) {
		for (final VersandRegel versandRegel : versandRegeln) {
			this.addChild(versandRegel);
		}
	}

	@Deprecated
	@ForTesting
	public void addChild(final VersandRegel child) {
		this.children.add(child);
	}

	/**
	 * Berechnet anhand der ergebnisListe das Ergebnis dieser Regel.
	 * 
	 * @param ergebnisListe
	 * @return RegelErgebnis
	 */
	@Deprecated
	@ForTesting
	public abstract RegelErgebnis auswerten(Pruefliste ergebnisListe);

	@Deprecated
	@ForTesting
	public Set<RegelErgebnis> gibKinderErgebnisse(final Pruefliste pruefliste) {
		final Set<RegelErgebnis> ergebnis = new HashSet<RegelErgebnis>();

		for (final VersandRegel regel : this.children) {
			ergebnis.add(pruefliste.gibErgebnisFuerRegel(regel));
		}

		return ergebnis;
	}

	@Override
    public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
			final AlarmNachricht nachricht, final Pruefliste bisherigesErgebnis) {
		if (!bisherigesErgebnis.gibErgebnisFuerRegel(this).istEntschieden()) {
			for (final VersandRegel regel : this.children) {
				regel.pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
						nachricht, bisherigesErgebnis);
			}
			final RegelErgebnis ergebnis = this.auswerten(bisherigesErgebnis);
			bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
					ergebnis);
		}
	}

	@Override
    public Millisekunden pruefeNachrichtAufTimeOuts(
			final Pruefliste bisherigesErgebnis,
			final Millisekunden zeitSeitErsterEvaluation) {
		final Set<Millisekunden> warteZeiten = new HashSet<Millisekunden>();
		if (!bisherigesErgebnis.gibErgebnisFuerRegel(this).istEntschieden()) {
			for (final VersandRegel child : this.children) {
				final Millisekunden wartezeit = child
						.pruefeNachrichtAufTimeOuts(bisherigesErgebnis,
								zeitSeitErsterEvaluation);
				if (wartezeit != null) {
					warteZeiten.add(wartezeit);
				}
			}
			final RegelErgebnis ergebnis = this.auswerten(bisherigesErgebnis);
			bisherigesErgebnis.setzeErgebnisFuerRegelFallsVeraendert(this,
					ergebnis);
		}
		return this.gibKuerzesteWartezeit(warteZeiten);
	}

	@Override
    public Millisekunden pruefeNachrichtErstmalig(
			final AlarmNachricht nachricht, final Pruefliste ergebnisListe) {
		final Set<Millisekunden> warteZeiten = new HashSet<Millisekunden>();
		if (!ergebnisListe.gibErgebnisFuerRegel(this).istEntschieden()) {
			for (final VersandRegel child : this.children) {
				final Millisekunden wartezeit = child.pruefeNachrichtErstmalig(
						nachricht, ergebnisListe);
				if (wartezeit != null) {
					warteZeiten.add(wartezeit);
				}
			}
			final RegelErgebnis ergebnis = this.auswerten(ergebnisListe);
			ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this, ergebnis);
		}
		return this.gibKuerzesteWartezeit(warteZeiten);
	}

	/**
	 * 
	 * @param warteZeiten
	 *            eine Menge von Wartezeiten
	 * @return die kuerzeste Wartezeit > 0 oder 0 wenn alle Wartezeiten == 0
	 *         sind.
	 */
	private Millisekunden gibKuerzesteWartezeit(
			final Set<Millisekunden> warteZeiten) {
		Millisekunden kuerzesteWartezeit = Millisekunden
				.valueOf(Long.MAX_VALUE);
		for (final Millisekunden millisekunden : warteZeiten) {
			if ((millisekunden.alsLongVonMillisekunden() > 0)
					&& (kuerzesteWartezeit.alsLongVonMillisekunden() > millisekunden
							.alsLongVonMillisekunden())) {
				kuerzesteWartezeit = millisekunden;
			}
		}
		if (kuerzesteWartezeit.alsLongVonMillisekunden() == Long.MAX_VALUE) {
			kuerzesteWartezeit = null;
		}
		return kuerzesteWartezeit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((children == null) ? 0 : children.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AbstractNodeVersandRegel other = (AbstractNodeVersandRegel) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		return true;
	}

}
