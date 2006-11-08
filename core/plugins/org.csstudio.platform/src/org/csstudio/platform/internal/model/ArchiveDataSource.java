package org.csstudio.platform.internal.model;

import org.csstudio.platform.model.AbstractControlSystemItem;
import org.csstudio.platform.model.IArchiveDataSource;

/**
 * Implementation of the {@link IArchiveDataSource} interface.
 * 
 * This is internal API and should not be instantiated directly by clients.
 * 
 * @author Kay Kasemir
 */
public class ArchiveDataSource extends AbstractControlSystemItem implements
		IArchiveDataSource {
	/** The URL of the archive data server. */
	private String _url;

	/** The key of the archive under the url. */
	private int _key;

	/**
	 * Constructor.
	 * 
	 * @param url
	 *            Data server URL.
	 * @param key
	 *            Archive key.
	 * @param name
	 *            Archive name, derived from key.
	 */
	public ArchiveDataSource(final String url, final int key, final String name) {
		super(name);
		assert url != null;
		assert name != null;
		_url = url;
		_key = key;
	}

	/**
	 * {@inheritDoc}
	 */
	public final String getUrl() {
		return _url;
	}

	/**
	 * {@inheritDoc}
	 */
	public final int getKey() {
		return _key;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return "Archive '" + _url + "' (" + _key + ", '" + getName() + "')"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	/**
	 * {@inheritDoc}
	 */
	public final String getTypeId() {
		return TYPE_ID;
	}
}
