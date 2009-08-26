package org.csstudio.config.ioconfig.model.pbmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.csstudio.config.ioconfig.model.Document;
import org.csstudio.config.ioconfig.model.DummyRepository;
import org.csstudio.config.ioconfig.model.Ioc;
import org.csstudio.config.ioconfig.model.Node;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.Repository;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test the {@link Channel} and the {@link StructChannel} classes.
 * 
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 12.01.2009
 */
public class Channel_Test {

    private static final boolean OUTPUT = false;
    private static final boolean INPUT = true;
    private ProfibusSubnet _subnet;
    private Master _master;
    private Slave _slave;
    private Module _module;

    // private ChannelStructure _structure;

    @Test
    public void testDocument() {
        Channel out = new Channel();

        Set<Document> docList = new HashSet<Document>();
        Document doc1 = new Document();
        doc1.setId("docId1");
        Document doc2 = new Document();
        doc2.setId("docId2");
        Document doc3 = new Document();
        doc3.setId("docId3");

        docList.add(doc1);
        docList.add(doc2);
        docList.add(doc3);

        out.setDocuments(docList);

        assertTrue(docList.containsAll(out.getDocuments()));
        assertTrue(out.getDocuments().containsAll(docList));
    }

    @Test
    public void testChannelNumber() throws PersistenceException {

        Channel out = ChannelStructure.makeSimpleChannel(_module, false).getFirstChannel();
        out.save();
        _module.setInputOffset(32);
        _module.setOutputOffset(64);
        assertEquals(64, out.getFullChannelNumber());
        out.setChannelNumber(2);
        out.save();
        assertEquals(66, out.getFullChannelNumber());
        out.setInput(true);
        out.save();
        assertEquals(34, out.getFullChannelNumber());
    }

    @Test
    public void TestInput() {
        Channel out = new Channel();
        out.setInput(true);
        assertTrue(out.isInput());
    }

    @Test
    public void testOutput() {
        Channel out = new Channel();
        out.setOutput(true);
        assertTrue(out.isOutput());
    }

    @Test
    public void testIoName() {
        Channel out = new Channel();
        assertNull(out.getIoName());
        out.setIoName("ioName");
        assertEquals(out.getIoName(), "ioName");
    }

    @Test
    public void testDigital() {
        Channel out = new Channel();
        assertFalse(out.isDigital());
        out.setDigital(true);
        assertTrue(out.isDigital());
    }

    @Test
    public void testChSize() {
        Channel out = ChannelStructure.makeSimpleChannel(_module, false).getFirstChannel();
        // Channel out = new Channel(_structure);
        assertEquals(1, out.getChSize());
        out.setChannelTypeNonHibernate(DataType.DS33);
        assertEquals(DataType.DS33, out.getChannelType());
        assertTrue(out.getChSize() == 40);
    }

    @Test
    public void testGetCurrenUserParamDataIndex() {
        Channel out = new Channel();
        assertNull(out.getCurrenUserParamDataIndex());
        out.setCurrenUserParamDataIndex("");
        assertEquals(out.getCurrenUserParamDataIndex(), "");
        out.setCurrenUserParamDataIndex("0x12");
        assertEquals(out.getCurrenUserParamDataIndex(), "0x12");
        out.setCurrenUserParamDataIndex("^1234567890ß´qwertzuiopü+asdfghjklöä#yxcvbnm,.-QAY\\\"");
        assertEquals(out.getCurrenUserParamDataIndex(),
                "^1234567890ß´qwertzuiopü+asdfghjklöä#yxcvbnm,.-QAY\\\"");
    }

    @Test
    public void testGetCurrentValue() {
        Channel out = new Channel();
        assertNull(out.getCurrentValue());
        out.setCurrentValue("");
        assertEquals(out.getCurrentValue(), "");
        out.setCurrentValue("0x12");
        assertEquals(out.getCurrentValue(), "0x12");
        out.setCurrentValue("^1234567890ß´qwertzuiopü+asdfghjklöä#yxcvbnm,.-QAY\\\"");
        assertEquals(out.getCurrentValue(),
                "^1234567890ß´qwertzuiopü+asdfghjklöä#yxcvbnm,.-QAY\\\"");
    }

    @Test
    public void testGetEpicsAdressString() throws PersistenceException {

        _slave.moveSortIndex((short) 17);
        _slave.localSave();

        _module = new Module(_slave);
        _module.moveSortIndex((short) 3);
        _module.localSave();

        Channel out = ChannelStructure.makeSimpleChannel(_module, false).getFirstChannel();
        out.localSave();
        assertNotNull(out.getEpicsAddressStringNH());
        assertEquals("@Subnet:17/0 'T=BIT'", out.getEpicsAddressStringNH());
        out.moveSortIndex((short) 24);
        assertNotNull(out.getEpicsAddressStringNH());
        assertEquals("@Subnet:17/0 'T=BIT'", out.getEpicsAddressStringNH());
        out.localSave();
        assertNotNull(out.getEpicsAddressStringNH());
        assertEquals("@Subnet:17/0 'T=BIT'", out.getEpicsAddressStringNH());
    }

    @Ignore("Not yet implemented")
    @Test
    public void testGetStruct() {
        Channel out = new Channel();
        out.getStruct();
        fail("Not yet implemented");
    }

    /**
     * Test only for {@link Channel}.
     */

    @Test
    public void testModule() {
        ProfibusSubnet profibusSubnet = new ProfibusSubnet(new Ioc());
        Master master = new Master(profibusSubnet);
        Slave slave = new Slave(master);
        Module module = new Module(slave);
        ChannelStructure structure = new ChannelStructure();
        structure.setModule(module);
        Channel out = new Channel(structure, false, false);
        assertNotNull(out.getParent());
        assertNotNull(out.getModule());

        module.setId(345);

        assertNotNull(out.getModule());
        assertEquals(out.getModule(), module);
    }

    /**
     * Test only for the {@link StructChannel}.
     */
    @Ignore("Not yet implemented")
    @Test
    public void testChannelStructure() {
        Channel out = new Channel();
        assertNull(out.getParent());
        assertNull(out.getChannelStructure());

        ChannelStructure channelStructure = new ChannelStructure();
        channelStructure.setId(456);
        channelStructure.addChild(out);

        assertNotNull(out.getChannelStructure());
        assertEquals(out.getChannelStructure(), channelStructure);

    }

