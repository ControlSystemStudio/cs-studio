package org.remotercp.util.serialize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Serializable;

import org.junit.Test;

public class ByteConverterTest {
	private String text = new String("message");

	@Test
	public void testByteConverter() {
		try {
			byte byteCode[] = SerializeUtil.convertObjectToByte(text);
			assertNotNull(byteCode);

			Object obj = SerializeUtil.convertByteToObject(byteCode);
			assertNotNull(obj);
			if (obj instanceof String) {
				String convertedText = (String) obj;
				assertEquals(text, convertedText);
			} else {
				fail();
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testNotSerializeableObject() {
		try {
			SerializeUtil.convertObjectToByte(new Person("susi"));

			fail("Should have thrown a NotSerializableException");

		} catch (IOException e) {
		}
	}

	@Test
	public void testSerializableObject() {
		SerializablePerson p = new SerializablePerson("john");
		byte serializedPerson[] = null;

		try {
			serializedPerson = SerializeUtil.convertObjectToByte(p);
			assertNotNull(serializedPerson);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}

		try {
			Object obj = SerializeUtil.convertByteToObject(serializedPerson);
			if (obj instanceof SerializablePerson) {
				SerializablePerson ps = (SerializablePerson) obj;
				assertEquals("john", ps.getName());
			} else {
				fail();
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			fail();
		}
	}

	private class Person {
		String name;

		public Person(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}

	private class SerializablePerson implements Serializable {

		private static final long serialVersionUID = -2510119924783620004L;
		String name;

		public SerializablePerson(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
}
