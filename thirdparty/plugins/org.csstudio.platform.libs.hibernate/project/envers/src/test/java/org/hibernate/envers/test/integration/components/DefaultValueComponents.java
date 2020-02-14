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
package org.hibernate.envers.test.integration.components;

import java.math.BigInteger;
import java.util.Arrays;

import javax.persistence.EntityManager;

import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.envers.test.AbstractEntityTest;
import org.hibernate.envers.test.entities.components.DefaultValueComponent1;
import org.hibernate.envers.test.entities.components.DefaultValueComponent2;
import org.hibernate.envers.test.entities.components.DefaultValueComponentTestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test class for components with default values.
 * 
 * @see <a
 *      href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-5288">
 *      Hibernate JIRA </a>
 * 
 * @author Erik-Berndt Scheper
 */
public class DefaultValueComponents extends AbstractEntityTest {
	private static final Logger log = LoggerFactory
			.getLogger(DefaultValueComponents.class);

	private Integer id0;
	private Integer id1;
	private Integer id2;
	private Integer id3;
	private Integer id4;
	private Integer id5;
	private Integer id6;

	public void configure(Ejb3Configuration cfg) {
		cfg.addAnnotatedClass(DefaultValueComponentTestEntity.class);
	}

	@BeforeClass(dependsOnMethods = "init")
	public void initData() {
		// Revision 1
		EntityManager em = getEntityManager();
		em.getTransaction().begin();

		DefaultValueComponentTestEntity cte0 = DefaultValueComponentTestEntity
				.of(null);

		DefaultValueComponentTestEntity cte1 = DefaultValueComponentTestEntity
				.of(DefaultValueComponent1.of("c1-str1", null));

		DefaultValueComponentTestEntity cte2 = DefaultValueComponentTestEntity
				.of(DefaultValueComponent1.of("c1-str1", DefaultValueComponent2
						.of("c2-str1", "c2-str2")));

		DefaultValueComponentTestEntity cte3 = DefaultValueComponentTestEntity
				.of(DefaultValueComponent1.of(null, DefaultValueComponent2.of(
						"c2-str1", "c2-str2")));

		DefaultValueComponentTestEntity cte4 = DefaultValueComponentTestEntity
				.of(DefaultValueComponent1.of(null, DefaultValueComponent2.of(
						null, "c2-str2")));

		DefaultValueComponentTestEntity cte5 = DefaultValueComponentTestEntity
				.of(DefaultValueComponent1.of(null, DefaultValueComponent2.of(
						"c2-str1", null)));

		DefaultValueComponentTestEntity cte6 = DefaultValueComponentTestEntity
				.of(DefaultValueComponent1.of(null, DefaultValueComponent2.of(
						null, null)));

		em.persist(cte0);
		em.persist(cte1);
		em.persist(cte2);
		em.persist(cte3);
		em.persist(cte4);
		em.persist(cte5);
		em.persist(cte6);

		em.getTransaction().commit();

		// Revision 2
		em = getEntityManager();
		em.getTransaction().begin();

		cte0 = em.find(DefaultValueComponentTestEntity.class, cte0.getId());
		cte1 = em.find(DefaultValueComponentTestEntity.class, cte1.getId());
		cte2 = em.find(DefaultValueComponentTestEntity.class, cte2.getId());
		cte3 = em.find(DefaultValueComponentTestEntity.class, cte3.getId());
		cte4 = em.find(DefaultValueComponentTestEntity.class, cte4.getId());
		cte5 = em.find(DefaultValueComponentTestEntity.class, cte5.getId());
		cte6 = em.find(DefaultValueComponentTestEntity.class, cte6.getId());

		cte0.setComp1(DefaultValueComponent1.of("upd-c1-str1", null));
		cte1.setComp1(DefaultValueComponent1.of(null, DefaultValueComponent2
				.of("upd-c2-str1", "upd-c2-str2")));
		cte2.getComp1().getComp2().setStr1("upd-c2-str1");
		cte3.getComp1().getComp2().setStr1("upd-c2-str1");
		cte4.getComp1().getComp2().setStr1("upd-c2-str1");
		cte5.getComp1().getComp2().setStr1("upd-c2-str1");
		cte6.getComp1().getComp2().setStr1("upd-c2-str1");

		em.getTransaction().commit();

		// afterwards
		id0 = cte0.getId();
		id1 = cte1.getId();
		id2 = cte2.getId();
		id3 = cte3.getId();
		id4 = cte4.getId();
		id5 = cte5.getId();
		id6 = cte6.getId();
	}

