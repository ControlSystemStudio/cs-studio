/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier.test;

public class UnitTestConstants {

	final public static String CONFIG_ROOT = "AlarmHandler";

	final public static int TIMER_THRESHOLD = 100;

	final public static String SYS_NAME = "Vacuum";
	final public static String PV_NAME = "DTL_Vac:Sensor1:Pressure";
	final public static String PV2_NAME = "DTL_Vac:Sensor2:Pressure";

	final public static String PV_VALUE_OK = "0";
	final public static String PV_VALUE_MINOR = "1";
	final public static String PV_VALUE_MAJOR = "2";
	final public static String PV_VALUE_INVALID = "3";

}
