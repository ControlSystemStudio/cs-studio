package org.csstudio.saverestore.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

import org.csstudio.saverestore.DataProvider;
import org.csstudio.saverestore.DataProviderException;
import org.csstudio.saverestore.DataProviderWrapper;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.data.SaveSet;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VDisconnectedData;
import org.csstudio.saverestore.data.VSnapshot;
import org.csstudio.saverestore.ui.util.VTypePair;
import org.diirt.util.array.ArrayDouble;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.Alarm;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.Time;
import org.diirt.vtype.VDouble;
import org.diirt.vtype.VDoubleArray;
import org.diirt.vtype.ValueFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * <code>SnapshotViewerControllerTest</code> tests the methods in {@link SnapshotViewerController}.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SnapshotViewerControllerTest {

    private SnapshotViewerController controller;

    @Before
    public void setUp() throws Exception {
        ISnapshotReceiver editor = mock(ISnapshotReceiver.class);
        when(editor.getShell()).thenReturn(null);
        controller = new SnapshotViewerController(editor);

        Field f = SnapshotViewerController.class.getDeclaredField("UI_EXECUTOR");
        f.setAccessible(true);
        Executor ex = r -> r.run();
        f.set(null, ex);

        DataProvider dpr = mock(DataProvider.class);
        when(dpr.takeSnapshot(any(SaveSet.class))).thenReturn(null);
        DataProviderWrapper dpw = new DataProviderWrapper("someId", "name", "description", dpr);
        // Mockito.when(service.getDataProvider("someId")).thenReturn(dpw);
        f = SaveRestoreService.class.getDeclaredField("dataProviders");
        f.setAccessible(true);
        f.set(SaveRestoreService.getInstance(), Arrays.asList(dpw));
    }

    /**
     * Tests {@link SnapshotViewerController#addSnapshot(VSnapshot)}.
     */
    @Test
    public void testAddSnapshot() {
        VSnapshot snapshot = createSnapshot(true);
        List<TableEntry> entries = controller.addSnapshot(snapshot);
        assertEquals(1, controller.getNumberOfSnapshots());
        assertTrue(controller.getSnapshot(0) == snapshot);
        assertEquals(3, entries.size());
        TableEntry e = entries.get(0);
        assertEquals("pv1", e.pvNameProperty().get());
        assertEquals(1, e.idProperty().get());
        assertEquals(5d, ((VDouble) ((VTypePair) e.valueProperty().get()).value).getValue(), 0);
        assertEquals(AlarmSeverity.MINOR, e.severityProperty().get());
        assertEquals("HIGH", e.statusProperty().get());
        assertTrue(controller.snapshotRestorableProperty().get());

        entries = controller.addSnapshot(snapshot);
        assertEquals(2, controller.getNumberOfSnapshots());
        assertEquals(3, entries.size());
        e = entries.get(0);
        VTypePair pair = e.compareValueProperty(1).get();
        VDouble val = (VDouble) ((VTypePair) e.valueProperty().get()).value;
        assertEquals(val.getValue(), ((VDouble) pair.value).getValue());
        assertTrue(val == pair.value);
    }

    /**
     * Tests {@link SnapshotViewerController#setAsBase(int)} and {@link SnapshotViewerController#removeSnapshot(int)}
     */
    @Test
    public void testMoveRemoveSnapshot() {
        VSnapshot snapshot1 = createSnapshot(true);
        VSnapshot snapshot2 = createSnapshot(true);
        controller.addSnapshot(snapshot1);
        controller.addSnapshot(snapshot2);

        assertTrue(snapshot1 == controller.baseSnapshotProperty().get());
        controller.setAsBase(1);
        assertTrue(snapshot2 == controller.baseSnapshotProperty().get());
        assertTrue(snapshot1 == controller.getSnapshot(1));
        assertTrue(snapshot2 == controller.getSnapshot(0));

        controller.removeSnapshot(1);
        assertEquals(1, controller.getAllSnapshots().size());
        controller.addSnapshot(snapshot1);

        try {
            controller.removeSnapshot(0);
            fail("Removing the base snapshot is not allowed");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    /**
     * Tests {@link SnapshotViewerController#takeSnapshot()} and
     * {@link SnapshotViewerController#saveSnapshot(String, VSnapshot)}.
     *
     * @throws DataProviderException
     */
    @Test
    public void testTakeAndSaveSnapshot() throws DataProviderException {
        VSnapshot snapshot1 = createSnapshot(true);
        controller.addSnapshot(snapshot1);
        controller.takeSnapshot();
        DataProvider provider = SaveRestoreService.getInstance().getDataProvider("someId").getProvider();
        verify(provider, times(1)).isTakingSnapshotsSupported();
        verify(provider, times(0)).takeSnapshot(snapshot1.getSaveSet());
        when(provider.isTakingSnapshotsSupported()).thenReturn(true);
        controller.takeSnapshot();
        verify(provider, times(1)).takeSnapshot(snapshot1.getSaveSet());
        verify(controller.getSnapshotReceiver(), times(2)).addSnapshot(any(VSnapshot.class), anyBoolean());
        VSnapshot snapshot2 = createSnapshot(false);
        controller.addSnapshot(snapshot2);
        assertEquals(1, controller.getSnapshots(true).size());
        assertTrue(snapshot2 == controller.getSnapshots(true).get(0));
        assertEquals(1, controller.getSnapshots(false).size());
        assertTrue(snapshot1 == controller.getSnapshots(false).get(0));

        VSnapshot snapshot3 = createSnapshot(true);
        when(provider.saveSnapshot(snapshot2, "comment")).thenReturn(snapshot3);
        controller.saveSnapshot("comment", snapshot2);
        verify(provider, times(1)).saveSnapshot(snapshot2, "comment");

    }

    /**
     * Tests {@link SnapshotViewerController#setFilter(String)}.
     */
    @Test
    public void testFilter() {
        VSnapshot snapshot1 = createSnapshot(true);
        List<TableEntry> entries = controller.addSnapshot(snapshot1);
        assertEquals(3, entries.size());
        entries = controller.setFilter("2");
        assertEquals(2, entries.size());
        assertEquals("pv2", entries.get(0).pvNameProperty().get());
        assertEquals("complexName:2a", entries.get(1).pvNameProperty().get());

        entries = controller.setFilter("[a-zN]+:.*");
        assertEquals(1, entries.size());
        assertEquals("complexName:2a", entries.get(0).pvNameProperty().get());
    }

    private static VSnapshot createSnapshot(boolean saved) {
        SaveSet set = new SaveSet(new Branch(), Optional.empty(), new String[] { "first", "second", "third" },
            "someId");
        Snapshot snapshot;
        if (saved) {
            snapshot = new Snapshot(set, new Date(), "comment", "owner");
        } else {
            snapshot = new Snapshot(set);
        }
        Date d = new Date(1455296909369L);
        Date d2 = new Date(1455296909379L);
        Alarm alarmNone = ValueFactory.alarmNone();
        Alarm alarm = ValueFactory.newAlarm(AlarmSeverity.MINOR, "HIGH");
        Display display = ValueFactory.displayNone();
        Time time = ValueFactory.newTime(Timestamp.of(d));
        Time time2 = ValueFactory.newTime(Timestamp.of(d2));

        VDouble val1 = ValueFactory.newVDouble(5d, alarm, time, display);
        VDoubleArray val2 = ValueFactory.newVDoubleArray(new ArrayDouble(1, 2, 3), alarmNone, time2, display);
        VDouble rval1 = ValueFactory.newVDouble(6d, alarmNone, time, display);
        VDoubleArray rval2 = ValueFactory.newVDoubleArray(new ArrayDouble(1, 1, 1), alarmNone, time, display);

        return new VSnapshot(snapshot, Arrays.asList("pv1", "pv2", "complexName:2a"), Arrays.asList(true, false, true),
            Arrays.asList(val1, val2, VDisconnectedData.INSTANCE), Arrays.asList("rb1", "rb2", "rb3"),
            Arrays.asList(rval1, rval2, VDisconnectedData.INSTANCE), Arrays.asList("50", "Math.min(x,3)", "30"),
            time.getTimestamp());
    }
}
