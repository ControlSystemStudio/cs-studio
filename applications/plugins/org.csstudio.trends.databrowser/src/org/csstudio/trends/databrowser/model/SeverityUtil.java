package org.csstudio.trends.databrowser.model;

import java.util.HashMap;

import org.csstudio.archive.Severity;

/** Helper for creating some Severity values.
 *  @author Kay Kasemir
 */
public class SeverityUtil
{
    private static HashMap<String, Severity> hash = null;
    /** Don't instantiate. */
    private SeverityUtil() {}
    
    /** @return Instance of 'invalid' Severity. */
    public static Severity getOK(String text)
    {
        return new AbstractSeverity(text)
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
        };
    }
    
    /** @return Instance of 'invalid' Severity. */
    public static Severity getMinor(String text)
    {
        return new AbstractSeverity(text)
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
        };
    }

    /** @return Instance of 'invalid' Severity. */
    public static Severity getMajor(String text)
    {
        return new AbstractSeverity(text)
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
        };
    }

    /** @return Instance of 'invalid' Severity. */
    public static Severity getInvalid(String text)
    {
        return new AbstractSeverity(text)
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
        };
    }

    /** @return Instance of 'invalid' Severity. */
    public static Severity get(int code, String text)
    {
        if (hash == null)
            hash = new HashMap<String, Severity>();
        
        Severity s = hash.get(text);
        if (s != null)
            return s;
        switch (code)
        {
        case 0:   s = getOK(text);
                  break;
        case 1:   s = getMinor(text);
                  break;
        case 2:   s = getMajor(text);
                  break;
        default:  s = getInvalid(text);
        }
        hash.put(text, s);
        return s;
    }
}
