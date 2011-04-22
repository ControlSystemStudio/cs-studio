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
package org.csstudio.platform.ui.internal.dnd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IControlSystemItem;
import org.csstudio.platform.util.ControlSystemItemPath;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * Drag-and-Drop transfer type for {@link IControlSystemItem} and collections
 * and arrays of {@link IControlSystemItem}.
 * <p>
 * This transfer type expects the data to transfer to implement the
 * <code>IControlSystemItem</code> interface, and the resulting data is
 * provided as an array of <code>IControlSystemItem</code>.
 * <p>
 * Most of this implementation is from the javadoc for ByteArrayTransfer.
 * 
 * The handler contains a workarround for Non-Windows operating systems. The
 * latest selection is made available directly for use in the
 * DropTargetListener. dropEnter method. The DropTargetEvent passed to
 * dropAccept does not contain the drop data. The selection may be used for
 * validation purposes so that the drop can be aborted if appropriate.
 * 
 * See also {@link LocalSelectionTransfer} which uses the same prinicple.
 * 
 * This class is not intended to be subclassed.
 * 
 * @author Sven Wende, Stefan Hofer
 * 
 * @deprecated
 */
public final class ControlSystemItemTransfer extends ByteArrayTransfer {
	/**
	 * The time period in which a local selection expires. Needed to handle
	 * cases, where data is dragged between different JVMs.
	 */
	private static final int SELECTION_EXPIRATION_PERIOD = 10000;

	/**
	 * The type name.
	 */
	private static final String TYPE_NAME = "controlsystemitem_name"; //$NON-NLS-1$

	/**
	 * The type ID.
	 */
	private static final int TYPE_ID = registerType(TYPE_NAME);

	/**
	 * The singleton instance.
	 */
	private static ControlSystemItemTransfer _instance;

	/**
	 * The items that were selected during the latest DnD operation.
	 */
	private List<IControlSystemItem> _selectedItems;

	/**
	 * The event time of the latest selection.
	 */
	private long _selectionSetTime;

	/**
	 * Hidden contructor.
	 * 
	 * @see #getInstance()
	 */
	private ControlSystemItemTransfer() {
		_selectionSetTime = 0;
	}

	/**
	 * @return The singleton instance of ControlSystemItemTransfer.
	 */
	public static ControlSystemItemTransfer getInstance() {
		if (_instance == null) {
			_instance = new ControlSystemItemTransfer();
		}
		return _instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int[] getTypeIds() {
		return new int[] { TYPE_ID };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void javaToNative(final Object object,
			final TransferData transferData) {
		if (!(object instanceof IControlSystemItem[]
				|| object instanceof Collection || object instanceof IControlSystemItem)) {
			return;
		}

		if (isSupportedType(transferData)) {
			List<ControlSystemItemPath> paths = new ArrayList<ControlSystemItemPath>();

			if (object instanceof IControlSystemItem) {
				paths
						.add(CentralItemFactory
								.createControlSystemItemPath((IControlSystemItem) object));
			}
			if (object instanceof IControlSystemItem[]) {
				for (IControlSystemItem ds : (IControlSystemItem[]) object) {
					paths.add(CentralItemFactory
							.createControlSystemItemPath(ds));
				}
			} else if (object instanceof Collection) {
				for (Object o : (Collection) object) {
					if (o instanceof IControlSystemItem) {
						IControlSystemItem item = (IControlSystemItem) o;
						ControlSystemItemPath x = CentralItemFactory
								.createControlSystemItemPath(item);
						paths.add(x);
					}
				}
			}

			try {
				ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
				ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
				objectOut.writeObject(paths.toArray());

				byte[] buffer = byteOut.toByteArray();
				byteOut.close();
				objectOut.close();

				super.javaToNative(buffer, transferData);
			} catch (IOException e) {
				CentralLogger.getInstance().error(this, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object nativeToJava(final TransferData transferData) {
		assert transferData != null;

		IControlSystemItem[] result = null;

		if (isSupportedType(transferData)) {

			byte[] buffer = (byte[]) super.nativeToJava(transferData);

			List<IControlSystemItem> received = new ArrayList<IControlSystemItem>();
			ByteArrayInputStream byteIn = new ByteArrayInputStream(buffer);

			try {
				ObjectInputStream objectIn = new ObjectInputStream(byteIn);

				Object[] paths = (Object[]) objectIn.readObject();

				for (Object path : paths) {
					assert path instanceof ControlSystemItemPath : "path instanceof ControlSystemItemPath"; //$NON-NLS-1$
					received
							.add(CentralItemFactory
									.createControlSystemItem((ControlSystemItemPath) path));
				}

				byteIn.close();
				objectIn.close();
			} catch (Exception e) {
				CentralLogger.getInstance().error(this, e);
			}
			result = received.toArray(new IControlSystemItem[received.size()]);
		}
		return result;
	}

	/**
	 * @return Gets the transfer data for local use.
	 */
	public List<IControlSystemItem> getSelectedItems() {
		long dist = new Date().getTime() - _selectionSetTime;

		if (dist > SELECTION_EXPIRATION_PERIOD) {
			return null;
		}
		return _selectedItems;
	}

	/**
	 * Sets the transfer data for local use.
	 * 
	 * @param selectedItems
	 *            the transfer data
	 */
	public void setSelectedItems(final List<IControlSystemItem> selectedItems) {
		_selectedItems = selectedItems;
		_selectionSetTime = new Date().getTime();
	}
}
