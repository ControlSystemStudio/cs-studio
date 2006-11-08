package org.csstudio.platform.ui.internal.logging;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.internal.localization.Messages;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.swt.SWT;

/**
 * A preference page for the central css log service.
 * 
 * @author awill, swende
 */
public class LoggingPreferencePage extends AbstractAppenderPreferencePage {
	/**
	 * Default constructor.
	 */
	public LoggingPreferencePage() {
		super(SWT.NULL);
		setMessage(Messages.getString("LoggingPreferencePage.PAGE_TITLE")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	protected final void createFieldEditors() {
		addField(new BooleanFieldEditor(CentralLogger.PROP_LOG4J_CONSOLE,
				Messages.getString("LoggingPreferencePage.CONSOLE_APPENDER"), getFieldEditorParent())); //$NON-NLS-1$
		addField(new BooleanFieldEditor(CentralLogger.PROP_LOG4J_FILE,
				Messages.getString("LoggingPreferencePage.FILE_APPENDER"), getFieldEditorParent())); //$NON-NLS-1$
		addField(new BooleanFieldEditor(CentralLogger.PROP_LOG4J_JMS,
				Messages.getString("LoggingPreferencePage.JMS_APPENDER"), getFieldEditorParent()));		 //$NON-NLS-1$
	}

}
