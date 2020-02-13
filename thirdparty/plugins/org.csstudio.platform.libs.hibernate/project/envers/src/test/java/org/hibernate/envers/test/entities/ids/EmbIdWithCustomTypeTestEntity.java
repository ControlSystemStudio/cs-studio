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
package org.hibernate.envers.test.entities.ids;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import org.hibernate.envers.Audited;

/**
 * @author Slawek Garwol (slawekgarwol at gmail dot com)
 */
@Entity
public class EmbIdWithCustomTypeTestEntity {
    @EmbeddedId
    private EmbIdWithCustomType id;

    @Audited
    private String str1;

    public EmbIdWithCustomTypeTestEntity() {
    }

    public EmbIdWithCustomTypeTestEntity(EmbIdWithCustomType id, String str1) {
        this.id = id;
        this.str1 = str1;
    }

    public EmbIdWithCustomType getId() {
        return id;
    }

    public void setId(EmbIdWithCustomType id) {
        this.id = id;
    }

    public String getStr1() {
        return str1;
    }

    public void setStr1(String str1) {
        this.str1 = str1;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EmbIdWithCustomTypeTestEntity)) return false;

        EmbIdWithCustomTypeTestEntity that = (EmbIdWithCustomTypeTestEntity) obj;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (str1 != null ? !str1.equals(that.str1) : that.str1 != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (id != null ? id.hashCode() : 0);
        result = 31 * result + (str1 != null ? str1.hashCode() : 0);
        return result;
    }
}
