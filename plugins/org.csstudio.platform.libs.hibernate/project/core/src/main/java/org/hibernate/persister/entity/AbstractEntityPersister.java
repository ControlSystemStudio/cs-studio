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
package org.hibernate.persister.entity;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.hibernate.AssertionFailure;
import org.hibernate.EntityMode;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.MappingException;
import org.hibernate.QueryException;
import org.hibernate.StaleObjectStateException;
import org.hibernate.StaleStateException;
import org.hibernate.LockOptions;
import org.hibernate.cache.CacheKey;
import org.hibernate.cache.access.EntityRegionAccessStrategy;
import org.hibernate.cache.entry.CacheEntry;
import org.hibernate.cache.entry.CacheEntryStructure;
import org.hibernate.cache.entry.StructuredCacheEntry;
import org.hibernate.cache.entry.UnstructuredCacheEntry;
import org.hibernate.dialect.lock.LockingStrategy;
import org.hibernate.engine.CascadeStyle;
import org.hibernate.engine.CascadingAction;
import org.hibernate.engine.EntityEntry;
import org.hibernate.engine.EntityKey;
import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
import org.hibernate.engine.LoadQueryInfluencers;
import org.hibernate.engine.Mapping;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.engine.Status;
import org.hibernate.engine.ValueInclusion;
import org.hibernate.engine.Versioning;
import org.hibernate.exception.JDBCExceptionHelper;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.PostInsertIdentifierGenerator;
import org.hibernate.id.PostInsertIdentityPersister;
import org.hibernate.id.insert.Binder;
import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
import org.hibernate.intercept.FieldInterceptionHelper;
import org.hibernate.intercept.FieldInterceptor;
import org.hibernate.intercept.LazyPropertyInitializer;
import org.hibernate.jdbc.Expectation;
import org.hibernate.jdbc.Expectations;
import org.hibernate.jdbc.TooManyRowsAffectedException;
import org.hibernate.loader.entity.BatchingEntityLoader;
import org.hibernate.loader.entity.CascadeEntityLoader;
import org.hibernate.loader.entity.EntityLoader;
import org.hibernate.loader.entity.UniqueEntityLoader;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Selectable;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.DiscriminatorMetadata;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.property.BackrefPropertyAccessor;
import org.hibernate.sql.Alias;
import org.hibernate.sql.Delete;
import org.hibernate.sql.Insert;
import org.hibernate.sql.JoinFragment;
import org.hibernate.sql.Select;
import org.hibernate.sql.SelectFragment;
import org.hibernate.sql.SimpleSelect;
import org.hibernate.sql.Template;
import org.hibernate.sql.Update;
import org.hibernate.tuple.Tuplizer;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.tuple.entity.EntityTuplizer;
import org.hibernate.type.AbstractComponentType;
import org.hibernate.type.AssociationType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;
import org.hibernate.type.TypeFactory;
import org.hibernate.type.VersionType;
import org.hibernate.util.ArrayHelper;
import org.hibernate.util.FilterHelper;
import org.hibernate.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic functionality for persisting an entity via JDBC
 * through either generated or custom SQL
 *
 * @author Gavin King
 */
