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
 package org.csstudio.ams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.TimeZone;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;

import org.eclipse.swt.layout.GridData;

public class Utils
{
	/**
	 * Returns a clone of the MapMessage.
	 * 
	 * @param in		MapMessage
	 * @param session	Session
	 * @return MapMessage
	 * @throws JMSException
	 */
	public static MapMessage cloneMessage(MapMessage in, Session session) throws JMSException
	{
    	MapMessage msg = session.createMapMessage();
		
		Enumeration<?> enumProps = in.getMapNames(); //?getMapNames
		while (enumProps.hasMoreElements())
		{
			String propName = (String)enumProps.nextElement();
			String value = in.getString(propName);
			msg.setString(propName, value);
		}
		
    	return msg;
	}
	
	/**
	 * Creates a log entry containing the String 
	 * and the MapMessage Properties.
	 * 
	 * @param str		String
	 * @param in		MapMessage
	 * @throws JMSException
	 * @see #getMessageString(MapMessage)
	 */
	public static void logMessage(String str, MapMessage in) throws JMSException
	{
/*		Enumeration enumProps = in.getMapNames(); //?getMapNames
		Log.log(Log.INFO, str + " (Properties=" + enumProps+"):");
		while (enumProps.hasMoreElements())
		{
			String propName = (String)enumProps.nextElement();
			String value = in.getString(propName);
			Log.log(Log.INFO, propName+"="+value);
		}
*/
		Log.log(Log.INFO, str + " Properties= " + getMessageString(in));
	}
	
	/**
	 * Returns a String with the MapMessage Properties.
	 * 
	 * @param in		MapMessage
	 * @return String
	 * @throws JMSException
	 * @see #logMessage(String, MapMessage)
	 */
	public static String getMessageString(MapMessage in) throws JMSException
	{
		String ret = null;
		Enumeration<?> enumProps = in.getMapNames(); //?getMapNames
		while (enumProps.hasMoreElements())
		{
			String propName = (String)enumProps.nextElement();
			String value = in.getString(propName);
		
			if (ret == null)
				ret = "";
			else
				ret += ";";
		
			ret += propName + "=" +  value;
		}
		return ret;
	}
	
	/**
	 * Returns a new GridData Object initialized with the specified Values.
	 * 
	 * @param width			int
	 * @param height		int
	 * @param cellWidth		int
	 * @param cellHeight	int
	 * @param hAlign		int
	 * @param vAlign		int
	 * @param hGrab			boolean
	 * @param vGrab			boolean
	 * @return GridData
	 */
	public static GridData getGridData(int width, int height,
			int cellWidth, int cellHeight,
			int hAlign, int vAlign,
			boolean hGrab, boolean vGrab)
	{
		GridData gd = new GridData(width, height);
		
		gd.horizontalSpan = cellWidth;
		gd.verticalSpan = cellHeight;
		gd.horizontalAlignment = hAlign;
		gd.verticalAlignment = vAlign;
		gd.grabExcessHorizontalSpace = hGrab;
		gd.grabExcessVerticalSpace = vGrab;
		
		return gd;
	}
	
	/**
	 * Checks if the String is empty or not.
	 * 
	 * @param str		String
	 * @return <code>true</code> if the String is <code>null</code> or
	 *  it's length is <code>0</code>, or <code>false</code>
	 *  if the String's length <code>!=0</code>
	 */
	public static boolean isEmpty(String str)
	{
		if(str == null || str.trim().length() == 0)
			return true;
		return false;
	}
	
	/**
	 * Returns a clone of the Object.
	 * 
	 * @param o			Object
	 * @return Object
	 */
	public static Object cloneObject(Object o)
	{
		ObjectOutputStream os = null;
		ObjectInputStream  is = null;
		Object result = null;
		
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			os = new ObjectOutputStream(out);
			os.writeObject(o);
			os.flush();

			ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
			is = new ObjectInputStream(in);
			result = is.readObject();
		}
		catch(Exception ex)
		{
			Log.log(Log.FATAL, ex);
		}
		finally
		{
			try{if(is != null)is.close();}catch(Exception ex){}
			try{if(os != null)os.close();}catch(Exception ex){}
		}
		return result;
	}
	
	/**
	 * Returns the Substring of the String.
	 * 
	 * @param str		String
	 * @param endIdx	the last char Position (not included in the Substring)
	 * @return <code>null</code> if the String is <code>null</code>,
	 *  the Substring from the beginning to the position <code>endIdx</code>,
	 *  or the String if the position <code>endIdx</code>
	 *  is larger than length of the String.
	 */
	public static String subStr(String str, int endIdx)
	{
		if (str == null)
			return null;
		
		if (str.length() < endIdx)
			return str;
		
		return str.substring(0, endIdx);
	}

	public static String longTimeToUTCString(long lSetTime)
	{
		java.util.Date date = new java.util.Date(lSetTime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(date);
	}

}