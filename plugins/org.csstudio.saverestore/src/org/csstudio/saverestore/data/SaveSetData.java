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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * <code>SaveSetData</code> represents the content of a save set file.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SaveSetData implements Serializable {

    private static final long serialVersionUID = 510361139183432408L;

    /**
     * <code>Entry</code> describes a single entry in the save set, which is composed from the pv name, readback
     * name and the delta value to be used in combination with the {@link Threshold}.
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     */
    public static class Entry {
        private final String pv;
        private final String readback;
        private final String delta;

        Entry(String pv, String readback, String delta) {
            this.pv = pv;
            this.readback = readback;
            this.delta = delta;
        }

        Entry(String pv, String readback) {
            this(pv, readback, null);
        }

        Entry(String pv) {
            this(pv, null, null);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(200);
            sb.append(pv);
            if (readback != null) {
                sb.append(',').append(readback);
            }
            if (delta != null) {
                sb.append(',');
                if (delta.indexOf(',') > -1) {
                    sb.append('"').append(delta).append('"');
                } else {
                    sb.append(delta);
                }
            }
            return sb.toString();
        }
    }

    private final String description;
    private final SaveSet descriptor;
    private final List<String> pvList;
    private final List<String> readbackList;
    private final List<String> deltaList;
    private final String storedComment;
    private final Instant storedDate;

    /**
     * Constructs a new save set data object.
     *
     * @param descriptor the save set that describes this data
     * @param pvList the list of PV names in this save set
     * @param readbackList the list of readback PV names (one for each PV)
     * @param deltaList the list of deltas for comparing the PV values (one for each PV)
     * @param description the description of the save set
     */
    public SaveSetData(SaveSet descriptor, List<String> pvList, List<String> readbackList,
        List<String> deltaList, String description) {
        this(descriptor, pvList, readbackList, deltaList, description, null, null);
    }

    /**
     * Constructs a new save set data object.
     *
     * @param descriptor the save set that describes this data
     * @param pvList the list of PV names in this save set
     * @param readbackList the list of readback PV names (one for each pv)
     * @param deltaList the list of deltas for comparing the PV values (one for each PV)
     * @param description the description of the save set
     * @param storedComment the comment describing the current revision of this save set
     * @param storedDate the creation date of the current revision of this save set
     */
    public SaveSetData(SaveSet descriptor, List<String> pvList, List<String> readbackList,
        List<String> deltaList, String description, String storedComment, Instant storedDate) {
        if (readbackList == null) {
            readbackList = new ArrayList<>(0);
        }
        if (deltaList == null) {
            deltaList = new ArrayList<>(0);
        }
        if (!readbackList.isEmpty() && readbackList.size() != pvList.size()) {
            throw new IllegalArgumentException("The number of readbacks does not match the number of pv names.");
        }
        if (!deltaList.isEmpty() && deltaList.size() != pvList.size()) {
            throw new IllegalArgumentException("The number of deltas does not match the number of pv names.");
        }
        this.descriptor = descriptor;
        this.description = description;
        this.pvList = Collections.unmodifiableList(pvList);
        this.readbackList = Collections.unmodifiableList(readbackList);
        this.deltaList = Collections.unmodifiableList(deltaList);
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
     * Returns the list of PV names defined in the save set file.
     *
     * @return the list of PV names
     */
    public List<String> getPVList() {
        return pvList;
    }

    /**
     * Returns the list of readback PV names in this save set file (either 0 size or one for each PV).
     *
     * @return the list of all readback PV names
     */
    public List<String> getReadbackList() {
        return readbackList;
    }

    /**
     * Returns the list of all delta values in this save set file (either 0 size or one for each PV)
     *
     * @return the list of all delta values
     */
    public List<String> getDeltaList() {
        return deltaList;
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
    public List<Entry> getEntries() {
        List<Entry> entries;
        if (readbackList.isEmpty() && deltaList.isEmpty()) {
            entries = pvList.stream().map(e -> new Entry(e)).collect(Collectors.toList());
        } else if (readbackList.isEmpty()) {
            entries = new ArrayList<>(pvList.size());
            for (int i = 0; i < pvList.size(); i++) {
                entries.add(new Entry(pvList.get(i), null, deltaList.get(i)));
            }
        } else if (deltaList.isEmpty()) {
            entries = new ArrayList<>(pvList.size());
            for (int i = 0; i < pvList.size(); i++) {
                entries.add(new Entry(pvList.get(i), readbackList.get(i)));
            }
        } else {
            entries = new ArrayList<>(pvList.size());
            for (int i = 0; i < pvList.size(); i++) {
                entries.add(new Entry(pvList.get(i), readbackList.get(i), deltaList.get(i)));
            }
        }
        return entries;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(description, descriptor, pvList, readbackList, deltaList);
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
        return Objects.equals(description, other.description) && Objects.equals(descriptor, other.descriptor)
            && Objects.equals(pvList, other.pvList) && Objects.equals(readbackList, other.readbackList)
            && Objects.equals(deltaList, other.deltaList);
    }

    /**
     * Checks if the given data and this data have the same content.
     *
     * @param other the data to check its content
     * @return true if the content is identical or false otherwise
     */
    public boolean equalContent(SaveSetData other) {
        return other != null && Objects.equals(description, other.description) && Objects.equals(pvList, other.pvList)
            && Objects.equals(readbackList, other.readbackList) && Objects.equals(deltaList, other.deltaList);
    }
}
