package org.csstudio.platform.ui.dnd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.csstudio.platform.model.rfc.ControlSystemEnum;
import org.csstudio.platform.model.rfc.ProcessVariable;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

public class PVTransfer extends ByteArrayTransfer {

	private static final String PVNAME = "pv_name";
	private static final int PV_ID = registerType(PVNAME);
	private static PVTransfer _instance = new PVTransfer();
 
	private PVTransfer() {}
 
	public static PVTransfer getInstance () {
		return _instance;
	}
	
	public void javaToNative (Object object, TransferData transferData) {
		if (object == null || !(object instanceof ProcessVariable[])) return;
 	
		if (isSupportedType(transferData)) {
			ProcessVariable[] pvs = (ProcessVariable[]) object;	
			try {
 			// write data to a byte array and then ask super to convert to pMedium
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				DataOutputStream writeOut = new DataOutputStream(out);
				for (int i = 0, length = pvs.length; i < length;  i++){
					byte[] buffer = pvs[i].getProperty().getBytes();
					writeOut.writeInt(buffer.length);
					writeOut.write(buffer);
					buffer = pvs[i].getControlSystemEnum().name().getBytes();
					writeOut.writeInt(buffer.length);
					writeOut.write(buffer);
					buffer = pvs[i].getDevice().getBytes();
					writeOut.writeInt(buffer.length);
					writeOut.write(buffer);
					buffer = pvs[i].getCharacteristic().getBytes();
					writeOut.writeInt(buffer.length);
					writeOut.write(buffer);
				}
				byte[] buffer = out.toByteArray();
				writeOut.close();
	 
				super.javaToNative(buffer, transferData);
	 			
			} catch (IOException e) {
			}
		}
	}
	
	public Object nativeToJava(TransferData transferData) {	 
		if (isSupportedType(transferData)) {
			byte[] buffer = (byte[])super.nativeToJava(transferData);
			if (buffer == null) return null;
			
			ProcessVariable[] processVariables = new ProcessVariable[0];
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(buffer);
				DataInputStream readIn = new DataInputStream(in);
				while(readIn.available() > 20) {
					//StringBuffer stringBuffer = new StringBuffer("");
					int size = readIn.readInt();
					byte[] propertyBytes = new byte[size];
					readIn.read(propertyBytes);
					String property = new String(propertyBytes);
					
					size = readIn.readInt();
					byte[] controlsystemBytes = new byte[size];
					readIn.read(controlsystemBytes);
					ControlSystemEnum controlSystem = ControlSystemEnum.valueOf(new String(controlsystemBytes));
					
					size = readIn.readInt();
					byte[] deviceBytes = new byte[size];
					readIn.read(deviceBytes);
					String device = new String(deviceBytes);
					
					size = readIn.readInt();
					byte[] characteristicBytes = new byte[size];
					readIn.read(characteristicBytes);
					String characteristic = new String(characteristicBytes);
					
					ProcessVariable pv = new ProcessVariable(controlSystem, device, property, characteristic);
					ProcessVariable[] newProcessVariables = new ProcessVariable[processVariables.length + 1];
					System.arraycopy(processVariables, 0, newProcessVariables, 0, processVariables.length);
					newProcessVariables[processVariables.length] = pv;
					processVariables = newProcessVariables;
				}
				readIn.close();
			} catch (IOException ex) {
				return null;
			}
			return processVariables;
		}
		return null;
	}
	
	protected String[] getTypeNames(){
		return new String[]{PVNAME};
	}
	
	protected int[] getTypeIds(){
		return new int[] {PV_ID};
	}
}
