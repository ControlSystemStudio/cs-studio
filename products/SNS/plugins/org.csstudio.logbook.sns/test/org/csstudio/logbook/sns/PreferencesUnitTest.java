/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/** JUnit test for {@link Preferences}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PreferencesUnitTest
{
    @Test
    public void testPreferences() throws Exception
    {
        assertThat(Preferences.getDefaultLogbook(), equalTo("Scratch Pad"));
    }
}
