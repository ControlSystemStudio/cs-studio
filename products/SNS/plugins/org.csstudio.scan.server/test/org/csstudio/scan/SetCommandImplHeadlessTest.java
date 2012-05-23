package org.csstudio.scan;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.commandimpl.SetCommandImpl;
import org.csstudio.scan.condition.WaitForDevicesCondition;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.device.DeviceContext;
import org.csstudio.scan.log.DataLog;
import org.csstudio.scan.server.ScanCommandImpl;
import org.csstudio.scan.server.ScanContext;
import org.csstudio.scan.server.internal.Scan;
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
    private ScanContext context;
    private DataLog data_logger;

    @Before
    public void setup() throws Exception
    {
        // To see mostly the scan log, enable all logs...
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.ALL);
        for (Handler handler : logger.getHandlers())
            handler.setLevel(Level.ALL);
        // .. then disable the PV-related messages
        Logger.getLogger("org.csstudio.utility.pv").setLevel(Level.WARNING);
        Logger.getLogger("com.cosylab").setLevel(Level.WARNING);
        Logger.getLogger("org.csstudio.scan.device.PVDevice").setLevel(Level.WARNING);

        devices = DeviceContext.getDefault();
        devices.startDevices();
        new WaitForDevicesCondition(devices.getDevices()).await();

        final Scan scan = new Scan("Test", devices);
        context = scan;
        data_logger = scan.getDataLogger();
    }

    @After
    public void shutdown() throws Exception
    {
        devices.stopDevices();
    }

    @Test(timeout=5000)
    public void testSetWithoutWait() throws Exception
    {
        final SetCommand command = new SetCommand("setpoint", 1.0);
        final ScanCommandImpl<SetCommand> impl = new SetCommandImpl(command);

        // Set to 1, 5, 10 with a little delay so it can be observed on displays
        impl.execute(context);
        Thread.sleep(500);

        command.setValue(5.0);
        impl.execute(context);
        Thread.sleep(500);

        command.setValue(10.0);
        impl.execute(context);
        Thread.sleep(500);

        // .. readback will not follow as quickly,
        // but that's not checked

        System.out.println("Data logged for 'setpoint':");
        final List<ScanSample> samples =
                data_logger.getScanData().getSamples("setpoint");
        for (ScanSample sample : samples)
            System.out.println(sample);
        assertEquals(3, samples.size());
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
        // This timeout is too small
        command.setTimeout(0.1);
        try
        {
            impl.execute(context);
        }
        catch (TimeoutException ex)
        {   // Expect the timeout
            System.out.println("Timed out as expected: " + ex.getMessage());
        }

        // Use 'forever' as timeout
        command.setTimeout(0.0);
        impl.execute(context);

        System.out.println("Data logged for 'readback':");
        final List<ScanSample> samples =
                data_logger.getScanData().getSamples("readback");
        for (ScanSample sample : samples)
            System.out.println(sample);
        assertEquals(3, samples.size());
    }
}
