package org.csstudio.platform.ui.internal.logging;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.internal.localization.Messages;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;

/**
 * A preference page for the css file log appender.
 * 
 * @author awill, swende
 */
public class FileAppenderPreferencePage extends AbstractAppenderPreferencePage {

	/**
	 * Default constructor.
	 */
	public FileAppenderPreferencePage() {
		super(SWT.NULL);
		setMessage(Messages.getString("FileAppenderPreferencePage.PAGE_TITLE")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	protected final void createFieldEditors() {
		addField(new RadioGroupFieldEditor(
				CentralLogger.PROP_LOG4J_FILE_THRESHOLD, Messages.getString("FileAppenderPreferencePage.LOG_LEVEL"), 1, //$NON-NLS-1$
				new String[][] { { "INFO", "INFO" }, { "DEBUG", "DEBUG" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						{ "WARN", "WARN" }, { "ERROR", "ERROR" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						{ "FATAL", "FATAL" } }, getFieldEditorParent(), true)); //$NON-NLS-1$ //$NON-NLS-2$

		addField(new StringFieldEditor(CentralLogger.PROP_LOG4J_FILE_PATTERN,
				Messages.getString("FileAppenderPreferencePage.PATTERN"), getFieldEditorParent())); //$NON-NLS-1$

		addField(new StringFieldEditor(
				CentralLogger.PROP_LOG4J_FILE_DESTINATION, Messages.getString("FileAppenderPreferencePage.LOG_FILE"), //$NON-NLS-1$
				getFieldEditorParent()));

		addField(new IntegerFieldEditor(
				CentralLogger.PROP_LOG4J_FILE_MAX_INDEX, Messages.getString("FileAppenderPreferencePage.BACKUP_INDEX"), //$NON-NLS-1$
				getFieldEditorParent()));
	}
}
