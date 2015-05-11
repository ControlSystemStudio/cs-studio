/**
 *
 */
package org.csstudio.examples;

import java.net.URL;

/**
 * Interface for the extension point to contribute sample screens to the
 * examples that can be install in CS-Studio
 *
 * The given sample screens will be installed in a directory under Examples
 *
 * @author shroffk
 *
 */
public interface SampleSet {

    public static final String ID = "org.csstudio.examples.sampleset";

    /**
     * Return the URL to the directory of the sample set
     *
     * @return
     */
    public URL getDirectoryURL();

}
