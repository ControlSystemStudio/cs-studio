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
import java.util.Date;
import java.util.List;

import org.csstudio.platform.model.pvs.IProcessVariableAdress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

public class PVTransfer extends ByteArrayTransfer {
	
	/**
	 * The time period in which a local selection expires. Needed to handle
	 * cases, where data is dragged between different JVMs.
	 */
	private static final int SELECTION_EXPIRATION_PERIOD = 10000;

	private static final String PVNAME = "dnd_process_variablev_name";
	private static final int PV_ID = registerType(PVNAME);
	private static PVTransfer _instance = new PVTransfer();
	
	/**
	 * The items that were selected during the latest DnD operation.
	 */
	private List<IProcessVariableAdress> _selectedItems;

	/**
	 * The event time of the latest selection.
	 */
	private long _selectionSetTime;
 
	/**
	 * Hidden constructor.
	 */
	private PVTransfer() {
		_selectionSetTime = 0;
	}
 
	/**
	 * Returns the instance of this PVTransfer.
	 * @return PVTransfer
	 * 			The instance of this PVTransfer
	 */
	public static PVTransfer getInstance () {
		return _instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void javaToNative (Object object, TransferData transferData) {
		if (object == null || !(object instanceof IProcessVariableAdress[] || object instanceof List)) return;
		if (isSupportedType(transferData)) {
			IProcessVariableAdress[] pvs;
			if (object instanceof List) {
				pvs = (IProcessVariableAdress[]) ((List)object).toArray(new IProcessVariableAdress[((List)object).size()]);
			} else {
				pvs = (IProcessVariableAdress[]) object;	
			}
			try {
 			// write data to a byte array and then ask super to convert to pMedium
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				DataOutputStream writeOut = new DataOutputStream(out);
				for (int i = 0, length = pvs.length; i < length;  i++){
//					byte[] buffer = pvs[i].getProperty().getBytes();
//					writeOut.writeInt(buffer.length);
//					writeOut.write(buffer);
//					buffer = pvs[i].getControlSystemEnum().name().getBytes();
//					writeOut.writeInt(buffer.length);
//					writeOut.write(buffer);
//					String device = pvs[i].getDevice();
//					if (device==null) {
//						device = "";
//					}	
//					buffer = device.getBytes();
//					writeOut.writeInt(buffer.length);
//					writeOut.write(buffer);
//					String characteristic = pvs[i].getCharacteristic();
//					if (characteristic==null) {
//						characteristic = "";
//					}						
//					buffer = characteristic.getBytes();
//					writeOut.writeInt(buffer.length);
//					writeOut.write(buffer);
					byte[] buffer = pvs[i].getRawName().getBytes();
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
	
	/**
	 * {@inheritDoc}
	 */
	public Object nativeToJava(TransferData transferData) {	 
		if (isSupportedType(transferData)) {
			byte[] buffer = (byte[])super.nativeToJava(transferData);
			if (buffer == null) return null;
			
			IProcessVariableAdress[] processVariables = new IProcessVariableAdress[0];
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(buffer);
				DataInputStream readIn = new DataInputStream(in);
				while(readIn.available() > 0) {
//					int size = readIn.readInt();
//					byte[] propertyBytes = new byte[size];
//					readIn.read(propertyBytes);
//					String property = new String(propertyBytes);
//					
//					size = readIn.readInt();
//					byte[] controlsystemBytes = new byte[size];
//					readIn.read(controlsystemBytes);
//					ControlSystemEnum controlSystem = ControlSystemEnum.valueOf(new String(controlsystemBytes));
//					
//					size = readIn.readInt();
//					byte[] deviceBytes = new byte[size];
//					readIn.read(deviceBytes);
//					String device = new String(deviceBytes);
//					
//					size = readIn.readInt();
//					byte[] characteristicBytes = new byte[size];
//					readIn.read(characteristicBytes);
//					String characteristic = new String(characteristicBytes);
					
					int size = readIn.readInt();
					byte[] pathBytes = new byte[size];
					readIn.read(pathBytes);
					String fullPath = new String(pathBytes);
					
					IProcessVariableAdress pv = ProcessVariableAdressFactory.getInstance().createProcessVariableAdress(fullPath);
					//ProcessVariable pv = new ProcessVariable(controlSystem, device, property, characteristic);
					IProcessVariableAdress[] newProcessVariables = new IProcessVariableAdress[processVariables.length + 1];
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
	
	/**
	 * {@inheritDoc}
	 */
	protected String[] getTypeNames(){
		return new String[]{PVNAME};
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected int[] getTypeIds(){
		return new int[] {PV_ID};
	}
	
	/**
	 * Gets the transfer data for local use.
	 * @return List of {@link IProcessVariableAdress}
	 * 			The transfer data for local use.
	 */
	public List<IProcessVariableAdress> getSelectedItems() {
		long dist = new Date().getTime() - _selectionSetTime;

		if (dist > SELECTION_EXPIRATION_PERIOD) {
			return null;
		}
		return _selectedItems;
	}

	/**
	 * Sets the transfer data for local use.
	 * @param selectedItems
	 *            the transfer data
	 */
	public void setSelectedItems(final List<IProcessVariableAdress> selectedItems) {
		_selectedItems = selectedItems;
		_selectionSetTime = new Date().getTime();
	}
}
