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
package org.csstudio.nams.common.material;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.wam.Material;

/**
 * Eine Alarmnachricht einer Maschine.
 */
@Material
public class AlarmNachricht implements Cloneable {
	private String nachricht;
	private Date zeitFuerToString;
	private final Map<MessageKeyEnum, String> content;

	@Deprecated
	public AlarmNachricht(String nachricht) {
		Contract.requireNotNull("nachricht", nachricht);
		this.nachricht = nachricht;
		this.zeitFuerToString = new Date(); // TODO Entfernen!

		this.content = new HashMap<MessageKeyEnum, String>();

		// TODO Auto-generated constructor stub
	}

	private AlarmNachricht(String nachricht, Date zeitFuerToString, Map<MessageKeyEnum, String> content){
		this.nachricht = nachricht;
		this.zeitFuerToString = zeitFuerToString;
		this.content = content;
		
	}
	
	public AlarmNachricht(Map<MessageKeyEnum, String> map) {
		this.content = map;
		this.zeitFuerToString = new Date();
	}

	public String getValueFor(MessageKeyEnum key) {
		String result = "";
		if (content.containsKey(key)) {
			result = content.get(key);
		}
		return result;
	}

	@Override
	public AlarmNachricht clone() {
		return new AlarmNachricht(nachricht, zeitFuerToString, content);
	}

	@Override
	public String toString() {
		return nachricht + "\t" + zeitFuerToString;
	}

	public void matchedMessageWithRegelwerk(Regelwerkskennung kennung) {
		content.put(MessageKeyEnum.AMS_REINSERTED, Integer.valueOf(
				kennung.getRegelwerksId()).toString());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result
				+ ((nachricht == null) ? 0 : nachricht.hashCode());
		result = prime
				* result
				+ ((zeitFuerToString == null) ? 0 : zeitFuerToString.hashCode());
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
		final AlarmNachricht other = (AlarmNachricht) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (nachricht == null) {
			if (other.nachricht != null)
				return false;
		} else if (!nachricht.equals(other.nachricht))
			return false;
		if (zeitFuerToString == null) {
			if (other.zeitFuerToString != null)
				return false;
		} else if (!zeitFuerToString.equals(other.zeitFuerToString))
			return false;
		return true;
	}

	public String gibNachrichtenText() {
		return nachricht;
	}
}
