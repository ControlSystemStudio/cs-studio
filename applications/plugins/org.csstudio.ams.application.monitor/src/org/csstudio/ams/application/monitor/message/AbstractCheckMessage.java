
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.ams.application.monitor.message;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author mmoeller
 * @version 1.0
 * @since 12.04.2012
 */
public class AbstractCheckMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private SimpleDateFormat dateFormat;
    
    private String _type;
    private String _eventTime;
    private String _name;
    private String _clazz;
    private String _host;
    private String _severity;
    private String _status;
    private String _applicationId;
    private String _user;
    private String _destination;
    private boolean _isValid;
    
    public AbstractCheckMessage() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        reset();
    }
    
    public void reset() {
        _type = null;
        _eventTime = null;
        _name = null;
        _clazz = null;
        _host = null;
        _severity = null;
        _status = null;
        _applicationId = null;
        _user = null;
        _destination = null;
        _isValid = false;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (_applicationId == null) ? 0 : _applicationId.hashCode());
        result = prime * result + ( (_clazz == null) ? 0 : _clazz.hashCode());
        result = prime * result + ( (_destination == null) ? 0 : _destination.hashCode());
        result = prime * result + ( (_host == null) ? 0 : _host.hashCode());
        result = prime * result + ( (_name == null) ? 0 : _name.hashCode());
        result = prime * result + ( (_severity == null) ? 0 : _severity.hashCode());
        result = prime * result + ( (_status == null) ? 0 : _status.hashCode());
        result = prime * result + ( (_type == null) ? 0 : _type.hashCode());
        result = prime * result + ( (_user == null) ? 0 : _user.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractCheckMessage other = (AbstractCheckMessage) obj;
        if (_applicationId == null) {
            if (other._applicationId != null)
                return false;
        } else if (!_applicationId.equals(other._applicationId))
            return false;
        if (_clazz == null) {
            if (other._clazz != null)
                return false;
        } else if (!_clazz.equals(other._clazz))
            return false;
        if (_destination == null) {
            if (other._destination != null)
                return false;
        } else if (!_destination.equals(other._destination))
            return false;
        if (_host == null) {
            if (other._host != null)
                return false;
        } else if (!_host.equals(other._host))
            return false;
        if (_name == null) {
            if (other._name != null)
                return false;
        } else if (!_name.equals(other._name))
            return false;
        if (_severity == null) {
            if (other._severity != null)
                return false;
        } else if (!_severity.equals(other._severity))
            return false;
        if (_status == null) {
            if (other._status != null)
                return false;
        } else if (!_status.equals(other._status))
            return false;
        if (_type == null) {
            if (other._type != null)
                return false;
        } else if (!_type.equals(other._type))
            return false;
        if (_user == null) {
            if (other._user != null)
                return false;
        } else if (!_user.equals(other._user))
            return false;
        return true;
    }

    @Override
    public String toString() {
        
        StringBuffer buffer = new StringBuffer();
        
        buffer.append(AbstractCheckMessage.class.getSimpleName() + "{");
        buffer.append("TYPE=" + _type + ",");
        buffer.append("EVENTTIME=" + _eventTime + ",");
        buffer.append("NAME=" + _name + ",");
        buffer.append("CLASS=" + _clazz + ",");
        buffer.append("HOST=" + _host + ",");
        buffer.append("USER=" + _user + ",");
        buffer.append("SEVERITY=" + _severity + ",");
        buffer.append("STATUS=" + _status + ",");
        buffer.append("APPLICATION-ID=" + _applicationId + ",");
        buffer.append("DESTINATION=" + _destination + "}");
        
        return buffer.toString();
    }
    
    public boolean isValid() {
        return _isValid;
    }
    
    public void setValid(boolean isValid) {
        _isValid = isValid;
    }
    
    public String getTypeValue() {
        return _type;
    }

    public void setTypeValue(String type) {
        _type = type;
    }

    public long getEventTimeAsLong() {
        long result = -1L;
        if (_eventTime != null) {
            try {
                result = dateFormat.parse(_eventTime).getTime();
            } catch (ParseException e) {
                result = -1L;
            }
        }
        return result;
    }
    
    public String getEventTimeValue() {
        return _eventTime;
    }

    public void setEventTimeValue(String eventTime) {
        _eventTime = eventTime;
    }

    public String getNameValue() {
        return _name;
    }

    public void setNameValue(String name) {
        _name = name;
    }

    public String getClassValue() {
        return _clazz;
    }

    public void setClassValue(String clazz) {
        _clazz = clazz;
    }

    public String getHostValue() {
        return _host;
    }

    public void setHostValue(String host) {
        _host = host;
    }

    public String getSeverityValue() {
        return _severity;
    }

    public void setSeverityValue(String severity) {
        _severity = severity;
    }

    public String getStatusValue() {
        return _status;
    }

    public void setStatusValue(String status) {
        _status = status;
    }

    public String getApplicationIdValue() {
        return _applicationId;
    }

    public void setApplicationIdValue(String applicationId) {
        _applicationId = applicationId;
    }

    public String getUserValue() {
        return _user;
    }

    public void setUserValue(String user) {
        _user = user;
    }

    public String getDestinationValue() {
        return _destination;
    }

    public void setDestinationValue(String destination) {
        _destination = destination;
    }
}
