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
 package org.csstudio.alarm.table.preferences;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.platform.libs.jms.preferences.PreferenceConstants;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;


/**
 * Class used to initialize default preference values.
 */
public class JmsLogPreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = JmsLogsPlugin.getDefault().getPreferenceStore();
		store.setDefault(JmsLogPreferenceConstants.KEY0,"INVALID"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY1,"MAJOR"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY2,"MINOR"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY3,"NO_ALARM"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY4,"4"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY5,"5"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY6,"6"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY7,"7"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY8,"8"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY9,"9"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR0,"191,191,191"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR1,"252,7,14"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR2,"251,215,11"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR3,"96,251,123"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR4,"201,201,201"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR5,"201,201,201"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR6,"201,201,201"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR7,"201,201,201"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR8,"201,201,201"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR9,"201,201,201"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE0,"INVALID"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE1,"MAJOR"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE2,"MINOR"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE3,"NO_ALARM"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE4,"NOT DEFINED"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE5,"NOT DEFINED"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE6,"NOT DEFINED"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE7,"NOT DEFINED"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE8,"NOT DEFINED"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE9,"NOT DEFINED"); //$NON-NLS-1$
	}
}