    @Test
    public void testOutputChannels() throws PersistenceException, InterruptedException {
        // create Master
        _master.setFdlAddress((short) 4711);
        _master.localSave();
        assertEquals("@Subnet", _master.getEpicsAdressString());

        // create Slave
        _slave.moveSortIndex((short) 815);
        _slave.localSave();
        assertEquals("@Subnet:815", _slave.getEpicsAdressString());

        /*
         * Test 1. Module on a Slave: Create Module 1, SortIndex 21 Create three ChannelStructure
         * with one simple Channel per ChannelStructure channelM1C1: channelM1C1, INT16, OUTPUT,
         * ANALOG channelM1C2: channelM1C2, UNSIGN16, OUTPUT, ANALOG channelM1C3: channelM1C2, INT8,
         * OUTPUT, ANALOG
         */
        // create Module 1
        Module module1 = new Module(_slave, "Module 1");
        module1.moveSortIndex((short) 21);
        module1.localSave();

        testModule("@Subnet:815", (short) 21, 0, 0, 0, 0, module1);

        // create Channels for Module 1
        ChannelStructure structureM1C1 = ChannelStructure.makeSimpleChannel(module1, OUTPUT);

        Channel channelM1C1 = structureM1C1.getFirstChannel();
        channelM1C1.setDigital(false);
        channelM1C1.setName("channelM1C1");
        channelM1C1.setChannelTypeNonHibernate(DataType.INT16);
        channelM1C1.localSave();

        ChannelStructure structureM1C2 = ChannelStructure.makeSimpleChannel(module1, OUTPUT);

        Channel channelM1C2 = structureM1C2.getFirstChannel();
        channelM1C2.setDigital(false);
        channelM1C2.setName("channelM1C2");
        channelM1C2.setChannelTypeNonHibernate(DataType.UINT16);
        channelM1C2.localSave();

        ChannelStructure structureM1C3 = ChannelStructure.makeSimpleChannel(module1, OUTPUT);

        Channel channelM1C3 = structureM1C3.getFirstChannel();
        channelM1C3.setDigital(false);
        channelM1C3.setName("channelM1C3");
        channelM1C3.setChannelTypeNonHibernate(DataType.INT8);
        channelM1C3.localSave();

        testModule("@Subnet:815", (short) 21, 0, 0, 0, 5, module1);

        testSimpleChannel("@Subnet:815/0 'T=INT16'", (short) 0, channelM1C1, structureM1C1);
        testSimpleChannel("@Subnet:815/2 'T=UNSIGN16'", (short) 1, channelM1C2, structureM1C2);
        testSimpleChannel("@Subnet:815/4 'T=INT8'", (short) 2, channelM1C3, structureM1C3);

        /*
         * Test 2. Module on a Slave: Create Module 2, SortIndex 22 Create three ChannelStructure
         * with one simple Channel per ChannelStructure channelM2C1: channelM2C1, UNSIGN8, OUTPUT,
         * ANALOG channelM2C2: channelM2C2, INT16, OUTPUT, ANALOG channelM2C3: channelM2C2, UNSIGN16,
         * OUTPUT, ANALOG
         */
        // --- create Module 2 -----------------------
        Module module2 = new Module(_slave, "Module 2");
        module2.moveSortIndex((short) 22);
        module2.localSave();

        testModule("@Subnet:815", (short) 22, 0, 0, 5, 0, module2);

        // create Channels for Module 2

        ChannelStructure structureM2C1 = ChannelStructure.makeSimpleChannel(module2, false);

        Channel channelM2C1 = structureM2C1.getFirstChannel();
        channelM2C1.setDigital(false);
        channelM2C1.setName("channelM2C1");
        channelM2C1.setChannelTypeNonHibernate(DataType.UINT8);
        channelM2C1.localSave();

        ChannelStructure structureM2C2 = ChannelStructure.makeSimpleChannel(module2, false);

        Channel channelM2C2 = structureM2C2.getFirstChannel();
        channelM2C2.setDigital(false);
        channelM2C2.setName("channelM2C2");
        channelM2C2.setChannelTypeNonHibernate(DataType.INT16);
        channelM2C2.localSave();

        ChannelStructure structureM2C3 = ChannelStructure.makeSimpleChannel(module2, false);

        Channel channelM2C3 = structureM2C3.getFirstChannel();
        channelM2C3.setDigital(false);
        channelM2C3.setName("channelM2C3");
        channelM2C3.setChannelTypeNonHibernate(DataType.UINT16);
        channelM2C3.localSave();

        testModule("@Subnet:815", (short) 22, 0, 0, 5, 5, module2);

        testSimpleChannel("@Subnet:815/5 'T=UNSIGN8,L=0,H=32768'", (short) 0, channelM2C1,
                structureM2C1);
        testSimpleChannel("@Subnet:815/6 'T=INT16'", (short) 1, channelM2C2, structureM2C2);
        testSimpleChannel("@Subnet:815/8 'T=UNSIGN16'", (short) 2, channelM2C3, structureM2C3);

        /*
         * Test 3. Module on a Slave: Create Module 3, SortIndex 23 Create three ChannelStructure
         * with one simple Channel per ChannelStructure channelM3C1: channelM3C1, UNSIGN8, OUTPUT,
         * ANALOG channelM3C2: channelM3C2, INT16, OUTPUT, ANALOG channelM3C3: channelM3C2, UNSIGN16,
         * OUTPUT, ANALOG
         */
        // --- create Module 3 -----------------------
        Module module3 = new Module(_slave, "Module 3");
        module3.moveSortIndex((short) 23);
        module3.localSave();

        testModule("@Subnet:815", (short) 23, 0, 0, 10, 0, module3);

        // create Channels for Module 3
        ChannelStructure structureM3C1 = ChannelStructure.makeSimpleChannel(module3, false);

        Channel channelM3C1 = structureM3C1.getFirstChannel();
        channelM3C1.setDigital(false);
        channelM3C1.setName("channelM3C1");
        channelM3C1.setChannelTypeNonHibernate(DataType.UINT8);
        channelM3C1.localSave();

        ChannelStructure structureM3C2 = ChannelStructure.makeSimpleChannel(module3, false);

        Channel channelM3C2 = structureM3C2.getFirstChannel();
        channelM3C2.setDigital(false);
        channelM3C2.setName("channelM3C2");
        channelM3C2.setChannelTypeNonHibernate(DataType.INT16);
        channelM3C2.localSave();

        ChannelStructure structureM3C3 = ChannelStructure.makeSimpleChannel(module3, false);

        Channel channelM3C3 = structureM3C3.getFirstChannel();
        channelM3C3.setDigital(false);
        channelM3C3.setName("channelM3C3");
        channelM3C3.setChannelTypeNonHibernate(DataType.UINT16);
        channelM3C3.localSave();

        testModule("@Subnet:815", (short) 23, 0, 0, 10, 5, module3);

        testSimpleChannel("@Subnet:815/10 'T=UNSIGN8,L=0,H=32768'", (short) 0, channelM3C1,
                structureM3C1);
        testSimpleChannel("@Subnet:815/11 'T=INT16'", (short) 1, channelM3C2, structureM3C2);
        testSimpleChannel("@Subnet:815/13 'T=UNSIGN16'", (short) 2, channelM3C3, structureM3C3);

        channelM2C1.setChannelTypeNonHibernate(DataType.INT16);
        channelM2C1.localSave();

        testSimpleChannel("@Subnet:815/5 'T=INT16'", (short) 0, channelM2C1, structureM2C1);
        testSimpleChannel("@Subnet:815/7 'T=INT16'", (short) 1, channelM2C2, structureM2C2);
        testSimpleChannel("@Subnet:815/9 'T=UNSIGN16'", (short) 2, channelM2C3, structureM2C3);

        testSimpleChannel("@Subnet:815/11 'T=UNSIGN8,L=0,H=32768'", (short) 0, channelM3C1,
                structureM3C1);
        testSimpleChannel("@Subnet:815/12 'T=INT16'", (short) 1, channelM3C2, structureM3C2);
        testSimpleChannel("@Subnet:815/14 'T=UNSIGN16'", (short) 2, channelM3C3, structureM3C3);
    }

    @Test
    public void testInputChannels() throws PersistenceException, InterruptedException {
        // create Master
        _master.setFdlAddress((short) 4711);
        _master.localSave();
        assertEquals("@Subnet", _master.getEpicsAdressString());

        // create Slave
        _slave.moveSortIndex((short) 815);
        _slave.localSave();
        assertEquals("@Subnet:815", _slave.getEpicsAdressString());

        /*
         * Test 1. Module on a Slave: Create Module 1, SortIndex 21 Create three ChannelStructure
         * with one simple Channel per ChannelStructure channelM1C1: channelM1C1, INT16, INPUT,
         * ANALOG channelM1C2: channelM1C2, UNSIGN16, INPUT, ANALOG channelM1C3: channelM1C2, INT8,
         * INPUT, ANALOG
         */
        // create Module 1
        Module module1 = new Module(_slave, "Module 1");
        module1.moveSortIndex((short) 21);
        module1.localSave();

        testModule("@Subnet:815", (short) 21, 0, 0, 0, 0, module1);

        // create Channels for Module 1
        ChannelStructure structureM1C1 = ChannelStructure.makeSimpleChannel(module1, INPUT);

        Channel channelM1C1 = structureM1C1.getFirstChannel();
        channelM1C1.setDigital(false);
        channelM1C1.setName("channelM1C1");
        channelM1C1.setChannelTypeNonHibernate(DataType.INT16);
        channelM1C1.localSave();

        ChannelStructure structureM1C2 = ChannelStructure.makeSimpleChannel(module1, INPUT);

        Channel channelM1C2 = structureM1C2.getFirstChannel();
        channelM1C2.setDigital(false);
        channelM1C2.setName("channelM1C2");
        channelM1C2.setChannelTypeNonHibernate(DataType.UINT16);
        channelM1C2.localSave();

        ChannelStructure structureM1C3 = ChannelStructure.makeSimpleChannel(module1, INPUT);

        Channel channelM1C3 = structureM1C3.getFirstChannel();
        channelM1C3.setDigital(false);
        channelM1C3.setName("channelM1C3");
        channelM1C3.setChannelTypeNonHibernate(DataType.INT8);
        channelM1C3.localSave();

        testModule("@Subnet:815", (short) 21, 0, 5, 0, 0, module1);

        testSimpleChannel("@Subnet:815/0 'T=INT16'", (short) 0, channelM1C1, structureM1C1);
        testSimpleChannel("@Subnet:815/2 'T=UNSIGN16'", (short) 1, channelM1C2, structureM1C2);
        testSimpleChannel("@Subnet:815/4 'T=INT8'", (short) 2, channelM1C3, structureM1C3);

        /*
         * Test 2. Module on a Slave: Create Module 2, SortIndex 22 Create three ChannelStructure
         * with one simple Channel per ChannelStructure channelM2C1: channelM2C1, UNSIGN8, INPUT,
         * ANALOG channelM2C2: channelM2C2, INT16, INPUT, ANALOG channelM2C3: channelM2C2, UNSIGN16,
         * INPUT, ANALOG
         */
        // create Module 2
        Module module2 = new Module(_slave, "Module 2");
        module2.moveSortIndex((short) 22);
        module2.localSave();

        testModule("@Subnet:815", (short) 22, 5, 0, 0, 0, module2);

        // create Channels for Module 2
        ChannelStructure structureM2C1 = ChannelStructure.makeSimpleChannel(module2, INPUT);

        Channel channelM2C1 = structureM2C1.getFirstChannel();
        channelM2C1.setDigital(false);
        channelM2C1.setName("channelM2C1");
        channelM2C1.setChannelTypeNonHibernate(DataType.UINT8);
        channelM2C1.localSave();

        ChannelStructure structureM2C2 = ChannelStructure.makeSimpleChannel(module2, INPUT);

        Channel channelM2C2 = structureM2C2.getFirstChannel();
        channelM2C2.setDigital(false);
        channelM2C2.setName("channelM2C2");
        channelM2C2.setChannelTypeNonHibernate(DataType.INT16);
        channelM2C2.localSave();

        ChannelStructure structureM2C3 = ChannelStructure.makeSimpleChannel(module2, INPUT);

        Channel channelM2C3 = structureM2C3.getFirstChannel();
        channelM2C3.setDigital(false);
        channelM2C3.setName("channelM2C3");
        channelM2C3.setChannelTypeNonHibernate(DataType.UINT16);
        channelM2C3.localSave();

        testModule("@Subnet:815", (short) 22, 5, 5, 0, 0, module2);

        testSimpleChannel("@Subnet:815/5 'T=UNSIGN8,L=0,H=32768'", (short) 0, channelM2C1,
                structureM2C1);
        testSimpleChannel("@Subnet:815/6 'T=INT16'", (short) 1, channelM2C2, structureM2C2);
        testSimpleChannel("@Subnet:815/8 'T=UNSIGN16'", (short) 2, channelM2C3, structureM2C3);

        /*
         * Test 3. Module on a Slave: Create Module 3, SortIndex 23 Create three ChannelStructure
         * with one simple Channel per ChannelStructure channelM3C1: channelM3C1, UNSIGN8, INPUT,
         * ANALOG channelM3C2: channelM3C2, INT16, INPUT, ANALOG channelM3C3: channelM3C2, UNSIGN16,
         * INPUT, ANALOG
         */
        // create Module 3
        Module module3 = new Module(_slave, "Module 3");
        module3.moveSortIndex((short) 23);
        module3.localSave();

        testModule("@Subnet:815", (short) 23, 10, 0, 0, 0, module3);

        // create Channels for Module 3
        ChannelStructure structureM3C1 = ChannelStructure.makeSimpleChannel(module3, INPUT);

        Channel channelM3C1 = structureM3C1.getFirstChannel();
        channelM3C1.setDigital(false);
        channelM3C1.setName("channelM3C1");
        channelM3C1.setChannelTypeNonHibernate(DataType.UINT8);
        channelM3C1.localSave();

        ChannelStructure structureM3C2 = ChannelStructure.makeSimpleChannel(module3, INPUT);

        Channel channelM3C2 = structureM3C2.getFirstChannel();
        channelM3C2.setDigital(false);
        channelM3C2.setName("channelM3C2");
        channelM3C2.setChannelTypeNonHibernate(DataType.INT16);
        channelM3C2.localSave();

        ChannelStructure structureM3C3 = ChannelStructure.makeSimpleChannel(module3, INPUT);

        Channel channelM3C3 = structureM3C3.getFirstChannel();
        channelM3C3.setDigital(false);
        channelM3C3.setName("channelM3C3");
        channelM3C3.setChannelTypeNonHibernate(DataType.UINT16);
        channelM3C3.localSave();

        testModule("@Subnet:815", (short) 23, 10, 5, 0, 0, module3);

        testSimpleChannel("@Subnet:815/10 'T=UNSIGN8,L=0,H=32768'", (short) 0, channelM3C1,
                structureM3C1);
        testSimpleChannel("@Subnet:815/11 'T=INT16'", (short) 1, channelM3C2, structureM3C2);
        testSimpleChannel("@Subnet:815/13 'T=UNSIGN16'", (short) 2, channelM3C3, structureM3C3);

        channelM2C1.setChannelTypeNonHibernate(DataType.INT16);
        channelM2C1.localSave();

        testSimpleChannel("@Subnet:815/5 'T=INT16'", (short) 0, channelM2C1, structureM2C1);
        testSimpleChannel("@Subnet:815/7 'T=INT16'", (short) 1, channelM2C2, structureM2C2);
        testSimpleChannel("@Subnet:815/9 'T=UNSIGN16'", (short) 2, channelM2C3, structureM2C3);

        testSimpleChannel("@Subnet:815/11 'T=UNSIGN8,L=0,H=32768'", (short) 0, channelM3C1,
                structureM3C1);
        testSimpleChannel("@Subnet:815/12 'T=INT16'", (short) 1, channelM3C2, structureM3C2);
        testSimpleChannel("@Subnet:815/14 'T=UNSIGN16'", (short) 2, channelM3C3, structureM3C3);
    }

