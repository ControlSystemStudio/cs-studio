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
package org.csstudio.alarm.table.dataModel;

import static org.csstudio.alarm.service.declaration.AlarmMessageKey.NAME;
import static org.csstudio.alarm.service.declaration.AlarmMessageKey.SEVERITY;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import org.csstudio.alarm.table.preferences.ISeverityMapping;
import org.eclipse.core.runtime.PlatformObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Message received from the JMS server. The properties of messages are not
 * restricted but the table will only display properties for which a column with
 * the same name is defined.
 * 
 * The BasicMessage utilizes an ISeverityMapping, which is retrieved by the SeverityRegistry.
 *
 * @author jhatje
 *
 */
public class BasicMessage extends PlatformObject {
    
    /**
     * The properties of the message.
     */
    private final Map<String, String> _messageProperties = new HashMap<String, String>();
    
    private static final Logger LOG = LoggerFactory.getLogger(BasicMessage.class);
    
    /**
     * Default constructor
     */
    public BasicMessage() {
        // Nothing to do
    }
    
    /**
     * Constructor with initial message properties of the table columns.
     */
    public BasicMessage(@Nonnull final String[] propNames) {
        for (final String propName : propNames) {
            _messageProperties.put(propName.split(",")[0], ""); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
    
    public BasicMessage(@Nonnull final Map<String, String> messageProperties) {
        _messageProperties.putAll(messageProperties);
    }
    
    /**
     * Set value of a message property
     *
     * @param property
     * @param value
     */
    public void setProperty(final String property, final String value) {
        _messageProperties.put(property, value);
    }
    
    /**
     * Returns value of the requested property
     *
     * @param property
     * @return
     */
    @CheckForNull
    public String getProperty(@Nonnull final String property) {
        
        // if the table asks for the severity we return the severity value from the preferences
        final String severityProp = _messageProperties.get(SEVERITY.getDefiningName());
        if(property.equals(SEVERITY.getDefiningName())) { //$NON-NLS-1$
            if(severityProp != null) { //$NON-NLS-1$
                try {
                    final String severityValue = getSeverityMapping()
                            .findSeverityValue(severityProp);
                    return severityValue;
                } catch (final Exception e) {
                    LOG.error("JmsLogsPlugin Service not available, ",e);
                }
            }
        } else if(property.equals("SEVERITY_KEY")) { //$NON-NLS-1$
            // to get the severity key (the 'real' severity get from the map
            // message)
            // we have to ask for 'SEVERITY_KEY'
            if(severityProp != null) { //$NON-NLS-1$
                return severityProp; //$NON-NLS-1$
            }
        }
        
        // all other properties
        return _messageProperties.get(property);
    }
    
    @Nonnull
    private ISeverityMapping getSeverityMapping() {
        return SeverityRegistry.getSeverityMapping();
    }
    
    public int getSeverityNumber() {
        return getSeverityMapping().getSeverityNumber(_messageProperties.get(SEVERITY
                .getDefiningName()));
        // return SeverityMapping.getSeverityNumber(_messageProperties
        // .get(SEVERITY.getDefiningName()));
    }

    @CheckForNull
    public String getName() {
        return this.getProperty(NAME.getDefiningName()); //$NON-NLS-1$
    }
    
    public Map<String, String> getHashMap() {
        return _messageProperties;
    }
    
    /**
     * @return deep copy of the JMSMessage.
     */
    @CheckReturnValue
    protected Map<String, String> copyProperties() {
        return new HashMap<String, String>(_messageProperties);
    }
    
}
