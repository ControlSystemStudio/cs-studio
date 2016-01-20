package org.csstudio.saverestore;

import java.util.Collections;
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
    /** The names of readback pvs, if one PV does not have a readback, there should be an empty string at that index */
    public final List<String> readbacks;
    /** The deltas defining how to treat the difference between values. If two values differ less than delta, they are
     * considered equal. The delta can be a number, or a function. If there is no known delta for a PV the entry should
     * be an empty string. */
    public final List<String> deltas;
    /** The description of the beamline set */
    public final String description;

    /**
     * Constructs a new beamline set content.
     *
     * @param description the description of the file
     * @param names the list of pv names
     * @param readbacks the list of readback values
     * @param deltas the list of delta values
     */
    BeamlineSetContent(String description, List<String> names, List<String> readbacks, List<String> deltas) {
        if (!readbacks.isEmpty() && readbacks.size() != names.size()) {
            throw new IllegalArgumentException("The number of readbacks does not match the number of pv names.");
        }
        if (!deltas.isEmpty() && deltas.size() != names.size()) {
            throw new IllegalArgumentException("The number of deltas does not match the number of pv names.");
        }
        this.names = Collections.unmodifiableList(names);
        this.readbacks = Collections.unmodifiableList(readbacks);
        this.deltas = Collections.unmodifiableList(deltas);
        this.description = description;
    }
}
