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
