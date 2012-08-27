/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.alarm.treeview;

import javax.annotation.CheckForNull;

import javax.annotation.Nonnull;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class of the LdapTree-Plug-In. This manages the plug-in's lifecycle.
 *
 * @author Joerg Rathlev
 */
public final class AlarmTreePlugin extends AbstractUIPlugin {
    
    public static final String PLUGIN_ID = "org.csstudio.alarm.treeview";
    
    private static AlarmTreePlugin INSTANCE;
    
    // Returns the shared instance.
    @Nonnull
    public static AlarmTreePlugin getDefault() {
        return INSTANCE;
    }
    
    /**
     * Don't instantiate.
     * Called by framework.
     */
    public AlarmTreePlugin() {
        if (INSTANCE != null) {
            throw new IllegalStateException("TreeModelActivator " + PLUGIN_ID
                    + " does already exist.");
        }
        INSTANCE = this; // Antipattern is required by the framework!
    }
    
    @Override
    public void start(@Nonnull final BundleContext context) throws Exception {
    	super.start(context);
    }
    
    @Override
    public void stop(@Nonnull final BundleContext context) throws Exception {
    	super.stop(context);
    }
    
    /**
     * Returns an image descriptor for the image file at the given plug-in relative path.
     *
     * @param path
     * @return the image descriptor
     */
    @CheckForNull
    public static ImageDescriptor getImageDescriptor(@Nonnull final String path) {
        return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
    }
    
}
