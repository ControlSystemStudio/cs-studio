/**
 * 
 */
package org.csstudio.dct.model.internal;

import static org.junit.Assert.*;

import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.IFolderMember;
import org.csstudio.dct.model.internal.Folder;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.classextension.EasyMock.*;

/**
 * Test cases for {@link Folder}.
 * 
 * @author Sven Wende
 * 
 */
public class FolderTest {
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
		folder.addMember(member1);
		folder.addMember(member2);
		parent = createMock(IFolder.class);
		folder.setParentFolder(parent);
	}

	/**
	 * Test method for {@link org.csstudio.dct.model.internal.Folder#getMembers()}.
	 */
	@Test
	public final void testGetMembers() {
		assertEquals(2, folder.getMembers().size());
		assertTrue(folder.getMembers().contains(member1));
		assertTrue(folder.getMembers().contains(member2));
	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.model.internal.Folder#addMember(org.csstudio.dct.model.IFolderMember)}.
	 */
	@Test
	public final void testAddMemberIFolderMember() {
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
	public final void testAddMemberIntIFolderMember() {
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
	public final void testRemoveMemberIFolderMember() {
		folder.removeMember(member1);
		folder.removeMember(member2);
		assertTrue(folder.getMembers().isEmpty());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.model.internal.Folder#removeMember(int)}.
	 */
	@Test
	public final void testRemoveMemberInt() {
		folder.removeMember(1);
		assertTrue(folder.getMembers().contains(member1));
		assertFalse(folder.getMembers().contains(member2));
	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.model.internal.Folder#getParentFolder()}.
	 */
	@Test
	public final void testGetParentFolder() {
		assertEquals(parent, folder.getParentFolder());
	}

	/**
	 * Test method for
	 * {@link org.csstudio.dct.model.internal.Folder#setParentFolder(org.csstudio.dct.model.IFolder)}.
	 */
	@Test
	public final void testSetParentFolder() {
		assertEquals(parent, folder.getParentFolder());
		folder.setParentFolder(null);
		assertNull(folder.getParentFolder());
	}

}