	@Test
	public void testRevisionsCounts() {
		log.error(getAuditReader().getRevisions(
				DefaultValueComponentTestEntity.class, id0).toString());
		log.error(getAuditReader().getRevisions(
				DefaultValueComponentTestEntity.class, id1).toString());
		log.error(getAuditReader().getRevisions(
				DefaultValueComponentTestEntity.class, id2).toString());
		log.error(getAuditReader().getRevisions(
				DefaultValueComponentTestEntity.class, id3).toString());
		log.error(getAuditReader().getRevisions(
				DefaultValueComponentTestEntity.class, id4).toString());
		log.error(getAuditReader().getRevisions(
				DefaultValueComponentTestEntity.class, id5).toString());
		log.error(getAuditReader().getRevisions(
				DefaultValueComponentTestEntity.class, id6).toString());

		assert Arrays.asList(1, 2).equals(
				getAuditReader().getRevisions(
						DefaultValueComponentTestEntity.class, id0));

		assert Arrays.asList(1, 2).equals(
				getAuditReader().getRevisions(
						DefaultValueComponentTestEntity.class, id1));

		assert Arrays.asList(1, 2).equals(
				getAuditReader().getRevisions(
						DefaultValueComponentTestEntity.class, id2));

		assert Arrays.asList(1, 2).equals(
				getAuditReader().getRevisions(
						DefaultValueComponentTestEntity.class, id3));

		assert Arrays.asList(1, 2).equals(
				getAuditReader().getRevisions(
						DefaultValueComponentTestEntity.class, id4));

		assert Arrays.asList(1, 2).equals(
				getAuditReader().getRevisions(
						DefaultValueComponentTestEntity.class, id5));

		assert Arrays.asList(1, 2).equals(
				getAuditReader().getRevisions(
						DefaultValueComponentTestEntity.class, id6));
	}

	@Test
	public void testHistoryOfId0() {

		DefaultValueComponentTestEntity ent1 = getAuditReader().find(
				DefaultValueComponentTestEntity.class, id0, 1);
		DefaultValueComponentTestEntity ent2 = getAuditReader().find(
				DefaultValueComponentTestEntity.class, id0, 2);

		log.error("------------ id0 -------------");
		log.error(ent1.toString());
		log.error(ent2.toString());

		checkCorrectlyPersisted(id0, null, null);

		DefaultValueComponentTestEntity expectedVer1 = DefaultValueComponentTestEntity
				.of(id0, DefaultValueComponent1.of(null, null));
		DefaultValueComponentTestEntity expectedVer2 = DefaultValueComponentTestEntity
				.of(id0, DefaultValueComponent1.of("upd-c1-str1", null));

		assert ent1.equals(expectedVer1);
		assert ent2.equals(expectedVer2);
	}

	@Test
	public void testHistoryOfId1() {

		DefaultValueComponentTestEntity ent1 = getAuditReader().find(
				DefaultValueComponentTestEntity.class, id1, 1);
		DefaultValueComponentTestEntity ent2 = getAuditReader().find(
				DefaultValueComponentTestEntity.class, id1, 2);

		log.error("------------ id1 -------------");
		log.error(ent1.toString());
		log.error(ent2.toString());

		checkCorrectlyPersisted(id1, null, "upd-c2-str1");

		DefaultValueComponentTestEntity expectedVer1 = DefaultValueComponentTestEntity
				.of(id1, DefaultValueComponent1.of("c1-str1", null));
		DefaultValueComponentTestEntity expectedVer2 = DefaultValueComponentTestEntity
				.of(id1, DefaultValueComponent1.of(null, DefaultValueComponent2
						.of("upd-c2-str1", "upd-c2-str2")));

		assert ent2.equals(expectedVer2);
		assert ent1.equals(expectedVer1);
	}

	@Test
	public void testHistoryOfId2() {

		DefaultValueComponentTestEntity ent1 = getAuditReader().find(
				DefaultValueComponentTestEntity.class, id2, 1);
		DefaultValueComponentTestEntity ent2 = getAuditReader().find(
				DefaultValueComponentTestEntity.class, id2, 2);

		log.error("------------ id2 -------------");
		log.error(ent1.toString());
		log.error(ent2.toString());

		DefaultValueComponentTestEntity expectedVer1 = DefaultValueComponentTestEntity
				.of(id2, DefaultValueComponent1.of("c1-str1",
						DefaultValueComponent2.of("c2-str1", "c2-str2")));
		DefaultValueComponentTestEntity expectedVer2 = DefaultValueComponentTestEntity
				.of(id2, DefaultValueComponent1.of("c1-str1",
						DefaultValueComponent2.of("upd-c2-str1", "c2-str2")));

		assert ent1.equals(expectedVer1);
		assert ent2.equals(expectedVer2);
	}

	@Test
	public void testHistoryOfId3() {

		DefaultValueComponentTestEntity ent1 = getAuditReader().find(
				DefaultValueComponentTestEntity.class, id3, 1);
		DefaultValueComponentTestEntity ent2 = getAuditReader().find(
				DefaultValueComponentTestEntity.class, id3, 2);

		log.error("------------ id3 -------------");
		log.error(ent1.toString());
		log.error(ent2.toString());

		DefaultValueComponentTestEntity expectedVer1 = DefaultValueComponentTestEntity
				.of(id3, DefaultValueComponent1.of(null, DefaultValueComponent2
						.of("c2-str1", "c2-str2")));
		DefaultValueComponentTestEntity expectedVer2 = DefaultValueComponentTestEntity
				.of(id3, DefaultValueComponent1.of(null, DefaultValueComponent2
						.of("upd-c2-str1", "c2-str2")));

		assert ent1.equals(expectedVer1);
		assert ent2.equals(expectedVer2);
	}

