/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.pv.epics;

import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ValueFactory;

/** Helper for creating the EPICS related severities.
 *  @author Kay Kasemir
 */
public class SeverityUtil
{
    /** @return Severity for code 0, 1, 2, 3 as used by EPICS. */
    public static ISeverity forCode(int code)
    {
        switch (code)
        {
        case 0: return ValueFactory.createOKSeverity();
        case 1: return ValueFactory.createMinorSeverity();
        case 2: return ValueFactory.createMajorSeverity();
        default: return ValueFactory.createInvalidSeverity();
        }
    }

}
