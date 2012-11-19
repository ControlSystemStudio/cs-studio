package org.csstudio.alarm.beast.notifier;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import javax.jms.MapMessage;

import org.csstudio.alarm.beast.Activator;
import org.csstudio.alarm.beast.JMSAlarmMessage;
import org.csstudio.alarm.beast.JMSNotifierMessage;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.logging.JMSLogMessage;

/**
 * Information about automated action execution from GUI.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class GUIExecInfo {

	/** Parser for received time stamp */
	final protected static SimpleDateFormat date_format = new SimpleDateFormat(
			JMSLogMessage.DATE_FORMAT);

	final int item_id;
	final private String item_name, item_path, aa_title;
	final private ITimestamp timestamp;

	public static GUIExecInfo fromMapMessage(final MapMessage message)
			throws Exception 
	{
		final int id = message.getInt(JMSNotifierMessage.ITEM_ID);
		final String item_name = message.getString(JMSNotifierMessage.ITEM_NAME);
		final String item_path = message.getString(JMSNotifierMessage.ITEM_PATH);
		final String aa_title = message.getString(JMSNotifierMessage.AA_TITLE);
		final String timetext = message.getString(JMSAlarmMessage.EVENTTIME);
		Date time;
		try {
			time = date_format.parse(timetext);
		} catch (ParseException ex) {
			Activator.getLogger().log(Level.WARNING,
					"Received invalid time {0}", timetext);
			time = new Date();
		}
		final long millisecs = time.getTime();
		final ITimestamp timestamp = TimestampFactory.fromMillisecs(millisecs);
		return new GUIExecInfo(id, item_name, item_path, aa_title, timestamp);
	}

	public GUIExecInfo(final int item_id, 
			final String item_name,
			final String item_path,
			final String aa_title, 
			final ITimestamp timestamp) 
	{
		this.item_id = item_id;
		this.item_name = item_name;
		this.item_path = item_path;
		this.aa_title = aa_title;
		this.timestamp = timestamp;
	}

	public static SimpleDateFormat getDateFormat() {
		return date_format;
	}

	public int getItem_id() {
		return item_id;
	}

	public String getItem_name() {
		return item_name;
	}
	
	public String getItem_path() {
		return item_path;
	}

	public String getAa_title() {
		return aa_title;
	}

	public ITimestamp getTimestamp() {
		return timestamp;
	}
	
}
