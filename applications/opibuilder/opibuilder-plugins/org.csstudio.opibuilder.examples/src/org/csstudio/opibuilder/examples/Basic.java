/**
 *
 */
package org.csstudio.opibuilder.examples;

import java.net.URL;
import java.util.Collections;

import org.csstudio.examples.SampleSet;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 *
 * @author shroffk
 *
 */
public class Basic implements SampleSet {

    /**
     *
     */
    public Basic() {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.csstudio.opibuilder.samples.SampleSet#getDirectory()
     */
    @Override
    public URL getDirectoryURL() {
    IPath path = new Path("examples/BOY Examples");
    URL url = FileLocator.find(Activator.getDefault().getBundle(),
        path, Collections.EMPTY_MAP);
    return url;
    }
}
