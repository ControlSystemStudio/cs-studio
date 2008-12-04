/**
 * 
 */
package org.csstudio.dct.model.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.junit.Test;

/**
 * Test cases for {@link AbstractContainer}.
 * 
 * @author Sven Wende
 * 
 */
public class AbstractContainerTest {
	@Test
	public final void testInheritance() {
		Project project = new Project("Test");

		// prototype 1 (contains record 1)
		IPrototype p1 = new Prototype("p1");
		Record r1 = new Record("r1", "ai");
		p1.addRecord(r1);
		project.addMember(p1);

		// prototype 2 (contains record 2)
		IPrototype p2 = new Prototype("p2");
		IRecord r2 = new Record("r2", "ai");
		p2.addRecord(r2);
		project.addMember(p2);

		// prototype 3 (contains record 3 + instance of prototype 1 + instance
		// of prototype 2)
		IPrototype p3 = new Prototype("p2");
		IRecord r3 = new Record("r3", "ai");
		p3.addRecord(r3);
		Instance i1 = new Instance(p1);
		p3.addInstance(i1);
		Instance i2 = new Instance(p2);
		p3.addInstance(i2);
		project.addMember(p3);

		// instance of prototype 3
		IInstance i3 = new Instance(p3);
		project.addMember(i3);

		// asserts

		// .. physical linking
		assertSame("P1 is physical container of R1", r1.getContainer(), p1);
		assertSame("P2 is physical container of R2", r2.getContainer(), p2);
		assertSame("P3 is physical container of R3", r3.getContainer(), p3);
		assertSame("P3 is physical container of I1", i1.getContainer(), p3);
		assertSame("P3 is physical container of I2", i2.getContainer(), p3);

		assertSame("Project is physical container of P1", p1.getParentFolder(), project);
		assertSame("Project is physical container of P2", p2.getParentFolder(), project);
		assertSame("Project is physical container of P3", p3.getParentFolder(), project);
		assertSame("Project is physical container of I3", i3.getParentFolder(), project);

		// .. record creation on inheritance
		assertEquals("I1 has as many records as P1", 1, i1.getRecords().size());
		assertEquals("I2 has as many records as P2", 1, i2.getRecords().size());
		assertEquals("I3 has as many records as P3", 1, i3.getRecords().size());

		// .. instance creation on inheritance
		assertEquals("I3 has as many records as P3", 2, i3.getInstances().size());
		assertSame("I3 has as many instances as P3", i3.getInstances().size(), p3.getInstances().size());

		// .. recursive record creation on inheritance
		assertEquals("Sub-Instance 1 in I3 has as many records as Sub-Instance 1 in P3", 1, i3.getInstances().get(0).getRecords().size());
		assertEquals("Sub-Instance 2 in I3 has as many records as Sub-Instance 2 in P3", 1, i3.getInstances().get(1).getRecords().size());

		// .. inheritance linking
		assertSame("Record in I1 inherits from R1", i1.getRecords().get(0).getParentRecord(), r1);
		assertSame("Record in I2 inherits from R2", i2.getRecords().get(0).getParentRecord(), r2);
		assertSame("Record in I3 inherits from R3", i3.getRecords().get(0).getParentRecord(), r3);

		assertSame("I1 inherits from P1", i1.getParent(), p1);
		assertSame("I2 inherits from P2", i2.getParent(), p2);
		assertSame("I3 inherits from P3", i3.getParent(), p3);

		assertSame("Sub-Instance 1 in I3 inherits from Sub-Instance 1 in P3", i3.getInstances().get(0).getParent(), p3.getInstances().get(0));
		assertSame("Sub-Instance 2 in I3 inherits from Sub-Instance 2 in P3", i3.getInstances().get(1).getParent(), p3.getInstances().get(1));

		assertSame("Records in Sub-Instance 1 in I3 inherit from records in Sub-Instance 1 in P3", i3.getInstances().get(0).getRecords().get(0)
				.getParentRecord(), p3.getInstances().get(0).getRecords().get(0));
		assertSame("Records in Sub-Instance 2 in I3 inherit from records in Sub-Instance 2 in P3", i3.getInstances().get(1).getRecords().get(0)
				.getParentRecord(), p3.getInstances().get(1).getRecords().get(0));

		assertTrue(p1.getDependentContainers().contains(i1));
		assertTrue(p2.getDependentContainers().contains(i2));
		assertTrue(p3.getDependentContainers().contains(i3));

		assertFalse(r1.isInheritedFromPrototype());
		assertFalse(r2.isInheritedFromPrototype());
		assertFalse(r3.isInheritedFromPrototype());

		assertTrue(p3.getInstance(0).getRecords().get(0).isInheritedFromPrototype());
		assertTrue(p3.getInstance(1).getRecords().get(0).isInheritedFromPrototype());

		// Adding a new record in prototype 1
		Record r11 = new Record("r11", "ai");
		p1.addRecord(r11);

		assertEquals(2, p3.getInstances().get(0).getRecords().size());
		assertSame(r11, p3.getInstances().get(0).getRecords().get(1).getParentRecord());

		assertEquals(2, i3.getInstances().get(0).getRecords().size());
		assertSame(i3.getInstances().get(0).getRecords().get(1).getParentRecord(), p3.getInstances().get(0).getRecords().get(1));

		assertTrue(i3.getInstances().get(0).getRecords().get(1).isInheritedFromPrototype());

		// Removing the record from prototype 2
		p2.removeRecord(p2.getRecords().get(0));

		assertEquals(0, i2.getRecords().size());
		assertEquals(0, i3.getInstances().get(1).getRecords().size());

		// Removing Sub-instance 1 from prototype 3
		p3.removeInstance(p3.getInstances().get(0));
		assertEquals(1, i3.getInstances().size());
		assertFalse(p1.getDependentContainers().contains(i1));

		// Adding new Sub-instance to prototype 3
		IInstance i5 = new Instance(p1);
		p3.addInstance(i5);
		assertEquals(2, i3.getInstances().size());
		assertEquals(2, i3.getInstances().get(1).getRecords().size());

		assertSame(i3.getInstances().get(1).getParent(), i5);
		assertSame(i5.getRecords().get(0), i3.getInstances().get(1).getRecords().get(0).getParentRecord());
	}
}
