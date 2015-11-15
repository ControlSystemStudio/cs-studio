package org.csstudio.saverestore;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * <code>Snapshot</code> is a descriptor of a single snapshot revision.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class Snapshot implements Comparable<Snapshot>, Serializable {

    private static final String UNKNOWN = "Unknown";

    public static final String TAG_NAME = "tagName";
    public static final String TAG_MESSAGE = "tagMessage";

    private static final long serialVersionUID = 2377640937070572130L;
    private final BeamlineSet set;
    private final Date date;
    private final String comment;
    private final String owner;
    private final String toString;

    private Map<String,String> parameters;


    /**
     * Constructs a new snapshot, which belongs to the specific set. Snapshot that is read from the central storage,
     * should never be constructed with this constructor.
     *
     * @param set the set to which the snapshot belongs
     */
    public Snapshot(BeamlineSet set) {
        this(set,null,null,null);
    }

    /**
     * Constructs a new snapshot.
     *
     * @param set the set to which the snapshot belongs
     * @param date the date when snapshot was taken
     * @param comment the comment provided when snapshot was stored
     * @param owner the person that created the snapshot
     */
    public Snapshot(BeamlineSet set, Date date, String comment, String owner) {
        this(set,date,comment,owner,new HashMap<>());
    }

    /**
     * Constructs a new snapshot.
     *
     * @param set the set to which the snapshot belongs
     * @param date the date when snapshot was taken
     * @param comment the comment provided when snapshot was stored
     * @param owner the person that created the snapshot
     * @param parameters a set of optional parameters
     */
    public Snapshot(BeamlineSet set, Date date, String comment, String owner, Map<String,String> parameters) {
        this.set = set;
        this.date = date;
        this.comment = comment;
        this.owner = owner;
        this.parameters = new HashMap<>(parameters);
        if (this.date == null) {
            toString = UNKNOWN;
        } else {
            StringBuilder sb = new StringBuilder(200).append(Utilities.timestampToBigEndianString(date,true))
                    .append(":\t (").append(owner).append(")\n  ").append(comment.split("\\n")[0]);
            String tagMessage = parameters.get(TAG_MESSAGE);
            String tagName = parameters.get(TAG_NAME);
            if (tagName != null) {
                sb.append("\n  ").append("TAG: " + tagName).append(": ").append(tagMessage.split("\\n")[0]);
            }
            this.toString = sb.toString();
        }
    }

    /**
     * @return the beamline set which this snapshot belongs to
     */
    public BeamlineSet getBeamlineSet() {
        return set;
    }

    /**
     * @return the date when the snapshot was stored
     */
    public Date getDate() {
        return date;
    }

    /**
     * @return the comment provided when the snapshot was stored
     */
    public String getComment() {
        return comment;
    }

    /**
     * @return the name of the person that stored the snapshot
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @return unmodifiable map of parameters
     */
    public Map<String,String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Snapshot o) {
        if (o.date != null && this.date != null) {
            return o.date.compareTo(this.date);
        } else if (o.date != null) {
            return -1;
        } else if (this.date != null) {
            return 1;
        }
        return 0;
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((owner == null) ? 0 : owner.hashCode());
        result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
        result = prime * result + ((set == null) ? 0 : set.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Snapshot other = (Snapshot) obj;
        if (comment == null) {
            if (other.comment != null)
                return false;
        } else if (!comment.equals(other.comment))
            return false;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (owner == null) {
            if (other.owner != null)
                return false;
        } else if (!owner.equals(other.owner))
            return false;
        if (parameters == null) {
            if (other.parameters != null)
                return false;
        } else if (!parameters.equals(other.parameters))
            return false;
        if (set == null) {
            if (other.set != null)
                return false;
        } else if (!set.equals(other.set))
            return false;
        return true;
    }

    /**
     * Same as {@link #equals(Object)}, except that parameters are not compared.
     *
     * @param obj the object to compare to
     * @return true if identical or false otherwise
     */
    public boolean almostEquals(Snapshot obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        if (comment == null) {
            if (obj.comment != null)
                return false;
        } else if (!comment.equals(obj.comment))
            return false;
        if (date == null) {
            if (obj.date != null)
                return false;
        } else if (!date.equals(obj.date))
            return false;
        if (owner == null) {
            if (obj.owner != null)
                return false;
        } else if (!owner.equals(obj.owner))
            return false;
        if (set == null) {
            if (obj.set != null)
                return false;
        } else if (!set.equals(obj.set))
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toString;
    }

}
