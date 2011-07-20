
/* 
 * Copyright (c) 2008 C1 WPS mbH, 
 * HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR
 * PURPOSE AND  NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, 
 * REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL
 * PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER 
 * EXCEPT UNDER THIS DISCLAIMER.
 * C1 WPS HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE 
 * SOFTWARE THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND 
 * OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU 
 * MAY FIND A COPY AT
 * {@link http://www.eclipse.org/org/documents/epl-v10.html}.
 */

package org.csstudio.nams.common.material.regelwerk;

import java.util.HashMap;

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.wam.Material;

@Material
public class Pruefliste implements Cloneable {

	HashMap<VersandRegel, RegelErgebnis> ergebnisse = new HashMap<VersandRegel, RegelErgebnis>();
	private final Regelwerkskennung regelwerkskennung;
	private final VersandRegel hauptRegel;
	private Millisekunden millisekundenBisZurNaechstenPruefung = Millisekunden
			.valueOf(0);

	private Millisekunden bereitsGewarteteZeit = Millisekunden.valueOf(0);
	private boolean hatSichGeaendert;

	// @Deprecated TODO warum?
	// @ForTesting
	public Pruefliste(final Regelwerkskennung regelwerkskennung,
			final VersandRegel hauptRegel) {
		this.hauptRegel = hauptRegel;
		Contract.require(regelwerkskennung != null, "regelwerkskennung!=null");
		this.regelwerkskennung = regelwerkskennung;
	}

	@Override
	public Pruefliste clone() {
		// TODO sollte so nich mehr klappen
		final Pruefliste pruefliste = new Pruefliste(this.regelwerkskennung,
				this.hauptRegel);
		return pruefliste;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Pruefliste)) {
			return false;
		}
		final Pruefliste other = (Pruefliste) obj;
		if (!this.regelwerkskennung.equals(other.regelwerkskennung)) {
			return false;
		}
		return true;
	}

	public WeiteresVersandVorgehen gesamtErgebnis() {
		final RegelErgebnis regelErgebnis = this
				.gibErgebnisFuerRegel(this.hauptRegel);
		return WeiteresVersandVorgehen.fromRegelErgebnis(regelErgebnis);
	}

	/**
	 * Die Zeit wird nur hochgesetzt nach einem timeout!
	 */
	public Millisekunden gibBereitsGewarteteZeit() {
		return this.bereitsGewarteteZeit;
	}

	public RegelErgebnis gibErgebnisFuerRegel(final VersandRegel versandRegel) {
		RegelErgebnis regelErgebnis = this.ergebnisse.get(versandRegel);
		if (regelErgebnis == null) {
			regelErgebnis = RegelErgebnis.NOCH_NICHT_GEPRUEFT;
		}
		return regelErgebnis;
	}

	/**
	 * 
	 * @return
	 */
	public Millisekunden gibMillisekundenBisZurNaechstenPruefung() {
		return this.millisekundenBisZurNaechstenPruefung;
	}

	public Regelwerkskennung gibRegelwerkskennung() {
		return this.regelwerkskennung;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.regelwerkskennung.hashCode();
		return result;
	}

	public boolean hatSichGeaendert() {
		return this.hatSichGeaendert;
	}

	public void msGewartet(final Millisekunden gewarteteZeit) {
		this.bereitsGewarteteZeit = Millisekunden.valueOf(gewarteteZeit
				.alsLongVonMillisekunden()
				+ this.bereitsGewarteteZeit.alsLongVonMillisekunden());
	}

	public void setzeAufNichtVeraendert() {
		this.hatSichGeaendert = false;
	}

	public void setzeErgebnisFuerRegelFallsVeraendert(final VersandRegel regel,
			final RegelErgebnis ergebnis) {
		final RegelErgebnis regelErgebnis = this.ergebnisse
				.put(regel, ergebnis);
		if (ergebnis != regelErgebnis) {
			this.hatSichGeaendert = true;
		}
	}

	public void setzeMillisekundenBisZurNaechstenPruefung(
			final Millisekunden warteZeit) {
		this.millisekundenBisZurNaechstenPruefung = warteZeit;
	}

}
