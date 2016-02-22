package org.csstudio.saverestore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import org.csstudio.saverestore.data.BaseLevel;
import org.csstudio.saverestore.data.SaveSet;
import org.csstudio.saverestore.data.Branch;
import org.csstudio.saverestore.data.Snapshot;
import org.csstudio.saverestore.data.Threshold;
import org.csstudio.saverestore.data.VDisconnectedData;
import org.csstudio.saverestore.data.VSnapshot;
import org.diirt.util.time.Timestamp;
import org.junit.Test;

/**
 *
 * <code>EntitiesTest</code> tests creation and some of the methods of the entities defined in the package
 * org.csstudio.saverestore.data, such as {@link Branch}, {@link BaseLevel} etc.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class EntitiesTest {

    /**
     * Tests creation of {@link BaseLevel}.
     */
    @Test
    public void testBaseLevel() {
        BaseLevel bl1 = new BaseLevel(null, "storage", "presentationName");
        BaseLevel bl2 = new BaseLevel(null, "storage", "presentationName2");
        assertEquals(bl1, bl2);

        bl1 = new BaseLevel(new Branch(), "storage", "presentationName");
        bl2 = new BaseLevel(new Branch(), "storage", "presentationName2");
        assertEquals(bl1, bl2);

        bl1 = new BaseLevel(null, "storage", "presentationName");
        bl2 = new BaseLevel(null, "storage", "presentationName");
        assertEquals(bl1, bl2);

        bl1 = new BaseLevel(null, "storage", "presentationName");
        bl2 = new BaseLevel(null, "storage1", "presentationName");
        assertNotEquals(bl1, bl2);

        BaseLevel bl = new BaseLevel(bl1);
        assertEquals(bl, bl1);

        for (int i = 0; i < 32; i++) {
            Exception ex = null;
            try {
                new BaseLevel(new Branch(), " " + (char) i + "A", "presentation");
                fail("Character " + (char) i + " not allowed");
            } catch (IllegalArgumentException e) {
                ex = e;
            }
            assertNotNull(ex);
        }

        for (int i = 32; i < 128; i++) {
            try {
                new BaseLevel(new Branch(), " " + (char) i + "A", "presentation");
            } catch (IllegalArgumentException e) {
                fail("Character " + (char) i + " allowed");
            }
        }

        for (int i = 128; i < 255; i++) {
            Exception ex = null;
            try {
                new BaseLevel(new Branch(), " " + (char) i + "A", "presentation");
                fail("Character " + (char) i + " not allowed");
            } catch (IllegalArgumentException e) {
                ex = e;
            }
            assertNotNull(ex);
        }
    }

    /**
     * Tests creation of {@link Branch}.
     */
    @Test
    public void testBranch() {
        Branch branch = new Branch("foo", "foo/bar");
        assertFalse(branch.isDefault());
        Branch branch2 = new Branch();
        assertTrue(branch2.isDefault());
        assertNotEquals(branch, branch2);
        assertEquals("master", branch2.getFullName());
    }

    /**
     * Tests creation of {@link SaveSet}.
     */
    @Test
    public void testSaveSet() {
        String[] path = new String[] { "first", "second", "third" };
        SaveSet set = new SaveSet(new Branch(), Optional.empty(), path, "someId");
        assertEquals("first/second/third", set.getPathAsString());
    }

    /**
     * Tests creation of {@link Snapshot}.
     */
    @Test
    public void testSnapshot() {
        Branch branch = new Branch();
        SaveSet set = new SaveSet(branch, Optional.empty(), new String[] { "first", "second", "third" },
            "someId");
        Snapshot snapshot = new Snapshot(set, new Date(), "comment", "owner", "tagName", "tagMessage");
        assertEquals("tagName", snapshot.getTagName().get());
        assertEquals("tagMessage", snapshot.getTagMessage().get());
    }

    /**
     * Tests creation of {@link VSnapshot}.
     */
    @Test
    public void testVSnapshot() {
        Branch branch = new Branch();
        SaveSet set = new SaveSet(branch, Optional.empty(), new String[] { "first", "second", "third" },
            "someId");
        Snapshot snapshot = new Snapshot(set, new Date(), "comment", "owner", "tagName", "tagMessage");
        VSnapshot vs = new VSnapshot(snapshot, Arrays.asList("name"), Arrays.asList(VDisconnectedData.INSTANCE), Timestamp.now(),
            null);

        assertTrue(vs.isSaved());
        assertFalse(vs.isSaveable());
        vs.addOrSetPV("another", true, VDisconnectedData.INSTANCE);
        assertFalse(vs.isSaved());
        assertTrue(vs.isSaveable());

        vs = new VSnapshot(set, Arrays.asList("name"), Arrays.asList("readback"), Arrays.asList("delta"));
        assertFalse(vs.isSaved());
        assertFalse(vs.isSaveable());
        vs.addOrSetPV("tralala", true, VDisconnectedData.INSTANCE);
        assertFalse(vs.isSaved());
        assertFalse(vs.isSaveable());

        snapshot = new Snapshot(set);
        vs = new VSnapshot(snapshot, Arrays.asList("name"), Arrays.asList(VDisconnectedData.INSTANCE), Timestamp.now(), null);
        assertFalse(vs.isSaved());
        assertTrue(vs.isSaveable());

        try {
            new VSnapshot(snapshot, Arrays.asList("name1", "name2"), Arrays.asList(VDisconnectedData.INSTANCE), Timestamp.now(),
                null);
            fail("Should fail because the length of names and values do not match.");
        } catch (IllegalArgumentException e) {
            assertNotNull("Exception was thrown", e.getMessage());
        }

        try {
            new VSnapshot(snapshot, Arrays.asList("name1", "name2"), Arrays.asList(true, true),
                Arrays.asList(VDisconnectedData.INSTANCE, VDisconnectedData.INSTANCE), Arrays.asList("readback"),
                Arrays.asList(VDisconnectedData.INSTANCE), Arrays.asList("delta"), Timestamp.now());
            fail("Should fail because the length of readbacks is different from the names.");
        } catch (IllegalArgumentException e) {
            assertNotNull("Exception was thrown", e.getMessage());
        }

        try {
            new VSnapshot(snapshot, Arrays.asList("name1", "name2"), Arrays.asList(true, true),
                Arrays.asList(VDisconnectedData.INSTANCE, VDisconnectedData.INSTANCE), Arrays.asList("readback", "readback2"),
                Arrays.asList(VDisconnectedData.INSTANCE, VDisconnectedData.INSTANCE), Arrays.asList("delta"), Timestamp.now());
            fail("Should fail because the length of deltas is different from the readbacks.");
        } catch (IllegalArgumentException e) {
            assertNotNull("Exception was thrown", e.getMessage());
        }
    }

    /**
     * Tests the {@link Threshold}.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testThreshold() {
        Threshold<Long> tr = new Threshold<>(50L, -50L);
        assertTrue(tr.isWithinThreshold(20L, 30L));
        assertFalse(tr.isWithinThreshold(20L, 80L));

        Threshold tr2 = new Threshold<>("x * 2");
        assertTrue(tr2.isWithinThreshold(5, 3));
        assertFalse(tr2.isWithinThreshold(10, 3));

        tr2 = new Threshold<>("base < 10 && value < 5 || base > 10 && value > 15");
        assertTrue(tr2.isWithinThreshold(3, 8));
        assertTrue(tr2.isWithinThreshold(16, 12));
        assertFalse(tr2.isWithinThreshold(8, 8));
        assertFalse(tr2.isWithinThreshold(12, 12));

        tr2 = new Threshold<>("x * 2");
        assertTrue(tr2.test());
        tr2 = new Threshold<>("x + 2");
        assertTrue(tr2.test());
        tr2 = new Threshold<>("x - 2");
        assertTrue(tr2.test());
        tr2 = new Threshold<>("base - 2");
        assertTrue(tr2.test());
        tr2 = new Threshold<>("Math.pow(x,2)");
        assertTrue(tr2.test());
        tr2 = new Threshold<>("Math.exp(x)");
        assertTrue(tr2.test());
        tr2 = new Threshold<>("Math.sin(x)");
        assertTrue(tr2.test());
        tr2 = new Threshold<>("Math.asin(x)");
        assertTrue(tr2.test());
        tr2 = new Threshold<>("Math.sqrt(x)");
        assertTrue(tr2.test());
        tr2 = new Threshold<>("Math.sqrt(x) > 0");
        assertFalse(tr2.test());
        tr2 = new Threshold<>("Math.sqrt(y)");
        assertFalse(tr2.test());
        tr2 = new Threshold<>("Math.sqrt(y)");
        assertFalse(tr2.test());
        tr2 = new Threshold<>("foobar");
        assertFalse(tr2.test());
    }
}
