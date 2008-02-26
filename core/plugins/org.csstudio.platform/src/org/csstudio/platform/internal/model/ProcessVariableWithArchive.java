/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
