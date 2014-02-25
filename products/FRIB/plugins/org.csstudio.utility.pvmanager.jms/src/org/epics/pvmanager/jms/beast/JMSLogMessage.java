/**
 * *****************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *****************************************************************************
 */
package org.epics.pvmanager.jms.beast;

/**
 * Description of a JMS 'LOG' message
 *
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public interface JMSLogMessage {

    /**
     * Date format for JMS time info
     */
    final public static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    /**
     * Default name of the JMS Queue used for log messages.
     *
     * @see #TYPE_LOG
     */
    final public static String DEFAULT_TOPIC = "LOG";
    /**
     * Mandatory MapMessage element: type
     */
    final public static String TYPE = "TYPE";
    /**
     * Value of the TYPE element.
     *
     * @see #TYPE
     * @see #DEFAULT_TOPIC
     */
    final public static String TYPE_LOG = "log";
    /**
     * Mandatory MapMessage element: content
     */
    final public static String TEXT = "TEXT";
    /**
     * Mandatory MapMessage element: Severity of the message
     */
    final public static String SEVERITY = "SEVERITY";
    /**
     * Mandatory MapMessage element: time of message creation
     */
    final public static String CREATETIME = "CREATETIME";
    /**
     * Optional MapMessage element: Java class that generated the event
     */
    final public static String CLASS = "CLASS";
    /**
     * Optional MapMessage element: Java method that generated the event. Also
     * used for alarm messages, where it's the PV name
     */
    final public static String NAME = "NAME";
    /**
     * Optional MapMessage element: ID of application that generated the event
     */
    final public static String APPLICATION_ID = "APPLICATION-ID";
    /**
     * Optional MapMessage element: host that generated the event
     */
    final public static String HOST = "HOST";
    /**
     * Optional MapMessage element: user that generated the event
     */
    final public static String USER = "USER";
}
