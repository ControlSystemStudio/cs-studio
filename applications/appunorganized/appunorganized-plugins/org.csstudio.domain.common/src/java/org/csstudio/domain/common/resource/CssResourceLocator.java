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
package org.csstudio.domain.common.resource;

import java.io.File;
import java.io.FileNotFoundException;


import org.csstudio.domain.common.SiteId;

/**
 * Util class to resolve location to data/resources without requiring the
 * bundle framework (and the epic fail of fragments always returning null on
 * getResource invocation).
 *
 * @author bknerr
 * @since 14.06.2011
 */
public final class CssResourceLocator {

    public static final String CONFIGURATION_REPO_PATH_KEY = "configRepoPath";

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
        private RepoDomain(final String path) {
            _path = path;
        }

            public String getPath() {
            return _path;
        }

        /**
         * {@inheritDoc}
         */
        @Override
            public String toString() {
            return getPath();
        }
    }

    /**
     * Constructor.
     */
    private CssResourceLocator() {
        // Don't instantiate
    }

    /**
     * Composes the system property for the configuration repo path given as jvm arg via the key
     * {@link CONFIGURATION_REPO_PATH_KEY} with the system property given via the key
     * {@link SiteId#JVM_ARG_KEY} and the given file name to a filepath and returns the file object
     * if existing.
     *
     * @param fileName the file name
     * @return the file found under the composed base path + site specific string + file name
     * @throws FileNotFoundException if file does not exist
     */
    public static File locateSiteSpecificResource(final String fileName)
                                                  throws FileNotFoundException {
        final String configRepoPath = System.getProperty(CONFIGURATION_REPO_PATH_KEY);
        if (configRepoPath != null) {
            return locateSiteSpecificResource(configRepoPath, fileName);
        }
        throw new FileNotFoundException("Configuration path has not been set via jvm arg with key: " + CONFIGURATION_REPO_PATH_KEY);
    }

    /**
     * Composes the base path with the system property for key {@link SiteId#JVM_ARG_KEY} and the
     * given file name to a filepath and returns the file object if existing.
     * Throws a {@link FileNotFoundException} otherwise.
     * @param basePath the base path
     * @param fileName the file name
     * @return the file found under the composed base path + site specific string + file name
     * @throws FileNotFoundException if file does not exist
     */
    public static File locateSiteSpecificResource(final String basePath,
                                                  final String fileName) throws FileNotFoundException {
        final String site = System.getProperty(SiteId.JVM_ARG_KEY);
        if (site == null) {
            throw new FileNotFoundException("Site specific path has not been set via jvm arg with key: " + SiteId.JVM_ARG_KEY);
        }
        final File file =
            new File(basePath + File.separator + site + File.separator + fileName);
        if (file.exists()) {
            return file;
        }
        throw new FileNotFoundException("File resource " + file.getAbsolutePath() + " could not be found.");
    }

    /**
     * Composes a file that is checked for existence according to path convention in our repo, which
     * works for unit tests (without bundle framework) and headless/uiplugin tests (with framework):<br/>
     *
     * The composition looks like follows:<br/>
     * the composed path: "./../../../$domain/plugins/$pluginWorkspace/$pathUnderPluginWorkspace"
     *
     * @param domain the repo domain in which the plugin resides
     * @param pluginWorkSpace the plugin name (by convention set as ID in the plugin's Activator singleton)
     * @param pathUnderPluginWorkspace the file path to the resource under the plugin under test
     * @return the file for which the path has been composed
     * @throws FileNotFoundException
     */
    public static File locateResourceFile(final RepoDomain domain,
                                          final String pluginName,
                                          final String pathUnderPluginWorkspace) throws FileNotFoundException {
        final File file = new File(composeResourceLocationString(domain, pluginName, pathUnderPluginWorkspace));
        if (!file.exists()) {
            throw new FileNotFoundException("File " + file.getAbsolutePath() + " does not exist.");
        }
        return file;
    }


    /**
     * Composes a file path according to path convention in our repo, which works for unit tests
     * (without bundle framework) and headless/uiplugin tests (with framework):<br/>
     *
     * The composition looks like follows:<br/>
     * the composed path: "./../../../$domain/plugins/$pluginWorkspace/$pathUnderPluginWorkspace"
     *
     * @param domain the repo domain in which the plugin resides
     * @param pluginWorkSpace the plugin name (by convention set as ID in the plugin's Activator singleton)
     * @param pathUnderPluginWorkspace the file path to the resource under the plugin under test
     * @return the composed path: "./../../../$domain/plugins/$pluginName/$pathUnderPluginWorkspace"
     */
    public static String composeResourceLocationString(final RepoDomain domain,
                                                       final String pluginWorkSpace,
                                                       final String pathUnderPluginWorkspace) {

        final File fromPluginToPluginPath = new File("./../../../" + domain.getPath() + "/plugins/" + pluginWorkSpace);
        return new File(fromPluginToPluginPath, pathUnderPluginWorkspace).getAbsolutePath();
    }

}
