package org.remotercp.util.intersection;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class IntersectionUtilTest {
	private Collection<Set<Person>> persons;

	@Before
	public void setUp() {
		this.persons = new ArrayList<Set<Person>>();

		Set<Person> person1 = new HashSet<Person>();
		Person p1 = new Person("Klaus");
		Person p2 = new Person("John");
		Person p3 = new Person("Sandra");
		person1.add(p1);
		person1.add(p2);
		person1.add(p3);

		Set<Person> person2 = new HashSet<Person>();
		Person p4 = new Person("Klaus");
		Person p5 = new Person("Sandra");
		Person p6 = new Person("Peter");
		person2.add(p4);
		person2.add(p5);
		person2.add(p6);

		Set<Person> person3 = new HashSet<Person>();
		Person p7 = new Person("Klaus");
		person3.add(p7);

		this.persons.add(person1);
		this.persons.add(person2);
		this.persons.add(person3);
	}

	@Test
	public void testIntersection() {
		Set<Object> intersectionSet = CollectionIntersectionUtil
				.getIntersectionSet(this.persons);
		assertEquals(1, intersectionSet.size());
		Person p = (Person) intersectionSet.iterator().next();
		assertEquals("Klaus", p.getName());

	}

	private class Person {

		private String name;

		public Person(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Person) {
				return this.getName().equals(((Person) obj).getName());
			}
			return super.equals(obj);
		}
	}

}
