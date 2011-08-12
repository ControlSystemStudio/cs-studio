
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

import java.util.Collections;
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
	private final Date zeitFuerToString;
	private final Map<MessageKeyEnum, String> content;
	private final Map<String, String> unknownContent;
	private final static Map<String, String> emptyContent = Collections.emptyMap();

	
	public AlarmNachricht(final Map<MessageKeyEnum, String> map) {
		this(map, emptyContent);
	}

	public AlarmNachricht(Map<MessageKeyEnum, String> map,
			Map<String, String> unknownMap) {
		this.unknownContent = unknownMap;
		this.content = map;
		this.zeitFuerToString = new Date();
		this.nachricht = map.toString() + " " + unknownMap.toString();
	}

	@Deprecated
	public AlarmNachricht(final String nachricht) {
		Contract.requireNotNull("nachricht", nachricht);
		this.nachricht = nachricht;
		this.zeitFuerToString = new Date();

		this.content = new HashMap<MessageKeyEnum, String>();
		this.unknownContent = emptyContent;
	}

	private AlarmNachricht(final String nachricht, final Date zeitFuerToString,
			final Map<MessageKeyEnum, String> content, Map<String, String> unknownContent) {
		this.nachricht = nachricht;
		this.zeitFuerToString = zeitFuerToString;
		this.content = content;
		this.unknownContent = unknownContent;
	}


	@Override
	public AlarmNachricht clone() {
		return new AlarmNachricht(this.nachricht, this.zeitFuerToString,
				this.content, this.unknownContent);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final AlarmNachricht other = (AlarmNachricht) obj;
		if (this.content == null) {
			if (other.content != null) {
				return false;
			}
		} else if (!this.content.equals(other.content)) {
			return false;
		}
		if (this.unknownContent == null) {
			if (other.unknownContent != null) {
				return false;
			}
		} else if (!this.unknownContent.equals(other.unknownContent)) {
			return false;
		}
		if (this.nachricht == null) {
			if (other.nachricht != null) {
				return false;
			}
		} else if (!this.nachricht.equals(other.nachricht)) {
			return false;
		}
		if (this.zeitFuerToString == null) {
			if (other.zeitFuerToString != null) {
				return false;
			}
		} else if (!this.zeitFuerToString.equals(other.zeitFuerToString)) {
			return false;
		}
		return true;
	}

	public Map<MessageKeyEnum, String> getContentMap() {
		return Collections.unmodifiableMap(this.content);
	}
	
	public Map<String, String> getUnknownContentMap() {
		return Collections.unmodifiableMap(this.unknownContent);
	}

	public String getValueFor(final MessageKeyEnum key) {
		String result = "";
		if (this.content.containsKey(key)) {
			result = this.content.get(key);
		}
		return result;
	}

	public String gibNachrichtenText() {
		return this.nachricht;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.content == null) ? 0 : this.content.hashCode());
		result = prime * result
				+ ((this.nachricht == null) ? 0 : this.nachricht.hashCode());
		result = prime
				* result
				+ ((this.zeitFuerToString == null) ? 0 : this.zeitFuerToString
						.hashCode());
		return result;
	}

	// TODO mz: Name sprechender gestalten.
	public void matchedMessageWithRegelwerk(final Regelwerkskennung kennung) {
		this.content.put(MessageKeyEnum.AMS_REINSERTED, Integer.valueOf(
				kennung.getRegelwerksId()).toString());
	}

	@Override
	public String toString() {
		return this.nachricht + "\t" + this.zeitFuerToString;
	}
}
