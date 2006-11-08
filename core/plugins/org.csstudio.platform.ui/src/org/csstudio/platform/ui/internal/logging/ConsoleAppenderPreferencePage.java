package org.csstudio.platform.ui.internal.logging;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.CSSPlatformUiPlugin;
import org.csstudio.platform.ui.internal.localization.Messages;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;

/**
 * A preference page for the css console log appender.
 * 
 * @author awill, swende
 */
public class ConsoleAppenderPreferencePage extends
		AbstractAppenderPreferencePage {

	/**
	 * Default constructor.
	 */
	public ConsoleAppenderPreferencePage() {
		super(SWT.NULL);
		setMessage(Messages.getString("ConsoleAppenderPreferencePage.PAGE_TITLE")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	protected final void createFieldEditors() {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),
				CSSPlatformUiPlugin.ID + ".console"); //$NON-NLS-1$

		addField(new RadioGroupFieldEditor(
				CentralLogger.PROP_LOG4J_CONSOLE_THRESHOLD, Messages.getString("ConsoleAppenderPreferencePage.LOG_LEVEL"), 1, //$NON-NLS-1$
				new String[][] { { "INFO", "INFO" }, { "DEBUG", "DEBUG" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						{ "WARN", "WARN" }, { "ERROR", "ERROR" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						{ "FATAL", "FATAL" } }, getFieldEditorParent(), true)); //$NON-NLS-1$ //$NON-NLS-2$

		addField(new StringFieldEditor(
				CentralLogger.PROP_LOG4J_CONSOLE_PATTERN, Messages.getString("ConsoleAppenderPreferencePage.PATTERN"), //$NON-NLS-1$
				getFieldEditorParent()));
	}
}
