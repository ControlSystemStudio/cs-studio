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
import org.csstudio.nams.common.wam.Material;


/**
 * Eine Alarmnachricht einer Maschine.
 */
@Material
public class AlarmNachricht implements Cloneable {
	private String nachricht;
	private Date zeitFuerToString;
	private final Map<String, String> map;
	
	@Deprecated
	public AlarmNachricht(String nachricht) {
		Contract.requireNotNull("nachricht", nachricht);
		this.nachricht = nachricht;
		this.zeitFuerToString = new Date(); // TODO Entfernen!
		
		this.map = new HashMap<String, String>();

		// TODO Auto-generated constructor stub
	}

	public AlarmNachricht(Map<String, String> map) {
		this.map = map;
	}

	public String getValueFor(String key){
		String result = "";
		if(map.containsKey(key)){
			result = map.get(key);
		}
		return result;
	}
	
	@Override
	public AlarmNachricht clone() {
		return new AlarmNachricht(nachricht);
	}

	@Override
	// TODO add MAP
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + nachricht.hashCode();
		return result;
	}

	@Override
	// TODO add MAP
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!getClass().isAssignableFrom(obj.getClass()))
			return false;
		final AlarmNachricht other = (AlarmNachricht) obj;
		if (!nachricht.equals(other.nachricht))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return nachricht + "\t" + zeitFuerToString;
	}
	
	public String gibNachrichtenText() {
		return nachricht;
	}
}
