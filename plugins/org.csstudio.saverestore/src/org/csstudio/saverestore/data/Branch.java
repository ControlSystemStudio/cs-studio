package org.csstudio.saverestore.data;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

/**
 *
 * <code>Branch</code> represents a branch in the repository.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class Branch implements Comparable<Branch>, Serializable {

    private static final long serialVersionUID = 7901647776511062560L;
    /** The name of the default branch */
    private static final String MASTER = "master";

    private final String fullName;
    private final String shortName;

    /**
     * Constructs default Branch.
     */
    public Branch() {
        this(MASTER, MASTER);
    }

    /**
     * Constructs a new branch.
     *
     * @param fullName full name
     * @param shortName readable name
     */
    public Branch(String fullName, String shortName) {
        this.fullName = fullName;
        this.shortName = shortName;
    }

    /**
     * Returns the full branch name, which might contain some prefixes or suffixes required by the data provider, but
     * are of no special use for the user. This full name should be unique among branches.
     *
     * @return the full branch name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Returns the readable branch name, which is used in the UI.
     *
     * @return readable branch name
     */
    public String getShortName() {
        return shortName;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Branch o) {
        if (o == null) {
            return -1;
        }
        return shortName.toLowerCase(Locale.UK).compareTo(o.shortName.toLowerCase(Locale.UK));
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return shortName;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(shortName);
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
        Branch other = (Branch) obj;
        return Objects.equals(shortName, other.shortName);
    }

    /**
     * Returns true if this is a default branch or false otherwise
     *
     * @return true if default branch or false otherwise
     */
    public boolean isDefault() {
        return MASTER.equals(shortName);
    }
}
