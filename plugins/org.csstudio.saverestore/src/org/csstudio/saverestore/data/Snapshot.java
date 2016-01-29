package org.csstudio.saverestore.data;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.csstudio.saverestore.Utilities;

/**
 *
 * <code>Snapshot</code> is a descriptor of a single snapshot revision. When comparing different snapshots all fields
 * are taken into account (beamline set, date, comment, owner and parameters). However {@link #compareTo(Snapshot)} only
 * uses the date field. Therefore {@link #compareTo(Snapshot)} might flag two snapshots equal when
 * {@link #equals(Object)} return false. On the other hand if equals returns true, than the compareTo will return 0.
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
    private final Map<String, String> parameters;

    /**
     * Constructs a new snapshot, which belongs to the specific set. Snapshot that is read from the central storage,
     * should never be constructed with this constructor.
     *
     * @param set the set to which the snapshot belongs
     */
    public Snapshot(BeamlineSet set) {
        this(set, null, null, null);
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
        this(set, date, comment, owner, new HashMap<>());
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
    public Snapshot(BeamlineSet set, Date date, String comment, String owner, Map<String, String> parameters) {
        this.set = set;
        this.date = date;
        this.comment = comment;
        this.owner = owner;
        this.parameters = new HashMap<>(parameters);
        if (this.date == null) {
            toString = UNKNOWN;
        } else {
            StringBuilder sb = new StringBuilder(200).append(Utilities.timestampToBigEndianString(date, true))
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
     * Returns the beamline set which this snapshot belongs to.
     *
     * @return the beamline set
     */
    public BeamlineSet getBeamlineSet() {
        return set;
    }

    /**
     * Returns the timestamp when the snapshot was stored. This is not the same as the timestamp of when the snapshot
     * was taken and should not be confused by {@link VSnapshot#getTimestamp()}.
     *
     * @return the date when the snapshot was stored
     */
    public Date getDate() {
        return date;
    }

    /**
     * Returns the comment provided when the snapshot was stored.
     *
     * @return the snapshot comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Returns the username of the person that stored this snapshot.
     *
     * @return the name of the person that stored the snapshot
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Returns additional parameters of this snapshot. Parameters might be any string that is required by the data
     * provider to work with this snapshot or any other info that is required to be carried around, including the tag
     * name and message if they exist.
     *
     * @return unmodifiable map of parameters
     */
    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    /**
     * Returns the tag name of this snapshot if it exists or empty if it does not exist.
     *
     * @return the tag name if it exists
     */
    public Optional<String> getTagName() {
        return Optional.ofNullable(parameters.get(TAG_NAME));
    }

    /**
     * Returns the tag message of this snapshot if it exists or empty if it does not exist.
     *
     * @return the tag message if it exists
     */
    public Optional<String> getTagMessage() {
        return Optional.ofNullable(parameters.get(TAG_MESSAGE));
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Snapshot o) {
        if (o.date != null && this.date != null) {
            // date is compared in reverse, to make the youngest snapshot the first in the list
            return o.date.compareTo(this.date);
        } else if (o.date != null) {
            return -1;
        } else if (this.date != null) {
            return 1;
        }
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(comment, date, owner, parameters, set);
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
        Snapshot other = (Snapshot) obj;
        return Objects.equals(comment, other.comment) && Objects.equals(date, other.date)
            && Objects.equals(owner, other.owner) && Objects.equals(parameters, other.parameters)
            && Objects.equals(set, other.set);
    }

    /**
     * Same as {@link #equals(Object)}, except that parameters are not compared.
     *
     * @param obj the object to compare to
     * @return true if identical or false otherwise
     */
    public boolean almostEquals(Snapshot obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }
        return Objects.equals(comment, obj.comment) && Objects.equals(date, obj.date)
            && Objects.equals(owner, obj.owner) && Objects.equals(set, obj.set);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toString;
    }
}
