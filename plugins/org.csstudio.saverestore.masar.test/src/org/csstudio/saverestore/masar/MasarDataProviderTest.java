package org.csstudio.saverestore.masar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.csstudio.saverestore.CompletionNotifier;
import org.csstudio.saverestore.DataProviderException;
import org.csstudio.saverestore.SearchCriterion;
import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.SaveSet;
import org.csstudio.saverestore.data.SaveSetData;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VDisconnectedData;
import org.csstudio.saverestore.data.VSnapshot;
import org.diirt.util.time.Timestamp;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 * <code>MasarDataProviderTest</code> tests the methods of the {@link MasarDataProvider}. This is mostly a test if the
 * data provider properly notifies the registered {@link CompletionNotifier}s.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class MasarDataProviderTest {

    private MasarDataProvider dataProvider;
    private CompletionNotifier notifier;

    private Date date = new Date();
    private Branch branch = new Branch();
    private Branch someBranch = new Branch("someBranch", "someBranch");
    private Branch demoBranch = new Branch("demo", "demo");
    private BaseLevel branchBase1 = new BaseLevel(new Branch(), "base1", "base1");
    private BaseLevel branchBase2 = new BaseLevel(new Branch(), "base2", "base2");
    private SaveSet branchSaveSet = new SaveSet(branch, Optional.of(branchBase1),
        new String[] { "first", "second.bms" }, "someId");
    private SaveSet branchSaveSet2 = new SaveSet(branch, Optional.of(branchBase1),
        new String[] { "first", "foo", "bar", "second.bms" }, "someId");
    private Snapshot branchSnapshot = new Snapshot(branchSaveSet, date, "comment", "owner");
    private Snapshot branchSnapshot2 = new Snapshot(branchSaveSet, new Date(date.getTime() - 5000),
        "another comment", "user");
    private Snapshot branchSnapshot3 = new Snapshot(branchSaveSet, new Date(date.getTime() + 5000), "new snapshot",
        "user");
    private SaveSetData bsd = new SaveSetData(branchSaveSet, Arrays.asList("pv1", "pv"),
        Arrays.asList("rb1", "rb2"), Arrays.asList("d1", "d2"), "description");
    private VSnapshot snapshot = new VSnapshot(branchSnapshot3, Arrays.asList("pv1"), Arrays.asList(VDisconnectedData.INSTANCE),
        Timestamp.now(), null);

    @Before
    public void setUp() throws Exception {
        MasarException exception = new MasarException("Problem");
        notifier = mock(CompletionNotifier.class);
        MasarClient mc = mock(MasarClient.class);
        dataProvider = new MasarDataProvider(mc);
        dataProvider.addCompletionNotifier(notifier);

        when(mc.getServices()).thenReturn(Arrays.asList(branch, demoBranch));
        when(mc.getSystemConfigs(branch)).thenReturn(Arrays.asList(branchBase1, branchBase2));
        when(mc.getSystemConfigs(demoBranch)).thenReturn(new ArrayList<>(0));
        when(mc.getSaveSets(Optional.of(branchBase1), branch))
            .thenReturn(Arrays.asList(branchSaveSet, branchSaveSet2));
        when(mc.getSnapshots(branchSaveSet)).thenReturn(Arrays.asList(branchSnapshot, branchSnapshot2));
        when(mc.loadSaveSetData(branchSaveSet)).thenReturn(bsd);
        when(mc.createService("someBranch")).thenReturn(someBranch);
        when(mc.createService("bla")).thenThrow(exception);
        when(mc.saveSnapshot(snapshot, "comment")).thenReturn(snapshot);
        when(mc.saveSnapshot(snapshot, "comment3")).thenReturn(null);
        when(mc.saveSnapshot(snapshot, "comment4")).thenThrow(exception);
        when(mc.loadSnapshotData(branchSnapshot)).thenReturn(snapshot);
        when(mc.findSnapshots(branch, "temp", true, true, Optional.empty(), Optional.empty()))
            .thenReturn(Arrays.asList(branchSnapshot, branchSnapshot3));
        when(mc.findSnapshots(branch, "temp", true, false, Optional.empty(), Optional.empty()))
            .thenReturn(Arrays.asList(branchSnapshot3));
        when(mc.findSnapshots(branch, "temp", false, true, Optional.empty(), Optional.empty()))
            .thenReturn(Arrays.asList(branchSnapshot2));
        when(mc.takeSnapshot(branchSaveSet)).thenReturn(snapshot);
        when(mc.findSnapshotById(branch, 1)).thenReturn(Optional.of(branchSnapshot));
        when(mc.findSnapshotById(branch, 2)).thenReturn(Optional.empty());
    }

    @Test
    public void testGetBranches() throws DataProviderException {
        Branch[] branches = dataProvider.getBranches();
        assertEquals("2 branches are defined", 2, branches.length);
        assertEquals(branch, branches[0]);
        assertEquals(demoBranch, branches[1]);
    }

    @Test
    public void testGetBaseLevels() throws DataProviderException {
        BaseLevel[] baseLevels = dataProvider.getBaseLevels(branch);
        assertEquals("2 base levels are defined for default branch", 2, baseLevels.length);
        assertEquals(branchBase1, baseLevels[0]);
        assertEquals(branchBase2, baseLevels[1]);
        baseLevels = dataProvider.getBaseLevels(demoBranch);
        assertEquals("No base levels in demo branch", 0, baseLevels.length);
    }

    @Test
    public void testGetSaveSets() throws DataProviderException {
        SaveSet[] sets = dataProvider.getSaveSets(Optional.of(branchBase1), branch);
        assertEquals("2 base levels are defined for default branch", 2, sets.length);
        assertEquals(branchSaveSet, sets[0]);
        assertEquals(branchSaveSet2, sets[1]);
    }

    @Test
    public void testGetSnapshots() throws DataProviderException {
        Snapshot[] snaps = dataProvider.getSnapshots(branchSaveSet, true, Optional.empty());
        assertEquals("2 base levels are defined for default branch", 2, snaps.length);
        assertEquals(branchSnapshot, snaps[0]);
        assertEquals(branchSnapshot2, snaps[1]);
    }

    @Test
    public void testGetSaveSetContent() throws DataProviderException {
        SaveSetData data = dataProvider.getSaveSetContent(branchSaveSet);
        assertEquals(bsd, data);
    }

    @Test
    public void testCreateNewBranch() throws DataProviderException {
        Branch newBranch = dataProvider.createNewBranch(branch, "someBranch");
        assertEquals(someBranch, newBranch);
        verify(notifier, times(1)).branchCreated(newBranch);
        verify(notifier, only()).branchCreated(newBranch);

        try {
            newBranch = dataProvider.createNewBranch(branch, "bla");
            fail("Exception should happen");
        } catch (DataProviderException e) {
            assertNotNull(e.getMessage());
        }
        verify(notifier, times(1)).branchCreated(newBranch);
        verify(notifier, only()).branchCreated(newBranch);
    }

    @Test
    public void testSaveSnapshot() throws DataProviderException {
        VSnapshot snap = dataProvider.saveSnapshot(snapshot, "comment");
        assertTrue(snap.equalsExceptSnapshot(snapshot));
        verify(notifier, times(1)).snapshotSaved(snap);
        verify(notifier, only()).snapshotSaved(snap);

        try {
            dataProvider.saveSnapshot(snapshot, "comment4");
            fail("Exception should happen");
        } catch (DataProviderException e) {
            assertNotNull(e.getMessage());
        }
        verify(notifier, times(1)).snapshotSaved(snap);
        verify(notifier, only()).snapshotSaved(snap);

        Mockito.reset(notifier);

        snap = dataProvider.saveSnapshot(snapshot, "comment3");
        assertNull(snap);
        verify(notifier, never()).synchronised();
    }

    @Test
    public void testGetSnapshotContent() throws DataProviderException {
        VSnapshot data = dataProvider.getSnapshotContent(branchSnapshot);
        assertEquals(snapshot, data);
    }

    @Test
    public void testFindSnapshots() throws DataProviderException {
        List<SearchCriterion> criteria = dataProvider.getSupportedSearchCriteria();
        Snapshot[] snapshots = dataProvider.findSnapshots("temp", branch,
            Arrays.asList(criteria.get(0), criteria.get(1)), Optional.empty(), Optional.empty());
        assertEquals(2, snapshots.length);
        assertEquals(branchSnapshot2, snapshots[1]);
        assertEquals(branchSnapshot3, snapshots[0]);

        snapshots = dataProvider.findSnapshots("temp", branch, Arrays.asList(criteria.get(1)), Optional.empty(),
            Optional.empty());
        assertEquals(1, snapshots.length);
        assertEquals(branchSnapshot3, snapshots[0]);

        snapshots = dataProvider.findSnapshots("temp", branch, Arrays.asList(criteria.get(0)), Optional.empty(),
            Optional.empty());
        assertEquals(1, snapshots.length);
        assertEquals(branchSnapshot2, snapshots[0]);

        snapshots = dataProvider.findSnapshots("1", branch, Arrays.asList(criteria.get(2)), Optional.empty(),
            Optional.empty());
        assertEquals(1, snapshots.length);
        assertEquals(branchSnapshot, snapshots[0]);

        snapshots = dataProvider.findSnapshots("2", branch, Arrays.asList(criteria.get(2)), Optional.empty(),
            Optional.empty());
        assertEquals(0, snapshots.length);

        try {
            snapshots = dataProvider.findSnapshots("bla", branch, Arrays.asList(criteria.get(2)), Optional.empty(),
                Optional.empty());
            fail("Exception should occur");
        } catch (DataProviderException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testTakeSnapshot() throws DataProviderException {
        VSnapshot snap = dataProvider.takeSnapshot(branchSaveSet);
        assertEquals(snapshot, snap);
    }

}
