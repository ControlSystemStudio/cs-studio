package org.csstudio.dct.nameresolution;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.commands.AddInstanceCommand;
import org.csstudio.dct.model.commands.AddPrototypeCommand;
import org.csstudio.dct.model.commands.AddRecordCommand;
import org.csstudio.dct.model.internal.Instance;
import org.csstudio.dct.model.internal.Project;
import org.csstudio.dct.model.internal.ProjectFactory;
import org.csstudio.dct.model.internal.Prototype;
import org.csstudio.dct.model.internal.RecordFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link RecordFinder}.
 *
 * @author Sven Wende
 *
 */
public final class RecordFinderTest {
    private IInstance car;
    private IInstance motor;

    private IRecord wheel1;
    private IRecord wheel2;
    private IRecord wheel3;
    private IRecord wheel4;

    private IRecord cylinder1;
    private IRecord cylinder2;

    /**
     * Setup.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        Project project = ProjectFactory.createNewDCTProject("carproject");

        // .. Motor Prototype (has 2 cylinders represented as records)
        Prototype motorPrototype = new Prototype("motor", UUID.randomUUID());
        new AddPrototypeCommand(project, motorPrototype).execute();
        new AddRecordCommand(motorPrototype, RecordFactory.createRecord(project, "ai", "cylinder1", UUID.randomUUID())).execute();
        new AddRecordCommand(motorPrototype, RecordFactory.createRecord(project, "ai", "cylinder2", UUID.randomUUID())).execute();

        // .. Car Prototype (has 4 wheels represented as records and a motor
        // represented as instance)
        Prototype carPrototype = new Prototype("car", UUID.randomUUID());
        new AddPrototypeCommand(project, carPrototype).execute();
        new AddRecordCommand(carPrototype, RecordFactory.createRecord(project, "ai", "wheel1", UUID.randomUUID())).execute();
        new AddRecordCommand(carPrototype, RecordFactory.createRecord(project, "ai", "wheel2", UUID.randomUUID())).execute();
        new AddRecordCommand(carPrototype, RecordFactory.createRecord(project, "ai", "wheel3", UUID.randomUUID())).execute();
        new AddRecordCommand(carPrototype, RecordFactory.createRecord(project, "ai", "wheel4", UUID.randomUUID())).execute();
        new AddInstanceCommand(carPrototype, new Instance(motorPrototype, UUID.randomUUID())).execute();

        // .. Car instance
        car = new Instance(carPrototype, UUID.randomUUID());
        new AddInstanceCommand(project, car).execute();

        wheel1 = car.getRecords().get(0);
        wheel2 = car.getRecords().get(1);
        wheel3 = car.getRecords().get(2);
        wheel4 = car.getRecords().get(3);

        motor = car.getInstance(0);

        cylinder1 = motor.getRecords().get(0);
        cylinder2 = motor.getRecords().get(1);
    }

    /**
     * Test method for
     * {@link RecordFinder#findRecordByPath(String, org.csstudio.dct.model.IContainer)}.
     *
     * @throws Exception
     */
    @Test
    public void testfindRecordByPath() throws Exception {
        // things you can find with [car] as the root node
        assertEquals(wheel1, RecordFinder.findRecordByPath("wheel1", car));
        assertEquals(wheel2, RecordFinder.findRecordByPath("wheel2", car));
        assertEquals(wheel3, RecordFinder.findRecordByPath("wheel3", car));
        assertEquals(wheel4, RecordFinder.findRecordByPath("wheel4", car));
        assertEquals(cylinder1, RecordFinder.findRecordByPath("motor.cylinder1", car));
        assertEquals(cylinder2, RecordFinder.findRecordByPath("motor.cylinder2", car));
        // things you cannot find with [car] as the root node
        assertEquals(null, RecordFinder.findRecordByPath("cylinder1", car));
        assertEquals(null, RecordFinder.findRecordByPath("cylinder2", car));
        // things you can find with [motor] as the root node
        assertEquals(wheel1, RecordFinder.findRecordByPath("wheel1", motor));
        assertEquals(wheel2, RecordFinder.findRecordByPath("wheel2", motor));
        assertEquals(wheel3, RecordFinder.findRecordByPath("wheel3", motor));
        assertEquals(wheel4, RecordFinder.findRecordByPath("wheel4", motor));
        assertEquals(cylinder1, RecordFinder.findRecordByPath("cylinder1", motor));
        assertEquals(cylinder2, RecordFinder.findRecordByPath("cylinder2", motor));
        assertEquals(cylinder1, RecordFinder.findRecordByPath("motor.cylinder1", motor));
        assertEquals(cylinder2, RecordFinder.findRecordByPath("motor.cylinder2", motor));
        // things you cannot find with [motor] as the root node
        // none
    }

}
