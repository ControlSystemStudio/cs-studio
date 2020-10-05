//$Id: $
/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 */
package org.hibernate.test.readonly;

import junit.framework.Test;

import org.hibernate.Session;
import org.hibernate.junit.functional.FunctionalTestCase;
import org.hibernate.junit.functional.FunctionalTestClassTestSuite;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

/**
 * @author Gail Badner
 */
public class ReadOnlyVersionedNodesTest extends AbstractReadOnlyTest {

	public ReadOnlyVersionedNodesTest(String str) {
		super( str );
	}

	public String[] getMappings() {
		return new String[] { "readonly/VersionedNode.hbm.xml" };
	}

	public static Test suite() {
		return new FunctionalTestClassTestSuite( ReadOnlyVersionedNodesTest.class );
	}

	public void testSetReadOnlyTrueAndFalse() throws Exception {
		Session s = openSession();
		s.beginTransaction();
		VersionedNode node = new VersionedNode( "node", "node" );
		s.persist( node );
		s.getTransaction().commit();
		s.close();

		clearCounts();

		s = openSession();

		s.beginTransaction();
		node = ( VersionedNode ) s.get( VersionedNode.class, node.getId() );
		s.setReadOnly( node, true );
		node.setName( "node-name" );
		s.getTransaction().commit();

		assertUpdateCount( 0 );
		assertInsertCount( 0 );

		// the changed name is still in node
		assertEquals( "node-name", node.getName() );

		s.beginTransaction();
		node = ( VersionedNode ) s.get( VersionedNode.class, node.getId() );
		// the changed name is still in the session
		assertEquals( "node-name", node.getName() );
		s.refresh( node );
		// after refresh, the name reverts to the original value
		assertEquals( "node", node.getName() );
		node = ( VersionedNode ) s.get( VersionedNode.class, node.getId() );
		assertEquals( "node", node.getName() );
		s.getTransaction().commit();

		s.close();

		assertUpdateCount( 0 );
		assertInsertCount( 0 );

		s = openSession();
		s.beginTransaction();
		node = ( VersionedNode ) s.get( VersionedNode.class, node.getId() );
		assertEquals( "node", node.getName() );
		s.setReadOnly( node, true );
		node.setName( "diff-node-name" );
		s.flush();
		assertEquals( "diff-node-name", node.getName() );
		s.refresh( node );
		assertEquals( "node", node.getName() );
		s.setReadOnly( node, false );
		node.setName( "diff-node-name" );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 1 );
		assertInsertCount( 0 );
		clearCounts();

		s = openSession();
		s.beginTransaction();
		node = ( VersionedNode ) s.get( VersionedNode.class, node.getId() );
		assertEquals( "diff-node-name", node.getName() );
		assertEquals( 1, node.getVersion() );
		s.setReadOnly( node, true );
		s.delete( node );
		s.getTransaction().commit();
		s.close();
		
