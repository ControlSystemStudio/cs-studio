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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.csstudio.saverestore.Utilities;

/**
 *
 * <code>Snapshot</code> is a descriptor of a single snapshot revision. When comparing different snapshots all fields
 * are taken into account (save set, date, comment, owner and parameters). However {@link #compareTo(Snapshot)} only
 * uses the date field. Therefore {@link #compareTo(Snapshot)} might flag two snapshots equal when
 * {@link #equals(Object)} return false. On the other hand if equals returns true, than the compareTo will return 0.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class Snapshot implements Comparable<Snapshot>, Serializable {

    private static final String UNKNOWN = "Unknown";

    private static final long serialVersionUID = 2377640937070572130L;
    private final SaveSet set;
    private final Instant date;
    private final String comment;
    private final String owner;
    private final String toString;
    private final String tagName;
    private final String tagMessage;
    private final Map<String, String> parameters;
    private final List<String> publicParameters;

    /**
     * Constructs a new snapshot, which belongs to the specific set. Snapshot that is read from the central storage,
     * should never be constructed with this constructor.
     *
     * @param set the set to which the snapshot belongs
     */
    public Snapshot(SaveSet set) {
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
    public Snapshot(SaveSet set, Instant date, String comment, String owner) {
        this(set, date, comment, owner, null, null, new HashMap<>(0), new ArrayList<>(0));
    }

    /**
     * Constructs a new snapshot.
     *
     * @param set the set to which the snapshot belongs
     * @param date the date when snapshot was taken
     * @param comment the comment provided when snapshot was stored
     * @param owner the person that created the snapshot
     * @param tagName the name of the tag if it exists
     * @param tagMessage the tag message
     */
    public Snapshot(SaveSet set, Instant date, String comment, String owner, String tagName, String tagMessage) {
        this(set, date, comment, owner, tagName, tagMessage, new HashMap<>(0), new ArrayList<>(0));
    }

    /**
     * Constructs a new snapshot.
     *
     * @param set the set to which the snapshot belongs
     * @param date the date when snapshot was taken
     * @param comment the comment provided when snapshot was stored
     * @param owner the person that created the snapshot
     * @param parameters a set of optional parameters
     * @param publicParameters specifies the list of parameter names which are considered public; only public parameters
     *            are ever displayed in the GUI, while non public are usually specific for the data provider
     */
    public Snapshot(SaveSet set, Instant date, String comment, String owner, Map<String, String> parameters,
        List<String> publicParameters) {
        this(set, date, comment, owner, null, null, parameters, publicParameters);
    }

    /**
     * Constructs a new snapshot.
     *
     * @param set the set to which the snapshot belongs
     * @param date the date when snapshot was taken
     * @param comment the comment provided when snapshot was stored
     * @param owner the person that created the snapshot
     * @param tagName the name of the tag if it exists
     * @param tagMessage the tag message
     * @param parameters a set of optional parameters
     * @param publicParameters specifies the list of parameter names which are considered public; only public parameters
     *            are ever displayed in the GUI, while non public are usually specific for the data provider
     */
    public Snapshot(SaveSet set, Instant date, String comment, String owner, String tagName, String tagMessage,
        Map<String, String> parameters, List<String> publicParameters) {
        this.set = set;
        this.date = date;
        this.comment = comment;
        this.owner = owner;
        this.parameters = new HashMap<>(parameters);
        this.publicParameters = publicParameters;
        this.tagName = tagName;
        this.tagMessage = tagMessage;
        if (this.date == null) {
            toString = UNKNOWN;
        } else {
            StringBuilder sb = new StringBuilder(200 + publicParameters.size() * 100)
                .append(Utilities.timestampToBigEndianString(date, true)).append(":\t (").append(owner).append(")\n  ")
                .append(comment == null ? "no comment" : comment.split("\\n")[0]);
            if (!publicParameters.isEmpty()) {
                sb.append(' ').append('(');
                int size = publicParameters.size();
                for (int i = 0; i < size; i++) {
                    sb.append(publicParameters.get(i).toUpperCase(Locale.UK)).append(':').append(' ')
                        .append(parameters.get(publicParameters.get(i)));
                    if (i < size - 1) {
                        sb.append(',').append(' ');
                    }
                }
                sb.append(')');
            }
            if (tagName != null) {
                sb.append("\n  ").append("TAG: ").append(tagName).append(':').append(' ')
                    .append(tagMessage.split("\\n")[0]);
            }

            this.toString = sb.toString();
        }
    }

    /**
     * Returns the save set which this snapshot belongs to.
     *
     * @return the save set
     */
    public SaveSet getSaveSet() {
        return set;
    }

    /**
     * Returns the timestamp when the snapshot was stored. This is not the same as the timestamp of when the snapshot
     * was taken and should not be confused by {@link VSnapshot#getTimestamp()}.
     *
     * @return the date when the snapshot was stored
     */
    public Instant getDate() {
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
     * Returns the names of all public parameters provided by the {@link #getParameters()} name. When parameters are
     * displayed to the user, only the public ones should be displayed, while the rest should be hidden. The non public
     * parameters are for internal use only.
     *
     * @return the list of public parameters names
     */
    public List<String> getPublicParameters() {
        return Collections.unmodifiableList(publicParameters);
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
        return Optional.ofNullable(tagName);
    }

    /**
     * Returns the tag message of this snapshot if it exists or empty if it does not exist.
     *
     * @return the tag message if it exists
     */
    public Optional<String> getTagMessage() {
        return Optional.ofNullable(tagMessage);
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
