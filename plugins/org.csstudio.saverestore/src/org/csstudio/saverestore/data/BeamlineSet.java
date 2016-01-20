package org.csstudio.saverestore.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * <code>BeamlineSet</code> is a descriptor for the beamline set files, which are collections of pv names for which a
 * snapshot can be taken. The beamline set belongs to a specific branch and optionally a {@link BaseLevel}. The set if
 * located at a specific path.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class BeamlineSet implements Comparable<BeamlineSet>, Serializable {

    private static final long serialVersionUID = 3576698958890734750L;

    private final Branch branch;
    private BaseLevel baseLevel;
    private final String[] path;
    private final String folder;
    private String fullyQualifiedName;
    private String displayName;

    private final String dataProviderId;

    /**
     * Constructs a new empty beamline (e.g. null beamline set).
     */
    public BeamlineSet() {
        this(new Branch("master", "master"), Optional.empty(), new String[] { "unknown" }, null);
    }

    /**
     * Construct a new beamline set from pieces.
     *
     * @param branch the branch on which the beamline set is located
     * @param baseLevel the base level for which this set is valid
     * @param path the path on which the set is stored (the last element of the pat is the file name)
     * @param the ID of the data provider from which this beamline set was loaded
     */
    public BeamlineSet(Branch branch, Optional<BaseLevel> base, String[] path, String dataProviderId) {
        this.baseLevel = base.orElse(null);
        this.branch = branch;
        this.path = path;
        this.dataProviderId = dataProviderId;
        if (this.path.length == 1) {
            folder = "";
        } else {
            StringBuilder sb = new StringBuilder(100);
            for (int i = 0; i < path.length - 1; i++) {
                sb.append(path[i]);
                if (i < path.length - 2) {
                    sb.append('/');
                }
            }
            folder = sb.toString();
        }
    }

    /**
     * The data provider id, which this beamline set originates from.
     *
     * @return the data provider id
     */
    public String getDataProviderId() {
        return dataProviderId;
    }

    /**
     * The path to this beamline set within its base level location, but without the actual file name.
     *
     * @return the full folder name within the top or base level location and without the file name
     */
    public String getFolder() {
        return folder;
    }

    /**
     * Returns the base level, which this beamline belongs to.
     *
     * @return the base level which this set is for
     */
    public Optional<BaseLevel> getBaseLevel() {
        return Optional.ofNullable(baseLevel);
    }

    /**
     * Updates the base level in this beamline set. This method should be called before the beamline set is serialized
     * and deserialized in a different plugin. If the destination plugin does not have a dependency on the current base
     * level type, the beamline set could not be deserialized. Therefore, the current base level has to be morphed into
     * an in instance that all plugin have dependency on.
     */
    public void updateBaseLevel() {
        if (baseLevel != null) {
            baseLevel = new BaseLevel(baseLevel);
        }
    }

    /**
     * Returns the branch, which this beamline set originates from.
     *
     * @return the branch from which the set was loaded
     */
    public Branch getBranch() {
        return branch;
    }

    /**
     * Returns the full path of this beamline set (including the set name) within the base level.
     *
     * @return the full path to the beamline set file
     */
    public String[] getPath() {
        return path;
    }

    /**
     * Returns the name of the beamline set. This is in general identical to the last part of the {@link #getPath()}.
     *
     * @return the name of the set (file name)
     */
    public String getName() {
        return path[path.length - 1];
    }

    /**
     * Returns the complete path as a single string. Individual parts are separated by the <code>/</code> character.
     *
     * @return the path as a single string, individual parts are separated by the <code>/</code> character
     */
    public String getPathAsString() {
        if (folder.isEmpty()) {
            return getName();
        } else {
            return folder + "/" + getName();
        }
    }

    /**
     * Returns the display name of the beamline set, which may not be a fully qualified name but rather a name that
     * quickly shows what this beamline set is about.
     *
     * @return the display name of the beamline set
     */
    public String getDisplayName() {
        if (displayName == null) {
            displayName = getName() + (baseLevel == null ? "" : " (" + baseLevel.getPresentationName() + ")");
        }
        return displayName;
    }

    /**
     * Returns the fully qualified name of this beamline set, which includes all parts of the path, base level and
     * branch.
     *
     * @return the fully qualified name
     */
    public String getFullyQualifiedName() {
        if (fullyQualifiedName == null) {
            StringBuilder sb = new StringBuilder(150).append('[').append(getBranch().getShortName());
            if (baseLevel != null) {
                sb.append('/').append(baseLevel.getStorageName());
            }
            sb.append(']').append(' ').append(getPathAsString());
            fullyQualifiedName = sb.toString();
        }
        return fullyQualifiedName;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getName();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(BeamlineSet o) {
        String[] otherPath = o.path;
        for (int i = 0; i < path.length && i < otherPath.length; i++) {
            int c = path[i].compareTo(otherPath[i]);
            if (c != 0)
                return c;
        }

        if (path.length == otherPath.length) {
            return 0;
        }
        return path.length < otherPath.length ? -1 : 1;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 31 * Objects.hash(baseLevel, branch) + Arrays.hashCode(path);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }
        BeamlineSet other = (BeamlineSet) obj;
        return Objects.equals(baseLevel, other.baseLevel) && Objects.equals(branch, other.branch)
            && Arrays.equals(path, other.path);

    }
}
