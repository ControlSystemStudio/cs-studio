package org.remotercp.util.serialize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

public class XMLConverterTest {

	private Person person;

	@Before
	public void setupObjects() {
		person = new Person("susi");
	}

	@Test
	public void testXMLConverter() {
		final XStream stream = new XStream();
		final String xml = stream.toXML(person);
		assertNotNull(xml);

		final Object obj = stream.fromXML(xml);
		if (obj instanceof Person) {
			final Person p = (Person) obj;
			assertEquals("susi", p.getName());
		} else {
			fail("The converted object is instance of an unknown type");
		}
	}


	private class Person {
		String name;

		public Person(final String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}
}
