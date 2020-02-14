/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
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
package org.hibernate.id.enhanced;

import java.util.Properties;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.hibernate.Hibernate;
import org.hibernate.MappingException;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.cfg.ObjectNameNormalizer;
import org.hibernate.dialect.Dialect;
import org.hibernate.id.PersistentIdentifierGenerator;

/**
 * Tests that SequenceStyleGenerator configures itself as expected
 * in various scenarios
 *
 * @author Steve Ebersole
 * @noinspection deprecation
 */
public class SequenceStyleConfigUnitTest extends TestCase {
	public SequenceStyleConfigUnitTest(String string) {
		super( string );
	}

	public static Test suite() {
		return new TestSuite( SequenceStyleConfigUnitTest.class );
	}


	private void assertClassAssignability(Class expected, Class actual) {
		if ( ! expected.isAssignableFrom( actual ) ) {
			fail( "Actual type [" + actual.getName() + "] is not assignable to expected type [" + expected.getName() + "]" );
		}
	}

	/**
	 * Test all params defaulted with a dialect supporting sequences
	 */
	public void testDefaultedSequenceBackedConfiguration() {
		Dialect dialect = new SequenceDialect();
		Properties props = buildGeneratorPropertiesBase();
		SequenceStyleGenerator generator = new SequenceStyleGenerator();
		generator.configure( Hibernate.LONG, props, dialect );

		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
		assertClassAssignability( OptimizerFactory.NoopOptimizer.class, generator.getOptimizer().getClass() );
		Assert.assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
	}

	private Properties buildGeneratorPropertiesBase() {
		Properties props = new Properties();
		props.put(
				PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER,
				new ObjectNameNormalizer() {
					protected boolean isUseQuotedIdentifiersGlobally() {
						return false;
					}

					protected NamingStrategy getNamingStrategy() {
						return null;
					}
				}
		);
		return props;
	}

	/**
	 * Test all params defaulted with a dialect which does not support sequences
	 */
	public void testDefaultedTableBackedConfiguration() {
		Dialect dialect = new TableDialect();
		Properties props = buildGeneratorPropertiesBase();
		SequenceStyleGenerator generator = new SequenceStyleGenerator();
		generator.configure( Hibernate.LONG, props, dialect );

		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
		assertClassAssignability( OptimizerFactory.NoopOptimizer.class, generator.getOptimizer().getClass() );
		Assert.assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
	}

	/**
	 * Test default optimizer selection for sequence backed generators
	 * based on the configured increment size; both in the case of the
	 * dialect supporting pooled sequences (pooled) and not (hilo)
	 */
	public void testDefaultOptimizerBasedOnIncrementBackedBySequence() {
		Properties props = buildGeneratorPropertiesBase();
		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "10" );

