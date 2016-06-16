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
package org.csstudio.saverestore.git;

/**
 *
 * <code>Result</code> represents the result of a particular action in the git repository.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 * @param <T> the type of the data provided by this result object
 */
public class Result<T> {

    /**
     *
     * <code>ChangeType</code> represents the possible changes in repository after an action is performed. If nothing
     * changed in the repository, the change is described by {@link #NONE}, if a new revision of a file was created it
     * is described by {@link #SAVE}, if changes were pulled from the remote repository the change is described by
     * {@link #PULL}.
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     */
    public static enum ChangeType {
        NONE, SAVE, PULL
    }

    final T data;
    final ChangeType change;

    /**
     * Constructs a new results object, which provides the data that was stored or created and the type of change in the
     * repository.
     *
     * @param data the data
     * @param change the type of change
     */
    Result(T data, ChangeType change) {
        this.data = data;
        this.change = change;
    }
}
