package org.csstudio.sds.internal.connection;


import java.util.Map;

import org.csstudio.sds.util.ChannelReferenceValidationException;
import org.csstudio.sds.util.ChannelReferenceValidationUtil;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;

/**
 * A channel reference is an abstract pointer to a control system channel. The
 * raw channel name can contain variables, which will be replaced before a
 * control system connection
 * 
 * during the connection.
 * 
 * @author swende
 * 
 */
public final class ChannelReference implements IAdaptable {
	/**
	 * String sequence, which escapes variable names within the channel name.
	 */
	public static final String VARIABLE_ESCAPE_CHAR = "$";

	/**
	 * The raw channel name. This name might contain variables that are escaped
	 * by {@link #VARIABLE_ESCAPE_CHAR}.
	 */
	private String _rawChannelName;

	/**
	 * The JAVA class type, which is expected for value that are received via
	 * this channel.
	 */
	private Class _type;

	private Object _defaultValue;

	/**
	 * Constructor.
	 * 
	 * @param rawName
	 *            the raw name for the channel, as entered by the user (might
	 *            contain variables)
	 * @param type
	 *            the JAVA class type, which is expected for value that are
	 *            received via this channel
	 */
	public ChannelReference(final String rawName, final Class type, final Object defaultValue) {
		_rawChannelName = rawName;
		_type = type;
		_defaultValue = defaultValue;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param rawName
	 *            the raw name for the channel, as entered by the user (might
	 *            contain variables)
	 * @param type
	 *            the JAVA class type, which is expected for value that are
	 *            received via this channel
	 */
	public ChannelReference(final String rawName, final Class type) {
		this(rawName, type, null);
	}

	/**
	 * The raw channel name. This name still can contain unresolved aliases. Use
	 * {@link #getCanonicalName(HashMap)} to receive a canonical name in which
	 * all aliases are resolved.
	 * 
	 * @return the raw channel name
	 */
	public String getRawChannelName() {
		return _rawChannelName;
	}

	/**
	 * Returns the full qualified channel name. In the full qualified channel
	 * name all variables have been substituted by their real values.
	 * 
	 * @param aliases
	 *            a hashmap which contains aliases
	 * 
	 * @throws ChannelReferenceValidationException
	 *             this exception is thrown, if the specified input or the
	 *             aliases cannot be processed
	 * 
	 * @return a canonical channel name which contains no aliases, that can be
	 *         used to create a connection to the connection layer
	 */
	public String getCanonicalName(final Map<String, String> aliases)
			throws ChannelReferenceValidationException {
		return ChannelReferenceValidationUtil.createCanonicalName(
				_rawChannelName, aliases);
	}
	
	public Object getValue() {
		return _defaultValue;
	}

	/**
	 * Gets the JAVA class type, which is expected for value that are received
	 * via this channel.
	 * 
	 * @return the JAVA class type, which is expected for value that are
	 *         received via this channel
	 */
	public Class getType() {
		return _type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_rawChannelName == null) ? 0 : _rawChannelName.hashCode());
		result = prime * result + ((_type == null) ? 0 : _type.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ChannelReference other = (ChannelReference) obj;
		if (_rawChannelName == null) {
			if (other._rawChannelName != null) {
				return false;
			}
		} else if (!_rawChannelName.equals(other._rawChannelName)) {
			return false;
		}
		if (_type == null) {
			if (other._type != null) {
				return false;
			}
		} else if (!_type.equals(other._type)) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getRawChannelName();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(final Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
	
	

}
