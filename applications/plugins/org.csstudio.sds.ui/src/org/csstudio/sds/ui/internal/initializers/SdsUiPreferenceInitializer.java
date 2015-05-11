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
 package org.csstudio.sds.ui.internal.initializers;

import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.internal.preferences.PreferenceConstants;
import org.csstudio.sds.ui.internal.editor.DisplayEditor;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * Initializes the SDSUi preferences.
 * @author Kai Meyer
 *
 * @deprecated is never called!!!
 *             The values are set in the {@link SdsPlugin}
 */
public final class SdsUiPreferenceInitializer extends AbstractPreferenceInitializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeDefaultPreferences() {
        IEclipsePreferences node = new DefaultScope()
            .getNode(SdsPlugin.PLUGIN_ID);
        initializeDisplayOptionsPreferences(node);
    }

    /**
     * Initializes the preference settings for the display options that are
     * used by the SDS.
     *
     * @param node
     *            the preferences node to use
     */
    private void initializeDisplayOptionsPreferences(
            final IEclipsePreferences node) {
        node.put(PreferenceConstants.PROP_GRID_SPACING,
                String.valueOf(DisplayEditor.GRID_SPACING));

        node.put(PreferenceConstants.PROP_ANTIALIASING, "false");

//        node.put(PreferenceConstants.PROP_USE_WORKSPACE_ID, "true");
//        XXX Removed, because the default dialog font should be used (23.11.2007)
//        node.put(PreferenceConstants.PROP_USE_DIALOG_FONT, "true");
    }

}
