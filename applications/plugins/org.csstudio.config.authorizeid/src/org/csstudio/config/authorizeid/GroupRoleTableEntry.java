/* 
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.config.authorizeid;

import java.io.Serializable;

import org.eclipse.core.runtime.PlatformObject;

/**
 * Entry for Group / Role Table
 * @author Rok Povsic
 */
public class GroupRoleTableEntry extends PlatformObject implements Serializable {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 7330376130143617241L;
	
	private String eaig;
	private String eair;

	/**
	 * The constructor.
	 * @param _eaig eaig (group)
	 * @param _eair eair (role)
	 */
	public GroupRoleTableEntry(String _eaig, String _eair) {
		eaig = _eaig;
		eair = _eair;
	}
	
	/**
	 * Getter for eaig.
	 * @return eaig
	 */
	public String getEaig() {
		return eaig;
	}
	
	/**
	 * Getter for eair.
	 * @return eair
	 */
	public String getEair() {
		return eair;
	}
	
}
