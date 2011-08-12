
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

import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.Regelwerkskennung;

public class StandardRegelwerk implements Regelwerk {
	private final Regelwerkskennung regelwerkskennung;
	private final VersandRegel hauptRegel;

	// still in for testing purposes
	@Deprecated
	public StandardRegelwerk(final Regelwerkskennung regelwerkskennung) {
		this(regelwerkskennung, null);
	}

	public StandardRegelwerk(final Regelwerkskennung regelwerkskennung,
			final VersandRegel hauptRegel) {
		this.regelwerkskennung = regelwerkskennung;
		this.hauptRegel = hauptRegel;
	}

	@Deprecated
	public StandardRegelwerk(final VersandRegel hauptRegel) {
		this.hauptRegel = hauptRegel;
		this.regelwerkskennung = Regelwerkskennung.valueOf();
	}

	public Pruefliste gibDummyPrueflisteNichtSenden() {
		return new DummyPruefliste(this.regelwerkskennung);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.c1wps.desy.ams.allgemeines.regelwerk.Regelwerk#gibNeueLeerePruefliste()
	 */
	@Override
    public Pruefliste gibNeueLeerePruefliste() {
		return new Pruefliste(this.gibRegelwerkskennung(), this.hauptRegel);
	}

	@Override
    public Regelwerkskennung gibRegelwerkskennung() {
		return this.regelwerkskennung;
	}

	/**
	 * Soll aufgerufen werden wenn offene Vorgaenge bearbeitet werden.
	 * 
	 * @param alarmNachricht
	 * @param pruefliste
	 */
	@Override
    public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
			final AlarmNachricht alarmNachricht, final Pruefliste pruefliste) {
		if (this.hauptRegel != null) {
			this.hauptRegel
					.pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
							alarmNachricht, pruefliste);
		}
	}

	public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
			final AlarmNachricht alarmNachricht, final Pruefliste pruefliste,
			final AlarmNachricht initialeNachricht) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void pruefeNachrichtAufTimeOuts(final Pruefliste pruefliste,
			final Millisekunden msSeitLetzterPruefung) {
		pruefliste.msGewartet(msSeitLetzterPruefung);
		if (this.hauptRegel != null) {
			final Millisekunden zeitBisZumNaechstenTimeOut = this.hauptRegel
					.pruefeNachrichtAufTimeOuts(pruefliste, pruefliste
							.gibBereitsGewarteteZeit());
			pruefliste
					.setzeMillisekundenBisZurNaechstenPruefung(zeitBisZumNaechstenTimeOut);
		}
	}

	// public void injectHistoryService(HistoryService historyService) {
	// hauptRegel.setHistoryService(historyService);
	// }

	@Override
    public void pruefeNachrichtErstmalig(final AlarmNachricht alarmNachricht,
			final Pruefliste pruefliste) {
		if (this.hauptRegel != null) {
			final Millisekunden zeitBisZurNaechstenAuswertung = this.hauptRegel
					.pruefeNachrichtErstmalig(alarmNachricht, pruefliste);
			pruefliste
					.setzeMillisekundenBisZurNaechstenPruefung(zeitBisZurNaechstenAuswertung);
		}
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder("Regelwerkskennung: ");
		builder.append(this.regelwerkskennung.toString());
		builder.append(" Hauptregel: ");
		builder.append(this.hauptRegel.toString());
		return builder.toString();
	}

}
