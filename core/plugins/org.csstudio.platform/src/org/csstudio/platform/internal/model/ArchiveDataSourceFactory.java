package org.csstudio.platform.internal.model;

import org.csstudio.platform.model.AbstractControlSystemItemFactory;
import org.csstudio.platform.model.IArchiveDataSource;

/**
 * Implementation of {@link AbstractControlSystemItemFactory} for archive data
 * sources.
 * 
 * @author Sven Wende
 * 
 */
public final class ArchiveDataSourceFactory extends
		AbstractControlSystemItemFactory<IArchiveDataSource> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String createStringRepresentationFromItem(final IArchiveDataSource item) {
		return item.getName() + ":" + item.getUrl() + ":" + item.getKey();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IArchiveDataSource createItemFromStringRepresentation(
			final String string) {
		IArchiveDataSource result = null;

		String[] parts = string.split(":");

		try {
			if (parts.length == 3) {
				String name = parts[0];
				String url = parts[1];
				int key = Integer.parseInt(parts[2]);
				result = new ArchiveDataSource(url, key, name);
			}
		} catch (Exception e) {
			result = null;
		}

		return result;
	}

}
