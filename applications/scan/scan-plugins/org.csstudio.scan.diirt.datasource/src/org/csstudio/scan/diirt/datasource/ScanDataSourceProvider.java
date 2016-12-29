/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.csstudio.scan.diirt.datasource;

import org.diirt.datasource.ConfigurableDataSourceProvider;

/**
 * DatasourceProvider for scanserver
 *
 * @author Eric Berryman
 *
 */
public class ScanDataSourceProvider
        extends ConfigurableDataSourceProvider<ScanDataSource, ScanDataSourceConfiguration> {

    public ScanDataSourceProvider() {
        super(ScanDataSourceConfiguration.class);
    }

    @Override
    public String getName() {
        return "scan";
    }

}
