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
package org.csstudio.saverestore;

import java.util.Collections;
import java.util.List;

import org.csstudio.saverestore.data.SaveSetEntry;

/**
 *
 * <code>SaveSetContent</code> provides the contents of a save set file. This class serves only as a container of the
 * loaded data and does not provide any other functionality.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public final class SaveSetContent {

    private final List<SaveSetEntry> entries;
    private final String description;

    /**
     * Constructs a new save set content.
     *
     * @param description the description of the file
     * @param entries the list of PV entries
     */
    SaveSetContent(String description, List<SaveSetEntry> entries) {
        this.entries = Collections.unmodifiableList(entries);
        this.description = description;
    }

    /**
     * Return the entries of this save set. Each entry contain the information about a single PV entry in the save set
     * file definition.
     *
     * @return the entries
     */
    public List<SaveSetEntry> getEntries() {
        return entries;
    }

    /**
     * Return the description of the save set.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}
