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
package org.hibernate.hql.ast.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import antlr.ASTFactory;
import antlr.collections.AST;
import antlr.collections.impl.ASTArray;

/**
 * Provides utility methods for AST traversal and manipulation.
 *
 * @author Joshua Davis
 * @author Steve Ebersole
 */
public final class ASTUtil {
	/**
	 * Disallow instantiation.
	 *
	 * @deprecated (tellclovertoignorethis)
	 */
	private ASTUtil() {
	}

	/**
	 * Creates a single node AST.
	 * <p/>
	 * TODO : this is silly, remove it...
	 *
	 * @param astFactory The factory.
	 * @param type The node type.
	 * @param text The node text.
	 *
	 * @return AST - A single node tree.
	 *
	 * @deprecated silly
	 */
	public static AST create(ASTFactory astFactory, int type, String text) {
		return astFactory.create( type, text );
	}

	/**
	 * Creates a single node AST as a sibling of the passed prevSibling,
	 * taking care to reorganize the tree correctly to account for this
	 * newly created node.
	 *
	 * @param astFactory The factory.
	 * @param type The node type.
	 * @param text The node text.
	 * @param prevSibling The previous sibling.
	 *
	 * @return The created AST node.
	 */
	public static AST createSibling(ASTFactory astFactory, int type, String text, AST prevSibling) {
		AST node = astFactory.create( type, text );
		return insertSibling( node, prevSibling );
	}

	/**
	 * Inserts a node into a child subtree as a particularly positioned
	 * sibling taking care to properly reorganize the tree to account for this
	 * new addition.
	 *
	 * @param node The node to insert
	 * @param prevSibling The previous node at the sibling position
	 * where we want this node inserted.
	 *
	 * @return The return is the same as the node parameter passed in.
	 */
	public static AST insertSibling(AST node, AST prevSibling) {
		node.setNextSibling( prevSibling.getNextSibling() );
		prevSibling.setNextSibling( node );
		return node;
	}

	/**
	 * Creates a 'binary operator' subtree, given the information about the
	 * parent and the two child nodex.
	 *
	 * @param factory The AST factory.
	 * @param parentType The type of the parent node.
	 * @param parentText The text of the parent node.
	 * @param child1 The first child.
	 * @param child2 The second child.
	 *
	 * @return AST - A new sub-tree of the form "(parent child1 child2)"
	 */
	public static AST createBinarySubtree(ASTFactory factory, int parentType, String parentText, AST child1, AST child2) {
		ASTArray array = createAstArray( factory, 3, parentType, parentText, child1 );
		array.add( child2 );
		return factory.make( array );
	}

	/**
	 * Creates a single parent of the specified child (i.e. a 'unary operator'
	 * subtree).
	 *
	 * @param factory The AST factory.
	 * @param parentType The type of the parent node.
	 * @param parentText The text of the parent node.
	 * @param child The child.
	 *
	 * @return AST - A new sub-tree of the form "(parent child)"
	 */
	public static AST createParent(ASTFactory factory, int parentType, String parentText, AST child) {
		ASTArray array = createAstArray( factory, 2, parentType, parentText, child );
		return factory.make( array );
	}

	public static AST createTree(ASTFactory factory, AST[] nestedChildren) {
		AST[] array = new AST[2];
		int limit = nestedChildren.length - 1;
		for ( int i = limit; i >= 0; i-- ) {
			if ( i != limit ) {
				array[1] = nestedChildren[i + 1];
				array[0] = nestedChildren[i];
				factory.make( array );
			}
		}
		return array[0];
	}

	/**
	 * Determine if a given node (test) is contained anywhere in the subtree
	 * of another given node (fixture).
	 *
	 * @param fixture The node against which to testto be checked for children.
	 * @param test The node to be tested as being a subtree child of the parent.
	 * @return True if child is contained in the parent's collection of children.
	 */
	public static boolean isSubtreeChild(AST fixture, AST test) {
		AST n = fixture.getFirstChild();
		while ( n != null ) {
			if ( n == test ) {
				return true;
			}
			if ( n.getFirstChild() != null && isSubtreeChild( n, test ) ) {
				return true;
			}
			n = n.getNextSibling();
		}
		return false;
	}

