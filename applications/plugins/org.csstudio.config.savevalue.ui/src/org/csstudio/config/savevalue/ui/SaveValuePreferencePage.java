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

package org.csstudio.config.savevalue.ui;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference Page for the Save Value Client plug-in.
 * 
 * @author Joerg Rathlev
 */
public class SaveValuePreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	
	/**
	 * Creates this preference page.
	 */
	public SaveValuePreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(Messages.SaveValuePreferencePage_DESCRIPTION);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void createFieldEditors() {
		addField(new StringFieldEditor(PreferenceConstants.RMI_REGISTRY_SERVER,
				Messages.SaveValuePreferencePage_RMI_FIELD_LABEL, getFieldEditorParent()));
		
		Group requiredServicesGroup = new Group(getFieldEditorParent(), SWT.SHADOW_ETCHED_IN);
		requiredServicesGroup.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		requiredServicesGroup.setLayout(new RowLayout(SWT.VERTICAL));
		requiredServicesGroup.setText(Messages.SaveValuePreferencePage_REQUIRED_SERVICES_GROUP);
		addField(new BooleanFieldEditor(PreferenceConstants.EPIS_ORA_REQUIRED, Messages.EPICS_ORA_SERVICE_NAME, requiredServicesGroup));
		addField(new BooleanFieldEditor(PreferenceConstants.DATABASE_REQUIRED, Messages.DATABASE_SERVICE_NAME, requiredServicesGroup));
		addField(new BooleanFieldEditor(PreferenceConstants.CA_FILE_REQUIRED, Messages.CA_FILE_SERVICE_NAME, requiredServicesGroup));
	}

	/**
	 * {@inheritDoc}
	 */
	public void init(final IWorkbench workbench) {
	}

}
