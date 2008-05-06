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

import org.csstudio.nams.common.material.AlarmNachricht;

import de.c1wps.desy.ams.allgemeines.Millisekunden;

public class StandardRegelwerk implements Regelwerk {
	private final Regelwerkskennung regelwerkskennung;
	private final VersandRegel hauptRegel;

	// still in for testing purposes
	@Deprecated
	public StandardRegelwerk(Regelwerkskennung regelwerkskennung) {
		this(regelwerkskennung, null);
	}

	public StandardRegelwerk(Regelwerkskennung regelwerkskennung,
			VersandRegel hauptRegel) {
		this.regelwerkskennung = regelwerkskennung;
		this.hauptRegel = hauptRegel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.c1wps.desy.ams.allgemeines.regelwerk.Regelwerk#gibNeueLeerePruefliste()
	 */
	public Pruefliste gibNeueLeerePruefliste() {
		return new Pruefliste(gibRegelwerkskennung(), hauptRegel);
	}

	public Pruefliste gibDummyPrueflisteNichtSenden() {
		return new DummyPruefliste(regelwerkskennung);
	}

	public void pruefeNachrichtErstmalig(AlarmNachricht alarmNachricht,
			Pruefliste pruefliste) {
		if (hauptRegel != null) {
			Millisekunden zeitBisZurNaechstenAuswertung = hauptRegel
					.pruefeNachrichtErstmalig(alarmNachricht, pruefliste);
			pruefliste
					.setzeMillisekundenBisZurNaechstenPruefung(zeitBisZurNaechstenAuswertung);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void pruefeNachrichtAufTimeOuts(Pruefliste pruefliste,
			Millisekunden msSeitLetzterPruefung) {
		pruefliste.msGewartet(msSeitLetzterPruefung);
		if (hauptRegel != null) {
			Millisekunden zeitBisZumNaechstenTimeOut = hauptRegel.pruefeNachrichtAufTimeOuts(pruefliste,
					pruefliste.gibBereitsGewarteteZeit());
			pruefliste.setzeMillisekundenBisZurNaechstenPruefung(zeitBisZumNaechstenTimeOut);
		}
	}

	/**
	 * Soll aufgerufen werden wenn offene Vorgaenge bearbeitet werden.
	 * 
	 * @param alarmNachricht
	 * @param pruefliste
	 */
	public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
			AlarmNachricht alarmNachricht, Pruefliste pruefliste) {
		if (hauptRegel != null)
			hauptRegel.pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
					alarmNachricht, pruefliste);
	}

	public Regelwerkskennung gibRegelwerkskennung() {
		return regelwerkskennung;
	}

}
