//$Id: OneToOneErrorTest.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.onetoone;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;
import org.hibernate.test.annotations.IncorrectEntity;
import org.hibernate.SessionFactory;
import org.hibernate.AnnotationException;

/**
 * @author Emmanuel Bernard
 */
public class OneToOneErrorTest extends junit.framework.TestCase {
	public void testWrongOneToOne() throws Exception {
		AnnotationConfiguration cfg = new AnnotationConfiguration();
		cfg.addAnnotatedClass( Show.class )
				.addAnnotatedClass( ShowDescription.class );
		cfg.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
		try {
			SessionFactory sf = cfg.buildSessionFactory();
			fail( "Wrong mappedBy does not fail property" );
		}
		catch (AnnotationException e) {
			//success
		}
	}
}