	/**
	 * Finds the first node of the specified type in the chain of children.
	 *
	 * @param parent The parent
	 * @param type The type to find.
	 *
	 * @return The first node of the specified type, or null if not found.
	 */
	public static AST findTypeInChildren(AST parent, int type) {
		AST n = parent.getFirstChild();
		while ( n != null && n.getType() != type ) {
			n = n.getNextSibling();
		}
		return n;
	}

	/**
	 * Returns the last direct child of 'n'.
	 *
	 * @param n The parent
	 *
	 * @return The last direct child of 'n'.
	 */
	public static AST getLastChild(AST n) {
		return getLastSibling( n.getFirstChild() );
	}

	/**
	 * Returns the last sibling of 'a'.
	 *
	 * @param a The sibling.
	 *
	 * @return The last sibling of 'a'.
	 */
	private static AST getLastSibling(AST a) {
		AST last = null;
		while ( a != null ) {
			last = a;
			a = a.getNextSibling();
		}
		return last;
	}

	/**
	 * Returns the 'list' representation with some brackets around it for debugging.
	 *
	 * @param n The tree.
	 *
	 * @return The list representation of the tree.
	 */
	public static String getDebugString(AST n) {
		StringBuffer buf = new StringBuffer();
		buf.append( "[ " );
		buf.append( ( n == null ) ? "{null}" : n.toStringTree() );
		buf.append( " ]" );
		return buf.toString();
	}

	/**
	 * Find the previous sibling in the parent for the given child.
	 *
	 * @param parent the parent node
	 * @param child the child to find the previous sibling of
	 *
	 * @return the previous sibling of the child
	 */
	public static AST findPreviousSibling(AST parent, AST child) {
		AST prev = null;
		AST n = parent.getFirstChild();
		while ( n != null ) {
			if ( n == child ) {
				return prev;
			}
			prev = n;
			n = n.getNextSibling();
		}
		throw new IllegalArgumentException( "Child not found in parent!" );
	}

	/**
	 * Makes the child node a sibling of the parent, reconnecting all siblings.
	 *
	 * @param parent the parent
	 * @param child the child
	 */
	public static void makeSiblingOfParent(AST parent, AST child) {
		AST prev = findPreviousSibling( parent, child );
		if ( prev != null ) {
			prev.setNextSibling( child.getNextSibling() );
		}
		else { // child == parent.getFirstChild()
			parent.setFirstChild( child.getNextSibling() );
		}
		child.setNextSibling( parent.getNextSibling() );
		parent.setNextSibling( child );
	}

	public static String getPathText(AST n) {
		StringBuffer buf = new StringBuffer();
		getPathText( buf, n );
		return buf.toString();
	}

	private static void getPathText(StringBuffer buf, AST n) {
		AST firstChild = n.getFirstChild();
		// If the node has a first child, recurse into the first child.
		if ( firstChild != null ) {
			getPathText( buf, firstChild );
		}
		// Append the text of the current node.
		buf.append( n.getText() );
		// If there is a second child (RHS), recurse into that child.
		if ( firstChild != null && firstChild.getNextSibling() != null ) {
			getPathText( buf, firstChild.getNextSibling() );
		}
	}

	public static boolean hasExactlyOneChild(AST n) {
		return n != null && n.getFirstChild() != null && n.getFirstChild().getNextSibling() == null;
	}

	public static void appendSibling(AST n, AST s) {
		while ( n.getNextSibling() != null ) {
			n = n.getNextSibling();
		}
		n.setNextSibling( s );
	}

	/**
	 * Inserts the child as the first child of the parent, all other children are shifted over to the 'right'.
	 *
	 * @param parent the parent
	 * @param child the new first child
	 */
	public static void insertChild(AST parent, AST child) {
		if ( parent.getFirstChild() == null ) {
			parent.setFirstChild( child );
		}
		else {
			AST n = parent.getFirstChild();
			parent.setFirstChild( child );
			child.setNextSibling( n );
		}
	}

	private static ASTArray createAstArray(ASTFactory factory, int size, int parentType, String parentText, AST child1) {
		ASTArray array = new ASTArray( size );
		array.add( factory.create( parentType, parentText ) );
		array.add( child1 );
		return array;
	}

