//$Id: NonReflectiveBinderTest.java 10977 2006-12-12 23:28:04Z steve.ebersole@jboss.com $
package org.hibernate.test.legacy;

import java.util.Iterator;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.MetaAttribute;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.junit.UnitTestCase;


public class NonReflectiveBinderTest extends UnitTestCase {

	private Configuration cfg;

	public NonReflectiveBinderTest(String x) {
		super( x );
	}

	public String[] getMappings() {
		return new String[] { "legacy/Wicked.hbm.xml"};
	}

	public static Test suite() {
		return new TestSuite( NonReflectiveBinderTest.class );
	}

	protected void setUp() throws Exception {
		super.setUp();
		cfg = new Configuration()
				.addResource( "org/hibernate/test/legacy/Wicked.hbm.xml" );
		cfg.buildMappings();
	}

	protected void tearDown() throws Exception {
		cfg = null;
		super.tearDown();
	}

	public void testMetaInheritance() {
		PersistentClass cm = cfg.getClassMapping("org.hibernate.test.legacy.Wicked");
		Map m = cm.getMetaAttributes();
		assertNotNull(m);
		assertNotNull(cm.getMetaAttribute("global"));
		assertNull(cm.getMetaAttribute("globalnoinherit"));
		
		MetaAttribute metaAttribute = cm.getMetaAttribute("implements");
		assertNotNull(metaAttribute);
		assertEquals("implements", metaAttribute.getName());
		assertTrue(metaAttribute.isMultiValued());
		assertEquals(3, metaAttribute.getValues().size());
		assertEquals("java.lang.Observer",metaAttribute.getValues().get(0));
		assertEquals("java.lang.Observer",metaAttribute.getValues().get(1));
		assertEquals("org.foo.BogusVisitor",metaAttribute.getValues().get(2));
				
		/*Property property = cm.getIdentifierProperty();
		property.getMetaAttribute(null);*/
		
		Iterator propertyIterator = cm.getPropertyIterator();
		while (propertyIterator.hasNext()) {
			Property element = (Property) propertyIterator.next();
			System.out.println(element);
			Map ma = element.getMetaAttributes();
			assertNotNull(ma);
			assertNotNull(element.getMetaAttribute("global"));
			MetaAttribute metaAttribute2 = element.getMetaAttribute("implements");
			assertNotNull(metaAttribute2);
			assertNull(element.getMetaAttribute("globalnoinherit"));
						
		}
		
		Property element = cm.getProperty("component");
		Map ma = element.getMetaAttributes();
		assertNotNull(ma);
		assertNotNull(element.getMetaAttribute("global"));
		assertNotNull(element.getMetaAttribute("componentonly"));
		assertNotNull(element.getMetaAttribute("allcomponent"));
		assertNull(element.getMetaAttribute("globalnoinherit"));							
		
		MetaAttribute compimplements = element.getMetaAttribute("implements");
		assertNotNull(compimplements);
		assertEquals(compimplements.getValue(), "AnotherInterface");
		
		Property xp = ((Component)element.getValue()).getProperty( "x" );
		MetaAttribute propximplements = xp.getMetaAttribute( "implements" );
		assertNotNull(propximplements);
		assertEquals(propximplements.getValue(), "AnotherInterface");
		
		
	}

