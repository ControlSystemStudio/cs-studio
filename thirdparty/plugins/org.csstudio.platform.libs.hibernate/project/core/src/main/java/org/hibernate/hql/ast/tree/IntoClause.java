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

import java.util.ArrayList;
import java.util.List;
import java.sql.Types;

import org.hibernate.QueryException;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.type.Type;
import org.hibernate.util.ArrayHelper;

import antlr.collections.AST;

/**
 * Represents an entity referenced in the INTO clause of an HQL
 * INSERT statement.
 *
 * @author Steve Ebersole
 */
public class IntoClause extends HqlSqlWalkerNode implements DisplayableNode {

	private Queryable persister;
	private String columnSpec = "";
	private Type[] types;

	private boolean discriminated;
	private boolean explicitIdInsertion;
	private boolean explicitVersionInsertion;


	public void initialize(Queryable persister) {
		if ( persister.isAbstract() ) {
			throw new QueryException( "cannot insert into abstract class (no table)" );
		}
		this.persister = persister;
		initializeColumns();

		if ( getWalker().getSessionFactoryHelper().hasPhysicalDiscriminatorColumn( persister ) ) {
			discriminated = true;
			columnSpec += ", " + persister.getDiscriminatorColumnName();
		}

		resetText();
	}

	private void resetText() {
		setText( "into " + getTableName() + " ( " + columnSpec + " )" );
	}

	public String getTableName() {
		return persister.getSubclassTableName( 0 );
	}

	public Queryable getQueryable() {
		return persister;
	}

	public String getEntityName() {
		return persister.getEntityName();
	}

	public Type[] getInsertionTypes() {
		return types;
	}

	public boolean isDiscriminated() {
		return discriminated;
	}

	public boolean isExplicitIdInsertion() {
		return explicitIdInsertion;
	}

	public boolean isExplicitVersionInsertion() {
		return explicitVersionInsertion;
	}

	public void prependIdColumnSpec() {
		columnSpec = persister.getIdentifierColumnNames()[0] + ", " + columnSpec;
		resetText();
	}

	public void prependVersionColumnSpec() {
		columnSpec = persister.getPropertyColumnNames( persister.getVersionProperty() )[0] + ", " + columnSpec;
		resetText();
	}

	public void validateTypes(SelectClause selectClause) throws QueryException {
		Type[] selectTypes = selectClause.getQueryReturnTypes();
		if ( selectTypes.length != types.length ) {
			throw new QueryException( "number of select types did not match those for insert" );
		}

		for ( int i = 0; i < types.length; i++ ) {
			if ( !areCompatible( types[i], selectTypes[i] ) ) {
				throw new QueryException(
				        "insertion type [" + types[i] + "] and selection type [" +
				        selectTypes[i] + "] at position " + i + " are not compatible"
				);
			}
		}

		// otherwise, everything ok.
	}

	/**
	 * Returns additional display text for the AST node.
	 *
	 * @return String - The additional display text.
	 */
	public String getDisplayText() {
		StringBuffer buf = new StringBuffer();
		buf.append( "IntoClause{" );
		buf.append( "entityName=" ).append( getEntityName() );
		buf.append( ",tableName=" ).append( getTableName() );
		buf.append( ",columns={" ).append( columnSpec ).append( "}" );
		buf.append( "}" );
		return buf.toString();
	}

	private void initializeColumns() {
		AST propertySpec = getFirstChild();
		List types = new ArrayList();
		visitPropertySpecNodes( propertySpec.getFirstChild(), types );
		this.types = ArrayHelper.toTypeArray( types );
		columnSpec = columnSpec.substring( 0, columnSpec.length() - 2 );
	}

	private void visitPropertySpecNodes(AST propertyNode, List types) {
		if ( propertyNode == null ) {
			return;
		}
		// TODO : we really need to be able to deal with component paths here also;
		// this is difficult because the hql-sql grammar expects all those node types
		// to be FromReferenceNodes.  One potential fix here would be to convert the
		// IntoClause to just use a FromClause/FromElement combo (as a child of the
		// InsertStatement) and move all this logic into the InsertStatement.  That's
		// probably the easiest approach (read: least amount of changes to the grammar
		// and code), but just doesn't feel right as then an insert would contain
		// 2 from-clauses
		String name = propertyNode.getText();
		if ( isSuperclassProperty( name ) ) {
			throw new QueryException( "INSERT statements cannot refer to superclass/joined properties [" + name + "]" );
		}

		if ( name.equals( persister.getIdentifierPropertyName() ) ) {
			explicitIdInsertion = true;
		}

		if ( persister.isVersioned() ) {
			if ( name.equals( persister.getPropertyNames()[ persister.getVersionProperty() ] ) ) {
				explicitVersionInsertion = true;
			}
		}

		String[] columnNames = persister.toColumns( name );
		renderColumns( columnNames );
		types.add( persister.toType( name ) );

		// visit width-first, then depth
		visitPropertySpecNodes( propertyNode.getNextSibling(), types );
		visitPropertySpecNodes( propertyNode.getFirstChild(), types );
	}

	private void renderColumns(String[] columnNames) {
		for ( int i = 0; i < columnNames.length; i++ ) {
			columnSpec += columnNames[i] + ", ";
		}
	}

	private boolean isSuperclassProperty(String propertyName) {
		// really there are two situations where it should be ok to allow the insertion
		// into properties defined on a superclass:
		//      1) union-subclass with an abstract root entity
		//      2) discrim-subclass
		//
		// #1 is handled already because of the fact that
		// UnionSubclassPersister alreay always returns 0
		// for this call...
		//
		// we may want to disallow it for discrim-subclass just for
		// consistency-sake (currently does not work anyway)...
		return persister.getSubclassPropertyTableNumber( propertyName ) != 0;
	}

	/**
	 * Determine whether the two types are "assignment compatible".
	 *
	 * @param target The type defined in the into-clause.
	 * @param source The type defined in the select clause.
	 * @return True if they are assignment compatible.
	 */
	private boolean areCompatible(Type target, Type source) {
		if ( target.equals( source ) ) {
			// if the types report logical equivalence, return true...
			return true;
		}

		// otherwise, doAfterTransactionCompletion a "deep equivalence" check...

		if ( !target.getReturnedClass().isAssignableFrom( source.getReturnedClass() ) ) {
			return false;
		}

		int[] targetDatatypes = target.sqlTypes( getSessionFactoryHelper().getFactory() );
		int[] sourceDatatypes = source.sqlTypes( getSessionFactoryHelper().getFactory() );

		if ( targetDatatypes.length != sourceDatatypes.length ) {
			return false;
		}

		for ( int i = 0; i < targetDatatypes.length; i++ ) {
			if ( !areSqlTypesCompatible( targetDatatypes[i], sourceDatatypes[i] ) ) {
				return false;
			}
		}

		return true;
	}

	private boolean areSqlTypesCompatible(int target, int source) {
		switch ( target ) {
			case Types.TIMESTAMP:
				return source == Types.DATE || source == Types.TIME || source == Types.TIMESTAMP;
			case Types.DATE:
				return source == Types.DATE || source == Types.TIMESTAMP;
			case Types.TIME:
				return source == Types.TIME || source == Types.TIMESTAMP;
			default:
				return target == source;
		}
	}
}
