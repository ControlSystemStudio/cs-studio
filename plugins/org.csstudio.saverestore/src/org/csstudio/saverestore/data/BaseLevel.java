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
import java.util.Objects;

import org.csstudio.saverestore.DataProvider;

/**
 *
 * <code>BaseLevel</code> represents the base object that can be selected. These can be for example the top level
 * folders if the data comes from the file system. When the base level is created it is uniquely defined by its storage
 * name. This is also the only property that is required by the {@link DataProvider} to do further loading of data.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class BaseLevel implements Comparable<BaseLevel>, Serializable {
    private static final long serialVersionUID = 7503287144725281421L;

    private final String storageName;
    private final String presentationName;
    private final Branch branch;

    /**
     * Construct a new BaseLevel using the values provided by the <code>level</code> parameter. Constructed base level
     * is equal to the provided one.
     *
     * @param level the parameters provider
     */
    public BaseLevel(BaseLevel level) {
        this(level.getBranch(), level.getStorageName(), level.getPresentationName());
    }

    /**
     * Construct a new BaseLevel using the values provided by the <code>level</code> parameter and the given branch.
     * Constructed base level is equal to the provided one.
     *
     * @param branch the branch of the new base level
     * @param level the parameters provider
     */
    public BaseLevel(Branch branch, BaseLevel level) {
        this(branch, level.getStorageName(), level.getPresentationName());
    }

    /**
     * Construct a new base level from pieces.
     *
     * @param branch the branch that this base level resides on
     * @param storageName the storage name of the base level
     * @param presentationName the presentation name of the base level
     * @throws IllegalArgumentException if the storage name contains non ASCII characters
     */
    public BaseLevel(Branch branch, String storageName, String presentationName) throws IllegalArgumentException {
        if (!storageName.chars().allMatch(c -> c >= 32 && c < 128)) {
            throw new IllegalArgumentException("Only ASCII characters are allowed in the storage name.");
        }
        this.branch = branch;
        this.storageName = storageName;
        this.presentationName = presentationName;
    }

    /**
     * Returns the presentation name of the base level.
     *
     * @return the name used for presentation of this base level
     */
    public String getPresentationName() {
        return presentationName;
    }

    /**
     * Returns the storage name of this base level. Storage name has to be unique among all base levels and can only
     * contain ASCII characters.
     *
     * @return the name used for storage of this base level.
     */
    public String getStorageName() {
        return storageName;
    }

    /**
     * Returns the branch on which the base level is located. The {@link DataProvider} is obliged to fill this field (if
     * branches are supported), but the clients of the data provider are not obliged to provide the base level with the
     * proper branch.
     *
     * @return the branch on which this base level is located
     */
    public Branch getBranch() {
        return branch;
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
        BaseLevel other = (BaseLevel) obj;
        return Objects.equals(getStorageName(), other.getStorageName())
            && Objects.equals(getBranch(), other.getBranch());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(getStorageName(), getBranch());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(BaseLevel o) {
        if (o == null) {
            return -1;
        }
        int i = getStorageName().compareTo(o.getStorageName());
        if (i == 0) {
            if (getBranch() != null) {
                return getBranch().compareTo(o.getBranch());
            } else if (o.getBranch() == null) {
                return i;
            }
        }
        return i;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getStorageName();
    }
}
