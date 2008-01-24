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
package org.csstudio.sds.internal;

import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.internal.model.initializers.ManualSchema;
import org.csstudio.sds.model.optionEnums.CursorStyleEnum;
import org.csstudio.sds.preferences.PreferenceConstants;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * Initializes the SDS preferences.
 * 
 * @author Stefan Hofer
 * @version $Revision$
 * 
 */
public final class SdsPreferenceInitializer extends
		AbstractPreferenceInitializer {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences node = new DefaultScope()
				.getNode(SdsPlugin.PLUGIN_ID);
		initializeInitializationSchemaPreferences(node);
	}

	/**
	 * Initializes all preference settings for the control system specific
	 * initialization of widget models.
	 * 
	 * @param node
	 *            the preferences node to use
	 */
	private void initializeInitializationSchemaPreferences(
			final IEclipsePreferences node) {
		node.put(PreferenceConstants.PROP_SCHEMA, ManualSchema.ID);
		node.putInt(PreferenceConstants.PROP_GRID_SPACING, 12);
		node.putBoolean(PreferenceConstants.PROP_ANTIALIASING, false);
		node.put(PreferenceConstants.PROP_DEFAULT_CURSOR, CursorStyleEnum.ARROW.getDisplayName());
		node.put(PreferenceConstants.PROP_ENABLED_ACTION_CURSOR, CursorStyleEnum.HAND.getDisplayName());
		node.put(PreferenceConstants.PROP_DISABLED_ACTION_CURSOR, CursorStyleEnum.HAND.getDisplayName());
//		node.putBoolean(PreferenceConstants.PROP_USE_WORKSPACE_ID, true);
//		XXX Removed, because the default dialog font should be used (23.11.2007) 
//		node.putBoolean(PreferenceConstants.PROP_USE_DIALOG_FONT, true);
	}

}
