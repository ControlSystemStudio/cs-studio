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
package org.csstudio.platform.ui.internal.logging;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.internal.localization.Messages;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;

/**
 * A preference page for the css file log appender.
 * 
 * @author Alexander Will, Sven Wende
 */
public class FileAppenderPreferencePage extends AbstractAppenderPreferencePage {

	/**
	 * Default constructor.
	 */
	public FileAppenderPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
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

		addField(new FileFieldEditor(
				CentralLogger.PROP_LOG4J_FILE_DESTINATION, Messages.getString("FileAppenderPreferencePage.LOG_FILE"), //$NON-NLS-1$
				getFieldEditorParent()));

		addField(new IntegerFieldEditor(
				CentralLogger.PROP_LOG4J_FILE_MAX_INDEX, Messages.getString("FileAppenderPreferencePage.BACKUP_INDEX"), //$NON-NLS-1$
				getFieldEditorParent()));
	}
}
