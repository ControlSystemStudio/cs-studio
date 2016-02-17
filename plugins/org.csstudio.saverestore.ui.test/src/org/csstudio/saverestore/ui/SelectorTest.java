package org.csstudio.saverestore.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

import org.csstudio.saverestore.CompletionNotifier;
import org.csstudio.saverestore.DataProvider;
import org.csstudio.saverestore.DataProvider.ImportType;
import org.csstudio.saverestore.DataProviderException;
import org.csstudio.saverestore.DataProviderWrapper;
import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.UnsupportedActionException;
import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.BeamlineSet;
import org.csstudio.saverestore.data.BeamlineSetData;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.VNoData;
import org.csstudio.saverestore.data.VSnapshot;
import org.diirt.util.time.Timestamp;
import org.eclipse.jface.window.IShellProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * <code>SelectorTest</code> tests various behaviour patterns of the {@link Selector} implementation.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SelectorTest {

    private Selector selector;
    private DataProviderWrapper dataProvider;
    private Date date = new Date();

    private Branch newBranch = new Branch("newBranch", "newBranch");
    private Branch branch = new Branch();
    private Branch someBranch = new Branch("someBranch", "someBranch");
    private BaseLevel branchBaseLevel = new BaseLevel(branch, "base1", "base1");
    private BaseLevel branchBaseLevel2 = new BaseLevel(branch, "base2", "base2");
    private BaseLevel someBranchBaseLevel = new BaseLevel(someBranch, "someBase", "someBase");
    private BeamlineSet branchBeamlineSet = new BeamlineSet(branch, Optional.of(branchBaseLevel),
        new String[] { "first", "second.bms" }, "someId");
    private BeamlineSet branchBeamlineSet2 = new BeamlineSet(branch, Optional.of(branchBaseLevel),
        new String[] { "first", "foo", "bar", "second.bms" }, "someId");
    private BeamlineSet someBranchBeamlineSet = new BeamlineSet(someBranch, Optional.of(someBranchBaseLevel),
        new String[] { "first", "foo", "haha", "second.bms" }, "someId");
    private Snapshot branchSnapshot = new Snapshot(branchBeamlineSet, date, "comment", "owner");
    private Snapshot branchSnapshot2 = new Snapshot(branchBeamlineSet, new Date(date.getTime() - 5000),
        "another comment", "user");
    private Snapshot branchSnapshot3 = new Snapshot(branchBeamlineSet, new Date(date.getTime() + 5000), "new snapshot",
        "user");
    private Snapshot someBranchSnapshot = new Snapshot(someBranchBeamlineSet, new Date(date.getTime() + 5000), "new snapshot",
        "user");
    private BeamlineSetData bsd = new BeamlineSetData(branchBeamlineSet, Arrays.asList("pv1", "pv"),
        Arrays.asList("rb1", "rb2"), Arrays.asList("d1", "d2"), "description");
    private VSnapshot snapshot = new VSnapshot(branchSnapshot3, Arrays.asList("pv1"), Arrays.asList(VNoData.INSTANCE),
        Timestamp.now(), null);
    private VSnapshot xSnapshot = new VSnapshot(someBranchSnapshot, Arrays.asList("pv1"), Arrays.asList(VNoData.INSTANCE),
        Timestamp.now(), null);

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        IShellProvider editor = mock(IShellProvider.class);
        when(editor.getShell()).thenReturn(null);
        selector = new Selector(editor);

        Field f = Selector.class.getDeclaredField("UI_EXECUTOR");
        f.setAccessible(true);
        Executor ex = r -> r.run();
        f.set(null, ex);

        f = Selector.class.getDeclaredField("SERVICE_EXECUTOR");
        f.setAccessible(true);
        BiConsumer<String, Runnable> se = (s, r) -> r.run();
        f.set(null, se);

        DataProvider dpr = mock(DataProvider.class);
        dataProvider = new DataProviderWrapper("someId", "name", "description", dpr);
        f = SaveRestoreService.class.getDeclaredField("dataProviders");
        f.setAccessible(true);
        f.set(SaveRestoreService.getInstance(), Arrays.asList(dataProvider));

        when(dpr.areBranchesSupported()).thenReturn(true);
        when(dpr.areBaseLevelsSupported()).thenReturn(true);
        // when(dpr.createNewBranch(any(Branch.class), matches("newBranch"))).thenReturn(newBranch);
        when(dpr.getBranches()).thenReturn(new Branch[] { branch, someBranch });
        when(dpr.getBaseLevels(branch)).thenReturn(new BaseLevel[] { branchBaseLevel, branchBaseLevel2 });
        when(dpr.getBaseLevels(someBranch)).thenReturn(new BaseLevel[] { someBranchBaseLevel });
        when(dpr.getBaseLevels(newBranch)).thenReturn(new BaseLevel[0]);
        when(dpr.getBeamlineSets(Optional.of(branchBaseLevel), branch))
            .thenReturn(new BeamlineSet[] { branchBeamlineSet, branchBeamlineSet2 });
        when(dpr.getBeamlineSets(Optional.of(branchBaseLevel2), branch)).thenReturn(new BeamlineSet[0]);
        when(dpr.getSnapshots(branchBeamlineSet, false, Optional.empty()))
            .thenReturn(new Snapshot[] { branchSnapshot, branchSnapshot2 });
        when(dpr.getSnapshots(branchBeamlineSet2, false, Optional.empty())).thenReturn(new Snapshot[0]);

        final CompletionNotifier[] notifier = new CompletionNotifier[1];
        doAnswer(inv -> {
            notifier[0] = (CompletionNotifier) inv.getArguments()[0];
            return null;
        }).when(dpr).addCompletionNotifier(any(CompletionNotifier.class));

        when(dpr.createNewBranch(any(Branch.class), matches("newBranch"))).then(inv -> {
            when(dpr.getBranches()).thenReturn(new Branch[] { branch, someBranch, newBranch });
            notifier[0].branchCreated(newBranch);
            return null;
        });
        when(dpr.deleteBeamlineSet(branchBeamlineSet, "comment")).then(inv -> {
            notifier[0].beamlineSetDeleted(branchBeamlineSet);
            return null;
        });
        when(dpr.saveBeamlineSet(bsd, "comment")).then(inv -> {
            notifier[0].beamlineSetSaved((BeamlineSetData) inv.getArguments()[0]);
            return null;
        });
        when(dpr.synchronise()).then(inv -> {
            notifier[0].synchronised();
            return null;
        });
        when(dpr.importData(any(BeamlineSet.class), any(Branch.class), any(Optional.class), any(ImportType.class)))
            .then(inv -> {
                Object[] args = inv.getArguments();
                notifier[0].dataImported((BeamlineSet) args[0], (Branch) args[1], (Optional<BaseLevel>) args[2]);
                return null;
            });
        when(dpr.saveSnapshot(any(VSnapshot.class), anyString())).then(inv -> {
            notifier[0].snapshotSaved((VSnapshot) inv.getArguments()[0]);
            return null;
        });
        when(dpr.tagSnapshot(branchSnapshot, Optional.of("name"), Optional.of("message"))).then(inv -> {
            Map<String,String> parameters = new HashMap<>();
            parameters.put(Snapshot.TAG_NAME, ((Optional<String>)inv.getArguments()[1]).get());
            parameters.put(Snapshot.TAG_MESSAGE, ((Optional<String>)inv.getArguments()[2]).get());
            Snapshot snapshot = new Snapshot(branchSnapshot.getBeamlineSet(),branchSnapshot.getDate(),
                branchSnapshot.getComment(), branchSnapshot.getOwner(), parameters);
            notifier[0].snapshotTagged(snapshot);
            return null;
        });
    }

    @After
    public void tearDown() throws Exception {
        selector.dispose();
        Field f = SaveRestoreService.class.getDeclaredField("selectedDataProvider");
        f.setAccessible(true);
        f.set(SaveRestoreService.getInstance(), null);
        f = SaveRestoreService.class.getDeclaredField("dataProviders");
        f.setAccessible(true);
        f.set(SaveRestoreService.getInstance(), null);
    }

    /**
     * Test selector initialisation and default settings.
     * @throws DataProviderException
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testInit() throws DataProviderException {
        SaveRestoreService.getInstance().setSelectedDataProvider(dataProvider);
        List<Branch> branches = selector.branchesProperty().get();
        assertEquals("Two branches are available at startup", 2, branches.size());
        assertEquals("The order of branches is prescribed", branch, branches.get(0));
        assertEquals("The order of branches is prescribed", someBranch, branches.get(1));
        Branch branch = selector.selectedBranchProperty().get();
        assertEquals("The first branch should be selected by default", branches.get(0), branch);
        List<BaseLevel> baseLevels = selector.baseLevelsProperty().get();
        assertEquals("There should be exactly 2 base levels in the selected branch", 2, baseLevels.size());
        assertEquals("The order of base levels is prescribed", branchBaseLevel, baseLevels.get(0));
        assertEquals("The order of base levels is prescribed", branchBaseLevel2, baseLevels.get(1));
        assertNull("No base level is selected by default", selector.selectedBaseLevelProperty().get());
        assertTrue("No beamline sets are available", selector.beamlineSetsProperty().get().isEmpty());
        assertNull("No beamline set is selected by default", selector.selectedBeamlineSetProperty().get());
        assertTrue("No snapshots are available", selector.snapshotsProperty().get().isEmpty());
        verify(dataProvider.getProvider(), times(1)).addCompletionNotifier(any(CompletionNotifier.class));
        verify(dataProvider.getProvider(), never()).getBeamlineSets(any(Optional.class), any(Branch.class));
        verify(dataProvider.getProvider(), never()).getSnapshots(any(BeamlineSet.class), any(Boolean.class),
            any(Optional.class));
    }

    /**
     * Test the output of selecting different values (branches, base levels, beamline sets).
     */
    @Test
    public void testSelecting() {
        SaveRestoreService.getInstance().setSelectedDataProvider(dataProvider);
        selector.selectedBaseLevelProperty().set(branchBaseLevel);
        List<BeamlineSet> beamlineSets = selector.beamlineSetsProperty().get();
        assertEquals("Exactly 2 beamline sets are available for the selected base level", 2, beamlineSets.size());
        assertEquals("Order of beamline sets is prescribed", branchBeamlineSet, beamlineSets.get(0));
        assertEquals("Order of beamline sets is prescribed", branchBeamlineSet2, beamlineSets.get(1));
        assertNull("No beamline set is selected by default", selector.selectedBeamlineSetProperty().get());
        selector.selectedBeamlineSetProperty().set(branchBeamlineSet);
        List<Snapshot> snapshots = selector.snapshotsProperty().get();
        assertEquals("Exactly 2 snapshots are available for the selected beamline set", 2, snapshots.size());
        assertEquals("Order of snapshots is prescribed", branchSnapshot, snapshots.get(0));
        assertEquals("Order of snapshots is prescribed", branchSnapshot2, snapshots.get(1));
        selector.selectedBaseLevelProperty().set(branchBaseLevel2);
        assertTrue("No beamline sets are available for the second base level",
            selector.beamlineSetsProperty().get().isEmpty());
        assertNull("No beamline set is selected by default", selector.selectedBeamlineSetProperty().get());
        assertTrue("No snapshots are available", selector.snapshotsProperty().get().isEmpty());
    }

    /**
     * Test the selection of the default branch at startup.
     */
    @Test
    public void testDefaultBranchSelection() {
        selector.setFirstTimeBranch("someBranch");
        SaveRestoreService.getInstance().setSelectedDataProvider(dataProvider);
        List<Branch> branches = selector.branchesProperty().get();
        assertEquals("Exactly two branches are available", 2, branches.size());
        Branch branch = selector.selectedBranchProperty().get();
        assertEquals("someBranch was preselected and should be the selected branch", someBranch, branch);
    }

    /**
     * Test notifications when a new branch is created.
     *
     * @throws UnsupportedActionException
     * @throws DataProviderException
     */
    @Test
    public void testBranchCreatedNotifications() throws UnsupportedActionException, DataProviderException {
        SaveRestoreService.getInstance().setSelectedDataProvider(dataProvider);
        verify(dataProvider.getProvider(), times(1)).getBranches();
        assertEquals("Default branch is selected", branch, selector.selectedBranchProperty().get());
        dataProvider.getProvider().createNewBranch(branch, "newBranch");
        // when new branch is created and selector notified, it should re-query the branches
        verify(dataProvider.getProvider(), times(2)).getBranches();
        assertEquals("New branch is selected", newBranch, selector.selectedBranchProperty().get());
    }

    /**
     * Test notifications when beamline set is deleted.
     *
     * @throws UnsupportedActionException
     * @throws DataProviderException
     */
    @Test
    public void testDeleteBeamlineSetNotifications() throws UnsupportedActionException, DataProviderException {
        SaveRestoreService.getInstance().setSelectedDataProvider(dataProvider);
        selector.selectedBranchProperty().set(branch);
        selector.selectedBaseLevelProperty().set(branchBaseLevel);

        List<BeamlineSet> beamlineSets = selector.beamlineSetsProperty().get();
        assertEquals("There are two beamline sets initially", 2, beamlineSets.size());
        assertTrue(beamlineSets.contains(branchBeamlineSet));
        dataProvider.getProvider().deleteBeamlineSet(branchBeamlineSet, "comment");
        beamlineSets = selector.beamlineSetsProperty().get();
        assertEquals("Only one beamline set remains after 1 is deleted", 1, beamlineSets.size());
        assertFalse("Deleted beamline set should not be available", beamlineSets.contains(branchBeamlineSet));
    }

    /**
     * Test notifications when beamline set is saved.
     *
     * @throws UnsupportedActionException
     * @throws DataProviderException
     */
    @Test
    public void testSaveBeamlineSetNotifications() throws UnsupportedActionException, DataProviderException {
        SaveRestoreService.getInstance().setSelectedDataProvider(dataProvider);
        selector.selectedBaseLevelProperty().set(branchBaseLevel);
        // after calling saveBeamlineSet the selector should fetch the beamline sets again
        verify(dataProvider.getProvider(), times(1)).getBeamlineSets(Optional.of(branchBaseLevel), branch);
        dataProvider.getProvider().saveBeamlineSet(bsd, "comment");
        verify(dataProvider.getProvider(), times(2)).getBeamlineSets(Optional.of(branchBaseLevel), branch);

        // save a beamline set in a different base level
        // if the beamline set was saved for another base level, there should be no refetching of the beamline sets
        selector.selectedBaseLevelProperty().set(branchBaseLevel2);
        dataProvider.getProvider().saveBeamlineSet(bsd, "comment");
        verify(dataProvider.getProvider(), times(2)).getBeamlineSets(Optional.of(branchBaseLevel), branch);
    }

    /**
     * Test notifications when repository is synchronised.
     *
     * @throws DataProviderException
     */
    @Test
    public void testSynchronisedNotifications() throws DataProviderException {
        SaveRestoreService.getInstance().setSelectedDataProvider(dataProvider);
        selector.selectedBaseLevelProperty().set(branchBaseLevel);
        selector.selectedBeamlineSetProperty().set(branchBeamlineSet);
        dataProvider.getProvider().synchronise();
        verify(dataProvider.getProvider(), times(2)).getBranches();
        assertNull("No base level is selected after synchronisation", selector.selectedBaseLevelProperty().get());
        assertNull("No beamline set is selected after synchronisation", selector.selectedBeamlineSetProperty().get());
        assertTrue("No beamline sets are available after synchronisation",
            selector.beamlineSetsProperty().get().isEmpty());
        assertTrue("No snapshots are available after synchronisation", selector.snapshotsProperty().get().isEmpty());
    }

    /**
     * Test notifications when data import happens.
     *
     * @throws DataProviderException
     */
    @Test
    public void testDataImportNotifications() throws DataProviderException {
        SaveRestoreService.getInstance().setSelectedDataProvider(dataProvider);
        selector.selectedBaseLevelProperty().set(branchBaseLevel);
        verify(dataProvider.getProvider(), times(1)).getBranches();
        verify(dataProvider.getProvider(), times(1)).getBaseLevels(branch);
        verify(dataProvider.getProvider(), times(1)).getBeamlineSets(Optional.of(branchBaseLevel), branch);
        dataProvider.getProvider().importData(branchBeamlineSet, someBranch, Optional.of(someBranchBaseLevel),
            ImportType.BEAMLINE_SET);
        // data imported into a different branch
        verify(dataProvider.getProvider(), times(1)).getBranches();
        verify(dataProvider.getProvider(), times(1)).getBaseLevels(branch);
        verify(dataProvider.getProvider(), times(1)).getBeamlineSets(Optional.of(branchBaseLevel), branch);

        // imported into the selected base level - beamline sets should be reloaded
        dataProvider.getProvider().importData(someBranchBeamlineSet, branch, Optional.of(branchBaseLevel),
            ImportType.BEAMLINE_SET);
        verify(dataProvider.getProvider(), times(1)).getBranches();
        verify(dataProvider.getProvider(), times(1)).getBaseLevels(branch);
        verify(dataProvider.getProvider(), times(2)).getBeamlineSets(Optional.of(branchBaseLevel), branch);

        // imported into selected branch, different base level - base levels and beamline sets should be
        // refetched, in case it is a new base level
        dataProvider.getProvider().importData(someBranchBeamlineSet, branch, Optional.of(branchBaseLevel2),
            ImportType.BEAMLINE_SET);
        verify(dataProvider.getProvider(), times(1)).getBranches();
        verify(dataProvider.getProvider(), times(2)).getBaseLevels(branch);
        verify(dataProvider.getProvider(), times(3)).getBeamlineSets(Optional.of(branchBaseLevel), branch);
    }

    /**
     * Test notifications when a new snapshot is saved.
     *
     * @throws DataProviderException
     */
    @Test
    public void testSnapshotSaved() throws DataProviderException {
        SaveRestoreService.getInstance().setSelectedDataProvider(dataProvider);
        selector.selectedBaseLevelProperty().set(branchBaseLevel);
        selector.selectedBeamlineSetProperty().set(branchBeamlineSet);
        List<Snapshot> snapshots = selector.snapshotsProperty().get();
        assertEquals("Exactly 2 snapshots are available", 2, snapshots.size());
        dataProvider.getProvider().saveSnapshot(snapshot, "comment");
        snapshots = selector.snapshotsProperty().get();
        assertEquals("Exactly 3 snapshots are available", 3, snapshots.size());
        assertEquals(branchSnapshot3, snapshots.get(0));
        assertEquals(branchSnapshot, snapshots.get(1));
        assertEquals(branchSnapshot2, snapshots.get(2));

        //nothing should change because the saved snapshot is in a different beamline set
        dataProvider.getProvider().saveSnapshot(xSnapshot, "comment");
        snapshots = selector.snapshotsProperty().get();
        assertEquals("Exactly 3 snapshots are available", 3, snapshots.size());
        assertEquals(branchSnapshot3, snapshots.get(0));
        assertEquals(branchSnapshot, snapshots.get(1));
        assertEquals(branchSnapshot2, snapshots.get(2));

    }

    /**
     * Test notifications when a snapshot is tagged.
     *
     * @throws DataProviderException
     */
    @Test
    public void testSnapshotTagged() throws DataProviderException {
        SaveRestoreService.getInstance().setSelectedDataProvider(dataProvider);
        selector.selectedBaseLevelProperty().set(branchBaseLevel);
        selector.selectedBeamlineSetProperty().set(branchBeamlineSet);
        List<Snapshot> snapshots = selector.snapshotsProperty().get();
        assertEquals("Exactly 2 snapshots are available", 2, snapshots.size());
        assertFalse("No tag name", snapshots.get(0).getTagName().isPresent());
        assertFalse("No tag message", snapshots.get(0).getTagMessage().isPresent());
        dataProvider.getProvider().tagSnapshot(branchSnapshot, Optional.of("name"), Optional.of("message"));
        snapshots = selector.snapshotsProperty().get();
        assertEquals("Exactly 2 snapshots are available", 2, snapshots.size());
        assertEquals("name", snapshots.get(0).getTagName().get());
        assertEquals("message", snapshots.get(0).getTagMessage().get());
    }
}
