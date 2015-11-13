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

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toString;
    }

}
