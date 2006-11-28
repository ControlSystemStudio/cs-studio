package org.csstudio.platform.internal.model;

import org.csstudio.platform.model.AbstractControlSystemItem;
import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.platform.model.IProcessVariableWithArchive;

/**
 * Minimal <code>IProcessVariableNameWithArchiveDataSource</code>
 * implementation.
 * <p>
 * The drag-and-drop transfer uses it internally.<br>
 * Applications which need to provide IArchiveDataSource can use this, but can
 * also implement the interface themselves.
 * 
 * @author Kay Kasemir
 */
public class ProcessVariableWithArchive extends AbstractControlSystemItem
		implements IProcessVariableWithArchive {

	/**
	 * The associated archive data source.
	 */
	private IArchiveDataSource _archiveDataSource;

	/**
	 * Constructor.
	 * 
	 * @param pvName
	 *            the name of the process variable
	 * @param url
	 *            an url
	 * @param key
	 *            a key
	 * @param archiveName
	 *            the archive name
	 */
	public ProcessVariableWithArchive(final String pvName, final String url, final int key,
			final String archiveName) {
		super(pvName);
		_archiveDataSource = CentralItemFactory.createArchiveDataSource(url,
				key, archiveName);
	}

	/**
	 * {@inheritDoc}
	 */
	public final IArchiveDataSource getArchiveDataSource() {
		return _archiveDataSource;
	}

	/**
	 * {@inheritDoc}
	 */
	public final String getTypeId() {
		return TYPE_ID;
	}
}
