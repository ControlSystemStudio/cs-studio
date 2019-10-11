package org.hibernate.envers.test.integration.onetomany.inverseToSuperclass;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import javax.persistence.EntityManager;

import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.envers.test.AbstractEntityTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Hern�n Chanfreau
 * 
 */

public class OneToManyInverseToSuperclassTest extends AbstractEntityTest {

	private long m1_id;

	public void configure(Ejb3Configuration cfg) {
		try {
			URL url = Thread.currentThread().getContextClassLoader()
					.getResource(
							"mappings/oneToMany/inverseToSuperclass/mappings.hbm.xml");
			cfg.addFile(new File(url.toURI()));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	@BeforeClass(dependsOnMethods = "init")
	public void initData() {
		EntityManager em = getEntityManager();

		Master m1 = new Master();
		DetailSubclass det1 = new DetailSubclass2();
		DetailSubclass det2 = new DetailSubclass2();

		// Revision 1
		em.getTransaction().begin();

		det1.setStr2("detail 1");

		m1.setStr("master");
		m1.setItems(new ArrayList<DetailSubclass>());
		m1.getItems().add(det1);
		det1.setParent(m1);

		em.persist(m1);
		em.getTransaction().commit();
		m1_id = m1.getId();

		// Revision 2
		em.getTransaction().begin();

		m1 = em.find(Master.class, m1_id);

		det2.setStr2("detail 2");
		det2.setParent(m1);
		m1.getItems().add(det2);
		em.getTransaction().commit();

		// Revision 3
		em.getTransaction().begin();

		m1 = em.find(Master.class, m1_id);
		m1.setStr("new master");

		det1 = m1.getItems().get(0);
		det1.setStr2("new detail");
		DetailSubclass det3 = new DetailSubclass2();
		det3.setStr2("detail 3");
		det3.setParent(m1);

		m1.getItems().get(1).setParent(null);
		// m1.getItems().remove(1);
		m1.getItems().add(det3);

		em.persist(m1);
		em.getTransaction().commit();

		// Revision 4
		em.getTransaction().begin();

		m1 = em.find(Master.class, m1_id);

		det1 = m1.getItems().get(0);
		det1.setParent(null);
		// m1.getItems().remove(det1);

		em.persist(m1);
		em.getTransaction().commit();

	}

	@Test
	public void testHistoryExists() {
		Master rev1_1 = getAuditReader().find(Master.class, m1_id, 1);
		Master rev1_2 = getAuditReader().find(Master.class, m1_id, 2);
		Master rev1_3 = getAuditReader().find(Master.class, m1_id, 3);
		Master rev1_4 = getAuditReader().find(Master.class, m1_id, 4);

		assert (rev1_1 != null);
		assert (rev1_2 != null);
		assert (rev1_3 != null);
		assert (rev1_4 != null);
	}

}
