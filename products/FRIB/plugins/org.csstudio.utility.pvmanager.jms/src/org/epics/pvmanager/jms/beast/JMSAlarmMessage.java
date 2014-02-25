/**
 * *****************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************
 */
package org.epics.pvmanager.jms.beast;

/**
 * Properties of a JMS alarm message.
 *
 * @author Kay Kasemir
 *
 * @see JMSLogMessage
 */
@SuppressWarnings("nls")
public class JMSAlarmMessage {

    /**
     * Value of the TYPE element for alarm messages.
     *
     * @see JMSLogMessage#TYPE
     */
    final public static String TYPE_ALARM = "alarm";
    /**
     * Mandatory alarm MapMessage element: time of original event
     */
    final public static String EVENTTIME = "EVENTTIME";
    /**
     * Property that contains the alarm configuration name (root element name)
     */
    final public static String CONFIG = "CONFIG";
    /**
     * Value for TEXT that indicates an idle message
     */
    final public static String TEXT_IDLE = "IDLE";
    /**
     * Value for TEXT that indicates an idle message in maintenance mode
     */
    final public static String TEXT_IDLE_MAINTENANCE = "IDLE_MAINTENANCE";
    /**
     * Value for TEXT that requests change in mode. VALUE will contain detail on
     * requested mode change
     */
    final public static String TEXT_MODE = "MODE";
    /**
     * VALUE for TEXT_MODE message to request normal mode
     */
    final public static String VALUE_MODE_NORMAL = "NORMAL";
    /**
     * VALUE for TEXT_MODE message to request maintenance mode
     */
    final public static String VALUE_MODE_MAINTENANCE = "MAINTENANCE";
    /**
     * Value for TEXT that indicates a state change NAME will contain PV name
     */
    final public static String TEXT_STATE = "STATE";
    /**
     * Value for TEXT that indicates a state change while in maintenance mode
     * NAME will contain PV name
     */
    final public static String TEXT_STATE_MAINTENANCE = "STATE_MAINTENANCE";
    /**
     * Value for TEXT that indicates a configuration change. NAME will contain
     * path to item that was added, removed, reconfigured, or null for an
     * overall change.
     */
    final public static String TEXT_CONFIG = "CONFIG";
    /**
     * Value for TEXT that indicates a PV was enabled. NAME will contain path to
     * item that was enabled.
     */
    final public static String TEXT_ENABLE = "ENABLE";
    /**
     * Value for TEXT that indicates a PV was disabled. NAME will contain path
     * to item that was disabled.
     */
    final public static String TEXT_DISABLE = "DISABLE";
    /**
     * Value for TEXT that performs acknowledgment. NAME will contain PV name
     */
    final public static String TEXT_ACKNOWLEDGE = "ACK";
    /**
     * Value for TEXT that performs un-acknowledgment NAME will contain PV name
     */
    final public static String TEXT_UNACKNOWLEDGE = "UN-ACK";
    /**
     * Value for TEXT that performs some server debugging
     */
    final public static String TEXT_DEBUG = "DEBUG";
    /**
     * Message property that holds alarm status
     */
    final public static String STATUS = "STATUS";
    /**
     * Severity of the current value, may be lower than the latched alarm
     */
    final public static String CURRENT_SEVERITY = "CURRENT_SEVERITY";
    /**
     * Message property that holds the current status
     */
    final public static String CURRENT_STATUS = "CURRENT_STATUS";
    /**
     * Value that caused the severity/message update
     */
    final public static String VALUE = "VALUE";
}
