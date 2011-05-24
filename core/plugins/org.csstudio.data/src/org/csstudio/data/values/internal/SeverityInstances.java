/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.data.values.internal;

import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.Messages;

/** Helper for creating some Severity values.
 *  @author Kay Kasemir
 */
public class SeverityInstances
{
    /** Don't instantiate. */
    private SeverityInstances() { /* NOP */ }

    /** Implementation of an 'OK' ISeverity. */
    public static final ISeverity ok = new ISeverity()
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
        {   return Messages.SevOK; }
    };

    /** Implementation of a 'minor' ISeverity. */
    public static final ISeverity minor = new ISeverity()
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
        {   return true;  }

        @Override
        public boolean isOK()
        {   return false;  }

        @Override
        public String toString()
        {   return Messages.SevMinor; }
    };

    /** Implementation of a 'major' ISeverity. */
    public static final ISeverity major = new ISeverity()
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
        {   return true;  }

        @Override
        public boolean isMinor()
        {   return false;  }

        @Override
        public boolean isOK()
        {   return false;  }

        @Override
        public String toString()
        {   return Messages.SevMajor; }
    };

    /** Implementation of an 'invalid' ISeverity. */
    public static final ISeverity invalid = new ISeverity()
    {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean hasValue()
        {   return true;  }

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
        {   return Messages.SevInvalid; }
    };
}
