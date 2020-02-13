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
 *
 */
package org.hibernate.test.criteria;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.junit.functional.FunctionalTestCase;


public class LikeTest extends FunctionalTestCase {

    public LikeTest( String string ) {
        super( string );
    }

    public String[] getMappings() {
        return new String[]{"criteria/TestObject.hbm.xml"};
    }
    public void testLike(){
        Session session = openSession();
        Transaction tx = session.beginTransaction();
        TestObject obj = new TestObject();
        String uniq = "uniq" + System.currentTimeMillis();
        obj.setText( "XyZ " + uniq + " blablabla" );
        session.save( obj );
        session.flush();
        tx.commit();
        session.close();
        String pattern = "XyZ " + uniq + "%";
        // retrieve object - case sensitive - works ok
        session = openSession();
        tx = session.beginTransaction();
        List objects = session.createCriteria( TestObject.class ).add(
                Restrictions.like( "text", pattern ) ).list();
        assertEquals( 1, objects.size() );
        session.clear();

        // retrieve object - case insensitive - works ok
        objects = session.createCriteria( TestObject.class ).add(
                Restrictions.like( "text", pattern ).ignoreCase() ).list();

        assertEquals( 1, objects.size() );
        session.clear();
        if ( !( getDialect() instanceof MySQLDialect ) ) {
            // retrieve object - case insensitive via custom expression - works
            // ok
            objects = session.createCriteria( TestObject.class ).add(
                    StringExpression.stringExpression( "text", pattern, true ) )
                    .list();

            assertEquals( 1, objects.size() );
            session.clear();

            // retrieve object - case sensitive via custom expression - not
            // working
            objects = session.createCriteria( TestObject.class )
                    .add(
                            StringExpression.stringExpression( "text", pattern,
                                    false ) ).list();
            assertEquals( 1, objects.size() );
        }
        tx.rollback();
        session.close();
        
    }
}
