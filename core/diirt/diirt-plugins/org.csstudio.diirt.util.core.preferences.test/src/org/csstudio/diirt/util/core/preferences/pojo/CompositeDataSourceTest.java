/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.core.preferences.pojo;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;

import org.csstudio.diirt.util.core.preferences.pojo.CompositeDataSource.DataSourceProtocol;
import org.junit.Test;


/**
 * @author claudiorosati, European Spallation Source ERIC
 * @version 1.0.0 13 Dec 2016
 */
public class CompositeDataSourceTest {

    @Test
    public void testConstructors ( ) {

        CompositeDataSource cds1 = new CompositeDataSource();

        assertNull(cds1.defaultDataSource);
        assertEquals("://", cds1.delimiter);

        cds1.defaultDataSource = DataSourceProtocol.ca;
        cds1.delimiter = "x@x";

        assertEquals(DataSourceProtocol.ca, cds1.defaultDataSource);
        assertEquals("x@x", cds1.delimiter);

        CompositeDataSource cds2 = new CompositeDataSource(DataSourceProtocol.ca, "x@x");

        assertEquals(cds1, cds2);

    }

    @Test
    public void testDataSourceProtocol ( ) {
        assertEquals(DataSourceProtocol.none, DataSourceProtocol.fromString("none"));
        assertEquals(DataSourceProtocol.ca, DataSourceProtocol.fromString("ca"));
        assertEquals(DataSourceProtocol.file, DataSourceProtocol.fromString("file"));
        assertEquals(DataSourceProtocol.loc, DataSourceProtocol.fromString("loc"));
        assertEquals(DataSourceProtocol.pva, DataSourceProtocol.fromString("pva"));
        assertEquals(DataSourceProtocol.sim, DataSourceProtocol.fromString("sim"));
        assertEquals(DataSourceProtocol.sys, DataSourceProtocol.fromString("sys"));
        assertEquals(DataSourceProtocol.none, DataSourceProtocol.fromString("FUFFA"));
    }

    /**
     * This test is made to fail if the structure of {@link CompositeDataSource}
     * changed, ensuring that also the test classes are changed too.
     */
    @Test
    public void testStructure ( ) throws NoSuchFieldException, SecurityException {

        Field[] fields = CompositeDataSource.class.getDeclaredFields();

        assertEquals(2, fields.length);
        assertEquals(DataSourceProtocol.class, CompositeDataSource.class.getDeclaredField("defaultDataSource").getType());
        assertEquals(String.class, CompositeDataSource.class.getDeclaredField("delimiter").getType());

    }

}
