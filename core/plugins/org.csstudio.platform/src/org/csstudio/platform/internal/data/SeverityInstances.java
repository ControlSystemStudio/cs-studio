package org.csstudio.platform.internal.data;

import org.csstudio.platform.data.ISeverity;

/** Helper for creating some Severity values.
 *  @author Kay Kasemir
 */
public class SeverityInstances
{
    /** Don't instantiate. */
    private SeverityInstances() {}

    /** Implementation of an 'OK' ISeverity. */
    public static final ISeverity ok = new ISeverity()
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
    
    /** Implementation of a 'minor' ISeverity. */
    public static final ISeverity minor = new ISeverity()
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
    
    /** Implementation of a 'major' ISeverity. */
   public static final ISeverity major = new ISeverity()
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
    
    /** Implementation of an 'invalid' ISeverity. */
    public static final ISeverity invalid = new ISeverity()
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
}
