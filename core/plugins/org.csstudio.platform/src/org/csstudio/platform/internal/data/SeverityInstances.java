/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.platform.internal.data;

import org.csstudio.platform.data.ISeverity;

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

        @Override
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

        @Override
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

        @Override
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

        @Override
        public String toString()
        {   return Messages.SevInvalid; }
    };
}
