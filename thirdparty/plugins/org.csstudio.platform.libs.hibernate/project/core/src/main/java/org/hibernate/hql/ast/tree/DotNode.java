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
package org.hibernate.hql.ast.tree;

import org.hibernate.QueryException;
import org.hibernate.engine.JoinSequence;
import org.hibernate.hql.CollectionProperties;
import org.hibernate.hql.antlr.SqlTokenTypes;
import org.hibernate.hql.ast.util.ASTUtil;
import org.hibernate.hql.ast.util.ColumnHelper;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.sql.JoinFragment;
import org.hibernate.type.CollectionType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;
import org.hibernate.util.StringHelper;

import antlr.SemanticException;
import antlr.collections.AST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a reference to a property or alias expression.  This should duplicate the relevant behaviors in
 * PathExpressionParser.
 *
 * @author Joshua Davis
 */
public class DotNode extends FromReferenceNode implements DisplayableNode, SelectExpression {

	///////////////////////////////////////////////////////////////////////////
	// USED ONLY FOR REGRESSION TESTING!!!!
	//
	// todo : obviously get rid of all this junk ;)
	///////////////////////////////////////////////////////////////////////////
	public static boolean useThetaStyleImplicitJoins = false;
	public static boolean REGRESSION_STYLE_JOIN_SUPPRESSION = false;
	public static interface IllegalCollectionDereferenceExceptionBuilder {
		public QueryException buildIllegalCollectionDereferenceException(String collectionPropertyName, FromReferenceNode lhs);
	}
	public static final IllegalCollectionDereferenceExceptionBuilder DEF_ILLEGAL_COLL_DEREF_EXCP_BUILDER = new IllegalCollectionDereferenceExceptionBuilder() {
		public QueryException buildIllegalCollectionDereferenceException(String propertyName, FromReferenceNode lhs) {
			String lhsPath = ASTUtil.getPathText( lhs );
			return new QueryException( "illegal attempt to dereference collection [" + lhsPath + "] with element property reference [" + propertyName + "]" );
		}
	};
	public static IllegalCollectionDereferenceExceptionBuilder ILLEGAL_COLL_DEREF_EXCP_BUILDER = DEF_ILLEGAL_COLL_DEREF_EXCP_BUILDER;
	///////////////////////////////////////////////////////////////////////////

	private static final Logger log = LoggerFactory.getLogger( DotNode.class );

	private static final int DEREF_UNKNOWN = 0;
	private static final int DEREF_ENTITY = 1;
	private static final int DEREF_COMPONENT = 2;
	private static final int DEREF_COLLECTION = 3;
	private static final int DEREF_PRIMITIVE = 4;
	private static final int DEREF_IDENTIFIER = 5;
	private static final int DEREF_JAVA_CONSTANT = 6;

	/**
	 * The identifier that is the name of the property.
	 */
	private String propertyName;
	/**
	 * The full path, to the root alias of this dot node.
	 */
	private String path;
	/**
	 * The unresolved property path relative to this dot node.
	 */
	private String propertyPath;

	/**
	 * The column names that this resolves to.
	 */
	private String[] columns;

	/**
	 * The type of join to create.   Default is an inner join.
	 */
	private int joinType = JoinFragment.INNER_JOIN;

	/**
	 * Fetch join or not.
	 */
	private boolean fetch = false;

	/**
	 * The type of dereference that hapened (DEREF_xxx).
	 */
	private int dereferenceType = DEREF_UNKNOWN;

	private FromElement impliedJoin;

	/**
	 * Sets the join type for this '.' node structure.
	 *
	 * @param joinType The type of join to use.
	 * @see JoinFragment
	 */
	public void setJoinType(int joinType) {
		this.joinType = joinType;
	}

	private String[] getColumns() throws QueryException {
		if ( columns == null ) {
			// Use the table fromElement and the property name to get the array of column names.
			String tableAlias = getLhs().getFromElement().getTableAlias();
			columns = getFromElement().toColumns( tableAlias, propertyPath, false );
		}
		return columns;
	}