		assertUpdateCount( 0 );
		assertDeleteCount( 1 );
	}

	public void testUpdateSetReadOnlyTwice() throws Exception {
		Session s = openSession();
		s.beginTransaction();
		VersionedNode node = new VersionedNode( "node", "node" );
		s.persist( node );
		s.getTransaction().commit();
		s.close();
 
		clearCounts();

		s = openSession();

		s.beginTransaction();
		node = ( VersionedNode ) s.get( VersionedNode.class, node.getId() );
		node.setName( "node-name" );
		s.setReadOnly( node, true );
		s.setReadOnly( node, true );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 0 );
		assertInsertCount( 0 );

		s = openSession();
		s.beginTransaction();
		node = ( VersionedNode ) s.get( VersionedNode.class, node.getId() );
		assertEquals( "node", node.getName() );
		assertEquals( 0, node.getVersion() );
		s.setReadOnly( node, true );
		s.delete( node );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 0 );
		assertDeleteCount( 1 );
	}

	public void testUpdateSetModifiable() throws Exception {
		Session s = openSession();
		s.beginTransaction();
		VersionedNode node = new VersionedNode( "node", "node" );
		s.persist( node );
		s.getTransaction().commit();
		s.close();
 
		clearCounts();

		s = openSession();

		s.beginTransaction();
		node = ( VersionedNode ) s.get( VersionedNode.class, node.getId() );
		node.setName( "node-name" );
		s.setReadOnly( node, false );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 1 );
		assertInsertCount( 0 );
		clearCounts();

		s = openSession();
		s.beginTransaction();
		node = ( VersionedNode ) s.get( VersionedNode.class, node.getId() );
		assertEquals( "node-name", node.getName() );
		assertEquals( 1, node.getVersion() );
		s.setReadOnly( node, true );
		s.delete( node );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 0 );
		assertDeleteCount( 1 );
	}

	public void testUpdateSetReadOnlySetModifiableFailureExpected() throws Exception {
		Session s = openSession();
		s.beginTransaction();
		VersionedNode node = new VersionedNode( "node", "node" );
		s.persist( node );
		s.getTransaction().commit();
		s.close();

		clearCounts();

		s = openSession();

		s.beginTransaction();
		node = ( VersionedNode ) s.get( VersionedNode.class, node.getId() );
		node.setName( "node-name" );
		s.setReadOnly( node, true );
		s.setReadOnly( node, false );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 1 );
		assertInsertCount( 0 );

		s = openSession();
		s.beginTransaction();
		node = ( VersionedNode ) s.get( VersionedNode.class, node.getId() );
		assertEquals( "node-name", node.getName() );
		assertEquals( 1, node.getVersion() );
		s.delete( node );
		s.getTransaction().commit();
		s.close();
	}

	public void testSetReadOnlyUpdateSetModifiableFailureExpected() throws Exception {
		Session s = openSession();
		s.beginTransaction();
		VersionedNode node = new VersionedNode( "node", "node" );
		s.persist( node );
		s.getTransaction().commit();
		s.close();

		clearCounts();

		s = openSession();

		s.beginTransaction();
		node = ( VersionedNode ) s.get( VersionedNode.class, node.getId() );
		s.setReadOnly( node, true );
		node.setName( "node-name" );
		s.setReadOnly( node, false );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 1 );
		assertInsertCount( 0 );

		s = openSession();
		s.beginTransaction();
		node = ( VersionedNode ) s.get( VersionedNode.class, node.getId() );
		assertEquals( "node-name", node.getName() );
		assertEquals( 1, node.getVersion() );
		s.delete( node );
		s.getTransaction().commit();
		s.close();
	}

	public void testAddNewChildToReadOnlyParent() throws Exception {
		Session s = openSession();
		s.beginTransaction();
		VersionedNode parent = new VersionedNode( "parent", "parent" );
		s.persist( parent );
		s.getTransaction().commit();
		s.close();

		clearCounts();

		s = openSession();
		s.beginTransaction();
		VersionedNode parentManaged = ( VersionedNode ) s.get( VersionedNode.class, parent.getId() );
		s.setReadOnly( parentManaged, true );
		parentManaged.setName( "new parent name" );
		VersionedNode child = new VersionedNode( "child", "child");
		parentManaged.addChild( child );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 1 );
		assertInsertCount( 1 );

		s = openSession();
		s.beginTransaction();
		parent = ( VersionedNode ) s.get( VersionedNode.class, parent.getId() );
		assertEquals( "parent", parent.getName() );
		assertEquals( 1, parent.getChildren().size() );
		assertEquals( 1, parent.getVersion() );
		child = ( VersionedNode ) s.get( VersionedNode.class, child.getId() );
		assertNotNull( child );
		s.delete( parent );
		s.getTransaction().commit();
		s.close();
	}

	public void testUpdateParentWithNewChildCommitWithReadOnlyParent() throws Exception {
		Session s = openSession();
		s.beginTransaction();
		VersionedNode parent = new VersionedNode( "parent", "parent" );
		s.persist( parent );
		s.getTransaction().commit();
		s.close();

		clearCounts();

		parent.setName( "new parent name" );
		VersionedNode child = new VersionedNode( "child", "child");
		parent.addChild( child );

		s = openSession();
		s.beginTransaction();
		s.update( parent );
		s.setReadOnly( parent, true );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 1 );
		assertInsertCount( 1 );
		clearCounts();

		s = openSession();
		s.beginTransaction();
		parent = ( VersionedNode ) s.get( VersionedNode.class, parent.getId() );
		child = ( VersionedNode ) s.get( VersionedNode.class, child.getId() );
		assertEquals( parent.getName(), "parent" );
		assertEquals( 1, parent.getChildren().size() );
		assertEquals( 1, parent.getVersion() );
		assertSame( parent, child.getParent() );
		assertSame( child, parent.getChildren().iterator().next() );
		assertEquals( 0, child.getVersion() );
		s.setReadOnly( parent, true );
		s.setReadOnly( child, true );
		s.delete( parent );
		s.delete( child );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 0 );
		assertDeleteCount( 2 );
	}

	public void testMergeDetachedParentWithNewChildCommitWithReadOnlyParent() throws Exception {
		Session s = openSession();
		s.beginTransaction();
		VersionedNode parent = new VersionedNode( "parent", "parent" );
		s.persist( parent );
		s.getTransaction().commit();
		s.close();

		clearCounts();

		parent.setName( "new parent name" );
		VersionedNode child = new VersionedNode( "child", "child");
		parent.addChild( child );

		s = openSession();
		s.beginTransaction();
		parent = ( VersionedNode ) s.merge( parent );
		s.setReadOnly( parent, true );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 1 );
		assertInsertCount( 1 );
		clearCounts();

		s = openSession();
		s.beginTransaction();
		parent = ( VersionedNode ) s.get( VersionedNode.class, parent.getId() );
		child = ( VersionedNode ) s.get( VersionedNode.class, child.getId() );
		assertEquals( parent.getName(), "parent" );
		assertEquals( 1, parent.getChildren().size() );
		assertEquals( 1, parent.getVersion() );
		assertSame( parent, child.getParent() );
		assertSame( child, parent.getChildren().iterator().next() );
		assertEquals( 0, child.getVersion() );
		s.setReadOnly( parent, true );
		s.setReadOnly( child, true );
		s.delete( parent );
		s.delete( child );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 0 );
		assertDeleteCount( 2 );
	}

	public void testGetParentMakeReadOnlyThenMergeDetachedParentWithNewChildC() throws Exception {
		Session s = openSession();
		s.beginTransaction();
		VersionedNode parent = new VersionedNode( "parent", "parent" );
		s.persist( parent );
		s.getTransaction().commit();
		s.close();

		clearCounts();

		parent.setName( "new parent name" );
		VersionedNode child = new VersionedNode( "child", "child");
		parent.addChild( child );

		s = openSession();
		s.beginTransaction();
		VersionedNode parentManaged = ( VersionedNode ) s.get( VersionedNode.class, parent.getId() );
		s.setReadOnly( parentManaged, true );
		VersionedNode parentMerged = ( VersionedNode ) s.merge( parent );
		assertSame( parentManaged, parentMerged );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 1 );
		assertInsertCount( 1 );
		clearCounts();

		s = openSession();
		s.beginTransaction();
		parent = ( VersionedNode ) s.get( VersionedNode.class, parent.getId() );
		child = ( VersionedNode ) s.get( VersionedNode.class, child.getId() );
		assertEquals( parent.getName(), "parent" );
		assertEquals( 1, parent.getChildren().size() );
		assertEquals( 1, parent.getVersion() );
		assertSame( parent, child.getParent() );
		assertSame( child, parent.getChildren().iterator().next() );
		assertEquals( 0, child.getVersion() );
		s.delete( parent );
		s.delete( child );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 0 );
		assertDeleteCount( 2 );
	}

	public void testMergeUnchangedDetachedParentChildren() throws Exception {
		Session s = openSession();
		s.beginTransaction();
		VersionedNode parent = new VersionedNode( "parent", "parent" );
		VersionedNode child = new VersionedNode( "child", "child");
		parent.addChild( child );
		s.persist( parent );
		s.getTransaction().commit();
		s.close();

		clearCounts();

		s = openSession();
		s.beginTransaction();
		parent = ( VersionedNode ) s.merge( parent );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 0 );
		assertInsertCount( 0 );
		clearCounts();

		s = openSession();
		s.beginTransaction();
		VersionedNode parentGet = ( VersionedNode ) s.get( parent.getClass(), parent.getId() );
		s.merge( parent );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 0 );
		assertInsertCount( 0 );
		clearCounts();

		s = openSession();
		s.beginTransaction();
		VersionedNode parentLoad = ( VersionedNode ) s.load( parent.getClass(), parent.getId() );
		s.merge( parent );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 0 );
		assertInsertCount( 0 );
		clearCounts();

		s = openSession();
		s.beginTransaction();
		parent = ( VersionedNode ) s.get( VersionedNode.class, parent.getId() );
		child = ( VersionedNode ) s.get( VersionedNode.class, child.getId() );
		assertEquals( parent.getName(), "parent" );
		assertEquals( 1, parent.getChildren().size() );
		assertEquals( 0, parent.getVersion() );
		assertSame( parent, child.getParent() );
		assertSame( child, parent.getChildren().iterator().next() );
		assertEquals( 0, child.getVersion() );
		s.delete( parent );
		s.delete( child );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 0 );
		assertDeleteCount( 2 );
	}

	public void testAddNewParentToReadOnlyChild() throws Exception {
		Session s = openSession();
		s.beginTransaction();
		VersionedNode child = new VersionedNode( "child", "child" );
		s.persist( child );
		s.getTransaction().commit();
		s.close();

		clearCounts();

		s = openSession();
		s.beginTransaction();
		VersionedNode childManaged = ( VersionedNode ) s.get( VersionedNode.class, child.getId() );
		s.setReadOnly( childManaged, true );
		childManaged.setName( "new child name" );
		VersionedNode parent = new VersionedNode( "parent", "parent");
		parent.addChild( childManaged );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 0 );
		assertInsertCount( 1 );

		s = openSession();
		s.beginTransaction();
		child = ( VersionedNode ) s.get( VersionedNode.class, child.getId() );
		assertEquals( "child", child.getName() );
		assertNull( child.getParent() );
		assertEquals( 0, child.getVersion() );
		parent = ( VersionedNode ) s.get( VersionedNode.class, parent.getId() );
		assertNotNull( parent );
		s.setReadOnly( child, true );
		s.delete( child );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 0 );
		assertDeleteCount( 1 );
	}

	public void testUpdateChildWithNewParentCommitWithReadOnlyChild() throws Exception {
		Session s = openSession();
		s.beginTransaction();
		VersionedNode child = new VersionedNode( "child", "child" );
		s.persist( child );
		s.getTransaction().commit();
		s.close();

		clearCounts();

		child.setName( "new child name" );
		VersionedNode parent = new VersionedNode( "parent", "parent");
		parent.addChild( child );

		s = openSession();
		s.beginTransaction();
		s.update( child );
		s.setReadOnly( child, true );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 0 );
		assertInsertCount( 1 );
		clearCounts();

		s = openSession();
		s.beginTransaction();
		parent = ( VersionedNode ) s.get( VersionedNode.class, parent.getId() );
		child = ( VersionedNode ) s.get( VersionedNode.class, child.getId() );
		assertEquals( child.getName(), "child" );
		assertNull( child.getParent() );
		assertEquals( 0, child.getVersion() );
		assertNotNull( parent );
		assertEquals( 0, parent.getChildren().size() );
		assertEquals( 0, parent.getVersion() );
		s.setReadOnly( parent, true );
		s.setReadOnly( child, true );
		s.delete( parent );
		s.delete( child );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 0 );
		assertDeleteCount( 2 );
	}

	public void testMergeDetachedChildWithNewParentCommitWithReadOnlyChild() throws Exception {
		Session s = openSession();
		s.beginTransaction();
		VersionedNode child = new VersionedNode( "child", "child" );
		s.persist( child );
		s.getTransaction().commit();
		s.close();

		clearCounts();

		child.setName( "new child name" );
		VersionedNode parent = new VersionedNode( "parent", "parent");
		parent.addChild( child );

		s = openSession();
		s.beginTransaction();
		child = ( VersionedNode ) s.merge( child );
		s.setReadOnly( child, true );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 1 );
		assertInsertCount( 1 );
		clearCounts();

		s = openSession();
		s.beginTransaction();
		parent = ( VersionedNode ) s.get( VersionedNode.class, parent.getId() );
		child = ( VersionedNode ) s.get( VersionedNode.class, child.getId() );
		assertEquals( child.getName(), "child" );
		assertNull( child.getParent() );
		assertEquals( 0, child.getVersion() );
		assertNotNull( parent );
		assertEquals( 0, parent.getChildren().size() );
		assertEquals( 1, parent.getVersion() );	// hmmm, why is was version updated?
		s.setReadOnly( parent, true );
		s.setReadOnly( child, true );
		s.delete( parent );
		s.delete( child );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 0 );
		assertDeleteCount( 2 );
	}

	public void testGetChildMakeReadOnlyThenMergeDetachedChildWithNewParent() throws Exception {
		Session s = openSession();
		s.beginTransaction();
		VersionedNode child = new VersionedNode( "child", "child" );
		s.persist( child );
		s.getTransaction().commit();
		s.close();

		clearCounts();

		child.setName( "new child name" );
		VersionedNode parent = new VersionedNode( "parent", "parent");
		parent.addChild( child );

		s = openSession();
		s.beginTransaction();
		VersionedNode childManaged = ( VersionedNode ) s.get( VersionedNode.class, child.getId() );
		s.setReadOnly( childManaged, true );
		VersionedNode childMerged = ( VersionedNode ) s.merge( child );
		assertSame( childManaged, childMerged );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 1 );
		assertInsertCount( 1 );
		clearCounts();

		s = openSession();
		s.beginTransaction();
		parent = ( VersionedNode ) s.get( VersionedNode.class, parent.getId() );
		child = ( VersionedNode ) s.get( VersionedNode.class, child.getId() );
		assertEquals( child.getName(), "child" );
		assertNull( child.getParent() );
		assertEquals( 0, child.getVersion() );
		assertNotNull( parent );
		assertEquals( 0, parent.getChildren().size() );
		assertEquals( 1, parent.getVersion() ); // / hmmm, why is was version updated?
		s.setReadOnly( parent, true );
		s.setReadOnly( child, true );
		s.delete( parent );
		s.delete( child );
		s.getTransaction().commit();
		s.close();

		assertUpdateCount( 0 );
		assertDeleteCount( 2 );
	}

	protected void cleanupTest() throws Exception {
		cleanup();
		super.cleanupTest();
	}

	private void cleanup() {
		Session s = sfi().openSession();
		s.beginTransaction();

		s.createQuery( "delete from VersionedNode where parent is not null" ).executeUpdate();
		s.createQuery( "delete from VersionedNode" ).executeUpdate();

		s.getTransaction().commit();
		s.close();
	}
}
