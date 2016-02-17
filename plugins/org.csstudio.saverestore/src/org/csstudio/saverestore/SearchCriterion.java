package org.csstudio.saverestore;

import java.io.Serializable;

/**
 *
 * <code>SearchCriterion</code> defines the criterion by which the snapshots are search for. This can be for example a
 * comment, tag name, tag message, user name, or any other property specific to the data provider.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SearchCriterion implements Serializable {

    private static final long serialVersionUID = 725886496615376551L;

    private final String readableName;
    private final boolean isDefault;
    private final boolean isExclusive;

    /**
     * Construct a new search criterion with the given name and default flag. Default criteria is automatically selected
     * in the UI.
     *
     * @param readableName the readable name for display purposes
     * @param isDefault true if this is a default criteria or false otherwise
     * @param isExclusive true if this criterion is exclusive; when exclusive criterion is selected all others are
     *            ignored
     * @return the criterion
     */
    public static SearchCriterion of(String readableName, boolean isDefault, boolean isExclusive) {
        return new SearchCriterion(readableName, isDefault, isExclusive);
    }

    private SearchCriterion(String readableName, boolean isDefault, boolean isExclusive) {
        this.readableName = readableName;
        this.isDefault = isDefault;
        this.isExclusive = isExclusive;
    }

    /**
     * Returns true if this is a default criterion. Each data provider can have certain default criteria, which are
     * automatically selected in the UI.
     *
     * @return true if this is a default criterion or false otherwise
     */
    public boolean isDefault() {
        return isDefault;
    }

    /**
     * Returns true if this criterion is exclusive. When exclusive criterion is used all other criteria are ignored.
     *
     * @return true if this criterion is exclusive or false otherwise
     */
    public boolean isExclusive() {
        return isExclusive;
    }

    /**
     * Returns the readable name of this search criterion.
     *
     * @return the readable name
     */
    public String getReadableName() {
        return readableName;
    }
}
