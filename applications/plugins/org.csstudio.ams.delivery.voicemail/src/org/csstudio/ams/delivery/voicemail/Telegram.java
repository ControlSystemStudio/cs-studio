
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

import java.io.DataInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Telegram  {
	
    private static final Logger LOG = LoggerFactory.getLogger(Telegram.class);
    
    //ausgehende
	public static final int TELID_TEXTTORECEIVER = 0x1000001;
	
	//eingehende
	public static final int TELID_TEXTTORECEIVER_RECEIPT = 0x1000002;
	public static final int TELID_CHANGE_RECEIVER_STATUS = 0x1000003;
	public static final int TELID_ALARM_REPLY = 0x1000005;
	
	private int telegramID;
	protected int telegramLen;
	private int telegramCnt;
	
	protected Telegram(int id, int cnt) {
		this.telegramID = id;
		this.telegramCnt = cnt;
	}

	public int getTelegramCnt() {
		return telegramCnt;
	}

	public int getTelegramID() {
		return telegramID;
	}

	public ByteBuffer getWriteBuffer() {
		ByteBuffer bb = ByteBuffer.allocate(telegramLen);
		bb = bb.order(ByteOrder.LITTLE_ENDIAN);
		bb = bb.putInt(telegramID);
		bb = bb.putInt(telegramLen);
		bb = bb.putInt(telegramCnt);

		return bb;
	}
	
	public abstract byte[] getWriteBytes();

	public ByteBuffer putString(String str, int iLen, ByteBuffer bb) {
		
	    if (str == null || str.length() == 0) {
			byte ba1[] = new byte[iLen];
			bb = bb.put(ba1);
			return bb;
	    }

	    byte ba[] = str.getBytes();
	    if (ba.length == iLen)
	    {
	    	bb = bb.put(ba);
	    	return bb;
	    }
	    else if (ba.length > iLen)
	    {
	    	byte baTmp[] = new byte[iLen];
	    	for (int i = 0; i < baTmp.length; i++)
	        baTmp[i] = ba[i];

	    	bb = bb.put(baTmp);
	    	return bb;
	    }

	    bb = bb.put(ba);
	    int iDiff = iLen - ba.length;
	    if (iDiff > 0)
	    {
	    	byte ba1[] = new byte[iDiff];
	    	bb = bb.put(ba1);
	    }
	    return bb;
	}
	
/*	  public void writeCStringNTS(String str, int iLen)
	  {
	    if (str == null || str.length() == 0)
	    {
	      byte ba1[] = new byte[iLen];
	      write(ba1);
	      return;
	    }

	    byte ba[] = str.getBytes();

	    if (ba.length >= iLen)
	    {
	      byte baTmp[] = new byte[iLen-1];
	      for (int i = 0; i < baTmp.length; i++)
	        baTmp[i] = baTmp[i];

	      write(baTmp);
	      write(new byte[1]);
	      return;
	    }

	    write(ba);

	    int iDiff = iLen - ba.length;
	    if (iDiff > 0)
	    {
	      byte ba1[] = new byte[iDiff];
	      write(ba1);
	    }
	  }
*/
	
	public static Telegram readNextTelegram(DataInputStream inStream) throws Exception
	{
		byte baHead[] = new byte[12];
		int iAvailable = inStream.available();
		
		if (iAvailable == 0)
			return null;
		
		inStream.read(baHead);
		ByteBuffer bbHead = ByteBuffer.wrap(baHead);
		bbHead.order(ByteOrder.LITTLE_ENDIAN);
    
		LOG.info("incoming av= " + iAvailable + "head dump " + toHexDump(baHead));
		
		int telegramID = bbHead.getInt(0);
		int telegramLen = bbHead.getInt(4);
		int telegramCnt = bbHead.getInt(8);
		
		byte baBody[] = new byte[telegramLen-12];
		inStream.read(baBody);
		ByteBuffer bbBody = ByteBuffer.wrap(baBody);
		bbBody.order(ByteOrder.LITTLE_ENDIAN);

		LOG.info("incoming body dump " + toHexDump(baBody));
		
		Telegram tel = null;
		switch (telegramID)
		{
			case TELID_TEXTTORECEIVER_RECEIPT:
				tel = new TextToReceiverReceipt(telegramCnt, bbBody);
				break;
			case TELID_CHANGE_RECEIVER_STATUS:
				tel = new ChangeReceiverStatus(telegramCnt, bbBody);
				break;
			case TELID_ALARM_REPLY:
				tel = new ReplyAlarm(telegramCnt, bbBody);
				break;
			default:
			    LOG.warn("incoming message no/len=(" + telegramCnt + "/"+ telegramLen 
						+ "): has unknown id: '" + telegramID + "'");
				break;
		}
		
		return tel;
	}

	public static String toHexDump(byte[] ba)
	{
		String str = "\n";
		for (int i = 0; i < ba.length; i++)
		{
			str += Integer.toHexString(((ba[i] & 0xF0) >> 4));
			str += Integer.toHexString(ba[i] & 0xF);
			str += " ";
			if ((i+1) % 16 == 0)
				str += "\n";
			else if ((i+1) % 8 == 0)
				str += "  ";
		}

		return str;
	}
}
