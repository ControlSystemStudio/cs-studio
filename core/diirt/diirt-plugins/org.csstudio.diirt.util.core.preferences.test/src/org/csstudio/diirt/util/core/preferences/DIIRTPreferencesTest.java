/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.core.preferences;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess;
import org.csstudio.diirt.util.core.preferences.pojo.CompositeDataSource;
import org.csstudio.diirt.util.core.preferences.pojo.CompositeDataSource.DataSourceProtocol;
import org.csstudio.diirt.util.core.preferences.pojo.DataSourceOptions;
import org.csstudio.diirt.util.core.preferences.pojo.DataSourceOptions.MonitorMask;
import org.csstudio.diirt.util.core.preferences.pojo.DataSourceOptions.VariableArraySupport;
import org.csstudio.diirt.util.core.preferences.pojo.DataSources;
import org.csstudio.diirt.util.core.preferences.pojo.JCAContext;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.junit.Test;


/**
 * @author claudiorosati, European Spallation Source ERIC
 * @version 1.0.0 22 Dec 2016
 */
public class DIIRTPreferencesTest {

    /**
     * Test method for {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#getBoolean(java.lang.String)},
     * {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#getDefaultBoolean(java.lang.String)},
     * {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#setBoolean(java.lang.String, boolean)},
     * and {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#setDefaultBoolean(java.lang.String, boolean)}.
     */
    @Test
    public final void testBoolean ( ) {

        final String pname = "property-name";
        DIIRTPreferences store = new DIIRTPreferences(new TestScope());

        store.setDefaultBoolean(pname, true);
        assertEquals(true, store.getDefaultBoolean(pname));
        assertEquals(true, store.getBoolean(pname));

        store.setDefaultBoolean(pname, false);
        assertEquals(false, store.getDefaultBoolean(pname));
        assertEquals(false, store.getBoolean(pname));

        store.setBoolean(pname, true);
        assertEquals(false, store.getDefaultBoolean(pname));
        assertEquals(true, store.getBoolean(pname));

        store.setToDefault(pname);
        assertEquals(false, store.getDefaultBoolean(pname));
        assertEquals(false, store.getBoolean(pname));

    }

    /**
     * Test method for {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#defaultPreferenceName(java.lang.String)}.
     */
    @Test
    public final void testDefaultPreferenceName ( ) {

        String name = "test.property";

        assertEquals("_default_." + name, DIIRTPreferences.defaultPreferenceName(name));

        name = "";

        assertEquals("_default_." + name, DIIRTPreferences.defaultPreferenceName(name));

        name = null;

        assertEquals("_default_." + name, DIIRTPreferences.defaultPreferenceName(name));

    }

    /**
     * Test method for {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#getDefaultDouble(java.lang.String)},
     * {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#getDouble(java.lang.String)},
     * {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#setDefaultDouble(java.lang.String, double)},
     * and {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#setDouble(java.lang.String, double)}.
     */
    @Test
    public final void testDouble ( ) {

        final String pname = "property-name";
        DIIRTPreferences store = new DIIRTPreferences(new TestScope());

        store.setDefaultDouble(pname, 0.234);
        assertEquals(0.234, store.getDefaultDouble(pname), 0.000001);
        assertEquals(0.234, store.getDouble(pname), 0.000001);

        store.setDefaultDouble(pname, 432.1);
        assertEquals(432.1, store.getDefaultDouble(pname), 0.000001);
        assertEquals(432.1, store.getDouble(pname), 0.000001);

        store.setDouble(pname, 34.21);
        assertEquals(432.1, store.getDefaultDouble(pname), 0.000001);
        assertEquals(34.21, store.getDouble(pname), 0.000001);

        store.setToDefault(pname);
        assertEquals(432.1, store.getDefaultDouble(pname), 0.000001);
        assertEquals(432.1, store.getDouble(pname), 0.000001);

    }

    /**
     * Test method for {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#getDefaultFloat(java.lang.String)},
     * {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#getFloat(java.lang.String)},
     * {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#setDefaultFloat(java.lang.String, float)},
     * and {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#setFloat(java.lang.String, float)}.
     */
    @Test
    public final void testFloat ( ) {

        final String pname = "property-name";
        DIIRTPreferences store = new DIIRTPreferences(new TestScope());

        store.setDefaultFloat(pname, 123.456F);
        assertEquals(123.456F, store.getDefaultFloat(pname), 0.000001F);
        assertEquals(123.456F, store.getFloat(pname), 0.000001F);

        store.setDefaultFloat(pname, 234.567F);
        assertEquals(234.567F, store.getDefaultFloat(pname), 0.000001F);
        assertEquals(234.567F, store.getFloat(pname), 0.000001F);

        store.setFloat(pname, 345.678F);
        assertEquals(234.567F, store.getDefaultFloat(pname), 0.000001F);
        assertEquals(345.678F, store.getFloat(pname), 0.000001F);

        store.setToDefault(pname);
        assertEquals(234.567F, store.getDefaultFloat(pname), 0.000001F);
        assertEquals(234.567F, store.getFloat(pname), 0.000001F);

    }