		// for dialects which do not support pooled sequences, we default to pooled+table
		Dialect dialect = new SequenceDialect();
		SequenceStyleGenerator generator = new SequenceStyleGenerator();
		generator.configure( Hibernate.LONG, props, dialect );
		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );
		Assert.assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );

		// for dialects which do support pooled sequences, we default to pooled+sequence
		dialect = new PooledSequenceDialect();
		generator = new SequenceStyleGenerator();
		generator.configure( Hibernate.LONG, props, dialect );
		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );
		Assert.assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
	}

	/**
	 * Test default optimizer selection for table backed generators
	 * based on the configured increment size.  Here we always prefer
	 * pooled.
	 */
	public void testDefaultOptimizerBasedOnIncrementBackedByTable() {
		Properties props = buildGeneratorPropertiesBase();
		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "10" );
		Dialect dialect = new TableDialect();
		SequenceStyleGenerator generator = new SequenceStyleGenerator();
		generator.configure( Hibernate.LONG, props, dialect );
		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );
		Assert.assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
	}

	/**
	 * Test forcing of table as backing strucuture with dialect supporting sequences
	 */
	public void testForceTableUse() {
		Dialect dialect = new SequenceDialect();
		Properties props = buildGeneratorPropertiesBase();
		props.setProperty( SequenceStyleGenerator.FORCE_TBL_PARAM, "true" );
		SequenceStyleGenerator generator = new SequenceStyleGenerator();
		generator.configure( Hibernate.LONG, props, dialect );
		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
		assertClassAssignability( OptimizerFactory.NoopOptimizer.class, generator.getOptimizer().getClass() );
		Assert.assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
	}

	/**
	 * Test explicitly specifying both optimizer and increment
	 */
	public void testExplicitOptimizerWithExplicitIncrementSize() {
		// with sequence ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		final Dialect dialect = new SequenceDialect();

		// optimizer=none w/ increment > 1 => should honor optimizer
		Properties props = buildGeneratorPropertiesBase();
		props.setProperty( SequenceStyleGenerator.OPT_PARAM, OptimizerFactory.NONE );
		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "20" );
		SequenceStyleGenerator generator = new SequenceStyleGenerator();
		generator.configure( Hibernate.LONG, props, dialect );
		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
		assertClassAssignability( OptimizerFactory.NoopOptimizer.class, generator.getOptimizer().getClass() );
		Assert.assertEquals( 1, generator.getOptimizer().getIncrementSize() );
		Assert.assertEquals( 1, generator.getDatabaseStructure().getIncrementSize() );

		// optimizer=hilo w/ increment > 1 => hilo
		props = buildGeneratorPropertiesBase();
		props.setProperty( SequenceStyleGenerator.OPT_PARAM, OptimizerFactory.HILO );
		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "20" );
		generator = new SequenceStyleGenerator();
		generator.configure( Hibernate.LONG, props, dialect );
		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
		assertClassAssignability( OptimizerFactory.HiLoOptimizer.class, generator.getOptimizer().getClass() );
		Assert.assertEquals( 20, generator.getOptimizer().getIncrementSize() );
		Assert.assertEquals( 20, generator.getDatabaseStructure().getIncrementSize() );

		// optimizer=pooled w/ increment > 1 => hilo
		props = buildGeneratorPropertiesBase();
		props.setProperty( SequenceStyleGenerator.OPT_PARAM, OptimizerFactory.POOL );
		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "20" );
		generator = new SequenceStyleGenerator();
		generator.configure( Hibernate.LONG, props, dialect );
		// because the dialect reports to not support pooled seqyences, the expectation is that we will
		// use a table for the backing structure...
		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );
		Assert.assertEquals( 20, generator.getOptimizer().getIncrementSize() );
		Assert.assertEquals( 20, generator.getDatabaseStructure().getIncrementSize() );
	}

	public void testPreferPooledLoSettingHonored() {
		final Dialect dialect = new PooledSequenceDialect();

		Properties props = buildGeneratorPropertiesBase();
		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "20" );
		SequenceStyleGenerator generator = new SequenceStyleGenerator();
		generator.configure( Hibernate.LONG, props, dialect );
		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
		assertClassAssignability( OptimizerFactory.PooledOptimizer.class, generator.getOptimizer().getClass() );

		props.setProperty( Environment.PREFER_POOLED_VALUES_LO, "true" );
		generator = new SequenceStyleGenerator();
		generator.configure( Hibernate.LONG, props, dialect );
		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
		assertClassAssignability( OptimizerFactory.PooledLoOptimizer.class, generator.getOptimizer().getClass() );
	}

	private static class TableDialect extends Dialect {
		public boolean supportsSequences() {
			return false;
		}
	}

	private static class SequenceDialect extends Dialect {
		public boolean supportsSequences() {
			return true;
		}
		public boolean supportsPooledSequences() {
			return false;
		}
		public String getSequenceNextValString(String sequenceName) throws MappingException {
			return "";
		}
	}

	private static class PooledSequenceDialect extends SequenceDialect {
		public boolean supportsPooledSequences() {
			return true;
		}
	}
}
