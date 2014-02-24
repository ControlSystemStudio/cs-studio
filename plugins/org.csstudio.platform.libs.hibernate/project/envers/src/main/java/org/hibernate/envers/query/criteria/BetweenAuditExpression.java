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
 */
package org.hibernate.envers.query.criteria;

import org.hibernate.envers.configuration.AuditConfiguration;
import org.hibernate.envers.tools.query.Parameters;
import org.hibernate.envers.tools.query.QueryBuilder;
import org.hibernate.envers.query.property.PropertyNameGetter;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class BetweenAuditExpression implements AuditCriterion {
    private PropertyNameGetter propertyNameGetter;
    private Object lo;
    private Object hi;

    public BetweenAuditExpression(PropertyNameGetter propertyNameGetter, Object lo, Object hi) {
        this.propertyNameGetter = propertyNameGetter;
        this.lo = lo;
        this.hi = hi;
    }

    public void addToQuery(AuditConfiguration auditCfg, String entityName, QueryBuilder qb, Parameters parameters) {
        String propertyName = propertyNameGetter.get(auditCfg);
        CriteriaTools.checkPropertyNotARelation(auditCfg, entityName, propertyName);
        parameters.addWhereWithParam(propertyName, ">=", lo);
        parameters.addWhereWithParam(propertyName, "<=", hi);
    }
}