    /**
     * Test method for {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#getDIIRTHome()}.
     */
    @Test
    public final void testGetDIIRTHome ( ) throws IOException {

        File path1 = Files.createTempDirectory("DIIRT-TEST").toFile().getCanonicalFile();
        Path path2 = Files.createDirectory(Paths.get(path1.toString(), DataSources.DATASOURCES_DIR));

        Files.createFile(Paths.get(path2.toString(), DataSources.DATASOURCES_FILE));

        DIIRTPreferences store = new DIIRTPreferences(new TestScope());

        store.setString(DIIRTPreferences.PREF_CONFIGURATION_DIRECTORY, path1.toString());
        assertEquals(path1.toString(), store.getDIIRTHome());

        store.setString(DIIRTPreferences.PREF_CONFIGURATION_DIRECTORY, path2.toString());
        assertEquals(path2.toString(), store.getDIIRTHome());

    }

    /**
     * Test method for {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#getDefaultInteger(java.lang.String)},
     * {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#getInteger(java.lang.String)},
     * {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#setDefaultInteger(java.lang.String, int)},
     * and {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#setInteger(java.lang.String, int)}.
     */
    @Test
    public final void testInteger ( ) {

        final String pname = "property-name";
        DIIRTPreferences store = new DIIRTPreferences(new TestScope());

        store.setDefaultInteger(pname, 1234);
        assertEquals(1234, store.getDefaultInteger(pname));
        assertEquals(1234, store.getInteger(pname));

        store.setDefaultInteger(pname, 2345);
        assertEquals(2345, store.getDefaultInteger(pname));
        assertEquals(2345, store.getInteger(pname));

        store.setInteger(pname, 3456);
        assertEquals(2345, store.getDefaultInteger(pname));
        assertEquals(3456, store.getInteger(pname));

        store.setToDefault(pname);
        assertEquals(2345, store.getDefaultInteger(pname));
        assertEquals(2345, store.getInteger(pname));

    }

    /**
     * Test method for {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#isDefaultPreferenceName(java.lang.String)}.
     */
    @Test
    public final void testIsDefaultPreferenceName ( ) {

        String name = "test.property";

        assertTrue(DIIRTPreferences.isDefaultPreferenceName("_default_." + name));
        assertTrue(DIIRTPreferences.isDefaultPreferenceName(DIIRTPreferences.defaultPreferenceName(name)));

        name = "";

        assertTrue(DIIRTPreferences.isDefaultPreferenceName("_default_." + name));
        assertTrue(DIIRTPreferences.isDefaultPreferenceName(DIIRTPreferences.defaultPreferenceName(name)));

        name = null;

        assertTrue(DIIRTPreferences.isDefaultPreferenceName("_default_." + name));
        assertTrue(DIIRTPreferences.isDefaultPreferenceName(DIIRTPreferences.defaultPreferenceName(name)));

    }

    /**
     * Test method for {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#getDefaultLong(java.lang.String)},
     * {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#getLong(java.lang.String)},
     * {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#setDefaultLong(java.lang.String, long)},
     * and {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#setLong(java.lang.String, long)}.
     */
    @Test
    public final void testLong ( ) {

        final String pname = "property-name";
        DIIRTPreferences store = new DIIRTPreferences(new TestScope());

        store.setDefaultLong(pname, 234L);
        assertEquals(234L, store.getDefaultLong(pname));
        assertEquals(234L, store.getLong(pname));

        store.setDefaultLong(pname, 345L);
        assertEquals(345L, store.getDefaultLong(pname));
        assertEquals(345L, store.getLong(pname));

        store.setLong(pname, 456L);
        assertEquals(345L, store.getDefaultLong(pname));
        assertEquals(456L, store.getLong(pname));

        store.setToDefault(pname);
        assertEquals(345L, store.getDefaultLong(pname));
        assertEquals(345L, store.getLong(pname));

    }

