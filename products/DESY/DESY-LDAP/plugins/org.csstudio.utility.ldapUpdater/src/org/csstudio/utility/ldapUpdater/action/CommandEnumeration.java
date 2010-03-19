/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.ldapUpdater.action;

import org.csstudio.platform.management.CommandParameterEnumValue;
import org.csstudio.platform.management.IDynamicParameterValues;

/**
 * TODO (bknerr) :
 * 
 * @author bknerr 19.03.2010
 */
public class CommandEnumeration implements IDynamicParameterValues {
    
    public enum IocModificationCommand {
        DELETE("Delete IOC from LDAP (incl. all Records)."),
        TIDY_UP("Tidy up (removes all Records from LDAP not contained in current IOC file).");
        
        private final String _description;
        
        /**
         * Constructor.
         */
        private IocModificationCommand(final String desc) {
            _description = desc;
        }
        
        /**
         * @return the _description
         */
        public String getDescription() {
            return _description;
        }
    }
    
    
    /**
     * (@inheritDoc)
     */
    @Override
    public CommandParameterEnumValue[] getEnumerationValues() {
        final CommandParameterEnumValue[] params =
            new CommandParameterEnumValue[IocModificationCommand.values().length];
        int i = 0;
        for (final IocModificationCommand command : IocModificationCommand.values()) {
            params[i++] = new CommandParameterEnumValue(command.name(), command.getDescription());
        }
        
        return params;
    }
    
}
