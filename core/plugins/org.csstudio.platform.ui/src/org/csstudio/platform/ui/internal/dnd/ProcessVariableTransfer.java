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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.model.CentralItemFactory;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * Drag-and-Drop transfer type for <code>IProcessVariableName</code>.
 * <p>
 * This transfer type expects the data to transfer to implement the
 * <code>IProcessVariable</code> interface, and the resulting data is
 * provided as an array of <code>IProcessVariable</code>.
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
	private int TYPE_ID;

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
		TYPE_ID = registerType(TYPE_NAME);
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
					received.add(CentralItemFactory
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
