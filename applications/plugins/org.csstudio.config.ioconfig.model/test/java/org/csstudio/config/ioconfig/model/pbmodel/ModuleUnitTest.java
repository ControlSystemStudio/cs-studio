package org.csstudio.config.ioconfig.model.pbmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.csstudio.config.ioconfig.model.DummyRepository;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
/**
 *
 * Der Test Prüft keine Hibernate Annotations ab!
 * Diese müssen in einem intgration Test seperat getestet werden.
 *
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 09.01.2009
 */
public class ModuleUnitTest {
    private ProfibusSubnetDBO _profibusSubnet;
    private MasterDBO _master;
    private SlaveDBO _slave;


     @Test
    public void testConstructors() {
        ModuleDBO out = new ModuleDBO();
        assertNotNull(out);
        out = new ModuleDBO();
        out.setName("Name");
        assertNotNull(out);
        assertEquals(out.getName(), "Name");
    }


    @Test
    public void testConfigurationData() {
        final ModuleDBO out = new ModuleDBO();
        assertEquals(out.getConfigurationData(), "");

        out.setConfigurationData("");
        assertEquals(out.getConfigurationData(), "");

        out.setConfigurationData("0x00,0x31,0xAf");
        assertEquals(out.getConfigurationData(), "0x00,0x31,0xAF");

        try {
            out.setConfigurationData("^1234567890ß´qwertzuiopü+asdfghjklöä#yxcvbnm,.-QAY\\\"");
            fail("NumberFormatException not thowed!");
        } catch (final NumberFormatException e) {
            assertTrue(true);
        }
//        assertEquals(out.getConfigurationData(), "^1234567890ß´qwertzuiopü+asdfghjklöä#yxcvbnm,.-QAY\\\"");
    }

    @Ignore("???")
    @Test
    public void testInputOffset() throws PersistenceException {
        final ModuleDBO out = new ModuleDBO();
        assertEquals(out.getInputOffsetNH(), 0);

        out.setInputOffset(Integer.MIN_VALUE);
        assertEquals(out.getInputOffsetNH(),Integer.MIN_VALUE);

        out.setInputOffset(123);
        assertEquals(out.getInputOffsetNH(),123);

        out.setInputOffset(Integer.MAX_VALUE);
        assertEquals(out.getInputOffsetNH(),Integer.MAX_VALUE);
    }


    @Test
    public void testModuleNumber() {
        final ModuleDBO out = new ModuleDBO();
        assertEquals(out.getModuleNumber(), -1);

        out.setModuleNumber(Integer.MIN_VALUE);
        assertEquals(out.getModuleNumber(),Integer.MIN_VALUE);

        out.setModuleNumber(123);
        assertEquals(out.getModuleNumber(),123);

        out.setModuleNumber(Integer.MAX_VALUE);
        assertEquals(out.getModuleNumber(),Integer.MAX_VALUE);
    }

    @Test
    public void testOutputOffset() throws PersistenceException {
        final ModuleDBO out = new ModuleDBO();
        assertEquals(out.getOutputOffsetNH(), 0);

        out.setOutputOffset(Integer.MIN_VALUE);
        assertEquals(out.getOutputOffset(),Integer.MIN_VALUE);

        out.setOutputOffset(123);
        assertEquals(out.getOutputOffset(),123);

        out.setOutputOffset(Integer.MAX_VALUE);
        assertEquals(out.getOutputOffset(),Integer.MAX_VALUE);
    }

    @Test
    public void testOutputSize() {
        final ModuleDBO out = new ModuleDBO();
        assertEquals(out.getOutputSize(), 0);

        out.setOutputSize(Integer.MIN_VALUE);
        assertEquals(out.getOutputSize(),Integer.MIN_VALUE);

        out.setOutputSize(123);
        assertEquals(out.getOutputSize(),123);

        out.setOutputSize(Integer.MAX_VALUE);
        assertEquals(out.getOutputSize(),Integer.MAX_VALUE);
    }