    /**
     * Test method for {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#resolveAndVerifyDIIRTPath(java.lang.String)}.
     */
    @Test
    public final void testResolveAndVerifyDIIRTPath ( ) throws IOException {

        assertEquals("The given DIIRT path is null.", DIIRTPreferences.resolveAndVerifyDIIRTPath(null));
        assertEquals("The given DIIRT path is whitespace or empty.", DIIRTPreferences.resolveAndVerifyDIIRTPath(""));
        assertEquals("The given DIIRT path is whitespace or empty.", DIIRTPreferences.resolveAndVerifyDIIRTPath(" "));
        assertEquals("The given DIIRT path is whitespace or empty.", DIIRTPreferences.resolveAndVerifyDIIRTPath("    "));

        File path1 = Files.createTempDirectory("DIIRT-TEST").toFile().getCanonicalFile();

        assertEquals("The given path doesn't contain DIIRT configuration [" + path1.toString() + "].", DIIRTPreferences.resolveAndVerifyDIIRTPath(path1.toString()));

        File path2 = new File(path1, "fuffa");

        assertEquals("The given DIIRT path doesn't exist [" + path2.toString() + "].", DIIRTPreferences.resolveAndVerifyDIIRTPath(path2.toString()));

        Path path3 = Files.createDirectory(Paths.get(path1.toString(), DataSources.DATASOURCES_DIR));

        Files.createFile(Paths.get(path3.toString(), DataSources.DATASOURCES_FILE));
        assertNull(DIIRTPreferences.resolveAndVerifyDIIRTPath(path1.toString()));

    }

    /**
     * Test method for {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#resolvePlatformPath(java.lang.String)}.
     */
    @Test
    public final void testResolvePlatformPath ( ) throws Throwable {

        try {
            DIIRTPreferences.resolvePlatformPath(null);
        } catch ( NullPointerException ex ) {
            assertEquals("Null 'path'.", ex.getMessage());
        } catch ( Throwable ex ) {
            throw ex;
        }

        try {
            DIIRTPreferences.resolvePlatformPath("");
        } catch ( IllegalArgumentException ex ) {
            assertEquals("Empty path.", ex.getMessage());
        } catch ( Throwable ex ) {
            throw ex;
        }

        try {
            DIIRTPreferences.resolvePlatformPath(" ");
        } catch ( IllegalArgumentException ex ) {
            assertEquals("Empty path.", ex.getMessage());
        } catch ( Throwable ex ) {
            throw ex;
        }

        try {
            DIIRTPreferences.resolvePlatformPath("      ");
        } catch ( IllegalArgumentException ex ) {
            assertEquals("Empty path.", ex.getMessage());
        } catch ( Throwable ex ) {
            throw ex;
        }

        assertThat(DIIRTPreferences.resolvePlatformPath("@user.home/"), not(startsWith("@user.home")));

        try {
            DIIRTPreferences.resolvePlatformPath("platform:/config/fuffa");
        } catch ( MalformedURLException ex ) {
            assertEquals("unknown protocol: platform", ex.getMessage());
        } catch ( Throwable ex ) {
            throw ex;
        }

    }

    /**
     * Test method for {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#getDefaultString(java.lang.String)},
     * {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#getString(java.lang.String)},
     * {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#setDefaultString(java.lang.String, java.lang.String)},
     * and {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#setString(java.lang.String, java.lang.String)}.
     */
    @Test
    public final void testString ( ) {

        final String pname = "property-name";
        DIIRTPreferences store = new DIIRTPreferences(new TestScope());

        store.setDefaultString(pname, "qwerty");
        assertEquals("qwerty", store.getDefaultString(pname));
        assertEquals("qwerty", store.getString(pname));

        store.setDefaultString(pname, "asdfgh");
        assertEquals("asdfgh", store.getDefaultString(pname));
        assertEquals("asdfgh", store.getString(pname));

        store.setString(pname, "zxcvbn");
        assertEquals("asdfgh", store.getDefaultString(pname));
        assertEquals("zxcvbn", store.getString(pname));

        store.setToDefault(pname);
        assertEquals("asdfgh", store.getDefaultString(pname));
        assertEquals("asdfgh", store.getString(pname));

    }