	public String getDisplayText() {
		StringBuffer buf = new StringBuffer();
		FromElement fromElement = getFromElement();
		buf.append( "{propertyName=" ).append( propertyName );
		buf.append( ",dereferenceType=" ).append( getWalker().getASTPrinter().getTokenTypeName( dereferenceType ) );
		buf.append( ",propertyPath=" ).append( propertyPath );
		buf.append( ",path=" ).append( getPath() );
		if ( fromElement != null ) {
			buf.append( ",tableAlias=" ).append( fromElement.getTableAlias() );
			buf.append( ",className=" ).append( fromElement.getClassName() );
			buf.append( ",classAlias=" ).append( fromElement.getClassAlias() );
		}
		else {
			buf.append( ",no from element" );
		}
		buf.append( '}' );
		return buf.toString();
	}

	/**
	 * Resolves the left hand side of the DOT.
	 *
	 * @throws SemanticException
	 */
	public void resolveFirstChild() throws SemanticException {
		FromReferenceNode lhs = ( FromReferenceNode ) getFirstChild();
		SqlNode property = ( SqlNode ) lhs.getNextSibling();

		// Set the attributes of the property reference expression.
		String propName = property.getText();
		propertyName = propName;
		// If the uresolved property path isn't set yet, just use the property name.
		if ( propertyPath == null ) {
			propertyPath = propName;
		}
		// Resolve the LHS fully, generate implicit joins.  Pass in the property name so that the resolver can
		// discover foreign key (id) properties.
		lhs.resolve( true, true, null, this );
		setFromElement( lhs.getFromElement() );			// The 'from element' that the property is in.

		checkSubclassOrSuperclassPropertyReference( lhs, propName );
	}

	public void resolveInFunctionCall(boolean generateJoin, boolean implicitJoin) throws SemanticException {
		if ( isResolved() ) {
			return;
		}
		Type propertyType = prepareLhs();			// Prepare the left hand side and get the data type.
		if ( propertyType!=null && propertyType.isCollectionType() ) {
			resolveIndex(null);
		}
		else {
			resolveFirstChild();
			super.resolve(generateJoin, implicitJoin);
		}
	}


	public void resolveIndex(AST parent) throws SemanticException {
		if ( isResolved() ) {
			return;
		}
		Type propertyType = prepareLhs();			// Prepare the left hand side and get the data type.
		dereferenceCollection( ( CollectionType ) propertyType, true, true, null, parent );
	}

