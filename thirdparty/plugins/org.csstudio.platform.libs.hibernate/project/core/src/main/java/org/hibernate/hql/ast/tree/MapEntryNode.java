/*
 * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
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
 */
package org.hibernate.hql.ast.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import antlr.SemanticException;

import org.hibernate.HibernateException;
import org.hibernate.sql.SelectExpression;
import org.hibernate.sql.AliasGenerator;
import org.hibernate.sql.SelectFragment;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.hql.NameGenerator;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.transform.BasicTransformerAdapter;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.Type;
import org.hibernate.type.EntityType;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public class MapEntryNode extends AbstractMapComponentNode implements AggregatedSelectExpression {
	private static class LocalAliasGenerator implements AliasGenerator {
		private final int base;
		private int counter = 0;

		private LocalAliasGenerator(int base) {
			this.base = base;
		}

		public String generateAlias(String sqlExpression) {
			return NameGenerator.scalarName( base, counter++ );
		}
	}

	protected String expressionDescription() {
		return "entry(*)";
	}

	protected Type resolveType(QueryableCollection collectionPersister) {
		Type keyType = collectionPersister.getIndexType();
		Type valueType = collectionPersister.getElementType();
		types.add( keyType );
		types.add( valueType );
		mapEntryBuilder = new MapEntryBuilder();

		// an entry (as an aggregated select expression) does not have a type...
		return null;
	}

	protected String[] resolveColumns(QueryableCollection collectionPersister) {
		List selections = new ArrayList();
		determineKeySelectExpressions( collectionPersister, selections );
		determineValueSelectExpressions( collectionPersister, selections );

		String text = "";
		String[] columns = new String[selections.size()];
		for ( int i = 0; i < selections.size(); i++ ) {
			SelectExpression selectExpression = (SelectExpression) selections.get(i);
			text += ( ", " + selectExpression.getExpression() + " as " + selectExpression.getAlias() );
			columns[i] = selectExpression.getExpression();
		}

		text = text.substring( 2 ); //strip leading ", "
		setText( text );
		setResolved();
		return columns;
	}

	private void determineKeySelectExpressions(QueryableCollection collectionPersister, List selections) {
		AliasGenerator aliasGenerator = new LocalAliasGenerator( 0 );
		appendSelectExpressions( collectionPersister.getIndexColumnNames(), selections, aliasGenerator );
		Type keyType = collectionPersister.getIndexType();
		if ( keyType.isAssociationType() ) {
			EntityType entityType = (EntityType) keyType;
			Queryable keyEntityPersister = ( Queryable ) sfi().getEntityPersister(
					entityType.getAssociatedEntityName( sfi() )
			);
			SelectFragment fragment = keyEntityPersister.propertySelectFragmentFragment(
					collectionTableAlias(),
					null,
					false
			);
			appendSelectExpressions( fragment, selections, aliasGenerator );
		}
	}

	private void appendSelectExpressions(String[] columnNames, List selections, AliasGenerator aliasGenerator) {
		for ( int i = 0; i < columnNames.length; i++ ) {
			selections.add(
					new BasicSelectExpression(
							collectionTableAlias() + '.' + columnNames[i],
							aliasGenerator.generateAlias( columnNames[i] )
					)
			);
		}
	}

	private void appendSelectExpressions(SelectFragment fragment, List selections, AliasGenerator aliasGenerator) {
		Iterator itr = fragment.getColumns().iterator();
		while ( itr.hasNext() ) {
			final String column = (String) itr.next();
			selections.add(
					new BasicSelectExpression( column, aliasGenerator.generateAlias( column ) )
			);
		}
	}

	private void determineValueSelectExpressions(QueryableCollection collectionPersister, List selections) {
		AliasGenerator aliasGenerator = new LocalAliasGenerator( 1 );
		appendSelectExpressions( collectionPersister.getElementColumnNames(), selections, aliasGenerator );
		Type valueType = collectionPersister.getElementType();
		if ( valueType.isAssociationType() ) {
			EntityType valueEntityType = (EntityType) valueType;
			Queryable valueEntityPersister = ( Queryable ) sfi().getEntityPersister(
					valueEntityType.getAssociatedEntityName( sfi() )
			);
			SelectFragment fragment = valueEntityPersister.propertySelectFragmentFragment(
					elementTableAlias(),
					null,
					false
			);
			appendSelectExpressions( fragment, selections, aliasGenerator );
		}
	}

	private String collectionTableAlias() {
		return getFromElement().getCollectionTableAlias() != null
				? getFromElement().getCollectionTableAlias()
				: getFromElement().getTableAlias();
	}

	private String elementTableAlias() {
		return getFromElement().getTableAlias();
	}

	private static class BasicSelectExpression implements SelectExpression {
		private final String expression;
		private final String alias;

		private BasicSelectExpression(String expression, String alias) {
			this.expression = expression;
			this.alias = alias;
		}

		public String getExpression() {
			return expression;
		}

		public String getAlias() {
			return alias;
		}
	}

	public SessionFactoryImplementor sfi() {
		return getSessionFactoryHelper().getFactory();
	}

	public void setText(String s) {
		if ( isResolved() ) {
			return;
		}
		super.setText( s );
	}

	public void setScalarColumnText(int i) throws SemanticException {
	}

	public boolean isScalar() {
		// Constructors are always considered scalar results.
		return true;
	}

	private List types = new ArrayList(4); // size=4 to prevent resizing

	public List getAggregatedSelectionTypeList() {
		return types;
	}

	private static final String[] ALIASES = { null, null };

	public String[] getAggregatedAliases() {
		return ALIASES;
	}

	private MapEntryBuilder mapEntryBuilder;

	public ResultTransformer getResultTransformer() {
		return mapEntryBuilder;
	}

	private static class MapEntryBuilder extends BasicTransformerAdapter {
		public Object transformTuple(Object[] tuple, String[] aliases) {
			if ( tuple.length != 2 ) {
				throw new HibernateException( "Expecting exactly 2 tuples to transform into Map.Entry" );
			}
			return new EntryAdapter( tuple[0], tuple[1] );
		}
	}

	private static class EntryAdapter implements Map.Entry {
		private final Object key;
		private Object value;

		private EntryAdapter(Object key, Object value) {
			this.key = key;
			this.value = value;
		}

		public Object getValue() {
			return value;
		}

		public Object getKey() {
			return key;
		}

		public Object setValue(Object value) {
			Object old = this.value;
			this.value = value;
			return old;
		}

		public boolean equals(Object o) {
			// IMPL NOTE : nulls are considered equal for keys and values according to Map.Entry contract
			if ( this == o ) {
				return true;
			}
			if ( o == null || getClass() != o.getClass() ) {
				return false;
			}
			EntryAdapter that = ( EntryAdapter ) o;

			// make sure we have the same types...
			return ( key == null ? that.key == null : key.equals( that.key ) )
					&& ( value == null ? that.value == null : value.equals( that.value ) );

		}

		public int hashCode() {
			int keyHash = key == null ? 0 : key.hashCode();
			int valueHash = value == null ? 0 : value.hashCode();
			return keyHash ^ valueHash;
		}
	}
}