    /**
     * This test is made to fail if the structure of {@link DataSources}
     * changed, ensuring that also the test classes are changed too.
     */
    @Test
    public void testStructure ( ) throws NoSuchFieldException, SecurityException {

        //  First is the number of instance variables.
        //  Second is the number of static variables.
        assertEquals(2 + 13, Arrays.asList(DIIRTPreferences.class.getDeclaredFields()).stream().filter(f -> !f.isSynthetic()).count());

        assertEquals(IScopeContext.class, DIIRTPreferences.class.getDeclaredField("scopeContext").getType());
        assertEquals(IPreferencesService.class, DIIRTPreferences.class.getDeclaredField("preferencesService").getType());

        assertEquals(String.class,  DIIRTPreferences.class.getDeclaredField("PREF_CONFIGURATION_DIRECTORY").getType());
        assertEquals(Logger.class,  DIIRTPreferences.class.getDeclaredField("LOGGER").getType());
        assertEquals(boolean.class, DIIRTPreferences.class.getDeclaredField("BOOLEAN_DEFAULT_DEFAULT").getType());
        assertEquals(String.class,  DIIRTPreferences.class.getDeclaredField("DEFAULT_PREFIX").getType());
        assertEquals(double.class,  DIIRTPreferences.class.getDeclaredField("DOUBLE_DEFAULT_DEFAULT").getType());
        assertEquals(float.class,   DIIRTPreferences.class.getDeclaredField("FLOAT_DEFAULT_DEFAULT").getType());
        assertEquals(int.class,     DIIRTPreferences.class.getDeclaredField("INTEGER_DEFAULT_DEFAULT").getType());
        assertEquals(long.class,    DIIRTPreferences.class.getDeclaredField("LONG_DEFAULT_DEFAULT").getType());
        assertEquals(String.class,  DIIRTPreferences.class.getDeclaredField("PLATFORM_URI_PREFIX").getType());
        assertEquals(String.class,  DIIRTPreferences.class.getDeclaredField("PREF_DEFAULT_INITIALIZED").getType());
        assertEquals(String.class,  DIIRTPreferences.class.getDeclaredField("QUALIFIER").getType());
        assertEquals(String.class,  DIIRTPreferences.class.getDeclaredField("STRING_DEFAULT_DEFAULT").getType());
        assertEquals(String.class,  DIIRTPreferences.class.getDeclaredField("USER_HOME_PARAMETER").getType());

    }

    /**
     * Test method for {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#toFiles(java.io.File)},
     * and {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#fromFiles(java.io.File)}.
     */
    @Test
    public final void testToFromFiles ( ) throws IOException, JAXBException {

        DIIRTPreferences store1 = new DIIRTPreferences(new TestScope());
        DataSources ds1 = new DataSources(new CompositeDataSource(DataSourceProtocol.pva, "zxc"));
        ChannelAccess ca1 = new ChannelAccess(
            new DataSourceOptions(true, false, MonitorMask.ALARM, 234, true, VariableArraySupport.FALSE),
            new JCAContext("fuffa foffi faffo", false, 23, 43.2, 12345, 23414, 23453)
        );

        ds1.updateDefaultsAndValues(store1);
        ca1.updateDefaultsAndValues(store1);

        File confDir = Files.createTempDirectory("diirt.test").toFile();

        store1.toFiles(confDir);

        DIIRTPreferences store2 = new DIIRTPreferences(new TestScope());

        store2.fromFiles(confDir);

        DataSources ds2 = new DataSources(store2);
        ChannelAccess ca2 = new ChannelAccess(store2);

        assertEquals(ds1,  ds2);
        assertEquals(ca1,  ca2);

    }

    /**
     * Test method for {@link org.csstudio.diirt.util.core.preferences.DIIRTPreferences#toFiles(java.io.File)},
     * where some other files exist in the home folder to be copied.
     */
    @Test
    public final void testToFilesWithCopy ( ) throws IOException, JAXBException {

        DIIRTPreferences store1 = new DIIRTPreferences(new TestScope());
        DataSources ds1 = new DataSources(new CompositeDataSource(DataSourceProtocol.pva, "zxc"));
        ChannelAccess ca1 = new ChannelAccess(
            new DataSourceOptions(true, false, MonitorMask.ALARM, 234, true, VariableArraySupport.FALSE),
            new JCAContext("fuffa foffi faffo", false, 23, 43.2, 12345, 23414, 23453)
        );

        ds1.updateDefaultsAndValues(store1);
        ca1.updateDefaultsAndValues(store1);

        File confDirSrc = Files.createTempDirectory("diirt.test.source").toFile();

        store1.toFiles(confDirSrc);

        File dsDir = new File(confDirSrc, DataSources.DATASOURCES_DIR);

        FileUtils.write(new File(confDirSrc, "f01.txt"),  "Some text\nsplitted in 2 lines.", (String) null);
        FileUtils.write(new File(dsDir, "f02.xml"),  "Some other text\nsplitted in\n3 lines.", (String) null);
        FileUtils.write(new File(new File(dsDir, "aSubDir"), "f03.log"),  "Some more text\nsplitted in\n4 lines\nto be read.", (String) null);

        DIIRTPreferences store2 = new DIIRTPreferences(new TestScope());

        store2.fromFiles(confDirSrc);
        store2.setString(DIIRTPreferences.PREF_CONFIGURATION_DIRECTORY, confDirSrc.toString());

        DataSources ds2 = new DataSources(store2);
        ChannelAccess ca2 = new ChannelAccess(store2);
        File confDirDst = Files.createTempDirectory("diirt.test.destination").toFile();

        store2.toFiles(confDirDst);

        DIIRTPreferences store3 = new DIIRTPreferences(new TestScope());

        store3.fromFiles(confDirDst);

        DataSources ds3 = new DataSources(store3);
        ChannelAccess ca3 = new ChannelAccess(store3);

        assertEquals(ds2,  ds3);
        assertEquals(ca2,  ca3);

    }

}
