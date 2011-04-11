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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.csstudio.domain.desy.preferences.AbstractPreference;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.settings.desy.Activator;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
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
    
 // the string to separate the columns.
    private static final String COLUMN_SEPARATOR = "?";
    
    // the string to separate the rows.
    private static final String ROW_SEPARATOR = ";";
    
    private static final Logger LOG = CentralLogger.getInstance()
            .getLogger(MaintenanceRulePreference.class);
    
    public static final MaintenanceRulePreference<String> MAINTENANCE_DISPLAY_PATHS =
        new MaintenanceRulePreference<String>("MaintenanceDisplayPaths","");
    
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
    
    public static Map<String, IPath> getRTypUrlMap(){
        String value = MAINTENANCE_DISPLAY_PATHS.getValue();
        Map<String, IPath> rTypUrlMap = new HashMap<String, IPath>();
        String[] rows = value.split(ROW_SEPARATOR);
        for (String row : rows) {
            String[] columns = row.split("\\" + COLUMN_SEPARATOR);
            if(columns.length == 2) {
                IWorkspace workspace = ResourcesPlugin.getWorkspace();
                IResource findMember = workspace.getRoot().findMember(columns[1]);
                rTypUrlMap.put(columns[0], findMember.getFullPath());
            }
        }
        return rTypUrlMap;
    }
    
}
