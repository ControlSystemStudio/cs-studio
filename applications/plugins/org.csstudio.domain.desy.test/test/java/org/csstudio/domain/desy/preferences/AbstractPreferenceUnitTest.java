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

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test for the AbstractPreference-Class.
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 14.06.2010
 */
// CHECKSTYLE OFF: AbstractClassName
public class AbstractPreferenceUnitTest {
// CHECKSTYLE ON: AbstractClassName


    @Test
    public void testPreferencesDefaults() {
        // Here the state of the preferences after their declaration is tested.0
        assertEquals("Some string", UnitTestPreference.STRING_PREF.getValue());
        assertEquals("Some string", UnitTestPreference.STRING_PREF.getDefaultValue());
        assertEquals("Some string", UnitTestPreference.STRING_PREF.getDefaultAsString());
        assertEquals("Unit_String_Pref", UnitTestPreference.STRING_PREF.getKeyAsString());

        assertEquals((Integer) 1234, UnitTestPreference.INT_PREF.getValue());
        assertEquals((Integer) 1234, UnitTestPreference.INT_PREF.getDefaultValue());
        assertEquals("1234", UnitTestPreference.INT_PREF.getDefaultAsString());
        assertEquals("Unit_Int_Pref", UnitTestPreference.INT_PREF.getKeyAsString());

        assertEquals((Long) 1234L, UnitTestPreference.LONG_PREF.getValue());
        assertEquals((Long) 1234L, UnitTestPreference.LONG_PREF.getDefaultValue());
        assertEquals("1234", UnitTestPreference.LONG_PREF.getDefaultAsString());
        assertEquals("Unit_Long_Pref", UnitTestPreference.LONG_PREF.getKeyAsString());

        assertEquals((Float) 12.34f, UnitTestPreference.FLOAT_PREF.getValue());
        assertEquals((Float) 12.34f, UnitTestPreference.FLOAT_PREF.getDefaultValue());
        assertEquals("12.34", UnitTestPreference.FLOAT_PREF.getDefaultAsString());
        assertEquals("Unit_Float_Pref", UnitTestPreference.FLOAT_PREF.getKeyAsString());

        assertEquals((Double) 12.34, UnitTestPreference.DOUBLE_PREF.getValue());
        assertEquals((Double) 12.34, UnitTestPreference.DOUBLE_PREF.getDefaultValue());
        assertEquals("12.34", UnitTestPreference.DOUBLE_PREF.getDefaultAsString());
        assertEquals("Unit_Double_Pref", UnitTestPreference.DOUBLE_PREF.getKeyAsString());

        assertEquals(true, UnitTestPreference.BOOLEAN_PREF.getValue());
        assertEquals(true, UnitTestPreference.BOOLEAN_PREF.getDefaultValue());
        assertEquals("true", UnitTestPreference.BOOLEAN_PREF.getDefaultAsString());
        assertEquals("Unit_Boolean_Pref", UnitTestPreference.BOOLEAN_PREF.getKeyAsString());
    }


    @Test
    public void testGetAllPreferences() {
        final List<AbstractPreference<?>> prefs = UnitTestPreference.BOOLEAN_PREF.getAllPreferences();

        Assert.assertEquals(7, prefs.size());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidPrefWithValidator() {
        @SuppressWarnings("unused")
        final UnitTestPreference<Double> pref =
            (UnitTestPreference<Double>) new UnitTestPreference<Double>("Unit_Double_Pref_With_Val", -1.0)
                                             .addValidator(new MinMaxPreferenceValidator<Double>(0.0, 100.0));

    }
}
