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
package org.hibernate.envers.test.integration.reventity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

/**
 * @author Adam Warski (adam at warski dot org)
 */
@Entity
@RevisionEntity
public class CustomBoxedRevEntity {
    @Id
    @GeneratedValue
    @RevisionNumber
    private Integer customId;

    @RevisionTimestamp
    private Long customTimestamp;

    public Integer getCustomId() {
        return customId;
    }

    public void setCustomId(Integer customId) {
        this.customId = customId;
    }

    public Long getCustomTimestamp() {
        return customTimestamp;
    }

    public void setCustomTimestamp(Long customTimestamp) {
        this.customTimestamp = customTimestamp;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomBoxedRevEntity)) return false;

        CustomBoxedRevEntity that = (CustomBoxedRevEntity) o;

        if (customId != null ? !customId.equals(that.customId) : that.customId != null) return false;
        if (customTimestamp != null ? !customTimestamp.equals(that.customTimestamp) : that.customTimestamp != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (customId != null ? customId.hashCode() : 0);
        result = 31 * result + (customTimestamp != null ? customTimestamp.hashCode() : 0);
        return result;
    }
}