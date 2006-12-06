package org.csstudio.trends.databrowser.model;

import org.csstudio.value.Severity;

/** Helper for creating some Severity values.
 *  @author Kay Kasemir
 */
public class SeverityFactory
{
    private final static Severity invalid = new Severity()
    {
        public boolean hasValue()
        {   return false;  }

        public boolean isInvalid()
        {   return true;   }

        public boolean isMajor()
        {   return false;  }

        public boolean isMinor()
        {   return false;  }

        public boolean isOK()
        {   return false;  }
        
        public String toString()
        {   return Messages.Sevr_INVALID; }
    };
    
    /** Don't instantiate. */
    private SeverityFactory() {}
    
    /** @return Instance of 'invalid' Severity. */
    public static Severity getInvalid()
    {   return invalid;    }
}
