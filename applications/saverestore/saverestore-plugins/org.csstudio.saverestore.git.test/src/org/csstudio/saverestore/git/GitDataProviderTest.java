/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.saverestore.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.csstudio.saverestore.CompletionNotifier;
import org.csstudio.saverestore.DataProvider.ImportType;
import org.csstudio.saverestore.DataProviderException;
import org.csstudio.saverestore.SearchCriterion;
import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.SaveSet;
import org.csstudio.saverestore.data.SaveSetData;
import org.csstudio.saverestore.data.SaveSetEntry;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.SnapshotEntry;
import org.csstudio.saverestore.data.VDisconnectedData;
import org.csstudio.saverestore.data.VSnapshot;
import org.csstudio.saverestore.git.Result.ChangeType;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 * <code>GitDataProviderTest</code> tests the methods of the {@link GitDataProvider}. This is mostly a test if the data
 * provider properly notifies the registered {@link CompletionNotifier}s.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class GitDataProviderTest {

    private GitDataProvider dataProvider;
    private CompletionNotifier notifier;

    private Instant date = Instant.now();
    private Branch branch = new Branch();
    private Branch someBranch = new Branch("someBranch", "someBranch");
    private Branch demoBranch = new Branch("demo", "demo");
    private BaseLevel branchBase1 = new BaseLevel(new Branch(), "base1", "base1");
    private BaseLevel branchBase2 = new BaseLevel(new Branch(), "base2", "base2");
    private BaseLevel someBaseLevel = new BaseLevel(someBranch, "sbase2", "sbase2");
    private SaveSet branchSaveSet = new SaveSet(branch, Optional.of(branchBase1),
        new String[] { "first", "second.bms" }, "someId");
    private SaveSet branchSaveSet2 = new SaveSet(branch, Optional.of(branchBase1),
        new String[] { "first", "foo", "bar", "second.bms" }, "someId");
    private Snapshot branchSnapshot = new Snapshot(branchSaveSet, date, "comment", "owner");
    private Snapshot branchSnapshot2 = new Snapshot(branchSaveSet, date.minusMillis(5000), "another comment", "user");
    private Snapshot branchSnapshot3 = new Snapshot(branchSaveSet, date.plusMillis(5000), "new snapshot", "user");
    private SaveSetData bsd = new SaveSetData(branchSaveSet,
        Arrays.asList(new SaveSetEntry("pv1", "rb1", "d1", false), new SaveSetEntry("pv2", "rb2", "d2", false)),
        "description");
    private VSnapshot snapshot = new VSnapshot(branchSnapshot3,
        Arrays.asList(new SnapshotEntry("pv1", VDisconnectedData.INSTANCE)), Instant.now(), null);

    @Before
    public void setUp() throws Exception {
        GitAPIException exception = new GitAPIException("Problem") {
            private static final long serialVersionUID = 1L;
        };

        GitManager grm = mock(GitManager.class);
        notifier = mock(CompletionNotifier.class);
        dataProvider = new GitDataProvider(grm);
        dataProvider.addCompletionNotifier(notifier);
        Field field = GitDataProvider.class.getDeclaredField("initialized");
        field.setAccessible(true);
        field.set(dataProvider, Boolean.TRUE);

        when(grm.getBranches()).thenReturn(Arrays.asList(branch, demoBranch));
        when(grm.getBaseLevels(branch)).thenReturn(Arrays.asList(branchBase1, branchBase2));
        when(grm.getBaseLevels(demoBranch)).thenReturn(new ArrayList<>(0));
        when(grm.getSaveSets(Optional.of(branchBase1), branch))
            .thenReturn(Arrays.asList(branchSaveSet, branchSaveSet2));
        when(grm.getSnapshots(branchSaveSet, 0, Optional.empty()))
            .thenReturn(Arrays.asList(branchSnapshot, branchSnapshot2));
        when(grm.loadSaveSetData(branchSaveSet, Optional.empty())).thenReturn(bsd);
        when(grm.createBranch(branch, "someBranch")).thenReturn(someBranch);
        when(grm.createBranch(branch, "bla")).thenThrow(exception);
        when(grm.saveSaveSet(bsd, "comment")).thenReturn(new Result<SaveSetData>(bsd, ChangeType.SAVE));
        when(grm.saveSaveSet(bsd, "comment2")).thenReturn(new Result<SaveSetData>(bsd, ChangeType.PULL));
        when(grm.saveSaveSet(bsd, "comment3")).thenThrow(exception);
        when(grm.deleteSaveSet(branchSaveSet, "comment"))
            .thenReturn(new Result<SaveSet>(branchSaveSet, ChangeType.SAVE));
        when(grm.deleteSaveSet(branchSaveSet, "comment2"))
            .thenReturn(new Result<SaveSet>(branchSaveSet, ChangeType.PULL));
        when(grm.deleteSaveSet(branchSaveSet, "comment4")).thenReturn(new Result<SaveSet>(null, ChangeType.NONE));
        when(grm.deleteSaveSet(branchSaveSet, "comment3")).thenThrow(exception);
        when(grm.saveSnapshot(snapshot, "comment")).thenReturn(new Result<VSnapshot>(snapshot, ChangeType.SAVE));
        when(grm.saveSnapshot(snapshot, "comment2")).thenReturn(new Result<VSnapshot>(snapshot, ChangeType.PULL));
        when(grm.saveSnapshot(snapshot, "comment3")).thenReturn(new Result<VSnapshot>(null, ChangeType.NONE));
        when(grm.saveSnapshot(snapshot, "comment4")).thenThrow(exception);
        when(grm.tagSnapshot(branchSnapshot, "name", "message"))
            .thenReturn(new Result<Snapshot>(branchSnapshot, ChangeType.SAVE));
        when(grm.tagSnapshot(branchSnapshot, "name2", "message"))
            .thenReturn(new Result<Snapshot>(branchSnapshot, ChangeType.PULL));
        when(grm.tagSnapshot(branchSnapshot, "name3", "message"))
            .thenReturn(new Result<Snapshot>(null, ChangeType.NONE));
        when(grm.tagSnapshot(branchSnapshot, "name4", "message")).thenThrow(exception);
        when(grm.loadSnapshotData(branchSnapshot)).thenReturn(snapshot);
        when(grm.findSnapshotsByCommentOrUser("temp", branch, true, true, Optional.empty(), Optional.empty()))
            .thenReturn(Arrays.asList(branchSnapshot, branchSnapshot3));
        when(grm.findSnapshotsByCommentOrUser("temp", branch, true, false, Optional.empty(), Optional.empty()))
            .thenReturn(Arrays.asList(branchSnapshot3));
        when(grm.findSnapshotsByCommentOrUser("temp", branch, false, true, Optional.empty(), Optional.empty()))
            .thenReturn(Arrays.asList(branchSnapshot2));
        when(grm.findSnapshotsByTag("temp", branch, Optional.empty(), Optional.empty()))
            .thenReturn(Arrays.asList(branchSnapshot));
        when(grm.findSnapshotsByTagMessage("temp", branch, Optional.empty(), Optional.empty()))
            .thenReturn(Arrays.asList(branchSnapshot2));
        when(grm.findSnapshotsByTagName("temp", branch, Optional.empty(), Optional.empty()))
            .thenReturn(Arrays.asList(branchSnapshot3));
        when(grm.importData(branchSaveSet, someBranch, Optional.of(someBaseLevel), ImportType.SAVE_SET))
            .thenReturn(new Result<Boolean>(true, ChangeType.SAVE));
        when(grm.importData(branchSaveSet, someBranch, Optional.of(someBaseLevel), ImportType.ALL_SNAPSHOTS))
            .thenReturn(new Result<Boolean>(true, ChangeType.PULL));
        when(grm.importData(branchSaveSet, someBranch, Optional.of(someBaseLevel), ImportType.LAST_SNAPSHOT))
            .thenReturn(new Result<Boolean>(false, ChangeType.NONE));
        when(grm.synchronise(Optional.empty())).thenReturn(true);
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
            dataProvider.createNewBranch(branch, "bla");
            fail("Exception should happen");
        } catch (DataProviderException e) {
            assertNotNull(e.getMessage());
        }
        verify(notifier, times(1)).branchCreated(newBranch);
        verify(notifier, only()).branchCreated(newBranch);

    }

    @Test
    public void testSaveSaveSet() throws DataProviderException {
        SaveSetData data = dataProvider.saveSaveSet(bsd, "comment");
        assertEquals(bsd, data);
        verify(notifier, times(1)).saveSetSaved(data);
        verify(notifier, only()).saveSetSaved(data);
        Mockito.reset(notifier);

        data = dataProvider.saveSaveSet(bsd, "comment2");
        assertEquals(bsd, data);
        verify(notifier, times(1)).synchronised();
        verify(notifier, only()).synchronised();

        try {
            dataProvider.saveSaveSet(bsd, "comment3");
            fail("Exception should happen");
        } catch (DataProviderException e) {
            assertNotNull(e.getMessage());
        }
        verify(notifier, times(1)).synchronised();
        verify(notifier, only()).synchronised();
    }

    @Test
    public void testDeleteSaveSet() throws DataProviderException {
        boolean b = dataProvider.deleteSaveSet(branchSaveSet, "comment");
        assertTrue(b);
        verify(notifier, times(1)).saveSetDeleted(branchSaveSet);
        verify(notifier, only()).saveSetDeleted(branchSaveSet);
        Mockito.reset(notifier);

        b = dataProvider.deleteSaveSet(branchSaveSet, "comment4");
        assertFalse(b);
        verify(notifier, never()).synchronised();
        Mockito.reset(notifier);

        b = dataProvider.deleteSaveSet(branchSaveSet, "comment2");
        assertTrue(b);
        verify(notifier, times(1)).synchronised();
        verify(notifier, only()).synchronised();

        try {
            dataProvider.deleteSaveSet(branchSaveSet, "comment3");
            fail("Exception should happen");
        } catch (DataProviderException e) {
            assertNotNull(e.getMessage());
        }
        verify(notifier, times(1)).synchronised();
        verify(notifier, only()).synchronised();
    }

    @Test
    public void testSaveSnapshot() throws DataProviderException {
        VSnapshot snap = dataProvider.saveSnapshot(snapshot, "comment");
        assertTrue(snap.equalsExceptSnapshot(snapshot));
        verify(notifier, times(1)).snapshotSaved(snap);
        verify(notifier, only()).snapshotSaved(snap);
        Mockito.reset(notifier);

        snap = dataProvider.saveSnapshot(snapshot, "comment2");
        assertTrue(snap.equalsExceptSnapshot(snapshot));
        verify(notifier, times(1)).synchronised();
        verify(notifier, only()).synchronised();

        try {
            dataProvider.saveSnapshot(snapshot, "comment4");
            fail("Exception should happen");
        } catch (DataProviderException e) {
            assertNotNull(e.getMessage());
        }
        verify(notifier, times(1)).synchronised();
        verify(notifier, only()).synchronised();

        Mockito.reset(notifier);

        snap = dataProvider.saveSnapshot(snapshot, "comment3");
        assertNull(snap);
        verify(notifier, never()).synchronised();
    }

    @Test
    public void testTagSnapshot() throws DataProviderException {
        Snapshot snap = dataProvider.tagSnapshot(branchSnapshot, Optional.of("name"), Optional.of("message"));
        assertEquals(branchSnapshot, snap);
        verify(notifier, times(1)).snapshotTagged(snap);
        verify(notifier, only()).snapshotTagged(snap);
        Mockito.reset(notifier);

        snap = dataProvider.tagSnapshot(branchSnapshot, Optional.of("name2"), Optional.of("message"));
        assertEquals(branchSnapshot, snap);
        verify(notifier, times(1)).synchronised();
        verify(notifier, only()).synchronised();

        try {
            dataProvider.tagSnapshot(branchSnapshot, Optional.of("name4"), Optional.of("message"));
            fail("Exception should happen");
        } catch (DataProviderException e) {
            assertNotNull(e.getMessage());
        }
        verify(notifier, times(1)).synchronised();
        verify(notifier, only()).synchronised();

        Mockito.reset(notifier);
        snap = dataProvider.tagSnapshot(branchSnapshot, Optional.of("name3"), Optional.of("message"));
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
            Arrays.asList(criteria.get(0), criteria.get(3)), Optional.empty(), Optional.empty());
        assertEquals(2, snapshots.length);
        assertEquals(branchSnapshot, snapshots[0]);
        assertEquals(branchSnapshot3, snapshots[1]);

        snapshots = dataProvider.findSnapshots("temp", branch, Arrays.asList(criteria.get(3)), Optional.empty(),
            Optional.empty());
        assertEquals(1, snapshots.length);
        assertEquals(branchSnapshot2, snapshots[0]);

        snapshots = dataProvider.findSnapshots("temp", branch, Arrays.asList(criteria.get(0)), Optional.empty(),
            Optional.empty());
        assertEquals(1, snapshots.length);
        assertEquals(branchSnapshot3, snapshots[0]);

        snapshots = dataProvider.findSnapshots("temp", branch,
            Arrays.asList(criteria.get(0), criteria.get(1), criteria.get(2)), Optional.empty(), Optional.empty());
        assertEquals(2, snapshots.length);
        assertEquals(branchSnapshot3, snapshots[0]);
        assertEquals(branchSnapshot, snapshots[1]);

        snapshots = dataProvider.findSnapshots("temp", branch, Arrays.asList(criteria.get(3), criteria.get(1)),
            Optional.empty(), Optional.empty());
        assertEquals(2, snapshots.length);
        assertEquals(branchSnapshot3, snapshots[0]);
        assertEquals(branchSnapshot2, snapshots[1]);

        snapshots = dataProvider.findSnapshots("temp", branch, Arrays.asList(criteria.get(2)), Optional.empty(),
            Optional.empty());
        assertEquals(1, snapshots.length);
        assertEquals(branchSnapshot2, snapshots[0]);
    }

    @Test
    public void testImportData() throws DataProviderException {
        boolean b = dataProvider.importData(branchSaveSet, someBranch, Optional.of(someBaseLevel), ImportType.SAVE_SET);
        assertTrue(b);
        verify(notifier, times(1)).dataImported(branchSaveSet, someBranch, Optional.of(someBaseLevel));
        verify(notifier, only()).dataImported(branchSaveSet, someBranch, Optional.of(someBaseLevel));
        Mockito.reset(notifier);

        b = dataProvider.importData(branchSaveSet, someBranch, Optional.of(someBaseLevel), ImportType.LAST_SNAPSHOT);
        assertFalse(b);
        verify(notifier, never()).synchronised();
        Mockito.reset(notifier);

        b = dataProvider.importData(branchSaveSet, someBranch, Optional.of(someBaseLevel), ImportType.ALL_SNAPSHOTS);
        assertTrue(b);
        verify(notifier, times(1)).synchronised();
        verify(notifier, only()).synchronised();
    }

    @Test
    public void testSynchronise() throws DataProviderException {
        boolean b = dataProvider.synchronise();
        assertTrue(b);
        verify(notifier, times(1)).synchronised();
        verify(notifier, only()).synchronised();
    }
}
