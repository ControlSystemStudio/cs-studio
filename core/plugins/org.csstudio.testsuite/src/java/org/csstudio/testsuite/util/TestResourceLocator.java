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
 */
package org.csstudio.testsuite.util;

import java.io.File;

import javax.annotation.Nonnull;

/**
 * Util class to resolve location to test data/resources.
 * 
 * @author bknerr
 * @since 14.06.2011
 */
public final class TestResourceLocator {
    
    /**
     * Repository domains (as by convention in the CSS community).  
     * 
     * @author bknerr
     * @since 14.06.2011
     */
    public static enum RepoDomain {
        APPLICATIONS("applications"),
        CORE("core"),
        BUILD("build"),
        PRODUCTS("products");
        
        private final String _path;

        /**
         * Constructor.
         */
        private RepoDomain(@Nonnull final String path) {
            _path = path;
        }

        @Nonnull
        public String getPath() {
            return _path;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public String toString() {
            return getPath();
        }
    }
    
    /**
     * Constructor.
     */
    private TestResourceLocator() {
        // Don't instantiate
    }
    
    /**
     * Composes a file path according to path convention in our repo, which works for unit tests
     * (without bundle framework) and headless/uiplugin tests (with framework):<br/>
     * 
     * The composition looks like follows:<br/>
     * 
     * @param domain the repo domain in which the plugin resides 
     * @param pluginName the plugin name (typically set as ID in the Activator singleton)
     * @param pathUnderPluginWorkspace the file path to the resource under the plugin under test
     * @return the composed path: "./../../../$domain/plugins/$pluginName/$pathUnderPluginWorkspace"
     */
    @Nonnull
    public static String composeResourceLocationString(@Nonnull final RepoDomain domain,
                                                       @Nonnull final String pluginName,
                                                       @Nonnull final String pathUnderPluginWorkspace) {
        
        final File fromPluginToPluginPath = new File("./../../../" + domain.getPath() + "/plugins/" + pluginName);
        return new File(fromPluginToPluginPath, pathUnderPluginWorkspace).getAbsolutePath(); 
    }
    
}
