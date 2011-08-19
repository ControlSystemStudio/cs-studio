
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.ams.dbAccess.configdb;

import org.csstudio.ams.dbAccess.ItemInterface;
import org.csstudio.ams.dbAccess.TObject;

/**
	iUserId 				NUMBER(11) NOT NULL,
	iGroupRef				NUMBER(11) default -1 NOT NULL,
	cUserName 				VARCHAR2(128),
	cEmail 					VARCHAR2(128),
	cMobilePhone			VARCHAR2(64),
	cPhone					VARCHAR2(64),
	cStatusCode				VARCHAR2(32),
	cConfirmCode			VARCHAR2(32),
	sActive					NUMBER(6),
	sPreferredAlarmingTypeRR	NUMBER(6),
	// sPreferredAlarmingType	NUMBER(6),
	PRIMARY KEY (iUserId)						
*/
@SuppressWarnings("hiding")
public class UserTObject extends TObject implements ItemInterface {
	
    private static final long serialVersionUID = -4695576106821896973L;
	
	private int 	userID;// PRIMARY KEY
	private int 	groupRef;
	private String 	name;
	private String 	email;
	private String 	mobilePhone;
	private String 	phone;
	private String 	statusCode;
	private String 	confirmCode;
	private short 	isActive;
	private short 	prefAlarmingTypeRR;
	
	public UserTObject()
	{
		this.userID = -1;
		this.groupRef = -1;
	}
	
	public UserTObject(int userID, int groupRef, String name,
	                   String email, String mobilePhone, String phone,
	                   String statusCode, String confirmCode, short isActive,
	                   short prefAlarmingTypeRR) {
		
	    this.userID = userID;
		this.groupRef = groupRef;
		this.name = name;
		this.email = email;
		this.mobilePhone = mobilePhone;
		this.phone = phone;
		this.statusCode = statusCode;
		this.confirmCode = confirmCode;
		this.isActive = isActive;
		this.prefAlarmingTypeRR = prefAlarmingTypeRR;
	}
	
	public UserKey getKey() {
		return new UserKey(userID, name, groupRef);
	}

	@Override
    public int getID() {
		return userID;
	}
	
	@Override
    public boolean equals(Object obj) {
		
	    if(!(obj instanceof UserTObject))
			return false;
		
		UserTObject compare = (UserTObject)obj;
		
		if(compare.getActive() != getActive())
			return false;
		if(!strEquals(compare.getConfirmCode(), getConfirmCode()))
			return false;
		if(!strEquals(compare.getEmail(), getEmail()))
			return false;
		if(compare.getGroupRef() != getGroupRef())
			return false;
		if(!strEquals(compare.getMobilePhone(), getMobilePhone()))
			return false;
		if(!strEquals(compare.getName(), getName()))
			return false;
		if(!strEquals(compare.getPhone(), getPhone()))
			return false;
		if(compare.getPrefAlarmingTypeRR() != getPrefAlarmingTypeRR())
			return false;
		if(!strEquals(compare.getStatusCode(), getStatusCode()))
			return false;
		if(compare.getUserID() != getUserID())
			return false;
		return true;
	}	


	////////// Getter- and Setter-Methods //////////

	public String getConfirmCode() {
		return confirmCode;
	}

	public void setConfirmCode(String confirmCode) {
		this.confirmCode = confirmCode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getGroupRef() {
		return groupRef;
	}

	public void setGroupRef(int groupRef) {
		this.groupRef = groupRef;
	}

	public short getActive() {
		return isActive;
	}

	public void setActive(short isActive) {
		this.isActive = isActive;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public short getPrefAlarmingTypeRR() {
		return prefAlarmingTypeRR;
	}

	public void setPrefAlarmingTypeRR(short prefAlarmingTypeRR) {
		this.prefAlarmingTypeRR = prefAlarmingTypeRR;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}
}
