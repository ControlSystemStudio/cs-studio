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
// CHECKSTYLE OFF: AbstractClassName
public class AbstractPreferenceHeadlessTest {
// CHECKSTYLE ON: AbstractClassName

    @Test
    public void testPreferencesService() {
        // Here the use of the preferences service is tested.

        // As an example the string-, double- and boolean-based preferences get different values
        final IEclipsePreferences prefs = new DefaultScope().getNode(HeadlessTestPreference.STRING_PREF.getPluginID());
        prefs.put(HeadlessTestPreference.STRING_PREF.getKeyAsString(), "Some other string");
        prefs.put(HeadlessTestPreference.DOUBLE_PREF.getKeyAsString(), "7654.321");
        prefs.put(HeadlessTestPreference.BOOLEAN_PREF.getKeyAsString(), "false"); // Except 'true' nearly any string will do, e.g. 'f' or 'false'

        assertEquals("Some other string", HeadlessTestPreference.STRING_PREF.getValue());
        assertEquals("Some string", HeadlessTestPreference.STRING_PREF.getDefaultValue());
        assertEquals("Some string", HeadlessTestPreference.STRING_PREF.getDefaultAsString());

        assertEquals((Double) 7654.321, HeadlessTestPreference.DOUBLE_PREF.getValue());
        assertEquals((Double) 12.34, HeadlessTestPreference.DOUBLE_PREF.getDefaultValue());
        assertEquals("12.34", HeadlessTestPreference.DOUBLE_PREF.getDefaultAsString());

        assertEquals(false, HeadlessTestPreference.BOOLEAN_PREF.getValue());
        assertEquals(true, HeadlessTestPreference.BOOLEAN_PREF.getDefaultValue());
        assertEquals("true", HeadlessTestPreference.BOOLEAN_PREF.getDefaultAsString());
    }

    @Test
    public void testPreferencesServiceWithValidatedPrefs() {
        // Here the use of the preferences service is tested.

        // As an example the string-, double- and boolean-based preferences get different values
        final IEclipsePreferences prefs = new DefaultScope().getNode(HeadlessTestPreference.STRING_PREF.getPluginID());
        prefs.put(HeadlessTestPreference.DOUBLE_PREF_WITH_VAL.getKeyAsString(), "50.0"); // Within bounds
        assertEquals(Double.valueOf(50.0), HeadlessTestPreference.DOUBLE_PREF_WITH_VAL.getValue());

        prefs.put(HeadlessTestPreference.DOUBLE_PREF_WITH_VAL.getKeyAsString(), "0.0"); // within bounds
        assertEquals(Double.valueOf(0.0), HeadlessTestPreference.DOUBLE_PREF_WITH_VAL.getValue());

        prefs.put(HeadlessTestPreference.DOUBLE_PREF_WITH_VAL.getKeyAsString(), "100.0"); // within bounds
        assertEquals(Double.valueOf(100.0), HeadlessTestPreference.DOUBLE_PREF_WITH_VAL.getValue());

        prefs.put(HeadlessTestPreference.DOUBLE_PREF_WITH_VAL.getKeyAsString(), "101.0"); // out of bounds
        assertEquals(Double.valueOf(12.34), HeadlessTestPreference.DOUBLE_PREF_WITH_VAL.getValue());

        prefs.put(HeadlessTestPreference.DOUBLE_PREF_WITH_VAL.getKeyAsString(), "-1.0"); // out of bounds
        assertEquals(Double.valueOf(12.34), HeadlessTestPreference.DOUBLE_PREF_WITH_VAL.getValue());
    }

}