public abstract class AbstractEntityPersister
		implements OuterJoinLoadable, Queryable, ClassMetadata, UniqueKeyLoadable,
		SQLLoadable, LazyPropertyInitializer, PostInsertIdentityPersister, Lockable {

	private static final Logger log = LoggerFactory.getLogger( AbstractEntityPersister.class );

	public static final String ENTITY_CLASS = "class";

	// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private final SessionFactoryImplementor factory;
	private final EntityRegionAccessStrategy cacheAccessStrategy;
	private final boolean isLazyPropertiesCacheable;
	private final CacheEntryStructure cacheEntryStructure;
	private final EntityMetamodel entityMetamodel;
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private final String[] rootTableKeyColumnNames;
	private final String[] rootTableKeyColumnReaders;
	private final String[] rootTableKeyColumnReaderTemplates;
	private final String[] identifierAliases;
	private final int identifierColumnSpan;
	private final String versionColumnName;
	private final boolean hasFormulaProperties;
	private final int batchSize;
	private final boolean hasSubselectLoadableCollections;
	protected final String rowIdName;

	private final Set lazyProperties;

	// The optional SQL string defined in the where attribute
	private final String sqlWhereString;
	private final String sqlWhereStringTemplate;

	//information about properties of this class,
	//including inherited properties
	//(only really needed for updatable/insertable properties)
	private final int[] propertyColumnSpans;
	private final String[] propertySubclassNames;
	private final String[][] propertyColumnAliases;
	private final String[][] propertyColumnNames;
	private final String[][] propertyColumnFormulaTemplates;
	private final String[][] propertyColumnReaderTemplates;
	private final String[][] propertyColumnWriters;
	private final boolean[][] propertyColumnUpdateable;
	private final boolean[][] propertyColumnInsertable;
	private final boolean[] propertyUniqueness;
	private final boolean[] propertySelectable;

	//information about lazy properties of this class
	private final String[] lazyPropertyNames;
	private final int[] lazyPropertyNumbers;
	private final Type[] lazyPropertyTypes;
	private final String[][] lazyPropertyColumnAliases;

	//information about all properties in class hierarchy
	private final String[] subclassPropertyNameClosure;
	private final String[] subclassPropertySubclassNameClosure;
	private final Type[] subclassPropertyTypeClosure;
	private final String[][] subclassPropertyFormulaTemplateClosure;
	private final String[][] subclassPropertyColumnNameClosure;
	private final String[][] subclassPropertyColumnReaderClosure;
	private final String[][] subclassPropertyColumnReaderTemplateClosure;
	private final FetchMode[] subclassPropertyFetchModeClosure;
	private final boolean[] subclassPropertyNullabilityClosure;
	private final boolean[] propertyDefinedOnSubclass;
	private final int[][] subclassPropertyColumnNumberClosure;
	private final int[][] subclassPropertyFormulaNumberClosure;
	private final CascadeStyle[] subclassPropertyCascadeStyleClosure;

	//information about all columns/formulas in class hierarchy
	private final String[] subclassColumnClosure;
	private final boolean[] subclassColumnLazyClosure;
	private final String[] subclassColumnAliasClosure;
	private final boolean[] subclassColumnSelectableClosure;
	private final String[] subclassColumnReaderTemplateClosure;
	private final String[] subclassFormulaClosure;
	private final String[] subclassFormulaTemplateClosure;
	private final String[] subclassFormulaAliasClosure;
	private final boolean[] subclassFormulaLazyClosure;

	// dynamic filters attached to the class-level
	private final FilterHelper filterHelper;

	private final Set affectingFetchProfileNames = new HashSet();

	private final Map uniqueKeyLoaders = new HashMap();
	private final Map lockers = new HashMap();
	private final Map loaders = new HashMap();

	// SQL strings
	private String sqlVersionSelectString;
	private String sqlSnapshotSelectString;
	private String sqlLazySelectString;

	private String sqlIdentityInsertString;
	private String sqlUpdateByRowIdString;
	private String sqlLazyUpdateByRowIdString;

	private String[] sqlDeleteStrings;
	private String[] sqlInsertStrings;
	private String[] sqlUpdateStrings;
	private String[] sqlLazyUpdateStrings;

	private String sqlInsertGeneratedValuesSelectString;
	private String sqlUpdateGeneratedValuesSelectString;

	//Custom SQL (would be better if these were private)
	protected boolean[] insertCallable;
	protected boolean[] updateCallable;
	protected boolean[] deleteCallable;
	protected String[] customSQLInsert;
	protected String[] customSQLUpdate;
	protected String[] customSQLDelete;
	protected ExecuteUpdateResultCheckStyle[] insertResultCheckStyles;
	protected ExecuteUpdateResultCheckStyle[] updateResultCheckStyles;
	protected ExecuteUpdateResultCheckStyle[] deleteResultCheckStyles;

	private InsertGeneratedIdentifierDelegate identityDelegate;

	private boolean[] tableHasColumns;

	private final String loaderName;

	private UniqueEntityLoader queryLoader;

	private final String temporaryIdTableName;
	private final String temporaryIdTableDDL;

	private final Map subclassPropertyAliases = new HashMap();
	private final Map subclassPropertyColumnNames = new HashMap();

	protected final BasicEntityPropertyMapping propertyMapping;

	protected void addDiscriminatorToInsert(Insert insert) {}

	protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {}

	protected abstract int[] getSubclassColumnTableNumberClosure();

	protected abstract int[] getSubclassFormulaTableNumberClosure();

	public abstract String getSubclassTableName(int j);

	protected abstract String[] getSubclassTableKeyColumns(int j);

	protected abstract boolean isClassOrSuperclassTable(int j);

	protected abstract int getSubclassTableSpan();

	protected abstract int getTableSpan();

	protected abstract boolean isTableCascadeDeleteEnabled(int j);

	protected abstract String getTableName(int j);

	protected abstract String[] getKeyColumns(int j);

	protected abstract boolean isPropertyOfTable(int property, int j);

	protected abstract int[] getPropertyTableNumbersInSelect();

	protected abstract int[] getPropertyTableNumbers();

	protected abstract int getSubclassPropertyTableNumber(int i);

	protected abstract String filterFragment(String alias) throws MappingException;

	private static final String DISCRIMINATOR_ALIAS = "clazz_";

	public String getDiscriminatorColumnName() {
		return DISCRIMINATOR_ALIAS;
	}

	public String getDiscriminatorColumnReaders() {
		return DISCRIMINATOR_ALIAS;
	}	
	
	public String getDiscriminatorColumnReaderTemplate() {
		return DISCRIMINATOR_ALIAS;
	}	
	
	protected String getDiscriminatorAlias() {
		return DISCRIMINATOR_ALIAS;
	}

	protected String getDiscriminatorFormulaTemplate() {
		return null;
	}

	protected boolean isInverseTable(int j) {
		return false;
	}

	protected boolean isNullableTable(int j) {
		return false;
	}

	protected boolean isNullableSubclassTable(int j) {
		return false;
	}

	protected boolean isInverseSubclassTable(int j) {
		return false;
	}

	public boolean isSubclassEntityName(String entityName) {
		return entityMetamodel.getSubclassEntityNames().contains(entityName);
	}

	private boolean[] getTableHasColumns() {
		return tableHasColumns;
	}

	public String[] getRootTableKeyColumnNames() {
		return rootTableKeyColumnNames;
	}

	protected String[] getSQLUpdateByRowIdStrings() {
		if ( sqlUpdateByRowIdString == null ) {
			throw new AssertionFailure( "no update by row id" );
		}
		String[] result = new String[getTableSpan() + 1];
		result[0] = sqlUpdateByRowIdString;
		System.arraycopy( sqlUpdateStrings, 0, result, 1, getTableSpan() );
		return result;
	}

	protected String[] getSQLLazyUpdateByRowIdStrings() {
		if ( sqlLazyUpdateByRowIdString == null ) {
			throw new AssertionFailure( "no update by row id" );
		}
		String[] result = new String[getTableSpan()];
		result[0] = sqlLazyUpdateByRowIdString;
		for ( int i = 1; i < getTableSpan(); i++ ) {
			result[i] = sqlLazyUpdateStrings[i];
		}
		return result;
	}

	protected String getSQLSnapshotSelectString() {
		return sqlSnapshotSelectString;
	}

	protected String getSQLLazySelectString() {
		return sqlLazySelectString;
	}

	protected String[] getSQLDeleteStrings() {
		return sqlDeleteStrings;
	}

	protected String[] getSQLInsertStrings() {
		return sqlInsertStrings;
	}

	protected String[] getSQLUpdateStrings() {
		return sqlUpdateStrings;
	}

	protected String[] getSQLLazyUpdateStrings() {
		return sqlLazyUpdateStrings;
	}

	/**
	 * The query that inserts a row, letting the database generate an id
	 * 
	 * @return The IDENTITY-based insertion query.
	 */
	protected String getSQLIdentityInsertString() {
		return sqlIdentityInsertString;
	}

	protected String getVersionSelectString() {
		return sqlVersionSelectString;
	}

	protected boolean isInsertCallable(int j) {
		return insertCallable[j];
	}

	protected boolean isUpdateCallable(int j) {
		return updateCallable[j];
	}

	protected boolean isDeleteCallable(int j) {
		return deleteCallable[j];
	}

	protected boolean isSubclassPropertyDeferred(String propertyName, String entityName) {
		return false;
	}

	protected boolean isSubclassTableSequentialSelect(int j) {
		return false;
	}

	public boolean hasSequentialSelect() {
		return false;
	}

	/**
	 * Decide which tables need to be updated.
	 * <p/>
	 * The return here is an array of boolean values with each index corresponding
	 * to a given table in the scope of this persister.
	 *
	 * @param dirtyProperties The indices of all the entity properties considered dirty.
	 * @param hasDirtyCollection Whether any collections owned by the entity which were considered dirty.
	 *
	 * @return Array of booleans indicating which table require updating.
	 */
	protected boolean[] getTableUpdateNeeded(final int[] dirtyProperties, boolean hasDirtyCollection) {

		if ( dirtyProperties == null ) {
			return getTableHasColumns(); // for objects that came in via update()
		}
		else {
			boolean[] updateability = getPropertyUpdateability();
			int[] propertyTableNumbers = getPropertyTableNumbers();
			boolean[] tableUpdateNeeded = new boolean[ getTableSpan() ];
			for ( int i = 0; i < dirtyProperties.length; i++ ) {
				int property = dirtyProperties[i];
				int table = propertyTableNumbers[property];
				tableUpdateNeeded[table] = tableUpdateNeeded[table] ||
						( getPropertyColumnSpan(property) > 0 && updateability[property] );
			}
			if ( isVersioned() ) {
				tableUpdateNeeded[0] = tableUpdateNeeded[0] ||
					Versioning.isVersionIncrementRequired( dirtyProperties, hasDirtyCollection, getPropertyVersionability() );
			}
			return tableUpdateNeeded;
		}
	}

	public boolean hasRowId() {
		return rowIdName != null;
	}

	public AbstractEntityPersister(
			final PersistentClass persistentClass,
			final EntityRegionAccessStrategy cacheAccessStrategy,
			final SessionFactoryImplementor factory) throws HibernateException {

		// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		this.factory = factory;
		this.cacheAccessStrategy = cacheAccessStrategy;
		isLazyPropertiesCacheable = persistentClass.isLazyPropertiesCacheable();
		this.cacheEntryStructure = factory.getSettings().isStructuredCacheEntriesEnabled() ?
				(CacheEntryStructure) new StructuredCacheEntry(this) :
				(CacheEntryStructure) new UnstructuredCacheEntry();

		this.entityMetamodel = new EntityMetamodel( persistentClass, factory );
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		int batch = persistentClass.getBatchSize();
		if ( batch == -1 ) {
			batch = factory.getSettings().getDefaultBatchFetchSize();
		}
		batchSize = batch;
		hasSubselectLoadableCollections = persistentClass.hasSubselectLoadableCollections();

		propertyMapping = new BasicEntityPropertyMapping( this );

		// IDENTIFIER

		identifierColumnSpan = persistentClass.getIdentifier().getColumnSpan();
		rootTableKeyColumnNames = new String[identifierColumnSpan];
		rootTableKeyColumnReaders = new String[identifierColumnSpan];
		rootTableKeyColumnReaderTemplates = new String[identifierColumnSpan];
		identifierAliases = new String[identifierColumnSpan];

		rowIdName = persistentClass.getRootTable().getRowId();

		loaderName = persistentClass.getLoaderName();

		Iterator iter = persistentClass.getIdentifier().getColumnIterator();
		int i = 0;
		while ( iter.hasNext() ) {
			Column col = ( Column ) iter.next();
			rootTableKeyColumnNames[i] = col.getQuotedName( factory.getDialect() );
			rootTableKeyColumnReaders[i] = col.getReadExpr( factory.getDialect() );
			rootTableKeyColumnReaderTemplates[i] = col.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
			identifierAliases[i] = col.getAlias( factory.getDialect(), persistentClass.getRootTable() );
			i++;
		}

		// VERSION

		if ( persistentClass.isVersioned() ) {
			versionColumnName = ( ( Column ) persistentClass.getVersion().getColumnIterator().next() ).getQuotedName( factory.getDialect() );
		}
		else {
			versionColumnName = null;
		}

		//WHERE STRING

		sqlWhereString = StringHelper.isNotEmpty( persistentClass.getWhere() ) ? "( " + persistentClass.getWhere() + ") " : null;
		sqlWhereStringTemplate = sqlWhereString == null ?
				null :
				Template.renderWhereStringTemplate( sqlWhereString, factory.getDialect(), factory.getSqlFunctionRegistry() );

		// PROPERTIES

		final boolean lazyAvailable = isInstrumented(EntityMode.POJO);

		int hydrateSpan = entityMetamodel.getPropertySpan();
		propertyColumnSpans = new int[hydrateSpan];
		propertySubclassNames = new String[hydrateSpan];
		propertyColumnAliases = new String[hydrateSpan][];
		propertyColumnNames = new String[hydrateSpan][];
		propertyColumnFormulaTemplates = new String[hydrateSpan][];
		propertyColumnReaderTemplates = new String[hydrateSpan][];
		propertyColumnWriters = new String[hydrateSpan][];
		propertyUniqueness = new boolean[hydrateSpan];
		propertySelectable = new boolean[hydrateSpan];
		propertyColumnUpdateable = new boolean[hydrateSpan][];
		propertyColumnInsertable = new boolean[hydrateSpan][];
		HashSet thisClassProperties = new HashSet();

		lazyProperties = new HashSet();
		ArrayList lazyNames = new ArrayList();
		ArrayList lazyNumbers = new ArrayList();
		ArrayList lazyTypes = new ArrayList();
		ArrayList lazyColAliases = new ArrayList();

		iter = persistentClass.getPropertyClosureIterator();
		i = 0;
		boolean foundFormula = false;
		while ( iter.hasNext() ) {
			Property prop = ( Property ) iter.next();
			thisClassProperties.add( prop );

			int span = prop.getColumnSpan();
			propertyColumnSpans[i] = span;
			propertySubclassNames[i] = prop.getPersistentClass().getEntityName();
			String[] colNames = new String[span];
			String[] colAliases = new String[span];
			String[] colReaderTemplates = new String[span];
			String[] colWriters = new String[span];
			String[] formulaTemplates = new String[span];
			Iterator colIter = prop.getColumnIterator();
			int k = 0;
			while ( colIter.hasNext() ) {
				Selectable thing = ( Selectable ) colIter.next();
				colAliases[k] = thing.getAlias( factory.getDialect() , prop.getValue().getTable() );
				if ( thing.isFormula() ) {
					foundFormula = true;
					formulaTemplates[k] = thing.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
				}
				else {
					Column col = (Column)thing;
					colNames[k] = col.getQuotedName( factory.getDialect() );
					colReaderTemplates[k] = col.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
					colWriters[k] = col.getWriteExpr();
				}
				k++;
			}
			propertyColumnNames[i] = colNames;
			propertyColumnFormulaTemplates[i] = formulaTemplates;
			propertyColumnReaderTemplates[i] = colReaderTemplates;
			propertyColumnWriters[i] = colWriters;
			propertyColumnAliases[i] = colAliases;

			if ( lazyAvailable && prop.isLazy() ) {
				lazyProperties.add( prop.getName() );
				lazyNames.add( prop.getName() );
				lazyNumbers.add( new Integer( i ) );
				lazyTypes.add( prop.getValue().getType() );
				lazyColAliases.add( colAliases );
			}

			propertyColumnUpdateable[i] = prop.getValue().getColumnUpdateability();
			propertyColumnInsertable[i] = prop.getValue().getColumnInsertability();

			propertySelectable[i] = prop.isSelectable();

			propertyUniqueness[i] = prop.getValue().isAlternateUniqueKey();

			i++;

		}
		hasFormulaProperties = foundFormula;
		lazyPropertyColumnAliases = ArrayHelper.to2DStringArray( lazyColAliases );
		lazyPropertyNames = ArrayHelper.toStringArray( lazyNames );
		lazyPropertyNumbers = ArrayHelper.toIntArray( lazyNumbers );
		lazyPropertyTypes = ArrayHelper.toTypeArray( lazyTypes );

		// SUBCLASS PROPERTY CLOSURE

		ArrayList columns = new ArrayList();
		ArrayList columnsLazy = new ArrayList();
		ArrayList columnReaderTemplates = new ArrayList();
		ArrayList aliases = new ArrayList();
		ArrayList formulas = new ArrayList();
		ArrayList formulaAliases = new ArrayList();
		ArrayList formulaTemplates = new ArrayList();
		ArrayList formulasLazy = new ArrayList();
		ArrayList types = new ArrayList();
		ArrayList names = new ArrayList();
		ArrayList classes = new ArrayList();
		ArrayList templates = new ArrayList();
		ArrayList propColumns = new ArrayList();
		ArrayList propColumnReaders = new ArrayList();
		ArrayList propColumnReaderTemplates = new ArrayList();
		ArrayList joinedFetchesList = new ArrayList();
		ArrayList cascades = new ArrayList();
		ArrayList definedBySubclass = new ArrayList();
		ArrayList propColumnNumbers = new ArrayList();
		ArrayList propFormulaNumbers = new ArrayList();
		ArrayList columnSelectables = new ArrayList();
		ArrayList propNullables = new ArrayList();

		iter = persistentClass.getSubclassPropertyClosureIterator();
		while ( iter.hasNext() ) {
			Property prop = ( Property ) iter.next();
			names.add( prop.getName() );
			classes.add( prop.getPersistentClass().getEntityName() );
			boolean isDefinedBySubclass = !thisClassProperties.contains( prop );
			definedBySubclass.add( Boolean.valueOf( isDefinedBySubclass ) );
			propNullables.add( Boolean.valueOf( prop.isOptional() || isDefinedBySubclass ) ); //TODO: is this completely correct?
			types.add( prop.getType() );

			Iterator colIter = prop.getColumnIterator();
			String[] cols = new String[prop.getColumnSpan()];
			String[] readers = new String[prop.getColumnSpan()];
			String[] readerTemplates = new String[prop.getColumnSpan()];
			String[] forms = new String[prop.getColumnSpan()];
			int[] colnos = new int[prop.getColumnSpan()];
			int[] formnos = new int[prop.getColumnSpan()];
			int l = 0;
			Boolean lazy = Boolean.valueOf( prop.isLazy() && lazyAvailable );
			while ( colIter.hasNext() ) {
				Selectable thing = ( Selectable ) colIter.next();
				if ( thing.isFormula() ) {
					String template = thing.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
					formnos[l] = formulaTemplates.size();
					colnos[l] = -1;
					formulaTemplates.add( template );
					forms[l] = template;
					formulas.add( thing.getText( factory.getDialect() ) );
					formulaAliases.add( thing.getAlias( factory.getDialect() ) );
					formulasLazy.add( lazy );
				}
				else {
					Column col = (Column)thing;
					String colName = col.getQuotedName( factory.getDialect() );
					colnos[l] = columns.size(); //before add :-)
					formnos[l] = -1;
					columns.add( colName );
					cols[l] = colName;
					aliases.add( thing.getAlias( factory.getDialect(), prop.getValue().getTable() ) );
					columnsLazy.add( lazy );
					columnSelectables.add( Boolean.valueOf( prop.isSelectable() ) );
					
					readers[l] = col.getReadExpr( factory.getDialect() );
					String readerTemplate = col.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
					readerTemplates[l] = readerTemplate;
					columnReaderTemplates.add( readerTemplate );
				}
				l++;
			}
			propColumns.add( cols );
			propColumnReaders.add( readers );
			propColumnReaderTemplates.add( readerTemplates );
			templates.add( forms );
			propColumnNumbers.add( colnos );
			propFormulaNumbers.add( formnos );

			joinedFetchesList.add( prop.getValue().getFetchMode() );
			cascades.add( prop.getCascadeStyle() );
		}
		subclassColumnClosure = ArrayHelper.toStringArray( columns );
		subclassColumnAliasClosure = ArrayHelper.toStringArray( aliases );
		subclassColumnLazyClosure = ArrayHelper.toBooleanArray( columnsLazy );
		subclassColumnSelectableClosure = ArrayHelper.toBooleanArray( columnSelectables );
		subclassColumnReaderTemplateClosure = ArrayHelper.toStringArray( columnReaderTemplates );

		subclassFormulaClosure = ArrayHelper.toStringArray( formulas );
		subclassFormulaTemplateClosure = ArrayHelper.toStringArray( formulaTemplates );
		subclassFormulaAliasClosure = ArrayHelper.toStringArray( formulaAliases );
		subclassFormulaLazyClosure = ArrayHelper.toBooleanArray( formulasLazy );

		subclassPropertyNameClosure = ArrayHelper.toStringArray( names );
		subclassPropertySubclassNameClosure = ArrayHelper.toStringArray( classes );
		subclassPropertyTypeClosure = ArrayHelper.toTypeArray( types );
		subclassPropertyNullabilityClosure = ArrayHelper.toBooleanArray( propNullables );
		subclassPropertyFormulaTemplateClosure = ArrayHelper.to2DStringArray( templates );
		subclassPropertyColumnNameClosure = ArrayHelper.to2DStringArray( propColumns );
		subclassPropertyColumnReaderClosure = ArrayHelper.to2DStringArray( propColumnReaders );
		subclassPropertyColumnReaderTemplateClosure = ArrayHelper.to2DStringArray( propColumnReaderTemplates );
		subclassPropertyColumnNumberClosure = ArrayHelper.to2DIntArray( propColumnNumbers );
		subclassPropertyFormulaNumberClosure = ArrayHelper.to2DIntArray( propFormulaNumbers );

		subclassPropertyCascadeStyleClosure = new CascadeStyle[cascades.size()];
		iter = cascades.iterator();
		int j = 0;
		while ( iter.hasNext() ) {
			subclassPropertyCascadeStyleClosure[j++] = ( CascadeStyle ) iter.next();
		}
		subclassPropertyFetchModeClosure = new FetchMode[joinedFetchesList.size()];
		iter = joinedFetchesList.iterator();
		j = 0;
		while ( iter.hasNext() ) {
			subclassPropertyFetchModeClosure[j++] = ( FetchMode ) iter.next();
		}

		propertyDefinedOnSubclass = new boolean[definedBySubclass.size()];
		iter = definedBySubclass.iterator();
		j = 0;
		while ( iter.hasNext() ) {
			propertyDefinedOnSubclass[j++] = ( ( Boolean ) iter.next() ).booleanValue();
		}

		// Handle any filters applied to the class level
		filterHelper = new FilterHelper( persistentClass.getFilterMap(), factory.getDialect(), factory.getSqlFunctionRegistry() );

		temporaryIdTableName = persistentClass.getTemporaryIdTableName();
		temporaryIdTableDDL = persistentClass.getTemporaryIdTableDDL();
	}

	protected String generateLazySelectString() {

		if ( !entityMetamodel.hasLazyProperties() ) {
			return null;
		}

		HashSet tableNumbers = new HashSet();
		ArrayList columnNumbers = new ArrayList();
		ArrayList formulaNumbers = new ArrayList();
		for ( int i = 0; i < lazyPropertyNames.length; i++ ) {
			// all this only really needs to consider properties
			// of this class, not its subclasses, but since we
			// are reusing code used for sequential selects, we
			// use the subclass closure
			int propertyNumber = getSubclassPropertyIndex( lazyPropertyNames[i] );

			int tableNumber = getSubclassPropertyTableNumber( propertyNumber );
			tableNumbers.add( new Integer( tableNumber ) );

			int[] colNumbers = subclassPropertyColumnNumberClosure[propertyNumber];
			for ( int j = 0; j < colNumbers.length; j++ ) {
				if ( colNumbers[j]!=-1 ) {
					columnNumbers.add( new Integer( colNumbers[j] ) );
				}
			}
			int[] formNumbers = subclassPropertyFormulaNumberClosure[propertyNumber];
			for ( int j = 0; j < formNumbers.length; j++ ) {
				if ( formNumbers[j]!=-1 ) {
					formulaNumbers.add( new Integer( formNumbers[j] ) );
				}
			}
		}

		if ( columnNumbers.size()==0 && formulaNumbers.size()==0 ) {
			// only one-to-one is lazy fetched
			return null;
		}

		return renderSelect( ArrayHelper.toIntArray( tableNumbers ),
				ArrayHelper.toIntArray( columnNumbers ),
				ArrayHelper.toIntArray( formulaNumbers ) );

	}

	public Object initializeLazyProperty(String fieldName, Object entity, SessionImplementor session)
			throws HibernateException {

		final Serializable id = session.getContextEntityIdentifier( entity );

		final EntityEntry entry = session.getPersistenceContext().getEntry( entity );
		if ( entry == null ) {
			throw new HibernateException( "entity is not associated with the session: " + id );
		}

		if ( log.isTraceEnabled() ) {
			log.trace(
					"initializing lazy properties of: " +
					MessageHelper.infoString( this, id, getFactory() ) +
					", field access: " + fieldName
				);
		}

		if ( hasCache() ) {
			CacheKey cacheKey = new CacheKey(id, getIdentifierType(), getEntityName(), session.getEntityMode(), getFactory() );
			Object ce = getCacheAccessStrategy().get( cacheKey, session.getTimestamp() );
			if (ce!=null) {
				CacheEntry cacheEntry = (CacheEntry) getCacheEntryStructure().destructure(ce, factory);
				if ( !cacheEntry.areLazyPropertiesUnfetched() ) {
					//note early exit here:
					return initializeLazyPropertiesFromCache( fieldName, entity, session, entry, cacheEntry );
				}
			}
		}

		return initializeLazyPropertiesFromDatastore( fieldName, entity, session, id, entry );

	}

	private Object initializeLazyPropertiesFromDatastore(
			final String fieldName,
			final Object entity,
			final SessionImplementor session,
			final Serializable id,
			final EntityEntry entry) {

		if ( !hasLazyProperties() ) {
			throw new AssertionFailure("no lazy properties");
		}

		log.trace("initializing lazy properties from datastore");

		try {

			Object result = null;
			PreparedStatement ps = null;
			try {
				final String lazySelect = getSQLLazySelectString();
				ResultSet rs = null;
				try {
					if ( lazySelect != null ) {
						// null sql means that the only lazy properties
						// are shared PK one-to-one associations which are
						// handled differently in the Type#nullSafeGet code...
						ps = session.getBatcher().prepareSelectStatement(lazySelect);
						getIdentifierType().nullSafeSet( ps, id, 1, session );
						rs = ps.executeQuery();
						rs.next();
					}
					final Object[] snapshot = entry.getLoadedState();
					for ( int j = 0; j < lazyPropertyNames.length; j++ ) {
						Object propValue = lazyPropertyTypes[j].nullSafeGet( rs, lazyPropertyColumnAliases[j], session, entity );
						if ( initializeLazyProperty( fieldName, entity, session, snapshot, j, propValue ) ) {
							result = propValue;
						}
					}
				}
				finally {
					if ( rs != null ) {
						rs.close();
					}
				}
			}
			finally {
				if ( ps != null ) {
					session.getBatcher().closeStatement( ps );
				}
			}

			log.trace( "done initializing lazy properties" );

			return result;

		}
		catch ( SQLException sqle ) {
			throw JDBCExceptionHelper.convert(
					getFactory().getSQLExceptionConverter(),
					sqle,
					"could not initialize lazy properties: " +
					MessageHelper.infoString( this, id, getFactory() ),
					getSQLLazySelectString()
				);
		}
	}

	private Object initializeLazyPropertiesFromCache(
			final String fieldName,
			final Object entity,
			final SessionImplementor session,
			final EntityEntry entry,
			final CacheEntry cacheEntry
	) {

		log.trace("initializing lazy properties from second-level cache");

		Object result = null;
		Serializable[] disassembledValues = cacheEntry.getDisassembledState();
		final Object[] snapshot = entry.getLoadedState();
		for ( int j = 0; j < lazyPropertyNames.length; j++ ) {
			final Object propValue = lazyPropertyTypes[j].assemble(
					disassembledValues[ lazyPropertyNumbers[j] ],
					session,
					entity
				);
			if ( initializeLazyProperty( fieldName, entity, session, snapshot, j, propValue ) ) {
				result = propValue;
			}
		}

		log.trace( "done initializing lazy properties" );

		return result;
	}

	private boolean initializeLazyProperty(
			final String fieldName,
			final Object entity,
			final SessionImplementor session,
			final Object[] snapshot,
			final int j,
			final Object propValue) {
		setPropertyValue( entity, lazyPropertyNumbers[j], propValue, session.getEntityMode() );
		if (snapshot != null) {
			// object have been loaded with setReadOnly(true); HHH-2236
			snapshot[ lazyPropertyNumbers[j] ] = lazyPropertyTypes[j].deepCopy( propValue, session.getEntityMode(), factory );
		}
		return fieldName.equals( lazyPropertyNames[j] );
	}

	public boolean isBatchable() {
		return optimisticLockMode()==Versioning.OPTIMISTIC_LOCK_NONE ||
			( !isVersioned() && optimisticLockMode()==Versioning.OPTIMISTIC_LOCK_VERSION ) ||
			getFactory().getSettings().isJdbcBatchVersionedData();
	}

	public Serializable[] getQuerySpaces() {
		return getPropertySpaces();
	}

	protected Set getLazyProperties() {
		return lazyProperties;
	}

	public boolean isBatchLoadable() {
		return batchSize > 1;
	}

	public String[] getIdentifierColumnNames() {
		return rootTableKeyColumnNames;
	}

	public String[] getIdentifierColumnReaders() {
		return rootTableKeyColumnReaders;
	}	

	public String[] getIdentifierColumnReaderTemplates() {
		return rootTableKeyColumnReaderTemplates;
	}	
	
	protected int getIdentifierColumnSpan() {
		return identifierColumnSpan;
	}

	protected String[] getIdentifierAliases() {
		return identifierAliases;
	}

	public String getVersionColumnName() {
		return versionColumnName;
	}

	protected String getVersionedTableName() {
		return getTableName( 0 );
	}

	protected boolean[] getSubclassColumnLazyiness() {
		return subclassColumnLazyClosure;
	}

	protected boolean[] getSubclassFormulaLazyiness() {
		return subclassFormulaLazyClosure;
	}

	/**
	 * We can't immediately add to the cache if we have formulas
	 * which must be evaluated, or if we have the possibility of
	 * two concurrent updates to the same item being merged on
	 * the database. This can happen if (a) the item is not
	 * versioned and either (b) we have dynamic update enabled
	 * or (c) we have multiple tables holding the state of the
	 * item.
	 */
	public boolean isCacheInvalidationRequired() {
		return hasFormulaProperties() ||
				( !isVersioned() && ( entityMetamodel.isDynamicUpdate() || getTableSpan() > 1 ) );
	}

	public boolean isLazyPropertiesCacheable() {
		return isLazyPropertiesCacheable;
	}

	public String selectFragment(String alias, String suffix) {
		return identifierSelectFragment( alias, suffix ) +
				propertySelectFragment( alias, suffix, false );
	}

	public String[] getIdentifierAliases(String suffix) {
		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
		// was toUnqotedAliasStrings( getIdentiferColumnNames() ) before - now tried
		// to remove that unqoting and missing aliases..
		return new Alias( suffix ).toAliasStrings( getIdentifierAliases() );
	}

	public String[] getPropertyAliases(String suffix, int i) {
		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
		return new Alias( suffix ).toUnquotedAliasStrings( propertyColumnAliases[i] );
	}

	public String getDiscriminatorAlias(String suffix) {
		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
		// was toUnqotedAliasStrings( getdiscriminatorColumnName() ) before - now tried
		// to remove that unqoting and missing aliases..
		return entityMetamodel.hasSubclasses() ?
				new Alias( suffix ).toAliasString( getDiscriminatorAlias() ) :
				null;
	}

	public String identifierSelectFragment(String name, String suffix) {
		return new SelectFragment()
				.setSuffix( suffix )
				.addColumns( name, getIdentifierColumnNames(), getIdentifierAliases() )
				.toFragmentString()
				.substring( 2 ); //strip leading ", "
	}


	public String propertySelectFragment(String tableAlias, String suffix, boolean allProperties) {
		return propertySelectFragmentFragment( tableAlias, suffix, allProperties ).toFragmentString();
	}

	public SelectFragment propertySelectFragmentFragment(
			String tableAlias,
			String suffix,
			boolean allProperties) {
		SelectFragment select = new SelectFragment()
				.setSuffix( suffix )
				.setUsedAliases( getIdentifierAliases() );

		int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
		String[] columnAliases = getSubclassColumnAliasClosure();
		String[] columnReaderTemplates = getSubclassColumnReaderTemplateClosure();
		for ( int i = 0; i < getSubclassColumnClosure().length; i++ ) {
			boolean selectable = ( allProperties || !subclassColumnLazyClosure[i] ) &&
				!isSubclassTableSequentialSelect( columnTableNumbers[i] ) &&
				subclassColumnSelectableClosure[i];
			if ( selectable ) {
				String subalias = generateTableAlias( tableAlias, columnTableNumbers[i] );
				select.addColumnTemplate( subalias, columnReaderTemplates[i], columnAliases[i] );
			}
		}

		int[] formulaTableNumbers = getSubclassFormulaTableNumberClosure();
		String[] formulaTemplates = getSubclassFormulaTemplateClosure();
		String[] formulaAliases = getSubclassFormulaAliasClosure();
		for ( int i = 0; i < getSubclassFormulaTemplateClosure().length; i++ ) {
			boolean selectable = ( allProperties || !subclassFormulaLazyClosure[i] )
				&& !isSubclassTableSequentialSelect( formulaTableNumbers[i] );
			if ( selectable ) {
				String subalias = generateTableAlias( tableAlias, formulaTableNumbers[i] );
				select.addFormula( subalias, formulaTemplates[i], formulaAliases[i] );
			}
		}

		if ( entityMetamodel.hasSubclasses() ) {
			addDiscriminatorToSelect( select, tableAlias, suffix );
		}

		if ( hasRowId() ) {
			select.addColumn( tableAlias, rowIdName, ROWID_ALIAS );
		}

		return select;
	}

	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session)
			throws HibernateException {

		if ( log.isTraceEnabled() ) {
			log.trace( "Getting current persistent state for: " + MessageHelper.infoString( this, id, getFactory() ) );
		}

		try {
			PreparedStatement ps = session.getBatcher().prepareSelectStatement( getSQLSnapshotSelectString() );
			try {
				getIdentifierType().nullSafeSet( ps, id, 1, session );
				//if ( isVersioned() ) getVersionType().nullSafeSet( ps, version, getIdentifierColumnSpan()+1, session );
				ResultSet rs = ps.executeQuery();
				try {
					//if there is no resulting row, return null
					if ( !rs.next() ) {
						return null;
					}

					//otherwise return the "hydrated" state (ie. associations are not resolved)
					Type[] types = getPropertyTypes();
					Object[] values = new Object[types.length];
					boolean[] includeProperty = getPropertyUpdateability();
					for ( int i = 0; i < types.length; i++ ) {
						if ( includeProperty[i] ) {
							values[i] = types[i].hydrate( rs, getPropertyAliases( "", i ), session, null ); //null owner ok??
						}
					}
					return values;
				}
				finally {
					rs.close();
				}
			}
			finally {
				session.getBatcher().closeStatement( ps );
			}
		}
		catch ( SQLException sqle ) {
			throw JDBCExceptionHelper.convert(
					getFactory().getSQLExceptionConverter(),
					sqle,
					"could not retrieve snapshot: " +
					MessageHelper.infoString( this, id, getFactory() ),
			        getSQLSnapshotSelectString()
				);
		}

	}

	/**
	 * Generate the SQL that selects the version number by id
	 */
	protected String generateSelectVersionString() {
		SimpleSelect select = new SimpleSelect( getFactory().getDialect() )
				.setTableName( getVersionedTableName() );
		if ( isVersioned() ) {
			select.addColumn( versionColumnName );
		}
		else {
			select.addColumns( rootTableKeyColumnNames );
		}
		if ( getFactory().getSettings().isCommentsEnabled() ) {
			select.setComment( "get version " + getEntityName() );
		}
		return select.addCondition( rootTableKeyColumnNames, "=?" ).toStatementString();
	}

	protected String generateInsertGeneratedValuesSelectString() {
		return generateGeneratedValuesSelectString( getPropertyInsertGenerationInclusions() );
	}

	protected String generateUpdateGeneratedValuesSelectString() {
		return generateGeneratedValuesSelectString( getPropertyUpdateGenerationInclusions() );
	}

	private String generateGeneratedValuesSelectString(ValueInclusion[] inclusions) {
		Select select = new Select( getFactory().getDialect() );

		if ( getFactory().getSettings().isCommentsEnabled() ) {
			select.setComment( "get generated state " + getEntityName() );
		}

		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );

		// Here we render the select column list based on the properties defined as being generated.
		// For partial component generation, we currently just re-select the whole component
		// rather than trying to handle the individual generated portions.
		String selectClause = concretePropertySelectFragment( getRootAlias(), inclusions );
		selectClause = selectClause.substring( 2 );

		String fromClause = fromTableFragment( getRootAlias() ) +
				fromJoinFragment( getRootAlias(), true, false );

		String whereClause = new StringBuffer()
			.append( StringHelper.join( "=? and ", aliasedIdColumns ) )
			.append( "=?" )
			.append( whereJoinFragment( getRootAlias(), true, false ) )
			.toString();

		return select.setSelectClause( selectClause )
				.setFromClause( fromClause )
				.setOuterJoins( "", "" )
				.setWhereClause( whereClause )
				.toStatementString();
	}

	protected static interface InclusionChecker {
		public boolean includeProperty(int propertyNumber);
	}

	protected String concretePropertySelectFragment(String alias, final ValueInclusion[] inclusions) {
		return concretePropertySelectFragment(
				alias,
				new InclusionChecker() {
					// TODO : currently we really do not handle ValueInclusion.PARTIAL...
					// ValueInclusion.PARTIAL would indicate parts of a component need to
					// be included in the select; currently we then just render the entire
					// component into the select clause in that case.
					public boolean includeProperty(int propertyNumber) {
						return inclusions[propertyNumber] != ValueInclusion.NONE;
					}
				}
		);
	}

	protected String concretePropertySelectFragment(String alias, final boolean[] includeProperty) {
		return concretePropertySelectFragment(
				alias,
				new InclusionChecker() {
					public boolean includeProperty(int propertyNumber) {
						return includeProperty[propertyNumber];
					}
				}
		);
	}

	protected String concretePropertySelectFragment(String alias, InclusionChecker inclusionChecker) {
		int propertyCount = getPropertyNames().length;
		int[] propertyTableNumbers = getPropertyTableNumbersInSelect();
		SelectFragment frag = new SelectFragment();
		for ( int i = 0; i < propertyCount; i++ ) {
			if ( inclusionChecker.includeProperty( i ) ) {
				frag.addColumnTemplates(
						generateTableAlias( alias, propertyTableNumbers[i] ),
						propertyColumnReaderTemplates[i],
						propertyColumnAliases[i]
				);
				frag.addFormulas(
						generateTableAlias( alias, propertyTableNumbers[i] ),
						propertyColumnFormulaTemplates[i],
						propertyColumnAliases[i]
				);
			}
		}
		return frag.toFragmentString();
	}

	protected String generateSnapshotSelectString() {

		//TODO: should we use SELECT .. FOR UPDATE?

		Select select = new Select( getFactory().getDialect() );

		if ( getFactory().getSettings().isCommentsEnabled() ) {
			select.setComment( "get current state " + getEntityName() );
		}

		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
		String selectClause = StringHelper.join( ", ", aliasedIdColumns ) +
				concretePropertySelectFragment( getRootAlias(), getPropertyUpdateability() );

		String fromClause = fromTableFragment( getRootAlias() ) +
				fromJoinFragment( getRootAlias(), true, false );

		String whereClause = new StringBuffer()
			.append( StringHelper.join( "=? and ",
					aliasedIdColumns ) )
			.append( "=?" )
			.append( whereJoinFragment( getRootAlias(), true, false ) )
			.toString();

		/*if ( isVersioned() ) {
			where.append(" and ")
				.append( getVersionColumnName() )
				.append("=?");
		}*/

		return select.setSelectClause( selectClause )
				.setFromClause( fromClause )
				.setOuterJoins( "", "" )
				.setWhereClause( whereClause )
				.toStatementString();
	}

	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
		if ( !isVersioned() ) {
			throw new AssertionFailure( "cannot force version increment on non-versioned entity" );
		}

		if ( isVersionPropertyGenerated() ) {
			// the difficulty here is exactly what do we update in order to
			// force the version to be incremented in the db...
			throw new HibernateException( "LockMode.FORCE is currently not supported for generated version properties" );
		}

		Object nextVersion = getVersionType().next( currentVersion, session );
		if ( log.isTraceEnabled() ) {
			log.trace(
					"Forcing version increment [" + MessageHelper.infoString( this, id, getFactory() ) +
					"; " + getVersionType().toLoggableString( currentVersion, getFactory() ) +
					" -> " + getVersionType().toLoggableString( nextVersion, getFactory() ) + "]"
			);
		}

		// todo : cache this sql...
		String versionIncrementString = generateVersionIncrementUpdateString();
		PreparedStatement st = null;
		try {
			try {
				st = session.getBatcher().prepareStatement( versionIncrementString );
				getVersionType().nullSafeSet( st, nextVersion, 1, session );
				getIdentifierType().nullSafeSet( st, id, 2, session );
				getVersionType().nullSafeSet( st, currentVersion, 2 + getIdentifierColumnSpan(), session );
				int rows = st.executeUpdate();
				if ( rows != 1 ) {
					throw new StaleObjectStateException( getEntityName(), id );
				}
			}
			finally {
				session.getBatcher().closeStatement( st );
			}
		}
		catch ( SQLException sqle ) {
			throw JDBCExceptionHelper.convert(
					getFactory().getSQLExceptionConverter(),
					sqle,
					"could not retrieve version: " +
					MessageHelper.infoString( this, id, getFactory() ),
					getVersionSelectString()
				);
		}

		return nextVersion;
	}

	private String generateVersionIncrementUpdateString() {
		Update update = new Update( getFactory().getDialect() );
		update.setTableName( getTableName( 0 ) );
		if ( getFactory().getSettings().isCommentsEnabled() ) {
			update.setComment( "forced version increment" );
		}
		update.addColumn( getVersionColumnName() );
		update.addPrimaryKeyColumns( getIdentifierColumnNames() );
		update.setVersionColumnName( getVersionColumnName() );
		return update.toStatementString();
	}

	/**
	 * Retrieve the version number
	 */
	public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {

		if ( log.isTraceEnabled() ) {
			log.trace( "Getting version: " + MessageHelper.infoString( this, id, getFactory() ) );
		}

		try {

			PreparedStatement st = session.getBatcher().prepareSelectStatement( getVersionSelectString() );
			try {
				getIdentifierType().nullSafeSet( st, id, 1, session );

				ResultSet rs = st.executeQuery();
				try {
					if ( !rs.next() ) {
						return null;
					}
					if ( !isVersioned() ) {
						return this;
					}
					return getVersionType().nullSafeGet( rs, getVersionColumnName(), session, null );
				}
				finally {
					rs.close();
				}
			}
			finally {
				session.getBatcher().closeStatement( st );
			}

		}
		catch ( SQLException sqle ) {
			throw JDBCExceptionHelper.convert(
					getFactory().getSQLExceptionConverter(),
					sqle,
					"could not retrieve version: " +
					MessageHelper.infoString( this, id, getFactory() ),
					getVersionSelectString()
				);
		}

	}

	protected void initLockers() {
		lockers.put( LockMode.READ, generateLocker( LockMode.READ ) );
		lockers.put( LockMode.UPGRADE, generateLocker( LockMode.UPGRADE ) );
		lockers.put( LockMode.UPGRADE_NOWAIT, generateLocker( LockMode.UPGRADE_NOWAIT ) );
		lockers.put( LockMode.FORCE, generateLocker( LockMode.FORCE ) );
		lockers.put( LockMode.PESSIMISTIC_READ, generateLocker( LockMode.PESSIMISTIC_READ ) );
		lockers.put( LockMode.PESSIMISTIC_WRITE, generateLocker( LockMode.PESSIMISTIC_WRITE ) );
		lockers.put( LockMode.PESSIMISTIC_FORCE_INCREMENT, generateLocker( LockMode.PESSIMISTIC_FORCE_INCREMENT ) );
		lockers.put( LockMode.OPTIMISTIC, generateLocker( LockMode.OPTIMISTIC ) );
		lockers.put( LockMode.OPTIMISTIC_FORCE_INCREMENT, generateLocker( LockMode.OPTIMISTIC_FORCE_INCREMENT ) );
	}

	protected LockingStrategy generateLocker(LockMode lockMode) {
		return factory.getDialect().getLockingStrategy( this, lockMode );
	}

	private LockingStrategy getLocker(LockMode lockMode) {
		return ( LockingStrategy ) lockers.get( lockMode );
	}

	public void lock(
			Serializable id,
	        Object version,
	        Object object,
	        LockMode lockMode,
	        SessionImplementor session) throws HibernateException {
		getLocker( lockMode ).lock( id, version, object, LockOptions.WAIT_FOREVER, session );
	}
	
	public void lock(
			Serializable id,
	        Object version,
	        Object object,
	        LockOptions lockOptions,
	        SessionImplementor session) throws HibernateException {
		getLocker( lockOptions.getLockMode() ).lock( id, version, object, lockOptions.getTimeOut(), session );
	}

	public String getRootTableName() {
		return getSubclassTableName( 0 );
	}

	public String getRootTableAlias(String drivingAlias) {
		return drivingAlias;
	}

	public String[] getRootTableIdentifierColumnNames() {
		return getRootTableKeyColumnNames();
	}

	public String[] toColumns(String alias, String propertyName) throws QueryException {
		return propertyMapping.toColumns( alias, propertyName );
	}

	public String[] toColumns(String propertyName) throws QueryException {
		return propertyMapping.getColumnNames( propertyName );
	}

	public Type toType(String propertyName) throws QueryException {
		return propertyMapping.toType( propertyName );
	}

	public String[] getPropertyColumnNames(String propertyName) {
		return propertyMapping.getColumnNames( propertyName );
	}

	/**
	 * Warning:
	 * When there are duplicated property names in the subclasses
	 * of the class, this method may return the wrong table
	 * number for the duplicated subclass property (note that
	 * SingleTableEntityPersister defines an overloaded form
	 * which takes the entity name.
	 */
	public int getSubclassPropertyTableNumber(String propertyPath) {
		String rootPropertyName = StringHelper.root(propertyPath);
		Type type = propertyMapping.toType(rootPropertyName);
		if ( type.isAssociationType() ) {
			AssociationType assocType = ( AssociationType ) type;
			if ( assocType.useLHSPrimaryKey() ) {
				// performance op to avoid the array search
				return 0;
			}
			else if ( type.isCollectionType() ) {
				// properly handle property-ref-based associations
				rootPropertyName = assocType.getLHSPropertyName();
			}
		}
		//Enable for HHH-440, which we don't like:
		/*if ( type.isComponentType() && !propertyName.equals(rootPropertyName) ) {
			String unrooted = StringHelper.unroot(propertyName);
			int idx = ArrayHelper.indexOf( getSubclassColumnClosure(), unrooted );
			if ( idx != -1 ) {
				return getSubclassColumnTableNumberClosure()[idx];
			}
		}*/
		int index = ArrayHelper.indexOf( getSubclassPropertyNameClosure(), rootPropertyName); //TODO: optimize this better!
		return index==-1 ? 0 : getSubclassPropertyTableNumber(index);
	}

	public Declarer getSubclassPropertyDeclarer(String propertyPath) {
		int tableIndex = getSubclassPropertyTableNumber( propertyPath );
		if ( tableIndex == 0 ) {
			return Declarer.CLASS;
		}
		else if ( isClassOrSuperclassTable( tableIndex ) ) {
			return Declarer.SUPERCLASS;
		}
		else {
			return Declarer.SUBCLASS;
		}
	}

	private DiscriminatorMetadata discriminatorMetadata;

	public DiscriminatorMetadata getTypeDiscriminatorMetadata() {
		if ( discriminatorMetadata == null ) {
			discriminatorMetadata = buildTypeDiscriminatorMetadata();
		}
		return discriminatorMetadata;
	}

	private DiscriminatorMetadata buildTypeDiscriminatorMetadata() {
		return new DiscriminatorMetadata() {
			public String getSqlFragment(String sqlQualificationAlias) {
				return toColumns( sqlQualificationAlias, ENTITY_CLASS )[0];
			}

			public Type getResolutionType() {
				return new DiscriminatorType( getDiscriminatorType(), AbstractEntityPersister.this );
			}
		};
	}

	protected String generateTableAlias(String rootAlias, int tableNumber) {
		if ( tableNumber == 0 ) {
			return rootAlias;
		}
		StringBuffer buf = new StringBuffer().append( rootAlias );
		if ( !rootAlias.endsWith( "_" ) ) {
			buf.append( '_' );
		}
		return buf.append( tableNumber ).append( '_' ).toString();
	}

	public String[] toColumns(String name, final int i) {
		final String alias = generateTableAlias( name, getSubclassPropertyTableNumber( i ) );
		String[] cols = getSubclassPropertyColumnNames( i );
		String[] templates = getSubclassPropertyFormulaTemplateClosure()[i];
		String[] result = new String[cols.length];
		for ( int j = 0; j < cols.length; j++ ) {
			if ( cols[j] == null ) {
				result[j] = StringHelper.replace( templates[j], Template.TEMPLATE, alias );
			}
			else {
				result[j] = StringHelper.qualify( alias, cols[j] );
			}
		}
		return result;
	}

	private int getSubclassPropertyIndex(String propertyName) {
		return ArrayHelper.indexOf(subclassPropertyNameClosure, propertyName);
	}

	protected String[] getPropertySubclassNames() {
		return propertySubclassNames;
	}

	public String[] getPropertyColumnNames(int i) {
		return propertyColumnNames[i];
	}
	
	public String[] getPropertyColumnWriters(int i) {
		return propertyColumnWriters[i];
	}	

	protected int getPropertyColumnSpan(int i) {
		return propertyColumnSpans[i];
	}

	protected boolean hasFormulaProperties() {
		return hasFormulaProperties;
	}

	public FetchMode getFetchMode(int i) {
		return subclassPropertyFetchModeClosure[i];
	}

	public CascadeStyle getCascadeStyle(int i) {
		return subclassPropertyCascadeStyleClosure[i];
	}

	public Type getSubclassPropertyType(int i) {
		return subclassPropertyTypeClosure[i];
	}

	public String getSubclassPropertyName(int i) {
		return subclassPropertyNameClosure[i];
	}

	public int countSubclassProperties() {
		return subclassPropertyTypeClosure.length;
	}

	public String[] getSubclassPropertyColumnNames(int i) {
		return subclassPropertyColumnNameClosure[i];
	}

	public boolean isDefinedOnSubclass(int i) {
		return propertyDefinedOnSubclass[i];
	}

	protected String[][] getSubclassPropertyFormulaTemplateClosure() {
		return subclassPropertyFormulaTemplateClosure;
	}

	protected Type[] getSubclassPropertyTypeClosure() {
		return subclassPropertyTypeClosure;
	}

	protected String[][] getSubclassPropertyColumnNameClosure() {
		return subclassPropertyColumnNameClosure;
	}
	
	public String[][] getSubclassPropertyColumnReaderClosure() {
		return subclassPropertyColumnReaderClosure;
	}

	public String[][] getSubclassPropertyColumnReaderTemplateClosure() {
		return subclassPropertyColumnReaderTemplateClosure;
	}

	protected String[] getSubclassPropertyNameClosure() {
		return subclassPropertyNameClosure;
	}

	protected String[] getSubclassPropertySubclassNameClosure() {
		return subclassPropertySubclassNameClosure;
	}

	protected String[] getSubclassColumnClosure() {
		return subclassColumnClosure;
	}

	protected String[] getSubclassColumnAliasClosure() {
		return subclassColumnAliasClosure;
	}

	public String[] getSubclassColumnReaderTemplateClosure() {
		return subclassColumnReaderTemplateClosure;
	}

	protected String[] getSubclassFormulaClosure() {
		return subclassFormulaClosure;
	}

	protected String[] getSubclassFormulaTemplateClosure() {
		return subclassFormulaTemplateClosure;
	}

	protected String[] getSubclassFormulaAliasClosure() {
		return subclassFormulaAliasClosure;
	}

	public String[] getSubclassPropertyColumnAliases(String propertyName, String suffix) {
		String rawAliases[] = ( String[] ) subclassPropertyAliases.get( propertyName );

		if ( rawAliases == null ) {
			return null;
		}

		String result[] = new String[rawAliases.length];
		for ( int i = 0; i < rawAliases.length; i++ ) {
			result[i] = new Alias( suffix ).toUnquotedAliasString( rawAliases[i] );
		}
		return result;
	}

	public String[] getSubclassPropertyColumnNames(String propertyName) {
		//TODO: should we allow suffixes on these ?
		return ( String[] ) subclassPropertyColumnNames.get( propertyName );
	}



	//This is really ugly, but necessary:
	/**
	 * Must be called by subclasses, at the end of their constructors
	 */
	protected void initSubclassPropertyAliasesMap(PersistentClass model) throws MappingException {

		// ALIASES
		internalInitSubclassPropertyAliasesMap( null, model.getSubclassPropertyClosureIterator() );

		// aliases for identifier ( alias.id ); skip if the entity defines a non-id property named 'id'
		if ( ! entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
			subclassPropertyAliases.put( ENTITY_ID, getIdentifierAliases() );
			subclassPropertyColumnNames.put( ENTITY_ID, getIdentifierColumnNames() );
		}

		// aliases named identifier ( alias.idname )
		if ( hasIdentifierProperty() ) {
			subclassPropertyAliases.put( getIdentifierPropertyName(), getIdentifierAliases() );
			subclassPropertyColumnNames.put( getIdentifierPropertyName(), getIdentifierColumnNames() );
		}

		// aliases for composite-id's
		if ( getIdentifierType().isComponentType() ) {
			// Fetch embedded identifiers propertynames from the "virtual" identifier component
			AbstractComponentType componentId = ( AbstractComponentType ) getIdentifierType();
			String[] idPropertyNames = componentId.getPropertyNames();
			String[] idAliases = getIdentifierAliases();
			String[] idColumnNames = getIdentifierColumnNames();

			for ( int i = 0; i < idPropertyNames.length; i++ ) {
				if ( entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
					subclassPropertyAliases.put(
							ENTITY_ID + "." + idPropertyNames[i],
							new String[] { idAliases[i] }
					);
					subclassPropertyColumnNames.put(
							ENTITY_ID + "." + getIdentifierPropertyName() + "." + idPropertyNames[i],
							new String[] { idColumnNames[i] }
					);
				}
//				if (hasIdentifierProperty() && !ENTITY_ID.equals( getIdentifierPropertyName() ) ) {
				if ( hasIdentifierProperty() ) {
					subclassPropertyAliases.put(
							getIdentifierPropertyName() + "." + idPropertyNames[i],
							new String[] { idAliases[i] }
					);
					subclassPropertyColumnNames.put(
							getIdentifierPropertyName() + "." + idPropertyNames[i],
							new String[] { idColumnNames[i] }
					);
				}
				else {
					// embedded composite ids ( alias.idname1, alias.idname2 )
					subclassPropertyAliases.put( idPropertyNames[i], new String[] { idAliases[i] } );
					subclassPropertyColumnNames.put( idPropertyNames[i],  new String[] { idColumnNames[i] } );
				}
			}
		}

		if ( entityMetamodel.isPolymorphic() ) {
			subclassPropertyAliases.put( ENTITY_CLASS, new String[] { getDiscriminatorAlias() } );
			subclassPropertyColumnNames.put( ENTITY_CLASS, new String[] { getDiscriminatorColumnName() } );
		}

	}

	private void internalInitSubclassPropertyAliasesMap(String path, Iterator propertyIterator) {
		while ( propertyIterator.hasNext() ) {

			Property prop = ( Property ) propertyIterator.next();
			String propname = path == null ? prop.getName() : path + "." + prop.getName();
			if ( prop.isComposite() ) {
				Component component = ( Component ) prop.getValue();
				Iterator compProps = component.getPropertyIterator();
				internalInitSubclassPropertyAliasesMap( propname, compProps );
			}
			else {
				String[] aliases = new String[prop.getColumnSpan()];
				String[] cols = new String[prop.getColumnSpan()];
				Iterator colIter = prop.getColumnIterator();
				int l = 0;
				while ( colIter.hasNext() ) {
					Selectable thing = ( Selectable ) colIter.next();
					aliases[l] = thing.getAlias( getFactory().getDialect(), prop.getValue().getTable() );
					cols[l] = thing.getText( getFactory().getDialect() ); // TODO: skip formulas?
					l++;
				}

				subclassPropertyAliases.put( propname, aliases );
				subclassPropertyColumnNames.put( propname, cols );
			}
		}

	}

	public Object loadByUniqueKey(
			String propertyName,
			Object uniqueKey,
			SessionImplementor session) throws HibernateException {
		return getAppropriateUniqueKeyLoader( propertyName, session ).loadByUniqueKey( session, uniqueKey );
	}

	private EntityLoader getAppropriateUniqueKeyLoader(String propertyName, SessionImplementor session) {
		final boolean useStaticLoader = !session.getLoadQueryInfluencers().hasEnabledFilters()
				&& !session.getLoadQueryInfluencers().hasEnabledFetchProfiles()
				&& propertyName.indexOf('.')<0; //ugly little workaround for fact that createUniqueKeyLoaders() does not handle component properties

		if ( useStaticLoader ) {
			return ( EntityLoader ) uniqueKeyLoaders.get( propertyName );
		}
		else {
			return createUniqueKeyLoader(
					propertyMapping.toType( propertyName ),
					propertyMapping.toColumns( propertyName ),
					session.getLoadQueryInfluencers()
			);
		}
	}

	public int getPropertyIndex(String propertyName) {
		return entityMetamodel.getPropertyIndex(propertyName);
	}

	protected void createUniqueKeyLoaders() throws MappingException {
		Type[] propertyTypes = getPropertyTypes();
		String[] propertyNames = getPropertyNames();
		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
			if ( propertyUniqueness[i] ) {
				//don't need filters for the static loaders
				uniqueKeyLoaders.put(
						propertyNames[i],
						createUniqueKeyLoader(
								propertyTypes[i],
								getPropertyColumnNames( i ),
								LoadQueryInfluencers.NONE
						)
				);
				//TODO: create uk loaders for component properties
			}
		}
	}

	private EntityLoader createUniqueKeyLoader(
			Type uniqueKeyType,
			String[] columns,
			LoadQueryInfluencers loadQueryInfluencers) {
		if ( uniqueKeyType.isEntityType() ) {
			String className = ( ( EntityType ) uniqueKeyType ).getAssociatedEntityName();
			uniqueKeyType = getFactory().getEntityPersister( className ).getIdentifierType();
		}
		return new EntityLoader(
				this,
				columns,
				uniqueKeyType,
				1,
				LockMode.NONE,
				getFactory(),
				loadQueryInfluencers
		);
	}

	protected String getSQLWhereString(String alias) {
		return StringHelper.replace( sqlWhereStringTemplate, Template.TEMPLATE, alias );
	}

	protected boolean hasWhere() {
		return sqlWhereString != null;
	}

	private void initOrdinaryPropertyPaths(Mapping mapping) throws MappingException {
		for ( int i = 0; i < getSubclassPropertyNameClosure().length; i++ ) {
			propertyMapping.initPropertyPaths( getSubclassPropertyNameClosure()[i],
					getSubclassPropertyTypeClosure()[i],
					getSubclassPropertyColumnNameClosure()[i],
					getSubclassPropertyColumnReaderClosure()[i],
					getSubclassPropertyColumnReaderTemplateClosure()[i],
					getSubclassPropertyFormulaTemplateClosure()[i],
					mapping );
		}
	}

	private void initIdentifierPropertyPaths(Mapping mapping) throws MappingException {
		String idProp = getIdentifierPropertyName();
		if ( idProp != null ) {
			propertyMapping.initPropertyPaths( idProp, getIdentifierType(), getIdentifierColumnNames(),
					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping );
		}
		if ( entityMetamodel.getIdentifierProperty().isEmbedded() ) {
			propertyMapping.initPropertyPaths( null, getIdentifierType(), getIdentifierColumnNames(),
					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping );
		}
		if ( ! entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
			propertyMapping.initPropertyPaths( ENTITY_ID, getIdentifierType(), getIdentifierColumnNames(),
					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping );
		}
	}

	private void initDiscriminatorPropertyPath(Mapping mapping) throws MappingException {
		propertyMapping.initPropertyPaths( ENTITY_CLASS,
				getDiscriminatorType(),
				new String[]{getDiscriminatorColumnName()},
				new String[]{getDiscriminatorColumnReaders()},
				new String[]{getDiscriminatorColumnReaderTemplate()},
				new String[]{getDiscriminatorFormulaTemplate()},
				getFactory() );
	}

	protected void initPropertyPaths(Mapping mapping) throws MappingException {
		initOrdinaryPropertyPaths(mapping);
		initOrdinaryPropertyPaths(mapping); //do two passes, for collection property-ref!
		initIdentifierPropertyPaths(mapping);
		if ( entityMetamodel.isPolymorphic() ) {
			initDiscriminatorPropertyPath( mapping );
		}
	}

	protected UniqueEntityLoader createEntityLoader(
			LockMode lockMode,
			LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
		//TODO: disable batch loading if lockMode > READ?
		return BatchingEntityLoader.createBatchingEntityLoader(
				this,
				batchSize,
				lockMode,
				getFactory(),
				loadQueryInfluencers
		);
	}

	protected UniqueEntityLoader createEntityLoader(
			LockOptions lockOptions,
			LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
		//TODO: disable batch loading if lockMode > READ?
		return BatchingEntityLoader.createBatchingEntityLoader(
				this,
				batchSize,
			lockOptions,
				getFactory(),
				loadQueryInfluencers
		);
	}

	protected UniqueEntityLoader createEntityLoader(LockMode lockMode) throws MappingException {
		return createEntityLoader( lockMode, LoadQueryInfluencers.NONE );
	}

	protected boolean check(int rows, Serializable id, int tableNumber, Expectation expectation, PreparedStatement statement) throws HibernateException {
		try {
			expectation.verifyOutcome( rows, statement, -1 );
		}
		catch( StaleStateException e ) {
			if ( !isNullableTable( tableNumber ) ) {
				if ( getFactory().getStatistics().isStatisticsEnabled() ) {
					getFactory().getStatisticsImplementor()
							.optimisticFailure( getEntityName() );
				}
				throw new StaleObjectStateException( getEntityName(), id );
			}
			return false;
		}
		catch( TooManyRowsAffectedException e ) {
			throw new HibernateException(
					"Duplicate identifier in table for: " +
					MessageHelper.infoString( this, id, getFactory() )
			);
		}
		catch ( Throwable t ) {
			return false;
		}
		return true;
	}

	protected String generateUpdateString(boolean[] includeProperty, int j, boolean useRowId) {
		return generateUpdateString( includeProperty, j, null, useRowId );
	}

	/**
	 * Generate the SQL that updates a row by id (and version)
	 */
	protected String generateUpdateString(final boolean[] includeProperty,
										  final int j,
										  final Object[] oldFields,
										  final boolean useRowId) {

		Update update = new Update( getFactory().getDialect() ).setTableName( getTableName( j ) );

		// select the correct row by either pk or rowid
		if ( useRowId ) {
			update.addPrimaryKeyColumns( new String[]{rowIdName} ); //TODO: eventually, rowIdName[j]
		}
		else {
			update.addPrimaryKeyColumns( getKeyColumns( j ) );
		}

		boolean hasColumns = false;
		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
				// this is a property of the table, which we are updating
				update.addColumns( getPropertyColumnNames(i), propertyColumnUpdateable[i], propertyColumnWriters[i] );
				hasColumns = hasColumns || getPropertyColumnSpan( i ) > 0;
			}
		}

		if ( j == 0 && isVersioned() && entityMetamodel.getOptimisticLockMode() == Versioning.OPTIMISTIC_LOCK_VERSION ) {
			// this is the root (versioned) table, and we are using version-based
			// optimistic locking;  if we are not updating the version, also don't
			// check it (unless this is a "generated" version column)!
			if ( checkVersion( includeProperty ) ) {
				update.setVersionColumnName( getVersionColumnName() );
				hasColumns = true;
			}
		}
		else if ( entityMetamodel.getOptimisticLockMode() > Versioning.OPTIMISTIC_LOCK_VERSION && oldFields != null ) {
			// we are using "all" or "dirty" property-based optimistic locking

			boolean[] includeInWhere = entityMetamodel.getOptimisticLockMode() == Versioning.OPTIMISTIC_LOCK_ALL ?
					getPropertyUpdateability() : //optimistic-lock="all", include all updatable properties
					includeProperty; //optimistic-lock="dirty", include all properties we are updating this time

			boolean[] versionability = getPropertyVersionability();
			Type[] types = getPropertyTypes();
			for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
				boolean include = includeInWhere[i] &&
						isPropertyOfTable( i, j ) &&
						versionability[i];
				if ( include ) {
					// this property belongs to the table, and it is not specifically
					// excluded from optimistic locking by optimistic-lock="false"
					String[] propertyColumnNames = getPropertyColumnNames( i );
					String[] propertyColumnWriters = getPropertyColumnWriters( i );
					boolean[] propertyNullness = types[i].toColumnNullness( oldFields[i], getFactory() );
					for ( int k=0; k<propertyNullness.length; k++ ) {
						if ( propertyNullness[k] ) {
							update.addWhereColumn( propertyColumnNames[k], "=" + propertyColumnWriters[k] );
						}
						else {
							update.addWhereColumn( propertyColumnNames[k], " is null" );
						}
					}
				}
			}

		}

		if ( getFactory().getSettings().isCommentsEnabled() ) {
			update.setComment( "update " + getEntityName() );
		}

		return hasColumns ? update.toStatementString() : null;
	}

	private boolean checkVersion(final boolean[] includeProperty) {
        return includeProperty[ getVersionProperty() ] ||
				entityMetamodel.getPropertyUpdateGenerationInclusions()[ getVersionProperty() ] != ValueInclusion.NONE;
	}

	protected String generateInsertString(boolean[] includeProperty, int j) {
		return generateInsertString( false, includeProperty, j );
	}

	protected String generateInsertString(boolean identityInsert, boolean[] includeProperty) {
		return generateInsertString( identityInsert, includeProperty, 0 );
	}

	/**
	 * Generate the SQL that inserts a row
	 */
	protected String generateInsertString(boolean identityInsert, boolean[] includeProperty, int j) {

		// todo : remove the identityInsert param and variations;
		//   identity-insert strings are now generated from generateIdentityInsertString()

		Insert insert = new Insert( getFactory().getDialect() )
				.setTableName( getTableName( j ) );

		// add normal properties
		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
				// this property belongs on the table and is to be inserted
				insert.addColumns( getPropertyColumnNames(i), propertyColumnInsertable[i], propertyColumnWriters[i] );
			}
		}

		// add the discriminator
		if ( j == 0 ) {
			addDiscriminatorToInsert( insert );
		}

		// add the primary key
		if ( j == 0 && identityInsert ) {
			insert.addIdentityColumn( getKeyColumns( 0 )[0] );
		}
		else {
			insert.addColumns( getKeyColumns( j ) );
		}

		if ( getFactory().getSettings().isCommentsEnabled() ) {
			insert.setComment( "insert " + getEntityName() );
		}

		String result = insert.toStatementString();

		// append the SQL to return the generated identifier
		if ( j == 0 && identityInsert && useInsertSelectIdentity() ) { //TODO: suck into Insert
			result = getFactory().getDialect().appendIdentitySelectToInsert( result );
		}

		return result;
	}

	/**
	 * Used to generate an insery statement against the root table in the
	 * case of identifier generation strategies where the insert statement
	 * executions actually generates the identifier value.
	 *
	 * @param includeProperty indices of the properties to include in the
	 * insert statement.
	 * @return The insert SQL statement string
	 */
	protected String generateIdentityInsertString(boolean[] includeProperty) {
		Insert insert = identityDelegate.prepareIdentifierGeneratingInsert();
		insert.setTableName( getTableName( 0 ) );

		// add normal properties
		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
			if ( includeProperty[i] && isPropertyOfTable( i, 0 ) ) {
				// this property belongs on the table and is to be inserted
				insert.addColumns( getPropertyColumnNames(i), propertyColumnInsertable[i], propertyColumnWriters[i] );
			}
		}

		// add the discriminator
		addDiscriminatorToInsert( insert );

		// delegate already handles PK columns

		if ( getFactory().getSettings().isCommentsEnabled() ) {
			insert.setComment( "insert " + getEntityName() );
		}

		return insert.toStatementString();
	}

	/**
	 * Generate the SQL that deletes a row by id (and version)
	 */
	protected String generateDeleteString(int j) {
		Delete delete = new Delete()
				.setTableName( getTableName( j ) )
				.addPrimaryKeyColumns( getKeyColumns( j ) );
		if ( j == 0 ) {
			delete.setVersionColumnName( getVersionColumnName() );
		}
		if ( getFactory().getSettings().isCommentsEnabled() ) {
			delete.setComment( "delete " + getEntityName() );
		}
		return delete.toStatementString();
	}

	protected int dehydrate(
			Serializable id,
			Object[] fields,
			boolean[] includeProperty,
			boolean[][] includeColumns,
			int j,
			PreparedStatement st,
			SessionImplementor session) throws HibernateException, SQLException {
		return dehydrate( id, fields, null, includeProperty, includeColumns, j, st, session, 1 );
	}

	/**
	 * Marshall the fields of a persistent instance to a prepared statement
	 */
	protected int dehydrate(
			final Serializable id,
	        final Object[] fields,
	        final Object rowId,
	        final boolean[] includeProperty,
	        final boolean[][] includeColumns,
	        final int j,
	        final PreparedStatement ps,
	        final SessionImplementor session,
	        int index) throws SQLException, HibernateException {

		if ( log.isTraceEnabled() ) {
			log.trace( "Dehydrating entity: " + MessageHelper.infoString( this, id, getFactory() ) );
		}

		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
				getPropertyTypes()[i].nullSafeSet( ps, fields[i], index, includeColumns[i], session );
				//index += getPropertyColumnSpan( i );
				index += ArrayHelper.countTrue( includeColumns[i] ); //TODO:  this is kinda slow...
			}
		}

		if ( rowId != null ) {
			ps.setObject( index, rowId );
			index += 1;
		}
		else if ( id != null ) {
			getIdentifierType().nullSafeSet( ps, id, index, session );
			index += getIdentifierColumnSpan();
		}

		return index;

	}

	/**
	 * Unmarshall the fields of a persistent instance from a result set,
	 * without resolving associations or collections. Question: should
	 * this really be here, or should it be sent back to Loader?
	 */
	public Object[] hydrate(
			final ResultSet rs,
	        final Serializable id,
	        final Object object,
	        final Loadable rootLoadable,
	        final String[][] suffixedPropertyColumns,
	        final boolean allProperties,
	        final SessionImplementor session) throws SQLException, HibernateException {

		if ( log.isTraceEnabled() ) {
			log.trace( "Hydrating entity: " + MessageHelper.infoString( this, id, getFactory() ) );
		}

		final AbstractEntityPersister rootPersister = (AbstractEntityPersister) rootLoadable;

		final boolean hasDeferred = rootPersister.hasSequentialSelect();
		PreparedStatement sequentialSelect = null;
		ResultSet sequentialResultSet = null;
		boolean sequentialSelectEmpty = false;
		try {

			if ( hasDeferred ) {
				final String sql = rootPersister.getSequentialSelect( getEntityName() );
				if ( sql != null ) {
					//TODO: I am not so sure about the exception handling in this bit!
					sequentialSelect = session.getBatcher().prepareSelectStatement( sql );
					rootPersister.getIdentifierType().nullSafeSet( sequentialSelect, id, 1, session );
					sequentialResultSet = sequentialSelect.executeQuery();
					if ( !sequentialResultSet.next() ) {
						// TODO: Deal with the "optional" attribute in the <join> mapping;
						// this code assumes that optional defaults to "true" because it
						// doesn't actually seem to work in the fetch="join" code
						//
						// Note that actual proper handling of optional-ality here is actually
						// more involved than this patch assumes.  Remember that we might have
						// multiple <join/> mappings associated with a single entity.  Really
						// a couple of things need to happen to properly handle optional here:
						//  1) First and foremost, when handling multiple <join/>s, we really
						//      should be using the entity root table as the driving table;
						//      another option here would be to choose some non-optional joined
						//      table to use as the driving table.  In all likelihood, just using
						//      the root table is much simplier
						//  2) Need to add the FK columns corresponding to each joined table
						//      to the generated select list; these would then be used when
						//      iterating the result set to determine whether all non-optional
						//      data is present
						// My initial thoughts on the best way to deal with this would be
						// to introduce a new SequentialSelect abstraction that actually gets
						// generated in the persisters (ok, SingleTable...) and utilized here.
						// It would encapsulated all this required optional-ality checking...
						sequentialSelectEmpty = true;
					}
				}
			}

			final String[] propNames = getPropertyNames();
			final Type[] types = getPropertyTypes();
			final Object[] values = new Object[types.length];
			final boolean[] laziness = getPropertyLaziness();
			final String[] propSubclassNames = getSubclassPropertySubclassNameClosure();

			for ( int i = 0; i < types.length; i++ ) {
				if ( !propertySelectable[i] ) {
					values[i] = BackrefPropertyAccessor.UNKNOWN;
				}
				else if ( allProperties || !laziness[i] ) {
					//decide which ResultSet to get the property value from:
					final boolean propertyIsDeferred = hasDeferred &&
							rootPersister.isSubclassPropertyDeferred( propNames[i], propSubclassNames[i] );
					if ( propertyIsDeferred && sequentialSelectEmpty ) {
						values[i] = null;
					}
					else {
						final ResultSet propertyResultSet = propertyIsDeferred ? sequentialResultSet : rs;
						final String[] cols = propertyIsDeferred ? propertyColumnAliases[i] : suffixedPropertyColumns[i];
						values[i] = types[i].hydrate( propertyResultSet, cols, session, object );
					}
				}
				else {
					values[i] = LazyPropertyInitializer.UNFETCHED_PROPERTY;
				}
			}

			if ( sequentialResultSet != null ) {
				sequentialResultSet.close();
			}

			return values;

		}
		finally {
			if ( sequentialSelect != null ) {
				session.getBatcher().closeStatement( sequentialSelect );
			}
		}
	}

	protected boolean useInsertSelectIdentity() {
		return !useGetGeneratedKeys() && getFactory().getDialect().supportsInsertSelectIdentity();
	}

	protected boolean useGetGeneratedKeys() {
		return getFactory().getSettings().isGetGeneratedKeysEnabled();
	}

	protected String getSequentialSelect(String entityName) {
		throw new UnsupportedOperationException("no sequential selects");
	}

	/**
	 * Perform an SQL INSERT, and then retrieve a generated identifier.
	 * <p/>
	 * This form is used for PostInsertIdentifierGenerator-style ids (IDENTITY,
	 * select, etc).
	 */
	protected Serializable insert(
			final Object[] fields,
	        final boolean[] notNull,
	        String sql,
	        final Object object,
	        final SessionImplementor session) throws HibernateException {

		if ( log.isTraceEnabled() ) {
			log.trace( "Inserting entity: " + getEntityName() + " (native id)" );
			if ( isVersioned() ) {
				log.trace( "Version: " + Versioning.getVersion( fields, this ) );
			}
		}

		Binder binder = new Binder() {
			public void bindValues(PreparedStatement ps) throws SQLException {
				dehydrate( null, fields, notNull, propertyColumnInsertable, 0, ps, session );
			}
			public Object getEntity() {
				return object;
			}
		};
		return identityDelegate.performInsert( sql, session, binder );
	}

	public String getIdentitySelectString() {
		//TODO: cache this in an instvar
		return getFactory().getDialect().getIdentitySelectString(
				getTableName(0),
				getKeyColumns(0)[0],
				getIdentifierType().sqlTypes( getFactory() )[0]
		);
	}

	public String getSelectByUniqueKeyString(String propertyName) {
		return new SimpleSelect( getFactory().getDialect() )
			.setTableName( getTableName(0) )
			.addColumns( getKeyColumns(0) )
			.addCondition( getPropertyColumnNames(propertyName), "=?" )
			.toStatementString();
	}

	/**
	 * Perform an SQL INSERT.
	 * <p/>
	 * This for is used for all non-root tables as well as the root table
	 * in cases where the identifier value is known before the insert occurs.
	 */
	protected void insert(
			final Serializable id,
	        final Object[] fields,
	        final boolean[] notNull,
	        final int j,
	        final String sql,
	        final Object object,
	        final SessionImplementor session) throws HibernateException {

		if ( isInverseTable( j ) ) {
			return;
		}

		//note: it is conceptually possible that a UserType could map null to
		//	  a non-null value, so the following is arguable:
		if ( isNullableTable( j ) && isAllNull( fields, j ) ) {
			return;
		}

		if ( log.isTraceEnabled() ) {
			log.trace( "Inserting entity: " + MessageHelper.infoString( this, id, getFactory() ) );
			if ( j == 0 && isVersioned() ) {
				log.trace( "Version: " + Versioning.getVersion( fields, this ) );
			}
		}

		Expectation expectation = Expectations.appropriateExpectation( insertResultCheckStyles[j] );
		boolean callable = isInsertCallable( j );
		// we can't batch joined inserts, *especially* not if it is an identity insert;
		// nor can we batch statements where the expectation is based on an output param
		final boolean useBatch = j == 0 && expectation.canBeBatched();
		try {

			// Render the SQL query
			final PreparedStatement insert;
			if ( useBatch ) {
				if ( callable ) {
					insert = session.getBatcher().prepareBatchCallableStatement( sql );
				}
				else {
					insert = session.getBatcher().prepareBatchStatement( sql );
				}
			}
			else {
				if ( callable ) {
					insert = session.getBatcher().prepareCallableStatement( sql );
				}
				else {
					insert = session.getBatcher().prepareStatement( sql );
				}
			}

			try {
				int index = 1;
				index += expectation.prepare( insert );

				// Write the values of fields onto the prepared statement - we MUST use the state at the time the
				// insert was issued (cos of foreign key constraints). Not necessarily the object's current state

				dehydrate( id, fields, null, notNull, propertyColumnInsertable, j, insert, session, index );

				if ( useBatch ) {
					// TODO : shouldnt inserts be Expectations.NONE?
					session.getBatcher().addToBatch( expectation );
				}
				else {
					expectation.verifyOutcome( insert.executeUpdate(), insert, -1 );
				}

			}
			catch ( SQLException sqle ) {
				if ( useBatch ) {
					session.getBatcher().abortBatch( sqle );
				}
				throw sqle;
			}
			finally {
				if ( !useBatch ) {
					session.getBatcher().closeStatement( insert );
				}
			}
		}
		catch ( SQLException sqle ) {
			throw JDBCExceptionHelper.convert(
					getFactory().getSQLExceptionConverter(),
					sqle,
					"could not insert: " + MessageHelper.infoString( this ),
					sql
				);
		}

	}

	/**
	 * Perform an SQL UPDATE or SQL INSERT
	 */
	protected void updateOrInsert(
			final Serializable id,
	        final Object[] fields,
	        final Object[] oldFields,
	        final Object rowId,
	        final boolean[] includeProperty,
	        final int j,
	        final Object oldVersion,
	        final Object object,
	        final String sql,
	        final SessionImplementor session) throws HibernateException {

		if ( !isInverseTable( j ) ) {

			final boolean isRowToUpdate;
			if ( isNullableTable( j ) && oldFields != null && isAllNull( oldFields, j ) ) {
				//don't bother trying to update, we know there is no row there yet
				isRowToUpdate = false;
			}
			else if ( isNullableTable( j ) && isAllNull( fields, j ) ) {
				//if all fields are null, we might need to delete existing row
				isRowToUpdate = true;
				delete( id, oldVersion, j, object, getSQLDeleteStrings()[j], session, null );
			}
			else {
				//there is probably a row there, so try to update
				//if no rows were updated, we will find out
				isRowToUpdate = update( id, fields, oldFields, rowId, includeProperty, j, oldVersion, object, sql, session );
			}

			if ( !isRowToUpdate && !isAllNull( fields, j ) ) {
				// assume that the row was not there since it previously had only null
				// values, so do an INSERT instead
				//TODO: does not respect dynamic-insert
				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
			}

		}

	}

	protected boolean update(
			final Serializable id,
	        final Object[] fields,
	        final Object[] oldFields,
	        final Object rowId,
	        final boolean[] includeProperty,
	        final int j,
	        final Object oldVersion,
	        final Object object,
	        final String sql,
	        final SessionImplementor session) throws HibernateException {

		final boolean useVersion = j == 0 && isVersioned();
		final Expectation expectation = Expectations.appropriateExpectation( updateResultCheckStyles[j] );
		final boolean callable = isUpdateCallable( j );
		final boolean useBatch = j == 0 && expectation.canBeBatched() && isBatchable(); //note: updates to joined tables can't be batched...

		if ( log.isTraceEnabled() ) {
			log.trace( "Updating entity: " + MessageHelper.infoString( this, id, getFactory() ) );
			if ( useVersion ) {
				log.trace( "Existing version: " + oldVersion + " -> New version: " + fields[getVersionProperty()] );
			}
		}

		try {

			int index = 1; // starting index
			final PreparedStatement update;
			if ( useBatch ) {
				if ( callable ) {
					update = session.getBatcher().prepareBatchCallableStatement( sql );
				}
				else {
					update = session.getBatcher().prepareBatchStatement( sql );
				}
			}
			else {
				if ( callable ) {
					update = session.getBatcher().prepareCallableStatement( sql );
				}
				else {
					update = session.getBatcher().prepareStatement( sql );
				}
			}

			try {

				index+= expectation.prepare( update );

				//Now write the values of fields onto the prepared statement
				index = dehydrate( id, fields, rowId, includeProperty, propertyColumnUpdateable, j, update, session, index );

				// Write any appropriate versioning conditional parameters
				if ( useVersion && Versioning.OPTIMISTIC_LOCK_VERSION == entityMetamodel.getOptimisticLockMode() ) {
					if ( checkVersion( includeProperty ) ) {
						getVersionType().nullSafeSet( update, oldVersion, index, session );
					}
				}
				else if ( entityMetamodel.getOptimisticLockMode() > Versioning.OPTIMISTIC_LOCK_VERSION && oldFields != null ) {
					boolean[] versionability = getPropertyVersionability(); //TODO: is this really necessary????
					boolean[] includeOldField = entityMetamodel.getOptimisticLockMode() == Versioning.OPTIMISTIC_LOCK_ALL ?
							getPropertyUpdateability() : includeProperty;
					Type[] types = getPropertyTypes();
					for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
						boolean include = includeOldField[i] &&
								isPropertyOfTable( i, j ) &&
								versionability[i]; //TODO: is this really necessary????
						if ( include ) {
							boolean[] settable = types[i].toColumnNullness( oldFields[i], getFactory() );
							types[i].nullSafeSet(
									update,
									oldFields[i],
									index,
									settable,
									session
								);
							index += ArrayHelper.countTrue(settable);
						}
					}
				}

				if ( useBatch ) {
					session.getBatcher().addToBatch( expectation );
					return true;
				}
				else {
					return check( update.executeUpdate(), id, j, expectation, update );
				}

			}
			catch ( SQLException sqle ) {
				if ( useBatch ) {
					session.getBatcher().abortBatch( sqle );
				}
				throw sqle;
			}
			finally {
				if ( !useBatch ) {
					session.getBatcher().closeStatement( update );
				}
			}

		}
		catch ( SQLException sqle ) {
			throw JDBCExceptionHelper.convert(
					getFactory().getSQLExceptionConverter(),
					sqle,
					"could not update: " + MessageHelper.infoString( this, id, getFactory() ),
					sql
				);
		}
	}

	/**
	 * Perform an SQL DELETE
	 */
	protected void delete(
			final Serializable id,
			final Object version,
			final int j,
			final Object object,
			final String sql,
			final SessionImplementor session,
			final Object[] loadedState) throws HibernateException {

		if ( isInverseTable( j ) ) {
			return;
		}

		final boolean useVersion = j == 0 && isVersioned();
		final boolean callable = isDeleteCallable( j );
		final Expectation expectation = Expectations.appropriateExpectation( deleteResultCheckStyles[j] );
		final boolean useBatch = j == 0 && isBatchable() && expectation.canBeBatched();

		if ( log.isTraceEnabled() ) {
			log.trace( "Deleting entity: " + MessageHelper.infoString( this, id, getFactory() ) );
			if ( useVersion ) {
				log.trace( "Version: " + version );
			}
		}

		if ( isTableCascadeDeleteEnabled( j ) ) {
			if ( log.isTraceEnabled() ) {
				log.trace( "delete handled by foreign key constraint: " + getTableName( j ) );
			}
			return; //EARLY EXIT!
		}

		try {

			//Render the SQL query
			PreparedStatement delete;
			int index = 1;
			if ( useBatch ) {
				if ( callable ) {
					delete = session.getBatcher().prepareBatchCallableStatement( sql );
				}
				else {
					delete = session.getBatcher().prepareBatchStatement( sql );
				}
			}
			else {
				if ( callable ) {
					delete = session.getBatcher().prepareCallableStatement( sql );
				}
				else {
					delete = session.getBatcher().prepareStatement( sql );
				}
			}

			try {

				index += expectation.prepare( delete );

				// Do the key. The key is immutable so we can use the _current_ object state - not necessarily
				// the state at the time the delete was issued
				getIdentifierType().nullSafeSet( delete, id, index, session );
				index += getIdentifierColumnSpan();

				// We should use the _current_ object state (ie. after any updates that occurred during flush)

				if ( useVersion ) {
					getVersionType().nullSafeSet( delete, version, index, session );
				}
				else if ( entityMetamodel.getOptimisticLockMode() > Versioning.OPTIMISTIC_LOCK_VERSION && loadedState != null ) {
					boolean[] versionability = getPropertyVersionability();
					Type[] types = getPropertyTypes();
					for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
						if ( isPropertyOfTable( i, j ) && versionability[i] ) {
							// this property belongs to the table and it is not specifically
							// excluded from optimistic locking by optimistic-lock="false"
							boolean[] settable = types[i].toColumnNullness( loadedState[i], getFactory() );
							types[i].nullSafeSet( delete, loadedState[i], index, settable, session );
							index += ArrayHelper.countTrue( settable );
						}
					}
				}

				if ( useBatch ) {
					session.getBatcher().addToBatch( expectation );
				}
				else {
					check( delete.executeUpdate(), id, j, expectation, delete );
				}

			}
			catch ( SQLException sqle ) {
				if ( useBatch ) {
					session.getBatcher().abortBatch( sqle );
				}
				throw sqle;
			}
			finally {
				if ( !useBatch ) {
					session.getBatcher().closeStatement( delete );
				}
			}

		}
		catch ( SQLException sqle ) {
			throw JDBCExceptionHelper.convert(
					getFactory().getSQLExceptionConverter(),
					sqle,
					"could not delete: " +
					MessageHelper.infoString( this, id, getFactory() ),
					sql
				);

		}

	}

	private String[] getUpdateStrings(boolean byRowId, boolean lazy) {
		if ( byRowId ) {
			return lazy ? getSQLLazyUpdateByRowIdStrings() : getSQLUpdateByRowIdStrings();
		}
		else {
			return lazy ? getSQLLazyUpdateStrings() : getSQLUpdateStrings();
		}
	}

	/**
	 * Update an object
	 */
	public void update(
			final Serializable id,
	        final Object[] fields,
	        final int[] dirtyFields,
	        final boolean hasDirtyCollection,
	        final Object[] oldFields,
	        final Object oldVersion,
	        final Object object,
	        final Object rowId,
	        final SessionImplementor session) throws HibernateException {

		//note: dirtyFields==null means we had no snapshot, and we couldn't get one using select-before-update
		//	  oldFields==null just means we had no snapshot to begin with (we might have used select-before-update to get the dirtyFields)

		final boolean[] tableUpdateNeeded = getTableUpdateNeeded( dirtyFields, hasDirtyCollection );
		final int span = getTableSpan();

		final boolean[] propsToUpdate;
		final String[] updateStrings;
		EntityEntry entry = session.getPersistenceContext().getEntry( object );

		// Ensure that an immutable or non-modifiable entity is not being updated unless it is
		// in the process of being deleted.
		if ( entry == null && ! isMutable() ) {
			throw new IllegalStateException( "Updating immutable entity that is not in session yet!" );
		}
		if ( ( entityMetamodel.isDynamicUpdate() && dirtyFields != null ) ) {
			// We need to generate the UPDATE SQL when dynamic-update="true"
			propsToUpdate = getPropertiesToUpdate( dirtyFields, hasDirtyCollection );
			// don't need to check laziness (dirty checking algorithm handles that)
			updateStrings = new String[span];
			for ( int j = 0; j < span; j++ ) {
				updateStrings[j] = tableUpdateNeeded[j] ?
						generateUpdateString( propsToUpdate, j, oldFields, j == 0 && rowId != null ) :
						null;
			}
		}
		else if ( ! isModifiableEntity( entry ) ) {
			// We need to generate UPDATE SQL when a non-modifiable entity (e.g., read-only or immutable)
			// needs:
			// - to have references to transient entities set to null before being deleted
			// - to have version incremented do to a "dirty" association
			// If dirtyFields == null, then that means that there are no dirty properties to
			// to be updated; an empty array for the dirty fields needs to be passed to
			// getPropertiesToUpdate() instead of null.
			propsToUpdate = getPropertiesToUpdate(
					( dirtyFields == null ? ArrayHelper.EMPTY_INT_ARRAY : dirtyFields ),
					hasDirtyCollection
			);
			// don't need to check laziness (dirty checking algorithm handles that)
			updateStrings = new String[span];
			for ( int j = 0; j < span; j++ ) {
				updateStrings[j] = tableUpdateNeeded[j] ?
						generateUpdateString( propsToUpdate, j, oldFields, j == 0 && rowId != null ) :
						null;
			}
		}
		else {
			// For the case of dynamic-update="false", or no snapshot, we use the static SQL
			updateStrings = getUpdateStrings(
					rowId != null,
					hasUninitializedLazyProperties( object, session.getEntityMode() )
				);
			propsToUpdate = getPropertyUpdateability( object, session.getEntityMode() );
		}

		for ( int j = 0; j < span; j++ ) {
			// Now update only the tables with dirty properties (and the table with the version number)
			if ( tableUpdateNeeded[j] ) {
				updateOrInsert(
						id,
						fields,
						oldFields,
						j == 0 ? rowId : null,
						propsToUpdate,
						j,
						oldVersion,
						object,
						updateStrings[j],
						session
					);
			}
		}
	}

	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
			throws HibernateException {

		final int span = getTableSpan();
		final Serializable id;
		if ( entityMetamodel.isDynamicInsert() ) {
			// For the case of dynamic-insert="true", we need to generate the INSERT SQL
			boolean[] notNull = getPropertiesToInsert( fields );
			id = insert( fields, notNull, generateInsertString( true, notNull ), object, session );
			for ( int j = 1; j < span; j++ ) {
				insert( id, fields, notNull, j, generateInsertString( notNull, j ), object, session );
			}
		}
		else {
			// For the case of dynamic-insert="false", use the static SQL
			id = insert( fields, getPropertyInsertability(), getSQLIdentityInsertString(), object, session );
			for ( int j = 1; j < span; j++ ) {
				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
			}
		}
		return id;
	}

	public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session)
			throws HibernateException {

		final int span = getTableSpan();
		if ( entityMetamodel.isDynamicInsert() ) {
			// For the case of dynamic-insert="true", we need to generate the INSERT SQL
			boolean[] notNull = getPropertiesToInsert( fields );
			for ( int j = 0; j < span; j++ ) {
				insert( id, fields, notNull, j, generateInsertString( notNull, j ), object, session );
			}
		}
		else {
			// For the case of dynamic-insert="false", use the static SQL
			for ( int j = 0; j < span; j++ ) {
				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
			}
		}
	}

	/**
	 * Delete an object
	 */
	public void delete(Serializable id, Object version, Object object, SessionImplementor session)
			throws HibernateException {
		final int span = getTableSpan();
		boolean isImpliedOptimisticLocking = !entityMetamodel.isVersioned() && entityMetamodel.getOptimisticLockMode() > Versioning.OPTIMISTIC_LOCK_VERSION;
		Object[] loadedState = null;
		if ( isImpliedOptimisticLocking ) {
			// need to treat this as if it where optimistic-lock="all" (dirty does *not* make sense);
			// first we need to locate the "loaded" state
			//
			// Note, it potentially could be a proxy, so doAfterTransactionCompletion the location the safe way...
			EntityKey key = new EntityKey( id, this, session.getEntityMode() );
			Object entity = session.getPersistenceContext().getEntity( key );
			if ( entity != null ) {
				EntityEntry entry = session.getPersistenceContext().getEntry( entity );
				loadedState = entry.getLoadedState();
			}
		}

		final String[] deleteStrings;
		if ( isImpliedOptimisticLocking && loadedState != null ) {
			// we need to utilize dynamic delete statements
			deleteStrings = generateSQLDeletStrings( loadedState );
		}
		else {
			// otherwise, utilize the static delete statements
			deleteStrings = getSQLDeleteStrings();
		}

		for ( int j = span - 1; j >= 0; j-- ) {
			delete( id, version, j, object, deleteStrings[j], session, loadedState );
		}

	}

	private String[] generateSQLDeletStrings(Object[] loadedState) {
		int span = getTableSpan();
		String[] deleteStrings = new String[span];
		for ( int j = span - 1; j >= 0; j-- ) {
			Delete delete = new Delete()
					.setTableName( getTableName( j ) )
					.addPrimaryKeyColumns( getKeyColumns( j ) );
			if ( getFactory().getSettings().isCommentsEnabled() ) {
				delete.setComment( "delete " + getEntityName() + " [" + j + "]" );
			}

			boolean[] versionability = getPropertyVersionability();
			Type[] types = getPropertyTypes();
			for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
				if ( isPropertyOfTable( i, j ) && versionability[i] ) {
					// this property belongs to the table and it is not specifically
					// excluded from optimistic locking by optimistic-lock="false"
					String[] propertyColumnNames = getPropertyColumnNames( i );
					boolean[] propertyNullness = types[i].toColumnNullness( loadedState[i], getFactory() );
					for ( int k = 0; k < propertyNullness.length; k++ ) {
						if ( propertyNullness[k] ) {
							delete.addWhereFragment( propertyColumnNames[k] + " = ?" );
						}
						else {
							delete.addWhereFragment( propertyColumnNames[k] + " is null" );
						}
					}
				}
			}
			deleteStrings[j] = delete.toStatementString();
		}
		return deleteStrings;
	}

	protected void logStaticSQL() {
		if ( log.isDebugEnabled() ) {
			log.debug( "Static SQL for entity: " + getEntityName() );
			if ( sqlLazySelectString != null ) {
				log.debug( " Lazy select: " + sqlLazySelectString );
			}
			if ( sqlVersionSelectString != null ) {
				log.debug( " Version select: " + sqlVersionSelectString );
			}
			if ( sqlSnapshotSelectString != null ) {
				log.debug( " Snapshot select: " + sqlSnapshotSelectString );
			}
			for ( int j = 0; j < getTableSpan(); j++ ) {
				log.debug( " Insert " + j + ": " + getSQLInsertStrings()[j] );
				log.debug( " Update " + j + ": " + getSQLUpdateStrings()[j] );
				log.debug( " Delete " + j + ": " + getSQLDeleteStrings()[j] );

			}
			if ( sqlIdentityInsertString != null ) {
				log.debug( " Identity insert: " + sqlIdentityInsertString );
			}
			if ( sqlUpdateByRowIdString != null ) {
				log.debug( " Update by row id (all fields): " + sqlUpdateByRowIdString );
			}
			if ( sqlLazyUpdateByRowIdString != null ) {
				log.debug( " Update by row id (non-lazy fields): " + sqlLazyUpdateByRowIdString );
			}
			if ( sqlInsertGeneratedValuesSelectString != null ) {
				log.debug( "Insert-generated property select: " + sqlInsertGeneratedValuesSelectString );
			}
			if ( sqlUpdateGeneratedValuesSelectString != null ) {
				log.debug( "Update-generated property select: " + sqlUpdateGeneratedValuesSelectString );
			}
		}
	}

	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
		final StringBuffer sessionFilterFragment = new StringBuffer();
		filterHelper.render( sessionFilterFragment, generateFilterConditionAlias( alias ), enabledFilters );

		return sessionFilterFragment.append( filterFragment( alias ) ).toString();
	}

	public String generateFilterConditionAlias(String rootAlias) {
		return rootAlias;
	}

	public String oneToManyFilterFragment(String alias) throws MappingException {
		return "";
	}

	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
		return getSubclassTableSpan() == 1 ?
				"" : //just a performance opt!
				createJoin( alias, innerJoin, includeSubclasses ).toFromFragmentString();
	}

	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
		return getSubclassTableSpan() == 1 ?
				"" : //just a performance opt!
				createJoin( alias, innerJoin, includeSubclasses ).toWhereFragmentString();
	}

	protected boolean isSubclassTableLazy(int j) {
		return false;
	}

	protected JoinFragment createJoin(String name, boolean innerJoin, boolean includeSubclasses) {
		final String[] idCols = StringHelper.qualify( name, getIdentifierColumnNames() ); //all joins join to the pk of the driving table
		final JoinFragment join = getFactory().getDialect().createOuterJoinFragment();
		final int tableSpan = getSubclassTableSpan();
		for ( int j = 1; j < tableSpan; j++ ) { //notice that we skip the first table; it is the driving table!
			final boolean joinIsIncluded = isClassOrSuperclassTable( j ) ||
					( includeSubclasses && !isSubclassTableSequentialSelect( j ) && !isSubclassTableLazy( j ) );
			if ( joinIsIncluded ) {
				join.addJoin( getSubclassTableName( j ),
						generateTableAlias( name, j ),
						idCols,
						getSubclassTableKeyColumns( j ),
						innerJoin && isClassOrSuperclassTable( j ) && !isInverseTable( j ) && !isNullableTable( j ) ?
						JoinFragment.INNER_JOIN : //we can inner join to superclass tables (the row MUST be there)
						JoinFragment.LEFT_OUTER_JOIN //we can never inner join to subclass tables
					);
			}
		}
		return join;
	}

	protected JoinFragment createJoin(int[] tableNumbers, String drivingAlias) {
		final String[] keyCols = StringHelper.qualify( drivingAlias, getSubclassTableKeyColumns( tableNumbers[0] ) );
		final JoinFragment jf = getFactory().getDialect().createOuterJoinFragment();
		for ( int i = 1; i < tableNumbers.length; i++ ) { //skip the driving table
			final int j = tableNumbers[i];
			jf.addJoin( getSubclassTableName( j ),
					generateTableAlias( getRootAlias(), j ),
					keyCols,
					getSubclassTableKeyColumns( j ),
					isInverseSubclassTable( j ) || isNullableSubclassTable( j ) ?
					JoinFragment.LEFT_OUTER_JOIN :
					JoinFragment.INNER_JOIN );
		}
		return jf;
	}

	protected SelectFragment createSelect(final int[] subclassColumnNumbers,
										  final int[] subclassFormulaNumbers) {

		SelectFragment selectFragment = new SelectFragment();

		int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
		String[] columnAliases = getSubclassColumnAliasClosure();
		String[] columnReaderTemplates = getSubclassColumnReaderTemplateClosure();
		for ( int i = 0; i < subclassColumnNumbers.length; i++ ) {
			int columnNumber = subclassColumnNumbers[i];
			if ( subclassColumnSelectableClosure[columnNumber] ) {
				final String subalias = generateTableAlias( getRootAlias(), columnTableNumbers[columnNumber] );
				selectFragment.addColumnTemplate( subalias, columnReaderTemplates[columnNumber], columnAliases[columnNumber] );
			}
		}

		int[] formulaTableNumbers = getSubclassFormulaTableNumberClosure();
		String[] formulaTemplates = getSubclassFormulaTemplateClosure();
		String[] formulaAliases = getSubclassFormulaAliasClosure();
		for ( int i = 0; i < subclassFormulaNumbers.length; i++ ) {
			int formulaNumber = subclassFormulaNumbers[i];
			final String subalias = generateTableAlias( getRootAlias(), formulaTableNumbers[formulaNumber] );
			selectFragment.addFormula( subalias, formulaTemplates[formulaNumber], formulaAliases[formulaNumber] );
		}

		return selectFragment;
	}

	protected String createFrom(int tableNumber, String alias) {
		return getSubclassTableName( tableNumber ) + ' ' + alias;
	}

	protected String createWhereByKey(int tableNumber, String alias) {
		//TODO: move to .sql package, and refactor with similar things!
		return StringHelper.join( "=? and ",
				StringHelper.qualify( alias, getSubclassTableKeyColumns( tableNumber ) ) ) + "=?";
	}

	protected String renderSelect(
			final int[] tableNumbers,
	        final int[] columnNumbers,
	        final int[] formulaNumbers) {

		Arrays.sort( tableNumbers ); //get 'em in the right order (not that it really matters)

		//render the where and from parts
		int drivingTable = tableNumbers[0];
		final String drivingAlias = generateTableAlias( getRootAlias(), drivingTable ); //we *could* regerate this inside each called method!
		final String where = createWhereByKey( drivingTable, drivingAlias );
		final String from = createFrom( drivingTable, drivingAlias );

		//now render the joins
		JoinFragment jf = createJoin( tableNumbers, drivingAlias );

		//now render the select clause
		SelectFragment selectFragment = createSelect( columnNumbers, formulaNumbers );

		//now tie it all together
		Select select = new Select( getFactory().getDialect() );
		select.setSelectClause( selectFragment.toFragmentString().substring( 2 ) );
		select.setFromClause( from );
		select.setWhereClause( where );
		select.setOuterJoins( jf.toFromFragmentString(), jf.toWhereFragmentString() );
		if ( getFactory().getSettings().isCommentsEnabled() ) {
			select.setComment( "sequential select " + getEntityName() );
		}
		return select.toStatementString();
	}

	private String getRootAlias() {
		return StringHelper.generateAlias( getEntityName() );
	}

	protected void postConstruct(Mapping mapping) throws MappingException {
		initPropertyPaths(mapping);

		//insert/update/delete SQL
		final int joinSpan = getTableSpan();
		sqlDeleteStrings = new String[joinSpan];
		sqlInsertStrings = new String[joinSpan];
		sqlUpdateStrings = new String[joinSpan];
		sqlLazyUpdateStrings = new String[joinSpan];

		sqlUpdateByRowIdString = rowIdName == null ?
				null :
				generateUpdateString( getPropertyUpdateability(), 0, true );
		sqlLazyUpdateByRowIdString = rowIdName == null ?
				null :
				generateUpdateString( getNonLazyPropertyUpdateability(), 0, true );

		for ( int j = 0; j < joinSpan; j++ ) {
			sqlInsertStrings[j] = customSQLInsert[j] == null ?
					generateInsertString( getPropertyInsertability(), j ) :
					customSQLInsert[j];
			sqlUpdateStrings[j] = customSQLUpdate[j] == null ?
					generateUpdateString( getPropertyUpdateability(), j, false ) :
					customSQLUpdate[j];
			sqlLazyUpdateStrings[j] = customSQLUpdate[j] == null ?
					generateUpdateString( getNonLazyPropertyUpdateability(), j, false ) :
					customSQLUpdate[j];
			sqlDeleteStrings[j] = customSQLDelete[j] == null ?
					generateDeleteString( j ) :
					customSQLDelete[j];
		}

		tableHasColumns = new boolean[joinSpan];
		for ( int j = 0; j < joinSpan; j++ ) {
			tableHasColumns[j] = sqlUpdateStrings[j] != null;
		}

		//select SQL
		sqlSnapshotSelectString = generateSnapshotSelectString();
		sqlLazySelectString = generateLazySelectString();
		sqlVersionSelectString = generateSelectVersionString();
		if ( hasInsertGeneratedProperties() ) {
			sqlInsertGeneratedValuesSelectString = generateInsertGeneratedValuesSelectString();
		}
		if ( hasUpdateGeneratedProperties() ) {
			sqlUpdateGeneratedValuesSelectString = generateUpdateGeneratedValuesSelectString();
		}
		if ( isIdentifierAssignedByInsert() ) {
			identityDelegate = ( ( PostInsertIdentifierGenerator ) getIdentifierGenerator() )
					.getInsertGeneratedIdentifierDelegate( this, getFactory().getDialect(), useGetGeneratedKeys() );
			sqlIdentityInsertString = customSQLInsert[0] == null
					? generateIdentityInsertString( getPropertyInsertability() )
					: customSQLInsert[0];
		}
		else {
			sqlIdentityInsertString = null;
		}

		logStaticSQL();

	}

	public void postInstantiate() throws MappingException {

		createLoaders();
		createUniqueKeyLoaders();
		createQueryLoader();

	}

	private void createLoaders() {
		loaders.put( LockMode.NONE, createEntityLoader( LockMode.NONE ) );

		UniqueEntityLoader readLoader = createEntityLoader( LockMode.READ );
		loaders.put( LockMode.READ, readLoader );

		//TODO: inexact, what we really need to know is: are any outer joins used?
		boolean disableForUpdate = getSubclassTableSpan() > 1 &&
				hasSubclasses() &&
				!getFactory().getDialect().supportsOuterJoinForUpdate();

		loaders.put(
				LockMode.UPGRADE,
				disableForUpdate ?
						readLoader :
						createEntityLoader( LockMode.UPGRADE )
			);
		loaders.put(
				LockMode.UPGRADE_NOWAIT,
				disableForUpdate ?
						readLoader :
						createEntityLoader( LockMode.UPGRADE_NOWAIT )
			);
		loaders.put(
				LockMode.FORCE,
				disableForUpdate ?
						readLoader :
						createEntityLoader( LockMode.FORCE )
			);
		loaders.put(
				LockMode.PESSIMISTIC_READ,
				disableForUpdate ?
						readLoader :
						createEntityLoader( LockMode.PESSIMISTIC_READ )
			);
		loaders.put(
				LockMode.PESSIMISTIC_WRITE,
				disableForUpdate ?
						readLoader :
						createEntityLoader( LockMode.PESSIMISTIC_WRITE )
			);
		loaders.put(
				LockMode.PESSIMISTIC_FORCE_INCREMENT,
				disableForUpdate ?
						readLoader :
						createEntityLoader( LockMode.PESSIMISTIC_FORCE_INCREMENT )
			);
		loaders.put( LockMode.OPTIMISTIC, createEntityLoader( LockMode.OPTIMISTIC) );
		loaders.put( LockMode.OPTIMISTIC_FORCE_INCREMENT, createEntityLoader(LockMode.OPTIMISTIC_FORCE_INCREMENT) );
	
		loaders.put(
				"merge",
				new CascadeEntityLoader( this, CascadingAction.MERGE, getFactory() )
			);
		loaders.put(
				"refresh",
				new CascadeEntityLoader( this, CascadingAction.REFRESH, getFactory() )
			);
	}

	protected void createQueryLoader() {
		if ( loaderName != null ) {
			queryLoader = new NamedQueryLoader( loaderName, this );
		}
	}

	/**
	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
	 * depending upon the value of the <tt>lock</tt> parameter
	 */
	public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
		return load( id, optionalObject, new LockOptions().setLockMode(lockMode), session );
	}

	/**
	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
	 * depending upon the value of the <tt>lock</tt> parameter
	 */
	public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
			throws HibernateException {

		if ( log.isTraceEnabled() ) {
			log.trace(
					"Fetching entity: " +
					MessageHelper.infoString( this, id, getFactory() )
				);
		}

		final UniqueEntityLoader loader = getAppropriateLoader(lockOptions, session );
		return loader.load( id, optionalObject, session, lockOptions );
	}

	public void registerAffectingFetchProfile(String fetchProfileName) {
		affectingFetchProfileNames.add( fetchProfileName );
	}

	private boolean isAffectedByEnabledFetchProfiles(SessionImplementor session) {
		Iterator itr = session.getLoadQueryInfluencers().getEnabledFetchProfileNames().iterator();
		while ( itr.hasNext() ) {
			if ( affectingFetchProfileNames.contains( itr.next() ) ) {
				return true;
			}
		}
		return false;
	}

	private boolean isAffectedByEnabledFilters(SessionImplementor session) {
		return session.getLoadQueryInfluencers().hasEnabledFilters()
				&& filterHelper.isAffectedBy( session.getLoadQueryInfluencers().getEnabledFilters() );
	}

	private UniqueEntityLoader getAppropriateLoader(LockOptions lockOptions, SessionImplementor session) {
		if ( queryLoader != null ) {
			// if the user specified a custom query loader we need to that
			// regardless of any other consideration
			return queryLoader;
		}
		else if ( isAffectedByEnabledFilters( session ) ) {
			// because filters affect the rows returned (because they add
			// restirctions) these need to be next in precendence
			return createEntityLoader(lockOptions, session.getLoadQueryInfluencers() );
		}
		else if ( session.getLoadQueryInfluencers().getInternalFetchProfile() != null && LockMode.UPGRADE.greaterThan( lockOptions.getLockMode() ) ) {
			// Next, we consider whether an 'internal' fetch profile has been set.
			// This indicates a special fetch profile Hibernate needs applied
			// (for its merge loading process e.g.).
			return ( UniqueEntityLoader ) loaders.get( session.getLoadQueryInfluencers().getInternalFetchProfile() );
		}
		else if ( isAffectedByEnabledFetchProfiles( session ) ) {
			// If the session has associated influencers we need to adjust the
			// SQL query used for loading based on those influencers
			return createEntityLoader(lockOptions, session.getLoadQueryInfluencers() );
		}
		else if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
			return createEntityLoader( lockOptions, session.getLoadQueryInfluencers() );
		}
		else {
			return ( UniqueEntityLoader ) loaders.get( lockOptions.getLockMode() );
		}
	}

	private boolean isAllNull(Object[] array, int tableNumber) {
		for ( int i = 0; i < array.length; i++ ) {
			if ( isPropertyOfTable( i, tableNumber ) && array[i] != null ) {
				return false;
			}
		}
		return true;
	}

	public boolean isSubclassPropertyNullable(int i) {
		return subclassPropertyNullabilityClosure[i];
	}

	/**
	 * Transform the array of property indexes to an array of booleans,
	 * true when the property is dirty
	 */
	protected final boolean[] getPropertiesToUpdate(final int[] dirtyProperties, final boolean hasDirtyCollection) {
		final boolean[] propsToUpdate = new boolean[ entityMetamodel.getPropertySpan() ];
		final boolean[] updateability = getPropertyUpdateability(); //no need to check laziness, dirty checking handles that
		for ( int j = 0; j < dirtyProperties.length; j++ ) {
			int property = dirtyProperties[j];
			if ( updateability[property] ) {
				propsToUpdate[property] = true;
			}
		}
		if ( isVersioned() && updateability[getVersionProperty() ]) {
			propsToUpdate[ getVersionProperty() ] =
				Versioning.isVersionIncrementRequired( dirtyProperties, hasDirtyCollection, getPropertyVersionability() );
		}
		return propsToUpdate;
	}

	/**
	 * Transform the array of property indexes to an array of booleans,
	 * true when the property is insertable and non-null
	 */
	protected boolean[] getPropertiesToInsert(Object[] fields) {
		boolean[] notNull = new boolean[fields.length];
		boolean[] insertable = getPropertyInsertability();
		for ( int i = 0; i < fields.length; i++ ) {
			notNull[i] = insertable[i] && fields[i] != null;
		}
		return notNull;
	}

	/**
	 * Locate the property-indices of all properties considered to be dirty.
	 *
	 * @param currentState The current state of the entity (the state to be checked).
	 * @param previousState The previous state of the entity (the state to be checked against).
	 * @param entity The entity for which we are checking state dirtiness.
	 * @param session The session in which the check is ccurring.
	 * @return <tt>null</tt> or the indices of the dirty properties
	 * @throws HibernateException
	 */
	public int[] findDirty(Object[] currentState, Object[] previousState, Object entity, SessionImplementor session)
	throws HibernateException {
		int[] props = TypeFactory.findDirty(
				entityMetamodel.getProperties(),
				currentState,
				previousState,
				propertyColumnUpdateable,
				hasUninitializedLazyProperties( entity, session.getEntityMode() ),
				session
			);
		if ( props == null ) {
			return null;
		}
		else {
			logDirtyProperties( props );
			return props;
		}
	}

	/**
	 * Locate the property-indices of all properties considered to be dirty.
	 *
	 * @param old The old state of the entity.
	 * @param current The current state of the entity.
	 * @param entity The entity for which we are checking state modification.
	 * @param session The session in which the check is ccurring.
	 * @return <tt>null</tt> or the indices of the modified properties
	 * @throws HibernateException
	 */
	public int[] findModified(Object[] old, Object[] current, Object entity, SessionImplementor session)
	throws HibernateException {
		int[] props = TypeFactory.findModified(
				entityMetamodel.getProperties(),
				current,
				old,
				propertyColumnUpdateable,
				hasUninitializedLazyProperties( entity, session.getEntityMode() ),
				session
			);
		if ( props == null ) {
			return null;
		}
		else {
			logDirtyProperties( props );
			return props;
		}
	}

	/**
	 * Which properties appear in the SQL update?
	 * (Initialized, updateable ones!)
	 */
	protected boolean[] getPropertyUpdateability(Object entity, EntityMode entityMode) {
		return hasUninitializedLazyProperties( entity, entityMode ) ?
				getNonLazyPropertyUpdateability() :
				getPropertyUpdateability();
	}

	private void logDirtyProperties(int[] props) {
		if ( log.isTraceEnabled() ) {
			for ( int i = 0; i < props.length; i++ ) {
				String propertyName = entityMetamodel.getProperties()[ props[i] ].getName();
				log.trace( StringHelper.qualify( getEntityName(), propertyName ) + " is dirty" );
			}
		}
	}

	protected EntityTuplizer getTuplizer(SessionImplementor session) {
		return getTuplizer( session.getEntityMode() );
	}

	protected EntityTuplizer getTuplizer(EntityMode entityMode) {
		return entityMetamodel.getTuplizer( entityMode );
	}

	public SessionFactoryImplementor getFactory() {
		return factory;
	}

	public EntityMetamodel getEntityMetamodel() {
		return entityMetamodel;
	}

	public boolean hasCache() {
		return cacheAccessStrategy != null;
	}

	public EntityRegionAccessStrategy getCacheAccessStrategy() {
		return cacheAccessStrategy;
	}

	public CacheEntryStructure getCacheEntryStructure() {
		return cacheEntryStructure;
	}

	public Comparator getVersionComparator() {
		return isVersioned() ? getVersionType().getComparator() : null;
	}

	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public final String getEntityName() {
		return entityMetamodel.getName();
	}

	public EntityType getEntityType() {
		return entityMetamodel.getEntityType();
	}

	public boolean isPolymorphic() {
		return entityMetamodel.isPolymorphic();
	}

	public boolean isInherited() {
		return entityMetamodel.isInherited();
	}

	public boolean hasCascades() {
		return entityMetamodel.hasCascades();
	}

	public boolean hasIdentifierProperty() {
		return !entityMetamodel.getIdentifierProperty().isVirtual();
	}

	public VersionType getVersionType() {
		return ( VersionType ) locateVersionType();
	}

	private Type locateVersionType() {
		return entityMetamodel.getVersionProperty() == null ?
				null :
				entityMetamodel.getVersionProperty().getType();
	}

	public int getVersionProperty() {
		return entityMetamodel.getVersionPropertyIndex();
	}

	public boolean isVersioned() {
		return entityMetamodel.isVersioned();
	}

	public boolean isIdentifierAssignedByInsert() {
		return entityMetamodel.getIdentifierProperty().isIdentifierAssignedByInsert();
	}

	public boolean hasLazyProperties() {
		return entityMetamodel.hasLazyProperties();
	}

