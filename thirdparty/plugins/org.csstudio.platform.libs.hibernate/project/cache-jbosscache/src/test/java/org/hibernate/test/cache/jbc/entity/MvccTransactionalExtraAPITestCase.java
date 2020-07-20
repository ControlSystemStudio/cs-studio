/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2007, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
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
package org.hibernate.test.cache.jbc.entity;

import org.hibernate.cache.access.EntityRegionAccessStrategy;

/**
 * Tests for the "extra API" in EntityRegionAccessStrategy; in this base
 * version using Optimistic locking with TRANSACTIONAL access.
 * <p>
 * By "extra API" we mean those methods that are superfluous to the
 * function of the JBC integration, where the impl is a no-op or a static
 * false return value, UnsupportedOperationException, etc.
 *
 * @author <a href="brian.stansberry@jboss.com">Brian Stansberry</a>
 * @version $Revision: 1 $
 */
public class MvccTransactionalExtraAPITestCase extends OptimisticTransactionalExtraAPITestCase {

    private static EntityRegionAccessStrategy localAccessStrategy;

    /**
     * Create a new PessimisticAccessStrategyExtraAPITestCase.
     *
     * @param name
     */
    public MvccTransactionalExtraAPITestCase(String name) {
        super(name);
    }

    @Override
    protected String getCacheConfigName() {
        return "mvcc-entity";
    }

    @Override
    protected EntityRegionAccessStrategy getEntityAccessStrategy() {
        return localAccessStrategy;
    }

    @Override
    protected void setEntityRegionAccessStrategy(EntityRegionAccessStrategy strategy) {
        localAccessStrategy = strategy;
    }

    @Override
    public void testCacheConfiguration() {
        assertFalse("Using Optimistic locking", isUsingOptimisticLocking());
    }
}
