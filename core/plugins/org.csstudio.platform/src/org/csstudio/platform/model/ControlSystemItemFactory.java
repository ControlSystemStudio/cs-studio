package org.csstudio.platform.model;

import org.csstudio.platform.internal.model.ArchiveDataSource;
import org.csstudio.platform.internal.model.ProcessVariable;

/**
 * A factory for control system items. Central control system items (e.g.
 * IProcessVariables), can be obtained by static creation methods.
 * 
 * @author swende
 * 
 */
public final class ControlSystemItemFactory {

	/**
	 * Hidden constructor.
	 * 
	 */
	private ControlSystemItemFactory() {

	}

	/**
	 * Creates a process variable.
	 * 
	 * @param name
	 *            the name of the process variable
	 * @return a process variable
	 */
	public static IProcessVariable createProcessVariable(final String name) {
		assert name != null;
		return new ProcessVariable(name);
	}

	/**
	 * Creates a archive datasource.
	 * 
	 * @param url
	 *            an url
	 * @param key
	 *            a key
	 * @param name
	 *            the name
	 * @return an archive datasource
	 */
	public static IArchiveDataSource createArchiveDataSource(final String url,
			final int key, final String name) {
		assert url != null;
		assert name != null;
		return new ArchiveDataSource(url, key, name);
	}
}
