/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.alarm.table.internal.localization;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Access to the localization message ressources within this plugin.
 *
 * @author Alexander Will
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.csstudio.alarm.table.internal.localization.messages"; //$NON-NLS-1$
//	private static final String BUNDLE_NAME = "org.csstudio.alarm.table.messages"; //$NON-NLS-1$

	public static String ExpertSearchDialog_Button_And;

	public static String ExpertSearchDialog_Button_Or;
    
    public static String ExpertSearchDialog_Button_Del;

	public static String ExpertSearchDialog_Label_And;

	public static String ExpertSearchDialog_Label_Or;
    
	public static String JMSPreferencePage_ALARM_PRIMERY_SERVER;

	public static String JMSPreferencePage_ALARM_SECONDARY_SERVER;

	public static String JMSPreferencePage_ALARM_CONTEXT_FACTORY;

	public static String JMSPreferencePage_ALARM_PROVIDER_URL;

	public static String JMSPreferencePage_ALARM_QUEUE_NAME;

    public static String JMSPreferencePage_ALARM_SENDER_URL;
    
	public static String JMSPreferencePage_LOG_PRIMERY_SERVER;

	public static String JMSPreferencePage_LOG_SECONDARY_SERVER;

	public static String JMSPreferencePage_LOG_CONTEXT_FACTORY;

	public static String JMSPreferencePage_LOG_PROVIDER_URL;

	public static String JMSPreferencePage_LOG_QUEUE_NAME;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String AlarmViewerPreferencePage_column;

	public static String AlarmViewerPreferencePage_columnNamesMessageKeys;

	public static String AlarmViewerPreferencePage_enterColumnName;

	public static String column;

	public static String columnNamesMessageKeys;

	public static String ExpertSearchDialog_expertButton;

	public static String ExpertSearchDialog_end;

	public static String ExpertSearchDialog_endTime;

	public static String ExpertSearchDialog_search;

	public static String ExpertSearchDialog_start;

	public static String ExpertSearchDialog_startEndMessage;

	public static String ExpertSearchDialog_startTime;

	public static String JmsLogPreferencePage_color;

	public static String JmsLogPreferencePage_key;

	public static String JmsLogPreferencePage_severityKeys;

	public static String JmsLogPreferencePage_value;

	public static String LogArchiveViewerPreferencePage_column;

	public static String LogArchiveViewerPreferencePage_columnNamesMessageKeys;

	public static String LogArchiveViewerPreferencePage_dateFormat;

	public static String LogArchiveViewerPreferencePage_maxAnswerSize;

	public static String LogArchiveViewerPreferencePage_javaDateFormat;

	public static String LogArchiveViewerPreferencePage_newColumnName;

	public static String LogViewArchive_3days;

	public static String LogViewArchive_day;

	public static String LogViewArchive_expert;

	public static String LogViewArchive_from;

	public static String LogViewArchive_period;

	public static String LogViewArchive_to;

	public static String LogViewArchive_user;

	public static String LogViewArchive_week;

	public static String newColumnName;

	/**
	 * The localzation messages ressource bundle.
	 */
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
