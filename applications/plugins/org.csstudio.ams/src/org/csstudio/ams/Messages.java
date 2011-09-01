
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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	
    private static final String BUNDLE_NAME = "org.csstudio.ams.messages";

	// database settings
	public static String Pref_Database;
	public static String Pref_ConfigDBCon;
	public static String Pref_ConfigDBUser;
	public static String Pref_ConfigDBPassword;
	public static String Pref_AppDBCon;
	public static String Pref_AppDBUser;
	public static String Pref_AppDBPassword;

	// filter key field of message
	public static String Pref_FilterKeyFields;
	public static String Pref_FilterKeyFieldEnterOne;
	public static String Pref_FilterKeyField;

	public static String Pref_Password_ShutdownAction;
	
	// jms communication
	public static String P_JMS_SOURCES;

	// external
	public static String P_JMS_EXTERN_CONNECTION_FACTORY_CLASS;
	public static String P_JMS_EXTERN_PROVIDER_URL_1;
    public static String P_JMS_EXTERN_PROVIDER_URL_2;
    public static String P_JMS_EXTERN_SENDER_PROVIDER_URL;
	public static String P_JMS_EXTERN_CONNECTION_FACTORY;
	public static String P_JMS_EXTERN_CREATE_DURABLE;
	
	// ams internal
	public static String P_JMS_AMS_CONNECTION_FACTORY_CLASS;
	public static String P_JMS_AMS_PROVIDER_URL_1;
    public static String P_JMS_AMS_PROVIDER_URL_2;
    public static String P_JMS_AMS_SENDER_PROVIDER_URL;
	public static String P_JMS_AMS_CONNECTION_FACTORY;
	public static String P_JMS_AMS_CREATE_DURABLE;
	
	// free topics
	public static String P_JMS_FREE_TOPIC_CONNECTION_FACTORY_CLASS;
	public static String P_JMS_FREE_TOPIC_CONNECTION_FACTORY;

	// external topics
	public static String P_JMS_EXT;
	public static String P_JMS_EXT_TOPIC_ALARM;
	public static String P_JMS_EXT_TSUB_ALARM_FMR;
	public static String P_JMS_EXT_TOPIC_COMMAND;
	public static String P_JMS_EXT_TSUB_CMD_FMR_START_RELOAD;
	public static String P_JMS_EXT_TOPIC_STATUSCHANGE;

	// ams internal topics
	public static String P_JMS_AMS;
	public static String P_JMS_AMS_TOPIC_DISTRIBUTOR;
	public static String P_JMS_AMS_TSUB_DISTRIBUTOR;
	public static String P_JMS_AMS_TOPIC_MESSAGEMINDER;
	public static String P_JMS_AMS_TSUB_MESSAGEMINDER;
	public static String P_JMS_AMS_TOPIC_REPLY;
	public static String P_JMS_AMS_TSUB_REPLY;

	public static String P_JMS_AMS_TOPIC_SMS_CONNECTOR;
	public static String P_JMS_AMS_TSUB_SMS_CONNECTOR;
	public static String P_JMS_AMS_TOPIC_EMAIL_CONNECTOR;
	public static String P_JMS_AMS_TSUB_EMAIL_CONNECTOR;
	public static String P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR;
	public static String P_JMS_AMS_TSUB_VOICEMAIL_CONNECTOR;
	public static String P_JMS_AMS_TOPIC_JMS_CONNECTOR;
	public static String P_JMS_AMS_TSUB_JMS_CONNECTOR;
	public static String P_JMS_AMS_TOPIC_FORWARD;
	
	public static String P_JMS_AMS_TOPIC_COMMAND;
	public static String P_JMS_AMS_TSUB_CMD_FMR_RELOAD_END;

    public static String P_JMS_AMS_TOPIC_MONITOR;
	
	public static String FilterConditionStringUI_lblKeyValue;
	public static String FilterConditionStringUI_lblKeyValueType;
	public static String FilterConditionStringUI_lblOperator;
	public static String FilterConditionStringUI_lblValue;

	public static String FilterConditionStringUI_String;
	public static String FilterConditionStringUI_Number;
	public static String FilterConditionStringUI_Time;
	
	public static String FilterConditionProcessVaribaleBasedUI_ChannelLabelText;
	public static String FilterConditionProcessVaribaleBasedUI_SuggRetTypeLabelText;
	public static String FilterConditionProcessVaribaleBasedUI_OperatorLabelText;
	public static String FilterConditionProcessVaribaleBasedUI_CompareValueLabelText;
	public static String FilterConditionProcessVaribaleBasedUI_Error_Title;
	public static String FilterConditionProcessVaribaleBasedUI_Error_No_Connection_With_Reason;
	public static String FilterConditionProcessVaribaleBasedUI_Error_No_Connection;
	public static String FilterConditionProcessVaribaleBasedUI_Unknown;
	public static String FilterConditionProcessVaribaleBasedUI_No_Error_Title;
	public static String FilterConditionProcessVaribaleBasedUI_Connection_Successful;
	

	public static String OPERATOR_TEXT_EQUAL;
	public static String OPERATOR_TEXT_NOT_EQUAL;

	public static String OPERATOR_NUMERIC_LT;
	public static String OPERATOR_NUMERIC_LT_EQUAL;
	public static String OPERATOR_NUMERIC_EQUAL;
	public static String OPERATOR_NUMERIC_GT_EQUAL;
	public static String OPERATOR_NUMERIC_GT;
	public static String OPERATOR_NUMERIC_NOT_EQUAL;

	public static String OPERATOR_TIME_BEFORE;
	public static String OPERATOR_TIME_BEFORE_EQUAL;
	public static String OPERATOR_TIME_EQUAL;
	public static String OPERATOR_TIME_AFTER_EQUAL;
	public static String OPERATOR_TIME_AFTER;
	public static String OPERATOR_TIME_NOT_EQUAL;

	public static String FilterConditionStringUI_Error_InputFormatNumber_Msg;
	public static String FilterConditionStringUI_Error_InputFormatNumber_Title;
	public static String FilterConditionStringUI_Error_InputFormatTime_Msg;
	public static String FilterConditionStringUI_Error_InputFormattime_Title;
	public static String FilterConditionStringUI_Error_MandantoryField_Msg;
	public static String FilterConditionStringUI_Error_MandantoryField_Title;

	public static String FilterConditionArrayStringUI_lblKeyValue;
	public static String FilterConditionArrayStringUI_lblKeyValueType;
	public static String FilterConditionArrayStringUI_lblOperator;
	public static String FilterConditionArrayStringUI_lblValue;

	public static String FilterConditionArrayStringUI_String;
	public static String FilterConditionArrayStringUI_Number;
	public static String FilterConditionArrayStringUI_Time;

	public static String FilterConditionArrayStringUI_Error_InputFormatNumber_Msg;
	public static String FilterConditionArrayStringUI_Error_InputFormatNumber_Title;
	public static String FilterConditionArrayStringUI_Error_InputFormatTime_Msg;
	public static String FilterConditionArrayStringUI_Error_InputFormattime_Title;
	public static String FilterConditionArrayStringUI_Error_MandantoryField_Msg;
	public static String FilterConditionArrayStringUI_Error_MandantoryField_Title;

	public static String FilterConditionTimeBasedUI_pnStartCondition;
	public static String FilterConditionTimeBasedUI_lblStartKeyValue;
	public static String FilterConditionTimeBasedUI_lblStartKeyValueType;
	public static String FilterConditionTimeBasedUI_lblStartOperator;
	public static String FilterConditionTimeBasedUI_lblStartValue;

	public static String FilterConditionTimeBasedUI_pnOptions;
	public static String FilterConditionTimeBasedUI_lblTimePeriod;
	public static String FilterConditionTimeBasedUI_optTimeRemoval;
	public static String FilterConditionTimeBasedUI_optTimeConfirm;

	public static String FilterConditionTimeBasedUI_pnConditionRemoval;
	public static String FilterConditionTimeBasedUI_pnConditionConfirm;
	public static String FilterConditionTimeBasedUI_lblConfirmKeyValue;
	public static String FilterConditionTimeBasedUI_lblConfirmKeyValueType;
	public static String FilterConditionTimeBasedUI_lblConfirmOperator;
	public static String FilterConditionTimeBasedUI_lblConfirmValue;

	public static String FilterConditionTimeBasedUI_String;
	public static String FilterConditionTimeBasedUI_Number;
	public static String FilterConditionTimeBasedUI_Time;

	public static String FilterConditionTimeBasedUI_Error_InputFormatNumber_Msg;
	public static String FilterConditionTimeBasedUI_Error_InputFormatNumber_Title;
	public static String FilterConditionTimeBasedUI_Error_InputFormatTime_Msg;
	public static String FilterConditionTimeBasedUI_Error_InputFormattime_Title;
	public static String FilterConditionTimeBasedUI_Error_MandantoryField_Msg;
	public static String FilterConditionTimeBasedUI_Error_MandantoryField_Title;
	public static String FilterConditionTimeBasedUI_Error_TimePeriod_Msg;
	public static String FilterConditionTimeBasedUI_Error_TimePeriod_Title;

	public static String FilterConditionProcessVaribaleBasedUI_Error_Dialog_Title;
	public static String FilterConditionUI_Error_Dialog_Message_Prefix;

	public static String FilterConditionProcessVaribaleBasedUI_Error_MissingValidChannelName;
	public static String FilterConditionProcessVaribaleBasedUI_Error_MissingSuggestedTypeSelection;
	public static String FilterConditionProcessVaribaleBasedUI_Error_MissingOperatorSelection;
	public static String FilterConditionProcessVaribaleBasedUI_Error_InvalidCompValue;
	public static String FilterConditionProcessVaribaleBasedUI_ChannelVerifierButtonText;
	
	public static String FilterConditionConjunction_LABEL_FIRST_OPERAND;
	public static String FilterConditionConjunction_LABEL_SECOND_OPERAND;
	public static String FilterConditionConjunction_CHECK_BUTTON_TEXT;
	
	public static String FilterConditionConjunctionUI_Error_MissingFirstCondition;
	public static String FilterConditionConjunctionUI_Error_MissingSecondCondition;
	public static String FilterConditionConjunctionUI_Error_Dialog_Title;
	public static String FilterConditionConjunctionUI_Error_Cycle_Detected;
	public static String FilterConditionConjunctionUI_Error_Not_Saved;
	public static String FilterConditionConjunctionUI_Error_No_DB_Connection;
	public static String FilterConditionConjunctionUI_Error_Reading_Database;
	public static String FilterConditionConjunctionUI_No_Error_Dialog_Title;
	public static String FilterConditionConjunctionUI_No_Error_Dialog_Message;
	public static String FilterConditionConjunctionUI_Warn_Dialog_Title;
	public static String FilterConditionConjunctionUI_Warn_Conditions_Equal;

	public static String FilterConditionStringBasedUI_DisplayName;
	public static String FilterConditionTimeBasedUI_DisplayName;
	public static String FilterConditionArrayStringUI_DisplayName;
	public static String FilterConditionProcessVaribaleBasedUI_DisplayName;
	public static String FilterConditionOrConjunctionUI_DisplayName;
	
	/**
	 * Initializes the given class with the values
	 * from the specified message bundle.
	 */
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * The localization messages ressource bundle.
	 */
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	/**
	 * This method does nothing. 
	 */
	private Messages() {
	    // Avoid instantiation
	}
	
	/**
	 * Gets a string for the given key from this resource bundle
	 * or one of its parents. 
	 * 
	 * @param key	String
	 * @return String
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