	@Test
	public void testHistoryOfId4() {

		DefaultValueComponentTestEntity ent1 = getAuditReader().find(
				DefaultValueComponentTestEntity.class, id4, 1);
		DefaultValueComponentTestEntity ent2 = getAuditReader().find(
				DefaultValueComponentTestEntity.class, id4, 2);

		log.error("------------ id4 -------------");
		log.error(ent1.toString());
		log.error(ent2.toString());

		DefaultValueComponentTestEntity expectedVer1 = DefaultValueComponentTestEntity
				.of(id4, DefaultValueComponent1.of(null, DefaultValueComponent2
						.of(null, "c2-str2")));
		DefaultValueComponentTestEntity expectedVer2 = DefaultValueComponentTestEntity
				.of(id4, DefaultValueComponent1.of(null, DefaultValueComponent2
						.of("upd-c2-str1", "c2-str2")));

		assert ent1.equals(expectedVer1);
		assert ent2.equals(expectedVer2);
	}

	@Test
	public void testHistoryOfId5() {

		DefaultValueComponentTestEntity ent1 = getAuditReader().find(
				DefaultValueComponentTestEntity.class, id5, 1);
		DefaultValueComponentTestEntity ent2 = getAuditReader().find(
				DefaultValueComponentTestEntity.class, id5, 2);

		log.error("------------ id5 -------------");
		log.error(ent1.toString());
		log.error(ent2.toString());

		DefaultValueComponentTestEntity expectedVer1 = DefaultValueComponentTestEntity
				.of(id5, DefaultValueComponent1.of(null, DefaultValueComponent2
						.of("c2-str1", null)));
		DefaultValueComponentTestEntity expectedVer2 = DefaultValueComponentTestEntity
				.of(id5, DefaultValueComponent1.of(null, DefaultValueComponent2
						.of("upd-c2-str1", null)));

		assert ent1.equals(expectedVer1);
		assert ent2.equals(expectedVer2);
	}

	@Test
	public void testHistoryOfId6() {

		DefaultValueComponentTestEntity ent1 = getAuditReader().find(
				DefaultValueComponentTestEntity.class, id6, 1);
		DefaultValueComponentTestEntity ent2 = getAuditReader().find(
				DefaultValueComponentTestEntity.class, id6, 2);

		log.error("------------ id6 -------------");
		log.error(ent1.toString());
		log.error(ent2.toString());

		DefaultValueComponentTestEntity expectedVer1 = DefaultValueComponentTestEntity
				.of(id6, DefaultValueComponent1.of(null, null));
		DefaultValueComponentTestEntity expectedVer2 = DefaultValueComponentTestEntity
				.of(id6, DefaultValueComponent1.of(null, DefaultValueComponent2
						.of("upd-c2-str1", null)));

		assert ent2.equals(expectedVer2);
		assert ent1.equals(expectedVer1);
	}

	private void checkCorrectlyPersisted(Integer expectedId,
			String expectedComp2Str1Rev1, String expectedComp2Str1Rev2) {
		// Verify that the entity was correctly persisted
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		Long entCount = (Long) em.createQuery(
				"select count(s) from DefaultValueComponentTestEntity s where s.id = "
						+ expectedId.toString()).getSingleResult();
		BigInteger auditCount = (BigInteger) em.createNativeQuery(
				"select count(ID) from DefaultValueComponentTestEntity_AUD s where s.id = "
						+ expectedId.toString()).getSingleResult();
		String comp2Str1Rev1 = (String) em
				.createNativeQuery(
						"select COMP2_STR1 from DefaultValueComponentTestEntity_AUD s where rev=1 and s.id = "
								+ expectedId.toString()).getSingleResult();
		String comp2Str1Rev2 = (String) em
				.createNativeQuery(
						"select COMP2_STR1 from DefaultValueComponentTestEntity_AUD s where rev=2 and s.id = "
								+ expectedId.toString()).getSingleResult();
		assert Long.valueOf(1L).equals(entCount);
		assert BigInteger.valueOf(2L).equals(auditCount);

		if (expectedComp2Str1Rev1 == null) {
			assert comp2Str1Rev1 == null;
		} else {
			assert expectedComp2Str1Rev1.equals(comp2Str1Rev1);
		}

		if (expectedComp2Str1Rev2 == null) {
			assert comp2Str1Rev2 == null;
		} else {
			assert expectedComp2Str1Rev2.equals(comp2Str1Rev2);
		}
		em.getTransaction().commit();
	}
}
