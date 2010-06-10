package org.csstudio.diag.interconnectionServer.server;
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton, 
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

/**
 * All the global local constants.
 * 
 * @author Matthias Clausen
 */
public class PreferenceProperties {
	
	//
	// all of these properties should finally be defined
	// in a preference page of the final implementation
	// in a headles Eclipse plugin
	//
	
	public static int COMMAND_PORT_NUMBER = 18325;			// PP
	public static int ERROR_COUNT_BEFORE_SWITCH_JMS_SERVER = 10;
	
	public static int MAX_NUMBER_OF_CLIENT_THREADS = 200;
	public static int BUFFER_ZIZE = 65535;
	
	public static String JMS_LOG_CONTEXT = "LOG";
	public static String JMS_ALARM_CONTEXT = "ALARM";
	public static String JMS_PUT_LOG_CONTEXT = "PUT_LOG";
	public static String JMS_SNL_LOG_CONTEXT = "SNL_LOG";
	public static String JMS_SIM_CONTEXT = "SIM";
	public static String JMS_ADIS_CONTEXT = "ADIS";
	
	public static String COMMAND_TAKE_OVER 			= "takeOver";
	public static String COMMAND_DISCONNECT 		= "disconnect";
	public static String COMMAND_SEND_ALARM			= "sendAlarm";
	public static String COMMAND_SEND_ALL_ALARMS 	= "sendAllAlarms";
	public static String COMMAND_SEND_STATUS 		= "sendStatus";
	public static String COMMAND_SEND_SIM			= "sendSim";
	public static String COMMAND_SEND_ADIS	 		= "sendAdis";
	
	public static final int COMMAND_TAKE_OVER_I 			= 0;
	public static final int COMMAND_DISCONNECT_I 			= 1;
	public static final int COMMAND_SEND_ALARM_I			= 2;
	public static final int COMMAND_SEND_ALL_ALARMS_I 	= 3;
	public static final int COMMAND_SEND_STATUS_I 		= 4;
	
	public static String[]	COMMAND_LIST = {COMMAND_TAKE_OVER,
		COMMAND_DISCONNECT, COMMAND_SEND_ALARM, 
		COMMAND_SEND_ALL_ALARMS, COMMAND_SEND_STATUS, COMMAND_SEND_SIM, COMMAND_SEND_ADIS};
	
	public static final int	TIME_TO_GET_ANSWER_FROM_IOC_AFTER_COMMAND	= 1000; // 8.7.2008 10 -> 3 sec MCL
	
	public static final int BEACON_TIMEOUT = 15000;	// 15 sec
	
	public static final int IOC_BEACON_TIMEOUT = 600;	// 0.6 sec
	public static final int IOC_MESSAGE_TIMEOUT = 1000;	// 1 sec
	
	public static String JMS_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	
	/*
	 * client request thread properties
	 */
	public static final int CLIENT_REQUEST_THREAD_MAX_NUMBER_ALARM_LIMIT			= 50;
	
	public static final int MAX_TIME_DELAY_FOR_STATUS_MESSSAGES = 750; // 750ms for status messages
	public static final int MAX_WAIT_UNTIL_SEND_ALL_ALARMS = 300000; // wait max 5 minutes after we reconnected
	

}
