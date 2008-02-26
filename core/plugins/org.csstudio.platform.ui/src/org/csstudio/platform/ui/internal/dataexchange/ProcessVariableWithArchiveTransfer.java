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
 package org.csstudio.platform.ui.internal.dataexchange;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.platform.model.IProcessVariableWithArchive;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * Drag-and-Drop transfer type for <code>IProcessVariableWithArchive</code>.
 * <p>
 * This transfer type expects the data to transfer to implement the
 * <code>IProcessVariableWithArchive</code> interface, and the resulting data
 * is provided as an array of <code>ProcessVariableWithArchive</code>.
 * <p>
 * Most of this implementation is from the javadoc for ByteArrayTransfer.
 * 
 * @author Kay Kasemir
 */
public class ProcessVariableWithArchiveTransfer extends ByteArrayTransfer {
	private static final String TYPE_NAME = "pv_with_archive_data_source";

	private static final int TYPE_ID = registerType(TYPE_NAME);

	private static ProcessVariableWithArchiveTransfer singleton_instance = new ProcessVariableWithArchiveTransfer();

	/**
	 * Hidden contructor.
	 * 
	 * @see #getInstance()
	 */
	private ProcessVariableWithArchiveTransfer() {
	}

	/** @return The singleton instance of the ArchiveDataSourceTransfer. */
	public static ProcessVariableWithArchiveTransfer getInstance() {
		return singleton_instance;
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}

	@Override
	protected int[] getTypeIds() {
		return new int[] { TYPE_ID };
	}

	@Override
	public void javaToNative(Object object, TransferData transferData) {
		if (!isSupportedType(transferData))
			return;

		IProcessVariableWithArchive[] data;
		if (object == null)
			return;
		if (object instanceof IProcessVariableWithArchive[])
			data = (IProcessVariableWithArchive[]) object;
		else if (object instanceof ArrayList) { // Table selection seems to use
												// ArrayList
			ArrayList list = (ArrayList) object;
			data = new IProcessVariableWithArchive[list.size()];
			for (int i = 0; i < data.length; i++)
				data[i] = (IProcessVariableWithArchive) list.get(i);
		} else
			return;
		try {
			// write data to a byte array and then ask super to convert to
			// pMedium
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DataOutputStream writeOut = new DataOutputStream(out);
			for (int i = 0; i < data.length; ++i) {
				byte[] buffer = data[i].getName().getBytes();
				writeOut.writeInt(buffer.length);
				writeOut.write(buffer);

				IArchiveDataSource archive = data[i].getArchiveDataSource();
				buffer = archive.getUrl().getBytes();
				writeOut.writeInt(buffer.length);
				writeOut.write(buffer);

				writeOut.writeInt(archive.getKey());

				buffer = archive.getName().getBytes();
				writeOut.writeInt(buffer.length);
				writeOut.write(buffer);
			}
			byte[] buffer = out.toByteArray();
			writeOut.close();

			super.javaToNative(buffer, transferData);
		} catch (IOException e) {
		}
	}

	@Override
	public Object nativeToJava(TransferData transferData) {
		if (!isSupportedType(transferData))
			return null;

		byte[] buffer = (byte[]) super.nativeToJava(transferData);
		if (buffer == null)
			return null;

		Vector<IProcessVariableWithArchive> received = new Vector<IProcessVariableWithArchive>();
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(buffer);
			DataInputStream readIn = new DataInputStream(in);
			// URL length, key, name length = 12?
			while (readIn.available() > 12) {
				int size = readIn.readInt();
				byte[] bytes = new byte[size];
				readIn.read(bytes);
				String pv = new String(bytes);

				size = readIn.readInt();
				bytes = new byte[size];
				readIn.read(bytes);
				String url = new String(bytes);

				int key = readIn.readInt();

				size = readIn.readInt();
				bytes = new byte[size];
				readIn.read(bytes);
				String arch_name = new String(bytes);

				received.add(CentralItemFactory
						.createProcessVariableWithArchive(pv, url, key,
								arch_name));
			}
			readIn.close();
		} catch (IOException ex) {
			return null;
		}
		IProcessVariableWithArchive array[] = new IProcessVariableWithArchive[received
				.size()];
		return received.toArray(array);
	}
}
