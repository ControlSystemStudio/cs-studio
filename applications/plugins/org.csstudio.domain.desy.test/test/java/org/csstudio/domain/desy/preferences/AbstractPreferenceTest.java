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
package org.csstudio.domain.desy.preferences;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.annotation.Nonnull;

import junit.framework.Assert;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.junit.Test;

/**
 * Test for the AbstractPreference-Class.
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 14.06.2010
 */
// CHECKSTYLE:OFF The class to test is abstract, but not its test...
public class AbstractPreferenceTest {
// CHECKSTYLE:ON

    @Test
    public void testPreferencesDefaults() {
        // Here the state of the preferences after their declaration is tested.0
        assertEquals("Some string", TestPreference.STRING_PREF.getValue());
        assertEquals("Some string", TestPreference.STRING_PREF.getDefaultValue());
        assertEquals("Some string", TestPreference.STRING_PREF.getDefaultAsString());
        assertEquals("String_Pref", TestPreference.STRING_PREF.getKeyAsString());

        assertEquals((Integer) 1234, TestPreference.INT_PREF.getValue());
        assertEquals((Integer) 1234, TestPreference.INT_PREF.getDefaultValue());
        assertEquals("1234", TestPreference.INT_PREF.getDefaultAsString());
        assertEquals("Int_Pref", TestPreference.INT_PREF.getKeyAsString());

        assertEquals((Long) 1234L, TestPreference.LONG_PREF.getValue());
        assertEquals((Long) 1234L, TestPreference.LONG_PREF.getDefaultValue());
        assertEquals("1234", TestPreference.LONG_PREF.getDefaultAsString());
        assertEquals("Long_Pref", TestPreference.LONG_PREF.getKeyAsString());

        assertEquals((Float) 12.34f, TestPreference.FLOAT_PREF.getValue());
        assertEquals((Float) 12.34f, TestPreference.FLOAT_PREF.getDefaultValue());
        assertEquals("12.34", TestPreference.FLOAT_PREF.getDefaultAsString());
        assertEquals("Float_Pref", TestPreference.FLOAT_PREF.getKeyAsString());

        assertEquals((Double) 12.34, TestPreference.DOUBLE_PREF.getValue());
        assertEquals((Double) 12.34, TestPreference.DOUBLE_PREF.getDefaultValue());
        assertEquals("12.34", TestPreference.DOUBLE_PREF.getDefaultAsString());
        assertEquals("Double_Pref", TestPreference.DOUBLE_PREF.getKeyAsString());

        assertEquals(true, TestPreference.BOOLEAN_PREF.getValue());
        assertEquals(true, TestPreference.BOOLEAN_PREF.getDefaultValue());
        assertEquals("true", TestPreference.BOOLEAN_PREF.getDefaultAsString());
        assertEquals("Boolean_Pref", TestPreference.BOOLEAN_PREF.getKeyAsString());
    }

    @Test
    public void testPreferencesService() {
        // Here the use of the preferences service is tested.

        // As an example the string-, double- and boolean-based preferences get different values
        final IEclipsePreferences prefs = new DefaultScope().getNode(TestPreference.STRING_PREF.getPluginID());
        prefs.put(TestPreference.STRING_PREF.getKeyAsString(), "Some other string");
        prefs.put(TestPreference.DOUBLE_PREF.getKeyAsString(), "7654.321");
        prefs.put(TestPreference.BOOLEAN_PREF.getKeyAsString(), "false"); // Except 'true' nearly any string will do, e.g. 'f' or 'false'

        assertEquals("Some other string", TestPreference.STRING_PREF.getValue());
        assertEquals("Some string", TestPreference.STRING_PREF.getDefaultValue());
        assertEquals("Some string", TestPreference.STRING_PREF.getDefaultAsString());

        assertEquals((Double) 7654.321, TestPreference.DOUBLE_PREF.getValue());
        assertEquals((Double) 12.34, TestPreference.DOUBLE_PREF.getDefaultValue());
        assertEquals("12.34", TestPreference.DOUBLE_PREF.getDefaultAsString());

        assertEquals(false, TestPreference.BOOLEAN_PREF.getValue());
        assertEquals(true, TestPreference.BOOLEAN_PREF.getDefaultValue());
        assertEquals("true", TestPreference.BOOLEAN_PREF.getDefaultAsString());
    }
    
