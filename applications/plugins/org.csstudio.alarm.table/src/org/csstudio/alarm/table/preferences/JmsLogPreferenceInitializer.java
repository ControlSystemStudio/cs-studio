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
		store.setDefault(JmsLogPreferenceConstants.KEY0,"MAJOR"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY1,"MINOR"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY2,"NO_ALARM"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY3,"INVALID"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY4,"QUERY"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY5,"FATAL"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY6,"ERROR"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY7,"WARN"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY8,"INFO"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.KEY9,"DEBUG"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR0,"255,0,0"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR1,"251,215,11"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR2,"96,251,123"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR3,"255,0,255"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR4,"203,232,4"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR5,"53,134,255"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR6,"255,0,0"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR7,"251,215,11"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR8,"187,255,204"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.COLOR9,"0,255,255"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE0,"MAJOR"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE1,"MINOR"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE2,"NO_ALARM"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE3,"INVALID"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE4,"QUERY"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE5,"FATAL"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE6,"ERROR"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE7,"WARN"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE8,"INFO"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.VALUE9,"DEBUG"); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.SOUND0,""); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.SOUND1,""); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.SOUND2,""); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.SOUND3,""); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.SOUND4,""); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.SOUND5,""); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.SOUND6,""); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.SOUND7,""); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.SOUND8,""); //$NON-NLS-1$
		store.setDefault(JmsLogPreferenceConstants.SOUND9,""); //$NON-NLS-1$
	}
}
