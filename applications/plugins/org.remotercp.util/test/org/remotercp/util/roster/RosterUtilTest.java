package org.remotercp.util.roster;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterGroup;
import org.eclipse.ecf.presence.roster.Roster;
import org.eclipse.ecf.presence.roster.RosterGroup;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class RosterUtilTest extends AbstractRosterGenerator {

    private IRoster _roster;

    private IRosterGroup _group1;

    private IRosterGroup _group2;

    private List<IRosterEntry> _rosterEntries;

    @Test
    public void testUserPresenceInContextMenu() {

        final List<IRosterEntry> rosterEntries = RosterUtil.getRosterEntries(_roster);
        assertEquals(7, rosterEntries.size());

        final List<IRosterEntry> group1Entries = RosterUtil.getRosterEntries(_group1);
        assertEquals(2, group1Entries.size());

        final List<IRosterEntry> group2Entries = RosterUtil.getRosterEntries(_group2);
        assertEquals(5, group2Entries.size());
    }

    @Test
    public void testGetUserIDs() {
        final ID[] userIDs = RosterUtil.getUserIDs(_rosterEntries);

        assertEquals(7, userIDs.length);
        for (final ID userID : userIDs) {
            assertNotNull(userID);
        }
    }

    @SuppressWarnings("unchecked")
    @Before
    public void initRoster() {

        _roster = new Roster(null);

        _group1 = new RosterGroup(_roster, "group1");
        _group2 = new RosterGroup(_roster, "group2");

        _roster.getItems().add(_group1);
        _roster.getItems().add(_group2);

        final IRosterEntry rosterEntry1 = super.createRosterEntry("Klaus", _group1,
                                                                  IPresence.Type.AVAILABLE);
        final IRosterEntry rosterEntry2 = super.createRosterEntry("Susi", _group1,
                                                                  IPresence.Type.UNAVAILABLE);

        final IRosterEntry rosterEntry3 = super.createRosterEntry("Peter", _group2,
                                                                  IPresence.Type.UNAVAILABLE);
        final IRosterEntry rosterEntry4 = super.createRosterEntry("Sandra", _group2,
                                                                  IPresence.Type.UNAVAILABLE);
        final IRosterEntry rosterEntry5 = super.createRosterEntry("Jack", _group2,
                                                                  IPresence.Type.UNAVAILABLE);
        final IRosterEntry rosterEntry6 = super.createRosterEntry("Mary", _group2,
                                                                  IPresence.Type.UNAVAILABLE);

        final IRosterEntry rosterEntry7 = super.createRosterEntry("Susan", _group2,
                                                                  IPresence.Type.AVAILABLE);

        this._rosterEntries = new ArrayList<IRosterEntry>();
        _rosterEntries.add(rosterEntry1);
        _rosterEntries.add(rosterEntry2);
        _rosterEntries.add(rosterEntry3);
        _rosterEntries.add(rosterEntry4);
        _rosterEntries.add(rosterEntry5);
        _rosterEntries.add(rosterEntry6);
        _rosterEntries.add(rosterEntry7);

        assertNotNull(_roster.getItems());
        // roster must contain 2 groups
        assertEquals(2, _roster.getItems().size());

        final Collection items = _roster.getItems();
        for (final Object rosterItem : items) {
            final IRosterGroup group = (IRosterGroup) rosterItem;
            assertNotNull(group.getEntries());
            assertFalse(group.getEntries().isEmpty());
        }
    }

    @Test
    public void testHasRosterItem() {
        final RosterGroup rosterGroup1 = new RosterGroup(null, "group1");
        final RosterGroup rosterGroup2 = new RosterGroup(null, "groupNew");

        assertEquals(true, RosterUtil.hasRosterItem(_roster, rosterGroup1));
        assertEquals(false, RosterUtil.hasRosterItem(_roster, rosterGroup2));

        final IRosterEntry rosterEntry1 = super.createRosterEntry("Mary",
                                                                  rosterGroup1, IPresence.Type.AVAILABLE);

        final IRosterEntry rosterEntry2 = super.createRosterEntry("NewPerson",
                                                                  rosterGroup1, IPresence.Type.AVAILABLE);

        assertEquals(true, RosterUtil.hasRosterItem(_roster, rosterEntry1));
        assertEquals(false, RosterUtil.hasRosterItem(_roster, rosterEntry2));
    }

    @Test
    public void testGetRosterEntriesWithResources() {
        final IRosterEntry entry1 = Mockito.mock(IRosterEntry.class);
        final IRosterEntry entry2 = Mockito.mock(IRosterEntry.class);
        final IRosterEntry entry3 = Mockito.mock(IRosterEntry.class);

        final IUser user1 = Mockito.mock(IUser.class);
        final IUser user2 = Mockito.mock(IUser.class);
        final IUser user3 = Mockito.mock(IUser.class);

        final ID id1 = Mockito.mock(ID.class);
        final ID id2 = Mockito.mock(ID.class);
        final ID id3 = Mockito.mock(ID.class);

        Mockito.when(entry1.getName()).thenReturn("dima");
        Mockito.when(entry2.getName()).thenReturn("dima");
        Mockito.when(entry3.getName()).thenReturn("sandra");

        Mockito.when(entry1.getUser()).thenReturn(user1);
        Mockito.when(entry2.getUser()).thenReturn(user2);
        Mockito.when(entry3.getUser()).thenReturn(user3);

        Mockito.when(user1.getID()).thenReturn(id1);
        Mockito.when(user2.getID()).thenReturn(id2);
        Mockito.when(user3.getID()).thenReturn(id3);

        final Collection<IRosterEntry> rosterEntries = new ArrayList<IRosterEntry>();
        rosterEntries.add(entry1);
        rosterEntries.add(entry2);
        rosterEntries.add(entry3);

        final IRoster roster = Mockito.mock(IRoster.class);
        Mockito.when(roster.getItems()).thenReturn(rosterEntries);

        final List<IRosterEntry> rosterEntriesWithResources1 = RosterUtil
        .getRosterEntriesWithResources(roster, entry1);
        assertEquals(2, rosterEntriesWithResources1.size());

        final List<IRosterEntry> rosterEntriesWithResources2 = RosterUtil
        .getRosterEntriesWithResources(roster, entry1);
        assertEquals(2, rosterEntriesWithResources2.size());

        final List<IRosterEntry> rosterEntriesWithResources3 = RosterUtil
        .getRosterEntriesWithResources(roster, entry3);
        assertEquals(1, rosterEntriesWithResources3.size());
    }
}
