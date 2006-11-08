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
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.model.ControlSystemItemFactory;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * Drag-and-Drop transfer type for <code>IProcessVariableName</code>.
 * <p>
 * This transfer type expects the data to transfer to implement the
 * <code>IProcessVariableName</code> interface, and the resulting data is
 * provided as an array of <code>ProcessVariableName</code>.
 * <p>
 * Most of this implementation is from the javadoc for ByteArrayTransfer.
 * 
 * @author Kay Kasemir
 */
public final class ProcessVariableTransfer extends ByteArrayTransfer {
	/**
	 * The type name.
	 */
	private static final String TYPE_NAME = "pv_name"; //$NON-NLS-1$

	/**
	 * The type ID.
	 */
	private static final int TYPE_ID = registerType(TYPE_NAME);

	/**
	 * The singleton instance.
	 */
	private static ProcessVariableTransfer _instance;

	/**
	 * Hidden contructor.
	 * 
	 * @see #getInstance()
	 */
	private ProcessVariableTransfer() {
	}

	/** @return The singleton instance of the ProcessVariableNameTransfer. */
	public static ProcessVariableTransfer getInstance() {
		if (_instance == null) {
			_instance = new ProcessVariableTransfer();
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
		if (object == null
				|| !(object instanceof IProcessVariable[]
						|| object instanceof Collection || object instanceof IProcessVariable)) {
			return;
		}

		if (isSupportedType(transferData)) {
			List<IProcessVariable> processVariables = new ArrayList<IProcessVariable>();

			if (object instanceof IProcessVariable) {
				processVariables.add((IProcessVariable) object);
			}
			if (object instanceof IProcessVariable[]) {
				for (IProcessVariable ds : (IProcessVariable[]) object) {
					processVariables.add(ds);
				}
			} else if (object instanceof Collection) {
				for (Object o : (Collection) object) {
					if (o instanceof IProcessVariable) {
						processVariables.add((IProcessVariable) o);
					}
				}
			}

			try {
				// write data to a byte array and then ask super to convert to
				// pMedium
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				DataOutputStream writeOut = new DataOutputStream(out);
				for (IProcessVariable processVariable : processVariables) {
					byte[] buffer = processVariable.getName().getBytes();
					writeOut.writeInt(buffer.length);
					writeOut.write(buffer);
				}
				byte[] buffer = out.toByteArray();
				writeOut.close();

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

		IProcessVariable[] result = null;

		if (isSupportedType(transferData)) {

			byte[] buffer = (byte[]) super.nativeToJava(transferData);

			List<IProcessVariable> received = new ArrayList<IProcessVariable>();
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(buffer);
				DataInputStream readIn = new DataInputStream(in);
				while (readIn.available() > 4) {
					int size = readIn.readInt();
					byte[] bytes = new byte[size];
					readIn.read(bytes);
					received.add(ControlSystemItemFactory
							.createProcessVariable(new String(bytes)));
				}
				readIn.close();
			} catch (IOException ex) {
				return null;
			}

			result = received.toArray(new IProcessVariable[received.size()]);
		}

		return result;
	}
}
