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
package org.hibernate.envers.test.integration.naming;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.envers.test.AbstractEntityTest;
import org.hibernate.envers.test.entities.components.Component1;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test class for {@link VersionsJoinTableRangeComponentTestEntity}, to test
 * various {@link org.hibernate.envers.AuditOverride} annotations.
 * 
 * @author Erik-Berndt Scheper
 */
public class VersionsJoinTableRangeComponentNamingTest extends
		AbstractEntityTest {
	private Integer vjrcte_id;
	private Integer vjtrte_id;
	private Integer vjtrtae_id1;

	public void configure(Ejb3Configuration cfg) {
		cfg.addAnnotatedClass(VersionsJoinTableRangeComponentTestEntity.class);
		cfg.addAnnotatedClass(VersionsJoinTableRangeTestEntitySuperClass.class);
		cfg.addAnnotatedClass(VersionsJoinTableRangeTestEntity.class);
		cfg.addAnnotatedClass(VersionsJoinTableRangeTestAlternateEntity.class);
	}

	@BeforeClass(dependsOnMethods = "init")
	public void initData() {

		// Revision 1
		EntityManager em = getEntityManager();
		em.getTransaction().begin();

		// create an instance of the test entity
		VersionsJoinTableRangeComponentTestEntity vjrcte = new VersionsJoinTableRangeComponentTestEntity();
		em.persist(vjrcte);
		em.getTransaction().commit();

		// Revision 2
		em.getTransaction().begin();

		vjrcte = em.find(VersionsJoinTableRangeComponentTestEntity.class,
				vjrcte.getId());

		// create a component containing a list of
		// VersionsJoinTableRangeTestEntity-instances
		VersionsJoinTableRangeTestEntity vjtrte = new VersionsJoinTableRangeTestEntity();
		vjtrte.setGenericValue("generic1");
		vjtrte.setValue("value1");
		// and add it to the test entity
		vjrcte.getComponent1().getRange().add(vjtrte);

		// create a second component containing a list of
		// VersionsJoinTableRangeTestAlternateEntity-instances
		VersionsJoinTableRangeTestAlternateEntity vjtrtae1 = new VersionsJoinTableRangeTestAlternateEntity();
		vjtrtae1.setGenericValue("generic2");
		vjtrtae1.setAlternateValue("alternateValue2");
		// and add it to the test entity
		vjrcte.getComponent2().getRange().add(vjtrtae1);

		// create a third component, and add it to the test entity
		Component1 simpleComponent = new Component1("string1", "string2");
		vjrcte.setComponent3(simpleComponent);

		em.persist(vjtrte);
		em.persist(vjtrtae1);
		em.persist(vjrcte);

		em.getTransaction().commit();

		// Revision 2
		em.getTransaction().begin();

		vjrcte = em.find(VersionsJoinTableRangeComponentTestEntity.class,
				vjrcte.getId());
		vjtrte = em
				.find(VersionsJoinTableRangeTestEntity.class, vjtrte.getId());
		vjtrtae1 = em.find(VersionsJoinTableRangeTestAlternateEntity.class,
				vjtrtae1.getId());

		assert vjrcte != null;
		assert vjtrte != null;
		assert vjtrtae1 != null;

		List<VersionsJoinTableRangeTestEntity> ent1List = vjrcte
				.getComponent1().getRange();
		assert ent1List.size() == 1;
		assert vjtrte.equals(ent1List.get(0));

		List<VersionsJoinTableRangeTestAlternateEntity> ent2List = vjrcte
				.getComponent2().getRange();
		assert ent2List.size() == 1;
		assert vjtrtae1.equals(ent2List.get(0));

		em.getTransaction().commit();

		vjrcte_id = vjrcte.getId();
		vjtrte_id = vjtrte.getId();
		vjtrtae_id1 = vjtrtae1.getId();
	}

	@Test
	public void testRevisionsCounts() {
		assert Arrays.asList(1, 2).equals(
				getAuditReader().getRevisions(
						VersionsJoinTableRangeComponentTestEntity.class,
						vjrcte_id));
		assert Arrays.asList(2).equals(
				getAuditReader().getRevisions(
						VersionsJoinTableRangeTestEntity.class, vjtrte_id));
		assert Arrays.asList(2).equals(
				getAuditReader().getRevisions(
						VersionsJoinTableRangeTestAlternateEntity.class,
						vjtrtae_id1));
	}

	@Test
	public void testHistoryOfUniId1() {
		VersionsJoinTableRangeTestEntity vjtrte = getEntityManager().find(
				VersionsJoinTableRangeTestEntity.class, vjtrte_id);
		VersionsJoinTableRangeTestAlternateEntity vjtrtae = getEntityManager()
				.find(VersionsJoinTableRangeTestAlternateEntity.class,
						vjtrtae_id1);

		VersionsJoinTableRangeComponentTestEntity rev1 = getAuditReader().find(
				VersionsJoinTableRangeComponentTestEntity.class, vjrcte_id, 1);
		VersionsJoinTableRangeComponentTestEntity rev2 = getAuditReader().find(
				VersionsJoinTableRangeComponentTestEntity.class, vjrcte_id, 2);

		assert rev1.getComponent1().getRange().size() == 0;
		assert rev1.getComponent2().getRange().size() == 0;

		assert rev2.getComponent1().getRange().size() == 1;
		assert rev2.getComponent1().getRange().get(0).equals(vjtrte);
		assert rev2.getComponent2().getRange().size() == 1;
		assert rev2.getComponent2().getRange().get(0).equals(vjtrtae);
	}

	/* The Audit join tables we expect */
	private final static String COMPONENT_1_AUDIT_JOIN_TABLE_NAME = "JOIN_TABLE_COMPONENT_1_AUD";
	private final static String COMPONENT_2_AUDIT_JOIN_TABLE_NAME = "JOIN_TABLE_COMPONENT_2_AUD";

	/* The Audit join tables that should NOT be there */
	private final static String UNMODIFIED_COMPONENT_1_AUDIT_JOIN_TABLE_NAME = "VersionsJoinTableRangeComponentTestEntity_VersionsJoinTableRangeTestEntity_AUD";
	private final static String UNMODIFIED_COMPONENT_2_AUDIT_JOIN_TABLE_NAME = "VersionsJoinTableRangeComponentTestEntity_VersionsJoinTableRangeTestAlternateEntity_AUD";

	@Test
	public void testExpectedTableNameComponent1() {
		PersistentClass auditClass = getCfg().getClassMapping(
				COMPONENT_1_AUDIT_JOIN_TABLE_NAME);
		assert auditClass != null;
		assert COMPONENT_1_AUDIT_JOIN_TABLE_NAME.equals(auditClass.getTable()
				.getName());
	}

	@Test
	public void testExpectedTableNameComponent2() {
		PersistentClass auditClass = getCfg().getClassMapping(
				COMPONENT_2_AUDIT_JOIN_TABLE_NAME);
		assert auditClass != null;
		assert COMPONENT_2_AUDIT_JOIN_TABLE_NAME.equals(auditClass.getTable()
				.getName());
	}

	@Test
	public void testWrongTableNameComponent1() {
		PersistentClass auditClass = getCfg().getClassMapping(
				UNMODIFIED_COMPONENT_1_AUDIT_JOIN_TABLE_NAME);
		assert auditClass == null;
	}

	@Test
	public void testWrongTableNameComponent2() {
		PersistentClass auditClass = getCfg().getClassMapping(
				UNMODIFIED_COMPONENT_2_AUDIT_JOIN_TABLE_NAME);
		assert auditClass == null;
	}

	@Test
	public void testJoinColumnNamesComponent1() {
		PersistentClass auditClass = getCfg().getClassMapping(
				COMPONENT_1_AUDIT_JOIN_TABLE_NAME);
		assert auditClass != null;

		@SuppressWarnings( { "unchecked" })
		Iterator<Column> columns = auditClass.getTable().getColumnIterator();

		boolean id1Found = false;
		boolean id2Found = false;

		while (columns.hasNext()) {
			Column column = columns.next();
			if ("VJTRCTE1_ID".equals(column.getName())) {
				id1Found = true;
			}

			if ("VJTRTE_ID".equals(column.getName())) {
				id2Found = true;
			}
		}

		assert id1Found && id2Found;
	}

	@Test
	public void testJoinColumnNamesComponent2() {
		PersistentClass auditClass = getCfg().getClassMapping(
				COMPONENT_2_AUDIT_JOIN_TABLE_NAME);
		assert auditClass != null;

		@SuppressWarnings( { "unchecked" })
		Iterator<Column> columns = auditClass.getTable().getColumnIterator();

		boolean id1Found = false;
		boolean id2Found = false;

		while (columns.hasNext()) {
			Column column = columns.next();
			if ("VJTRCTE2_ID".equals(column.getName())) {
				id1Found = true;
			}

			if ("VJTRTAE_ID".equals(column.getName())) {
				id2Found = true;
			}
		}

		assert id1Found && id2Found;
	}

	/**
	 * Verify that
	 * {@link VersionsJoinTableRangeComponentTestEntity#getComponent3()} is
	 * partially audited.
	 */
	@Test
	public void testOverrideNotAudited() {
		PersistentClass auditClass = getCfg().getClassMapping(
				VersionsJoinTableRangeComponentTestEntity.class.getName()
						+ "_AUD");
		assert auditClass != null;

		@SuppressWarnings( { "unchecked" })
		Iterator<Column> columns = auditClass.getTable().getColumnIterator();

		boolean auditColumn1Found = false;
		boolean auditColumn2Found = false;

		while (columns.hasNext()) {
			Column column = columns.next();
			if ("STR1".equals(column.getName())) {
				auditColumn1Found = true;
			}

			if ("STR2".equals(column.getName())) {
				auditColumn2Found = true;
			}
		}

		assert auditColumn1Found && !auditColumn2Found;
	}

}
