/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.core.preferences.pojo;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.csstudio.diirt.util.core.preferences.pojo.CompositeDataSource.DataSourceProtocol;
import org.eclipse.jface.preference.PreferenceStore;
import org.hamcrest.core.StringStartsWith;
import org.junit.Test;


/**
 * @author claudiorosati, European Spallation Source ERIC
 * @version 1.0.0 13 Dec 2016
 */
public class DataSourcesTest {

    public static final Logger LOGGER = Logger.getLogger(DataSourcesTest.class.getName());

    @Test
    public void testConstructors ( ) {

        DataSources ds1 = new DataSources();

        assertNull(ds1.compositeDataSource);
        assertEquals(DataSources.DATASOURCES_VERSION, ds1.version);

        CompositeDataSource cds1 = new CompositeDataSource(DataSourceProtocol.pva, "zxc");

        ds1.compositeDataSource = cds1;

        assertEquals(cds1, ds1.compositeDataSource);

        DataSources ds2 = new DataSources(new CompositeDataSource(DataSourceProtocol.pva, "zxc"));

        assertEquals(ds1,  ds2);

        PreferenceStore store = new PreferenceStore();

        store.setDefault(DataSources.PREF_DEFAULT, DataSourceProtocol.pva.name());
        store.setDefault(DataSources.PREF_DELIMITER, "zxc");

        DataSources ds3 = new DataSources(store);

        assertEquals(ds2,  ds3);

    }

    @Test
    public void testCopy ( ) {

        PreferenceStore source = new PreferenceStore();

        source.setDefault(DataSources.PREF_DEFAULT,   DataSourceProtocol.pva.name());
        source.setDefault(DataSources.PREF_DELIMITER, "zxc");

        source.setValue(DataSources.PREF_DEFAULT,   DataSourceProtocol.file.name());
        source.setValue(DataSources.PREF_DELIMITER, "asd");

        source.setDefault("fakeKey1", "fakeValue1");
        source.setValue("fakeKey2", "fakeValue2");

        PreferenceStore destination = new PreferenceStore();

        DataSources.copy(source, destination);

        assertEquals(DataSourceProtocol.pva.name(), destination.getDefaultString(DataSources.PREF_DEFAULT));
        assertEquals("zxc",                         destination.getDefaultString(DataSources.PREF_DELIMITER));

        assertEquals(DataSourceProtocol.file.name(), destination.getString(DataSources.PREF_DEFAULT));
        assertEquals("asd",                          destination.getString(DataSources.PREF_DELIMITER));

        assertNotEquals("fakeValue1", destination.getDefaultString("fakeKey1"));
        assertNotEquals("fakeValue2", destination.getString("fakeKey2"));

    }

    @Test
    public void testFromToFile ( ) throws IOException, JAXBException {

        File confDir = Files.createTempDirectory("diirt.test").toFile();
        DataSources ds1 = new DataSources(new CompositeDataSource(DataSourceProtocol.pva, "zxc"));

        ds1.toFile(confDir);

        DataSources ds2 = DataSources.fromFile(confDir);

        assertEquals(ds1, ds2);

        confDir = Files.createTempDirectory("diirt.test").toFile();
        ds2.compositeDataSource.delimiter = "345";
        ds2.compositeDataSource.defaultDataSource = DataSourceProtocol.file;

        ds2.toFile(confDir);

        DataSources ds3 = DataSources.fromFile(confDir);

        assertEquals(ds2, ds3);

        confDir = Files.createTempDirectory("diirt.test").toFile();
        ds2.version = "1.3.42";

        ds2.toFile(confDir);

        try {
            ds3 = DataSources.fromFile(confDir);
        } catch ( IOException ex ) {
            assertThat(ex.getMessage(), new StringStartsWith("Version mismatch:"));
        }

    }

    /**
     * This test is made to fail if the structure of {@link DataSources}
     * changed, ensuring that also the test classes are changed too.
     */
    @Test
    public void testStructure ( ) throws NoSuchFieldException, SecurityException {

        Field[] fields = DataSources.class.getDeclaredFields();

        assertEquals(2 + 5, fields.length);
        assertEquals(CompositeDataSource.class, DataSources.class.getDeclaredField("compositeDataSource").getType());
        assertEquals(String.class, DataSources.class.getDeclaredField("version").getType());

    }

    @Test
    public void testUpdate ( ) {

        PreferenceStore store = new PreferenceStore();

        store.setDefault(DataSources.PREF_DEFAULT,   DataSourceProtocol.pva.name());
        store.setDefault(DataSources.PREF_DELIMITER, "zxc");

        store.setValue(DataSources.PREF_DEFAULT,   DataSourceProtocol.file.name());
        store.setValue(DataSources.PREF_DELIMITER, "asd");

        store.setDefault("fakeKey1", "fakeValue1");
        store.setValue("fakeKey2", "fakeValue2");

        assertEquals(DataSourceProtocol.pva.name(), store.getDefaultString(DataSources.PREF_DEFAULT));
        assertEquals("zxc",                         store.getDefaultString(DataSources.PREF_DELIMITER));

        assertEquals(DataSourceProtocol.file.name(), store.getString(DataSources.PREF_DEFAULT));
        assertEquals("asd",                          store.getString(DataSources.PREF_DELIMITER));

        assertEquals("fakeValue1", store.getDefaultString("fakeKey1"));
        assertEquals("fakeValue2", store.getString("fakeKey2"));

        DataSources ds1 = new DataSources(new CompositeDataSource(DataSourceProtocol.ca, "fgh"));

        ds1.updateValues(store);

        assertEquals(DataSourceProtocol.pva.name(), store.getDefaultString(DataSources.PREF_DEFAULT));
        assertEquals("zxc",                         store.getDefaultString(DataSources.PREF_DELIMITER));

        assertEquals(DataSourceProtocol.ca.name(), store.getString(DataSources.PREF_DEFAULT));
        assertEquals("fgh",                          store.getString(DataSources.PREF_DELIMITER));

        assertEquals("fakeValue1", store.getDefaultString("fakeKey1"));
        assertEquals("fakeValue2", store.getString("fakeKey2"));

        DataSources ds2 = new DataSources(new CompositeDataSource(DataSourceProtocol.none, "poi"));

        ds2.updateDefaultsAndValues(store);

        assertEquals(DataSourceProtocol.none.name(), store.getDefaultString(DataSources.PREF_DEFAULT));
        assertEquals("poi",                          store.getDefaultString(DataSources.PREF_DELIMITER));

        assertEquals(DataSourceProtocol.none.name(), store.getString(DataSources.PREF_DEFAULT));
        assertEquals("poi",                          store.getString(DataSources.PREF_DELIMITER));

        assertEquals("fakeValue1", store.getDefaultString("fakeKey1"));
        assertEquals("fakeValue2", store.getString("fakeKey2"));

    }

    /**
     * This test is made to fail when {@link DataSources#DATASOURCES_VERSION} is updated.
     */
    @Test
    public void testVersion ( ) {
        assertEquals("1", new DataSources().version);
    }

}
