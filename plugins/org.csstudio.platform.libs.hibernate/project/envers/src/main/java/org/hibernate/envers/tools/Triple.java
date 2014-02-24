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
package org.hibernate.envers.tools;

/**
 * A triple of objects.
 * @param <T1>
 * @param <T2>
 * @param <T3>
 * @author Adam Warski (adamw@aster.pl)
 */
public class Triple<T1, T2, T3> {
    private T1 obj1;
    private T2 obj2;
    private T3 obj3;

    public Triple(T1 obj1, T2 obj2, T3 obj3) {
        this.obj1 = obj1;
        this.obj2 = obj2;
        this.obj3 = obj3;
    }

    public T1 getFirst() {
        return obj1;
    }

    public T2 getSecond() {
        return obj2;
    }

    public T3 getThird() {
        return obj3;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Triple)) return false;

        Triple triple = (Triple) o;

        if (obj1 != null ? !obj1.equals(triple.obj1) : triple.obj1 != null) return false;
        if (obj2 != null ? !obj2.equals(triple.obj2) : triple.obj2 != null) return false;
        if (obj3 != null ? !obj3.equals(triple.obj3) : triple.obj3 != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (obj1 != null ? obj1.hashCode() : 0);
        result = 31 * result + (obj2 != null ? obj2.hashCode() : 0);
        result = 31 * result + (obj3 != null ? obj3.hashCode() : 0);
        return result;
    }

    public static <T1, T2, T3> Triple<T1, T2, T3> make(T1 obj1, T2 obj2, T3 obj3) {
        return new Triple<T1, T2, T3>(obj1, obj2, obj3);
    }
}