    @Test
    public void testChildren() throws PersistenceException {


        final ModuleDBO out = new ModuleDBO(_slave);
        assertTrue(out.getChildren().size()==0);

        final ChannelStructureDBO channelStructure = ChannelStructureDBO.makeChannelStructure(out, false, DataType.INT8, "StructChannelModel");
        channelStructure.setId(21);
        channelStructure.moveSortIndex((short) 121);
        final ChannelStructureDBO simpleChannelStructure = ChannelStructureDBO.makeSimpleChannel(out, "", false, false);
        simpleChannelStructure.setSimple(true);

        final ChannelDBO pureChannel = new ChannelDBO(simpleChannelStructure,false,false);
        pureChannel.setName("PureChannel");
        pureChannel.setId(12);
        pureChannel.moveSortIndex((short) 0);

//        Set<Node> channelStruts = new HashSet<Node>();
//        channelStruts.add(channelStructure);
//        out.setChildren(channelStruts);
//
//        Set<Node> pureChannels = new HashSet<Node>();
//        pureChannels.add(pureChannel);
//        channelStructure.setChildren(pureChannels);

        assertNotNull(out.getChildren());

        assertEquals(1, out.getPureChannels().size());
        assertEquals(2, out.getChildren().size());

//        assertTrue(out.getChildren().contains(channelStructure));
        assertTrue(out.getChildren().contains(simpleChannelStructure));
//        assertTrue(out.getPureChannels().contains(pureChannel));

        // test PureChannel
//        assertTrue(out.getPureChannels().contains(pureChannel));

        // test ChannelStruct
        //assertTrue(channelStruts.containsAll(out.getChannelStructs()));
        //assertTrue(out.getChannelStructs().containsAll(channelStruts));

    }

    @Test
    public void testSlaveParent() throws PersistenceException {

        // Build the tree, down to Salve (Slave 1)


        _slave.setName("Slave 1");
        _slave.setId(4711);
        _slave.localSave();

        // create the test Module
        final ModuleDBO out = new ModuleDBO(_slave);
        out.localSave();

        // test Parent
        assertNotNull(out.getSlave());
        assertEquals(out.getSlave(), _slave);

        assertNotNull(out.getParent());
        assertEquals(out.getParent(), out.getSlave());

        // Create a new Parent (Slave 2)
        _slave = new SlaveDBO(_master);
        _slave.setId(815);
        _slave.setName("Slave 2");
        _slave.localSave();

        // create a second test Module with this own parent.
//        out = new ModuleDBO(_slave);
//        out.localSave();
//
//        // test the second Module
//        assertNotNull(out.getSlave());
//        assertEquals(out.getSlave(), _slave);
//
//        assertNotNull(out.getParent());
//        assertEquals(out.getParent(), out.getSlave());
//
//
//     // create a third test Module with the same Parent (Slave 2)
//        out = new Module(_slave);
//        out.localSave();
//
//        assertNotNull(out.getSlave());
//        assertEquals(out.getSlave(), _slave);
//
//        assertNotNull(out.getParent());
//        assertEquals(out.getParent(), out.getSlave());

    }

    @Test
    public void testDocument() {
         // Wird momentan noch nicht unterstützt.
//        Module out = new Module();
//        assertTrue(out.getDocument().size()==0);
//
//        Document doc = new Document();
//        Collection<Document> docList = new ArrayList<Document>();
//        docList.add(doc);
//        out.setDocument(docList);
//
//        Collection<Document> documents = out.getDocument();
//        assertNotNull(documents);
//        assertTrue(documents.size()==1);
//        assertEquals(documents, docList);


    }

    @Test
    public void testSetExtModulePrmDataLen() {
        final ModuleDBO out = new ModuleDBO();
        assertNull(out.getExtModulePrmDataLen());

        out.setExtModulePrmDataLen("");
        assertEquals(out.getExtModulePrmDataLen(),"");

        out.setExtModulePrmDataLen("123");
        assertEquals(out.getExtModulePrmDataLen(),"123");

        out.setExtModulePrmDataLen("^1234567890ß´qwertzuiopü+asdfghjklöä#yxcvbnm,.-QAY\\\"");
        assertEquals(out.getExtModulePrmDataLen(), "^1234567890ß´qwertzuiopü+asdfghjklöä#yxcvbnm,.-QAY\\\"");
    }

    @Test
    public void testEpicsAdressString() throws PersistenceException {

        _profibusSubnet.setName("Name");
        _slave.moveSortIndex((short) 12);
        final ModuleDBO out = new ModuleDBO(_slave);
        assertNotNull(out.getEpicsAddressString());
        assertEquals(out.getEpicsAddressString(), "@Name:12");
    }

    @Before
    public void setUp() throws PersistenceException {
        Repository.injectIRepository(new DummyRepository());
         _profibusSubnet = new ProfibusSubnetDBO(new IocDBO());
        _profibusSubnet.localSave();
        _master = new MasterDBO(_profibusSubnet);
        _master.localSave();
        _slave = new SlaveDBO(_master);
        _slave.localSave();

    }

    @After
    public void tearDown() {
        _slave = null;
        _master = null;
        _profibusSubnet = null;
    }


}
