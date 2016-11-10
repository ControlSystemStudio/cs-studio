/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.saverestore.git;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Optional;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 *
 * <code>Activator</code> provides access to preferences of the saverestore.git plugin.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class Activator extends AbstractUIPlugin {

    public static final String ID = "org.csstudio.saverestore.git";

    public static final String PREF_LOCAL = "local";
    public static final String PREF_URL = "url";
    public static final String PREF_DESTINATION = "destination";
    public static final String PREF_AUTOMATIC_SYNC = "automaticSynhronisation";

    private static Activator defaultInstance;

    /**
     * The default instance if it exists. May not exist when running unit tests.
     *
     * @return the instance
     */
    public static Optional<Activator> getInstance() {
        return Optional.ofNullable(defaultInstance);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        defaultInstance = this;
    }

    /**
     * Returns the URL to the remote git repository.
     *
     * @return the URL to git repository
     */
    public URI getGitURI() {
        String str = getPreferenceStore().getString(PREF_URL);
        return str == null ? null : URI.create(str);
    }

    /**
     * Returns the destination folder with the local git clone.
     *
     * @return the file to the location where a local git clone exists or will exist after first usage
     */
    public File getDestination() {
        URL workspace = Platform.getInstanceLocation().getURL();
        IPath location = new Path(new File(workspace.getFile()).toString());
        String s = getPreferenceStore().getString(PREF_DESTINATION);
        if (s == null || s.trim().isEmpty()) {
            s = location.append(".metadata").append(".plugins").append(ID).append("repository").toString();
        } else {
            s = location.append(s).toString();
        }
        return new File(s);
    }
}