    @Test
    public void testPreferencesServiceWithValidatedPrefs() {
        // Here the use of the preferences service is tested.
        
        // As an example the string-, double- and boolean-based preferences get different values
        final IEclipsePreferences prefs = new DefaultScope().getNode(TestPreference.STRING_PREF.getPluginID());
        prefs.put(TestPreference.DOUBLE_PREF_WITH_VAL.getKeyAsString(), "50.0"); // Within bounds
        assertEquals(Double.valueOf(50.0), TestPreference.DOUBLE_PREF_WITH_VAL.getValue());
        
        prefs.put(TestPreference.DOUBLE_PREF_WITH_VAL.getKeyAsString(), "0.0"); // within bounds
        assertEquals(Double.valueOf(0.0), TestPreference.DOUBLE_PREF_WITH_VAL.getValue());
        
        prefs.put(TestPreference.DOUBLE_PREF_WITH_VAL.getKeyAsString(), "100.0"); // within bounds
        assertEquals(Double.valueOf(100.0), TestPreference.DOUBLE_PREF_WITH_VAL.getValue());
        
        prefs.put(TestPreference.DOUBLE_PREF_WITH_VAL.getKeyAsString(), "101.0"); // out of bounds
        assertEquals(Double.valueOf(12.34), TestPreference.DOUBLE_PREF_WITH_VAL.getValue());
        
        prefs.put(TestPreference.DOUBLE_PREF_WITH_VAL.getKeyAsString(), "-1.0"); // out of bounds
        assertEquals(Double.valueOf(12.34), TestPreference.DOUBLE_PREF_WITH_VAL.getValue());
    }
    


    @Test
    public void testGetAllPreferences() {
        final List<AbstractPreference<?>> prefs = TestPreference.BOOLEAN_PREF.getAllPreferences();

        Assert.assertEquals(7, prefs.size());
    }

    /**
     * Test Helper class.  
     * 
     * @author bknerr
     * @since 20.04.2011
     * @param <T> the preference type
     */
    private static final class TestPreference<T> extends AbstractPreference<T> {

        /**
         * For test purposes
         */
        @SuppressWarnings("unused")
        private final Integer _notTestPreference = Integer.valueOf(0);

        public static final TestPreference<String> STRING_PREF =
            new TestPreference<String>("String_Pref", "Some string");

        public static final TestPreference<Integer> INT_PREF =
            new TestPreference<Integer>("Int_Pref", 1234);

        public static final TestPreference<Long> LONG_PREF =
            new TestPreference<Long>("Long_Pref", 1234L);

        public static final TestPreference<Float> FLOAT_PREF =
            new TestPreference<Float>("Float_Pref", 12.34f);

        public static final TestPreference<Double> DOUBLE_PREF =
            new TestPreference<Double>("Double_Pref", 12.34);

        public static final TestPreference<Double> DOUBLE_PREF_WITH_VAL =
            (TestPreference<Double>) new TestPreference<Double>("Double_Pref", 
                                   12.34).addValidator(new MinMaxPreferenceValidator<Double>(0.0, 100.0));

        public static final TestPreference<Boolean> BOOLEAN_PREF =
            new TestPreference<Boolean>("Boolean_Pref", true);

        /**
         * For test purposes
         */
        @SuppressWarnings("unused")
        public static final Integer STATIC_NOT_TESTPREFERENCE = new Integer(0);



        /**
         * The following two lines of a non static instance field of type <itself> enable an
         * infinite recursion while constructing the object => stack overflow.
         *
         * public final TestPreference<Boolean> NOT_STATIC =
         *     new TestPreference<Boolean>("NOT_STATIC", true);
         */
        private TestPreference(@Nonnull final String keyAsString, 
                               @Nonnull final T defaultValue) {
            super(keyAsString, defaultValue);
        }

        @Override
        @Nonnull 
        public String getPluginID() {
            return "QualifierForTest";
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        @Nonnull 
        protected Class<? extends AbstractPreference<T>> getClassType() {
            return (Class<? extends AbstractPreference<T>>) TestPreference.class;
        }

    }

}
