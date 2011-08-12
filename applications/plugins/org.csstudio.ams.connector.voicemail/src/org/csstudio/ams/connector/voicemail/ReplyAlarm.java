
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

package org.csstudio.ams.connector.voicemail;

import java.nio.ByteBuffer;

public class ReplyAlarm extends Telegram {
	
    private String originator;	//32
	private int chainIdAndPos;	//4
	private String confirmCode;	//32
	
	public ReplyAlarm(int telegramCnt, 
					  ByteBuffer bbUserData) {
		
	    super(Telegram.TELID_ALARM_REPLY, telegramCnt);
		telegramLen = 12 + 32 + 4 + 32;

		//bUseData
		byte baTmp[] = new byte[32];
		bbUserData.get(baTmp, 0, 32);  //relative, counts intern cnt
		this.originator = new String(baTmp).trim();

		this.chainIdAndPos = bbUserData.getInt();  //relative, counts intern cnt +4

		baTmp = new byte[32];
		bbUserData.get(baTmp, 0, 32);  //relative, counts intern cnt +4
		this.confirmCode = new String(baTmp).trim();
	}

	@Override
    public byte[] getWriteBytes() {
		return null;
	}

	public int getChainIdAndPos() {
		return chainIdAndPos;
	}

	public String getConfirmCode() {
		return confirmCode;
	}

	public String getOriginator() {
		return originator;
	}
}
