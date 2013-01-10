// $Id: FkCircularityTest.java 19255 2010-04-21 01:57:44Z steve.ebersole@jboss.com $
package org.hibernate.test.annotations.fkcircularity;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.SQLServerDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test case for ANN-722 and ANN-730.
 * 
 * @author Hardy Ferentschik
 */
public class FkCircularityTest extends TestCase {

	private Logger log = LoggerFactory.getLogger(FkCircularityTest.class);

	public void testJoinedSublcassesInPK() {
		try {
			AnnotationConfiguration config = new AnnotationConfiguration();
			config.addAnnotatedClass(A.class);
			config.addAnnotatedClass(B.class);
			config.addAnnotatedClass(C.class);
			config.addAnnotatedClass(D.class);
			config.buildSessionFactory();
			String[] schema = config
					.generateSchemaCreationScript(new SQLServerDialect());
			for (String s : schema) {
				log.debug(s);
			}
			log.debug("success");
		} catch (Exception e) {
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			log.debug(writer.toString());
			fail(e.getMessage());
		}
	}

	public void testDeepJoinedSuclassesHierachy() {
		try {
			AnnotationConfiguration config = new AnnotationConfiguration();
			config.addAnnotatedClass(ClassA.class);
			config.addAnnotatedClass(ClassB.class);
			config.addAnnotatedClass(ClassC.class);
			config.addAnnotatedClass(ClassD.class);
			config.buildSessionFactory();
			String[] schema = config
					.generateSchemaCreationScript(new HSQLDialect());
			for (String s : schema) {
				log.debug(s);
			}
			log.debug("success");
		} catch (Exception e) {
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			log.debug(writer.toString());
			fail(e.getMessage());
		}
	}
}