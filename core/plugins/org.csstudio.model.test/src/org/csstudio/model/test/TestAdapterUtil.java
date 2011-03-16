package org.csstudio.model.test;

import java.util.Arrays;

import org.csstudio.model.ControlSystemObjectAdapter;
import org.csstudio.model.DeviceName;
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
				"Convert to single: " + ControlSystemObjectAdapter.convert(device,
						DeviceAndAPV.class.getName()) + "\n" +
				"Convert to an array: " + Arrays.toString((Object[]) ControlSystemObjectAdapter.convert(device,
						DeviceAndAPV[].class.getName())) + "\n" +
				"Convert to a Device: " + ControlSystemObjectAdapter.convert(device,
						DeviceName.class.getName()) + "\n" +
				"Convert to a Device array: " + Arrays.toString((Object[]) ControlSystemObjectAdapter.convert(device,
						DeviceName[].class.getName())) + "\n" +
				"Array of single DeviceAndAPV\n" +
				"Convert to single: " + ControlSystemObjectAdapter.convert(singleDevice,
						DeviceAndAPV.class.getName()) + "\n" +
				"Convert to an array: " + Arrays.toString((Object[]) ControlSystemObjectAdapter.convert(singleDevice,
						DeviceAndAPV[].class.getName())) + "\n" +
				"Convert to a Device: " + ControlSystemObjectAdapter.convert(singleDevice,
						DeviceName.class.getName()) + "\n" +
				"Convert to a Device array: " + Arrays.toString((Object[]) ControlSystemObjectAdapter.convert(singleDevice,
				DeviceName[].class.getName())) + "\n" +
				"Array of multiple DeviceAndAPV\n" +
				"Convert to an array: " + Arrays.toString((Object[]) ControlSystemObjectAdapter.convert(multipleDevices,
						DeviceAndAPV[].class.getName())) + "\n" +
				"Convert to a Device array: " + Arrays.toString((Object[]) ControlSystemObjectAdapter.convert(multipleDevices,
						DeviceName[].class.getName())) + "\n" +
				"");
//		MessageDialog.openInformation(
//				window.getShell(),
//				"My command",
//				"Hello, CSS world " + Arrays.toString(ControlSystemObjectAdapter.getSerializableTypes(
//						new DeviceAndAPV("Device", "PV"))));
		return null;
	}

}
