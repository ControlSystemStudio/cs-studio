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
package org.csstudio.sds.model.logic;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;

/**
 * A description of rule parameters.
 * 
 * FIXME: DIe Klasse sollte anstelle von Strings mit
 * {@link IProcessVariableAddress} arbeiten
 * 
 * @author Alexander Will & Sven Wende
 * @version $Revision$
 * 
 */
public final class ParameterDescriptor {
	/**
	 * The name of the channel.
	 */
	private String _channel;

	/**
	 * The type of the expected data. This will probably be one of standard Java
	 * types and should be configured by the user.
	 */
	private Class _type;
	
	/**
	 * The default value.
	 */
	private Object _value;

	/**
	 * Constructs a default parameter with an empty channel name and type
	 * Double.class.
	 * 
	 */
	public ParameterDescriptor() {
		this("", Double.class); //$NON-NLS-1$
	}
	
	/**
	 * Standard constructor.
	 * 
	 * @param channel
	 *            The name of the channel, respectively process variable.
	 * @param type
	 *            The type of the expected data.
	 * @param value
	 * 			  The default value.
	 */
	public ParameterDescriptor(final String channel, final Class type, final Object value) {
		assert (channel!=null || value!=null);
		assert type != null;
		_channel = channel;
		_type = type;
		_value = value;
	}

	/**
	 * Standard constructor.
	 * 
	 * @param channel
	 *            The name of the channel, respectively process variable.
	 * @param type
	 *            The type of the expected data.
	 */
	public ParameterDescriptor(final String channel, final Class type) {
		this(channel, type , null);
	}

	public ParameterDescriptor(String channel) {
		this(channel, Object.class);
	}

	/**
	 * Return the type of the expected data.
	 * 
	 * @return Type of the expected data.
	 */
	public Class getType() {
		return _type;
	}

	/**
	 * Return the name of the channel, respectively process variable.
	 * 
	 * @return The name of the channel, respectively process variable.
	 */
	public String getChannel() {
		return _channel;
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
	public Object getValue() {
		return _value;
	}

	/**
	 * Set the default value.
	 * 
	 * @param value
	 *            The default value.
	 */
	public void setValue(final Object value) {
		_value = value;
	}

	/**
	 * Set the type of the expected data.
	 * 
	 * @param type
	 *            The type of the expected data.
	 */
	public void setType(final Class type) {
		_type = type;
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
		return new ParameterDescriptor(new String(_channel), _type, _value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_channel == null) ? 0 : _channel.hashCode());
		result = prime * result + ((_type == null) ? 0 : _type.hashCode());
		result = prime * result + ((_value == null) ? 0 : _value.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ParameterDescriptor other = (ParameterDescriptor) obj;
		if (_channel == null) {
			if (other._channel != null)
				return false;
		} else if (!_channel.equals(other._channel))
			return false;
		if (_type == null) {
			if (other._type != null)
				return false;
		} else if (!_type.equals(other._type))
			return false;
		if (_value == null) {
			if (other._value != null)
				return false;
		} else if (!_value.equals(other._value))
			return false;
		return true;
	}


}
