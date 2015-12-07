package org.csstudio.saverestore;

import java.util.List;

/**
 *
 * <code>BeamlineSetContent</code> provides the contents of a beamline set file.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class BeamlineSetContent {

    /** The names of all pvs */
    public final List<String> names;
    /** The description of the beamline set */
    public final String description;

    /**
     * Constructs a new beamline set content.
     *
     * @param description the description of the file
     * @param names the list of pv names
     */
    BeamlineSetContent(String description, List<String> names) {
        this.names = names;
        this.description = description;
    }
}
