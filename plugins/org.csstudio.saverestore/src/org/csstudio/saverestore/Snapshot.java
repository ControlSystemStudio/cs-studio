package org.csstudio.saverestore;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

/**
 *
 * <code>Snapshot</code> is a descriptor of a single snapshot revision.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class Snapshot implements Comparable<Snapshot>, Serializable {

    private static final String UNKNOWN = "Unknown";

    private static final long serialVersionUID = 2377640937070572130L;
    private final BeamlineSet set;
    private final Date date;
    private final String comment;
    private final String tag;
    private final String tagMessage;
    private final String owner;
    private final String toString;

    /**
     * Constructs a new snapshot, which belongs to the specific set. Snapshot that is read from the central storage,
     * should never be constructed with this constructor.
     *
     * @param set the set to which the snapshot belongs
     */
    public Snapshot(BeamlineSet set) {
        this(set,null,null,null,null,null);
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
        this(set,date,comment,owner,null,null);
    }

    /**
     * Constructs a new snapshot.
     *
     * @param set the set to which the snapshot belongs
     * @param date the date when snapshot was taken
     * @param comment the comment provided when snapshot was stored
     * @param owner the person that created the snapshot
     * @param tag optional tag of the snapshot
     * @param tagMessage optional tag message
     */
    public Snapshot(BeamlineSet set, Date date, String comment, String owner, String tag, String tagMessage) {
        this.set = set;
        this.date = date;
        this.comment = comment;
        this.owner = owner;
        this.tag = tag;
        this.tagMessage = tagMessage;
        this.toString = this.date == null ? UNKNOWN
                : (date.toString() + ":\t " + owner + "\n  " + comment.split("\\n")[0]);
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
     * The snapshot can optionally contain a tag name.
     *
     * @return optional tag name
     */
    public Optional<String> getTag() {
        return Optional.ofNullable(tag);
    }

    /**
     * If the snapshot has a tag it may also have a tag message.
     *
     * @return the tag message
     */
    public Optional<String> getTagMessage() {
        return Optional.ofNullable(tagMessage);
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

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toString;
    }

}
