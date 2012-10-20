/*******************************************************************************
* Copyright (c) 2010-2012 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier.test;

public class TestConstants {

	final public static String CONFIG_ROOT = "CODAC_AlarmHandler";
	final public static String SERVER_TOPIC = "CODAC_AlarmHandler_SERVER";
	final public static String CLIENT_TOPIC = "CODAC_AlarmHandler_CLIENT";
	final public static String NOTIFIER_TOPIC = "CODAC_AlarmHandler_NOTIFIER";
	final public static String GLOBAL_TOPIC = "GLOBAL_SERVER";
	
	final public static int THRESHOLD = 10;
	final public static String JMS_HOST = "tcp://localhost:61616";
	final public static String JMS_USER = "arnaudf";
	
	final public static String SMTP_HOST = "mail1.codac.iter.org";
	final public static String SMTP_SENDER = "notification@iter.org";
	
	final public static String SYS_NAME = "Vacuum";
	
	final public static String PV_NAME = "DTL_Vac:Sensor1:Pressure";
	final public static String PV_VALUE_OK = "0";
	final public static String PV_VALUE_MINOR = "1";
	final public static String PV_VALUE_MAJOR = "2";
	final public static String PV_VALUE_INVALID = "3";
	
	final public static String PV2_NAME = "DTL_Vac:Sensor2:Pressure";
	final public static String PV2_VALUE_OK = "0";
	final public static String PV2_VALUE_MINOR = "1";
	final public static String PV2_VALUE_MAJOR = "2";
	final public static String PV2_VALUE_INVALID = "3";
	
}