	/**
	 * Filters nodes out of a tree.
	 */
	public static interface FilterPredicate {
		/**
		 * Returns true if the node should be filtered out.
		 *
		 * @param n The node.
		 *
		 * @return true if the node should be filtered out, false to keep the node.
		 */
		boolean exclude(AST n);
	}

	/**
	 * A predicate that uses inclusion, rather than exclusion semantics.
	 */
	public abstract static class IncludePredicate implements FilterPredicate {
		public final boolean exclude(AST node) {
			return !include( node );
		}

		public abstract boolean include(AST node);
	}

	public static List collectChildren(AST root, FilterPredicate predicate) {
		return new CollectingNodeVisitor( predicate ).collect( root );
	}

	private static class CollectingNodeVisitor implements NodeTraverser.VisitationStrategy {
		private final FilterPredicate predicate;
		private final List collectedNodes = new ArrayList();

		public CollectingNodeVisitor(FilterPredicate predicate) {
			this.predicate = predicate;
		}

		public void visit(AST node) {
			if ( predicate == null || !predicate.exclude( node ) ) {
				collectedNodes.add( node );
			}
		}

		public List getCollectedNodes() {
			return collectedNodes;
		}

		public List collect(AST root) {
			NodeTraverser traverser = new NodeTraverser( this );
			traverser.traverseDepthFirst( root );
			return collectedNodes;
		}
	}

	/**
	 * Method to generate a map of token type names, keyed by their token type values.
	 *
	 * @param tokenTypeInterface The *TokenTypes interface (or implementor of said interface).
	 * @return The generated map.
	 */
	public static Map generateTokenNameCache(Class tokenTypeInterface) {
		final Field[] fields = tokenTypeInterface.getFields();
		Map cache = new HashMap( (int)( fields.length * .75 ) + 1 );
		for ( int i = 0; i < fields.length; i++ ) {
			final Field field = fields[i];
			if ( Modifier.isStatic( field.getModifiers() ) ) {
				try {
					cache.put( field.get( null ), field.getName() );
				}
				catch ( Throwable ignore ) {
				}
			}
		}
		return cache;
	}

	/**
	 * Get the name of a constant defined on the given class which has the given value.
	 * <p/>
	 * Note, if multiple constants have this value, the first will be returned which is known to be different
	 * on different JVM implementations.
	 *
	 * @param owner The class which defines the constant
	 * @param value The value of the constant.
	 *
	 * @return The token type name, *or* the integer value if the name could not be found.
	 *
	 * @deprecated Use #getTokenTypeName instead
	 */
	public static String getConstantName(Class owner, int value) {
		return getTokenTypeName( owner, value );
	}

	/**
	 * Intended to retrieve the name of an AST token type based on the token type interface.  However, this
	 * method can be used to look up the name of any constant defined on a class/interface based on the constant value.
	 * Note that if multiple constants have this value, the first will be returned which is known to be different
	 * on different JVM implementations.
	 *
	 * @param tokenTypeInterface The *TokenTypes interface (or one of its implementors).
	 * @param tokenType The token type value.
	 *
	 * @return The corresponding name.
	 */
	public static String getTokenTypeName(Class tokenTypeInterface, int tokenType) {
		String tokenTypeName = Integer.toString( tokenType );
		if ( tokenTypeInterface != null ) {
			Field[] fields = tokenTypeInterface.getFields();
			for ( int i = 0; i < fields.length; i++ ) {
				final Integer fieldValue = extractIntegerValue( fields[i] );
				if ( fieldValue != null && fieldValue.intValue() == tokenType ) {
					tokenTypeName = fields[i].getName();
					break;
				}
			}
		}
		return tokenTypeName;
	}

	private static Integer extractIntegerValue(Field field) {
		Integer rtn = null;
		try {
			Object value = field.get( null );
			if ( value instanceof Integer ) {
				rtn = ( Integer ) value;
			}
			else if ( value instanceof Short ) {
				rtn = new Integer( ( ( Short ) value ).intValue() );
			}
			else if ( value instanceof Long ) {
				if ( ( ( Long ) value ).longValue() <= Integer.MAX_VALUE ) {
					rtn = new Integer( ( ( Long ) value ).intValue() );
				}
			}
		}
		catch ( IllegalAccessException ignore ) {
		}
		return rtn;
	}
}
