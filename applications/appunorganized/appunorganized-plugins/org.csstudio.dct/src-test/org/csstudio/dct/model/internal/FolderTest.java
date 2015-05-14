/**
 *
 */
package org.csstudio.dct.model.internal;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.reset;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IFolderMember;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link Folder}.
 *
 * @author Sven Wende
 *
 */
public final class FolderTest {
    private Folder folder;
    private IFolderMember member1;
    private IFolderMember member2;
    private IFolder parent;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        folder = new Folder("test");
        member1 = createMock(IFolderMember.class);
        member2 = createMock(IFolderMember.class);
        expect(member1.getParentFolder()).andReturn(null).anyTimes();
        expect(member2.getParentFolder()).andReturn(null).anyTimes();
        replay(member1, member2);
        folder.addMember(member1);
        folder.addMember(member2);
        reset(member1, member2);
        expect(member1.getParentFolder()).andReturn(folder).anyTimes();
        expect(member2.getParentFolder()).andReturn(folder).anyTimes();
        replay(member1, member2);
        parent = createMock(IFolder.class);
        folder.setParentFolder(parent);
    }

    /**
     * Test method for {@link org.csstudio.dct.model.internal.Folder#getMembers()}.
     */
    @Test
    public void testGetMembers() {
        assertEquals(2, folder.getMembers().size());
        assertTrue(folder.getMembers().contains(member1));
        assertTrue(folder.getMembers().contains(member2));
    }

    /**
     * Test method for
     * {@link org.csstudio.dct.model.internal.Folder#addMember(org.csstudio.dct.model.IFolderMember)}.
     */
    @Test
    public void testAddMemberIFolderMember() {
        IFolderMember member3 = createMock(IFolderMember.class);

        assertFalse(folder.getMembers().contains(member3));
        folder.addMember(member3);
        assertTrue(folder.getMembers().contains(member3));
        assertEquals(member3, folder.getMembers().get(2));
    }

    /**
     * Test method for
     * {@link org.csstudio.dct.model.internal.Folder#addMember(int, org.csstudio.dct.model.IFolderMember)}.
     */
    @Test
    public void testAddMemberIntIFolderMember() {
        IFolderMember member3 = createMock(IFolderMember.class);

        assertFalse(folder.getMembers().contains(member3));
        folder.addMember(1, member3);
        assertTrue(folder.getMembers().contains(member3));
        assertSame(member3, folder.getMembers().get(1));
    }

    /**
     * Test method for
     * {@link org.csstudio.dct.model.internal.Folder#removeMember(org.csstudio.dct.model.IFolderMember)}.
     */
    @Test
    public void testRemoveMemberIFolderMember() {
        folder.removeMember(member1);
        folder.removeMember(member2);
        assertTrue(folder.getMembers().isEmpty());
    }

    /**
     * Test method for
     * {@link org.csstudio.dct.model.internal.Folder#removeMember(int)}.
     */
    @Test
    public void testRemoveMemberInt() {
        folder.removeMember(1);
        assertTrue(folder.getMembers().contains(member1));
        assertFalse(folder.getMembers().contains(member2));
    }

    /**
     * Test method for
     * {@link org.csstudio.dct.model.internal.Folder#getParentFolder()}.
     */
    @Test
    public void testGetParentFolder() {
        assertEquals(parent, folder.getParentFolder());
    }

    /**
     * Test method for
     * {@link org.csstudio.dct.model.internal.Folder#setParentFolder(org.csstudio.dct.model.IFolder)}.
     */
    @Test
    public void testSetParentFolder() {
        assertEquals(parent, folder.getParentFolder());
        folder.setParentFolder(null);
        assertNull(folder.getParentFolder());
    }

}
