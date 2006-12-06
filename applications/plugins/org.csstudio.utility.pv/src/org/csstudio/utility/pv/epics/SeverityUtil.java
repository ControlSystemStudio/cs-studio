package org.csstudio.utility.pv.epics;

import org.csstudio.value.Severity;

/** Helper for creating some Severity values.
 *  @author Kay Kasemir
 */
public class SeverityUtil
{
    private static final Severity ok = new Severity()
    {
        public boolean hasValue()
        {   return true;  }

        public boolean isInvalid()
        {   return false;   }

        public boolean isMajor()
        {   return false;  }

        public boolean isMinor()
        {   return false;  }

        public boolean isOK()
        {   return true;  }

        public String toString()
        {   return Messages.SevOK; }
    };
    
    private static final Severity minor = new Severity()
    {
        public boolean hasValue()
        {   return true;  }

        public boolean isInvalid()
        {   return false;   }

        public boolean isMajor()
        {   return false;  }

        public boolean isMinor()
        {   return true;  }

        public boolean isOK()
        {   return false;  }

        public String toString()
        {   return Messages.SevMinor; }
    };
    
    private static final Severity mayor = new Severity()
    {
        public boolean hasValue()
        {   return true;  }

        public boolean isInvalid()
        {   return false;   }

        public boolean isMajor()
        {   return true;  }

        public boolean isMinor()
        {   return false;  }

        public boolean isOK()
        {   return false;  }

        public String toString()
        {   return Messages.SevMajor; }
    };
    
    private static final Severity invalid = new Severity()
    {
        public boolean hasValue()
        {   return true;  }

        public boolean isInvalid()
        {   return true;   }

        public boolean isMajor()
        {   return false;  }

        public boolean isMinor()
        {   return false;  }

        public boolean isOK()
        {   return false;  }

        public String toString()
        {   return Messages.SevInvalid; }
    };
    
    /** Don't instantiate. */
    private SeverityUtil() {}

    /** @return Severity for code 0, 1, 2, 3 as used by EPICS. */
    public static Severity forCode(int code)
    {
        switch (code)
        {
        case 0: return ok;
        case 1: return minor;
        case 2: return mayor;
        default: return invalid;
        }
    }
}
