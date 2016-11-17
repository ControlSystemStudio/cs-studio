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
package org.csstudio.saverestore.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * <code>SaveSet</code> is a descriptor for the save set files, which are collections of pv names for which a snapshot
 * can be taken. The SaveSet belongs to a specific branch and optionally a {@link BaseLevel}. The set is always located
 * on a specific path, which uniquely describes it.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SaveSet implements Comparable<SaveSet>, Serializable {

    private static final long serialVersionUID = 3576698958890734750L;

    private final Branch branch;
    private BaseLevel baseLevel;
    private final String[] path;
    private final String folder;
    private String fullyQualifiedName;
    private String displayName;
    private final Map<String, String> parameters;
    private final String dataProviderId;

    /**
     * Constructs a new empty save set.
     */
    public SaveSet() {
        this(new Branch(), Optional.empty(), new String[] { "unknown" }, null);
    }

    /**
     * Construct a new save set from pieces.
     *
     * @param branch the branch on which the save set is located
     * @param baseLevel the base level for which this set is valid
     * @param path the path on which the set is stored (the last element of the pat is the file name)
     * @param the ID of the data provider from which this save set was loaded
     */
    public SaveSet(Branch branch, Optional<BaseLevel> base, String[] path, String dataProviderId) {
        this(branch, base, path, dataProviderId, new HashMap<>());
    }

    /**
     * Construct a new save set from pieces.
     *
     * @param branch the branch on which the save set is located
     * @param baseLevel the base level for which this set is valid
     * @param path the path on which the set is stored (the last element of the pat is the file name)
     * @param the ID of the data provider from which this save set was loaded
     * @param parameters a map of optional parameters usually required by the data provider
     */
    public SaveSet(Branch branch, Optional<BaseLevel> base, String[] path, String dataProviderId,
        Map<String, String> parameters) {
        this.baseLevel = base.orElse(null);
        this.branch = branch;
        this.path = path;
        this.dataProviderId = dataProviderId;
        this.parameters = new HashMap<>(parameters);
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
     * Returns additional parameters of this save set. A parameter might be any string that is required by the data
     * provider to work with this save set or any other info that is required to be carried around.
     *
     * @return unmodifiable map of parameters
     */
    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    /**
     * The data provider id, which this save set originates from.
     *
     * @return the data provider id
     */
    public String getDataProviderId() {
        return dataProviderId;
    }

    /**
     * The path to this save set within its base level location, but without the actual file name.
     *
     * @return the full folder name within the top or base level location and without the file name
     */
    public String getFolder() {
        return folder;
    }

    /**
     * Returns the base level, which this save set belongs to.
     *
     * @return the base level which this set is for
     */
    public Optional<BaseLevel> getBaseLevel() {
        return Optional.ofNullable(baseLevel);
    }

    /**
     * Updates the base level in this save set. This method should be called before the save set is serialised and
     * deserialised in a different plug-in. If the destination plug-in does not have a dependency on the current base
     * level type, the save set could not be deserialised. Therefore, the current base level has to be morphed into an
     * in instance that all plug-ins have dependency on.
     */
    public void updateBaseLevel() {
        if (baseLevel != null) {
            baseLevel = new BaseLevel(baseLevel);
        }
    }

    /**
     * Returns the branch, which this save set originates from.
     *
     * @return the branch from which the set was loaded
     */
    public Branch getBranch() {
        return branch;
    }

    /**
     * Returns the full path of this save set (including the set name) within the base level.
     *
     * @return the full path to the save set file
     */
    public String[] getPath() {
        return path;
    }

    /**
     * Returns the name of the save set. This is in general identical to the last part of the {@link #getPath()}.
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
     * Returns the display name of the save set, which may not be a fully qualified name but rather a name that
     * quickly shows what this save set is about.
     *
     * @return the display name of the save set
     */
    public String getDisplayName() {
        if (displayName == null) {
            displayName = getName() + (baseLevel == null ? "" : " (" + baseLevel.getPresentationName() + ")");
        }
        return displayName;
    }

    /**
     * Returns the fully qualified name of this save set, which includes all parts of the path, base level and
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
    public int compareTo(SaveSet o) {
        String[] otherPath = o.path;
        for (int i = 0; i < path.length && i < otherPath.length; i++) {
            int c = path[i].compareTo(otherPath[i]);
            if (c != 0) {
                return c;
            }
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
        return 31 * Objects.hash(SaveSet.class,baseLevel, branch) + Arrays.hashCode(path);
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
        SaveSet other = (SaveSet) obj;
        return Objects.equals(baseLevel, other.baseLevel) && Objects.equals(branch, other.branch)
            && Arrays.equals(path, other.path);

    }
}