    @Test
    public void testInputAndOutputChannels() throws PersistenceException {
        // create Master
        _master.setFdlAddress((short) 4711);
        _master.localSave();
        assertEquals("@Subnet", _master.getEpicsAdressString());

        // create Slave
        _slave.setName("Slave");
        _slave.moveSortIndex((short) 815);
        _slave.localSave();
        assertEquals("@Subnet:815", _slave.getEpicsAdressString());

        // create Module 1
        Module module1 = new Module(_slave, "Module 1");
        module1.moveSortIndex((short) 21);
        module1.localSave();

        testModule("@Subnet:815", (short) 21, 0, 0, 0, 0, module1);

        // create Channels for Module 1
        ChannelStructure structureM1C1 = ChannelStructure.makeSimpleChannel(module1, INPUT);
        Channel channelM1C1 = structureM1C1.getFirstChannel();
        channelM1C1.setDigital(false);
        channelM1C1.setName("channelM1C1");
        channelM1C1.setChannelTypeNonHibernate(DataType.INT8);
        channelM1C1.localSave();

        ChannelStructure structureM1C2 = ChannelStructure.makeSimpleChannel(module1, INPUT);
        Channel channelM1C2 = structureM1C2.getFirstChannel();
        channelM1C2.setDigital(false);
        channelM1C2.setName("channelM1C2");
        channelM1C2.setChannelTypeNonHibernate(DataType.INT16);
        // channelM1C2.localSave();

        ChannelStructure structureM1C3 = ChannelStructure.makeSimpleChannel(module1, OUTPUT);
        Channel channelM1C3 = structureM1C3.getFirstChannel();
        channelM1C3.setDigital(false);
        channelM1C3.setName("channelM1C3");
        channelM1C3.setChannelTypeNonHibernate(DataType.INT16);
        // channelM1C3.localSave();

        ChannelStructure structureM1C4 = ChannelStructure.makeSimpleChannel(module1, OUTPUT);
        Channel channelM1C4 = structureM1C4.getFirstChannel();
        channelM1C4.setDigital(false);
        channelM1C4.setName("channelM1C4");
        channelM1C4.setChannelTypeNonHibernate(DataType.INT16);
        // channelM1C4.localSave();

        testModule("@Subnet:815", (short) 21, 0, 3, 0, 4, module1);

        assertEquals("@Subnet:815/0 'T=INT8'", channelM1C1.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/1 'T=INT16'", channelM1C2.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/0 'T=INT16'", channelM1C3.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/2 'T=INT16'", channelM1C4.getEpicsAddressStringNH());

        // create Module 2
        Module module2 = new Module(_slave, "Module 2");
        module2.moveSortIndex((short) 22);
        module2.localSave();

        testModule("@Subnet:815", (short) 22, 3, 0, 4, 0, module2);

        // create Channels for Module 2
        ChannelStructure structureM2C1 = ChannelStructure.makeSimpleChannel(module2, OUTPUT);
        Channel channelM2C1 = structureM2C1.getFirstChannel();
        channelM2C1.setName("channelM2C1");
        channelM2C1.setChannelTypeNonHibernate(DataType.INT8);
        // channelM2C1.localSave();

        ChannelStructure structureM2C2 = ChannelStructure.makeSimpleChannel(module2, OUTPUT);
        Channel channelM2C2 = structureM2C2.getFirstChannel();
        channelM2C2.setName("channelM2C2");
        channelM2C2.setChannelTypeNonHibernate(DataType.INT16);
        // channelM2C2.localSave();

        ChannelStructure structureM2C3 = ChannelStructure.makeSimpleChannel(module2, INPUT);
        Channel channelM2C3 = structureM2C3.getFirstChannel();
        channelM2C3.setName("channelM2C3");
        channelM2C3.setChannelTypeNonHibernate(DataType.UINT16);
        // channelM2C3.localSave();

        ChannelStructure structureM2C4 = ChannelStructure.makeSimpleChannel(module2, INPUT);
        Channel channelM2C4 = structureM2C4.getFirstChannel();
        channelM2C4.setName("channelM2C4");
        channelM2C4.setChannelTypeNonHibernate(DataType.UINT16);
        // channelM2C4.localSave();

        testModule("@Subnet:815", (short) 22, 3, 4, 4, 3, module2);

        assertEquals("@Subnet:815/4 'T=INT8'", channelM2C1.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/5 'T=INT16'", channelM2C2.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/3 'T=UNSIGN16'", channelM2C3.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/5 'T=UNSIGN16'", channelM2C4.getEpicsAddressStringNH());

        // create Module 3
        Module module3 = new Module(_slave, "Module 3");
        module3.moveSortIndex((short) 23);
        module3.localSave();

        testModule("@Subnet:815", (short) 23, 7, 0, 7, 0, module3);

        // create Channels for Module 3

        ChannelStructure structureM3C1 = ChannelStructure.makeSimpleChannel(module3, INPUT);
        Channel channelM3C1 = structureM3C1.getFirstChannel();
        channelM3C1.setDigital(false);
        channelM3C1.setName("channelM3C1");
        channelM3C1.setChannelTypeNonHibernate(DataType.UINT8);
        // channelM3C1.localSave();

        ChannelStructure structureM3C2 = ChannelStructure.makeSimpleChannel(module3, OUTPUT);
        Channel channelM3C2 = structureM3C2.getFirstChannel();
        channelM3C2.setDigital(false);
        channelM3C2.setName("channelM3C2");
        channelM3C2.setChannelTypeNonHibernate(DataType.INT8);
        // channelM3C2.localSave();

        ChannelStructure structureM3C3 = ChannelStructure.makeSimpleChannel(module3, OUTPUT);
        Channel channelM3C3 = structureM3C3.getFirstChannel();
        channelM3C3.setDigital(false);
        channelM3C3.setName("channelM3C3");
        channelM3C3.setChannelTypeNonHibernate(DataType.UINT8);
        // channelM3C3.localSave();

        ChannelStructure structureM3C4 = ChannelStructure.makeSimpleChannel(module3, INPUT);
        Channel channelM3C4 = structureM3C4.getFirstChannel();
        channelM3C4.setDigital(false);
        channelM3C4.setName("channelM3C4");
        channelM3C4.setChannelTypeNonHibernate(DataType.UINT16);
        // channelM3C4.localSave();
        channelM3C4.getModule().getParent().getParent().update();
        testModule("@Subnet:815", (short) 23, 7, 3, 7, 2, module3);
        testSimpleChannel("@Subnet:815/7 'T=UNSIGN8,L=0,H=32768'", (short) 0, channelM3C1,
                structureM3C1);
        testSimpleChannel("@Subnet:815/7 'T=INT8'", (short) 1, channelM3C2, structureM3C2);
        testSimpleChannel("@Subnet:815/8 'T=UNSIGN8,L=0,H=32768'", (short) 2, channelM3C3,
                structureM3C3);
        testSimpleChannel("@Subnet:815/8 'T=UNSIGN16'", (short) 3, channelM3C4, structureM3C4);

        // create Module 4
        Module module4 = new Module(_slave, "Module 4");
        module4.moveSortIndex((short) 24);
        module4.localSave();

        testModule("@Subnet:815", (short) 24, 10, 0, 9, 0, module4);

        // create Channels for Module 4
        ChannelStructure structureM4C1 = ChannelStructure.makeSimpleChannelStructure(module4, true);
        Channel channelM4C1 = new Channel(structureM4C1, true, false);
        channelM4C1.setName("channelM4C1");
        channelM4C1.setChannelTypeNonHibernate(DataType.INT16);
        // channelM4C1.localSave();

        ChannelStructure structureM4C2 = ChannelStructure.makeSimpleChannelStructure(module4, true);
        Channel channelM4C2 = new Channel(structureM4C2, true, false);
        channelM4C2.setName("channelM4C2");
        channelM4C2.setChannelTypeNonHibernate(DataType.INT8);
        // channelM4C2.localSave();

        ChannelStructure structureM4C3 = ChannelStructure
                .makeSimpleChannelStructure(module4, false);
        Channel channelM4C3 = new Channel(structureM4C3, false, false);
        channelM4C3.setName("channelM4C3");
        channelM4C3.setChannelTypeNonHibernate(DataType.UINT8);
        // channelM4C3.localSave();

        ChannelStructure structureM4C4 = ChannelStructure
                .makeSimpleChannelStructure(module4, false);
        Channel channelM4C4 = new Channel(structureM4C4, false, false);
        channelM4C4.setName("channelM4C3");
        channelM4C4.setChannelTypeNonHibernate(DataType.UINT16);
        // channelM4C4.localSave();

        testModule("@Subnet:815", (short) 24, 10, 3, 9, 3, module4);
        assertEquals("@Subnet:815/10 'T=INT16'", channelM4C1.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/12 'T=INT8'", channelM4C2.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/9 'T=UNSIGN8,L=0,H=32768'", channelM4C3.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/10 'T=UNSIGN16'", channelM4C4.getEpicsAddressStringNH());

        // 
        channelM2C1.setChannelTypeNonHibernate(DataType.INT16);
        channelM2C1.localSave();

        assertEquals("@Subnet:815/4 'T=INT16'", channelM2C1.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/6 'T=INT16'", channelM2C2.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/3 'T=UNSIGN16'", channelM2C3.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/5 'T=UNSIGN16'", channelM2C4.getEpicsAddressStringNH());

        assertEquals("@Subnet:815/7 'T=UNSIGN8,L=0,H=32768'", channelM3C1.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/8 'T=INT8'", channelM3C2.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/9 'T=UNSIGN8,L=0,H=32768'", channelM3C3.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/8 'T=UNSIGN16'", channelM3C4.getEpicsAddressStringNH());

        assertEquals("@Subnet:815/10 'T=INT16'", channelM4C1.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/12 'T=INT8'", channelM4C2.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/10 'T=UNSIGN8,L=0,H=32768'", channelM4C3.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/11 'T=UNSIGN16'", channelM4C4.getEpicsAddressStringNH());

        module3.moveSortIndex((short) 22);
        // allUnsaved = PersistentAndUpdateHelper.getAllUnsaved();
        // // Erwarte das Module 2,4, So wie die Channels 2_1 bis 2_4 und 3_1 bis 3_4 geupdatet
        // wurden.
        // assertEquals(10, allUnsaved.size());
        // assertTrue(allUnsaved.contains(module2));
        // assertTrue(allUnsaved.contains(channelM2C1));
        // assertTrue(allUnsaved.contains(channelM2C2));
        // assertTrue(allUnsaved.contains(channelM2C3));
        // assertTrue(allUnsaved.contains(channelM2C4));
        // assertTrue(allUnsaved.contains(channelM3C1));
        // assertTrue(allUnsaved.contains(channelM3C2));
        // assertTrue(allUnsaved.contains(channelM3C3));
        // assertTrue(allUnsaved.contains(channelM3C4));

        assertEquals("@Subnet:815/3 'T=UNSIGN8,L=0,H=32768'", channelM3C1.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/4 'T=INT8'", channelM3C2.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/5 'T=UNSIGN8,L=0,H=32768'", channelM3C3.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/4 'T=UNSIGN16'", channelM3C4.getEpicsAddressStringNH());

        assertEquals("@Subnet:815/6 'T=INT16'", channelM2C1.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/8 'T=INT16'", channelM2C2.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/6 'T=UNSIGN16'", channelM2C3.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/8 'T=UNSIGN16'", channelM2C4.getEpicsAddressStringNH());

        assertEquals("@Subnet:815/10 'T=INT16'", channelM4C1.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/12 'T=INT8'", channelM4C2.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/10 'T=UNSIGN8,L=0,H=32768'", channelM4C3.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/11 'T=UNSIGN16'", channelM4C4.getEpicsAddressStringNH());

        module3.localSave();

        assertEquals("@Subnet:815/3 'T=UNSIGN8,L=0,H=32768'", channelM3C1.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/4 'T=INT8'", channelM3C2.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/5 'T=UNSIGN8,L=0,H=32768'", channelM3C3.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/4 'T=UNSIGN16'", channelM3C4.getEpicsAddressStringNH());

        assertEquals("@Subnet:815/6 'T=INT16'", channelM2C1.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/8 'T=INT16'", channelM2C2.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/6 'T=UNSIGN16'", channelM2C3.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/8 'T=UNSIGN16'", channelM2C4.getEpicsAddressStringNH());

        assertEquals("@Subnet:815/10 'T=INT16'", channelM4C1.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/12 'T=INT8'", channelM4C2.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/10 'T=UNSIGN8,L=0,H=32768'", channelM4C3.getEpicsAddressStringNH());
        assertEquals("@Subnet:815/11 'T=UNSIGN16'", channelM4C4.getEpicsAddressStringNH());

    }

    @Test
    public void testTestOutputStructChannels() throws PersistenceException {
        // create Master
        _master.setFdlAddress((short) 4711);
        _master.localSave();
        assertEquals("@Subnet", _master.getEpicsAdressString());

        // create Slave
        _slave.moveSortIndex((short) 815);
        _slave.localSave();
        assertEquals("@Subnet:815", _slave.getEpicsAdressString());

        // create Module 1
        Module module1 = new Module(_slave, "Module 1");
        module1.moveSortIndex((short) 21);
        module1.localSave();

        testModule("@Subnet:815", (short) 21, 0, 0, 0, 0, module1);

        ChannelStructure channelStructure1 = ChannelStructure.makeChannelStructure(module1, false,
                DataType.INT8, "ABC");
        Set<? extends Node> values1 = channelStructure1.getChildren();
        Collection<Channel> channels1 = channelStructure1.getChannelsAsMap().values();
        assertEquals(8, values1.size());
        assertEquals(8, channels1.size());
        Channel[] structChannels1 = channels1.toArray(new Channel[0]);

        Channel structChannel1 = structChannels1[0];
        assertEquals("Struct Channel: " + structChannel1, DataType.BIT, structChannel1
                .getChannelType());
        assertEquals(1, structChannel1.getChSize());
        assertEquals(0, structChannel1.getChannelNumber());
        assertEquals(0, structChannel1.getFullChannelNumber());
        assertEquals("@Subnet:815/0 'T=INT8,B=0'", structChannel1.getEpicsAddressStringNH());

        structChannel1 = structChannels1[1];
        assertEquals("Struct Channel: " + structChannel1, DataType.BIT, structChannel1
                .getChannelType());
        assertEquals(1, structChannel1.getChSize());
        assertEquals(0, structChannel1.getChannelNumber());
        assertEquals(0, structChannel1.getFullChannelNumber());
        assertEquals("@Subnet:815/0 'T=INT8,B=1'", structChannel1.getEpicsAddressStringNH());

        structChannel1 = structChannels1[2];
        assertEquals("Struct Channel: " + structChannel1, DataType.BIT, structChannel1
                .getChannelType());
        assertEquals(1, structChannel1.getChSize());
        assertEquals(0, structChannel1.getChannelNumber());
        assertEquals(0, structChannel1.getFullChannelNumber());
        assertEquals("@Subnet:815/0 'T=INT8,B=2'", structChannel1.getEpicsAddressStringNH());

        structChannel1 = structChannels1[3];
        assertEquals("Struct Channel: " + structChannel1, DataType.BIT, structChannel1
                .getChannelType());
        assertEquals(1, structChannel1.getChSize());
        assertEquals(0, structChannel1.getChannelNumber());
        assertEquals(0, structChannel1.getFullChannelNumber());
        assertEquals("@Subnet:815/0 'T=INT8,B=3'", structChannel1.getEpicsAddressStringNH());

        structChannel1 = structChannels1[4];
        assertEquals("Struct Channel: " + structChannel1, DataType.BIT, structChannel1
                .getChannelType());
        assertEquals(1, structChannel1.getChSize());
        assertEquals(0, structChannel1.getChannelNumber());
        assertEquals(0, structChannel1.getFullChannelNumber());
        assertEquals("@Subnet:815/0 'T=INT8,B=4'", structChannel1.getEpicsAddressStringNH());

        structChannel1 = structChannels1[5];
        assertEquals("Struct Channel: " + structChannel1, DataType.BIT, structChannel1
                .getChannelType());
        assertEquals(1, structChannel1.getChSize());
        assertEquals(0, structChannel1.getChannelNumber());
        assertEquals(0, structChannel1.getFullChannelNumber());
        assertEquals("@Subnet:815/0 'T=INT8,B=5'", structChannel1.getEpicsAddressStringNH());

        structChannel1 = structChannels1[6];
        assertEquals("Struct Channel: " + structChannel1, DataType.BIT, structChannel1
                .getChannelType());
        assertEquals(1, structChannel1.getChSize());
        assertEquals(0, structChannel1.getChannelNumber());
        assertEquals(0, structChannel1.getFullChannelNumber());
        assertEquals("@Subnet:815/0 'T=INT8,B=6'", structChannel1.getEpicsAddressStringNH());

        structChannel1 = structChannels1[7];
        assertEquals("Struct Channel: " + structChannel1, DataType.BIT, structChannel1
                .getChannelType());
        assertEquals(1, structChannel1.getChSize());
        assertEquals(0, structChannel1.getChannelNumber());
        assertEquals(0, structChannel1.getFullChannelNumber());
        assertEquals("@Subnet:815/0 'T=INT8,B=7'", structChannel1.getEpicsAddressStringNH());

        assertEquals("@Subnet:815", module1.getEpicsAddressString());
        assertEquals(0, module1.getInputOffsetNH());
        assertEquals(0, module1.getInputSize());
        assertEquals(0, module1.getOutputOffsetNH());
        assertEquals(1, module1.getOutputSize());

        structChannel1.localSave();
        structChannel1.localUpdate();

        testModule("@Subnet:815", (short) 21, 0, 0, 0, 1, module1);

        ChannelStructure channelStructure2 = ChannelStructure.makeChannelStructure(module1, false,
                DataType.INT8, "def");

        Collection<? extends Node> values2 = channelStructure2.getChildren();
        Collection<Channel> channels2 = channelStructure2.getChannelsAsMap().values();
        assertEquals(8, values2.size());
        assertEquals(8, channels2.size());
        Channel[] structChannels2 = channels2.toArray(new Channel[0]);
        Channel structChannel2 = structChannels2[0];

        assertEquals("Struct Channel: " + structChannel2, DataType.BIT, structChannel2
                .getChannelType());
        assertEquals(1, structChannel2.getChSize());
        assertEquals(1, structChannel2.getChannelNumber());
        assertEquals(1, structChannel2.getFullChannelNumber());
        assertEquals("@Subnet:815/1 'T=INT8,B=0'", structChannel2.getEpicsAddressStringNH());

        structChannel2 = structChannels2[1];
        assertEquals("Struct Channel: " + structChannel2, DataType.BIT, structChannel2
                .getChannelType());
        assertEquals(1, structChannel2.getChSize());
        assertEquals(1, structChannel2.getChannelNumber());
        assertEquals(1, structChannel2.getFullChannelNumber());
        assertEquals("@Subnet:815/1 'T=INT8,B=1'", structChannel2.getEpicsAddressStringNH());

        structChannel2 = structChannels2[2];
        assertEquals("Struct Channel: " + structChannel2, DataType.BIT, structChannel2
                .getChannelType());
        assertEquals(1, structChannel2.getChSize());
        assertEquals(1, structChannel2.getChannelNumber());
        assertEquals(1, structChannel2.getFullChannelNumber());
        assertEquals("@Subnet:815/1 'T=INT8,B=2'", structChannel2.getEpicsAddressStringNH());

        structChannel2 = structChannels2[3];
        assertEquals("Struct Channel: " + structChannel2, DataType.BIT, structChannel2
                .getChannelType());
        assertEquals(1, structChannel2.getChSize());
        assertEquals(1, structChannel2.getChannelNumber());
        assertEquals(1, structChannel2.getFullChannelNumber());
        assertEquals("@Subnet:815/1 'T=INT8,B=3'", structChannel2.getEpicsAddressStringNH());

        structChannel2 = structChannels2[4];
        assertEquals("Struct Channel: " + structChannel2, DataType.BIT, structChannel2
                .getChannelType());
        assertEquals(1, structChannel2.getChSize());
        assertEquals(1, structChannel2.getChannelNumber());
        assertEquals(1, structChannel2.getFullChannelNumber());
        assertEquals("@Subnet:815/1 'T=INT8,B=4'", structChannel2.getEpicsAddressStringNH());

        structChannel2 = structChannels2[5];
        assertEquals("Struct Channel: " + structChannel2, DataType.BIT, structChannel2
                .getChannelType());
        assertEquals(1, structChannel2.getChSize());
        assertEquals(1, structChannel2.getChannelNumber());
        assertEquals(1, structChannel2.getFullChannelNumber());
        assertEquals("@Subnet:815/1 'T=INT8,B=5'", structChannel2.getEpicsAddressStringNH());

        structChannel2 = structChannels2[6];
        assertEquals("Struct Channel: " + structChannel2, DataType.BIT, structChannel2
                .getChannelType());
        assertEquals(1, structChannel2.getChSize());
        assertEquals(1, structChannel2.getChannelNumber());
        assertEquals(1, structChannel2.getFullChannelNumber());
        assertEquals("@Subnet:815/1 'T=INT8,B=6'", structChannel2.getEpicsAddressStringNH());

        structChannel2 = structChannels2[7];
        assertEquals("Struct Channel: " + structChannel2, DataType.BIT, structChannel2
                .getChannelType());
        assertEquals(1, structChannel2.getChSize());
        assertEquals(1, structChannel2.getChannelNumber());
        assertEquals(1, structChannel2.getFullChannelNumber());
        assertEquals("@Subnet:815/1 'T=INT8,B=7'", structChannel2.getEpicsAddressStringNH());

        assertEquals("@Subnet:815", module1.getEpicsAddressString());
        assertEquals(0, module1.getInputOffsetNH());
        assertEquals(0, module1.getInputSize());
        assertEquals(0, module1.getOutputOffsetNH());
        assertEquals(2, module1.getOutputSize());
        structChannel2.localSave();
        structChannel2.localUpdate();
        assertEquals("@Subnet:815", module1.getEpicsAddressString());
        assertEquals(0, module1.getInputOffsetNH());
        assertEquals(0, module1.getInputSize());
        assertEquals(0, module1.getOutputOffsetNH());
        assertEquals(2, module1.getOutputSize());

        // create Module 2
        Module module2 = new Module(_slave, "Module 2");
        module2.moveSortIndex((short) 22);
        module2.localSave();

        assertEquals("@Subnet:815", module2.getEpicsAddressString());
        assertEquals(0, module2.getInputOffsetNH());
        assertEquals(0, module2.getInputSize());
        assertEquals(2, module2.getOutputOffsetNH());
        assertEquals(0, module2.getOutputSize());

        ChannelStructure channelStructure3 = ChannelStructure.makeChannelStructure(module2, false,
                DataType.INT16, "GhI");

        Collection<? extends Node> values3 = channelStructure3.getChildren();
        Collection<Channel> channels3 = channelStructure3.getChannelsAsMap().values();
        assertEquals(16, values3.size());
        assertEquals(16, channels3.size());
        Channel[] structChannels3 = channels3.toArray(new Channel[0]);

        Channel structChannel3 = structChannels3[0];
        assertEquals("Struct Channel: " + structChannel3, DataType.BIT, structChannel3
                .getChannelType());
        assertEquals((short) 0, structChannel3.getSortIndex());
        assertEquals(1, structChannel3.getChSize());
        assertEquals(0, structChannel3.getChannelNumber());
        assertEquals(2, structChannel3.getFullChannelNumber());
        assertEquals("@Subnet:815/2 'T=INT16,B=0'", structChannel3.getEpicsAddressStringNH());

        structChannel3 = structChannels3[1];
        assertEquals("Struct Channel: " + structChannel3, DataType.BIT, structChannel3
                .getChannelType());
        assertEquals((short) 1, structChannel3.getSortIndex());
        assertEquals(1, structChannel3.getChSize());
        assertEquals(0, structChannel3.getChannelNumber());
        assertEquals(2, structChannel3.getFullChannelNumber());
        assertEquals("@Subnet:815/2 'T=INT16,B=1'", structChannel3.getEpicsAddressStringNH());

        structChannel3 = structChannels3[2];
        assertEquals("Struct Channel: " + structChannel3, DataType.BIT, structChannel3
                .getChannelType());
        assertEquals((short) 2, structChannel3.getSortIndex());
        assertEquals(1, structChannel3.getChSize());
        assertEquals(0, structChannel3.getChannelNumber());
        assertEquals(2, structChannel3.getFullChannelNumber());
        assertEquals("@Subnet:815/2 'T=INT16,B=2'", structChannel3.getEpicsAddressStringNH());

        structChannel3 = structChannels3[3];
        assertEquals("Struct Channel: " + structChannel3, DataType.BIT, structChannel3
                .getChannelType());
        assertEquals((short) 3, structChannel3.getSortIndex());
        assertEquals(1, structChannel3.getChSize());
        assertEquals(0, structChannel3.getChannelNumber());
        assertEquals(2, structChannel3.getFullChannelNumber());
        assertEquals("@Subnet:815/2 'T=INT16,B=3'", structChannel3.getEpicsAddressStringNH());

        structChannel3 = structChannels3[4];
        assertEquals("Struct Channel: " + structChannel3, DataType.BIT, structChannel3
                .getChannelType());
        assertEquals((short) 4, structChannel3.getSortIndex());
        assertEquals(1, structChannel3.getChSize());
        assertEquals(0, structChannel3.getChannelNumber());
        assertEquals(2, structChannel3.getFullChannelNumber());
        assertEquals("@Subnet:815/2 'T=INT16,B=4'", structChannel3.getEpicsAddressStringNH());

        structChannel3 = structChannels3[5];
        assertEquals("Struct Channel: " + structChannel3, DataType.BIT, structChannel3
                .getChannelType());
        assertEquals((short) 5, structChannel3.getSortIndex());
        assertEquals(1, structChannel3.getChSize());
        assertEquals(0, structChannel3.getChannelNumber());
        assertEquals(2, structChannel3.getFullChannelNumber());
        assertEquals("@Subnet:815/2 'T=INT16,B=5'", structChannel3.getEpicsAddressStringNH());

        structChannel3 = structChannels3[6];
        assertEquals("Struct Channel: " + structChannel3, DataType.BIT, structChannel3
                .getChannelType());
        assertEquals((short) 6, structChannel3.getSortIndex());
        assertEquals(1, structChannel3.getChSize());
        assertEquals(0, structChannel3.getChannelNumber());
        assertEquals(2, structChannel3.getFullChannelNumber());
        assertEquals("@Subnet:815/2 'T=INT16,B=6'", structChannel3.getEpicsAddressStringNH());

        structChannel3 = structChannels3[7];
        assertEquals("Struct Channel: " + structChannel3, DataType.BIT, structChannel3
                .getChannelType());
        assertEquals((short) 7, structChannel3.getSortIndex());
        assertEquals(1, structChannel3.getChSize());
        assertEquals(0, structChannel3.getChannelNumber());
        assertEquals(2, structChannel3.getFullChannelNumber());
        assertEquals("@Subnet:815/2 'T=INT16,B=7'", structChannel3.getEpicsAddressStringNH());

        structChannel3 = structChannels3[8];
        assertEquals("Struct Channel: " + structChannel3, DataType.BIT, structChannel3
                .getChannelType());
        assertEquals((short) 8, structChannel3.getSortIndex());
        assertEquals(1, structChannel3.getChSize());
        assertEquals(0, structChannel3.getChannelNumber());
        assertEquals(2, structChannel3.getFullChannelNumber());
        assertEquals("@Subnet:815/2 'T=INT16,B=8'", structChannel3.getEpicsAddressStringNH());

        structChannel3 = structChannels3[9];
        assertEquals("Struct Channel: " + structChannel3, DataType.BIT, structChannel3
                .getChannelType());
        assertEquals((short) 9, structChannel3.getSortIndex());
        assertEquals(1, structChannel3.getChSize());
        assertEquals(0, structChannel3.getChannelNumber());
        assertEquals(2, structChannel3.getFullChannelNumber());
        assertEquals("@Subnet:815/2 'T=INT16,B=9'", structChannel3.getEpicsAddressStringNH());

        structChannel3 = structChannels3[10];
        assertEquals("Struct Channel: " + structChannel3, DataType.BIT, structChannel3
                .getChannelType());
        assertEquals((short) 10, structChannel3.getSortIndex());
        assertEquals(1, structChannel3.getChSize());
        assertEquals(0, structChannel3.getChannelNumber());
        assertEquals(2, structChannel3.getFullChannelNumber());
        assertEquals("@Subnet:815/2 'T=INT16,B=10'", structChannel3.getEpicsAddressStringNH());

        structChannel3 = structChannels3[11];
        assertEquals("Struct Channel: " + structChannel3, DataType.BIT, structChannel3
                .getChannelType());
        assertEquals((short) 11, structChannel3.getSortIndex());
        assertEquals(1, structChannel3.getChSize());
        assertEquals(0, structChannel3.getChannelNumber());
        assertEquals(2, structChannel3.getFullChannelNumber());
        assertEquals("@Subnet:815/2 'T=INT16,B=11'", structChannel3.getEpicsAddressStringNH());

        structChannel3 = structChannels3[12];
        assertEquals("Struct Channel: " + structChannel3, DataType.BIT, structChannel3
                .getChannelType());
        assertEquals((short) 12, structChannel3.getSortIndex());
        assertEquals(1, structChannel3.getChSize());
        assertEquals(0, structChannel3.getChannelNumber());
        assertEquals(2, structChannel3.getFullChannelNumber());
        assertEquals("@Subnet:815/2 'T=INT16,B=12'", structChannel3.getEpicsAddressStringNH());

        structChannel3 = structChannels3[13];
        assertEquals("Struct Channel: " + structChannel3, DataType.BIT, structChannel3
                .getChannelType());
        assertEquals((short) 13, structChannel3.getSortIndex());
        assertEquals(1, structChannel3.getChSize());
        assertEquals(0, structChannel3.getChannelNumber());
        assertEquals(2, structChannel3.getFullChannelNumber());
        assertEquals("@Subnet:815/2 'T=INT16,B=13'", structChannel3.getEpicsAddressStringNH());

        structChannel3 = structChannels3[14];
        assertEquals("Struct Channel: " + structChannel3, DataType.BIT, structChannel3
                .getChannelType());
        assertEquals((short) 14, structChannel3.getSortIndex());
        assertEquals(1, structChannel3.getChSize());
        assertEquals(0, structChannel3.getChannelNumber());
        assertEquals(2, structChannel3.getFullChannelNumber());
        assertEquals("@Subnet:815/2 'T=INT16,B=14'", structChannel3.getEpicsAddressStringNH());

        structChannel3 = structChannels3[15];
        assertEquals("Struct Channel: " + structChannel3, DataType.BIT, structChannel3
                .getChannelType());
        assertEquals((short) 15, structChannel3.getSortIndex());
        assertEquals(1, structChannel3.getChSize());
        assertEquals(0, structChannel3.getChannelNumber());
        assertEquals(2, structChannel3.getFullChannelNumber());
        assertEquals("@Subnet:815/2 'T=INT16,B=15'", structChannel3.getEpicsAddressStringNH());

        assertEquals("@Subnet:815", module2.getEpicsAddressString());
        assertEquals(0, module2.getInputOffsetNH());
        assertEquals(0, module2.getInputSize());
        assertEquals(2, module2.getOutputOffsetNH());
        assertEquals(2, module2.getOutputSize());

        structChannel3.localSave();
        structChannel3.localUpdate();

        assertEquals("@Subnet:815", module2.getEpicsAddressString());
        assertEquals(0, module2.getInputOffsetNH());
        assertEquals(0, module2.getInputSize());
        assertEquals(2, module2.getOutputOffsetNH());
        assertEquals(2, module2.getOutputSize());

        ChannelStructure channelStructure4 = ChannelStructure.makeChannelStructure(module2, false,
                DataType.UINT16, "jKl");

        Collection<? extends Node> values4 = channelStructure4.getChildren();
        Collection<Channel> channels4 = channelStructure4.getChannelsAsMap().values();
        assertEquals(16, values4.size());
        assertEquals(16, channels4.size());
        Channel[] structChannels4 = channels4.toArray(new Channel[0]);
        Channel structChannel4 = structChannels4[0];

        assertEquals("Struct Channel: " + structChannel4, DataType.BIT, structChannel4
                .getChannelType());
        assertEquals(1, structChannel4.getChSize());
        assertEquals(2, structChannel4.getChannelNumber());
        assertEquals(4, structChannel4.getFullChannelNumber());
        assertEquals("@Subnet:815/4 'T=UNSIGN16,B=0'", structChannel4.getEpicsAddressStringNH());

        structChannel4 = structChannels4[1];
        assertEquals("Struct Channel: " + structChannel4, DataType.BIT, structChannel4
                .getChannelType());
        assertEquals(1, structChannel4.getChSize());
        assertEquals(2, structChannel4.getChannelNumber());
        assertEquals(4, structChannel4.getFullChannelNumber());
        assertEquals("@Subnet:815/4 'T=UNSIGN16,B=1'", structChannel4.getEpicsAddressStringNH());

        structChannel4 = structChannels4[2];
        assertEquals("Struct Channel: " + structChannel4, DataType.BIT, structChannel4
                .getChannelType());
        assertEquals(1, structChannel4.getChSize());
        assertEquals(2, structChannel4.getChannelNumber());
        assertEquals(4, structChannel4.getFullChannelNumber());
        assertEquals("@Subnet:815/4 'T=UNSIGN16,B=2'", structChannel4.getEpicsAddressStringNH());

        structChannel4 = structChannels4[3];
        assertEquals("Struct Channel: " + structChannel4, DataType.BIT, structChannel4
                .getChannelType());
        assertEquals(1, structChannel4.getChSize());
        assertEquals(2, structChannel4.getChannelNumber());
        assertEquals(4, structChannel4.getFullChannelNumber());
        assertEquals("@Subnet:815/4 'T=UNSIGN16,B=3'", structChannel4.getEpicsAddressStringNH());

        structChannel4 = structChannels4[4];
        assertEquals("Struct Channel: " + structChannel4, DataType.BIT, structChannel4
                .getChannelType());
        assertEquals(1, structChannel4.getChSize());
        assertEquals(2, structChannel4.getChannelNumber());
        assertEquals(4, structChannel4.getFullChannelNumber());
        assertEquals("@Subnet:815/4 'T=UNSIGN16,B=4'", structChannel4.getEpicsAddressStringNH());

        structChannel4 = structChannels4[5];
        assertEquals("Struct Channel: " + structChannel4, DataType.BIT, structChannel4
                .getChannelType());
        assertEquals(1, structChannel4.getChSize());
        assertEquals(2, structChannel4.getChannelNumber());
        assertEquals(4, structChannel4.getFullChannelNumber());
        assertEquals("@Subnet:815/4 'T=UNSIGN16,B=5'", structChannel4.getEpicsAddressStringNH());

        structChannel4 = structChannels4[6];
        assertEquals("Struct Channel: " + structChannel2, DataType.BIT, structChannel2
                .getChannelType());
        assertEquals(1, structChannel4.getChSize());
        assertEquals(2, structChannel4.getChannelNumber());
        assertEquals(4, structChannel4.getFullChannelNumber());
        assertEquals("@Subnet:815/4 'T=UNSIGN16,B=6'", structChannel4.getEpicsAddressStringNH());

        structChannel4 = structChannels4[7];
        assertEquals("Struct Channel: " + structChannel4, DataType.BIT, structChannel4
                .getChannelType());
        assertEquals(1, structChannel4.getChSize());
        assertEquals(2, structChannel4.getChannelNumber());
        assertEquals(4, structChannel4.getFullChannelNumber());
        assertEquals("@Subnet:815/4 'T=UNSIGN16,B=7'", structChannel4.getEpicsAddressStringNH());

        structChannel4 = structChannels4[15];
        assertEquals("Struct Channel: " + structChannel4, DataType.BIT, structChannel4
                .getChannelType());
        assertEquals(1, structChannel4.getChSize());
        assertEquals(2, structChannel4.getChannelNumber());
        assertEquals(4, structChannel4.getFullChannelNumber());
        assertEquals("@Subnet:815/4 'T=UNSIGN16,B=15'", structChannel4.getEpicsAddressStringNH());

        assertEquals("@Subnet:815", module2.getEpicsAddressString());
        assertEquals(0, module2.getInputOffsetNH());
        assertEquals(0, module2.getInputSize());
        assertEquals(2, module2.getOutputOffsetNH());
        assertEquals(4, module2.getOutputSize());

        structChannel4.localSave();
        structChannel4.localUpdate();

        assertEquals("@Subnet:815", module2.getEpicsAddressString());
        assertEquals(0, module2.getInputOffsetNH());
        assertEquals(0, module2.getInputSize());
        assertEquals(2, module2.getOutputOffsetNH());
        assertEquals(4, module2.getOutputSize());
    }

    /**
     * See the also the TestAllChannelCombinations.html file.
     * 
     * Need Modules (M) and every - Channel as Input and Output (PCI/PCO) - StructChannel as Input
     * and Output (SCI/SCO) combination.
     * 
     * Possible combinations: M->PCI ok | PCI->SCI ok M->PCO ok | SCI->PCI ok M->SCI ok | PCO->SCO
     * ok M->SCO ok | SCO->PCO ok
     * 
     * The test M1-->PCI11(INT16)-->SCO11(INT8 )-->PCO11(UNSIGN16)-->SCI11(UNSIGN8 )-->SCO12()-->PCO12
     * M2-->SCI2(INT8 )-->PCO2(INT16)-->PCI2(UNSIGN8 )-->SCI2(UNSIGN16)
     * 
     */
    @Test
    public void TestAllChannelCombinations() throws PersistenceException {
        _slave.moveSortIndex((short) 123);
        _slave.localSave();
        assertEquals("@Subnet:123", _slave.getEpicsAdressString());

        // create Module 1
        // M1-->
        Module module1 = new Module(_slave, "Module 1");
        module1.moveSortIndex((short) 31);
        module1.localSave();

        testModule("@Subnet:123", (short) 31, 0, 0, 0, 0, module1);

        // Make Channe Input 1
        // M1-->PCI1(INT16)-->

        ChannelStructure structure11 = ChannelStructure.makeSimpleChannel(module1, INPUT);
        Channel pci11 = structure11.getFirstChannel();
        pci11.setDigital(false);
        pci11.setName(" Channel Input 1.1");
        pci11.setChannelTypeNonHibernate(DataType.INT16);
        pci11.localSave();

        // - Test Module Changes
        testModule("@Subnet:123", (short) 31, 0, 2, 0, 0, module1);

        // - Test simple Channel.
        testChannelFromStructure("@Subnet:123/0 'T=INT16'", (short) 0, DataType.INT16, 16, 0, 0,
                structure11);

        // Make Structure Channel Output 1.1
        // M1-->PCI1-->SCO1(INT8)-->
        ChannelStructure sco11 = ChannelStructure.makeChannelStructure(module1, OUTPUT,
                DataType.INT8, "SCO1.1_");

        // - Test Module Changes
        testModule("@Subnet:123", (short) 31, 0, 2, 0, 1, module1);

        // - Test Structure Channels.
        Collection<Channel> valuesSCO1 = sco11.getChannelsAsMap().values();
        Set<Channel> channelsSCO1 = sco11.getChannels();
        assertEquals(8, valuesSCO1.size());
        assertEquals(8, channelsSCO1.size());

        testChannelFromStructure("@Subnet:123/0 'T=INT8,B=0'", (short) 0, DataType.BIT, 1, 0, 0,
                sco11);

        testChannelFromStructure("@Subnet:123/0 'T=INT8,B=3'", (short) 3, DataType.BIT, 1, 0, 0,
                sco11);

        testChannelFromStructure("@Subnet:123/0 'T=INT8,B=7'", (short) 7, DataType.BIT, 1, 0, 0,
                sco11);

        // Make Channel Output 1.1
        // M1-->PCI1-->SCO1-->PCO11-->
        ChannelStructure structurePCO11 = ChannelStructure.makeSimpleChannel(module1,
                "Channel Output 1.1", OUTPUT, false);
        Channel pco11 = structurePCO11.getFirstChannel();
        pco11.setChannelTypeNonHibernate(DataType.UINT16);
        pco11.localSave();

        // - Test Module Changes
        testModule("@Subnet:123", (short) 31, 0, 2, 0, 3, module1);

        // - Test Simple Channel.
        testChannelFromStructure("@Subnet:123/1 'T=UNSIGN16'", (short) 2, DataType.UINT16, 16, 1, 1,
                structurePCO11);

        // Make Struct Channel Input 1.1
        // M1-->PCI1-->SCO1-->PCO1-->SCI1
        ChannelStructure sci1 = ChannelStructure.makeChannelStructure(module1, INPUT,
                DataType.UINT8, "SCI1.1_");

        // - Test Module Changes
        testModule("@Subnet:123", (short) 31, 0, 3, 0, 3, module1);

        // - Test Channel.
        Collection<Channel> valuesSCI1 = sci1.getChannelsAsMap().values();
        Set<Channel> channelssci1 = sci1.getChannels();
        assertEquals(8, valuesSCI1.size());
        assertEquals(8, channelssci1.size());

        testChannelFromStructure("@Subnet:123/2 'T=UNSIGN8,B=0'", (short) 0, DataType.BIT, 1, 2, 2,
                sci1);
        testChannelFromStructure("@Subnet:123/2 'T=UNSIGN8,B=3'", (short) 3, DataType.BIT, 1, 2, 2,
                sci1);
        testChannelFromStructure("@Subnet:123/2 'T=UNSIGN8,B=7'", (short) 7, DataType.BIT, 1, 2, 2,
                sci1);

        // Make Struct Channel Input 1.2
        // M1-->PCI11-->SCO11-->PCO11-->SCI11-->SCO12
        ChannelStructure sco12 = ChannelStructure.makeChannelStructure(module1, OUTPUT,
                DataType.UINT16, "SCO1.2_");

        // - Test Module Changes
        testModule("@Subnet:123", (short) 31, 0, 3, 0, 5, module1);

        // - Test Channel.
        assertEquals(16, sco12.getChildren().size());
        assertEquals(16, sco12.getChannelsAsMap().values().size());
        testChannelFromStructure("@Subnet:123/3 'T=UNSIGN16,B=0'", (short) 0, DataType.BIT, 1, 3, 3,
                sco12);
        testChannelFromStructure("@Subnet:123/3 'T=UNSIGN16,B=7'", (short) 7, DataType.BIT, 1, 3, 3,
                sco12);
        testChannelFromStructure("@Subnet:123/3 'T=UNSIGN16,B=15'", (short) 15, DataType.BIT, 1, 3,
                3, sco12);

        // Make Channel Input 1.2
        // M1-->PCI11-->SCO11-->PCO11-->SCI11-->SCO12-->PCI12
        ChannelStructure structurePCI12 = ChannelStructure.makeSimpleChannel(module1,
                "Channel Input 1.2", INPUT, false);
        Channel pci12 = structurePCI12.getFirstChannel();
        pci12.setChannelTypeNonHibernate(DataType.INT8);
        pci12.localSave();

        // - Test Module Changes
        testModule("@Subnet:123", (short) 31, 0, 4, 0, 5, module1);

        // - Test Channel.
        testChannelFromStructure("@Subnet:123/3 'T=INT8'", (short) 5, DataType.INT8, 8, 3, 3,
                structurePCI12);

        // create Module 2
        // M2-->
        Module module2 = new Module(_slave, "Module 2");
        module2.moveSortIndex((short) 32);
        module2.localSave();

        // - Test Module create
        testModule("@Subnet:123", (short) 32, 4, 0, 5, 0, module2);

        // Make structure Channel Input 2.1
        // M2-->SCI21-->
        ChannelStructure sci21 = ChannelStructure.makeChannelStructure(module2, INPUT,
                DataType.INT8, "SCI2.2_");

        // - Test Module Changes
        testModule("@Subnet:123", (short) 32, 4, 1, 5, 0, module2);

        // - Test structure Channels.
        Collection<Channel> valuesSCI21 = sci21.getChannelsAsMap().values();
        Set<Channel> channelsSCI21 = sci21.getChannels();
        assertEquals(8, valuesSCI21.size());
        assertEquals(8, channelsSCI21.size());

        testChannelFromStructure("@Subnet:123/4 'T=INT8,B=0'", (short) 0, DataType.BIT, 1, 0, 4,
                sci21);
        testChannelFromStructure("@Subnet:123/4 'T=INT8,B=3'", (short) 3, DataType.BIT, 1, 0, 4,
                sci21);
        testChannelFromStructure("@Subnet:123/4 'T=INT8,B=7'", (short) 7, DataType.BIT, 1, 0, 4,
                sci21);

        // Make Struct Channel Output 2.2
        // M2-->SCI21-->PCO21-->
        ChannelStructure structurePCO21 = ChannelStructure.makeSimpleChannel(module2,
                "Channel Output 2.1", OUTPUT, false);
        Channel pco21 = structurePCO21.getFirstChannel();
        pco21.setChannelTypeNonHibernate(DataType.INT16);
        pco21.localSave();

        // - Test Module Changes
        testModule("@Subnet:123", (short) 32, 4, 1, 5, 2, module2);

        // - Test simple Channel.
        testChannelFromStructure("@Subnet:123/5 'T=INT16'", (short) 1, DataType.INT16, 16, 0, 5,
                structurePCO21);

        // Make structure Channel Input 2
        // M2-->SCI21-->PCO21-->PCI21-->
        ChannelStructure structurePCI21 = ChannelStructure.makeSimpleChannel(module2,
                "Channel Input 2.1", INPUT, false);
        Channel pci21 = structurePCI21.getFirstChannel();
        pci21.setChannelTypeNonHibernate(DataType.UINT8);
        pci21.localSave();

        // - Test Module Changes
        testModule("@Subnet:123", (short) 32, 4, 2, 5, 2, module2);

        // - Test Channel.
        testChannelFromStructure("@Subnet:123/5 'T=UNSIGN8,L=0,H=32768'", (short) 2, DataType.UINT8,
                8, 1, 5, structurePCI21);

        // Make Struct Channel Input 2
        // M2-->SCI21-->PCO21-->PCI21-->SCO21
        ChannelStructure sco21 = ChannelStructure.makeChannelStructure(module2, OUTPUT,
                DataType.UINT16, "SCO2.1_");

        // - Test Module Changes
        testModule("@Subnet:123", (short) 32, 4, 2, 5, 4, module2);

        // - Test structure Channels.
        Collection<Channel> valuesSCO21 = sco21.getChannelsAsMap().values();
        Set<Channel> channelsSCO21 = sco21.getChannels();
        assertEquals(16, valuesSCO21.size());
        assertEquals(16, channelsSCO21.size());

        testChannelFromStructure("@Subnet:123/7 'T=UNSIGN16,B=0'", (short) 0, DataType.BIT, 1, 2, 7,
                sco21);
        testChannelFromStructure("@Subnet:123/7 'T=UNSIGN16,B=3'", (short) 3, DataType.BIT, 1, 2, 7,
                sco21);
        testChannelFromStructure("@Subnet:123/7 'T=UNSIGN16,B=7'", (short) 7, DataType.BIT, 1, 2, 7,
                sco21);

        // Make simple Channel Output 2.2
        // M2-->SCI21-->PCO21-->PCI21-->SCO21-->PCO22
        ChannelStructure structurePCO22 = ChannelStructure.makeSimpleChannel(module2,
                "Channel Output 2.2", OUTPUT, false);
        Channel pco22 = structurePCO22.getFirstChannel();
        pco22.setChannelTypeNonHibernate(DataType.INT8);
        pco22.localSave();

        // - Test Module Changes
        testModule("@Subnet:123", (short) 32, 4, 2, 5, 5, module2);

        // - Test Channel.
        testChannelFromStructure("@Subnet:123/9 'T=INT8'", (short) 4, DataType.INT8, 8, 4, 9,
                structurePCO22);

        // Make Struct Channel Input 2.2
        // M2-->SCI21-->PCO21-->PCI21-->SCO21-->PCO22-->SCI22
        ChannelStructure sci22 = ChannelStructure.makeChannelStructure(module2, INPUT,
                DataType.UINT16, "SCI2.2_");

        // - Test Module Changes
        testModule("@Subnet:123", (short) 32, 4, 4, 5, 5, module2);

        // - Test structure Channels.
        Collection<Channel> valuesSCI22 = sci22.getChannelsAsMap().values();
        Set<Channel> channelsSCI22 = sci22.getChannels();
        assertEquals(16, valuesSCI22.size());
        assertEquals(16, channelsSCI22.size());

        testChannelFromStructure("@Subnet:123/6 'T=UNSIGN16,B=0'", (short) 0, DataType.BIT, 1, 2, 6,
                sci22);
        testChannelFromStructure("@Subnet:123/6 'T=UNSIGN16,B=7'", (short) 7, DataType.BIT, 1, 2, 6,
                sci22);
        testChannelFromStructure("@Subnet:123/6 'T=UNSIGN16,B=15'", (short) 15, DataType.BIT, 1, 2,
                6, sci22);

    }

    private void testModule(String addressString, short sortIndex, int inputOffset, int inputSize,
            int outputOffset, int outputSize, Module module) {
        assertEquals(addressString, module.getEpicsAddressString());
        assertEquals(sortIndex, module.getSortIndex());
        assertEquals(inputOffset, module.getInputOffsetNH());
        assertEquals(inputSize, module.getInputSize());
        assertEquals(outputOffset, module.getOutputOffsetNH());
        assertEquals(outputSize, module.getOutputSize());
    }

    private void testSimpleChannel(String addressString, short sortIndex, Channel channel,
            ChannelStructure structure) {
        assertEquals(sortIndex, structure.getSortIndex());
        assertEquals(sortIndex, channel.getSortIndex());
        assertEquals(addressString, channel.getEpicsAddressStringNH());
    }

    private void testChannelFromStructure(String addressString, short sortIndex, DataType dataType,
            int chSize, int channelNumber, int fullChannelNumber, ChannelStructure channelStructure) {
        Channel channel = (Channel) channelStructure.getChildrenAsMap().get(sortIndex);
        assertNotNull("Wrong sortIndex (" + sortIndex + ")! Hint: "
                + channelStructure.getFirstChannel().getSortIndex(), channel);
        assertEquals("Struct Channel: " + channel, dataType, channel.getChannelType());
        assertEquals(sortIndex, channel.getSortIndex());
        assertEquals(chSize, channel.getChSize());
        assertEquals(channelNumber, channel.getChannelNumber());
        assertEquals(fullChannelNumber, channel.getFullChannelNumber());
        assertEquals(addressString, channel.getEpicsAddressStringNH());
    }

    @Before
    public void setUp() throws PersistenceException {
        Repository.injectIRepository(new DummyRepository());
        _subnet = new ProfibusSubnet(new Ioc());
        _subnet.setName("Subnet");
        _subnet.localSave();
        _master = new Master(_subnet);
        _master.setName("Master");
        _master.localSave();
        _slave = new Slave(_master);
        _slave.setName("Slave");
        _slave.localSave();
        _module = new Module(_slave, "Module");
        _module.localSave();

    }

    @After
    public void setDown() {
        // PersistentAndUpdateHelper.removeAllUnsavedNodes();
        _module = null;
        _slave = null;
        _master = null;
        _subnet = null;
        Repository.injectIRepository(null);
    }

}