	public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent)
	throws SemanticException {
		// If this dot has already been resolved, stop now.
		if ( isResolved() ) {
			return;
		}
		Type propertyType = prepareLhs(); // Prepare the left hand side and get the data type.

		// If there is no data type for this node, and we're at the end of the path (top most dot node), then
		// this might be a Java constant.
		if ( propertyType == null ) {
			if ( parent == null ) {
				getWalker().getLiteralProcessor().lookupConstant( this );
			}
			// If the propertyType is null and there isn't a parent, just
			// stop now... there was a problem resolving the node anyway.
			return;
		}

		if ( propertyType.isComponentType() ) {
			// The property is a component...
			checkLhsIsNotCollection();
			dereferenceComponent( parent );
			initText();
		}
		else if ( propertyType.isEntityType() ) {
			// The property is another class..
			checkLhsIsNotCollection();
			dereferenceEntity( ( EntityType ) propertyType, implicitJoin, classAlias, generateJoin, parent );
			initText();
		}
		else if ( propertyType.isCollectionType() ) {
			// The property is a collection...
			checkLhsIsNotCollection();
			dereferenceCollection( ( CollectionType ) propertyType, implicitJoin, false, classAlias, parent );
		}
		else {
			// Otherwise, this is a primitive type.
			if ( ! CollectionProperties.isAnyCollectionProperty( propertyName ) ) {
				checkLhsIsNotCollection();
			}
			dereferenceType = DEREF_PRIMITIVE;
			initText();
		}
		setResolved();
	}

	private void initText() {
		String[] cols = getColumns();
		String text = StringHelper.join( ", ", cols );
		if ( cols.length > 1 && getWalker().isComparativeExpressionClause() ) {
			text = "(" + text + ")";
		}
		setText( text );
	}

	private Type prepareLhs() throws SemanticException {
		FromReferenceNode lhs = getLhs();
		lhs.prepareForDot( propertyName );
		return getDataType();
	}

	private void dereferenceCollection(CollectionType collectionType, boolean implicitJoin, boolean indexed, String classAlias, AST parent)
	throws SemanticException {

		dereferenceType = DEREF_COLLECTION;
		String role = collectionType.getRole();

		//foo.bars.size (also handles deprecated stuff like foo.bars.maxelement for backwardness)
		boolean isSizeProperty = getNextSibling()!=null &&
			CollectionProperties.isAnyCollectionProperty( getNextSibling().getText() );

		if ( isSizeProperty ) indexed = true; //yuck!

		QueryableCollection queryableCollection = getSessionFactoryHelper().requireQueryableCollection( role );
		String propName = getPath();
		FromClause currentFromClause = getWalker().getCurrentFromClause();

		if ( getWalker().getStatementType() != SqlTokenTypes.SELECT && indexed && classAlias == null ) {
			// should indicate that we are processing an INSERT/UPDATE/DELETE
			// query with a subquery implied via a collection property
			// function. Here, we need to use the table name itself as the
			// qualification alias.
			// TODO : verify this works for all databases...
			// TODO : is this also the case in non-"indexed" scenarios?
			String alias = getLhs().getFromElement().getQueryable().getTableName();
			columns = getFromElement().toColumns( alias, propertyPath, false, true );
		}

		//We do not look for an existing join on the same path, because
		//it makes sense to join twice on the same collection role
		FromElementFactory factory = new FromElementFactory(
		        currentFromClause,
		        getLhs().getFromElement(),
		        propName,
				classAlias,
		        getColumns(),
		        implicitJoin
		);
		FromElement elem = factory.createCollection( queryableCollection, role, joinType, fetch, indexed );

		if ( log.isDebugEnabled() ) {
			log.debug( "dereferenceCollection() : Created new FROM element for " + propName + " : " + elem );
		}

		setImpliedJoin( elem );
		setFromElement( elem );	// This 'dot' expression now refers to the resulting from element.

		if ( isSizeProperty ) {
			elem.setText("");
			elem.setUseWhereFragment(false);
		}

		if ( !implicitJoin ) {
			EntityPersister entityPersister = elem.getEntityPersister();
			if ( entityPersister != null ) {
				getWalker().addQuerySpaces( entityPersister.getQuerySpaces() );
			}
		}
		getWalker().addQuerySpaces( queryableCollection.getCollectionSpaces() );	// Always add the collection's query spaces.
	}

	private void dereferenceEntity(EntityType entityType, boolean implicitJoin, String classAlias, boolean generateJoin, AST parent) throws SemanticException {
		checkForCorrelatedSubquery( "dereferenceEntity" );
		// three general cases we check here as to whether to render a physical SQL join:
		// 1) is our parent a DotNode as well?  If so, our property reference is
		// 		being further de-referenced...
		// 2) is this a DML statement
		// 3) we were asked to generate any needed joins (generateJoins==true) *OR*
		//		we are currently processing a select or from clause
		// (an additional check is the REGRESSION_STYLE_JOIN_SUPPRESSION check solely intended for the test suite)
		//
		// The REGRESSION_STYLE_JOIN_SUPPRESSION is an additional check
		// intended solely for use within the test suite.  This forces the
		// implicit join resolution to behave more like the classic parser.
		// The underlying issue is that classic translator is simply wrong
		// about its decisions on whether or not to render an implicit join
		// into a physical SQL join in a lot of cases.  The piece it generally
		// tends to miss is that INNER joins effect the results by further
		// restricting the data set!  A particular manifestation of this is
		// the fact that the classic translator will skip the physical join
		// for ToOne implicit joins *if the query is shallow*; the result
		// being that Query.list() and Query.iterate() could return
		// different number of results!
		DotNode parentAsDotNode = null;
		String property = propertyName;
		final boolean joinIsNeeded;

		if ( isDotNode( parent ) ) {
			// our parent is another dot node, meaning we are being further dereferenced.
			// thus we need to generate a join unless the parent refers to the associated
			// entity's PK (because 'our' table would know the FK).
			parentAsDotNode = ( DotNode ) parent;
			property = parentAsDotNode.propertyName;
			joinIsNeeded = generateJoin && !isReferenceToPrimaryKey( parentAsDotNode.propertyName, entityType );
		}
		else if ( ! getWalker().isSelectStatement() ) {
			// in non-select queries, the only time we should need to join is if we are in a subquery from clause
			joinIsNeeded = getWalker().getCurrentStatementType() == SqlTokenTypes.SELECT && getWalker().isInFrom();
		}
		else if ( REGRESSION_STYLE_JOIN_SUPPRESSION ) {
			// this is the regression style determination which matches the logic of the classic translator
			joinIsNeeded = generateJoin && ( !getWalker().isInSelect() || !getWalker().isShallowQuery() );
		}
		else {
			joinIsNeeded = generateJoin || ( getWalker().isInSelect() || getWalker().isInFrom() );
		}

		if ( joinIsNeeded ) {
			dereferenceEntityJoin( classAlias, entityType, implicitJoin, parent );
		}
		else {
			dereferenceEntityIdentifier( property, parentAsDotNode );
		}

	}

	private boolean isDotNode(AST n) {
		return n != null && n.getType() == SqlTokenTypes.DOT;
	}

	private void dereferenceEntityJoin(String classAlias, EntityType propertyType, boolean impliedJoin, AST parent)
	throws SemanticException {
		dereferenceType = DEREF_ENTITY;
		if ( log.isDebugEnabled() ) {
			log.debug( "dereferenceEntityJoin() : generating join for " + propertyName + " in "
					+ getFromElement().getClassName() + " "
					+ ( ( classAlias == null ) ? "{no alias}" : "(" + classAlias + ")" )
					+ " parent = " + ASTUtil.getDebugString( parent )
			);
		}
		// Create a new FROM node for the referenced class.
		String associatedEntityName = propertyType.getAssociatedEntityName();
		String tableAlias = getAliasGenerator().createName( associatedEntityName );

		String[] joinColumns = getColumns();
		String joinPath = getPath();

		if ( impliedJoin && getWalker().isInFrom() ) {
			joinType = getWalker().getImpliedJoinType();
		}

		FromClause currentFromClause = getWalker().getCurrentFromClause();
		FromElement elem = currentFromClause.findJoinByPath( joinPath );

///////////////////////////////////////////////////////////////////////////////
//
// This is the piece which recognizes the condition where an implicit join path
// resolved earlier in a correlated subquery is now being referenced in the
// outer query.  For 3.0final, we just let this generate a second join (which
// is exactly how the old parser handles this).  Eventually we need to add this
// logic back in and complete the logic in FromClause.promoteJoin; however,
// FromClause.promoteJoin has its own difficulties (see the comments in
// FromClause.promoteJoin).
//
//		if ( elem == null ) {
//			// see if this joinPath has been used in a "child" FromClause, and if so
//			// promote that element to the outer query
//			FromClause currentNodeOwner = getFromElement().getFromClause();
//			FromClause currentJoinOwner = currentNodeOwner.locateChildFromClauseWithJoinByPath( joinPath );
//			if ( currentJoinOwner != null && currentNodeOwner != currentJoinOwner ) {
//				elem = currentJoinOwner.findJoinByPathLocal( joinPath );
//				if ( elem != null ) {
//					currentFromClause.promoteJoin( elem );
//					// EARLY EXIT!!!
//					return;
//				}
//			}
//		}
//
///////////////////////////////////////////////////////////////////////////////

		boolean found = elem != null;
		// even though we might find a pre-existing element by join path, for FromElements originating in a from-clause
		// we should only ever use the found element if the aliases match (null != null here).  Implied joins are
		// always (?) ok to reuse.
		boolean useFoundFromElement = found && ( elem.isImplied() || areSame( classAlias, elem.getClassAlias() ) );

		if ( ! useFoundFromElement ) {
			// If this is an implied join in a from element, then use the impled join type which is part of the
			// tree parser's state (set by the gramamar actions).
			JoinSequence joinSequence = getSessionFactoryHelper()
				.createJoinSequence( impliedJoin, propertyType, tableAlias, joinType, joinColumns );

			// If the lhs of the join is a "component join", we need to go back to the
			// first non-component-join as the origin to properly link aliases and
			// join columns
			FromElement lhsFromElement = getLhs().getFromElement();
			while ( lhsFromElement != null &&  ComponentJoin.class.isInstance( lhsFromElement ) ) {
				lhsFromElement = lhsFromElement.getOrigin();
			}
			if ( lhsFromElement == null ) {
				throw new QueryException( "Unable to locate appropriate lhs" );
			}

			FromElementFactory factory = new FromElementFactory(
			        currentFromClause,
					lhsFromElement,
					joinPath,
					classAlias,
					joinColumns,
					impliedJoin
			);
			elem = factory.createEntityJoin(
					associatedEntityName,
					tableAlias,
					joinSequence,
					fetch,
					getWalker().isInFrom(),
					propertyType
			);
		}
		else {
			// NOTE : addDuplicateAlias() already performs nullness checks on the alias.
			currentFromClause.addDuplicateAlias( classAlias, elem );
		}
		setImpliedJoin( elem );
		getWalker().addQuerySpaces( elem.getEntityPersister().getQuerySpaces() );
		setFromElement( elem );	// This 'dot' expression now refers to the resulting from element.
	}

	private boolean areSame(String alias1, String alias2) {
		// again, null != null here
		return !StringHelper.isEmpty( alias1 ) && !StringHelper.isEmpty( alias2 ) && alias1.equals( alias2 );
	}

	private void setImpliedJoin(FromElement elem) {
		this.impliedJoin = elem;
		if ( getFirstChild().getType() == SqlTokenTypes.DOT ) {
			DotNode dotLhs = ( DotNode ) getFirstChild();
			if ( dotLhs.getImpliedJoin() != null ) {
				this.impliedJoin = dotLhs.getImpliedJoin();
			}
		}
	}

	public FromElement getImpliedJoin() {
		return impliedJoin;
	}

	/**
	 * Is the given property name a reference to the primary key of the associated
	 * entity construed by the given entity type?
	 * <p/>
	 * For example, consider a fragment like order.customer.id
	 * (where order is a from-element alias).  Here, we'd have:
	 * propertyName = "id" AND
	 * owningType = ManyToOneType(Customer)
	 * and are being asked to determine whether "customer.id" is a reference
	 * to customer's PK...
	 *
	 * @param propertyName The name of the property to check.
	 * @param owningType The type represeting the entity "owning" the property
	 * @return True if propertyName references the entity's (owningType->associatedEntity)
	 * primary key; false otherwise.
	 */
	private boolean isReferenceToPrimaryKey(String propertyName, EntityType owningType) {
		EntityPersister persister = getSessionFactoryHelper()
				.getFactory()
				.getEntityPersister( owningType.getAssociatedEntityName() );
		if ( persister.getEntityMetamodel().hasNonIdentifierPropertyNamedId() ) {
			// only the identifier property field name can be a reference to the associated entity's PK...
			return propertyName.equals( persister.getIdentifierPropertyName() ) && owningType.isReferenceToPrimaryKey();
		}
		else {
			// here, we have two possibilities:
			// 		1) the property-name matches the explicitly identifier property name
			//		2) the property-name matches the implicit 'id' property name
			if ( EntityPersister.ENTITY_ID.equals( propertyName ) ) {
				// the referenced node text is the special 'id'
				return owningType.isReferenceToPrimaryKey();
			}
			else {
				String keyPropertyName = getSessionFactoryHelper().getIdentifierOrUniqueKeyPropertyName( owningType );
				return keyPropertyName != null && keyPropertyName.equals( propertyName ) && owningType.isReferenceToPrimaryKey();
			}
		}
	}

	private void checkForCorrelatedSubquery(String methodName) {
		if ( isCorrelatedSubselect() ) {
			if ( log.isDebugEnabled() ) {
				log.debug( methodName + "() : correlated subquery" );
			}
		}
	}

	private boolean isCorrelatedSubselect() {
		return getWalker().isSubQuery() &&
			getFromElement().getFromClause() != getWalker().getCurrentFromClause();
	}

	private void checkLhsIsNotCollection() throws SemanticException {
		if ( getLhs().getDataType() != null && getLhs().getDataType().isCollectionType() ) {
			throw ILLEGAL_COLL_DEREF_EXCP_BUILDER.buildIllegalCollectionDereferenceException( propertyName, getLhs() );
		}
	}
	private void dereferenceComponent(AST parent) {
		dereferenceType = DEREF_COMPONENT;
		setPropertyNameAndPath( parent );
	}

	private void dereferenceEntityIdentifier(String propertyName, DotNode dotParent) {
		// special shortcut for id properties, skip the join!
		// this must only occur at the _end_ of a path expression
		if ( log.isDebugEnabled() ) {
			log.debug( "dereferenceShortcut() : property " +
				propertyName + " in " + getFromElement().getClassName() +
				" does not require a join." );
		}

		initText();
		setPropertyNameAndPath( dotParent ); // Set the unresolved path in this node and the parent.
		// Set the text for the parent.
		if ( dotParent != null ) {
			dotParent.dereferenceType = DEREF_IDENTIFIER;
			dotParent.setText( getText() );
			dotParent.columns = getColumns();
		}
	}

	private void setPropertyNameAndPath(AST parent) {
		if ( isDotNode( parent ) ) {
			DotNode dotNode = ( DotNode ) parent;
			AST lhs = dotNode.getFirstChild();
			AST rhs = lhs.getNextSibling();
			propertyName = rhs.getText();
			propertyPath = propertyPath + "." + propertyName; // Append the new property name onto the unresolved path.
			dotNode.propertyPath = propertyPath;
			if ( log.isDebugEnabled() ) {
				log.debug( "Unresolved property path is now '" + dotNode.propertyPath + "'" );
			}
		}
		else {
			if ( log.isDebugEnabled() ) {
				log.debug( "terminal propertyPath = [" + propertyPath + "]" );
			}
		}
	}

	public Type getDataType() {
		if ( super.getDataType() == null ) {
			FromElement fromElement = getLhs().getFromElement();
			if ( fromElement == null ) {
				return null;
			}
			// If the lhs is a collection, use CollectionPropertyMapping
			Type propertyType = fromElement.getPropertyType( propertyName, propertyPath );
			if ( log.isDebugEnabled() ) {
				log.debug( "getDataType() : " + propertyPath + " -> " + propertyType );
			}
			super.setDataType( propertyType );
		}
		return super.getDataType();
	}

	public void setPropertyPath(String propertyPath) {
		this.propertyPath = propertyPath;
	}

	public String getPropertyPath() {
		return propertyPath;
	}

	public FromReferenceNode getLhs() {
		FromReferenceNode lhs = ( ( FromReferenceNode ) getFirstChild() );
		if ( lhs == null ) {
			throw new IllegalStateException( "DOT node with no left-hand-side!" );
		}
		return lhs;
	}

	/**
	 * Returns the full path of the node.
	 *
	 * @return the full path of the node.
	 */
	public String getPath() {
		if ( path == null ) {
			FromReferenceNode lhs = getLhs();
			if ( lhs == null ) {
				path = getText();
			}
			else {
				SqlNode rhs = ( SqlNode ) lhs.getNextSibling();
				path = lhs.getPath() + "." + rhs.getOriginalText();
			}
		}
		return path;
	}

	public void setFetch(boolean fetch) {
		this.fetch = fetch;
	}

	public void setScalarColumnText(int i) throws SemanticException {
		String[] sqlColumns = getColumns();
		ColumnHelper.generateScalarColumns( this, sqlColumns, i );
	}

	/**
	 * Special method to resolve expressions in the SELECT list.
	 *
	 * @throws SemanticException if this cannot be resolved.
	 */
	public void resolveSelectExpression() throws SemanticException {
		if ( getWalker().isShallowQuery() || getWalker().getCurrentFromClause().isSubQuery() ) {
			resolve(false, true);
		}
		else {
			resolve(true, false);
			Type type = getDataType();
			if ( type.isEntityType() ) {
				FromElement fromElement = getFromElement();
				fromElement.setIncludeSubclasses( true ); // Tell the destination fromElement to 'includeSubclasses'.
				if ( useThetaStyleImplicitJoins ) {
					fromElement.getJoinSequence().setUseThetaStyle( true );	// Use theta style (for regression)
					// Move the node up, after the origin node.
					FromElement origin = fromElement.getOrigin();
					if ( origin != null ) {
						ASTUtil.makeSiblingOfParent( origin, fromElement );
					}
				}
			}
		}

		FromReferenceNode lhs = getLhs();
		while ( lhs != null ) {
			checkSubclassOrSuperclassPropertyReference( lhs, lhs.getNextSibling().getText() );
			lhs = ( FromReferenceNode ) lhs.getFirstChild();
		}
	}

	public void setResolvedConstant(String text) {
		path = text;
		dereferenceType = DEREF_JAVA_CONSTANT;
		setResolved(); // Don't resolve the node again.
	}

	private boolean checkSubclassOrSuperclassPropertyReference(FromReferenceNode lhs, String propertyName) {
		if ( lhs != null && !( lhs instanceof IndexNode ) ) {
			final FromElement source = lhs.getFromElement();
			if ( source != null ) {
				source.handlePropertyBeingDereferenced( lhs.getDataType(), propertyName );
			}
		}
		return false;
	}
}
