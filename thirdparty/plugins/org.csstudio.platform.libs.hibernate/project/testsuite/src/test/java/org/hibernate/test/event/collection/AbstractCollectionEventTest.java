//$Id: $
/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2007, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution statements
 * applied by the authors.
 * 
 * All third-party contributions are distributed under license by Red Hat
 * Middleware LLC.  This copyrighted material is made available to anyone
 * wishing to use, modify, copy, or redistribute it subject to the terms
 * and conditions of the GNU Lesser General Public License, as published by
 * the Free Software Foundation.  This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details.  You should
 * have received a copy of the GNU Lesser General Public License along with
 * this distribution; if not, write to: Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor Boston, MA  02110-1301  USA
 */
package org.hibernate.test.event.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.collection.PersistentSet;
import org.hibernate.event.AbstractCollectionEvent;
import org.hibernate.junit.functional.FunctionalTestCase;
import org.hibernate.test.event.collection.association.bidirectional.manytomany.ChildWithBidirectionalManyToMany;

/**
 *
 * @author Gail Badner
 */
public abstract class AbstractCollectionEventTest extends FunctionalTestCase {

	public AbstractCollectionEventTest(String string) {
		super( string );
	}

	public abstract String[] getMappings();

	public abstract ParentWithCollection createParent(String name);

	public abstract Collection createCollection();

	protected void cleanupTest() {
		ParentWithCollection dummyParent = createParent( "dummyParent" );
		dummyParent.newChildren( createCollection() );
		Child dummyChild = dummyParent.addChild( "dummyChild" );
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		List children = s.createCriteria( dummyChild.getClass() ).list();
		List parents = s.createCriteria( dummyParent.getClass() ).list();
		for ( Iterator it = parents.iterator(); it.hasNext(); ) {
			ParentWithCollection parent = ( ParentWithCollection ) it.next();
			parent.clearChildren();
			s.delete( parent );
		}
		for ( Iterator it = children.iterator(); it.hasNext(); ) {
			s.delete( it.next() );
		}
		tx.commit();
		s.close();
	}

	public void testSaveParentEmptyChildren() {
		CollectionListeners listeners = new CollectionListeners( getSessions() );
		ParentWithCollection parent = createParentWithNoChildren( "parent" );
		assertEquals( 0, parent.getChildren().size() );
		int index = 0;
		checkResult( listeners, listeners.getPreCollectionRecreateListener(), parent, index++ );
		checkResult( listeners, listeners.getPostCollectionRecreateListener(), parent, index++ );
		checkNumberOfResults( listeners, index );
		listeners.clear();
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
		tx.commit();
		s.close();
		assertNotNull( parent.getChildren() );
		checkNumberOfResults( listeners, 0 );
	}

