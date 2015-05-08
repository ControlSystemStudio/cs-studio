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
 package org.csstudio.sds.components.common;

import java.util.HashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

/**
 * A shared class used to get information about the loaded switch type plugins.
 *
 * @author jbercic
 *
 */
public class SwitchPlugins {
    /**
     * The switch names and their IConfigurationElements.
     */
    public static HashMap<String,IConfigurationElement> classes_map;
    public static String [] names;
    public static String [] ids;

    /**
     * enumerate plugins
     */
    static {
        HashMap<String,String> names_map=new HashMap<String,String>();
        classes_map=new HashMap<String,IConfigurationElement>();
        IExtension [] extensions=Platform.getExtensionRegistry().getExtensionPoint("org.csstudio.sds.components.Switch").getExtensions();
        IConfigurationElement [] configs;
        for (IExtension i:extensions) {
            configs=i.getConfigurationElements();
            for (IConfigurationElement j:configs) {
                names_map.put(j.getAttribute("SwitchID"),j.getAttribute("Name"));
                classes_map.put(j.getAttribute("SwitchID"),j);
            }
        }
        names=new String[names_map.size()];
        ids=names_map.keySet().toArray(new String[names_map.size()]);
        for (int i=0;i<names_map.size();i++) {
            names[i]=names_map.get(ids[i]);
        }
    }
}
