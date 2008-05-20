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
package de.c1wps.desy.ams.allgemeines;

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.regelwerk.Pruefliste;

import de.c1wps.desy.ams.allgemeines.wam.Material;

@Material
public class Vorgangsmappe implements Ablagefaehig {

	private final AlarmNachricht alarmNachricht;
	private Pruefliste pruefliste;
	private final Vorgangsmappenkennung kennung;
	private Vorgangsmappenkennung abgeschlossenDurchMappenkennung;

//	@Deprecated
//	public Vorgangsmappe(AlarmNachricht nachricht) {
//		Contract.requireNotNull("nachricht", nachricht);
//
//		this.alarmNachricht = nachricht;
//		this.kennung = null;
//	}

	public Vorgangsmappe(Vorgangsmappenkennung kennung, AlarmNachricht nachricht) {
		Contract.requireNotNull("nachricht", nachricht);

		this.alarmNachricht = nachricht;
		this.kennung = kennung;
	}

	public AlarmNachricht gibAusloesendeAlarmNachrichtDiesesVorganges() {
		return alarmNachricht;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + alarmNachricht.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!getClass().isAssignableFrom(obj.getClass()))
			return false;
		final Vorgangsmappe other = (Vorgangsmappe) obj;
		if (!alarmNachricht.equals(other.alarmNachricht))
			return false;
		if (!this.kennung.equals(other.kennung))
			return false;
		return true;
	}

	public Pruefliste gibPruefliste() {
		return this.pruefliste;
	}

	public void setzePruefliste(Pruefliste pruefliste) {
		this.pruefliste = pruefliste;
	}

	public void pruefungAbgeschlossenDurch(Vorgangsmappenkennung mappenkennung) {
		this.abgeschlossenDurchMappenkennung = mappenkennung;
	}
	
	public Vorgangsmappenkennung gibAbschliessendeMappenkennung() {
		return abgeschlossenDurchMappenkennung;
	}
	
	public boolean istAbgeschlossen() {
		return (abgeschlossenDurchMappenkennung!=null);
	}

	public Vorgangsmappenkennung gibMappenkennung() {
		return this.kennung;
	}

	public Vorgangsmappe erstelleKopieFuer(String bearbeiter) {
		Vorgangsmappe kopie = new Vorgangsmappe(Vorgangsmappenkennung.valueOf(
				this.kennung, bearbeiter), this.alarmNachricht.clone());
		Pruefliste prueflisteHier = this.gibPruefliste();
		if (prueflisteHier != null) {
			kopie.setzePruefliste(prueflisteHier.clone());
		}
		return kopie;
	}

	@Override
	public String toString() {
		return this.kennung.toString() + " " + alarmNachricht;
	}

	// TODO Ggf. spaeter Kapitel einfuehren für einzelne Bereiche!
}
