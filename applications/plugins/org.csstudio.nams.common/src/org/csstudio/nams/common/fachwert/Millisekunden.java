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
package org.csstudio.nams.common.fachwert;

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.common.wam.Fachwert;

/**
 * Repräsentiert Millisekunden als Fachwert.
 */
@Fachwert
public final class Millisekunden {

	private final long value;

	private Millisekunden(long value) {
		this.value = value;
	}

	/**
	 * Liefert die Millisekunden, die durch einen long repraesentiert werden.
	 * 
	 * @param value Ein positiver long-Wert.
	 */
	public static Millisekunden valueOf(long value) {
		Contract.require(value >= 0, "value >= 0");
		return new Millisekunden(value);
	}
	
	public Millisekunden differenz(Millisekunden millisekunden) {
		return Millisekunden.valueOf(Math.abs(millisekunden.value - this.value));
	}

	public boolean istNull() {
		return this.value == 0;
	}

	@Override
	public String toString() {
		return String.valueOf(this.value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (value ^ (value >>> 32));
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
		final Millisekunden other = (Millisekunden) obj;
		if (value != other.value)
			return false;
		return true;
	}

	public boolean istKleiner(Millisekunden millisekunden) {
		return millisekunden.value > this.value;
	}

	public boolean istGroesser(Millisekunden millisekunden) {
		return millisekunden.value < this.value;
	}

	public long alsLongVonMillisekunden() {
		return this.value;
	}
	
	

}