	// HBX-718
	public void testNonMutatedInheritance() {
		PersistentClass cm = cfg.getClassMapping("org.hibernate.test.legacy.Wicked");
		MetaAttribute metaAttribute = cm.getMetaAttribute( "globalmutated" );
		
		assertNotNull(metaAttribute);
		/*assertEquals( metaAttribute.getValues().size(), 2 );		
		assertEquals( "top level", metaAttribute.getValues().get(0) );*/
		assertEquals( "wicked level", metaAttribute.getValue() );
		
		Property property = cm.getProperty( "component" );
		MetaAttribute propertyAttribute = property.getMetaAttribute( "globalmutated" );
		
		assertNotNull(propertyAttribute);
		/*assertEquals( propertyAttribute.getValues().size(), 3 );
		assertEquals( "top level", propertyAttribute.getValues().get(0) );
		assertEquals( "wicked level", propertyAttribute.getValues().get(1) );*/
		assertEquals( "monetaryamount level", propertyAttribute.getValue() );
		
		org.hibernate.mapping.Component component = (Component)property.getValue();
		property = component.getProperty( "x" );
		propertyAttribute = property.getMetaAttribute( "globalmutated" );
		
		assertNotNull(propertyAttribute);
		/*assertEquals( propertyAttribute.getValues().size(), 4 );
		assertEquals( "top level", propertyAttribute.getValues().get(0) );
		assertEquals( "wicked level", propertyAttribute.getValues().get(1) );
		assertEquals( "monetaryamount level", propertyAttribute.getValues().get(2) );*/
		assertEquals( "monetaryamount x level", propertyAttribute.getValue() );
		
		property = cm.getProperty( "sortedEmployee" );
		propertyAttribute = property.getMetaAttribute( "globalmutated" );
		
		assertNotNull(propertyAttribute);
		/*assertEquals( propertyAttribute.getValues().size(), 3 );
		assertEquals( "top level", propertyAttribute.getValues().get(0) );
		assertEquals( "wicked level", propertyAttribute.getValues().get(1) );*/
		assertEquals( "sortedemployee level", propertyAttribute.getValue() );
		
		property = cm.getProperty( "anotherSet" );
		propertyAttribute = property.getMetaAttribute( "globalmutated" );
		
		assertNotNull(propertyAttribute);
		/*assertEquals( propertyAttribute.getValues().size(), 2 );
		assertEquals( "top level", propertyAttribute.getValues().get(0) );*/
		assertEquals( "wicked level", propertyAttribute.getValue() );
				
		Bag bag = (Bag) property.getValue();
		component = (Component)bag.getElement(); 
		
		assertEquals(4,component.getMetaAttributes().size());
		
		metaAttribute = component.getMetaAttribute( "globalmutated" );
		/*assertEquals( metaAttribute.getValues().size(), 3 );
		assertEquals( "top level", metaAttribute.getValues().get(0) );
		assertEquals( "wicked level", metaAttribute.getValues().get(1) );*/
		assertEquals( "monetaryamount anotherSet composite level", metaAttribute.getValue() );		
		
		property = component.getProperty( "emp" );
		propertyAttribute = property.getMetaAttribute( "globalmutated" );
		
		assertNotNull(propertyAttribute);
		/*assertEquals( propertyAttribute.getValues().size(), 4 );
		assertEquals( "top level", propertyAttribute.getValues().get(0) );
		assertEquals( "wicked level", propertyAttribute.getValues().get(1) );
		assertEquals( "monetaryamount anotherSet composite level", propertyAttribute.getValues().get(2) );*/
		assertEquals( "monetaryamount anotherSet composite property emp level", propertyAttribute.getValue() );
		
		
		property = component.getProperty( "empinone" );
		propertyAttribute = property.getMetaAttribute( "globalmutated" );
		
		assertNotNull(propertyAttribute);
		/*assertEquals( propertyAttribute.getValues().size(), 4 );
		assertEquals( "top level", propertyAttribute.getValues().get(0) );
		assertEquals( "wicked level", propertyAttribute.getValues().get(1) );
		assertEquals( "monetaryamount anotherSet composite level", propertyAttribute.getValues().get(2) );*/
		assertEquals( "monetaryamount anotherSet composite property empinone level", propertyAttribute.getValue() );
		
		
	}
	
	public void testComparator() {
		PersistentClass cm = cfg.getClassMapping("org.hibernate.test.legacy.Wicked");
		
		Property property = cm.getProperty("sortedEmployee");
		Collection col = (Collection) property.getValue();
		assertEquals(col.getComparatorClassName(),"org.hibernate.test.legacy.NonExistingComparator");
	}
}
