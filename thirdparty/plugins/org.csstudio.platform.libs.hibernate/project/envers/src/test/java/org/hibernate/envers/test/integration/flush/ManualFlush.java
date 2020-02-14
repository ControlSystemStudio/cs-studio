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
package org.hibernate.envers.test.integration.flush;

import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;

import org.hibernate.envers.test.entities.StrTestEntity;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.RevisionType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

import org.hibernate.FlushMode;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class ManualFlush extends AbstractFlushTest {
    private Integer id;

    public FlushMode getFlushMode() {
        return FlushMode.MANUAL;
    }

    @BeforeClass(dependsOnMethods = "initFlush")
    public void initData() {
        // Revision 1
        EntityManager em = getEntityManager();
        em.getTransaction().begin();

        StrTestEntity fe = new StrTestEntity("x");
        em.persist(fe);

        em.getTransaction().commit();

        // No revision - we change the data, but do not flush the session
        em.getTransaction().begin();

        fe = em.find(StrTestEntity.class, fe.getId());
        fe.setStr("y");

        em.getTransaction().commit();

        // Revision 2 - only the first change should be saved
        em.getTransaction().begin();

        fe = em.find(StrTestEntity.class, fe.getId());
        fe.setStr("z");
        em.flush();

        fe = em.find(StrTestEntity.class, fe.getId());
        fe.setStr("z2");

        em.getTransaction().commit();

        //

        id = fe.getId();
    }

    @Test
    public void testRevisionsCounts() {
        assert Arrays.asList(1, 2).equals(getAuditReader().getRevisions(StrTestEntity.class, id));
    }

    @Test
    public void testHistoryOfId() {
        StrTestEntity ver1 = new StrTestEntity("x", id);
        StrTestEntity ver2 = new StrTestEntity("z", id);

        assert getAuditReader().find(StrTestEntity.class, id, 1).equals(ver1);
        assert getAuditReader().find(StrTestEntity.class, id, 2).equals(ver2);
    }

    @Test
    public void testCurrent() {
        assert getEntityManager().find(StrTestEntity.class, id).equals(new StrTestEntity("z", id));
    }

    @Test
    public void testRevisionTypes() {
        @SuppressWarnings({"unchecked"}) List<Object[]> results =
                getAuditReader().createQuery()
                        .forRevisionsOfEntity(StrTestEntity.class, false, true)
                        .add(AuditEntity.id().eq(id))
                        .getResultList();

        assertEquals(results.get(0)[2], RevisionType.ADD);
        assertEquals(results.get(1)[2], RevisionType.MOD);
    }
}
