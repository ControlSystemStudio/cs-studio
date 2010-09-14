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
package org.hibernate.envers.test.integration.naming.ids;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToMany;

import org.hibernate.envers.Audited;

/**
 * ReferencEd entity
 * @author Adam Warski (adam at warski dot org)
 */
@Entity
@IdClass(MulIdNaming.class)
public class JoinMulIdNamingRefEdEntity {
    @Id
    private Integer id1;

    @Id
    private Integer id2;

    @Audited
    private String data;

    @Audited
    @OneToMany(mappedBy="reference")
    private List<JoinMulIdNamingRefIngEntity> reffering;

    public JoinMulIdNamingRefEdEntity() {
    }

    public JoinMulIdNamingRefEdEntity(MulIdNaming id, String data) {
        this.id1 = id.getId1();
        this.id2 = id.getId2();
        this.data = data;
    }

    public JoinMulIdNamingRefEdEntity(String data) {
        this.data = data;
    }

    public Integer getId1() {
        return id1;
    }

    public void setId1(Integer id1) {
        this.id1 = id1;
    }

    public Integer getId2() {
        return id2;
    }

    public void setId2(Integer id2) {
        this.id2 = id2;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<JoinMulIdNamingRefIngEntity> getReffering() {
        return reffering;
    }

    public void setReffering(List<JoinMulIdNamingRefIngEntity> reffering) {
        this.reffering = reffering;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JoinMulIdNamingRefEdEntity)) return false;

        JoinMulIdNamingRefEdEntity that = (JoinMulIdNamingRefEdEntity) o;

        if (data != null ? !data.equals(that.data) : that.data != null) return false;
        if (id1 != null ? !id1.equals(that.id1) : that.id1 != null) return false;
        if (id2 != null ? !id2.equals(that.id2) : that.id2 != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (id1 != null ? id1.hashCode() : 0);
        result = 31 * result + (id2 != null ? id2.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "JoinMulIdNamingRefEdEntity(id1 = " + id1 + ", id2 = " + id2 + ", data = " + data + ")";
    }
}