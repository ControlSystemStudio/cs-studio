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

import org.diirt.datasource.ConfigurableDataSourceProvider;

/**
 *
 * <code>ArchiveDataSourceProvider</code> provides the data source capable of creating channels that fetch data from
 * the archive servers.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class ArchiveDataSourceProvider
    extends ConfigurableDataSourceProvider<ArchiveDataSource, ArchiveDataSourceConfiguration> {

    /** The prefix of the data source */
    public static final String ARCHIVE = "archive";

    /**
     * Construct a new data source provider.
     */
    public ArchiveDataSourceProvider() {
        super(ArchiveDataSourceConfiguration.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.diirt.datasource.DataSourceProvider#getName()
     */
    @Override
    public String getName() {
        return ARCHIVE;
    }
}