	public void testSaveParentOneChild() {
		CollectionListeners listeners = new CollectionListeners( getSessions() );
		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
		int index = 0;
		checkResult( listeners, listeners.getPreCollectionRecreateListener(), parent, index++ );
		checkResult( listeners, listeners.getPostCollectionRecreateListener(), parent, index++ );
		Child child = ( Child ) parent.getChildren().iterator().next();
		if ( child instanceof ChildWithBidirectionalManyToMany ) {
			checkResult( listeners, listeners.getPreCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
			checkResult( listeners, listeners.getPostCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
		}
		checkNumberOfResults( listeners, index );
	}

	public void testUpdateParentNullToOneChild() {
		CollectionListeners listeners = new CollectionListeners( getSessions() );
		ParentWithCollection parent = createParentWithNullChildren( "parent" );
		listeners.clear();
		assertNull( parent.getChildren() );
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
		assertNotNull( parent.getChildren() );
		Child newChild = parent.addChild( "new" );
		tx.commit();
		s.close();
		int index = 0;
		if ( ( ( PersistentCollection ) parent.getChildren() ).wasInitialized() ) {
			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, index++ );
		}
		checkResult( listeners, listeners.getPreCollectionUpdateListener(), parent, index++ );
		checkResult( listeners, listeners.getPostCollectionUpdateListener(), parent, index++ );
		if ( newChild instanceof ChildWithBidirectionalManyToMany ) {
			checkResult( listeners, listeners.getPreCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) newChild, index++ );
			checkResult( listeners, listeners.getPostCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) newChild, index++ );
		}
		checkNumberOfResults( listeners, index );
	}

	public void testUpdateParentNoneToOneChild() {
		CollectionListeners listeners = new CollectionListeners( getSessions() );
		ParentWithCollection parent = createParentWithNoChildren( "parent" );
		listeners.clear();
		assertEquals( 0, parent.getChildren().size() );
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
		Child newChild = parent.addChild( "new" );
		tx.commit();
		s.close();
		int index = 0;
		if ( ( ( PersistentCollection ) parent.getChildren() ).wasInitialized() ) {
			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, index++ );
		}
		checkResult( listeners, listeners.getPreCollectionUpdateListener(), parent, index++ );
		checkResult( listeners, listeners.getPostCollectionUpdateListener(), parent, index++ );
		if ( newChild instanceof ChildWithBidirectionalManyToMany ) {
			checkResult( listeners, listeners.getPreCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) newChild, index++ );
			checkResult( listeners, listeners.getPostCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) newChild, index++ );
		}
		checkNumberOfResults( listeners, index );
	}

	public void testUpdateParentOneToTwoChildren() {
		CollectionListeners listeners = new CollectionListeners( getSessions() );
		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
		assertEquals( 1, parent.getChildren().size() );
		listeners.clear();
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
		Child newChild = parent.addChild( "new2" );
		tx.commit();
		s.close();
		int index = 0;
		if ( ( ( PersistentCollection ) parent.getChildren() ).wasInitialized() ) {
			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, index++ );
		}
		checkResult( listeners, listeners.getPreCollectionUpdateListener(), parent, index++ );
		checkResult( listeners, listeners.getPostCollectionUpdateListener(), parent, index++ );
		if ( newChild instanceof ChildWithBidirectionalManyToMany ) {
			checkResult( listeners, listeners.getPreCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) newChild, index++ );
			checkResult( listeners, listeners.getPostCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) newChild, index++ );
		}
		checkNumberOfResults( listeners, index );
	}

	public void testUpdateParentOneToTwoSameChildren() {
		CollectionListeners listeners = new CollectionListeners( getSessions() );
		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
		Child child = ( Child ) parent.getChildren().iterator().next();
		assertEquals( 1, parent.getChildren().size() );
		listeners.clear();
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
		if ( child instanceof Entity ) {
			child = ( Child ) s.get( child.getClass(), ( ( Entity ) child ).getId() );
		}
		parent.addChild( child );
		tx.commit();
		s.close();
		int index = 0;
		if ( ( ( PersistentCollection ) parent.getChildren() ).wasInitialized() ) {
			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, index++ );
		}
		ChildWithBidirectionalManyToMany childWithManyToMany = null;
		if ( child instanceof ChildWithBidirectionalManyToMany ) {
			childWithManyToMany = ( ChildWithBidirectionalManyToMany ) child;
			if ( ( ( PersistentCollection ) childWithManyToMany.getParents() ).wasInitialized() ) {
				checkResult( listeners, listeners.getInitializeCollectionListener(), childWithManyToMany, index++ );
			}
		}
		if ( !( parent.getChildren() instanceof PersistentSet ) ) {
			checkResult( listeners, listeners.getPreCollectionUpdateListener(), parent, index++ );
			checkResult( listeners, listeners.getPostCollectionUpdateListener(), parent, index++ );
		}
		if ( childWithManyToMany != null && !( childWithManyToMany.getParents() instanceof PersistentSet ) ) {
			checkResult( listeners, listeners.getPreCollectionUpdateListener(), childWithManyToMany, index++ );
			checkResult( listeners, listeners.getPostCollectionUpdateListener(), childWithManyToMany, index++ );
		}
		checkNumberOfResults( listeners, index );
	}

	public void testUpdateParentNullToOneChildDiffCollection() {
		CollectionListeners listeners = new CollectionListeners( getSessions() );
		ParentWithCollection parent = createParentWithNullChildren( "parent" );
		listeners.clear();
		assertNull( parent.getChildren() );
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
		Collection collectionOrig = parent.getChildren();
		parent.newChildren( createCollection() );
		Child newChild = parent.addChild( "new" );
		tx.commit();
		s.close();
		int index = 0;
		if ( ( ( PersistentCollection ) collectionOrig ).wasInitialized() ) {
			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, collectionOrig, index++ );
		}
		checkResult( listeners, listeners.getPreCollectionRemoveListener(), parent, collectionOrig, index++ );
		checkResult( listeners, listeners.getPostCollectionRemoveListener(), parent, collectionOrig, index++ );
		if ( newChild instanceof ChildWithBidirectionalManyToMany ) {
			checkResult( listeners, listeners.getPreCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) newChild, index++ );
			checkResult( listeners, listeners.getPostCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) newChild, index++ );
		}
		checkResult( listeners, listeners.getPreCollectionRecreateListener(), parent, index++ );
		checkResult( listeners, listeners.getPostCollectionRecreateListener(), parent, index++ );
		checkNumberOfResults( listeners, index );
	}

	public void testUpdateParentNoneToOneChildDiffCollection() {
		CollectionListeners listeners = new CollectionListeners( getSessions() );
		ParentWithCollection parent = createParentWithNoChildren( "parent" );
		listeners.clear();
		assertEquals( 0, parent.getChildren().size() );
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
		Collection oldCollection = parent.getChildren();
		parent.newChildren( createCollection() );
		Child newChild = parent.addChild( "new" );
		tx.commit();
		s.close();
		int index = 0;
		if ( ( ( PersistentCollection ) oldCollection ).wasInitialized() ) {
			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, oldCollection, index++ );
		}
		checkResult( listeners, listeners.getPreCollectionRemoveListener(), parent, oldCollection, index++ );
		checkResult( listeners, listeners.getPostCollectionRemoveListener(), parent, oldCollection, index++ );
		if ( newChild instanceof ChildWithBidirectionalManyToMany ) {
			checkResult( listeners, listeners.getPreCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) newChild, index++ );
			checkResult( listeners, listeners.getPostCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) newChild, index++ );
		}
		checkResult( listeners, listeners.getPreCollectionRecreateListener(), parent, index++ );
		checkResult( listeners, listeners.getPostCollectionRecreateListener(), parent, index++ );
		checkNumberOfResults( listeners, index );
	}

	public void testUpdateParentOneChildDiffCollectionSameChild() {
		CollectionListeners listeners = new CollectionListeners( getSessions() );
		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
		Child child = ( Child ) parent.getChildren().iterator().next();
		listeners.clear();
		assertEquals( 1, parent.getChildren().size() );
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
		if ( child instanceof Entity ) {
			child = ( Child ) s.get( child.getClass(), ( ( Entity ) child).getId() );
		}
		Collection oldCollection = parent.getChildren();
		parent.newChildren( createCollection() );
		parent.addChild( child );
		tx.commit();
		s.close();
		int index = 0;
		if ( ( ( PersistentCollection ) oldCollection ).wasInitialized() ) {
			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, oldCollection, index++ );
		}
		if ( child instanceof ChildWithBidirectionalManyToMany ) {
			ChildWithBidirectionalManyToMany childWithManyToMany = ( ChildWithBidirectionalManyToMany ) child;
			if ( ( ( PersistentCollection ) childWithManyToMany.getParents() ).wasInitialized() ) {
				checkResult( listeners, listeners.getInitializeCollectionListener(), childWithManyToMany, index++ );
			}
		}
		checkResult( listeners, listeners.getPreCollectionRemoveListener(), parent, oldCollection, index++ );
		checkResult( listeners, listeners.getPostCollectionRemoveListener(), parent, oldCollection, index++ );
		if ( child instanceof ChildWithBidirectionalManyToMany ) {
			// hmmm, the same parent was removed and re-added to the child's collection;
			// should this be considered an update?
			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
		}
		checkResult( listeners, listeners.getPreCollectionRecreateListener(), parent, index++ );
		checkResult( listeners, listeners.getPostCollectionRecreateListener(), parent, index++ );
		checkNumberOfResults( listeners, index );
	}

	public void testUpdateParentOneChildDiffCollectionDiffChild() {
		CollectionListeners listeners = new CollectionListeners( getSessions() );
		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
		Child oldChild = ( Child ) parent.getChildren().iterator().next();
		listeners.clear();
		assertEquals( 1, parent.getChildren().size() );
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
		if ( oldChild instanceof Entity ) {
			oldChild = ( Child ) s.get( oldChild.getClass(), ( ( Entity ) oldChild).getId() );
		}
		Collection oldCollection = parent.getChildren();
		parent.newChildren( createCollection() );
		Child newChild = parent.addChild( "new1" );
		tx.commit();
		s.close();
		int index = 0;
		if ( ( ( PersistentCollection ) oldCollection ).wasInitialized() ) {
			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, oldCollection, index++ );
		}
		if ( oldChild instanceof ChildWithBidirectionalManyToMany ) {
			ChildWithBidirectionalManyToMany oldChildWithManyToMany = ( ChildWithBidirectionalManyToMany ) oldChild;
			if ( ( ( PersistentCollection ) oldChildWithManyToMany.getParents() ).wasInitialized() ) {
				checkResult( listeners, listeners.getInitializeCollectionListener(), oldChildWithManyToMany, index++ );
			}
		}
		checkResult( listeners, listeners.getPreCollectionRemoveListener(), parent, oldCollection, index++ );
		checkResult( listeners, listeners.getPostCollectionRemoveListener(), parent, oldCollection, index++ );
		if ( oldChild instanceof ChildWithBidirectionalManyToMany ) {
			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) oldChild, index++ );
			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) oldChild, index++ );
			checkResult( listeners, listeners.getPreCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) newChild, index++ );
			checkResult( listeners, listeners.getPostCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) newChild, index++ );
		}
		checkResult( listeners, listeners.getPreCollectionRecreateListener(), parent, index++ );
		checkResult( listeners, listeners.getPostCollectionRecreateListener(), parent, index++ );
		checkNumberOfResults( listeners, index );
	}

	public void testUpdateParentOneChildToNoneByRemove() {
		CollectionListeners listeners = new CollectionListeners( getSessions() );
		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
		assertEquals( 1, parent.getChildren().size() );
		Child child = ( Child ) parent.getChildren().iterator().next();
		listeners.clear();
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
		if ( child instanceof Entity ) {
			child = ( Child ) s.get( child.getClass(), ( ( Entity ) child ).getId() );
		}
		parent.removeChild( child );
		tx.commit();
		s.close();
		int index = 0;
		if ( ( ( PersistentCollection ) parent.getChildren() ).wasInitialized() ) {
			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, index++ );
		}
		if ( child instanceof ChildWithBidirectionalManyToMany ) {
			ChildWithBidirectionalManyToMany childWithManyToMany = ( ChildWithBidirectionalManyToMany ) child;
			if ( ( ( PersistentCollection ) childWithManyToMany.getParents( ) ).wasInitialized() ) {
				checkResult( listeners, listeners.getInitializeCollectionListener(), childWithManyToMany, index++ );
			}
		}
		checkResult( listeners, listeners.getPreCollectionUpdateListener(), parent, index++ );
		checkResult( listeners, listeners.getPostCollectionUpdateListener(), parent, index++ );
		if ( child instanceof ChildWithBidirectionalManyToMany ) {
			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
		}
		checkNumberOfResults( listeners, index );
	}

	public void testUpdateParentOneChildToNoneByClear() {
		CollectionListeners listeners = new CollectionListeners( getSessions() );
		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
		assertEquals( 1, parent.getChildren().size() );
		Child child = ( Child ) parent.getChildren().iterator().next();
		listeners.clear();
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
		if ( child instanceof Entity ) {
			child = ( Child ) s.get( child.getClass(), ( ( Entity ) child ).getId() );
		}
		parent.clearChildren();
		tx.commit();
		s.close();
		int index = 0;
		if ( ( ( PersistentCollection ) parent.getChildren() ).wasInitialized() ) {
			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, index++ );
		}
		if ( child instanceof ChildWithBidirectionalManyToMany ) {
			ChildWithBidirectionalManyToMany childWithManyToMany = ( ChildWithBidirectionalManyToMany ) child;
			if ( ( ( PersistentCollection ) childWithManyToMany.getParents() ).wasInitialized() ) {
				checkResult( listeners, listeners.getInitializeCollectionListener(), childWithManyToMany, index++ );
			}
		}
		checkResult( listeners, listeners.getPreCollectionUpdateListener(), parent, index++ );
		checkResult( listeners, listeners.getPostCollectionUpdateListener(), parent, index++ );
		if ( child instanceof ChildWithBidirectionalManyToMany ) {
			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
		}
		checkNumberOfResults( listeners, index );
	}

	public void testUpdateParentTwoChildrenToOne() {
		CollectionListeners listeners = new CollectionListeners( getSessions() );
		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
		assertEquals( 1, parent.getChildren().size() );
		Child oldChild = ( Child ) parent.getChildren().iterator().next();
		listeners.clear();
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
		parent.addChild( "new" );
		tx.commit();
		s.close();
		listeners.clear();
		s = openSession();
		tx = s.beginTransaction();
		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
		if ( oldChild instanceof Entity ) {
			oldChild = ( Child ) s.get( oldChild.getClass(), ( ( Entity ) oldChild ).getId() );
		}
		parent.removeChild( oldChild );
		tx.commit();
		s.close();
		int index = 0;
		if ( ( ( PersistentCollection ) parent.getChildren() ).wasInitialized() ) {
			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, index++ );
		}
		if ( oldChild instanceof ChildWithBidirectionalManyToMany ) {
			ChildWithBidirectionalManyToMany oldChildWithManyToMany = ( ChildWithBidirectionalManyToMany ) oldChild;
			if ( ( ( PersistentCollection ) oldChildWithManyToMany.getParents() ).wasInitialized() ) {
				checkResult( listeners, listeners.getInitializeCollectionListener(), oldChildWithManyToMany, index++ );
			}
		}
		checkResult( listeners, listeners.getPreCollectionUpdateListener(), parent, index++ );
		checkResult( listeners, listeners.getPostCollectionUpdateListener(), parent, index++ );
		if ( oldChild instanceof ChildWithBidirectionalManyToMany ) {
			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) oldChild, index++ );
			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) oldChild, index++ );
		}
		checkNumberOfResults( listeners, index );
	}

	public void testDeleteParentWithNullChildren() {
		CollectionListeners listeners = new CollectionListeners( getSessions() );
		ParentWithCollection parent = createParentWithNullChildren( "parent" );
		listeners.clear();
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
		s.delete( parent );
		tx.commit();
		s.close();
		int index = 0;
		checkResult( listeners, listeners.getInitializeCollectionListener(), parent, index++ );
		checkResult( listeners, listeners.getPreCollectionRemoveListener(), parent, index++ );
		checkResult( listeners, listeners.getPostCollectionRemoveListener(), parent, index++ );
		checkNumberOfResults( listeners, index );
	}

	public void testDeleteParentWithNoChildren() {
		CollectionListeners listeners = new CollectionListeners( getSessions() );
		ParentWithCollection parent = createParentWithNoChildren( "parent" );
		listeners.clear();
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
		s.delete( parent );
		tx.commit();
		s.close();
		int index = 0;
		checkResult( listeners, listeners.getInitializeCollectionListener(), parent, index++ );
		checkResult( listeners, listeners.getPreCollectionRemoveListener(), parent, index++ );
		checkResult( listeners, listeners.getPostCollectionRemoveListener(), parent, index++ );
		checkNumberOfResults( listeners, index );
	}

	public void testDeleteParentAndChild() {
		CollectionListeners listeners = new CollectionListeners( getSessions() );
		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
		Child child = ( Child ) parent.getChildren().iterator().next();
		listeners.clear();
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
		if ( child instanceof Entity ) {
			child = ( Child ) s.get( child.getClass(), ( ( Entity ) child ).getId() );
		}
		parent.removeChild( child );
		if ( child instanceof Entity ) {
			s.delete( child );
		}
		s.delete( parent );
		tx.commit();
		s.close();
		int index = 0;
		checkResult( listeners, listeners.getInitializeCollectionListener(), parent, index++ );
		if ( child instanceof ChildWithBidirectionalManyToMany ) {
			checkResult( listeners, listeners.getInitializeCollectionListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
		}
		checkResult( listeners, listeners.getPreCollectionRemoveListener(), parent, index++ );
		checkResult( listeners, listeners.getPostCollectionRemoveListener(), parent, index++ );
		if ( child instanceof ChildWithBidirectionalManyToMany ) {
			checkResult( listeners, listeners.getPreCollectionRemoveListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
			checkResult( listeners, listeners.getPostCollectionRemoveListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
		}
		checkNumberOfResults( listeners, index );
	}

	public void testMoveChildToDifferentParent() {
		CollectionListeners listeners = new CollectionListeners( getSessions() );
		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
		ParentWithCollection otherParent = createParentWithOneChild( "otherParent", "otherChild" );
		Child child = ( Child ) parent.getChildren().iterator().next();
		listeners.clear();
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
		otherParent = ( ParentWithCollection ) s.get( otherParent.getClass(), otherParent.getId() );
		if ( child instanceof Entity ) {
			child = ( Child ) s.get( child.getClass(), ( ( Entity ) child ).getId() );
		}
		parent.removeChild( child );
		otherParent.addChild( child );
		tx.commit();
		s.close();
		int index = 0;
		if ( ( ( PersistentCollection ) parent.getChildren() ).wasInitialized() ) {
			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, index++ );
		}
		if ( child instanceof ChildWithBidirectionalManyToMany ) {
			checkResult( listeners, listeners.getInitializeCollectionListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
		}
		if ( ( ( PersistentCollection ) otherParent.getChildren() ).wasInitialized() ) {
			checkResult( listeners, listeners.getInitializeCollectionListener(), otherParent, index++ );
		}
		checkResult( listeners, listeners.getPreCollectionUpdateListener(), parent, index++ );
		checkResult( listeners, listeners.getPostCollectionUpdateListener(), parent, index++ );
		checkResult( listeners, listeners.getPreCollectionUpdateListener(), otherParent, index++ );
		checkResult( listeners, listeners.getPostCollectionUpdateListener(), otherParent, index++ );
		if ( child instanceof ChildWithBidirectionalManyToMany ) {
			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
		}
		checkNumberOfResults( listeners, index );
	}

	public void testMoveAllChildrenToDifferentParent() {
		CollectionListeners listeners = new CollectionListeners( getSessions() );
		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
		ParentWithCollection otherParent = createParentWithOneChild( "otherParent", "otherChild" );
		Child child = ( Child ) parent.getChildren().iterator().next();
		listeners.clear();
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
		otherParent = ( ParentWithCollection ) s.get( otherParent.getClass(), otherParent.getId() );
		if ( child instanceof Entity ) {
			child = ( Child ) s.get( child.getClass(), ( ( Entity ) child ).getId() );
		}
		otherParent.addAllChildren( parent.getChildren() );
		parent.clearChildren();
		tx.commit();
		s.close();
		int index = 0;
		if ( ( ( PersistentCollection ) parent.getChildren() ).wasInitialized() ) {
			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, index++ );
		}
		if ( ( ( PersistentCollection ) otherParent.getChildren() ).wasInitialized() ) {
			checkResult( listeners, listeners.getInitializeCollectionListener(), otherParent, index++ );
		}
		if ( child instanceof ChildWithBidirectionalManyToMany ) {
			checkResult( listeners, listeners.getInitializeCollectionListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
		}
		checkResult( listeners, listeners.getPreCollectionUpdateListener(), parent, index++ );
		checkResult( listeners, listeners.getPostCollectionUpdateListener(), parent, index++ );
		checkResult( listeners, listeners.getPreCollectionUpdateListener(), otherParent, index++ );
		checkResult( listeners, listeners.getPostCollectionUpdateListener(), otherParent, index++ );
		if ( child instanceof ChildWithBidirectionalManyToMany ) {
			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
		}
		checkNumberOfResults( listeners, index );
	}

	public void testMoveCollectionToDifferentParent() {
		CollectionListeners listeners = new CollectionListeners( getSessions() );
		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
		ParentWithCollection otherParent = createParentWithOneChild( "otherParent", "otherChild" );
		listeners.clear();
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
		otherParent = ( ParentWithCollection ) s.get( otherParent.getClass(), otherParent.getId() );
		Collection otherCollectionOrig = otherParent.getChildren();
		otherParent.newChildren( parent.getChildren() );
		parent.newChildren( null );
		tx.commit();
		s.close();
		int index = 0;
		Child otherChildOrig = null;
		if ( ( ( PersistentCollection ) otherCollectionOrig ).wasInitialized() ) {
			checkResult( listeners, listeners.getInitializeCollectionListener(), otherParent, otherCollectionOrig, index++ );
			otherChildOrig = ( Child ) otherCollectionOrig.iterator().next();
			if ( otherChildOrig instanceof ChildWithBidirectionalManyToMany ) {
				checkResult( listeners, listeners.getInitializeCollectionListener(), ( ChildWithBidirectionalManyToMany ) otherChildOrig, index++ );
			}
		}
		checkResult( listeners, listeners.getInitializeCollectionListener(), parent, otherParent.getChildren(), index++ );
		Child otherChild = ( Child ) otherParent.getChildren().iterator().next();
		if ( otherChild instanceof ChildWithBidirectionalManyToMany ) {
			checkResult( listeners, listeners.getInitializeCollectionListener(), ( ChildWithBidirectionalManyToMany ) otherChild, index++ );
		}
		checkResult( listeners, listeners.getPreCollectionRemoveListener(), parent, otherParent.getChildren(), index++ );
		checkResult( listeners, listeners.getPostCollectionRemoveListener(), parent, otherParent.getChildren(), index++ );
		checkResult( listeners, listeners.getPreCollectionRemoveListener(), otherParent, otherCollectionOrig, index++ );
		checkResult( listeners, listeners.getPostCollectionRemoveListener(), otherParent, otherCollectionOrig, index++ );
		if ( otherChild instanceof ChildWithBidirectionalManyToMany ) {
			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) otherChildOrig, index++ );
			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) otherChildOrig, index++ );
			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) otherChild, index++ );
			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) otherChild, index++ );
		}
		checkResult( listeners, listeners.getPreCollectionRecreateListener(), otherParent, index++ );
		checkResult( listeners, listeners.getPostCollectionRecreateListener(), otherParent, index++ );
		// there should also be pre- and post-recreate collection events for parent, but thats broken now;
		// this is covered in BrokenCollectionEventTest
		checkNumberOfResults( listeners, index );
	}

	public void testMoveCollectionToDifferentParentFlushMoveToDifferentParent() {
		CollectionListeners listeners = new CollectionListeners( getSessions() );
		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
		ParentWithCollection otherParent = createParentWithOneChild( "otherParent", "otherChild" );
		ParentWithCollection otherOtherParent = createParentWithNoChildren( "otherParent" );
		listeners.clear();
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
		otherParent = ( ParentWithCollection ) s.get( otherParent.getClass(), otherParent.getId() );
		otherOtherParent = ( ParentWithCollection ) s.get( otherOtherParent.getClass(), otherOtherParent.getId() );
		Collection otherCollectionOrig = otherParent.getChildren();
		Collection otherOtherCollectionOrig = otherOtherParent.getChildren();
		otherParent.newChildren( parent.getChildren() );
		parent.newChildren( null );
		s.flush();
		otherOtherParent.newChildren( otherParent.getChildren() );
		otherParent.newChildren( null );
		tx.commit();
		s.close();
		int index = 0;
		Child otherChildOrig = null;
		if ( ( ( PersistentCollection ) otherCollectionOrig ).wasInitialized() ) {
			checkResult( listeners, listeners.getInitializeCollectionListener(), otherParent, otherCollectionOrig, index++ );
			otherChildOrig = ( Child ) otherCollectionOrig.iterator().next();
			if ( otherChildOrig instanceof ChildWithBidirectionalManyToMany ) {
				checkResult( listeners, listeners.getInitializeCollectionListener(), ( ChildWithBidirectionalManyToMany ) otherChildOrig, index++ );
			}
		}
		checkResult( listeners, listeners.getInitializeCollectionListener(), parent, otherOtherParent.getChildren(), index++ );
		Child otherOtherChild = ( Child ) otherOtherParent.getChildren().iterator().next();
		if ( otherOtherChild instanceof ChildWithBidirectionalManyToMany ) {
			checkResult( listeners, listeners.getInitializeCollectionListener(), ( ChildWithBidirectionalManyToMany ) otherOtherChild, index++ );
		}
		checkResult( listeners, listeners.getPreCollectionRemoveListener(), parent, otherOtherParent.getChildren(), index++ );
		checkResult( listeners, listeners.getPostCollectionRemoveListener(), parent, otherOtherParent.getChildren(), index++ );
		checkResult( listeners, listeners.getPreCollectionRemoveListener(), otherParent, otherCollectionOrig, index++ );
		checkResult( listeners, listeners.getPostCollectionRemoveListener(), otherParent, otherCollectionOrig, index++ );
		if ( otherOtherChild instanceof ChildWithBidirectionalManyToMany ) {
			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) otherChildOrig, index++ );
			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) otherChildOrig, index++ );
			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) otherOtherChild, index++ );
			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) otherOtherChild, index++ );
		}
		checkResult( listeners, listeners.getPreCollectionRecreateListener(), otherParent, otherOtherParent.getChildren(), index++ );
		checkResult( listeners, listeners.getPostCollectionRecreateListener(), otherParent, otherOtherParent.getChildren(), index++ );
		if ( ( ( PersistentCollection ) otherOtherCollectionOrig ).wasInitialized() ) {
			checkResult( listeners, listeners.getInitializeCollectionListener(), otherOtherParent, otherOtherCollectionOrig, index++ );
		}
		checkResult( listeners, listeners.getPreCollectionRemoveListener(), otherParent, otherOtherParent.getChildren(), index++ );
		checkResult( listeners, listeners.getPostCollectionRemoveListener(), otherParent, otherOtherParent.getChildren(), index++ );
		checkResult( listeners, listeners.getPreCollectionRemoveListener(), otherOtherParent, otherOtherCollectionOrig, index++ );
		checkResult( listeners, listeners.getPostCollectionRemoveListener(), otherOtherParent, otherOtherCollectionOrig, index++ );
		if ( otherOtherChild instanceof ChildWithBidirectionalManyToMany ) {
			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) otherOtherChild, index++ );
			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) otherOtherChild, index++ );
		}
		checkResult( listeners, listeners.getPreCollectionRecreateListener(), otherOtherParent, index++ );
		checkResult( listeners, listeners.getPostCollectionRecreateListener(), otherOtherParent, index++ );
		// there should also be pre- and post-recreate collection events for parent, and otherParent
		// but thats broken now; this is covered in BrokenCollectionEventTest
		checkNumberOfResults( listeners, index );
	}

	protected ParentWithCollection createParentWithNullChildren(String parentName) {
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		ParentWithCollection parent = createParent( parentName );
		s.save( parent );
		tx.commit();
		s.close();
		return parent;
	}

	protected ParentWithCollection createParentWithNoChildren(String parentName) {
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		ParentWithCollection parent = createParent( parentName );
		parent.newChildren( createCollection() );
		s.save( parent );
		tx.commit();
		s.close();
		return parent;
	}

	protected ParentWithCollection createParentWithOneChild(String parentName, String ChildName) {
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		ParentWithCollection parent = createParent( parentName );
		parent.newChildren( createCollection() );
		parent.addChild( ChildName );
		s.save( parent );
		tx.commit();
		s.close();
		return parent;
	}

	protected void checkResult(CollectionListeners listeners,
							 CollectionListeners.Listener listenerExpected,
							 ParentWithCollection parent,
							 int index) {
		checkResult( listeners, listenerExpected, parent, parent.getChildren(), index );
	}
	protected void checkResult(CollectionListeners listeners,
							 CollectionListeners.Listener listenerExpected,
							 ChildWithBidirectionalManyToMany child,
							 int index) {
		checkResult( listeners, listenerExpected, child, child.getParents(), index );
	}

	protected void checkResult(CollectionListeners listeners,
							 CollectionListeners.Listener listenerExpected,
							 Entity ownerExpected,
							 Collection collExpected,
							 int index) {
		assertSame( listenerExpected, listeners.getListenersCalled().get( index ) );
		assertSame(
				ownerExpected,
				( ( AbstractCollectionEvent ) listeners.getEvents().get( index ) ).getAffectedOwnerOrNull()
		);
		assertEquals(
				ownerExpected.getId(),
				( ( AbstractCollectionEvent ) listeners.getEvents().get( index ) ).getAffectedOwnerIdOrNull()
		);
		assertEquals(
				ownerExpected.getClass().getName(),
				( ( AbstractCollectionEvent ) listeners.getEvents().get( index ) ).getAffectedOwnerEntityName()
		);
		assertSame(
				collExpected, ( ( AbstractCollectionEvent ) listeners.getEvents().get( index ) ).getCollection()
		);
	}

	protected void checkNumberOfResults(CollectionListeners listeners, int nEventsExpected) {
		assertEquals( nEventsExpected, listeners.getListenersCalled().size() );
		assertEquals( nEventsExpected, listeners.getEvents().size() );
	}
}
