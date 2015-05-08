/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.sds.internal.rules;

import java.util.Map;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.sds.util.ChannelReferenceValidationException;
import org.csstudio.sds.util.ChannelReferenceValidationUtil;

/**
 * A description of rule parameters.
 *
 * @author Alexander Will & Sven Wende
 * @version $Revision: 1.3 $
 *
 */
public final class ParameterDescriptor {
    /**
     * The name of the channel.
     */
    private String _channel;

    /**
     * The default value.
     */
    private String _value = "";

    /**
     * Constructs a default parameter with an empty channel name and type
     * Double.class.
     *
     */
    public ParameterDescriptor() {
        this("", ""); //$NON-NLS-1$
    }


    /**
     * Standard constructor.
     *
     * @param channel
     *            The name of the channel, respectively process variable.
     * @param type
     *            The type of the expected data.
     * @param value
     *               The default value.
     */
    public ParameterDescriptor(final String channel, final String value) {
        assert channel != null : "channel != null";
        assert value != null : "value != null";

        _channel = channel;
        _value = value;
    }

    public ParameterDescriptor(String channel) {
        this(channel, "");
    }

    /**
     * Return the name of the channel, respectively process variable.
     *
     * @return The name of the channel, respectively process variable.
     */
    public String getChannel() {
        return _channel;
    }

    private boolean isChannelSpecified() {
        return _channel!=null && _channel.trim().length()>0;
    }

    public IProcessVariableAddress getPv(final Map<String, String> aliases) {
        IProcessVariableAddress result = null;

        if (isChannelSpecified()) {
            try {
                String realName = ChannelReferenceValidationUtil.createCanonicalName(getChannel(), aliases);
                result = ProcessVariableAdressFactory.getInstance().createProcessVariableAdress(realName);
            } catch (ChannelReferenceValidationException e) {
                result = null;
            }
        }

        return result;
    }

    /**
     * Set the name of the channel, respectively process variable.
     *
     * @param channel
     *            The name of the channel, respectively process variable.
     */
    public void setChannel(final String channel) {
        _channel = channel;
    }

    /**
     * Return the default value.
     *
     * @return The default value.
     */
    public String getValue() {
        return _value;
    }

    /**
     * Set the default value.
     *
     * @param value
     *            The default value.
     */
    public void setValue(final String value) {
        assert value != null : "value != null";

        _value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return _channel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ParameterDescriptor clone() {
        return new ParameterDescriptor(new String(_channel), _value);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((_channel == null) ? 0 : _channel.hashCode());
        result = prime * result + ((_value == null) ? 0 : _value.hashCode());
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
        ParameterDescriptor other = (ParameterDescriptor) obj;
        if (_channel == null) {
            if (other._channel != null)
                return false;
        } else if (!_channel.equals(other._channel))
            return false;
        if (_value == null) {
            if (other._value != null)
                return false;
        } else if (!_value.equals(other._value))
            return false;
        return true;
    }

}
