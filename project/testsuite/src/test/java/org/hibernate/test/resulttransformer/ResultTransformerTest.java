/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2009, Red Hat, Inc. and/or its affiliates or third-
 * party contributors as indicated by the @author tags or express 
 * copyright attribution statements applied by the authors.  
 * All third-party contributions are distributed under license by 
 * Red Hat, Inc.
 *
 * This copyrighted material is made available to anyone wishing to 
 * use, modify, copy, or redistribute it subject to the terms and 
 * conditions of the GNU Lesser General Public License, as published 
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU 
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this distribution; if not, write to:
 * 
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */

package org.hibernate.test.resulttransformer;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.junit.functional.FunctionalTestCase;
import org.hibernate.transform.ResultTransformer;

/**
 * @author Sharath Reddy
 *
 */
public class ResultTransformerTest extends FunctionalTestCase {

	public ResultTransformerTest(String string) {
		super(string);
	}
	
	/**
	 * HHH-3694 ResultTransformer not used when scroll() is used on a named SQLQuery
	 */
	public void testResultTransformerIsAppliedToScrollableResults() throws Exception
	{
		Session s = openSession();
		Transaction tx = s.beginTransaction();

		PartnerA a = new PartnerA();
		a.setName("Partner A");
		PartnerB b = new PartnerB();
		b.setName("Partner B");
		Contract obj1 = new Contract();
		obj1.setName("Contract");
		obj1.setA(a);
		obj1.setB(b);
		s.save(a);
		s.save(b);
		s.save(obj1);

		tx.commit();
		s.close();

		s = openSession();

		Query q = s.getNamedQuery(Contract.class.getName() + ".testQuery");
		q.setFetchSize(100);
		q.setResultTransformer(new ResultTransformer() {

			private static final long serialVersionUID = -5815434828170704822L;

			public Object transformTuple(Object[] arg0, String[] arg1)
			{
				// return only the PartnerA object from the query
				return arg0[1];
			}

			@SuppressWarnings("unchecked")
			public List transformList(List arg0)
			{
				return arg0;
			}
		});
		ScrollableResults sr = q.scroll();
		sr.first();
		Object[] row = sr.get();
		assertEquals(1, row.length);
		Object obj = row[0];
		assertTrue(obj instanceof PartnerA);
		PartnerA obj2 = (PartnerA) obj;
		assertEquals("Partner A", obj2.getName());
		s.close();
	}

	public String[] getMappings() {
		return new String[] { "resulttransformer/Contract.hbm.xml" };
	}
}


