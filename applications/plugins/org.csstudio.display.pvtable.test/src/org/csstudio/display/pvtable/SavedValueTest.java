/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.csstudio.display.pvtable.model.SavedScalarValue;
import org.csstudio.display.pvtable.model.SavedValue;
import org.epics.vtype.VDouble;
import org.epics.vtype.ValueFactory;
import org.junit.Test;

/** JUnit test of {@link SavedValue}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SavedValueTest
{
    @Test
    public void testSavedScalarValue() throws Exception
    {
        SavedValue saved = new SavedScalarValue("3.14");
        System.out.println(saved);
        assertThat(saved.toString(), equalTo("3.14"));
        
        VDouble value = ValueFactory.newVDouble(42.1);
        saved = SavedValue.forCurrentValue(value);
        System.out.println(saved);
        assertThat(saved.toString(), equalTo("42.1"));
        assertThat(saved.isEqualTo(value, 0.1), equalTo(true));

        saved = new SavedScalarValue("42.2");
        System.out.println(saved);
        assertThat(saved.isEqualTo(value, 0.2), equalTo(true));
        assertThat(saved.isEqualTo(value, 0.1), equalTo(false));
    }
}
