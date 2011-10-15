package org.csstudio.archive.reader.kblog;

import org.csstudio.data.values.ISeverity;

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
}
