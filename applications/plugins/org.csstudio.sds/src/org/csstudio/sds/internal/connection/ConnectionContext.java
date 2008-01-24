package org.csstudio.sds.internal.connection;

import java.util.HashMap;
import java.util.Map;

/**
 * A connection context is a simple state object, which contains some global
 * information that are necessary in the course of connecting channels.
 * 
 * @author swende
 * 
 */
public final class ConnectionContext {
	/**
	 * Contains aliases and their current bindings.
	 */
	private Map<String, String> _aliases;

	/**
	 * Constructor.
	 */
	public ConnectionContext() {
		this(new HashMap<String, String>());
	}

	/**
	 * Constructor.
	 * 
	 * @param aliases
	 *            aliases and their current bindings
	 * @param rate
	 *            the global refresh rate
	 */
	public ConnectionContext(final Map<String, String> aliases) {
		_aliases = aliases;
	}

	/**
	 * Gets all aliases and their current bindings.
	 * 
	 * @return all aliases and their current bindings
	 */
	public Map<String, String> getAliases() {
		return _aliases;
	}
}
