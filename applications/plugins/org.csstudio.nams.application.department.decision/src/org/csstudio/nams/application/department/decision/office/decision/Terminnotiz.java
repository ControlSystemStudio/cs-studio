package org.csstudio.nams.application.department.decision.office.decision;

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.common.decision.Ablagefaehig;
import org.csstudio.nams.common.decision.Vorgangsmappenkennung;
import org.csstudio.nams.common.fachwert.Millisekunden;

public final class Terminnotiz implements Ablagefaehig {

	public static Terminnotiz valueOf(
			final Vorgangsmappenkennung betreffendeVorgangsmappe,
			final Millisekunden zeitBisZurBenachrichtigung,
			final String nameDesZuInformierendenSachbearbeiters) {
		return new Terminnotiz(betreffendeVorgangsmappe,
				zeitBisZurBenachrichtigung,
				nameDesZuInformierendenSachbearbeiters);
	}

	private final Vorgangsmappenkennung betreffendeVorgangsmappe;
	private final Millisekunden zeitBisZurBenachrichtigung;

	private final String nameDesZuInformierendenSachbearbeiters;

	private Terminnotiz(final Vorgangsmappenkennung betreffendeVorgangsmappe,
			final Millisekunden zeitBisZurBenachrichtigung,
			final String nameDesZuInformierendenSachbearbeiters) {
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

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Terminnotiz)) {
			return false;
		}
		final Terminnotiz other = (Terminnotiz) obj;
		if (!this.betreffendeVorgangsmappe
				.equals(other.betreffendeVorgangsmappe)) {
			return false;
		}
		if (!this.nameDesZuInformierendenSachbearbeiters
				.equals(other.nameDesZuInformierendenSachbearbeiters)) {
			return false;
		}
		if (!this.zeitBisZurBenachrichtigung
				.equals(other.zeitBisZurBenachrichtigung)) {
			return false;
		}
		return true;
	}

	public String gibNamenDesZuInformierendenSachbearbeiters() {
		return this.nameDesZuInformierendenSachbearbeiters;
	}

	public Vorgangsmappenkennung gibVorgangsmappenkennung() {
		return this.betreffendeVorgangsmappe;
	}

	public Millisekunden gibWartezeit() {
		return this.zeitBisZurBenachrichtigung;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.betreffendeVorgangsmappe.hashCode();
		result = prime * result
				+ this.nameDesZuInformierendenSachbearbeiters.hashCode();
		result = prime * result + this.zeitBisZurBenachrichtigung.hashCode();
		return result;
	}

}
