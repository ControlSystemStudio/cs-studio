/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.model;

import org.csstudio.archive.vtype.ArchiveVNumber;
import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.Display;
import org.epics.pvmanager.data.VType;
import org.epics.pvmanager.data.ValueFactory;
import org.epics.pvmanager.util.NumberFormats;
import org.epics.util.time.Timestamp;

/** Helper for creating test data
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TestValueFactory
{
	final public static Display display = ValueFactory.newDisplay(0.0, 1.0, 2.0, "Eggs", NumberFormats.format(2), 8.0, 9.0, 10.0, 0.0, 10.0);

    public static VType getDouble(double value)
    {
    	return new ArchiveVNumber(Timestamp.now(), AlarmSeverity.NONE, "OK", display, value);
    }
}
