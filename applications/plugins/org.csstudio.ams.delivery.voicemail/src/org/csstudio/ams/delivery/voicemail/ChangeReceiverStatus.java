
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

package org.csstudio.ams.delivery.voicemail;

import java.nio.ByteBuffer;

public class ChangeReceiverStatus extends Telegram
{
	private String originator;	//32
	private int grpNo;			//4
	private int usrNo;			//4
	private int statusNew;		//4
	private String statusCode;	//32
	private String reason;		//128
	
	public ChangeReceiverStatus(int telegramCnt, 
					  ByteBuffer bbUserData) {
		
	    super(Telegram.TELID_CHANGE_RECEIVER_STATUS, telegramCnt);
		telegramLen = 12 + 32 + 12 + 32 + 128;

		//bUseData
		byte baTmp[] = new byte[32];
		bbUserData.get(baTmp, 0, 32);
		this.originator = new String(baTmp).trim();

		this.grpNo = bbUserData.getInt();
		this.usrNo = bbUserData.getInt();
		this.statusNew = bbUserData.getInt();

		baTmp = new byte[32];
		bbUserData.get(baTmp, 0, 32);
		this.statusCode = new String(baTmp).trim();
		
		baTmp = new byte[128];
		bbUserData.get(baTmp, 0, 128);
		this.reason = new String(baTmp).trim();
	}

	@Override
    public byte[] getWriteBytes() {
		return null;
	}

	public int getGrpNo() {
		return grpNo;
	}

	public String getReason() {
		return reason;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public int getStatusNew() {
		return statusNew;
	}

	public int getUsrNo() {
		return usrNo;
	}

	public String getOriginator() {
		return originator;
	}
}
