package org.csstudio.logbook;

/**
 * An Interface for defining Logbooks.
 *
 * @author shroffk
 *
 */
public interface Logbook {

    /**
     * @return String - the name of this logbook
     */
    public String getName();

    /**
     * @return String - the owner of this logbook
     */
    public String getOwner();

}
