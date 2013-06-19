/**
 * 
 */
package org.csstudio.opibuilder.examples;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;

import org.csstudio.examples.SampleSet;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.URIUtil;

/**
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
	try {
	    IPath path = new Path("examples");
	    URL url = FileLocator.find(Activator.getDefault().getBundle(),
		    path, Collections.EMPTY_MAP);
	    File dir = new File(FileLocator.toFileURL(url).toURI());
	    return url;
	} catch (URISyntaxException | IOException e) {
	    e.printStackTrace();
	    return null;
	}
    }
}
