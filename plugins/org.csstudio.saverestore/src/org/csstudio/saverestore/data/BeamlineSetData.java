package org.csstudio.saverestore.data;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 *
 * <code>BeamlineSetData</code> represents the content of a beamline set file.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class BeamlineSetData {

    private final String description;
    private final BeamlineSet descriptor;
    private final List<String> pvList;
    private final String storedComment;
    private final Date storedDate;

    /**
     * Constructs a new beamline set data object.
     *
     * @param descriptor the beamline set that describes this data
     * @param pvList the list of pv names in this beamline set
     * @param description the description of the beamline set
     */
    public BeamlineSetData(BeamlineSet descriptor, List<String> pvList, String description) {
        this(descriptor, pvList, description, null, null);
    }

    /**
     * Constructs a new beamline set data object.
     *
     * @param descriptor the beamline set that describes this data
     * @param pvList the list of pv names in this beamline set
     * @param description the description of the beamline set
     * @param storedComment the comment describing the current revision of this beamline set
     * @param storedDate the creation date of the current revision of this beamline set
     */
    public BeamlineSetData(BeamlineSet descriptor, List<String> pvList, String description, String storedComment,
            Date storedDate) {
        this.descriptor = descriptor;
        this.description = description;
        this.pvList = Collections.unmodifiableList(pvList);
        this.storedComment = storedComment;
        this.storedDate = storedDate;
    }

    /**
     * @return the descriptor that this data belongs to
     */
    public BeamlineSet getDescriptor() {
        return descriptor;
    }

    /**
     * @return the list of pb names in this beamline set file
     */
    public List<String> getPVList() {
        return pvList;
    }

    /**
     * @return the description of this beamline set
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the comment used to store this revision of the beamline set
     */
    public String getStoredComment() {
        return storedComment;
    }

    /**
     * @return the date when this revision was stored
     */
    public Date getStoredDate() {
        return storedDate;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(description, descriptor, pvList);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BeamlineSetData other = (BeamlineSetData) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (descriptor == null) {
            if (other.descriptor != null)
                return false;
        } else if (!descriptor.equals(other.descriptor))
            return false;
        if (pvList == null) {
            if (other.pvList != null)
                return false;
        } else if (!pvList.equals(other.pvList))
            return false;
        return true;
    }
}
