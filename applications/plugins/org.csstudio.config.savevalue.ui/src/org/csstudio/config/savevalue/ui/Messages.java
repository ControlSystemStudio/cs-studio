package org.csstudio.config.savevalue.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.csstudio.config.savevalue.ui.messages"; //$NON-NLS-1$
	public static String CA_FILE_SERVICE_NAME;
	public static String ChangelogViewPart_DATE_MODIFIED_COLUMN;
	public static String ChangelogViewPart_ERRMSG_READ_ERROR;
	public static String ChangelogViewPart_ERRMSG_RMI_REGISTRY;
	public static String ChangelogViewPart_ERRMSG_SERVICE_NOT_AVAILABLE;
	public static String ChangelogViewPart_GET_CHANGELOG_BUTTON;
	public static String ChangelogViewPart_HOST_COLUMN;
	public static String ChangelogViewPart_IOC_FIELD_LABEL;
	public static String ChangelogViewPart_PV_COLUMN;
	public static String ChangelogViewPart_TITLE;
	public static String ChangelogViewPart_USER_COLUMN;
	public static String ChangelogViewPart_VALUE_COLUMN;
	public static String ChangelogViewPart_GET_IOC_JOB;
	public static String DATABASE_SERVICE_NAME;
	public static String EPICS_ORA_SERVICE_NAME;
	public static String SaveValueDialog_DIALOG_TITLE;
	public static String SaveValueDialog_ERRMSG_IOC_NOT_FOUND;
	public static String SaveValueDialog_ERRMSG_NO_REQUIRED_SERVICES;
	public static String SaveValueDialog_ERRMSG_NO_RMI_REGISTRY;
	public static String SaveValueDialog_ERRMSG_TEXT_IS_NOT_A_DOUBLE;
	public static String SaveValueDialog_FAILED_TIMEOUT;
	public static String SaveValueDialog_FAILED_WITH_REMOTE_EXCEPTION;
	public static String SaveValueDialog_FAILED_WITH_SERVICE_ERROR;
	public static String SaveValueDialog_IOC_FIELD_LABEL;
	public static String SaveValueDialog_NOT_BOUND;
	public static String SaveValueDialog_PV_FIELD_LABEL;
	public static String SaveValueDialog_RESULT_COLUMN;
	public static String SaveValueDialog_RESULT_ERROR_VALUE_NOT_SAVED;
	public static String SaveValueDialog_RESULT_SUCCESS;
	public static String SaveValueDialog_SAVE_BUTTON;
	public static String SaveValueDialog_STEP_COLUMN;
	public static String SaveValueDialog_SUCCESS_NEW_ENTRY;
	public static String SaveValueDialog_SUCCESS_REPLACED;
	public static String SaveValueDialog_VALUE_FIELD_LABEL;
	public static String SaveValuePreferencePage_DESCRIPTION;
	public static String SaveValuePreferencePage_REQUIRED_SERVICES_GROUP;
	public static String SaveValuePreferencePage_RMI_FIELD_LABEL;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
