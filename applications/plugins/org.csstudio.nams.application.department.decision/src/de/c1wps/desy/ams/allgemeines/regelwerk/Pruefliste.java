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
package de.c1wps.desy.ams.allgemeines.regelwerk;

import java.util.HashMap;

import de.c1wps.desy.ams.allgemeines.Millisekunden;
import de.c1wps.desy.ams.allgemeines.contract.Contract;
import de.c1wps.desy.ams.allgemeines.wam.Material;

@Material
public class Pruefliste implements Cloneable {

	HashMap<VersandRegel, RegelErgebnis> ergebnisse = new HashMap<VersandRegel, RegelErgebnis>();
	private final Regelwerkskennung regelwerkskennung;
	private final VersandRegel hauptRegel;
	private Millisekunden millisekundenBisZurNaechstenPruefung = Millisekunden
	.valueOf(0);
	
	private Millisekunden bereitsGewarteteZeit = Millisekunden.valueOf(0);
	private boolean hatSichGeaendert;

	@Override
	public Pruefliste clone() {
		// TODO sollte so nich mehr klappen
		return new Pruefliste(regelwerkskennung, hauptRegel);
	}

	protected Pruefliste(Regelwerkskennung regelwerkskennung,
			VersandRegel hauptRegel) {
		this.hauptRegel = hauptRegel;
		Contract.require(regelwerkskennung != null, "regelwerkskennung!=null");
		this.regelwerkskennung = regelwerkskennung;
	}

//	@Deprecated
	// Sichtbarkeit sollte nicht "public" sein, aktuell (01.04.08) nur für
	// Sachberarbeiter_Test nötig
//	Pruefliste(Regelwerkskennung regelwerkskennung) {
//		this(regelwerkskennung, null);
//	}

	public void setzeErgebnisFuerRegelFallsVeraendert(VersandRegel regel, RegelErgebnis ergebnis) {
		RegelErgebnis regelErgebnis = ergebnisse.put(regel, ergebnis);
		if (ergebnis != regelErgebnis)
			this.hatSichGeaendert = true;
	}

	public RegelErgebnis gibErgebnisFuerRegel(VersandRegel versandRegel) {
		RegelErgebnis regelErgebnis = ergebnisse.get(versandRegel);
		if (regelErgebnis == null) {
			regelErgebnis = RegelErgebnis.NOCH_NICHT_GEPRUEFT;
		}
		return regelErgebnis;
	}

	public WeiteresVersandVorgehen gesamtErgebnis() {
		RegelErgebnis regelErgebnis = gibErgebnisFuerRegel(hauptRegel);
		return WeiteresVersandVorgehen.fromRegelErgebnis(regelErgebnis);
	}

	public void setzeMillisekundenBisZurNaechstenPruefung(Millisekunden warteZeit){
		millisekundenBisZurNaechstenPruefung = warteZeit;
	}
	
	/**
	 * 
	 * @return
	 */
	public Millisekunden gibMillisekundenBisZurNaechstenPruefung() {
		// TODO Auto-generated method stub
		return millisekundenBisZurNaechstenPruefung;
	}

	public void msGewartet(Millisekunden gewarteteZeit) {
		bereitsGewarteteZeit = Millisekunden.valueOf(gewarteteZeit
				.alsLongVonMillisekunden()
				+ bereitsGewarteteZeit.alsLongVonMillisekunden());
	}
	
	/**
	 * Die Zeit wird nur hochgesetzt nach einem timeout!
	 */
	public Millisekunden gibBereitsGewarteteZeit(){
		return bereitsGewarteteZeit;
	}

	public Regelwerkskennung gibRegelwerkskennung() {
		return regelwerkskennung;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + regelwerkskennung.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Pruefliste))
			return false;
		final Pruefliste other = (Pruefliste) obj;
		if (!regelwerkskennung.equals(other.regelwerkskennung))
			return false;
		return true;
	}

	public void setzeAufNichtVeraendert() {
		this.hatSichGeaendert = false;
	}

	public boolean hatSichGeaendert() {
		return this.hatSichGeaendert;
	}

}
