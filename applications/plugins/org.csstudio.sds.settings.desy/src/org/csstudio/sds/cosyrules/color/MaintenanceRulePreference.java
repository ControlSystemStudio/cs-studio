/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.sds.cosyrules.color;

import org.csstudio.domain.common.preferences.AbstractPreference;
import org.csstudio.sds.settings.desy.Activator;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

/**
 * TODO (Rickens Helge) : 
 * 
 * @author Rickens Helge
 * @author $Author: $
 * @since 24.03.2011

 */
public class MaintenanceRulePreference<T> extends AbstractPreference<T> {
    
    public static final MaintenanceRulePreference<IPath> MAINTENANCE_UNKNOWN_DISPLAY_PATH =
        new MaintenanceRulePreference<IPath>("MaintenanceUnknownDisplayPath", ResourcesPlugin.getWorkspace().getRoot().getFullPath());
    public static final MaintenanceRulePreference<IPath> MAINTENANCE_DISPLAY_PATH =
        new MaintenanceRulePreference<IPath>("MaintenanceDisplayPath", ResourcesPlugin.getWorkspace().getRoot().getFullPath());
    public static final MaintenanceRulePreference<String> MAINTENANCE_PRE_FILE_NAME =
        new MaintenanceRulePreference<String>("MaintenancePreFileName","");
    
    /**
     * Constructor.
     */
    public MaintenanceRulePreference(final String keyAsString, final T defaultValue) {
        super(keyAsString, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Class<? extends AbstractPreference<T>> getClassType() {
        return (Class<? extends AbstractPreference<T>>) MaintenanceRulePreference.class;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getPluginID() {
        return Activator.PLUGIN_ID;
    }
    
}
