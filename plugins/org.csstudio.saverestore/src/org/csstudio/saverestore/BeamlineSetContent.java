package org.csstudio.saverestore;

import java.util.Collections;
import java.util.List;

import org.csstudio.saverestore.data.Threshold;

/**
 *
 * <code>BeamlineSetContent</code> provides the contents of a beamline set file.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public final class BeamlineSetContent {

    private final List<String> names;
    private final List<String> readbacks;
    private final List<String> deltas;
    private final String description;

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

    /**
     * Return the names of all PVs in the beamline set.
     *
     * @return the names
     */
    public List<String> getNames() {
        return names;
    }

    /**
     * Returns the names of readback pvs, if one PV does not have a readback, there should be an empty string at that
     * index. If none of the pvs have a readback PV associated with it, the list can be empty.
     *
     * @return the readback pv names
     */
    public List<String> getReadbacks() {
        return readbacks;
    }

    /**
     * Returns the deltas list, which define how to treat the difference between values. The deltas are later
     * transformed to {@link Threshold}s, which evaluate the difference between the values. In general if two values
     * differ less than delta, they are considered equal or at least non-critically different. The delta can be a
     * number, or a function. If there is no known delta for a PV the entry should be an empty string. If none of the
     * PVs have a delta associated with it, the list can be empty.
     *
     * @return the deltas list
     */
    public List<String> getDeltas() {
        return deltas;
    }

    /**
     * Return the description of the beamline set.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

}
