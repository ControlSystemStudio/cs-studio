package org.csstudio.platform.ui.dnd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.ControlSystemItemFactory;
import org.csstudio.platform.model.IArchiveDataSource;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * Drag-and-Drop transfer type for <code>IArchiveDataSource</code>.
 * <p>
 * This transfer type expects the data to transfer to implement the
 * <code>IArchiveDataSource</code> interface, and the resulting data is
 * provided as an array of <code>IArchiveDataSource</code>.
 * <p>
 * Most of this implementation is from the javadoc for ByteArrayTransfer.
 * 
 * @author Kay Kasemir, swende
 */
public final class ArchiveDataSourceTransfer extends ByteArrayTransfer {
	/**
	 * The type name of this Transfer.
	 */
	private static final String TYPE_NAME = "archive_data_source"; //$NON-NLS-1$

	/**
	 * The type identification of this Transfer.
	 */
	private static final int TYPE_ID = registerType(TYPE_NAME);

	/**
	 * The singleton instance.
	 */
	private static ArchiveDataSourceTransfer _instance;

	/**
	 * Hidden constructor.
	 * 
	 * @see #getInstance()
	 */
	private ArchiveDataSourceTransfer() {
	}

	/** @return The singleton instance of the ArchiveDataSourceTransfer. */
	public static ArchiveDataSourceTransfer getInstance() {
		if (_instance == null) {
			_instance = new ArchiveDataSourceTransfer();
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
	public void javaToNative(final Object object, final TransferData transferData) {
		if (object == null
				|| !(object instanceof IArchiveDataSource[]
						|| object instanceof Collection || object instanceof IArchiveDataSource)) {
			return;
		}

		if (isSupportedType(transferData)) {
			List<IArchiveDataSource> archivesList = new ArrayList<IArchiveDataSource>();

			if (object instanceof IArchiveDataSource) {
				archivesList.add((IArchiveDataSource) object);
			}
			if (object instanceof IArchiveDataSource[]) {
				for (IArchiveDataSource ds : (IArchiveDataSource[]) object) {
					archivesList.add(ds);
				}
			} else if (object instanceof Collection) {
				for (Object o : (Collection) object) {
					if (o instanceof IArchiveDataSource) {
						archivesList.add((IArchiveDataSource) o);
					}
				}
			}

			try {
				// write data to a byte array and then ask super to convert to
				// pMedium
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				DataOutputStream writeOut = new DataOutputStream(out);
				for (IArchiveDataSource archiveDataSource : archivesList) {
					byte[] buffer = archiveDataSource.getUrl().getBytes();
					writeOut.writeInt(buffer.length);
					writeOut.write(buffer);
					writeOut.writeInt(archiveDataSource.getKey());
					buffer = archiveDataSource.getName().getBytes();
					writeOut.writeInt(buffer.length);
					writeOut.write(buffer);
				}
				byte[] buffer = out.toByteArray();
				writeOut.close();

				super.javaToNative(buffer, transferData);
			} catch (IOException e) {
				CentralLogger.getInstance().equals(e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object nativeToJava(final TransferData transferData) {
		assert transferData != null;

		IArchiveDataSource[] result = null;

		if (isSupportedType(transferData)) {
			byte[] buffer = (byte[]) super.nativeToJava(transferData);

			List<IArchiveDataSource> received = new ArrayList<IArchiveDataSource>();

			try {
				ByteArrayInputStream in = new ByteArrayInputStream(buffer);
				DataInputStream readIn = new DataInputStream(in);
				// URL length, key, name length = 12?
				while (readIn.available() > 12) {
					int size = readIn.readInt();
					byte[] bytes = new byte[size];
					readIn.read(bytes);
					String url = new String(bytes);

					int key = readIn.readInt();

					size = readIn.readInt();
					bytes = new byte[size];
					readIn.read(bytes);
					String name = new String(bytes);

					received.add(ControlSystemItemFactory.createArchiveDataSource(url, key, name));
				}
				readIn.close();
			} catch (IOException e) {
				CentralLogger.getInstance().equals(e);
			}

			result = received.toArray(new IArchiveDataSource[received.size()]);
		}

		return result;
	}
}
