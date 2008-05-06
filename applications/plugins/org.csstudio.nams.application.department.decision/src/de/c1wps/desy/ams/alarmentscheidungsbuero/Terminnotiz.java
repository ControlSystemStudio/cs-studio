package de.c1wps.desy.ams.alarmentscheidungsbuero;

import org.csstudio.nams.common.contract.Contract;

import de.c1wps.desy.ams.allgemeines.Ablagefaehig;
import de.c1wps.desy.ams.allgemeines.Millisekunden;
import de.c1wps.desy.ams.allgemeines.Vorgangsmappenkennung;

public final class Terminnotiz implements Ablagefaehig {

	private final Vorgangsmappenkennung betreffendeVorgangsmappe;
	private final Millisekunden zeitBisZurBenachrichtigung;
	private final String nameDesZuInformierendenSachbearbeiters;

	private Terminnotiz(Vorgangsmappenkennung betreffendeVorgangsmappe,
			Millisekunden zeitBisZurBenachrichtigung,
			String nameDesZuInformierendenSachbearbeiters) {
		Contract.require(betreffendeVorgangsmappe != null,
				"betreffendeVorgangsmappe!=null");
		Contract.require(zeitBisZurBenachrichtigung != null,
				"zeitBisZurBenachrichtigung!=null");
		Contract.require(nameDesZuInformierendenSachbearbeiters != null,
				"nameDesZuInformierendenSachbearbeiters!=null");
		this.betreffendeVorgangsmappe = betreffendeVorgangsmappe;
		this.zeitBisZurBenachrichtigung = zeitBisZurBenachrichtigung;
		this.nameDesZuInformierendenSachbearbeiters = nameDesZuInformierendenSachbearbeiters;
	}

	public static Terminnotiz valueOf(
			Vorgangsmappenkennung betreffendeVorgangsmappe,
			Millisekunden zeitBisZurBenachrichtigung,
			String nameDesZuInformierendenSachbearbeiters) {
		return new Terminnotiz(betreffendeVorgangsmappe,
				zeitBisZurBenachrichtigung,
				nameDesZuInformierendenSachbearbeiters);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + betreffendeVorgangsmappe.hashCode();
		result = prime * result
				+ nameDesZuInformierendenSachbearbeiters.hashCode();
		result = prime * result + zeitBisZurBenachrichtigung.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Terminnotiz))
			return false;
		final Terminnotiz other = (Terminnotiz) obj;
		if (!betreffendeVorgangsmappe.equals(other.betreffendeVorgangsmappe))
			return false;
		if (!nameDesZuInformierendenSachbearbeiters
				.equals(other.nameDesZuInformierendenSachbearbeiters))
			return false;
		if (!zeitBisZurBenachrichtigung
				.equals(other.zeitBisZurBenachrichtigung))
			return false;
		return true;
	}

	public Millisekunden gibWartezeit() {
		return zeitBisZurBenachrichtigung;
	}

	public Vorgangsmappenkennung gibVorgangsmappenkennung() {
		return betreffendeVorgangsmappe;
	}

	public String gibNamenDesZuInformierendenSachbearbeiters() {
		return nameDesZuInformierendenSachbearbeiters;
	}

}
