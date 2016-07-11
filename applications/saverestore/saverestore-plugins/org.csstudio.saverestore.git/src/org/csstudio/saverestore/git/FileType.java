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
 * This represents the file types with suffix.
 *
 * @author <a href="mailto:miha.novak@cosylab.com">Miha Novak</a>
 */
enum FileType {

    SAVE_SET(".bms", "BeamlineSets"), SNAPSHOT(".snp", "Snapshots");

    final String suffix;
    final String directory;

    /**
     * Constructs file type with suffix.
     *
     * @param suffix suffix
     */
    private FileType(String suffix, String directory) {
        this.suffix = suffix;
        this.directory = directory;
    }
}
