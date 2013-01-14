package org.csstudio.alarm.syslog2jms;

import java.net.InetAddress;
import java.text.SimpleDateFormat;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.csstudio.platform.utility.jms.JmsSimpleProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParseSyslogMessage implements Runnable {
	
	private String message = null;
	private JmsSimpleProducer jmsProducer = null;
	private InetAddress inetAddress = null;
	private int port = 0;
	
	/** The class logger */
    private static final Logger LOG = LoggerFactory.getLogger(Syslog2JmsApplication.class);

	ParseSyslogMessage ( String message, JmsSimpleProducer jmsProducer, InetAddress inetAddress, int port) {
		this.message = message;
		this.jmsProducer = jmsProducer;
		this.inetAddress = inetAddress;
		this.port = port;
	}
	
	@Override
	public void run() {
		byte sev = 0;
		byte fac = 0;
		byte sysVersion = 0;
		java.util.Date dateTime = null;
		String hostName = "";
		String appName = "";
		String prodid = "";
		String msgid = "";
		
		// sd 1..4 and msg
		String msg = "";
		String sdTimeQuality = "";
		String sdMeta = "";
		String sdOrigin = "";
		String sdSelf = "";
		
		boolean debug = true;
		
		if (debug) {
		System.out.println("======= NEW Message =======");
		System.out.println("Message:" + message);
		}
		
		String stringMsg = message;
		
		try
		 {
			 // get severity and facility
			 int a = stringMsg.indexOf("<");
			 int b = stringMsg.indexOf(">");
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: Byte c = Convert.ToByte(stringMsg.Substring(a + 1, b - a - 1));
			 byte c = Byte.parseByte(stringMsg.substring(a + 1, a + 1 + b - a - 1));
			 sev = (byte)(c & 0x07);
			 fac = (byte)(c / 8);

			 // detect syslog version
			 if (stringMsg.substring(b + 1, b + 1 + 2).equals("1 "))
			 {
				 sysVersion = 1;

				 // remove <prio> and version
				 stringMsg = stringMsg.substring(b + 3);
			 }
			 else
			 {
				 sysVersion = 0;

				 // remove <prio> only
				 stringMsg = stringMsg.substring(b + 1);
			 }
		 }
		 catch (java.lang.Exception e)
		 {
		 }

		 //if (((STRProperties.rfcAuto) && (sysVersion == 1)) || (STRProperties.rfc5424))
		 if ( true) {	// always
			 int sp1 = stringMsg.indexOf(" ");
			 int sp2 = stringMsg.indexOf(" ", sp1 + 1);
			 int sp3 = stringMsg.indexOf(" ", sp2 + 1);
			 int sp4 = stringMsg.indexOf(" ", sp3 + 1);
			 int sp5 = stringMsg.indexOf(" ", sp4 + 1);

			 if ((sp1 != 0) && (sp2 != 0) && (sp3 != 0) && (sp4 != 0) && (sp5 != 0))
			 {
				 // get date/time
				 dateTime = convertSyslogDateToDateTime(stringMsg.substring(0, sp1));
				 if ( debug) {
					 System.out.println("Date: " + dateTime);
				 }

				 // get hostname
				 if (checkSubstringBoundaries (stringMsg.length(), sp1 + 1, sp1 + 1 + sp2 - sp1 - 1)) {
					 hostName = stringMsg.substring(sp1 + 1, sp1 + 1 + sp2 - sp1 - 1);
					 if ( debug) {
						 System.out.println("Date: " + dateTime);
					 }
				 }
				 

				 // get app-name
				 if (checkSubstringBoundaries (stringMsg.length(), sp2 + 1, sp2 + 1 + sp3 - sp2 - 1)) {
					 appName = stringMsg.substring(sp2 + 1, sp2 + 1 + sp3 - sp2 - 1);
					 if ( debug) {
						 System.out.println("hostName: " + hostName);
					 }
				 }
				 

				 // proc-id
				 if (checkSubstringBoundaries (stringMsg.length(), sp3 + 1, sp3 + 1 + sp4 - sp3 - 1)) {
					 prodid = stringMsg.substring(sp3 + 1, sp3 + 1 + sp4 - sp3 - 1);
					 if ( debug) {
						 System.out.println("prodid: " + prodid);
					 }
				 }
				

				 // msg-id
				 if (checkSubstringBoundaries (stringMsg.length(), sp4 + 1, sp4 + 1 + sp5 - sp4 - 1)) {
					 msgid = stringMsg.substring(sp4 + 1, sp4 + 1 + sp5 - sp4 - 1);
					 if ( debug) {
						 System.out.println("msgid: " + msgid);
					 }
				 }
				 


				 if ( (sp5 + 1 <= stringMsg.length()) && stringMsg.charAt(sp5 + 1) == '-')
				 {
					 // sd is nil-value
					 msg = stringMsg.substring(sp5 + 3);
					 if ( debug) {
						 System.out.println("msg: " + msg);
					 }
				 }
				 else
				 {
					 // sd is/are present
					 int sp6 = stringMsg.indexOf("] ", sp5 + 1);
					 if (sp6 != 0)
					 {
						 msg = stringMsg.substring(sp6 + 2);
					 }
					 if ( debug) {
						 System.out.println("msg: " + msg);
					 }

					 int pos = 0;
					 int sb = 0;
					 int eb = 0;
					 int c = 0;
					 while (true)
					 {
						 
						 boolean tempVar = false;
						 
						 try
						 {
							 sb = stringMsg.indexOf('[', pos);
							 eb = stringMsg.indexOf(']', sb);

							 if ((sb > 0) && (eb > 0) && (sb < eb))
							 {
								 tempVar = true;
							 }
						 }
						 catch (java.lang.Exception e)
						 {
						 }
							 
						 if (tempVar)
						 {
							 c++;
//C# TO JAVA CONVERTER NOTE: The following 'switch' operated on a string member and was converted to Java 'if-else' logic:
//							 switch (stringMsg.Substring(sb + 1, 4))
//ORIGINAL LINE: case "time":
							 if (stringMsg.substring(sb + 1, sb + 1 + 4).equals("time"))
							 {
									 sdTimeQuality = stringMsg.substring(sb + 14, sb + 14 + eb - sb - 14);
							 }
//ORIGINAL LINE: case "orig":
							 else if (stringMsg.substring(sb + 1, sb + 1 + 4).equals("orig"))
							 {
									 sdOrigin = stringMsg.substring(sb + 7, sb + 7 + eb - sb - 7);
							 }
//ORIGINAL LINE: case "meta":
							 else if (stringMsg.substring(sb + 1, sb + 1 + 4).equals("meta"))
							 {
									 sdMeta = stringMsg.substring(sb + 5, sb + 5 + eb - sb - 5);
							 }
							 else
							 {
									 if ((stringMsg.substring(sb + 1, sb + 1 + eb - sb - 1).contains("@")) && (c <= 4))
									 {
									 sdSelf = stringMsg.substring(sb + 1, sb + 1 + eb - sb - 1);
									 }
							 }
							 pos = eb;
						 }
						 else
						 {
							 break;
						 }

					 } // end while
				 } // end else "sd is not nil"

				 // removing possible BOM char from msg: Byte[] BOM = new Byte[] { 0xEF, 0xBB, 0xBF };
				 //... is causing exceptions
				 /*
				 if (Integer.parseInt("" + msg.charAt(0)) == 65279)
				 {
					 msg = msg.substring(1);
				 }
				 */

				 // storing event in rfc5424
//				 STRProperties.IncomingEventBuffer.AddMessage(dateTime, fac, sev, 1, hostName, appName, prodid, msgid, "", msg, sdTimeQuality, sdOrigin, sdMeta, sdSelf, "", "", 0, 0, 0, 0, 0, 0, java.util.Date.UtcNow.AddMonths(12), "", "", 0);
			 } // end syslog format is OK for rfc5424
			 else
			 {
				 // syslog format not correct, failback! - storing event in rfc3164
//				 STRProperties.IncomingEventBuffer.AddMessage(java.util.Date.UtcNow, fac, sev, 0, ipEndpoint, "-", "-", "-", "", stringMsg, "", "", "", "", "", "", 0, 0, 0, 0, 0, 0, java.util.Date.UtcNow.AddMonths(12), "", "", 0);
			 }
		 }
		 else
		 {
			 // storing event in rfc3164
//			 STRProperties.IncomingEventBuffer.AddMessage(java.util.Date.UtcNow, fac, sev, 0, ipEndpoint, "", "", "", "", stringMsg, "", "", "", "", "", "", 0, 0, 0, 0, 0, 0, java.util.Date.UtcNow.AddMonths(12), "", "", 0);
		 }
		 
		 // unresolved stuff
		 if ( sysVersion != 1) {
			 hostName = inetAddress.getHostName();
			 // just a good guess from NetApp messages
			 appName = prodid + msgid;
			 msg = stringMsg;
		 }
		 SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		 String jmsTime = dateFormater.format(dateTime);
		 
		 if ( debug) { 
			 System.out.println("sysVersion: " + sysVersion);
		 	 System.out.println("dateTime: " + dateTime);
		 	 System.out.println("jmsTime: " + jmsTime);
			 System.out.println("fac: " + fac + " -> " + getFacility(fac));
			 System.out.println("sev: " + sev + " -> " + getSeverity(sev));
			 System.out.println("hostName: " + hostName);
			 System.out.println("appName: " + appName);
			 System.out.println("prodid: " + prodid);
			 System.out.println("msgid: " + msgid);
			 System.out.println("sdTimeQuality: " + sdTimeQuality);
			 System.out.println("sdOrigin: " + sdOrigin);
			 System.out.println("sdMeta: " + sdMeta);
			 System.out.println("sdSelf: " + sdSelf);
			 System.out.println("msg: " + msg);
		 }

		
		
		
		// Simple example to show how to create and send a JMS message
	    MapMessage mapMessage = null;
		try {
			mapMessage = this.jmsProducer.createMapMessage();
		
		    if (mapMessage != null) {
		    	mapMessage.setString("TYPE", "SysLog");
		    	mapMessage.setString("EVENTTIME", jmsTime); // jmsProducer.getCurrentDateAsString());
		    	mapMessage.setString("STATUS", getStatus(sev));
		    	mapMessage.setString("SEVERITY", getSeverity(sev));
		    	mapMessage.setString("VALUE", getValue(sev));
		    	mapMessage.setString("TEXT", msg);
		    	mapMessage.setString("APPLICATION-ID", appName);
		    	mapMessage.setString("FACILITY", getFacility(fac));
		    	mapMessage.setString("HOST", hostName);
		    	
		        jmsProducer.sendMessage(mapMessage);
		    }
	    } catch (javax.jms.IllegalStateException e1) {
			// session is invalid or closed - create a new one!
			e1.printStackTrace();
			LOG.info("JMS session invalid or closed");
		}
	        catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	// to which TOPIC ??
	}
	
	
	
/*	
	private static boolean findBrakets(String s, int pos, tangible.RefObject<Integer> sb, tangible.RefObject<Integer> eb)
	 {
		 try
		 {
			 sb.argvalue = s.indexOf('[', pos);
			 eb.argvalue = s.indexOf(']', sb.argvalue);

			 if ((sb.argvalue > 0) && (eb.argvalue > 0) && (sb.argvalue < eb.argvalue))
			 {
				 return true;
			 }
		 }
		 catch (java.lang.Exception e)
		 {
		 }

		 return false;
	 }
	 */

	 private boolean checkSubstringBoundaries(int length, int start, int end) {
		if ( ((start >=0) && (start < length)) && ((end > 0)&& (end <= length)) ) {
			return true;
		}
		return false;
	}

	private static int CountChar(char c, String s)
	 {
		 int pos = 0;
		 int count = 0;
		 while ((pos = s.indexOf(c, pos)) != -1)
		 {
			 count++;
			 pos++;
		 }
		 return count;
	 }

	 private static java.util.Date convertSyslogDateToDateTime(String d)
	 {
		 java.util.Date dt = new java.util.Date();
		 try
		 {
			 dt = new java.util.Date(java.util.Date.parse(d));
		 }
		 catch (java.lang.Exception e)
		 {
		 }

//		 return(dt.ToUniversalTime());
		 return(dt);
	 }
	 
	 private static String getFacility ( byte facility) {
		 String[] facilityList = {
				 "kernel messages",
				 "user-level messages",
				 "mail system",
				 "system daemons",
				 "security/authorization messages",
				 "messages generated internally by syslogd",
				 "line printer subsystem",
				 "network news subsystem",
				 "UUCP subsystem",
				 "clock daemon",
				 "security/authorization messages",
				 "FTP daemon",
				 "NTP subsystem",
				 "log audit",
				 "log alert",
				 "clock daemon (note 2)",
				 "local use 0  (local0)",
				 "local use 1  (local1)",
				 "local use 2  (local2)",
				 "local use 3  (local3)",
				 "local use 4  (local4)",
				 "local use 5  (local5)",
				 "local use 6  (local6)",
				 "local use 7  (local7)"
		 };

		 if ( ( facility >= 0) && (facility < 24)) {
			 return facilityList[ facility];
		 } else {
			 return "undefined";
		 }
	 }
	 
	 private static String getSeverity ( byte severity) {
		 String[]severityList = {
				 "FATAL",
				 "MAJOR",
				 "MINOR",
				 "ERROR",
				 "WARN",
				 "NOTICE",
				 "INFO",
				 "DEBUG"
		 };
		 if ( ( severity >= 0) && (severity < 8)) {
			 return severityList[ severity];
		 } else {
			 return "undefined";
		 }
	 }
	 
	 private static String getValue ( byte severity) {
		 String[]severityList = {
				 "Emergency",
				 "Alert",
				 "Critical",
				 "Error",
				 "Warning",
				 "Notice",
				 "Informational",
				 "Debug"
		 };
		 if ( ( severity >= 0) && (severity < 8)) {
			 return severityList[ severity];
		 } else {
			 return "undefined";
		 }
	 }
	 
	 private static String getStatus ( byte severity) {
		 String[]severityList = {
				 "Emergency",
				 "Alert",
				 "Critical",
				 "Error",
				 "Warning",
				 "Notice",
				 "Informational",
				 "Debug"
		 };
		 if ( ( severity >= 0) && (severity < 8)) {
			 return severityList[ severity];
		 } else {
			 return "";
		 }
	 }

		 
}
