/*
 * This software is Copyright by the Board of Trustees of Michigan State University (c) Copyright 2016. Contact
 * Information: Facility for Rare Isotope Beam Michigan State University East Lansing, MI 48824-1321 http://frib.msu.edu
 */
package org.csstudio.saverestore.data;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <code>SaveSetData</code> represents the content of a save set file.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 */
public class SaveSetData implements Serializable {

    private static final long serialVersionUID = 510361139183432408L;

    private final String description;
    private final SaveSet descriptor;
    private final List<SaveSetEntry> entries;
    private final String storedComment;
    private final Instant storedDate;

    /**
     * Constructs a new save set data object.
     *
     * @param descriptor the save set that describes this data
     * @param entries the list of all PV entries in this save set
     * @param description the description of the save set
     */
    public SaveSetData(SaveSet descriptor, List<SaveSetEntry> entries, String description) {
        this(descriptor, entries, description, null, null);
    }

    /**
     * Constructs a new save set data object.
     *
     * @param descriptor the save set that describes this data
     * @param entries the list of all PV entries in this save set
     * @param description the description of the save set
     * @param storedComment the comment describing the current revision of this save set
     * @param storedDate the creation date of the current revision of this save set
     */
    public SaveSetData(SaveSet descriptor, List<SaveSetEntry> pvList, String description, String storedComment,
        Instant storedDate) {
        this.descriptor = descriptor;
        this.description = description;
        this.entries = Collections.unmodifiableList(new ArrayList<>(pvList));
        this.storedComment = storedComment;
        this.storedDate = storedDate;
    }

    /**
     * Returns the save set file descriptor of the data.
     *
     * @return the descriptor
     */
    public SaveSet getDescriptor() {
        return descriptor;
    }

    /**
     * Returns a human readable description of this save set.
     *
     * @return the description of this save set
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the comment provided when the save set was stored. Can be null.
     *
     * @return the comment used to store this revision of the save set
     */
    public String getStoredComment() {
        return storedComment;
    }

    /**
     * Returns the date of this save set revision.
     *
     * @return the date when this revision was stored
     */
    public Instant getStoredDate() {
        return storedDate;
    }

    /**
     * Returns the list of entries composed from the data in this set.
     *
     * @return the list of all entries
     */
    public List<SaveSetEntry> getEntries() {
        return entries;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(SaveSetData.class, description, descriptor, entries);
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
        SaveSetData other = (SaveSetData) obj;
        return Objects.equals(descriptor, other.descriptor) && equalContent(other);
    }

    /**
     * Checks if the given data and this data have the same content.
     *
     * @param other the data to check its content
     * @return true if the content is identical or false otherwise
     */
    public boolean equalContent(SaveSetData other) {
        return other != null && Objects.equals(description, other.description)
            && Objects.equals(entries, other.entries);
    }
}
