package org.csstudio.csdata.test;

import java.util.Arrays;

import org.csstudio.csdata.Device;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class TestAdapterUtil extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		DeviceAndAPV device = new DeviceAndAPV("Device", "PV");
		DeviceAndAPV[] singleDevice = new DeviceAndAPV[] {device};
		DeviceAndAPV[] multipleDevices = new DeviceAndAPV[] {device, new DeviceAndAPV("Device2", "PV2")};
		MessageDialog.openInformation(
				window.getShell(),
				"My command",
				"Single DeviceAndAPV\n" +
				"Convert to single: " + AdapterUtil.convert(device,
						DeviceAndAPV.class.getName()) + "\n" +
				"Convert to an array: " + Arrays.toString((Object[]) AdapterUtil.convert(device,
						DeviceAndAPV[].class.getName())) + "\n" +
				"Convert to a Device: " + AdapterUtil.convert(device,
						Device.class.getName()) + "\n" +
				"Convert to a Device array: " + Arrays.toString((Object[]) AdapterUtil.convert(device,
						Device[].class.getName())) + "\n" +
				"Array of single DeviceAndAPV\n" +
				"Convert to single: " + AdapterUtil.convert(singleDevice,
						DeviceAndAPV.class.getName()) + "\n" +
				"Convert to an array: " + Arrays.toString((Object[]) AdapterUtil.convert(singleDevice,
						DeviceAndAPV[].class.getName())) + "\n" +
				"Convert to a Device: " + AdapterUtil.convert(singleDevice,
						Device.class.getName()) + "\n" +
				"Convert to a Device array: " + Arrays.toString((Object[]) AdapterUtil.convert(singleDevice,
				Device[].class.getName())) + "\n" +
				"Array of multiple DeviceAndAPV\n" +
				"Convert to an array: " + Arrays.toString((Object[]) AdapterUtil.convert(multipleDevices,
						DeviceAndAPV[].class.getName())) + "\n" +
				"Convert to a Device array: " + Arrays.toString((Object[]) AdapterUtil.convert(multipleDevices,
						Device[].class.getName())) + "\n" +
				"");
//		MessageDialog.openInformation(
//				window.getShell(),
//				"My command",
//				"Hello, CSS world " + Arrays.toString(ControlSystemObjectAdapter.getSerializableTypes(
//						new DeviceAndAPV("Device", "PV"))));
		return null;
	}

}
