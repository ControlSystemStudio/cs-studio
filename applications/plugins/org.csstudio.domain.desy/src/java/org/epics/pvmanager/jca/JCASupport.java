/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import org.epics.pvmanager.DataSource;

/**
 * Adds support for CA types as defined in JCA.
 *
 * @deprecated Create a {@link JCADataSource} instead.
 * @author carcassi
 */
@Deprecated
public class JCASupport {

    @Deprecated
    public static DataSource jca() {
        return JCADataSource.INSTANCE;
    }
    
}
