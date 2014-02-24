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
package org.hibernate.envers.test.integration.revfordate;

import java.util.Date;
import javax.persistence.EntityManager;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.exception.RevisionDoesNotExistException;
import org.hibernate.envers.test.AbstractEntityTest;
import org.hibernate.envers.test.entities.StrTestEntity;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.hibernate.ejb.Ejb3Configuration;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class RevisionForDate extends AbstractEntityTest {
    private long timestamp1;
    private long timestamp2;
    private long timestamp3;
    private long timestamp4;

    public void configure(Ejb3Configuration cfg) {
        cfg.addAnnotatedClass(StrTestEntity.class);
    }

    @BeforeClass(dependsOnMethods = "init")
    public void initData() throws InterruptedException {
        timestamp1 = System.currentTimeMillis();

        Thread.sleep(100);

        // Revision 1
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        StrTestEntity rfd = new StrTestEntity("x");
        em.persist(rfd);
        Integer id = rfd.getId();
        em.getTransaction().commit();

        timestamp2 = System.currentTimeMillis();

        Thread.sleep(100);

        // Revision 2
        em.getTransaction().begin();
        rfd = em.find(StrTestEntity.class, id);
        rfd.setStr("y");
        em.getTransaction().commit();

        timestamp3 = System.currentTimeMillis();

        Thread.sleep(100);

        // Revision 3
        em.getTransaction().begin();
        rfd = em.find(StrTestEntity.class, id);
        rfd.setStr("z");
        em.getTransaction().commit();

        timestamp4 = System.currentTimeMillis();
    }

    @Test(expectedExceptions = RevisionDoesNotExistException.class)
    public void testTimestamps1() {
        getAuditReader().getRevisionNumberForDate(new Date(timestamp1));
    }

    @Test
    public void testTimestamps() {
        assert getAuditReader().getRevisionNumberForDate(new Date(timestamp2)).intValue() == 1;
        assert getAuditReader().getRevisionNumberForDate(new Date(timestamp3)).intValue() == 2;
        assert getAuditReader().getRevisionNumberForDate(new Date(timestamp4)).intValue() == 3;
    }

    @Test
    public void testDatesForRevisions() {
        AuditReader vr = getAuditReader();
        assert vr.getRevisionNumberForDate(vr.getRevisionDate(1)).intValue() == 1;
        assert vr.getRevisionNumberForDate(vr.getRevisionDate(2)).intValue() == 2;
        assert vr.getRevisionNumberForDate(vr.getRevisionDate(3)).intValue() == 3;
    }

    @Test
    public void testRevisionsForDates() {
        AuditReader vr = getAuditReader();

        assert vr.getRevisionDate(vr.getRevisionNumberForDate(new Date(timestamp2))).getTime() <= timestamp2;
        assert vr.getRevisionDate(vr.getRevisionNumberForDate(new Date(timestamp2)).intValue()+1).getTime() > timestamp2;

        assert vr.getRevisionDate(vr.getRevisionNumberForDate(new Date(timestamp3))).getTime() <= timestamp3;
        assert vr.getRevisionDate(vr.getRevisionNumberForDate(new Date(timestamp3)).intValue()+1).getTime() > timestamp3;

        assert vr.getRevisionDate(vr.getRevisionNumberForDate(new Date(timestamp4))).getTime() <= timestamp4;
    }
}