//	public boolean hasUninitializedLazyProperties(Object entity) {
//		if ( hasLazyProperties() ) {
//			InterceptFieldCallback callback = ( ( InterceptFieldEnabled ) entity ).getInterceptFieldCallback();
//			return callback != null && !( ( FieldInterceptor ) callback ).isInitialized();
//		}
//		else {
//			return false;
//		}
//	}

	public void afterReassociate(Object entity, SessionImplementor session) {
		//if ( hasLazyProperties() ) {
		if ( FieldInterceptionHelper.isInstrumented( entity ) ) {
			FieldInterceptor interceptor = FieldInterceptionHelper.extractFieldInterceptor( entity );
			if ( interceptor != null ) {
				interceptor.setSession( session );
			}
			else {
				FieldInterceptor fieldInterceptor = FieldInterceptionHelper.injectFieldInterceptor(
						entity,
						getEntityName(),
						null,
						session
				);
				fieldInterceptor.dirty();
			}
		}
	}

	public Boolean isTransient(Object entity, SessionImplementor session) throws HibernateException {
		final Serializable id;
		if ( canExtractIdOutOfEntity() ) {
			id = getIdentifier( entity, session );
		}
		else {
			id = null;
		}
		// we *always* assume an instance with a null
		// identifier or no identifier property is unsaved!
		if ( id == null ) {
			return Boolean.TRUE;
		}

		// check the version unsaved-value, if appropriate
		final Object version = getVersion( entity, session.getEntityMode() );
		if ( isVersioned() ) {
			// let this take precedence if defined, since it works for
			// assigned identifiers
			Boolean result = entityMetamodel.getVersionProperty()
					.getUnsavedValue().isUnsaved( version );
			if ( result != null ) {
				return result;
			}
		}

		// check the id unsaved-value
		Boolean result = entityMetamodel.getIdentifierProperty()
				.getUnsavedValue().isUnsaved( id );
		if ( result != null ) {
			return result;
		}

		// check to see if it is in the second-level cache
		if ( hasCache() ) {
			CacheKey ck = new CacheKey(
					id,
					getIdentifierType(),
					getRootEntityName(),
					session.getEntityMode(),
					session.getFactory()
				);
			if ( getCacheAccessStrategy().get( ck, session.getTimestamp() ) != null ) {
				return Boolean.FALSE;
			}
		}

		return null;
	}

	public boolean hasCollections() {
		return entityMetamodel.hasCollections();
	}

	public boolean hasMutableProperties() {
		return entityMetamodel.hasMutableProperties();
	}

	public boolean isMutable() {
		return entityMetamodel.isMutable();
	}

	private boolean isModifiableEntity(EntityEntry entry) {

		return ( entry == null ? isMutable() : entry.isModifiableEntity() );
	}

	public boolean isAbstract() {
		return entityMetamodel.isAbstract();
	}

	public boolean hasSubclasses() {
		return entityMetamodel.hasSubclasses();
	}

	public boolean hasProxy() {
		return entityMetamodel.isLazy();
	}

	public IdentifierGenerator getIdentifierGenerator() throws HibernateException {
		return entityMetamodel.getIdentifierProperty().getIdentifierGenerator();
	}

	public String getRootEntityName() {
		return entityMetamodel.getRootName();
	}

	public ClassMetadata getClassMetadata() {
		return this;
	}

	public String getMappedSuperclass() {
		return entityMetamodel.getSuperclass();
	}

	public boolean isExplicitPolymorphism() {
		return entityMetamodel.isExplicitPolymorphism();
	}

	protected boolean useDynamicUpdate() {
		return entityMetamodel.isDynamicUpdate();
	}

	protected boolean useDynamicInsert() {
		return entityMetamodel.isDynamicInsert();
	}

	protected boolean hasEmbeddedCompositeIdentifier() {
		return entityMetamodel.getIdentifierProperty().isEmbedded();
	}

	public boolean canExtractIdOutOfEntity() {
		return hasIdentifierProperty() || hasEmbeddedCompositeIdentifier() || hasIdentifierMapper();
	}

	private boolean hasIdentifierMapper() {
		return entityMetamodel.getIdentifierProperty().hasIdentifierMapper();
	}

	public String[] getKeyColumnNames() {
		return getIdentifierColumnNames();
	}

	public String getName() {
		return getEntityName();
	}

	public boolean isCollection() {
		return false;
	}

	public boolean consumesEntityAlias() {
		return true;
	}

	public boolean consumesCollectionAlias() {
		return false;
	}

	public Type getPropertyType(String propertyName) throws MappingException {
		return propertyMapping.toType(propertyName);
	}

	public Type getType() {
		return entityMetamodel.getEntityType();
	}

	public boolean isSelectBeforeUpdateRequired() {
		return entityMetamodel.isSelectBeforeUpdate();
	}

	protected final int optimisticLockMode() {
		return entityMetamodel.getOptimisticLockMode();
	}

	public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
		return entityMetamodel.getTuplizer( session.getEntityMode() )
				.createProxy( id, session );
	}

	public String toString() {
		return StringHelper.unqualify( getClass().getName() ) +
				'(' + entityMetamodel.getName() + ')';
	}

	public final String selectFragment(
			Joinable rhs,
			String rhsAlias,
			String lhsAlias,
			String entitySuffix,
			String collectionSuffix,
			boolean includeCollectionColumns) {
		return selectFragment( lhsAlias, entitySuffix );
	}

	public boolean isInstrumented(EntityMode entityMode) {
		EntityTuplizer tuplizer = entityMetamodel.getTuplizerOrNull(entityMode);
		return tuplizer!=null && tuplizer.isInstrumented();
	}

	public boolean hasInsertGeneratedProperties() {
		return entityMetamodel.hasInsertGeneratedValues();
	}

	public boolean hasUpdateGeneratedProperties() {
		return entityMetamodel.hasUpdateGeneratedValues();
	}

	public boolean isVersionPropertyGenerated() {
		return isVersioned() && ( getPropertyUpdateGenerationInclusions() [ getVersionProperty() ] != ValueInclusion.NONE );
	}

	public boolean isVersionPropertyInsertable() {
		return isVersioned() && getPropertyInsertability() [ getVersionProperty() ];
	}

	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
		getTuplizer( session ).afterInitialize( entity, lazyPropertiesAreUnfetched, session );
	}

	public String[] getPropertyNames() {
		return entityMetamodel.getPropertyNames();
	}

	public Type[] getPropertyTypes() {
		return entityMetamodel.getPropertyTypes();
	}

	public boolean[] getPropertyLaziness() {
		return entityMetamodel.getPropertyLaziness();
	}

	public boolean[] getPropertyUpdateability() {
		return entityMetamodel.getPropertyUpdateability();
	}

	public boolean[] getPropertyCheckability() {
		return entityMetamodel.getPropertyCheckability();
	}

	public boolean[] getNonLazyPropertyUpdateability() {
		return entityMetamodel.getNonlazyPropertyUpdateability();
	}

	public boolean[] getPropertyInsertability() {
		return entityMetamodel.getPropertyInsertability();
	}

	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
		return entityMetamodel.getPropertyInsertGenerationInclusions();
	}

	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
		return entityMetamodel.getPropertyUpdateGenerationInclusions();
	}

	public boolean[] getPropertyNullability() {
		return entityMetamodel.getPropertyNullability();
	}

	public boolean[] getPropertyVersionability() {
		return entityMetamodel.getPropertyVersionability();
	}

	public CascadeStyle[] getPropertyCascadeStyles() {
		return entityMetamodel.getCascadeStyles();
	}

	public final Class getMappedClass(EntityMode entityMode) {
		Tuplizer tup = entityMetamodel.getTuplizerOrNull(entityMode);
		return tup==null ? null : tup.getMappedClass();
	}

	public boolean implementsLifecycle(EntityMode entityMode) {
		return getTuplizer( entityMode ).isLifecycleImplementor();
	}

	public boolean implementsValidatable(EntityMode entityMode) {
		return getTuplizer( entityMode ).isValidatableImplementor();
	}

	public Class getConcreteProxyClass(EntityMode entityMode) {
		return getTuplizer( entityMode ).getConcreteProxyClass();
	}

	public void setPropertyValues(Object object, Object[] values, EntityMode entityMode)
			throws HibernateException {
		getTuplizer( entityMode ).setPropertyValues( object, values );
	}

	public void setPropertyValue(Object object, int i, Object value, EntityMode entityMode)
			throws HibernateException {
		getTuplizer( entityMode ).setPropertyValue( object, i, value );
	}

	public Object[] getPropertyValues(Object object, EntityMode entityMode)
			throws HibernateException {
		return getTuplizer( entityMode ).getPropertyValues( object );
	}

	public Object getPropertyValue(Object object, int i, EntityMode entityMode)
			throws HibernateException {
		return getTuplizer( entityMode ).getPropertyValue( object , i );
	}

	public Object getPropertyValue(Object object, String propertyName, EntityMode entityMode)
			throws HibernateException {
		return getTuplizer( entityMode ).getPropertyValue( object, propertyName );
	}

	public Serializable getIdentifier(Object object, EntityMode entityMode) throws HibernateException {
		return getTuplizer( entityMode ).getIdentifier( object, null );
	}

	public Serializable getIdentifier(Object entity, SessionImplementor session) {
		return getTuplizer( session.getEntityMode() ).getIdentifier( entity, session );
	}

	/**
	 * {@inheritDoc}
	 */
	public void setIdentifier(Object entity, Serializable id, EntityMode entityMode)
			throws HibernateException {
		getTuplizer( entityMode ).setIdentifier( entity, id, null );
	}

	/**
	 * {@inheritDoc}
	 */
	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
		getTuplizer( session ).setIdentifier( entity, id, session );
	}

	public Object getVersion(Object object, EntityMode entityMode)
			throws HibernateException {
		return getTuplizer( entityMode ).getVersion( object );
	}

	/**
	 * {@inheritDoc}
	 */
	public Object instantiate(Serializable id, EntityMode entityMode)
			throws HibernateException {
		return getTuplizer( entityMode ).instantiate( id, null );
	}

	/**
	 * {@inheritDoc}
	 */
	public Object instantiate(Serializable id, SessionImplementor session)
			throws HibernateException {
		return getTuplizer( session ).instantiate( id, session );
	}

	public boolean isInstance(Object object, EntityMode entityMode) {
		return getTuplizer( entityMode ).isInstance( object );
	}

	public boolean hasUninitializedLazyProperties(Object object, EntityMode entityMode) {
		return getTuplizer( entityMode ).hasUninitializedLazyProperties( object );
	}

	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, EntityMode entityMode) {
		getTuplizer( entityMode ).resetIdentifier( entity, currentId, currentVersion, null );
	}

	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
		getTuplizer( session ).resetIdentifier( entity, currentId, currentVersion, session );
	}

	/**
	 * {@inheritDoc}
	 */
	public EntityPersister getSubclassEntityPersister(
			Object instance,
			SessionFactoryImplementor factory,
			EntityMode entityMode) {
		if ( !hasSubclasses() ) {
			return this;
		}
		else {
			final String concreteEntityName = getTuplizer( entityMode )
					.determineConcreteSubclassEntityName( instance, factory );
			if ( concreteEntityName == null || getEntityName().equals( concreteEntityName ) ) {
				// the contract of EntityTuplizer.determineConcreteSubclassEntityName says that returning null
				// is an indication that the specified entity-name (this.getEntityName) should be used.
				return this;
			}
			else {
				return factory.getEntityPersister( concreteEntityName );
			}
		}
	}

	public EntityMode guessEntityMode(Object object) {
		return entityMetamodel.guessEntityMode(object);
	}

	public boolean isMultiTable() {
		return false;
	}

	public String getTemporaryIdTableName() {
		return temporaryIdTableName;
	}

	public String getTemporaryIdTableDDL() {
		return temporaryIdTableDDL;
	}

	protected int getPropertySpan() {
		return entityMetamodel.getPropertySpan();
	}

	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) throws HibernateException {
		return getTuplizer( session.getEntityMode() ).getPropertyValuesToInsert( object, mergeMap, session );
	}

	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
		if ( !hasInsertGeneratedProperties() ) {
			throw new AssertionFailure("no insert-generated properties");
		}
		processGeneratedProperties( id, entity, state, session, sqlInsertGeneratedValuesSelectString, getPropertyInsertGenerationInclusions() );
	}

	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
		if ( !hasUpdateGeneratedProperties() ) {
			throw new AssertionFailure("no update-generated properties");
		}
		processGeneratedProperties( id, entity, state, session, sqlUpdateGeneratedValuesSelectString, getPropertyUpdateGenerationInclusions() );
	}

	private void processGeneratedProperties(
			Serializable id,
	        Object entity,
	        Object[] state,
	        SessionImplementor session,
	        String selectionSQL,
	        ValueInclusion[] includeds) {

		session.getBatcher().executeBatch(); //force immediate execution of the insert

		try {
			PreparedStatement ps = session.getBatcher().prepareSelectStatement( selectionSQL );
			try {
				getIdentifierType().nullSafeSet( ps, id, 1, session );
				ResultSet rs = ps.executeQuery();
				try {
					if ( !rs.next() ) {
						throw new HibernateException(
								"Unable to locate row for retrieval of generated properties: " +
								MessageHelper.infoString( this, id, getFactory() )
							);
					}
					for ( int i = 0; i < getPropertySpan(); i++ ) {
						if ( includeds[i] != ValueInclusion.NONE ) {
							Object hydratedState = getPropertyTypes()[i].hydrate( rs, getPropertyAliases( "", i ), session, entity );
							state[i] = getPropertyTypes()[i].resolve( hydratedState, session, entity );
							setPropertyValue( entity, i, state[i], session.getEntityMode() );
						}
					}
				}
				finally {
					if ( rs != null ) {
						rs.close();
					}
				}
			}
			finally {
				session.getBatcher().closeStatement( ps );
			}
		}
		catch( SQLException sqle ) {
			throw JDBCExceptionHelper.convert(
					getFactory().getSQLExceptionConverter(),
					sqle,
					"unable to select generated column values",
					selectionSQL
			);
		}

	}

	public String getIdentifierPropertyName() {
		return entityMetamodel.getIdentifierProperty().getName();
	}

	public Type getIdentifierType() {
		return entityMetamodel.getIdentifierProperty().getType();
	}

	public boolean hasSubselectLoadableCollections() {
		return hasSubselectLoadableCollections;
	}

	public int[] getNaturalIdentifierProperties() {
		return entityMetamodel.getNaturalIdentifierProperties();
	}

	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
		if ( !hasNaturalIdentifier() ) {
			throw new MappingException( "persistent class did not define a natural-id : " + MessageHelper.infoString( this ) );
		}
		if ( log.isTraceEnabled() ) {
			log.trace( "Getting current natural-id snapshot state for: " + MessageHelper.infoString( this, id, getFactory() ) );
		}

		int[] naturalIdPropertyIndexes = getNaturalIdentifierProperties();
		int naturalIdPropertyCount = naturalIdPropertyIndexes.length;
		boolean[] naturalIdMarkers = new boolean[ getPropertySpan() ];
		Type[] extractionTypes = new Type[ naturalIdPropertyCount ];
		for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
			extractionTypes[i] = getPropertyTypes()[ naturalIdPropertyIndexes[i] ];
			naturalIdMarkers[ naturalIdPropertyIndexes[i] ] = true;
		}

		///////////////////////////////////////////////////////////////////////
		// TODO : look at perhaps caching this...
		Select select = new Select( getFactory().getDialect() );
		if ( getFactory().getSettings().isCommentsEnabled() ) {
			select.setComment( "get current natural-id state " + getEntityName() );
		}
		select.setSelectClause( concretePropertySelectFragmentSansLeadingComma( getRootAlias(), naturalIdMarkers ) );
		select.setFromClause( fromTableFragment( getRootAlias() ) + fromJoinFragment( getRootAlias(), true, false ) );

		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
		String whereClause = new StringBuffer()
			.append( StringHelper.join( "=? and ",
					aliasedIdColumns ) )
			.append( "=?" )
			.append( whereJoinFragment( getRootAlias(), true, false ) )
			.toString();

		String sql = select.setOuterJoins( "", "" )
				.setWhereClause( whereClause )
				.toStatementString();
		///////////////////////////////////////////////////////////////////////

		Object[] snapshot = new Object[ naturalIdPropertyCount ];
		try {
			PreparedStatement ps = session.getBatcher().prepareSelectStatement( sql );
			try {
				getIdentifierType().nullSafeSet( ps, id, 1, session );
				ResultSet rs = ps.executeQuery();
				try {
					//if there is no resulting row, return null
					if ( !rs.next() ) {
						return null;
					}

					final EntityKey key = new EntityKey( id, this, session.getEntityMode() );
					Object owner = session.getPersistenceContext().getEntity( key );
					for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
						snapshot[i] = extractionTypes[i].hydrate( rs, getPropertyAliases( "", naturalIdPropertyIndexes[i] ), session, null );
						if (extractionTypes[i].isEntityType()) {
							snapshot[i] = extractionTypes[i].resolve(snapshot[i], session, owner);
						}
					}
					return snapshot;
				}
				finally {
					rs.close();
				}
			}
			finally {
				session.getBatcher().closeStatement( ps );
			}
		}
		catch ( SQLException sqle ) {
			throw JDBCExceptionHelper.convert(
					getFactory().getSQLExceptionConverter(),
					sqle,
					"could not retrieve snapshot: " +
					MessageHelper.infoString( this, id, getFactory() ),
			        sql
				);
		}
	}

	protected String concretePropertySelectFragmentSansLeadingComma(String alias, boolean[] include) {
		String concretePropertySelectFragment = concretePropertySelectFragment( alias, include );
		int firstComma = concretePropertySelectFragment.indexOf( ", " );
		if ( firstComma == 0 ) {
			concretePropertySelectFragment = concretePropertySelectFragment.substring( 2 );
		}
		return concretePropertySelectFragment;
	}
	public boolean hasNaturalIdentifier() {
		return entityMetamodel.hasNaturalIdentifier();
	}

	public void setPropertyValue(Object object, String propertyName, Object value, EntityMode entityMode)
			throws HibernateException {
		getTuplizer( entityMode ).setPropertyValue( object, propertyName, value );
	}
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
