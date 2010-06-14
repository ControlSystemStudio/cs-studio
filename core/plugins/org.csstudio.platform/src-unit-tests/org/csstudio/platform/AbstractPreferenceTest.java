/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 *
 * $Id$
 */
package org.csstudio.platform;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for the AbstractPreference-Class.
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 14.06.2010
 */
public class AbstractPreferenceTest {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testPreferencesDefaults() {
        // Here the state of the preferences after their declaration is tested.0
        assertEquals("Some string", Preference.STRING_PREF.getValue());
        assertEquals("Some string", Preference.STRING_PREF.getDefaultValue());
        assertEquals("Some string", Preference.STRING_PREF.getDefaultAsString());
        assertEquals("String_Pref", Preference.STRING_PREF.getKeyAsString());

        assertEquals((Integer) 1234, Preference.INT_PREF.getValue());
        assertEquals((Integer) 1234, Preference.INT_PREF.getDefaultValue());
        assertEquals("1234", Preference.INT_PREF.getDefaultAsString());
        assertEquals("Int_Pref", Preference.INT_PREF.getKeyAsString());

        assertEquals((Long) 1234L, Preference.LONG_PREF.getValue());
        assertEquals((Long) 1234L, Preference.LONG_PREF.getDefaultValue());
        assertEquals("1234", Preference.LONG_PREF.getDefaultAsString());
        assertEquals("Long_Pref", Preference.LONG_PREF.getKeyAsString());

        assertEquals((Float) 12.34f, Preference.FLOAT_PREF.getValue());
        assertEquals((Float) 12.34f, Preference.FLOAT_PREF.getDefaultValue());
        assertEquals("12.34", Preference.FLOAT_PREF.getDefaultAsString());
        assertEquals("Float_Pref", Preference.FLOAT_PREF.getKeyAsString());

        assertEquals((Double) 12.34, Preference.DOUBLE_PREF.getValue());
        assertEquals((Double) 12.34, Preference.DOUBLE_PREF.getDefaultValue());
        assertEquals("12.34", Preference.DOUBLE_PREF.getDefaultAsString());
        assertEquals("Double_Pref", Preference.DOUBLE_PREF.getKeyAsString());

        assertEquals(true, Preference.BOOLEAN_PREF.getValue());
        assertEquals(true, Preference.BOOLEAN_PREF.getDefaultValue());
        assertEquals("true", Preference.BOOLEAN_PREF.getDefaultAsString());
        assertEquals("Boolean_Pref", Preference.BOOLEAN_PREF.getKeyAsString());
    }

    @Test
    public void testPreferencesService() {
        // Here the use of the preferences service is tested.

        // As an example the string-, double- and boolean-based preferences get different values
        final IEclipsePreferences prefs = new DefaultScope().getNode(Preference.STRING_PREF.getPluginID());
        prefs.put(Preference.STRING_PREF.getKeyAsString(), "Some other string");
        prefs.put(Preference.DOUBLE_PREF.getKeyAsString(), "7654.321");
        prefs.put(Preference.BOOLEAN_PREF.getKeyAsString(), "false"); // Except 'true' nearly any string will do, e.g. 'f' or 'flase'

        assertEquals("Some other string", Preference.STRING_PREF.getValue());
        assertEquals("Some string", Preference.STRING_PREF.getDefaultValue());
        assertEquals("Some string", Preference.STRING_PREF.getDefaultAsString());

        assertEquals((Double) 7654.321, Preference.DOUBLE_PREF.getValue());
        assertEquals((Double) 12.34, Preference.DOUBLE_PREF.getDefaultValue());
        assertEquals("12.34", Preference.DOUBLE_PREF.getDefaultAsString());

        assertEquals(false, Preference.BOOLEAN_PREF.getValue());
        assertEquals(true, Preference.BOOLEAN_PREF.getDefaultValue());
        assertEquals("true", Preference.BOOLEAN_PREF.getDefaultAsString());
    }

    private static class Preference<T> extends AbstractPreference<T> {
        public static final Preference<String> STRING_PREF = new Preference<String>("String_Pref",
                                                                                    "Some string");
        public static final Preference<Integer> INT_PREF = new Preference<Integer>("Int_Pref", 1234);
        public static final Preference<Long> LONG_PREF = new Preference<Long>("Long_Pref", 1234L);
        public static final Preference<Float> FLOAT_PREF = new Preference<Float>("Float_Pref",
                                                                                 12.34f);
        public static final Preference<Double> DOUBLE_PREF = new Preference<Double>("Double_Pref",
                                                                                    12.34);
        public static final Preference<Boolean> BOOLEAN_PREF = new Preference<Boolean>("Boolean_Pref",
                                                                                       true);

        private Preference(final String keyAsString, final T defaultValue) {
            super(keyAsString, defaultValue);
        }

        @Override
        protected String getPluginID() {
            return "QualifierForTest";
        }

    }

}
