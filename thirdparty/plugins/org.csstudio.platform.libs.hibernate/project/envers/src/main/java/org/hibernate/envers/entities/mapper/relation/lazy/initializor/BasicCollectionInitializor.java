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
package org.hibernate.envers.entities.mapper.relation.lazy.initializor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.envers.configuration.AuditConfiguration;
import org.hibernate.envers.entities.mapper.relation.MiddleComponentData;
import org.hibernate.envers.entities.mapper.relation.query.RelationQueryGenerator;
import org.hibernate.envers.exception.AuditException;
import org.hibernate.envers.reader.AuditReaderImplementor;

/**
 * Initializes a non-indexed java collection (set or list, eventually sorted).
 * @author Adam Warski (adam at warski dot org)
 */
public class BasicCollectionInitializor<T extends Collection> extends AbstractCollectionInitializor<T> {
    private final Class<? extends T> collectionClass;
    private final MiddleComponentData elementComponentData;

    public BasicCollectionInitializor(AuditConfiguration verCfg,
                                       AuditReaderImplementor versionsReader,
                                       RelationQueryGenerator queryGenerator,
                                       Object primaryKey, Number revision,
                                       Class<? extends T> collectionClass,
                                       MiddleComponentData elementComponentData) {
        super(verCfg, versionsReader, queryGenerator, primaryKey, revision);

        this.collectionClass = collectionClass;
        this.elementComponentData = elementComponentData;
    }

    protected T initializeCollection(int size) {
        try {
            return collectionClass.newInstance();
        } catch (InstantiationException e) {
            throw new AuditException(e);
        } catch (IllegalAccessException e) {
            throw new AuditException(e);
        }
    }

    @SuppressWarnings({"unchecked"})
    protected void addToCollection(T collection, Object collectionRow) {
        Object elementData = ((List) collectionRow).get(elementComponentData.getComponentIndex());

        // If the target entity is not audited, the elements may be the entities already, so we have to check
        // if they are maps or not.
        Object element;
        if (elementData instanceof Map) {
            element = elementComponentData.getComponentMapper().mapToObjectFromFullMap(entityInstantiator,
                    (Map<String, Object>) elementData, null, revision);
        } else {
            element = elementData;
        }
        collection.add(element);
    }
}
