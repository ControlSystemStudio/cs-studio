
/*
 * Copyright (c) C1 WPS mbH, HAMBURG, GERMANY. All Rights Reserved.
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
