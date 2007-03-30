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
import org.csstudio.platform.model.IProcessVariableWithSample;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * Drag-and-Drop transfer type for <code>IProcessVariableWithSample</code>.
 * <p>
 * This transfer type expects the data to transfer to implement the
 * <code>IProcessVariableWithSample</code> interface, and the resulting data
 * is provided as an array of <code>ProcessVariableWithSample</code>.
 * <p>
 * Most of this implementation is from the javadoc for ByteArrayTransfer.
 * 
 * @author Jan Hatje  und Helge Rickens
 */
public class ProcessVariableWithSampleTransfer extends ByteArrayTransfer {
	private static final String TYPE_NAME = "pv_with_sample_data";

	private static final int TYPE_ID = registerType(TYPE_NAME);

	private static ProcessVariableWithSampleTransfer singleton_instance = new ProcessVariableWithSampleTransfer();

	/**
	 * Hidden contructor.
	 * 
	 * @see #getInstance()
	 */
	private ProcessVariableWithSampleTransfer() {
	}

	/** @return The singleton instance of the ArchiveDataSourceTransfer. */
	public static ProcessVariableWithSampleTransfer getInstance() {
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

		IProcessVariableWithSample[] data;
		if (object == null)
			return;
		if (object instanceof IProcessVariableWithSample[])
			data = (IProcessVariableWithSample[]) object;
		if (object instanceof IProcessVariableWithSample)
			data = new IProcessVariableWithSample[]{(IProcessVariableWithSample) object};
		else if (object instanceof ArrayList) { // Table selection seems to use
												// ArrayList
			ArrayList list = (ArrayList) object;
			data = new IProcessVariableWithSample[list.size()];
			for (int i = 0; i < data.length; i++)
				data[i] = (IProcessVariableWithSample) list.get(i);
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

				buffer =  new Integer(data[i].getDBRTyp()).toString().getBytes();
				writeOut.writeInt(buffer.length);
				writeOut.write(buffer);

				buffer = data[i].getEGU().getBytes();
				writeOut.writeInt(buffer.length);
				writeOut.write(buffer);

				buffer = new Double(data[i].getLow()).toString().getBytes();
				writeOut.writeInt(buffer.length);
				writeOut.write(buffer);
				
				buffer = new Double(data[i].getHigh()).toString().getBytes();
				writeOut.writeInt(buffer.length);
				writeOut.write(buffer);
				
				buffer =  new Integer(data[i].getPrecision()).toString().getBytes();
				writeOut.writeInt(buffer.length);
				writeOut.write(buffer);
				double[] values = data[i].getSampleValue();
				double[] time = data[i].getTimeStamp();
				String[] status =  data[i].getStatus();
				String[] severity =  data[i].getSeverity();
				writeOut.writeInt(values.length);
				for (int n=0;n<values.length&&n<time.length;n++) {
					buffer = new Double(values[n]).toString().getBytes();
					writeOut.writeInt(buffer.length);
					writeOut.write(buffer);
					buffer = new Double(time[n]).toString().getBytes();
					writeOut.writeInt(buffer.length);
					writeOut.write(buffer);
					buffer = status[n].getBytes();
					writeOut.writeInt(buffer.length);
					writeOut.write(buffer);
					buffer = severity[n].getBytes();
					writeOut.writeInt(buffer.length);
					writeOut.write(buffer);
				}
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

		Vector<IProcessVariableWithSample> received = new Vector<IProcessVariableWithSample>();
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(buffer);
			DataInputStream readIn = new DataInputStream(in);
			// URL length, key, name length = 12?
			while (readIn.available() > 1) {
				int size = readIn.readInt();
				byte[] bytes = new byte[size];
				readIn.read(bytes);
				String pvName = new String(bytes);
				
				size = readIn.readInt();
				bytes = new byte[size];
				readIn.read(bytes);
				int dbrTyp = Integer.parseInt(new String(bytes));

				size = readIn.readInt();
				bytes = new byte[size];
				readIn.read(bytes);
				String egu = new String(bytes);

				size = readIn.readInt();
				bytes = new byte[size];
				readIn.read(bytes);
				double low = Double.parseDouble(new String(bytes));
				
				
				size = readIn.readInt();
				bytes = new byte[size];
				readIn.read(bytes);
				double high = Double.parseDouble(new String(bytes));
				
				size = readIn.readInt();
				bytes = new byte[size];
				readIn.read(bytes);
				int precision = Integer.parseInt(new String(bytes));
				
				size = readIn.readInt();
				double[] sampleValues= new double[size];
				double[] timeStamp = new double[size];
				String[] status =  new String[size];
				String[] severity =  new String[size];

				for (int n=0;n<size;n++) {
					int s = readIn.readInt();
					bytes = new byte[s];
					readIn.read(bytes);
					sampleValues[n] = Double.parseDouble(new String(bytes));

					s = readIn.readInt();
					bytes = new byte[s];
					readIn.read(bytes);
					timeStamp[n] = Double.parseDouble(new String(bytes));
					
					s = readIn.readInt();
					bytes = new byte[s];
					readIn.read(bytes);
					status[n] = new String(bytes);

					s = readIn.readInt();
					bytes = new byte[s];
					readIn.read(bytes);
					severity[n] = new String(bytes);
				}
				received.add(CentralItemFactory
						.createProcessVariableWithSample(pvName, dbrTyp, egu, low, high,precision, sampleValues, timeStamp, status,severity));
			}
			readIn.close();
		} catch (IOException ex) {
			return null;
		}
		 catch (Exception ex) {
			System.out.println(ex);;
		}

		IProcessVariableWithArchive array[] = new IProcessVariableWithArchive[received
				.size()];
		return received.toArray(array);
	}
}
