package org.csstudio.utility.pv.epics;

import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ValueFactory;

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
