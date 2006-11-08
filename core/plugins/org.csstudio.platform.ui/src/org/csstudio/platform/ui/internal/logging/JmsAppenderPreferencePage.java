package org.csstudio.platform.ui.internal.logging;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.internal.localization.Messages;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;

/**
 * A preference page for the css JMS log appender.
 * 
 * @author awill, swende
 */
public class JmsAppenderPreferencePage extends AbstractAppenderPreferencePage {

	/**
	 * Default constructor.
	 */
	public JmsAppenderPreferencePage() {
		super(SWT.NULL);
		setMessage(Messages.getString("JmsAppenderPreferencePage.PAGE_TITLE")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	protected final void createFieldEditors() {
		addField(new RadioGroupFieldEditor(
				CentralLogger.PROP_LOG4J_JMS_THRESHOLD, Messages.getString("JmsAppenderPreferencePage.LOG_LEVEL"), 1, //$NON-NLS-1$
				new String[][] { { "INFO", "INFO" }, { "DEBUG", "DEBUG" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						{ "WARN", "WARN" }, { "ERROR", "ERROR" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						{ "FATAL", "FATAL" } }, getFieldEditorParent(), true)); //$NON-NLS-1$ //$NON-NLS-2$

		addField(new StringFieldEditor(CentralLogger.PROP_LOG4J_JMS_PATTERN,
				Messages.getString("JmsAppenderPreferencePage.PATTERN"), getFieldEditorParent())); //$NON-NLS-1$

		addField(new StringFieldEditor(CentralLogger.PROP_LOG4J_JMS_URL, Messages.getString("JmsAppenderPreferencePage.URL"), //$NON-NLS-1$
				getFieldEditorParent()));

		addField(new StringFieldEditor(CentralLogger.PROP_LOG4J_JMS_TOPIC,
				Messages.getString("JmsAppenderPreferencePage.TOPIC"), getFieldEditorParent())); //$NON-NLS-1$

		addField(new StringFieldEditor(CentralLogger.PROP_LOG4J_JMS_USER,
				Messages.getString("JmsAppenderPreferencePage.USER"), getFieldEditorParent())); //$NON-NLS-1$

		addField(new StringFieldEditor(CentralLogger.PROP_LOG4J_JMS_PASSWORD,
				Messages.getString("JmsAppenderPreferencePage.PASSWORD"), getFieldEditorParent())); //$NON-NLS-1$
	}
}
