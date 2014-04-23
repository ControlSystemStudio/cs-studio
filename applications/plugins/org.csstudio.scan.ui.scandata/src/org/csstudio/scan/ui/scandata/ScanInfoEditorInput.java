/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scandata;

import org.csstudio.scan.server.ScanInfo;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/** Editor input wrapper for {@link ScanInfo}
 *
 *  <p>Can 'persist' itself to re-create the scan data
 *  when application is restarted.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanInfoEditorInput implements IEditorInput, IPersistableElement
{
	/** Memento IDs */
    final public static String TAG_NAME = "name",
			                   TAG_ID = "id";

	final private long scan_id;
	final private String name;

	/** Initialize
	 *  @param scan {@link ScanInfo}
	 */
	public ScanInfoEditorInput(final ScanInfo scan)
    {
		this(scan.getId(), scan.getName());
    }

	/** Initialize
	 *  @param scan_id Scan ID
	 *  @param name Scan name
	 */
	public ScanInfoEditorInput(final long scan_id, final String name)
    {
		this.scan_id = scan_id;
		this.name = name;
    }

	// IEditorInput

	/** @return Scan ID */
	public long getScanID()
    {
    	return scan_id;
    }

	/** {@inheritDoc} */
	@SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(final Class adapter)
    {
	    return null;
    }

	/** {@inheritDoc} */
	@Override
    public boolean exists()
    {
	    return true;
    }

	/** {@inheritDoc} */
	@Override
    public ImageDescriptor getImageDescriptor()
    {
	    return null;
    }

	/** {@inheritDoc} */
	@Override
    public String getName()
    {
	    return NLS.bind(Messages.ScanEditorTTFmt, name, scan_id);
    }

	/** {@inheritDoc} */
	@Override
    public IPersistableElement getPersistable()
    {
	    return this;
    }

	/** {@inheritDoc} */
	@Override
    public String getToolTipText()
    {
	    return getName();
    }

	// IPersistableElement

	/** {@inheritDoc} */
	@Override
    public String getFactoryId()
    {
	    return ScanInfoEditorInputFactory.ID;
    }

	/** {@inheritDoc} */
	@Override
	public void saveState(final IMemento memento)
	{
		// Saving Long ID as String because IMemento only offers Integer
		memento.putString(TAG_ID, Long.toString(scan_id));
		memento.putString(TAG_NAME, name);
	}
}
