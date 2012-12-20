package org.csstudio.archive.reader.kblog;

import org.csstudio.data.values.ISeverity;

/**
 * Severity instances for the data specific to kblog.
 * 
 * @author Takashi Nakamoto
 */
public class KBLogSeverityInstances {
    /** Implementation of an 'CONNECTED' ISeverity. */
    public static final ISeverity connected = new ISeverity()
    {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean hasValue()
        {   return false;  }

        @Override
        public boolean isInvalid()
        {   return false;   }

        @Override
        public boolean isMajor()
        {   return false;  }

        @Override
        public boolean isMinor()
        {   return false;  }

        @Override
        public boolean isOK()
        {   return true;  }

        @Override
        public String toString()
        {   return KBLogMessages.SeverityConnected; }
    };
    
    public static final ISeverity disconnected = new ISeverity()
    {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean hasValue()
        {   return false;  }

        @Override
        public boolean isInvalid()
        {   return true;   }

        @Override
        public boolean isMajor()
        {   return false;  }

        @Override
        public boolean isMinor()
        {   return false;  }

        @Override
        public boolean isOK()
        {   return false;  }

        @Override
        public String toString()
        {   return KBLogMessages.SeverityDisconnected; }
    };
    
    public static final ISeverity nan = new ISeverity()
    {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean hasValue()
        {   return false;  }

        @Override
        public boolean isInvalid()
        {   return true;   }

        @Override
        public boolean isMajor()
        {   return false;  }

        @Override
        public boolean isMinor()
        {   return false;  }

        @Override
        public boolean isOK()
        {   return false;  }

        @Override
        public String toString()
        {   return KBLogMessages.SeverityNaN; }
    };
    
    public static final ISeverity normal = new ISeverity()
    {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean hasValue()
        {   return true;  }

        @Override
        public boolean isInvalid()
        {   return false;   }

        @Override
        public boolean isMajor()
        {   return false;  }

        @Override
        public boolean isMinor()
        {   return false;  }

        @Override
        public boolean isOK()
        {   return true;  }

        @Override
        public String toString()
        {   return KBLogMessages.SeverityNormal; }
    };
}
