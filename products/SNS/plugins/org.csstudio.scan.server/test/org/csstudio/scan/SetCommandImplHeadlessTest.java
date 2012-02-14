package org.csstudio.scan;

import java.util.concurrent.TimeoutException;

import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.commandimpl.SetCommandImpl;
import org.csstudio.scan.condition.WaitForDevicesCondition;
import org.csstudio.scan.device.DeviceContext;
import org.csstudio.scan.server.ScanCommandImpl;
import org.csstudio.scan.server.internal.ScanContextImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** [Headless] JUnit Plug-In test of the SetCommand with readback
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SetCommandImplHeadlessTest
{
    private DeviceContext devices;
    private ScanContextImpl context;

    @Before
    public void setup() throws Exception
    {
        devices = DeviceContext.getDefault();
        context = new ScanContextImpl(devices);
        devices.startDevices();
        new WaitForDevicesCondition(devices.getDevices()).await();
    }

    @After
    public void shutdown() throws Exception
    {
        devices.stopDevices();
    }

    @Test(timeout=20000)
    public void testSetWithoutWait() throws Exception
    {
        final SetCommand command = new SetCommand("setpoint", 1.0);
        final ScanCommandImpl<SetCommand> impl = new SetCommandImpl(command);

        // Set to 1.0
        impl.execute(context);

        // Set to 5.0
        command.setValue(5.0);

        // Jumping up to 10.0 takes about 3 seconds
        command.setValue(10.0);
        impl.execute(context);
    }

    @Test(timeout=20000)
    public void testSetWithWait() throws Exception
    {
        final SetCommand command = new SetCommand("setpoint", 1.0, "readback", true, 0.1, 10.0);
        final ScanCommandImpl<SetCommand> impl = new SetCommandImpl(command);

        // Set to 1.0 and wait for readback
        impl.execute(context);

        // Set to 5.0 and wait for readback
        command.setValue(5.0);
        impl.execute(context);

        // Jumping up to 10.0 takes about 3 seconds
        command.setValue(10.0);
        command.setTimeout(0.1);
        try
        {
            impl.execute(context);
        }
        catch (TimeoutException ex)
        {   // Expect the timeout
            System.out.println("Timed out as expected: " + ex.getMessage());
        }
    }
}
