package org.csstudio.alarm.beast;

public class JMSNotifierMessage {
	
	/** Mandatory notifier MapMessage element: time of original event */
    final public static String EVENTTIME = "EVENTTIME";

	/** Value of the TYPE element for notifier messages.
     *  @see JMSLogMessage#TYPE
     */
	final public static String TYPE_NOTIFIER_EXE = "execute";
    final public static String TYPE_NOTIFIER_RTN = "return";
    
    final public static String ITEM_ID = "item_id";
    final public static String ITEM_NAME = "item_name";
    final public static String ITEM_PATH = "item_path";
    final public static String AA_TITLE = "aa_title";
}
