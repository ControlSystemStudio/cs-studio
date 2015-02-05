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
 package org.csstudio.dal.ui.internal.logging;

import org.csstudio.platform.SimpleDalPluginActivator;
import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.Bundle;

public class ControlSystemPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public ControlSystemPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		setMessage("Set the default control system");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createFieldEditors() {
		String[][] labelsAndValues = new String[ControlSystemEnum.valuesShown().length][2];
		for (int i = 0; i < ControlSystemEnum.valuesShown().length; i++) {
			labelsAndValues[i] = new String[] {
					ControlSystemEnum.valuesShown()[i].name(),
					ControlSystemEnum.valuesShown()[i].name() };
		}
		RadioGroupFieldEditor radioFields = new RadioGroupFieldEditor(
				ProcessVariableAdressFactory.PROP_CONTROL_SYSTEM, "Control Systems", 1,
				labelsAndValues, getFieldEditorParent());

		addField(radioFields);

		BooleanFieldEditor bfe = new BooleanFieldEditor(
				ProcessVariableAdressFactory.PROP_ASK_FOR_CONTROL_SYSTEM,
				"Ask for the right control system, each time a user drops a text String into CSS.",
				getFieldEditorParent());
		
		addField(bfe);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IPreferenceStore doGetPreferenceStore() {
        InstanceScope context = new InstanceScope();
        Bundle bundle = SimpleDalPluginActivator.getDefault().getBundle();
        String symbolicName = bundle
                .getSymbolicName();
        return new ScopedPreferenceStore(context, symbolicName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void init(final IWorkbench workbench) {
	    // nothing to do
	}

}
