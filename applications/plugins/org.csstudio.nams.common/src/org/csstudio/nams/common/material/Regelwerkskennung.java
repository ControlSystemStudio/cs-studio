package org.csstudio.nams.common.material;

import org.csstudio.nams.common.wam.Fachwert;

@Fachwert
public final class Regelwerkskennung {
	private static int zaehler = 0;

	@Deprecated
	public static Regelwerkskennung valueOf() {
		return new Regelwerkskennung();
	}

	public static Regelwerkskennung valueOf(final int filterId,
			final String regelwerkName) {
		return new Regelwerkskennung(filterId, regelwerkName);
	}

	public static Regelwerkskennung valueOf(final String name) {
		return new Regelwerkskennung(name);
	}

	private final int meinZaehlerWert;

	private final String name;

	private Regelwerkskennung() {
		this.meinZaehlerWert = Regelwerkskennung.zaehler++;
		this.name = "" + this.meinZaehlerWert;
	}

	private Regelwerkskennung(final int filterId, final String regelwerkName) {
		this.name = regelwerkName;
		this.meinZaehlerWert = filterId;
		// this.meinZaehlerWert = zaehler++;
	}

	private Regelwerkskennung(final String n) {
		this.name = n;
		this.meinZaehlerWert = Regelwerkskennung.zaehler++;
	}

	public int getRegelwerksId() {
		return this.meinZaehlerWert;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.meinZaehlerWert;
		return result;
	}

	@Override
	public String toString() {
		return this.meinZaehlerWert + "-" + this.name;
	}

	// Eine eigene equals()-Methode ist nicht erforderlich, da
	// Regelwerkskennungen nur gleich sind, wenn sie identisch sind!
	// @Override
	// public boolean equals(Object obj) {
	// if (this == obj)
	// return true;
	// if (obj == null)
	// return false;
	// if (!(obj instanceof Regelwerkskennung))
	// return false;
	// final Regelwerkskennung other = (Regelwerkskennung) obj;
	// if (meinZaehlerWert != other.meinZaehlerWert)
	// return false;
	// return true;
	// Dies wir nie passieren: ist der Zaehler identisch sind auch die Kennungen
	// identisch!!
	// return false;
	// }

}
