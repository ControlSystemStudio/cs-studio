
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
	public static MapMessage cloneMessage(final MapMessage in, final Session session) throws JMSException
	{
    	final MapMessage msg = session.createMapMessage();

		final Enumeration<?> enumProps = in.getMapNames(); //?getMapNames
		while (enumProps.hasMoreElements())
		{
			final String propName = (String)enumProps.nextElement();
			final String value = in.getString(propName);
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
	public static void logMessage(final String str, final MapMessage in) throws JMSException
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
	public static String getMessageString(final MapMessage in) throws JMSException
	{
		String ret = null;
		final Enumeration<?> enumProps = in.getMapNames(); //?getMapNames
		while (enumProps.hasMoreElements())
		{
			final String propName = (String)enumProps.nextElement();
			final String value = in.getString(propName);

			if (ret == null) {
                ret = "";
            } else {
                ret += ";";
            }

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
	public static GridData getGridData(final int width, final int height,
			final int cellWidth, final int cellHeight,
			final int hAlign, final int vAlign,
			final boolean hGrab, final boolean vGrab)
	{
		final GridData gd = new GridData(width, height);

		gd.horizontalSpan = cellWidth;
		gd.verticalSpan = cellHeight;
		gd.horizontalAlignment = hAlign;
		gd.verticalAlignment = vAlign;
		gd.grabExcessHorizontalSpace = hGrab;
		gd.grabExcessVerticalSpace = vGrab;

		return gd;
	}

	/**
	 * Returns a clone of the Object.
	 *
	 * @param o			Object
	 * @return Object
	 */
	public static Object cloneObject(final Object o)
	{
		ObjectOutputStream os = null;
		ObjectInputStream  is = null;
		Object result = null;

		try
		{
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			os = new ObjectOutputStream(out);
			os.writeObject(o);
			os.flush();

			final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
			is = new ObjectInputStream(in);
			result = is.readObject();
		}
		catch(final Exception ex)
		{
			Log.log(Log.FATAL, ex);
		}
		finally
		{
			try{if(is != null) {
                is.close();
            }}catch(final Exception ex){}
			try{if(os != null) {
                os.close();
            }}catch(final Exception ex){}
		}
		return result;
	}

	public static String longTimeToUTCString(final long lSetTime)
	{
		final java.util.Date date = new java.util.Date(lSetTime);
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(date);
	}

}