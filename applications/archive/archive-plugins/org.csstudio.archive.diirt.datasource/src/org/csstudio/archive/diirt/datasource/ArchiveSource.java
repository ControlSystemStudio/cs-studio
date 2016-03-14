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
package org.csstudio.archive.diirt.datasource;

import java.util.Objects;

/**
 *
 * <code>ArchiveSource</code> represents a configuration for a single archive server. The server is specified by a
 * unique url, key (if needed, default value is 1), and a name. It is recommended that name is unique.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
class ArchiveSource {

    final String name;
    final int key;
    final String url;

    /**
     * Constructs a new archive source from pieces.
     *
     * @param name the name of the source
     * @param key the key
     * @param url the url to the archive source (the same url as understood by the archive reader plugin)
     */
    ArchiveSource(String name, String key, String url) {
        this.name = name;
        this.key = key == null || key.isEmpty() ? 1 : Integer.parseInt(key);
        this.url = url;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(key, name, url);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ArchiveSource other = (ArchiveSource) obj;
        return Objects.equals(key, other.key) && Objects.equals(name, other.name) && Objects.equals(url, other.url);
    }

}
